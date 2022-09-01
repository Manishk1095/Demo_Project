package Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import Tracker.PlayerInfo;

public class GameState implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6503957062286496164L;

	private ArrayList<PlayerLocation> playerLocations;
	
	private ArrayList<TreasureLocation> treasureLocations;
	
	private HashMap<PlayerInfo, Integer> playerScores;
	
	public GameState() {}

	public ArrayList<PlayerLocation> getPlayerLocations() {
		return playerLocations;
	}

	public void setPlayerLocations(ArrayList<PlayerLocation> playerLocations) {
		this.playerLocations = playerLocations;
	}

	public ArrayList<TreasureLocation> getTreasureLocations() {
		return treasureLocations;
	}

	public void setTreasureLocations(ArrayList<TreasureLocation> treasureLocations) {
		this.treasureLocations = treasureLocations;
	}

	public HashMap<PlayerInfo, Integer> getPlayerScores() {
		return playerScores;
	}

	public void setPlayerScores(HashMap<PlayerInfo, Integer> playerScores) {
		this.playerScores = playerScores;
	}

	@Override
	public String toString() {
		return "GameState [playerLocations=" + playerLocations + ", treasureLocations=" + treasureLocations
				+ ", playerScores=" + playerScores + "]";
	}
}
