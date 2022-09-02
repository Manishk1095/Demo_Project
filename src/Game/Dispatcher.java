package Game;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Tracker.GameInfo;
import Tracker.PlayerInfo;
import Tracker.TrackerStub;

public class Dispatcher implements Runnable {
	
	public static Selector selector;
	public PlayerInfo currentPlayerInfo;
	public PlayerCategory playerCategory;
	public SocketChannel connectionToPrimary;
	public SocketChannel connectionToSecondary;
	public TrackerStub trackerStub;
	public HashMap<SocketChannel, Message> messagesToSend = new HashMap<>();
	public HashMap<String, SocketChannel> playerIdToSocket = new HashMap<>();
	public GameInfo gameInfo;

	private HashSet<String> reportedPlayer = new HashSet<>();
	private ServerSocketChannel server;
	private GameState gameState;
	private String trackerIpAddress;
	private int trackerPortNumber;
	private String playerId;
	
	public Dispatcher(String trackerIpAddress, int trackerPortNumber, String playerId) {
		super();
		this.trackerIpAddress = trackerIpAddress;
		this.trackerPortNumber = trackerPortNumber;
		this.playerId = playerId;
	}

	@Override
	public void run() {
		try {
			Registry registry = LocateRegistry.getRegistry(trackerIpAddress, trackerPortNumber);
			trackerStub = (TrackerStub) registry.lookup("Tracker");
			gameInfo = trackerStub.fetchGameInfoAndRegisterNewPlayer(playerId, InetAddress.getLocalHost().getHostAddress());
			System.out.println(gameInfo);
			ArrayList<PlayerInfo> playerList = gameInfo.getPlayerList();
			currentPlayerInfo = gameInfo.getPlayerList().get(playerList.size() - 1);
			processPlayerList(playerList);
		} catch (Exception e) {
			 System.err.println("Game exception: " + e.toString());
	         e.printStackTrace();
		}
	}
	
	public void processPlayerList(ArrayList<PlayerInfo> playerList) throws IOException {
		selector = Selector.open();
		if (playerList.size() == 1) {
			newPrimaryPlayerRoutine(playerList);
		} else if (playerList.size() == 2) {
			newSecondaryPlayerRoutine(playerList, false);
		} else {
			newTertiaryPlayerRoutine(playerList);
		}
		examineChannels();
	}
	
	public void newPrimaryPlayerRoutine(ArrayList<PlayerInfo> playerList) throws IOException {
		playerCategory = PlayerCategory.PRIMARY_PLAYER;
		server = createServer();
		System.out.println("I am a " + playerCategory.toString());
	}
	
	public void newSecondaryPlayerRoutine(ArrayList<PlayerInfo> playerList, Boolean isConnectedToPrimary) throws IOException {
		playerCategory = PlayerCategory.SECONDARY_PLAYER;
		server = createServer();
		if (!isConnectedToPrimary) {
			connectToServer(playerList.get(0), true);
		}
		Message message = new Message(Operation.PROPAGATE_PLAYER_LIST, gameInfo, null, currentPlayerInfo);
		messagesToSend.put(connectionToPrimary, message);
		System.out.println("I am a " + playerCategory.toString());		
	}
	
	public void newTertiaryPlayerRoutine(ArrayList<PlayerInfo> playerList) throws IOException {
		playerCategory = PlayerCategory.TERTIARY_PLAYER;
		connectToServer(playerList.get(0), true);
		Message message = new Message(Operation.PROPAGATE_PLAYER_LIST, gameInfo, null, currentPlayerInfo);
		messagesToSend.put(connectionToPrimary, message);
		System.out.println("I am a " + playerCategory.toString());
	}

	public void examineChannels() throws IOException {
		while (true) {
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = keys.iterator();
			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				try {
					if (key.isAcceptable()) {
						SocketChannel client = server.accept();
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						System.out.println("New connection accepted: " + client.getRemoteAddress());
					} else if (key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						channel.read(buffer);
						Message message = (Message) ConverterUtil.convertBytesToObject(buffer.array());
						System.out.println("Message received from: " + message.getFrom().getId() + " message: " + message);
						MessageHandler messageHandler = new MessageHandler(this, message, gameInfo, gameState, channel);
						new Thread(messageHandler).start();
						buffer.clear();
					} else if (key.isConnectable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						while (channel.isConnectionPending()) {
							channel.finishConnect();
						}
						channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);						
						System.out.println("Connected to server");
					} else if (key.isWritable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						if (channel == connectionToPrimary) {
							channel.keyFor(selector).attach(gameInfo.getPlayerList().get(0));
						}
						Message message = messagesToSend.get(channel);
						if (message != null) {
							byte[] databytes = ConverterUtil.convertObjectToBytes(message);
							ByteBuffer buffer = ByteBuffer.wrap(databytes);
							channel.write(buffer);
							messagesToSend.remove(channel);
						}
					}
					keyIterator.remove();
				} catch (Exception e) {
					PlayerInfo crashedPlayer = (PlayerInfo) key.attachment();
					if (crashedPlayer != null && !reportedPlayer.contains(crashedPlayer.getId())) {
						reportedPlayer.add(crashedPlayer.getId());
						System.out.println(key.attachment() + " is unplugged!");
						PlayerCrashHandler playerCrashHandler = new PlayerCrashHandler(this, crashedPlayer, gameInfo);
						playerCrashHandler.handle();
					}
					key.cancel();
				}
			}
		}
	}

	public void connectToServer(PlayerInfo mainPlayer, Boolean toPrimary) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		InetSocketAddress address = new InetSocketAddress(mainPlayer.getIpAddress(), mainPlayer.getPortNumber());
		socketChannel.connect(address);
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		if (toPrimary) {
			connectionToPrimary = socketChannel;
		} else {
			connectionToSecondary = socketChannel;
		}
	}

	public ServerSocketChannel createServer() throws IOException{
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress inetSocketAddress = new InetSocketAddress(currentPlayerInfo.getIpAddress(), currentPlayerInfo.getPortNumber());
		serverSocketChannel.configureBlocking(false);
		int ops = serverSocketChannel.validOps();
		serverSocketChannel.bind(inetSocketAddress);
		serverSocketChannel.register(selector, ops, currentPlayerInfo);
		System.out.println("Server socket created");
		return serverSocketChannel;
	}
	
	public void updateGameInfo(GameInfo info) {
		gameInfo = info;
		System.out.println("Current Game Info: " + gameInfo);
	}
	
	public void updateConnectionPool(PlayerInfo player, SocketChannel channel) {
		playerIdToSocket.put(player.getId(), channel);
		channel.keyFor(selector).attach(player);
	}
	
	public void updatePlayerCategory(PlayerCategory category) {
		playerCategory = category;
		System.out.println("I am promoted to " + playerCategory.toString());
	}

}
