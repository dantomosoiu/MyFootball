package Transmitable.toServer;

import Transmitable.toClient.Player.PlayerPosition;
import java.io.Serializable;
import java.util.UUID;

public class PlayerAction implements Serializable {

    public String playerName;
    public boolean isReady;
    public int currentTeam;
	public PlayerPosition position;
    public UUID playerID;

    public PlayerAction() {}

    public PlayerAction(String name) {
        this.playerName = name;
		currentTeam = 1;
		isReady = false;
		position = PlayerPosition.NONE;
		playerID = new UUID(5,10);
        playerID = UUID.randomUUID();
    }

}
