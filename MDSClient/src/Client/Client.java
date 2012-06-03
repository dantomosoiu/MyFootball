package Client;

import java.util.UUID;

public class Client {
	/**
	 * state = LOBBY sau PLAY
	 */
	static String state = "LOBBY";
	static boolean isRunning = true;
	static UUID playerID;

	public static void exit() {
		System.exit(0);
	}

	public static void main(String[] args) {

		new ClientSelectIP();

	}

}
