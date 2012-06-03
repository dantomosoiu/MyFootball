package Client;

import Transmitable.toClient.Player.PlayerPosition;
import Transmitable.toClient.PlayerTable;
import Transmitable.toServer.PlayerAction;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Clasa instantiata din ClientSelectIP
 * Afiseaza doua liste cu jucatorii celor 2 echipe deindata ce primeste PlayerTable de la server
 *
 */
public class GameLobby extends Frame implements ActionListener, ItemListener {

	public PlayerAction pa;
	boolean preventDrawTable = false;
	Label lblName;
	Label lblPosition;
	Label lblSelectedPosition;
	Label lblPositionMessage;
	Label lblListPosition;
	Label lblTeam1, lblTeam2;
	Label lblStatus;
	Button btnSetTeam1;
	Button btnSetTeam2;
	Button btnReady;
	List listPosition;
	List listTeam1;
	List listTeam2;
	Color myTeam = new Color(230, 255, 230);
	Color otherTeam = Color.white;
	Color colorReady = Color.green;
	Color colorNotReady = new Color(226, 226, 226);
	PlayerPosition position = PlayerPosition.NONE;

	public GameLobby(String name) {
		Font titleFont = new Font("Verdana", Font.BOLD, 20);
		Font labelFont = new Font("Verdana", Font.BOLD, 14);

		pa = new PlayerAction(name);
		Client.playerID = pa.playerID;
		NetworkCommunication.send(pa);
		setLayout(null);
		setSize(640, 580);
		setResizable(false);

		lblName = new Label(name);
		lblPosition = new Label("Position");
		lblSelectedPosition = new Label("(none)");
		lblListPosition = new Label("Select a position:");
		lblPositionMessage = new Label("", Label.RIGHT);

		lblTeam1 = new Label("Team 1");
		lblTeam2 = new Label("Team 2");
		btnSetTeam1 = new Button("Play in TEAM 1");
		btnSetTeam2 = new Button("Play in TEAM 2");
		btnReady = new Button("Ready");
		lblStatus = new Label("", Label.RIGHT);

		listTeam1 = new List(10);
		listTeam2 = new List(10);
		listPosition = new List();

		listPosition.add("Goalkeeper");
		listPosition.add("Defender");
		listPosition.add("Midfielder");
		listPosition.add("Attacker");

		lblName.setFont(titleFont);
		lblPosition.setFont(labelFont);
		lblSelectedPosition.setFont(titleFont);
		lblPositionMessage.setForeground(Color.red);

		lblTeam1.setFont(labelFont);
		lblTeam2.setFont(labelFont);
		btnReady.setBackground(colorNotReady);
		listTeam1.setBackground(myTeam);

		lblName.setBounds(40, 75, 250, 22);
		lblPosition.setBounds(340, 60, 90, 20);
		lblSelectedPosition.setBounds(lblPosition.getX(), lblPosition.getY() + lblPosition.getHeight(), 150, 22);
		lblListPosition.setBounds(520, 40, 100, 20);
		listPosition.setBounds(lblListPosition.getX(), lblListPosition.getY() + lblListPosition.getHeight(), 100, 70);
		lblPositionMessage.setBounds(300, listPosition.getY() + listPosition.getHeight(), 320, 20);

		listTeam1.setBounds(70, 220, 200, 250);
		listTeam2.setBounds(listTeam1.getX() + listTeam1.getWidth() + 100, listTeam1.getY(), listTeam1.getWidth(), listTeam1.getHeight());
		lblTeam1.setBounds(listTeam1.getX(), listTeam1.getY() - 25, 50, 18);
		lblTeam2.setBounds(listTeam2.getX(), lblTeam1.getY(), 50, 18);
		btnSetTeam1.setBounds(listTeam1.getX() + listTeam1.getWidth() - 100, listTeam1.getY() - 30, 100, 25);
		btnSetTeam2.setBounds(listTeam2.getX() + listTeam2.getWidth() - 100, btnSetTeam1.getY(), 100, 25);
		btnReady.setBounds(getWidth() - 100, getHeight() - 40, 80, 30);
		lblStatus.setBounds(btnReady.getX() - 310, btnReady.getY() + 8, 300, 14);

		add(lblName);
		add(lblPosition);
		add(lblSelectedPosition);
		add(lblListPosition);
		add(listPosition);
		add(lblPositionMessage);
		add(lblTeam1);
		add(lblTeam2);
		add(btnSetTeam1);
		add(btnSetTeam2);
		add(btnReady);
		add(listTeam1);
		add(listTeam2);
		add(lblStatus);

		btnSetTeam1.addActionListener(this);
		btnSetTeam2.addActionListener(this);
		btnReady.addActionListener(this);
		listPosition.addItemListener(this);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setVisible(true);
		setTitle(name);
	}

