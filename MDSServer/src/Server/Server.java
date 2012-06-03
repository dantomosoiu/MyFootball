package Server;

import Transmitable.toClient.GameState;
import Transmitable.toClient.PlayerTable;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server extends Frame implements ActionListener, ItemListener, TextListener {

	static String state = "LOBBY";
	static PlayerTable playerTable;
	static GameState gameState;
	static KeyMap keyMap;
	static boolean allowNewClients = true;
	static boolean startServerByNumberOfClients = false;
	static int maxClients = 22;
	static int connectedClients = 0;
	static int matchMin = 5;
	Label lblIP;
	Label lblMatchLength;
	Label lblConnectionType;
	Label lblTotalPlayers;
	TextField txtIP;
	TextField txtMatchLength;
	TextField txtNumPlayers;
	Checkbox chkAllReady;
	Checkbox chkAllPlayers;
	CheckboxGroup chkStartMode;
	Button btnStartServer;
	Button btnExitServer;

	Server() {
		String ipAddress;
		setSize(400, 230);
		setResizable(false);
		setLayout(null);

		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			ipAddress = inetAddress.getHostAddress();
		} catch (UnknownHostException uhe) {
			ipAddress = "";
		}

		lblIP = new Label("Server IP:");
		txtIP = new TextField(ipAddress);
		lblMatchLength = new Label("Match length (minutes):");
		txtMatchLength = new TextField("5");
		lblConnectionType = new Label("Select a method to start game:");
		chkStartMode = new CheckboxGroup();
		chkAllReady = new Checkbox("When all players are ready", true, chkStartMode);
		chkAllPlayers = new Checkbox("When all positions are occupied and all players are ready", false, chkStartMode);
		lblTotalPlayers = new Label("Total players:");
		txtNumPlayers = new TextField("22");
		btnStartServer = new Button("Start Server");
		btnExitServer = new Button("Exit Server");

		txtIP.setEditable(false);
		txtNumPlayers.setEnabled(false);
		btnStartServer.setFont(new Font("Verdana", Font.BOLD, 12));
		btnExitServer.setFont(new Font("Verdana", Font.BOLD, 12));

		lblIP.setBounds(10, 40, 60, 14);
		txtIP.setBounds(lblIP.getX() + lblIP.getWidth(), lblIP.getY() - 3, 100, 20);
		lblMatchLength.setBounds(10, txtIP.getY() + txtIP.getHeight() + 10, 135, 14);
		txtMatchLength.setBounds(lblMatchLength.getX() + lblMatchLength.getWidth(), lblMatchLength.getY() - 3, 40, 20);
		lblConnectionType.setBounds(10, txtMatchLength.getY() + txtMatchLength.getHeight() + 10, 500, 14);
		chkAllReady.setBounds(10, lblConnectionType.getY() + lblConnectionType.getHeight() + 5, 400, 20);
		chkAllPlayers.setBounds(10, chkAllReady.getY() + chkAllReady.getHeight(), 400, 20);
		lblTotalPlayers.setBounds(30, chkAllPlayers.getY() + chkAllPlayers.getHeight() + 5, 80, 20);
		txtNumPlayers.setBounds(lblTotalPlayers.getX() + lblTotalPlayers.getWidth(), chkAllPlayers.getY() + chkAllPlayers.getHeight() + 5, 40, 20);
		btnStartServer.setBounds(getWidth() - 110, getHeight() - 40, 100, 30);
		btnExitServer.setBounds(btnStartServer.getX(), btnStartServer.getY(), btnStartServer.getWidth(), btnStartServer.getHeight());

		add(lblIP);
		add(txtIP);
		add(lblMatchLength);
		add(txtMatchLength);
		add(lblConnectionType);
		add(chkAllReady);
		add(chkAllPlayers);
		add(lblTotalPlayers);
		add(txtNumPlayers);
		add(btnStartServer);
		add(btnExitServer);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		chkAllReady.addItemListener(this);
		chkAllPlayers.addItemListener(this);
		btnStartServer.addActionListener(this);
		btnExitServer.addActionListener(this);
		txtMatchLength.addTextListener(this);

		setTitle("Server");
		setVisible(true);

	}

	public static void startServer() {
		Thread serverThread = new Thread(new Runnable() {

			public void run() {
				System.out.println("Pornesc ThreadStarter");
				ThreadStarter threadStarter = new ThreadStarter();
				threadStarter.start();
				while (!Server.playersReady()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
					}
				}
				System.out.println("Inchid ThreadStarter");
				try {
					threadStarter.serverSocket.close();
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}
				System.out.println("Intram in GamePlay");
				GamePlay gamePlay = new GamePlay(matchMin);
				gamePlay.start();
			}
		});
		serverThread.start();
	}

	public static void exitServer() {
		System.exit(0);
	}

	static boolean playersReady() {
		int i, team1 = 0, team2 = 0;
		boolean ready = true;

		if (playerTable.isReady.size() == 0) {
			//System.out.println("Nu sunt jucatori conectati.");
			ready = false;
		}
		for (i = 0; i < playerTable.isReady.size(); i++) {
			if (playerTable.getIsReady(i) == false) {
				//System.out.println("Nu sunt toti GATA");
				ready = false;
			}
			if(playerTable.getCurrentTeam(i) == 1) {
				team1++;
			}
			else {
				team2++;
			}
		}

		if(team1 == 0 || team2 == 0) {
			return false;
		}

		if(ready) {
			if(startServerByNumberOfClients) {
				if(playerTable.playerName.size() == maxClients) {
					System.out.println("Toti jucatorii sunt gata. Nu mai accept conexiuni noi.");
					allowNewClients = false;
					ready = true;
				}
				else {
					ready = false;
				}
			}
		}

		return ready;
	}

	public static void main(String[] args) {
		playerTable = new PlayerTable();
		Server server = new Server();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStartServer) {
			try {
				if (chkAllReady.getState() == true) {
					startServerByNumberOfClients = false;
				} else {
					startServerByNumberOfClients = true;
					maxClients = Integer.parseInt(txtNumPlayers.getText());
				}
				chkAllReady.setEnabled(false);
				chkAllPlayers.setEnabled(false);
				txtNumPlayers.setEnabled(false);
				btnExitServer.setVisible(true);
				btnStartServer.setVisible(false);
				btnStartServer.setLocation(900, 900);
				startServer();
			} catch (NumberFormatException nfe) {
			}
		} else if (e.getSource() == btnExitServer) {
			exitServer();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (chkAllPlayers.getState() == true) {
			txtNumPlayers.setEnabled(true);
		} else {
			txtNumPlayers.setEnabled(false);
		}
	}

	public void textValueChanged(TextEvent e) {
		int x;
		try {
			x = Integer.parseInt(txtMatchLength.getText());
			if(x <= 0) {
				throw new NumberFormatException();
			}
			matchMin = x;
			btnStartServer.setEnabled(true);
		} catch(NumberFormatException nfe) {
			btnStartServer.setEnabled(false);
		}
	}
}

