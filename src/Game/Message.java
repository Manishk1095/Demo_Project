package Game;

import java.io.Serializable;

import Tracker.GameInfo;
import Tracker.PlayerInfo;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Operation operation;
	private GameInfo gameInfo;
	private GameState gameState;
	private PlayerInfo from;
	
	public Message(Operation operation, GameInfo gameInfo, GameState gameState, PlayerInfo from) {
		this.setOperation(operation);
		this.setGameInfo(gameInfo);
		this.setGameState(gameState);
		this.setFrom(from);
	}

	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public PlayerInfo getFrom() {
		return from;
	}

	public void setFrom(PlayerInfo from) {
		this.from = from;
	}

	@Override
	public String toString() {
		return "Message [operation=" + operation + ", gameInfo=" + gameInfo + ", gameState=" + gameState + ", from="
				+ from + "]";
	}
}
