package Tracker;

import java.io.Serializable;
import java.util.ArrayList;

public class GameInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int N;

	private int K;
	
	private ArrayList<PlayerInfo> playerList;
	
	private int version;
	
	public GameInfo(int N, int K) {
		this.setN(N);
		this.setK(K);
		setPlayerList(new ArrayList<PlayerInfo>());
		setVersion(0);
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public ArrayList<PlayerInfo> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(ArrayList<PlayerInfo> playerList) {
		this.playerList = playerList;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "GameInfo [N=" + N + ", K=" + K + ", playerList=" + playerList + ", version=" + version + "]";
	}
	
}
