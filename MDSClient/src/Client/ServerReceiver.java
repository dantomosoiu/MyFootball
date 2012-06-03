package Client;

import Transmitable.toClient.GameState;
import Transmitable.toClient.PlayerTable;
import java.io.IOException;

public class ServerReceiver extends Thread {

	PlayerTable playerTable;
	GameState gameState;
	GameLobby gameLobby;
	GraphicEngine graphicEngine;

	public ServerReceiver() {
		graphicEngine = null;
	}

	/**
	 * primeste GameState sau PlayerTable si apeleaza GraphicEngine
	 */
	public void run() {
		Object obj = null;
		while (Client.isRunning) {

			try {
				obj = NetworkCommunication.receive();
			} // <editor-fold defaultstate="collapsed" desc="blocuri catch">
			catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(0);
			} catch (ClassNotFoundException e) {
				System.err.println(e.getMessage());
			}// </editor-fold>

			if (Client.state.equalsIgnoreCase("LOBBY")) {
				try {
					playerTable = (PlayerTable) obj;
					if (playerTable.nextState.equalsIgnoreCase("PLAY")) {	//primesc pentru prima data PLAY; trec in starea PLAY, pornind GraphicEngine
						gameLobby.setGameReady();
						Client.state = "PLAY";
						gameLobby.dispose();	//inchid fereastra GameLobby
						graphicEngine = new GraphicEngine();	//si deschid fereastra GraphicEngine
					} else {
						gameLobby.drawTable(playerTable);
					}
				} // <editor-fold defaultstate="collapsed" desc="bloc catch">
				catch (ClassCastException cce) {
					System.err.println(cce.getMessage());
				}// </editor-fold>


			} else if (Client.state.equalsIgnoreCase("PLAY")) {
				try {
					gameState = (GameState) obj;
					if(graphicEngine != null) {
						graphicEngine.render(gameState);
					}
				} // <editor-fold defaultstate="collapsed" desc="bloc catch">
				catch (ClassCastException cce) {
					System.err.println(cce.getMessage());
				}// </editor-fold>
			}
		}
		
	}
}
