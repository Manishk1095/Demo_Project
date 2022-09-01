package Game;

import java.io.Serializable;

import Tracker.PlayerInfo;

public class PlayerLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4970332837254613822L;

	private int x;
	
	private int y;
	
	private PlayerInfo player;

	public PlayerLocation(int x, int y, PlayerInfo player) {
		super();
		this.x = x;
		this.y = y;
		this.player = player;
	}

	@Override
	public String toString() {
		return "PlayerLocation [x=" + x + ", y=" + y + ", player=" + player + "]";
	}

}
