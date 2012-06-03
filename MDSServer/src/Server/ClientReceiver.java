package Server;

import Transmitable.toServer.ActionKey;
import Transmitable.toServer.PlayerAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Clasa initializata in ThreadStarter pentru fiecare client conectat.
 * @see ThreadStarter
 */
public class ClientReceiver extends Thread {

	UUID playerID;
	Socket socket;
	ObjectInputStream in;
	PlayerAction pa;
	ActionKey ak;
	int index = -1; //pe ce pozitie se afla jucatorul in GameState.players
	ClientSender clientSender;

	ClientReceiver(Socket s) {
		playerID = null;
		socket = s;
		try {
			in = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setClientSender(ClientSender clientSender) {
		this.clientSender = clientSender;
	}

	public void run() {
		Object obj = null;
		while (true) {
			//System.out.println(Server.state);
			try {
				obj = in.readObject();
			} // <editor-fold defaultstate="collapsed" desc="blocuri catch">
			catch (IOException e) {
				clientSender.setConnectionActive(false);
				System.out.println("Un jucator a iesit.");
				if (Server.state.equalsIgnoreCase("LOBBY")) {
					Server.playerTable.removePlayer(playerID);
				} else if (Server.state.equalsIgnoreCase("PLAY")) {

						if (Server.gameState.getPlayers().elementAt(index).isHasBall()) {
							System.out.println("am intrat in if");
							Server.gameState.getPlayers().elementAt(index).setHasBall(false);
							Server.gameState.getBall().setOwner(null);
							Server.gameState.getBall().setIsOwned(false);
						}
						
					Server.gameState.getPlayers().elementAt(index).setIsActive(false);

				}
				Server.connectedClients--;
				System.err.println("ClientReceiver.Run.IOException : " + e.getMessage());
				break;
			} catch (ClassNotFoundException e) {
				System.err.println(e.getMessage());
			}// </editor-fold>
			if (Server.state.equalsIgnoreCase("LOBBY")) {

				if (Server.allowNewClients = true) {
					try {
						pa = (PlayerAction) obj;
						if (playerID == null) {
							playerID = pa.playerID;
						}
						Server.playerTable.setPlayer(pa);

					} // <editor-fold defaultstate="collapsed" desc="blocuri catch">
					catch (ClassCastException cce) {
						System.err.println(cce.getMessage());
						break;
					}
					// </editor-fold>
				}
			} else if (Server.state.equalsIgnoreCase("PLAY")) {
				try {
					ak = (ActionKey) obj;
					KeyMap.update(ak, index);
				} // <editor-fold defaultstate="collapsed" desc="bloc catch">
				catch (ClassCastException cce) {
					System.err.println(cce.getMessage());
					break;
				}// </editor-fold>
			}

		}

		if (socket.isClosed() == false) {
			try {
				in.close();
				socket.close();
				System.out.println("Am inchis socket-ul in ClientReceiver");
			} catch (IOException ex) {
				System.err.println("ClientReceiver.Run.DupaWhile : Nu am reusit sa inchid socket-ul");
			}
		}

		if (Server.state.equalsIgnoreCase("LOBBY") && Server.playerTable.playerName.size() == 0) {
			System.out.println("Nu mai sunt jucatori in LOBBY. Inchid serverul...");
			System.exit(0);
		}

	}
}
