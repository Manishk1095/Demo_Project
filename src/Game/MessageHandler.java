package Game;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import Tracker.GameInfo;

public class MessageHandler implements Runnable {
	
	private Dispatcher dispatcher;
	private Message message;
	private GameInfo gameInfo;
	private GameState gameState;
	private SocketChannel clientChannel;
	
	public MessageHandler(Dispatcher dispatcher, Message message, GameInfo gameInfo, GameState gameState, SocketChannel clientChannel) {
		super();
		this.dispatcher = dispatcher;
		this.message = message;
		this.gameInfo = gameInfo;
		this.gameState = gameState;
		this.clientChannel = clientChannel;
	}
	
	@Override
	public void run() {
		Operation operation = message.getOperation();
		switch (operation) {
		case PROPAGATE_PLAYER_LIST:
			if (gameInfo.getVersion() <= message.getGameInfo().getVersion() || dispatcher.connectionToSecondary == null) {
				if (gameInfo.getVersion() < message.getGameInfo().getVersion()) {
					dispatcher.updateGameInfo(message.getGameInfo());					
				}
				dispatcher.updateConnectionPool(message.getFrom(), clientChannel);
				if (message.getFrom().getId().equals(message.getGameInfo().getPlayerList().get(1).getId())) {
					try {
						dispatcher.connectToServer(message.getGameInfo().getPlayerList().get(1), false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (dispatcher.playerCategory.equals(PlayerCategory.PRIMARY_PLAYER)) {
					while (dispatcher.connectionToSecondary == null) {
						System.out.println("Waiting to connect to new secondary");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.println("Sync GameInfo with Secondary");
					System.out.println("The GameInfo is: " + message.getGameInfo());
					Message msg = new Message(Operation.PROPAGATE_PLAYER_LIST, message.getGameInfo(), message.getGameState(), dispatcher.currentPlayerInfo);
					dispatcher.messagesToSend.put(dispatcher.connectionToSecondary, msg);						
				}
			}
			break;
		case NEW_SECONDARY_PLAYER:
			try {
				dispatcher.updateGameInfo(message.getGameInfo());
				dispatcher.newSecondaryPlayerRoutine(message.getGameInfo().getPlayerList(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
			
		}
	}
}
