package Game;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Game {

	public static void main(String[] args) {
		String trackerIpAddress = args[0];
		int trackerPortNumber = Integer.valueOf(args[1]);
		String playerId = args[2];
		new Thread(new Dispatcher(trackerIpAddress, trackerPortNumber, playerId)).start();

		// Code for UI goes here ...
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		try {
//			System.out.println("> you can now communicate to other peers");
//			while (true) {
//				String message = br.readLine();
//				System.out.println("input: " + message);
//			}
//		} catch (Exception e) {}	
	}
}
