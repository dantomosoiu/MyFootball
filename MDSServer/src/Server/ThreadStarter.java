package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ThreadStarter extends Thread {

	public ServerSocket serverSocket;
	Socket socket = null;

	public void run() {
		ClientReceiver cr;	//in
		ClientSender cs;
		try {
			System.out.println("Astept...");
			serverSocket = new ServerSocket(6667);
			while (true) {
				if ((socket = serverSocket.accept()) == null) {
					break;
				}
				if (Server.startServerByNumberOfClients == false || (Server.startServerByNumberOfClients == true && Server.connectedClients < Server.maxClients)) {

					if (Server.allowNewClients = true) {
						System.out.println("S-a conectat!");
						Server.connectedClients++;
						cr = new ClientReceiver(socket);
						cs = new ClientSender(socket, cr); //in ClientSender este o referinta a ClientReceiver-ului corespunzator aceluiasi client
						cr.setClientSender(cs);
						cr.start();
						cs.start();
					} else {
						System.out.println("S-a conectat dupa ce nu mai sunt acceptate conexiuni noi !");
					}
				}
				else {
					try {
						socket.close();
						System.out.println("Conexiunea clientului a fost respinsa deoarece sunt destui jucatori.");
					}
					catch (IOException ioe) {
						System.err.println("Nu am reusit sa resping conexiunea clientului. (numarul maxim de jucatori a fost atins)");
					}
				}
			}
		} catch (SocketException se) {
			System.out.println("Socket inchis in ThreadStarter: " + se.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
