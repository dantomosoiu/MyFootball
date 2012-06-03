package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Instantiata in ClientSelectIP
 * Trimite catre server obiecte serializate (PlayerAction sau ActionKey). Serverul va sti ce obiect este in functie de variabila nextState, stocata pe server.
 * @see ClientSelectIP
 * 
 */
public class NetworkCommunication {

	static Socket socket = null;
	static int port = 6667;
	static ObjectOutputStream out;
	static ObjectInputStream in;
	static ServerReceiver serverReceiver;


	NetworkCommunication(String IP) throws ConnectionFailedException {
		try {
			socket = new Socket(IP, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			throw new ConnectionFailedException();
		}
	}

	static boolean isConnected() {
		if(socket == null) {
			return false;
		}
		return true;
	}

	static void send(Object o) {
		try {
			while(!isConnected()) {}			
			out.writeObject(o);
			out.flush();
			out.reset();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	static Object receive() throws IOException, ClassNotFoundException {
		return in.readObject();
	}
}
