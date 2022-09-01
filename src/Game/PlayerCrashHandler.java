package Game;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;

import Tracker.GameInfo;
import Tracker.PlayerInfo;

public class PlayerCrashHandler implements Runnable {

	private Dispatcher dispatcher;
	private PlayerInfo crashedPlayer;
	private GameInfo gameInfo;

	public PlayerCrashHandler(Dispatcher dispatcher, PlayerInfo crashedPlayer, GameInfo gameInfo) {
		super();
		this.dispatcher = dispatcher;
		this.crashedPlayer = crashedPlayer;
		this.gameInfo = gameInfo;
	}

	@Override
	public void run() {
		try {
			switch (dispatcher.playerCategory) {
			case PRIMARY_PLAYER:
				System.out.println("Reporting player " + crashedPlayer.getId() + " has been crashed to Tracker");
				if (crashedPlayer.getId().equals(gameInfo.getPlayerList().get(1).getId())) {
					GameInfo latestGameInfo = dispatcher.trackerStub.reportCrashedPlayer(crashedPlayer.getId());
					dispatcher.updateGameInfo(latestGameInfo);
					Message message = new Message(Operation.NEW_SECONDARY_PLAYER, latestGameInfo, null, dispatcher.currentPlayerInfo);
					PlayerInfo newSecondaryPlayer = latestGameInfo.getPlayerList().get(1);
					SocketChannel channel = dispatcher.playerIdToSocket.get(newSecondaryPlayer.getId());
					dispatcher.messagesToSend.put(channel, message);
					channel.keyFor(dispatcher.selector).interestOps(SelectionKey.OP_WRITE);
				} else {
					GameInfo latestGameInfo = dispatcher.trackerStub.reportCrashedPlayer(crashedPlayer.getId());
					dispatcher.updateGameInfo(latestGameInfo);
					Message message = new Message(Operation.PROPAGATE_PLAYER_LIST, latestGameInfo, null, dispatcher.currentPlayerInfo);
					dispatcher.messagesToSend.put(dispatcher.connectionToSecondary, message);
					dispatcher.connectionToSecondary.keyFor(dispatcher.selector).interestOps(SelectionKey.OP_WRITE);
				}
				break;
			case SECONDARY_PLAYER:
				if (crashedPlayer.getId().equals(gameInfo.getPlayerList().get(0).getId())) {
					System.out.println("Reporting player " + crashedPlayer.getId() + " has been crashed to Tracker");
					GameInfo latestGameInfo = dispatcher.trackerStub.reportCrashedPlayer(crashedPlayer.getId());
					dispatcher.updateGameInfo(latestGameInfo);
					dispatcher.updatePlayerCategory(PlayerCategory.PRIMARY_PLAYER);
				}
				break;
			case TERTIARY_PLAYER:
				if (crashedPlayer.getId().equals(gameInfo.getPlayerList().get(0).getId())) {
					GameInfo latestGameInfo = null;
					do {
						latestGameInfo = dispatcher.trackerStub.fetchGameInfo();
					} while (latestGameInfo.getVersion() <= gameInfo.getVersion()
							|| crashedPlayer.getId().equals(latestGameInfo.getPlayerList().get(0).getId()));
					dispatcher.updateGameInfo(latestGameInfo);
					if (dispatcher.currentPlayerInfo.getId().equals(latestGameInfo.getPlayerList().get(1).getId())) {
						try {
							dispatcher.newSecondaryPlayerRoutine(latestGameInfo.getPlayerList(), false);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						try {
							dispatcher.newTertiaryPlayerRoutine(latestGameInfo.getPlayerList());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			default:
				break;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
}
