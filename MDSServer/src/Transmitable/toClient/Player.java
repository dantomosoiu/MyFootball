package Transmitable.toClient;

import java.io.Serializable;
import java.util.UUID;

public class Player implements Serializable, Comparable<Player> {

	int x, y;
	int index;
	boolean isActive;
	String playerName;
	String direction;
	PlayerStates state;
	PlayerPosition position;
	boolean hasBall;
	UUID playerID;
	int currentTeam;
	int noRunSpeed;
	int speed; // adaugat la viteza jucatorilor in timul sprintului (initial e 0)
	int energy; //folosit la sprintul jucatorilor (scade in timpul sprintului, creste cand se "odihneste").
	int shootPower;
	String attackDirection;
	int canShoot;
	boolean requestingBall;
	boolean holdingBall;

	public enum PlayerStates implements Serializable {

		NORMAL, CORNER, OUT, GOALKICK, FAULT, KEEPER, SLIDING;
	}

	public enum PlayerPosition implements Serializable {
		// NONE e folosit numai pentru a ii indica clientului ca pozitia nu este disponibila

		NONE, GOALKEEPER, DEFENDER, MIDFIELDER, ATTACKER;
	}

	public Player(String playerName, int currentTeam, PlayerPosition position, UUID playerID, int index) {
		this.playerName = playerName;
		this.currentTeam = currentTeam;
		this.position = position;
		this.playerID = playerID;
		this.isActive = true;
		this.index = index;
		direction = "S";
		state = PlayerStates.NORMAL;
		hasBall = false;
		speed = 0;
		energy = 200;
		x = 200;
		y = 200;
		noRunSpeed = 1;
		this.shootPower = 0;
		canShoot = 25;
		if (currentTeam == 1) {
			attackDirection = "E";
		} else {
			attackDirection = "W";
		}
		requestingBall = false;
		holdingBall = false;
	}

	public int getCurrentTeam() {
		return currentTeam;
	}

	public void setCurrentTeam(int currentTeam) {
		this.currentTeam = currentTeam;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public boolean isHasBall() {
		return hasBall;
	}

	public void setHasBall(boolean hasBall) {
		this.hasBall = hasBall;
	}

	public UUID getPlayerID() {
		return playerID;
	}

	public void setPlayerID(UUID playerID) {
		this.playerID = playerID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public PlayerStates getState() {
		return state;
	}

	public void setState(PlayerStates state) {
		this.state = state;
	}

	public PlayerPosition getPosition() {
		return position;
	}

	public void setPosition(PlayerPosition position) {
		this.position = position;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean y) {
		this.isActive = y;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int y) {
		this.speed = y;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int y) {
		this.energy = y;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getNoRunSpeed() {
		return noRunSpeed;
	}

	public void increaseNoRunSpeed() {
		this.noRunSpeed = 1;
	}

	public void decreaseNoRunSpeed() {
		this.noRunSpeed = 0;
	}

	public String getAttackDirection() {
		return attackDirection;
	}

	public void setAttackDirection(String attackDirection) {
		this.attackDirection = attackDirection;
	}

	public int getShootPower() {
		return shootPower;
	}

	public void setShootPower(int shootPower) {
		this.shootPower = shootPower;
	}

	public int getCanShoot() {
		return canShoot;
	}

	public void setCanShoot(int canShoot) {
		this.canShoot = canShoot;
	}

	public boolean isRequestingBall() {
		return requestingBall;
	}

	public void setRequestingBall(boolean requestingBall) {
		this.requestingBall = requestingBall;
	}

	public boolean isInsidePenaltyArea() { //gandita doar pt portari
		if (getCurrentTeam() == 1 && x < 315 && y > 190 && y < 650) {
			return true;
		}
		if (getCurrentTeam() == 2 && x > 1110 && y > 190 && y < 650) {
			return true;
		}
		return false;
	}

	public boolean isHoldingBall() {
		return holdingBall;
	}

	public void setHoldingBall(boolean holdingBall) {
		this.holdingBall = holdingBall;
	}

	public int compareTo(Player o) {
		if (o.getY() < y) {
			return 1;
		} else if (o.getY() == y) {
			if (o.getPosition() == PlayerPosition.ATTACKER) {
				if (getPosition() == PlayerPosition.MIDFIELDER) {
					return 1;
				} else {
					if (getPosition() == PlayerPosition.DEFENDER) {
						return 1;
					} else {
						if (getPosition() == PlayerPosition.GOALKEEPER) {
							return 1;
						} else {
							return 0;
						}
					}
				}
			} else {
				if (o.getPosition() == PlayerPosition.MIDFIELDER) {
					if (getPosition() == PlayerPosition.ATTACKER) {
						return -1;
					} else {
						if (getPosition() == PlayerPosition.MIDFIELDER) {
							return 0;
						} else {
							return 1;
						}
					}
				} else {
					if (o.getPosition() == PlayerPosition.DEFENDER) {
						if (getPosition() == PlayerPosition.ATTACKER || getPosition() == PlayerPosition.MIDFIELDER) {
							return -1;
						} else {
							if (getPosition() == PlayerPosition.DEFENDER) {
								return 0;
							} else {
								return 1;
							}
						}
					} else {
						if (getPosition() != PlayerPosition.GOALKEEPER) {
							return -1;
						} else {
							return 0;
						}
					}
				}
			}
		} else {
			return -1;
		}
	}
}
