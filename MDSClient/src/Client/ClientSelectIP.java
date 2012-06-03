package Client;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.List;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Exceptia este aruncata atunci cand nu se poate realiza conexiunea la server.
 *
 */
class ConnectionFailedException extends Exception {
}

/**
 * Clasa instantiata in clasa Client
 * Afiseaza fereastra cu selectare numelui si ip-ului
 * Instantiaza NetworkCommunication, ServerReceiver, GameLobby si porneste thread-ul care primeste obiecte de la server
 */
public class ClientSelectIP extends Frame implements ActionListener {

	Label lblTitleConnectToServer;
	Label lblTitleSettings;
	Label lblName;
	Label lblIP;
	TextField txtName;
	TextField txtIP;
	Checkbox chkFullscreen;
	Label lblResolution;
	List lstResolution;
	Label lblStatus;
	Button btnConnect;

	ClientSelectIP() {
		Font titleFont = new Font("Verdana", Font.BOLD, 20);

		// <editor-fold defaultstate="collapsed" desc="Definirea ferestrei">
		setSize(300, 340);
		setResizable(false);
		setLayout(null);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="Crearea si adaugarea elementelor">
		lblTitleConnectToServer = new Label("Connect to a server");
		lblTitleSettings = new Label("Settings");
		lblTitleConnectToServer.setFont(titleFont);
		lblTitleSettings.setFont(titleFont);

		lblName = new Label("Name:");
		lblIP = new Label("Server IP:");
		txtName = new TextField(System.getProperty("user.name"), 20);
		txtIP = new TextField("localhost", 20);

		chkFullscreen = new Checkbox("Fullscreen", true);
		lblResolution = new Label("Screen resolution:");
		lstResolution = new List(3);
		lstResolution.add("800x600x16");
		lstResolution.add("1024x768x16");
		lstResolution.add("1280x1024x16");
		lstResolution.select(1);

		lblTitleConnectToServer.setBounds(10, 30, 200, 20);
		lblName.setBounds(10, 70, 45, 12);
		txtName.setBounds(70, 66, 200, 20);
		lblIP.setBounds(10, 100, 55, 12);
		txtIP.setBounds(70, 96, 200, 20);

		lblStatus = new Label("Click Connect when you're ready.");
		btnConnect = new Button("Connect");
		btnConnect.setFont(new Font("Verdana", Font.BOLD, 12));

		lblTitleSettings.setBounds(10, 136, 200, 22);
		chkFullscreen.setBounds(10, 176, 200, 15);
		lblResolution.setBounds(10, 200, 200, 12);
		lstResolution.setBounds(10, 215, 280, 50);

		lblStatus.setBounds(10, 307, 190, 14);
		btnConnect.setBounds(210, 300, 80, 30);

		add(lblTitleConnectToServer);
		add(lblName);
		add(txtName);
		add(lblIP);
		add(txtIP);
		add(lblTitleSettings);
		add(chkFullscreen);
		add(lblResolution);
		add(lstResolution);
		add(lblStatus);
		add(btnConnect);

		btnConnect.addActionListener(this);// </editor-fold>

		setTitle("Connect");
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {	//am apasat connect
			try {
				GraphicEngine.runFullScreen = chkFullscreen.getState();
				GraphicEngine.screenResolution = lstResolution.getSelectedItem();
				lblStatus.setText("Connecting to server...");
				setComponentsEnabled(false);
				new NetworkCommunication(txtIP.getText());	//instantiez NetworkCommunication
				lblStatus.setText("Connected. Please wait...");
				NetworkCommunication.serverReceiver = new ServerReceiver();	//instantiez ServerReceiver
				GameLobby gl = new GameLobby(txtName.getText());	//instantiez GameLobby cu numele utilizatorului
				dispose();
				NetworkCommunication.serverReceiver.gameLobby = gl;	//pun referinta la gameLobby in serverReceiver din NetworkCommunication
				NetworkCommunication.serverReceiver.start();  //pornesc thread-ul care primeste obiecte de la server
			} catch (ConnectionFailedException cfe) {
				System.err.println("Nu am reusit sa ma conectez la server");
				lblStatus.setText("Connection failed !");
			} finally {
				setComponentsEnabled(true);
			}
		}
	}

	void setComponentsEnabled(boolean state) {
		txtName.setEnabled(state);
		txtIP.setEnabled(state);
		chkFullscreen.setEnabled(state);
		lstResolution.setEnabled(state);
		btnConnect.setEnabled(state);
	}

	public void paint(Graphics g) {
		g.drawLine(10, 55, 290, 55);
		g.drawLine(10, 160, 290, 160);
		g.drawLine(10, 290, 290, 290);
	}
}