	/**
	 * Reactualizeaza listele de jucatori
	 *
	 */
	public void drawTable(PlayerTable pt) {
		if (!preventDrawTable) {
			int i;
			listTeam1.removeAll();
			listTeam2.removeAll();
			for (i = 0; i < pt.playerName.size(); i++) {
				if (pt.currentTeam.elementAt(i) == 1) {
					listTeam1.add(pt.playerName.elementAt(i) + " [" + pt.position.elementAt(i).toString() + "]" + ((pt.isReady.elementAt(i)) ? " (ready)" : ""));
				}
				if (pt.currentTeam.elementAt(i) == 2) {
					listTeam2.add(pt.playerName.elementAt(i) + " [" + pt.position.elementAt(i).toString() + "]" + ((pt.isReady.elementAt(i)) ? " (ready)" : ""));
				}
				if (pt.playerID.elementAt(i).equals(Client.playerID)) {
					if (!pt.position.elementAt(i).equals(position) || pt.position.elementAt(i).equals(PlayerPosition.NONE)) {
						lblPositionMessage.setText("Selected position is not available or not accepted.");
						position = PlayerPosition.NONE;
					} else {
						lblPositionMessage.setText("");
					}
				}
			}

			if(listTeam1.getItemCount() == 0 || listTeam2.getItemCount() == 0) {
				lblStatus.setText("One of the teams has no players !");
			}
			else {
				lblStatus.setText("");
			}

			if (position == PlayerPosition.NONE) {
				btnReady.setEnabled(false);
				lblSelectedPosition.setText("NONE");
			} else {
				btnReady.setEnabled(true);
			}
		}
	}

	public void setGameReady() {
		listPosition.setEnabled(false);
		btnSetTeam1.setEnabled(false);
		btnSetTeam2.setEnabled(false);
		btnReady.setEnabled(false);
		lblStatus.setText("Preparing for game...");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSetTeam1) {
			pa.currentTeam = 1;
			listTeam1.setBackground(myTeam);
			listTeam2.setBackground(otherTeam);
		} else if (e.getSource() == btnSetTeam2) {
			pa.currentTeam = 2;
			listTeam1.setBackground(otherTeam);
			listTeam2.setBackground(myTeam);
		} else if (e.getSource() == btnReady) {
			pa.isReady = !pa.isReady;
			if (pa.isReady == true) {
				btnReady.setBackground(colorReady);
				btnSetTeam1.setEnabled(false);
				btnSetTeam2.setEnabled(false);
				listPosition.setEnabled(false);
			} else {
				btnReady.setBackground(colorNotReady);
				btnSetTeam1.setEnabled(true);
				btnSetTeam2.setEnabled(true);
				listPosition.setEnabled(true);
			}
		}
		if (Client.state.equalsIgnoreCase("LOBBY")) {
			preventDrawTable = true;
			NetworkCommunication.send(pa);
			preventDrawTable = false;
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == listPosition) {
			if (listPosition.getSelectedIndex() == 0) {
				position = PlayerPosition.GOALKEEPER;
			} else if (listPosition.getSelectedIndex() == 1) {
				position = PlayerPosition.DEFENDER;
			} else if (listPosition.getSelectedIndex() == 2) {
				position = PlayerPosition.MIDFIELDER;
			} else if (listPosition.getSelectedIndex() == 3) {
				position = PlayerPosition.ATTACKER;
			}

			lblSelectedPosition.setText(position.toString());
			listPosition.deselect(listPosition.getSelectedIndex());

			if (Client.state.equalsIgnoreCase("LOBBY")) {
				pa.position = position;
				preventDrawTable = true;
				NetworkCommunication.send(pa);
				preventDrawTable = false;
			}
		}
	}

	public void paint(Graphics g) {
		g.drawLine(10, lblPositionMessage.getY() + lblPositionMessage.getHeight() + 5, getWidth() - 20, lblPositionMessage.getY() + lblPositionMessage.getHeight() + 5);
		g.drawLine(getWidth() / 2, listTeam1.getY() - 20, getWidth() / 2, listTeam1.getY() + listTeam1.getHeight());
		g.drawLine(10, listTeam1.getY() + listTeam1.getHeight() + 60, getWidth() - 10, listTeam1.getY() + listTeam1.getHeight() + 60);
	}
//	public static void main(String[] args) {
//		GameLobby gl = new GameLobby("Test");
//	}
}
