package Game;

public class Game {

	public static void main(String[] args) {
		String trackerIpAddress = args[0];
		int trackerPortNumber = Integer.valueOf(args[1]);
		String playerId = args[2];
		new Dispatcher(trackerIpAddress, trackerPortNumber, playerId).run();
		
		// Code for UI goes here ...
	}
}
