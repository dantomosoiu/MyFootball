package Transmitable.toClient;

import java.io.Serializable;

public class Ball implements Serializable {

	double x, y;
	double speedX, speedY; //intreg care se adauga la coordonatele mingiei pentru a afla urmatoarea pozitie a mingiei, doar in cazul in care nu se afla in posesia niciunui jucator
	Player owner;
	boolean isOwned;
	String state;
	double angle; // unghiul sub care se deplaseaza mingea, in radiani ( in intervalul [0, 2pi] ca in cercul trigonometric ), ajuta la stabilirea speedX si speedY
	Player lastOwner;

	Ball() {
		x = 705;
		y = 380;
		owner = null;
		isOwned = false;
		System.out.println("apelez constructor fara parametri");
	}

	Ball(int x, int y) {
		this.x = 715;
		this.y = 400;
		speedX = 0;
		speedY = 0;
		isOwned = false;
		System.out.println("apelez constructor de " + x + " si " + y);
	}

	public boolean isIsOwned() {
		return isOwned;
	}

	public void setIsOwned(boolean isOwned) {
		this.isOwned = isOwned;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
		if (owner != null) {
			this.setLastOwner(owner);
		}
	}

	public double getSpeedX() {
		return speedX;
	}

	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}

	public double getSpeedY() {
		return speedY;
	}

	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public Player getLastOwner() {
		return lastOwner;
	}

	public void setLastOwner(Player lastOwner) {
		this.lastOwner = lastOwner;
	}
}
