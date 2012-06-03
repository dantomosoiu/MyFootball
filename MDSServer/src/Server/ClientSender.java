package Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Clasa initializata in ThreadStarter pentru fiecare client conectat.
 * @see ThreadStarter
 */
public class ClientSender extends Thread {

	Socket socket;
	ObjectOutputStream out;
	ClientReceiver clientReceiver;
	boolean connectionActive = true;

	ClientSender(Socket s, ClientReceiver cr) {
		socket = s;
		clientReceiver = cr; //folosim referinta ca sa stim index-ul corespunzator clientului
		try {
			out = new ObjectOutputStream(s.getOutputStream());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void setConnectionActive(boolean connectionActive) {
		this.connectionActive = connectionActive;
	}

	public void run() {
		boolean firstTime = true; //vede cand a trecut in starea PLAY, in scopul de a initializa index
		int i;
		while (connectionActive) {
			if (Server.state.equalsIgnoreCase("LOBBY")) {
				try {
					sleep(1000);
					out.writeObject(Server.playerTable);
					out.flush();
					out.reset();
				} // <editor-fold defaultstate="collapsed" desc="blocuri catch">
				catch (InterruptedException ie) {
					System.err.println("ClientSender.Run.LOBBY.InterruptedException : " + ie.getMessage());
					break;
				} catch (IOException e) {
					System.err.println("ClientSender.Run.LOBBY.IOException : " + e.getMessage());
					break;
				}// </editor-fold>
			} else if (Server.state.equalsIgnoreCase("PLAY")) {
				try {
					if (firstTime) {
						firstTime = false;
						Server.playerTable.nextState = "PLAY";
						for (i = 0; i < Server.gameState.getPlayers().size(); i++) {
							if (Server.gameState.getPlayers().elementAt(i).getPlayerID().equals(clientReceiver.playerID)) {
								clientReceiver.setIndex(i);
								System.out.println("Am setat index " + i);
								break;
							}
						}

						if(clientReceiver.getIndex() != -1) {
							out.writeObject(Server.playerTable);
							out.flush();
							out.reset();
						}
						else {
							System.out.println("Nu ii trimit nimic clientului pentru ca nu are setat index in GameState.");
						}
					} else {
						sleep(40);
						out.writeObject(Server.gameState);
						out.flush();
						out.reset();

					}
				} // <editor-fold defaultstate="collapsed" desc="blocuri catch">
				catch (InterruptedException ie) {
					System.err.println("ClientSender.Run.PLAY.InterruptedException : " + ie.getMessage());
					break;
				} catch (IOException e) {
					System.err.println("ClientSender.Run.PLAY.IOException : " + e.getMessage());
					break;
				}// </editor-fold>
			}
		}
		
		if(socket.isClosed() == false) {
			try {
				out.close();
				socket.close();
				System.out.println("Am inchis socket-ul in ClientSender");
				Server.connectedClients--;
			} catch (IOException ex) {
				System.err.println("ClientSender.Run.DupaWhile : Nu am reusit sa inchid socket-ul");
			}
		}

		if(Server.state.equalsIgnoreCase("LOBBY") && Server.playerTable.playerName.size() == 0) {
			System.out.println("Nu mai sunt jucatori in LOBBY. Inchid serverul...");
			System.exit(0);
		}
	}
}
