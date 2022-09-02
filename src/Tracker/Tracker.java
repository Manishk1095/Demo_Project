package Tracker;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Tracker implements TrackerStub {
	
	private static GameInfo gameInfo;
	private static int playerPort = 5000;

	public Tracker() {}

	@Override
	public GameInfo fetchGameInfoAndRegisterNewPlayer(String id, String ipAddress) throws RemoteException {
		synchronized (gameInfo) {
			int portNumber = playerPort++;
			PlayerInfo playerInfo = new PlayerInfo(id, ipAddress, portNumber);
			gameInfo.getPlayerList().add(playerInfo);
			gameInfo.setVersion(gameInfo.getVersion() + 1);
			System.out.println("New player joins the game:");
			System.out.println(gameInfo);
		}
		return gameInfo;
	}
	
	@Override
	public GameInfo reportCrashedPlayer(String playerId) throws RemoteException {
		synchronized (gameInfo) {
			System.out.println(playerId + " just crashed, removing it from player list!");
			gameInfo.getPlayerList().removeIf(p -> (p.getId().equals(playerId)));
			gameInfo.setVersion(gameInfo.getVersion() + 1);
			System.out.println(gameInfo);
		}
		return gameInfo;
	}
	
	@Override
	public GameInfo fetchGameInfo() throws RemoteException {
		return gameInfo;
	}
	
	public static void main(String[] args) {
		try {
			int port = Integer.valueOf(args[0]);
			int N = Integer.valueOf(args[1]);
			int K = Integer.valueOf(args[2]);
			gameInfo = new GameInfo(N, K);
			
			Tracker tracker = new Tracker();
			TrackerStub stub = (TrackerStub) UnicastRemoteObject.exportObject(tracker, 0);
			
			Registry registry = LocateRegistry.getRegistry(port);
			registry.bind("Tracker", stub);
			
			System.out.println("Tracker is ready");
		} catch (Exception ex) {
			System.err.println("Tracker exception: " + ex.toString());
			ex.printStackTrace();
		}
	}
}
