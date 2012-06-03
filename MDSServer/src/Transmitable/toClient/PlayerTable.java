package Transmitable.toClient;

import Transmitable.toClient.Player.PlayerPosition;
import Transmitable.toServer.PlayerAction;
import java.io.Serializable;
import java.util.Vector;
import java.util.UUID;

public class PlayerTable implements Serializable {

	public Vector<String> playerName;
	public Vector<Boolean> isReady;
	public Vector<Integer> currentTeam;
	public Vector<PlayerPosition> position;
	public Vector<UUID> playerID;
	public String nextState;

	public PlayerTable() {
		playerName = new Vector<String>();
		isReady = new Vector<Boolean>();
		currentTeam = new Vector<Integer>();
		position = new Vector<PlayerPosition>();
		playerID = new Vector<UUID>();
		nextState = "LOBBY";
	}

	/**
	 * Adauga un jucator nou.
	 *
	 */
	synchronized private void addPlayer(PlayerAction pa) {
		System.out.println("PlayerTable.addPlayer: " + pa.playerName);
		playerName.add(pa.playerName);
		isReady.add(pa.isReady);
		playerID.add(pa.playerID);
		currentTeam.add(pa.currentTeam);
		position.add(pa.position);
		System.out.println("Am adaugat jucator pe pozitia " + pa.position.toString());
	}

	/**
	 * Stabileste daca modifica un jucator existent sau adauga unul nou.
	 *
	 */
	synchronized public void setPlayer(PlayerAction pa) {
		int i, j;
		boolean found = false, positionOccupied = false;
		System.out.println("PlayerTable.setPlayer");
		for (i = 0; i < playerName.size(); i++) {
			if (playerID.elementAt(i).equals(pa.playerID)) {
				isReady.set(i, pa.isReady);
				currentTeam.set(i, pa.currentTeam);
				if(pa.position == PlayerPosition.GOALKEEPER) {
					for(j = 0; j < currentTeam.size(); j++) {
						if(!playerID.elementAt(i).equals(playerID.elementAt(j)) && currentTeam.elementAt(j).equals(currentTeam.elementAt(i)) && position.elementAt(j).equals(PlayerPosition.GOALKEEPER)) {
							position.set(i, PlayerPosition.NONE);
							positionOccupied = true;
						}
					}
					if(!positionOccupied) {
						position.set(i, PlayerPosition.GOALKEEPER);
					}
				}
				else {
					position.set(i, pa.position);
				}
				System.out.println("PlayerTable.setPlayer -> name: " + pa.playerName + " ; team: " + pa.currentTeam + " ; position: " + position.elementAt(i).toString());
				found = true;
				break;
			}
		}
		if (!found) {
			addPlayer(pa);
		}
	}

	/**
	 * Sterge un jucator.
	 */
	synchronized public void removePlayer(UUID ID) {
		int i;
		for(i=0; i < playerID.size(); i++) {
			if(playerID.elementAt(i).equals(ID)) {
				playerID.removeElementAt(i);
				playerName.removeElementAt(i);
				isReady.removeElementAt(i);
				currentTeam.removeElementAt(i);
				position.removeElementAt(i);
				break;
			}
		}
	}

	synchronized public boolean getIsReady(int i) {
		return isReady.elementAt(i);
	}

	synchronized public int getCurrentTeam(int i) {
		return currentTeam.elementAt(i);
	}
}
