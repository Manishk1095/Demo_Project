package Tracker;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TrackerStub extends Remote {

	public GameInfo fetchGameInfoAndRegisterNewPlayer(String id, String ipAddress) throws RemoteException;
	
	public GameInfo reportCrashedPlayer(String playerId) throws RemoteException;
	
	public GameInfo fetchGameInfo() throws RemoteException;

}
