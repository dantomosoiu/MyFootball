package Transmitable.toClient;

import java.io.Serializable;
import java.util.Vector;

public class GameState implements Serializable {

	Vector<Player> players;
	Ball ball;
	public String nextState;
	long elapsedTime;
	String playState;
	int scorTeam1;
	int scorTeam2;

	public GameState() {
	}

	public GameState(PlayerTable pt) {
		int i;
		Player np;
		nextState = "";
		elapsedTime = 0;
		players = new Vector<Player>();
		for (i = 0; i < pt.playerName.size(); i++) {
			np = new Player(pt.playerName.elementAt(i), pt.currentTeam.elementAt(i), pt.position.elementAt(i), pt.playerID.elementAt(i), i);
			players.add(np);
		}

		ball = new Ball(0, 0);
		playState = "INIT";
		scorTeam1 = 0;
		scorTeam2 = 0;

	}

	public Ball getBall() {
		return ball;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}

	public Vector<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Vector<Player> players) {
		this.players = players;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getPlayState() {
		return playState;
	}

	public void setPlayState(String playState) {
		this.playState = playState;
	}

	public int getScorTeam1() {
		return scorTeam1;
	}

	public void setScorTeam1(int scorTeam1) {
		this.scorTeam1 = scorTeam1;
	}

	public int getScorTeam2() {
		return scorTeam2;
	}

	public void setScorTeam2(int scorTeam2) {
		this.scorTeam2 = scorTeam2;
	}

	public Player getRandomPlayer(int team) {
		int r;
		boolean found = false;
		while (!found) {
			r = (int) (getPlayers().size() * Math.random());
			for (int i = 0; i < getPlayers().size(); i++) {
				if (i == r && getPlayers().elementAt(i).getCurrentTeam() == team && getPlayers().elementAt(i).isActive) {
					found = true;
					return getPlayers().elementAt(i);
				}
			}
		}
		System.out.println("am returnat null in getRandomPlayer!");
		return null;
	}
}
