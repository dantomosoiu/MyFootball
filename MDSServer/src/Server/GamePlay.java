package Server;

import Transmitable.toClient.GameState;
import Transmitable.toClient.Player;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

/**
 * Aceasta clasa este instantiata in clasa Server
 * Reactualizeaza la fiecare 40 ms GameState in funtie de tastele apasate (KeyMap)
 * @see Server
 * @see GameState
 * @see KeyMap
 */
public class GamePlay extends Thread {
    
    int team1Att, team1Mid, team1Def, team1GK, team2Att, team2Mid, team2Def, team2GK;
    Vector<Player> team1;
    Vector<Player> team2;
    Date startTime;
    long matchLengthMs;
    int MOVINGPACE = 4; //nr de pixeli cu care se deplaseaza un jucator pe update
// <editor-fold defaultstate="collapsed" desc="limitele terenului">
// <editor-fold defaultstate="collapsed" desc="limitele exterioare">
    int TOTAL_UPPER_BOUND = 0;
    int TOTAL_LOWER_BOUND = 850;
    int TOTAL_LEFT_BOUND = 0;
    int TOTAL_RIGHT_BOUND = 1375;// </editor-fold>
    int PLAYABLE_UPPER_BOUND = 40;
    int PLAYABLE_LEFT_BOUND = 127;
    int PLAYABLE_RIGHT_BOUND = 1300;
    int PLAYABLE_LOWER_BOUND = 796;
    int GOAL_START_Y = (PLAYABLE_LOWER_BOUND - PLAYABLE_UPPER_BOUND) / 2 + PLAYABLE_UPPER_BOUND - 42;
    int GOAL_END_Y = (PLAYABLE_LOWER_BOUND - PLAYABLE_UPPER_BOUND) / 2 + PLAYABLE_UPPER_BOUND + 42;
//</editor-fold>
// <editor-fold defaultstate="collapsed" desc="player starting positions">
    int[] team1ActiveX = {715, 715, 600, 345, 675, 675, 485, 485, 485, 485, 155}; //
    int[] team1ActiveY = {400, 445, 418, 481, 305, 540, 722, 118, 479, 361, 418}; // echipa stanga pt stanga kick-off
    int[] team2ActiveX = {715, 715, 830, 1085, 755, 755, 945, 945, 945, 945, 1275};//
    int[] team2ActiveY = {400, 445, 418, 481, 305, 540, 722, 118, 479, 361, 418};// echipa dreapta pt dreapta kick-off
    int[] team1PassiveX = {600, 675, 675, 345, 485, 485, 485, 485, 345, 345, 155};//
    int[] team1PassiveY = {418, 305, 540, 418, 361, 478, 722, 118, 175, 596, 418};// echipa stanga pt dreapta kick-off
    int[] team2PassiveX = {830, 755, 755, 1085, 945, 945, 945, 945, 1085, 1085, 1275};//
    int[] team2PassiveY = {418, 305, 540, 418, 361, 478, 722, 118, 175, 596, 418};// echipa dreapta pt stanga kick-off
//</editor-fold>
    
    public GamePlay(int matchMin) {
        Server.gameState = new GameState(Server.playerTable);
        System.out.println("Sunt " + Server.gameState.getPlayers().size() + " jucatori.");
        Server.keyMap = new KeyMap(Server.gameState.getPlayers().size());
        Server.state = "PLAY";
        startTime = new Date();
        matchLengthMs = matchMin * 60 * 1000;
        team1 = new Vector<Player>();
        team2 = new Vector<Player>();
        for (int i = 0; i < Server.gameState.getPlayers().size(); i++) {
            if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 1) { //numar in echipa 1
                team1.add(Server.gameState.getPlayers().elementAt(i));
                if (Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.ATTACKER)) {
                    team1Att++;
                } else if (Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.MIDFIELDER)) {
                    team1Mid++;
                } else if (Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.DEFENDER)) {
                    team1Def++;
                } else {
                    team1GK++;
                }
            } else { //numar in echipa 2
                team2.add(Server.gameState.getPlayers().elementAt(i));
                if (Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.ATTACKER)) {
                    team2Att++;
                } else if (Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.MIDFIELDER)) {
                    team2Mid++;
                } else if (Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.DEFENDER)) {
                    team2Def++;
                } else {
                    team2GK++;
                }
            }
        }
        Collections.sort(team1);
        Collections.sort(team2);
        System.out.println("echipa 1: " + team1Att + team1Mid + team1Def + team1GK);
        
        for (int i = 1; i < team1.size(); i++) {
            System.out.println(team1.elementAt(i).getPlayerName());
        }
    }
    
    void setSprint(int index) {
        if (Server.keyMap.isWPressed.elementAt(index)) {
            
            //System.out.println("energia jucatorului " + index + " este:" + Server.gameState.getPlayers().elementAt(index).getEnergy());
            if (Server.gameState.getPlayers().elementAt(index).getEnergy() >= 1) {
                // if (Server.gameState.getPlayers().elementAt(index).getSpeed() == 0) {
                if (Server.gameState.getPlayers().elementAt(index).getEnergy() > 140) {
                    Server.gameState.getPlayers().elementAt(index).setSpeed(MOVINGPACE / 2 + 1);
                } else if (Server.gameState.getPlayers().elementAt(index).getEnergy() > 60) {
                    Server.gameState.getPlayers().elementAt(index).setSpeed(MOVINGPACE / 2);
                } else {
                    Server.gameState.getPlayers().elementAt(index).setSpeed(MOVINGPACE / 2 - 1);
                    //   }
                }
            } else {
                Server.gameState.getPlayers().elementAt(index).setSpeed(0);
            }
            
            if (Server.gameState.getPlayers().elementAt(index).getEnergy() >= 1) {
                Server.gameState.getPlayers().elementAt(index).setEnergy(Server.gameState.getPlayers().elementAt(index).getEnergy() - 2);
                //System.out.println("scad energia jucatorului " + index + " la " + Server.gameState.getPlayers().elementAt(index).getEnergy());
            }
        }
        
        if (!Server.keyMap.isWPressed.elementAt(index)) {
            if (Server.gameState.getPlayers().elementAt(index).getSpeed() != 0) {
                Server.gameState.getPlayers().elementAt(index).setSpeed(0);
            }
            if (Server.gameState.getPlayers().elementAt(index).getEnergy() < 200) {
                Server.gameState.getPlayers().elementAt(index).setEnergy(Server.gameState.getPlayers().elementAt(index).getEnergy() + 1);
                //System.out.println("cresc energia jucatorului " + index + " la " + Server.gameState.getPlayers().elementAt(index).getEnergy());
            }
        }
    }
    
    void movePlayer(int index) {
        
        
        int x = 0, y = 0;
        
        boolean isN = false, isS = false, isE = false, isW = false;
        String tempDirection = "";
        
        int slidingTacts = 0;
        
        if (Server.keyMap.isAPressed.elementAt(index) && !Server.gameState.getPlayers().elementAt(index).isHasBall() && Server.gameState.getPlayers().elementAt(index).getEnergy() > 100) {
            slidingTacts = 20;
            
            Server.gameState.getPlayers().elementAt(index).setEnergy(Server.gameState.getPlayers().elementAt(index).getEnergy() * 2 / 3);
            tempDirection = Server.gameState.getPlayers().elementAt(index).getDirection();
            
            if (Server.gameState.getPlayers().elementAt(index).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                
                if (tempDirection.contains("N")) {
                    isN = true;
                    y = -20;
                }
                if (tempDirection.contains("S")) {
                    isS = true;
                    y = 20;
                }
                if (tempDirection.contains("E")) {
                    isE = true;
                    x = 20;
                }
                if (tempDirection.contains("W")) {
                    isW = true;
                    x = -20;
                }
                
                
            } else {
                
                if (tempDirection.contains("N")) {
                    isN = true;
                    y = -10;
                }
                if (tempDirection.contains("S")) {
                    isS = true;
                    y = 10;
                }
                if (tempDirection.contains("E")) {
                    isE = true;
                    x = 10;
                }
                if (tempDirection.contains("W")) {
                    isW = true;
                    x = -10;
                }
            }
        }
        
        if (slidingTacts > 0) {
            slidingTacts--;
            
        } else {
            
            if (Server.keyMap.isUpPressed.elementAt(index)) {
                if (Server.gameState.getPlayers().elementAt(index).getY() > TOTAL_UPPER_BOUND) {
                    y -= MOVINGPACE + Server.gameState.getPlayers().elementAt(index).getSpeed() + Server.gameState.getPlayers().elementAt(index).getNoRunSpeed();
                }
                isN = true;
            }
            
            if (Server.keyMap.isDownPressed.elementAt(index)) {
                if (Server.gameState.getPlayers().elementAt(index).getY() < TOTAL_LOWER_BOUND) {
                    y += MOVINGPACE + Server.gameState.getPlayers().elementAt(index).getSpeed() + Server.gameState.getPlayers().elementAt(index).getNoRunSpeed();
                }
                isS = true;
            }
            if (Server.keyMap.isLeftPressed.elementAt(index)) {
                if (Server.gameState.getPlayers().elementAt(index).getX() > TOTAL_LEFT_BOUND) {
                    x -= MOVINGPACE + Server.gameState.getPlayers().elementAt(index).getSpeed() + Server.gameState.getPlayers().elementAt(index).getNoRunSpeed();
                }
                isW = true;
            }
            if (Server.keyMap.isRightPressed.elementAt(index)) {
                if (Server.gameState.getPlayers().elementAt(index).getX() < TOTAL_RIGHT_BOUND) {
                    x += MOVINGPACE + Server.gameState.getPlayers().elementAt(index).getSpeed() + Server.gameState.getPlayers().elementAt(index).getNoRunSpeed();
                }
                isE = true;
            }
            
            
            if (isN && !isS) {
                tempDirection += "N";
            }
            if (isS && !isN) {
                tempDirection += "S";
            }
            if (isW && !isE) {
                tempDirection += "W";
            }
            if (isE && !isW) {
                tempDirection += "E";
            }
            
        }
        if (isN && isW || isN && isE || isS && isE || isS && isW) {
            x *= 1 / Math.sqrt(2);
            y *= 1 / Math.sqrt(2);
        }
        
        if ((isN || isS || isW || isE) && !tempDirection.equals("")) {
            Server.gameState.getPlayers().elementAt(index).setDirection(tempDirection);
        }
        if (Server.gameState.getPlayers().elementAt(index).getState() == Player.PlayerStates.NORMAL && ( !Server.gameState.getPlayers().elementAt(index).isHoldingBall() || Server.gameState.getPlayers().elementAt(index).isInsidePenaltyArea() )) {
            boolean zoneUnnocupied = true;
            for (int i = 0; i < Server.gameState.getPlayers().size(); i++) {
                if (distance((Server.gameState.getPlayers().elementAt(i)), Server.gameState.getPlayers().elementAt(index).getX() + x, Server.gameState.getPlayers().elementAt(index).getY() + y) < 8 && Server.gameState.getPlayers().elementAt(index) != Server.gameState.getPlayers().elementAt(i)) {
                    zoneUnnocupied = false;
                }
                
            }
            if (zoneUnnocupied) {
                
                
                    
                    
                    Server.gameState.getPlayers().elementAt(index).setX(Server.gameState.getPlayers().elementAt(index).getX() + x);
                    Server.gameState.getPlayers().elementAt(index).setY(Server.gameState.getPlayers().elementAt(index).getY() + y);
                
                    
                    if (Server.gameState.getPlayers().elementAt(index).isHoldingBall() && !Server.gameState.getPlayers().elementAt(index).isInsidePenaltyArea() ) {
                        Server.gameState.getPlayers().elementAt(index).setX(Server.gameState.getPlayers().elementAt(index).getX() - x);
                    Server.gameState.getPlayers().elementAt(index).setY(Server.gameState.getPlayers().elementAt(index).getY() - y);
                    }
            }
        }
    }
    
    void updateTime() {
        Server.gameState.setElapsedTime((new Date()).getTime() - startTime.getTime());	//actualizeaza timpul scurs de la inceputul jocului
    }
    
    void testShoot() {
        Player ballOwner;
        Random r = new Random();
        double normal, deltaX, deltaY, tangent, angle, sX, sY, dist, dispersion, energy, difY, shootPower;
        int goalX, goalY;
        if (Server.gameState.getBall().isIsOwned() && Server.gameState.getBall().getOwner().getCanShoot() >= 15) { //daca mingea se afla in posesia vreunui jucator
            ballOwner = Server.gameState.getBall().getOwner();
            if (ballOwner.getState() != Player.PlayerStates.OUT) {
                if (Server.keyMap.isDPressed.elementAt(ballOwner.getIndex()) && ballOwner.getShootPower() < 25) { //daca cel care are mingea are apasat D si shootPower nu e la maxim
                    ballOwner.setShootPower(ballOwner.getShootPower() + 1);
                } else { //cel care are mingea nu are apasat D sau shootPowe a ajuns la maxim
                    if (ballOwner.getShootPower() != 0) { //a ridicat de pe D sau shootPower a ajuns la maxim
                        
                        
                        if (ballOwner.getAttackDirection().equals("W")) { // stabilesc punctul spre care se va duce sutul
                            goalX = PLAYABLE_LEFT_BOUND;
                        } else {
                            goalX = PLAYABLE_RIGHT_BOUND;
                        }
                        goalY = (int) ((PLAYABLE_LOWER_BOUND - PLAYABLE_UPPER_BOUND) / 2 + PLAYABLE_UPPER_BOUND);
                        System.out.println("goaly : " + goalY);
                        dist = Math.sqrt((goalX - ballOwner.getX()) * (goalX - ballOwner.getX()) + (goalY - ballOwner.getY()) * (goalY - ballOwner.getY()));
                        difY = Math.abs(ballOwner.getY() - goalY);
                        
                        shootPower = ballOwner.getShootPower();
                        energy = 200 - ballOwner.getEnergy();
                        
                        //le aduc in intervalul 0 - 1000
                        shootPower = shootPower *= 40;
                        difY = difY * 10 / 4;
                        energy *= 5;
                        dist = Math.abs(dist);
                        dispersion = (10 * energy + 50 * dist + 15 * shootPower + 25 * difY) / 100; //e in intervalul 0 - 1000
                        dispersion = dispersion * 0.15; // o aduc in intervalul 0 - 150
                        
                        normal = r.nextGaussian() * dispersion;
                        
                        System.out.println("shootPower: " + shootPower);
                        System.out.println("difY: " + difY);
                        System.out.println("energy: " + energy);
                        System.out.println("dist; " + dist);
                        System.out.println("disersion: " + dispersion);
                        System.out.println("normal: " + normal);
                        
                        
                        goalY += normal;
                        deltaX = ballOwner.getX() - goalX;
                        deltaY = ballOwner.getY() - goalY;
                        
                        if (deltaX == 0) { //evita impartirea la 0
                            deltaX = 1;
                        }
                        
                        tangent = deltaY / deltaX; //tangenta unghiului sub care se va deplasa mingea ( conform cercului trigonometric )
                        angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                        
                        if (deltaX >= 0 && deltaY >= 0) { //cadranul 2
                            angle = Math.PI - angle;
                        }
                        if (deltaX <= 0 && deltaY >= 0) { //cadranul 1
                            angle = -angle;
                        }
                        if (deltaX <= 0 && deltaY <= 0) { //cadranul 4
                            angle = 2 * Math.PI - angle;
                        }
                        if (deltaX >= 0 && deltaY <= 0) { //cadranul 3
                            angle = Math.PI - angle;
                        }
                        
                        sX = Math.cos(angle) * (ballOwner.getShootPower() + 22);
                        sY = -Math.sin(angle) * (ballOwner.getShootPower() + 22);
                        
                        Server.gameState.getBall().setAngle(angle);
                        Server.gameState.getBall().setIsOwned(false);
                        Server.gameState.getBall().setLastOwner(Server.gameState.getBall().getOwner());
                        Server.gameState.getBall().getOwner().setHoldingBall(false);
                        Server.gameState.getBall().getOwner().setHasBall(false);
                        Server.gameState.getBall().setOwner(null);
                        
                        
                        Server.gameState.getBall().setSpeedX(sX);
                        Server.gameState.getBall().setSpeedY(sY);
                        
                        ballOwner.setShootPower(0);
                        ballOwner.increaseNoRunSpeed();
                        ballOwner.setState(Player.PlayerStates.NORMAL);
                    }
                }
            }
        } else { // niciun jucator nu are mingea
            if (Server.gameState.getBall().getLastOwner() != null) {
                Server.gameState.getBall().getLastOwner().setState(Player.PlayerStates.NORMAL);
                Server.gameState.getBall().getLastOwner().setShootPower(0);
            }
        }
    }
    
    void checkRequest(int index) {
        if (Server.keyMap.isSPressed.elementAt(index) && !Server.gameState.getPlayers().elementAt(index).isHasBall()) {
            Server.gameState.getPlayers().elementAt(index).setRequestingBall(true);
        } else {
            Server.gameState.getPlayers().elementAt(index).setRequestingBall(false);
        }
        
        
    }
    
    void updatePlayer(int index) {
        
        if (Server.gameState.getPlayers().elementAt(index).getCanShoot() < 15) {
            Server.gameState.getPlayers().elementAt(index).setCanShoot(Server.gameState.getPlayers().elementAt(index).getCanShoot() + 1);
        }
        
        if (Server.gameState.getPlayers().elementAt(index).getShootPower() > 0 && !Server.gameState.getPlayers().elementAt(index).isHasBall()) {
            Server.gameState.getPlayers().elementAt(index).setShootPower(0);
        }
        
        setSprint(index);
        movePlayer(index);
        checkRequest(index); //verific daca jucatorul cere mingea (are S apasat)
        
        
    }
    
    double distanceToBall(Player p) { // calculeaza distanta de la minge la jucatorul dat ca parametru
        int xP = p.getX();
        int yP = p.getY();
        double xB = Server.gameState.getBall().getX();
        double yB = Server.gameState.getBall().getY();
        return Math.sqrt((xP - xB) * (xP - xB) + (yP - yB) * (yP - yB));
    }
    
    double distance(Player p1, Player p2) {
        double xP1 = p1.getX();
        double yP1 = p1.getY();
        double xP2 = p2.getX();
        double yP2 = p2.getY();
        return Math.sqrt((xP1 - xP2) * (xP1 - xP2) + (yP1 - yP2) * (yP1 - yP2));
    }
    
    double distance(Player p1, int xP2, int yP2) {
        double xP1 = p1.getX();
        double yP1 = p1.getY();
        return Math.sqrt((xP1 - xP2) * (xP1 - xP2) + (yP1 - yP2) * (yP1 - yP2));
    }
    
    void clearArea(Player p) {
        for (int i = 0; i < Server.gameState.getPlayers().size(); i++) {
            if (distance(p, Server.gameState.getPlayers().elementAt(i)) < 150 && Server.gameState.getPlayers().elementAt(i) != p) {
                if (p.getCurrentTeam() == 1) {
                    if (Server.gameState.getPlayers().elementAt(i).getX() + 150 > PLAYABLE_RIGHT_BOUND) {
                        Server.gameState.getPlayers().elementAt(i).setX(Server.gameState.getPlayers().elementAt(i).getX() - 150);
                    } else {
                        Server.gameState.getPlayers().elementAt(i).setX(Server.gameState.getPlayers().elementAt(i).getX() + 150);
                    }
                } else {
                    if (Server.gameState.getPlayers().elementAt(i).getX() - 150 < PLAYABLE_LEFT_BOUND) {
                        Server.gameState.getPlayers().elementAt(i).setX(Server.gameState.getPlayers().elementAt(i).getX() + 150);
                    } else {
                        Server.gameState.getPlayers().elementAt(i).setX(Server.gameState.getPlayers().elementAt(i).getX() - 150);
                    }
                }
                
            }
            
        }
    }
    
    void updateBall() {
        int i;
        String direction, directionAux;
        double sX = 0, sY = 0, speed, ssx, ssy, ty;
        boolean isN = false, isS = false, isE = false, isW = false;
        Player ballOwner, currentPlayer, passReceiverPlayer;
        double deltaX, deltaY, tangent, angle = 0, minDistance = 3000;
        
        double currentSpeedX = Server.gameState.getBall().getSpeedX();
        double currentSpeedY = Server.gameState.getBall().getSpeedY();
        
        
        
        
        
        
        if (!Server.gameState.getBall().isIsOwned()) { // nu se afla in posesia niciunui jucator
            Server.gameState.getBall().setX(currentSpeedX + Server.gameState.getBall().getX());
            Server.gameState.getBall().setY(currentSpeedY + Server.gameState.getBall().getY());
            
            if (currentSpeedX <= -1 || currentSpeedX >= 1) { //daca merge prea incet mingea
                Server.gameState.getBall().setSpeedX(currentSpeedX - Math.cos(Server.gameState.getBall().getAngle()));
            } else {
                Server.gameState.getBall().setSpeedX(0);
            }
            
            
            if (currentSpeedY <= -1 || currentSpeedY >= 1) {
                Server.gameState.getBall().setSpeedY(currentSpeedY + Math.sin(Server.gameState.getBall().getAngle()));
            } else {
                Server.gameState.getBall().setSpeedY(0);
            }
            
            
//// <editor-fold defaultstate="collapsed" desc="bouncing edges">
//            if (Server.gameState.getBall().getY() <= UPPERBOUND) {
//                Server.gameState.getBall().setSpeedY(-currentSpeedY);
//                Server.gameState.getBall().setAngle(Server.gameState.getBall().getAngle() + Math.PI);
//            }
//            if (Server.gameState.getBall().getY() >= LOWERBOUND) {
//                Server.gameState.getBall().setSpeedY(-currentSpeedY);
//                Server.gameState.getBall().setAngle(Server.gameState.getBall().getAngle() - Math.PI);
//            }
//            if (Server.gameState.getBall().getX() <= LEFTBOUND) {
//                Server.gameState.getBall().setSpeedX(-currentSpeedX);
//                if (Server.gameState.getBall().getAngle() <= Math.PI) { // daca e in primu cadran
//                    Server.gameState.getBall().setAngle(Server.gameState.getBall().getAngle() + Math.PI);
//                } else {
//                    Server.gameState.getBall().setAngle(Server.gameState.getBall().getAngle() - Math.PI);
//                }
//            }
//            if (Server.gameState.getBall().getX() >= RIGHTBOUND) {
//                Server.gameState.getBall().setSpeedX(-currentSpeedX);
//                if (Server.gameState.getBall().getAngle() <= Math.PI / 2) { // daca e in primu cadran
//                    Server.gameState.getBall().setAngle(Server.gameState.getBall().getAngle() + Math.PI);
//                } else {
//                    Server.gameState.getBall().setAngle(Server.gameState.getBall().getAngle() - Math.PI);
//                }
//            }// </editor-fold>
            
            for (i = 0; i < Server.gameState.getPlayers().size(); i++) { // caut sa vad daca e un jucator suficient de aproape ca sa intre in posesia ei
                if ((distanceToBall(Server.gameState.getPlayers().elementAt(i)) < 15 || (distanceToBall(Server.gameState.getPlayers().elementAt(i)) < 25 && Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER))) && Server.gameState.getPlayers().elementAt(i).getIsActive()) {
                    Server.gameState.getBall().setIsOwned(true);
                    Server.gameState.getPlayers().elementAt(i).decreaseNoRunSpeed();
                    Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(i));
                    Server.gameState.getBall().getOwner().setHasBall(true);
                }
            }
        } else { // se afla in posesia unui jucator
            ballOwner = Server.gameState.getBall().getOwner();
            if (Server.keyMap.isSPressed.elementAt(ballOwner.getIndex())) { // daca jucatorul care are mingea a apasat S
                passReceiverPlayer = null;
                for (i = 0; i < Server.gameState.getPlayers().size(); i++) { // caut un jucator in aria vizuala a lui ballOwner
                    currentPlayer = Server.gameState.getPlayers().elementAt(i);
                    if (currentPlayer == ballOwner) {
                        continue;
                    }
                    if (ballOwner.getCurrentTeam() != currentPlayer.getCurrentTeam()) {
                        continue;
                    }
                    if (minDistance < distance(ballOwner, currentPlayer)) { // daca e mai aproape decat cel mai apropiat gasit anterior
                        continue;
                    } else {
                        minDistance = distance(ballOwner, currentPlayer);
                    }
                    
                    deltaX = ballOwner.getX() - currentPlayer.getX();
                    deltaY = ballOwner.getY() - currentPlayer.getY();
                    if (deltaX == 0) { //evita impartirea la 0
                        deltaX = 1;
                    }
                    tangent = deltaY / deltaX; //tangenta unghiului dintre cei 2 jucatori
                    
                    //incerc sa vad daca currentPlayer e in aria vizuala a lui ballOwner
                    
                    if (tangent >= -Math.tan(Math.PI / 6) && tangent <= Math.tan(Math.PI / 6)) {
                        if (deltaX >= 0 && ballOwner.getDirection().equals("W")) {
                            passReceiverPlayer = currentPlayer;
                            angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]									}
                        }
                        if (deltaX <= 0 && ballOwner.getDirection().equals("E")) {
                            passReceiverPlayer = currentPlayer;
                            angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                        }
                    }
                    if (1 / tangent >= -Math.tan(Math.PI / 6) && 1 / tangent <= Math.tan(Math.PI / 6)) { // ctg
                        if (deltaY >= 0 && ballOwner.getDirection().equals("N")) {
                            passReceiverPlayer = currentPlayer;
                            angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            
                        }
                        if (deltaY <= 0 && ballOwner.getDirection().equals("S")) {
                            passReceiverPlayer = currentPlayer;
                            angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                        }
                    }
                    
                    if ((tangent >= Math.tan(Math.PI / 12) && tangent <= Math.tan(5 * Math.PI / 12)) || (tangent >= -Math.tan(5 * Math.PI / 12) && tangent <= -Math.tan(-Math.PI / 12))) {
                        if (deltaX <= 0 && deltaY >= 0) {
                            if (ballOwner.getDirection().equals("NE")) {
                                passReceiverPlayer = currentPlayer;
                                angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            }
                        }
                        if (deltaX >= 0 && deltaY <= 0) {
                            if (ballOwner.getDirection().equals("SW")) {
                                passReceiverPlayer = currentPlayer;
                                angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            }
                        }
                        if (deltaX >= 0 && deltaY >= 0) {
                            if (ballOwner.getDirection().equals("NW")) {
                                passReceiverPlayer = currentPlayer;
                                angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            }
                        }
                        if (deltaX <= 0 && deltaY <= 0) {
                            if (ballOwner.getDirection().equals("SE")) {
                                passReceiverPlayer = currentPlayer;
                                angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            }
                        }
                    }
                }
                speed = 20;
                if (passReceiverPlayer != null) { // daca am gasit un jucator in aria vizuala
                    
                    deltaX = ballOwner.getX() - passReceiverPlayer.getX();
                    deltaY = ballOwner.getY() - passReceiverPlayer.getY();
                    
                    if (deltaX == 0) { //evita impartirea la 0
                        deltaX = 1;
                    }
                    
                    tangent = deltaY / deltaX; //tangenta unghiului sub care se va deplasa mingea ( conform cercului trigonometric )
                    angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                    
                    //stabilesc unghiul
                    
                    if (deltaX >= 0 && deltaY >= 0) { //cadranul 2
                        angle = Math.PI - angle;
                    }
                    if (deltaX <= 0 && deltaY >= 0) { //cadranul 1
                        angle = -angle;
                    }
                    if (deltaX <= 0 && deltaY <= 0) { //cadranul 4
                        angle = 2 * Math.PI - angle;
                    }
                    if (deltaX >= 0 && deltaY <= 0) { //cadranul 3
                        angle = Math.PI - angle;
                    }
                    
                    speed = Math.min(Math.sqrt(8 * minDistance + 800) / 2, 33); //viteza in funtie de distanta pana la jucator
                    
                    sX = Math.cos(angle) * speed;
                    sY = -Math.sin(angle) * speed;
                    
                    
                    
                } else { //nu are jucator in aria vizuala, va da pasa inainte
                    
                    direction = Server.gameState.getBall().getOwner().getDirection();
                    
                    if (direction.indexOf("N") != -1) {
                        sY = -speed;
                        isN = true;
                        angle = Math.PI / 2;
                    }
                    if (direction.indexOf("S") != -1) {
                        sY = speed;
                        isS = true;
                        angle = 3 * Math.PI / 2;
                    }
                    if (direction.indexOf("E") != -1) {
                        sX = speed;
                        isE = true;
                        angle = 0;
                    }
                    if (direction.indexOf("W") != -1) {
                        sX = -speed;
                        isW = true;
                        angle = Math.PI;
                    }
                    
                    if (isN && isW || isN && isE || isS && isE || isS && isW) { //mers pe diagonala
                        sX *= 1 / Math.sqrt(2);
                        sY *= 1 / Math.sqrt(2);
                    }
                    
                    if (isN && isW) {
                        angle = Math.PI * 3 / 4;
                    }
                    if (isN && isE) {
                        angle = Math.PI * 1 / 4;
                    }
                    if (isS && isE) {
                        angle = Math.PI * 7 / 4;
                    }
                    if (isS && isW) {
                        angle = Math.PI * 5 / 4;
                    }
                }
                
                Server.gameState.getBall().setAngle(angle);
                Server.gameState.getBall().getOwner().setHasBall(false);
                Server.gameState.getBall().getOwner().setState(Player.PlayerStates.NORMAL);
                Server.gameState.getBall().getOwner().increaseNoRunSpeed();
                Server.gameState.getBall().getOwner().setHoldingBall(false);
                Server.gameState.getBall().setIsOwned(false);
                
                
                Server.gameState.getBall().setSpeedX(sX);
                Server.gameState.getBall().setSpeedY(sY);
                
                
            } else if (Server.keyMap.isEPressed.elementAt(ballOwner.getIndex())) { // daca jucatorul care are mingea a apasat E**************************
                passReceiverPlayer = null;
                for (i = 0; i < Server.gameState.getPlayers().size(); i++) { // caut un jucator in aria vizuala a lui ballOwner
                    currentPlayer = Server.gameState.getPlayers().elementAt(i);
                    if (currentPlayer == ballOwner) {
                        continue;
                    }
                    if (ballOwner.getCurrentTeam() != currentPlayer.getCurrentTeam()) {
                        continue;
                    }
                    if (minDistance < distance(ballOwner, currentPlayer)) { // daca e mai aproape decat cel mai apropiat gasit anterior
                        continue;
                    } else {
                        minDistance = distance(ballOwner, currentPlayer);
                    }
                    
                    deltaX = ballOwner.getX() - currentPlayer.getX();
                    deltaY = ballOwner.getY() - currentPlayer.getY();
                    
//                                        directionAux = currentPlayer.getDirection();
//
//					System.out.println("**************" + directionAux);
//
//					if (directionAux.equalsIgnoreCase("N")) {
//						deltaY -= 100;
//					}
//					if (directionAux.equalsIgnoreCase("S")) {
//						deltaY += 100;
//					}
//					if (directionAux.equalsIgnoreCase("E")) {
//						deltaX += 100;
//					}
//					if (directionAux.equalsIgnoreCase("W")) {
//						deltaX -= 100;
//					}
//					if (directionAux.equalsIgnoreCase("NE")) {
//						deltaY -= 100 * Math.sqrt(2) / 2;
//						deltaX += 100 * Math.sqrt(2) / 2;
//					}
//					if (directionAux.equalsIgnoreCase("SE")) {
//						deltaY += 100 * Math.sqrt(2) / 2;
//						deltaX += 100 * Math.sqrt(2) / 2;
//					}
//					if (directionAux.equalsIgnoreCase("SW")) {
//						deltaY += 100 * Math.sqrt(2) / 2;
//						deltaX -= 100 * Math.sqrt(2) / 2;
//					}
//					if (directionAux.equalsIgnoreCase("NW")) {
//						deltaY -= 100 * Math.sqrt(2) / 2;
//						deltaX -= 100 * Math.sqrt(2) / 2;
//					}
                    
                    
                    
                    
                    
                    if (deltaX == 0) { //evita impartirea la 0
                        deltaX = 1;
                    }
                    tangent = deltaY / deltaX; //tangenta unghiului dintre cei 2 jucatori
                    
                    //incerc sa vad daca currentPlayer e in aria vizuala a lui ballOwner
                    
                    if (tangent >= -Math.tan(Math.PI / 6) && tangent <= Math.tan(Math.PI / 6)) {
                        if (deltaX >= 0 && ballOwner.getDirection().equals("W")) {
                            passReceiverPlayer = currentPlayer;
                            angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]									}
                        }
                        if (deltaX <= 0 && ballOwner.getDirection().equals("E")) {
                            passReceiverPlayer = currentPlayer;
                            angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                        }
                    }
                    if (1 / tangent >= -Math.tan(Math.PI / 6) && 1 / tangent <= Math.tan(Math.PI / 6)) { // ctg
                        if (deltaY >= 0 && ballOwner.getDirection().equals("N")) {
                            passReceiverPlayer = currentPlayer;
                            angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            
                        }
                        if (deltaY <= 0 && ballOwner.getDirection().equals("S")) {
                            passReceiverPlayer = currentPlayer;
                            angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                        }
                    }
                    
                    if ((tangent >= Math.tan(Math.PI / 12) && tangent <= Math.tan(5 * Math.PI / 12)) || (tangent >= -Math.tan(5 * Math.PI / 12) && tangent <= -Math.tan(-Math.PI / 12))) {
                        if (deltaX <= 0 && deltaY >= 0) {
                            if (ballOwner.getDirection().equals("NE")) {
                                passReceiverPlayer = currentPlayer;
                                angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            }
                        }
                        if (deltaX >= 0 && deltaY <= 0) {
                            if (ballOwner.getDirection().equals("SW")) {
                                passReceiverPlayer = currentPlayer;
                                angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            }
                        }
                        if (deltaX >= 0 && deltaY >= 0) {
                            if (ballOwner.getDirection().equals("NW")) {
                                passReceiverPlayer = currentPlayer;
                                angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            }
                        }
                        if (deltaX <= 0 && deltaY <= 0) {
                            if (ballOwner.getDirection().equals("SE")) {
                                passReceiverPlayer = currentPlayer;
                                angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                            }
                        }
                    }
                }
                speed = 20;
                if (passReceiverPlayer != null) { // daca am gasit un jucator in aria vizuala
                    
                    deltaX = ballOwner.getX() - passReceiverPlayer.getX();
                    deltaY = ballOwner.getY() - passReceiverPlayer.getY();
                    
                    directionAux = passReceiverPlayer.getDirection();
                    
                    System.out.println("**************" + directionAux);
                    
                    if (directionAux.equalsIgnoreCase("N")) {
                        deltaY += 100;
                    }
                    if (directionAux.equalsIgnoreCase("S")) {
                        deltaY -= 100;
                    }
                    if (directionAux.equalsIgnoreCase("E")) {
                        deltaX -= 100;
                    }
                    if (directionAux.equalsIgnoreCase("W")) {
                        deltaX += 100;
                    }
                    if (directionAux.equalsIgnoreCase("NE")) {
                        deltaY += 100 * Math.sqrt(2) / 2;
                        deltaX -= 100 * Math.sqrt(2) / 2;
                    }
                    if (directionAux.equalsIgnoreCase("SE")) {
                        deltaY -= 100 * Math.sqrt(2) / 2;
                        deltaX -= 100 * Math.sqrt(2) / 2;
                    }
                    if (directionAux.equalsIgnoreCase("SW")) {
                        deltaY -= 100 * Math.sqrt(2) / 2;
                        deltaX += 100 * Math.sqrt(2) / 2;
                    }
                    if (directionAux.equalsIgnoreCase("NW")) {
                        deltaY += 100 * Math.sqrt(2) / 2;
                        deltaX += 100 * Math.sqrt(2) / 2;
                    }
                    
                    if (deltaX == 0) { //evita impartirea la 0
                        deltaX = 1;
                    }
                    
                    tangent = deltaY / deltaX; //tangenta unghiului sub care se va deplasa mingea ( conform cercului trigonometric )
                    angle = Math.atan(tangent); //in intervalul [-pi/2, pi/2]
                    
                    //stabilesc unghiul
                    
                    if (deltaX >= 0 && deltaY >= 0) { //cadranul 2
                        angle = Math.PI - angle;
                    }
                    if (deltaX <= 0 && deltaY >= 0) { //cadranul 1
                        angle = -angle;
                    }
                    if (deltaX <= 0 && deltaY <= 0) { //cadranul 4
                        angle = 2 * Math.PI - angle;
                    }
                    if (deltaX >= 0 && deltaY <= 0) { //cadranul 3
                        angle = Math.PI - angle;
                    }
                    
                    speed = Math.min(Math.sqrt(8 * minDistance + 800) / 2, 33); //viteza in funtie de distanta pana la jucator
                    
                    sX = Math.cos(angle) * speed;
                    sY = -Math.sin(angle) * speed;
                    
                    
                    
                } else { //nu are jucator in aria vizuala, va da pasa inainte
                    
                    direction = Server.gameState.getBall().getOwner().getDirection();
                    
                    if (direction.indexOf("N") != -1) {
                        sY = -speed;
                        isN = true;
                        angle = Math.PI / 2;
                    }
                    if (direction.indexOf("S") != -1) {
                        sY = speed;
                        isS = true;
                        angle = 3 * Math.PI / 2;
                    }
                    if (direction.indexOf("E") != -1) {
                        sX = speed;
                        isE = true;
                        angle = 0;
                    }
                    if (direction.indexOf("W") != -1) {
                        sX = -speed;
                        isW = true;
                        angle = Math.PI;
                    }
                    
                    if (isN && isW || isN && isE || isS && isE || isS && isW) { //mers pe diagonala
                        sX *= 1 / Math.sqrt(2);
                        sY *= 1 / Math.sqrt(2);
                    }
                    
                    if (isN && isW) {
                        angle = Math.PI * 3 / 4;
                    }
                    if (isN && isE) {
                        angle = Math.PI * 1 / 4;
                    }
                    if (isS && isE) {
                        angle = Math.PI * 7 / 4;
                    }
                    if (isS && isW) {
                        angle = Math.PI * 5 / 4;
                    }
                }
                
                Server.gameState.getBall().setAngle(angle);
                Server.gameState.getBall().getOwner().setHasBall(false);
                Server.gameState.getBall().getOwner().setState(Player.PlayerStates.NORMAL);
                Server.gameState.getBall().getOwner().increaseNoRunSpeed();
                Server.gameState.getBall().setIsOwned(false);
                Server.gameState.getBall().getOwner().setHoldingBall(false);
                
                Server.gameState.getBall().setSpeedX(sX);
                Server.gameState.getBall().setSpeedY(sY);
                
                
            } else { // jucatorul nu are apasat S sau E ***********************************************************************************************
                Server.gameState.getBall().setX(Server.gameState.getBall().getOwner().getX());
                Server.gameState.getBall().setY(Server.gameState.getBall().getOwner().getY());
                String relativePosition;
                relativePosition = Server.gameState.getBall().getOwner().getDirection();
                if (relativePosition.contains("N")) {
                    Server.gameState.getBall().setY(Server.gameState.getBall().getY() - 5);
                }
                if (relativePosition.contains("S")) {
                    Server.gameState.getBall().setY(Server.gameState.getBall().getY() + 3);
                }
                if (relativePosition.contains("E")) {
                    Server.gameState.getBall().setX(Server.gameState.getBall().getX() + 4);
                }
                if (relativePosition.contains("W")) {
                    Server.gameState.getBall().setX(Server.gameState.getBall().getX() - 4);
                }
                if (Server.gameState.getBall().getOwner().isHoldingBall()) {
                    Server.gameState.getBall().setY(Server.gameState.getBall().getY() - 10);
                }
            }
        }
        
        
        if (Server.gameState.getBall().getY() <= PLAYABLE_UPPER_BOUND) {
            Server.gameState.getBall().setSpeedX(0);
            Server.gameState.getBall().setSpeedY(0);
            Server.gameState.setPlayState("OUT");
        }
        if (Server.gameState.getBall().getY() >= PLAYABLE_LOWER_BOUND) {
            Server.gameState.getBall().setSpeedX(0);
            Server.gameState.getBall().setSpeedY(0);
            Server.gameState.setPlayState("OUT");
            
        }
        if (Server.gameState.getBall().getX() <= PLAYABLE_LEFT_BOUND) {
            ssx = Server.gameState.getBall().getSpeedX();
            ssy = Server.gameState.getBall().getSpeedY();
            if (ssx == 0) {
                ssx = 1;
            }
            Server.gameState.getBall().setSpeedX(0);
            Server.gameState.getBall().setSpeedY(0);
            
//			if (Server.gameState.getBall().getY() > GOAL_START_Y && Server.gameState.getBall().getY() < GOAL_END_Y) {
            ty = Server.gameState.getBall().getY() - (PLAYABLE_LEFT_BOUND - Server.gameState.getBall().getX()) * ssy / ssx;
            if (ty > GOAL_START_Y && ty < GOAL_END_Y) {
                
                Server.gameState.setScorTeam1(Server.gameState.getScorTeam1() + 1);
                Server.gameState.setPlayState("GOAL");
            } else {
                if (Server.gameState.getBall().getLastOwner().getCurrentTeam() == 1) {
                    Server.gameState.setPlayState("CORNER");
                } else {
                    Server.gameState.setPlayState("GOALKICK");
                }
            }
        }
        if (Server.gameState.getBall().getX() >= PLAYABLE_RIGHT_BOUND) {
            ssx = Server.gameState.getBall().getSpeedX();
            ssy = Server.gameState.getBall().getSpeedY();
            Server.gameState.getBall().setSpeedX(0);
            Server.gameState.getBall().setSpeedY(0);
//			if (Server.gameState.getBall().getY() > GOAL_START_Y && Server.gameState.getBall().getY() < GOAL_END_Y) {
            if (ssx == 0) {
                ssx = 1;
            }
            ty = Server.gameState.getBall().getY() - (Server.gameState.getBall().getX() - PLAYABLE_RIGHT_BOUND) * ssy / ssx;
            
            if (ty > GOAL_START_Y && ty < GOAL_END_Y) {
                
                Server.gameState.setScorTeam2(Server.gameState.getScorTeam2() + 1);
                Server.gameState.setPlayState("GOAL");
            } else {
                if (Server.gameState.getBall().getLastOwner().getCurrentTeam() == 2) {
                    Server.gameState.setPlayState("CORNER");
                } else {
                    Server.gameState.setPlayState("GOALKICK");
                }
            }
        }
        
        
    }
    
    void tryTackle(int index) {
        if (Server.gameState.getBall().isIsOwned()) {
            if (Server.gameState.getBall().getOwner().getState() == Player.PlayerStates.NORMAL) {
                if (!Server.gameState.getPlayers().elementAt(index).isHasBall()) {
                    if (Server.keyMap.isDPressed.elementAt(index)) {
                        if (distanceToBall(Server.gameState.getPlayers().elementAt(index)) < 15) {
                            if (Math.random() > 0.98 && Server.gameState.getBall().getOwner().getCurrentTeam() != Server.gameState.getPlayers().elementAt(index).getCurrentTeam()) {
                                Server.gameState.getBall().getOwner().setState(Player.PlayerStates.FAULT);
                                Server.gameState.setPlayState("FOUL");
                            } else if (Math.random() < 0.3) {
                                if (Server.gameState.getBall().getOwner().getCurrentTeam() != Server.gameState.getPlayers().elementAt(index).getCurrentTeam()) {
                                    Server.gameState.getBall().getOwner().increaseNoRunSpeed();
                                    Server.gameState.getBall().getOwner().setHasBall(false);
                                    Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(index));
                                    Server.gameState.getBall().getOwner().decreaseNoRunSpeed();
                                    Server.gameState.getPlayers().elementAt(index).setHasBall(true);
                                    Server.gameState.getBall().getOwner().setCanShoot(0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    void trySlide(int index) {
        
        //daca nu e portar
        if (!Server.gameState.getPlayers().elementAt(index).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
            if (Server.gameState.getBall().isIsOwned()) {
                if (Server.gameState.getBall().getOwner().getState() == Player.PlayerStates.NORMAL) {
                    if (!Server.gameState.getPlayers().elementAt(index).isHasBall()) {
                        if (Server.keyMap.isAPressed.elementAt(index)) {
                            if (distanceToBall(Server.gameState.getPlayers().elementAt(index)) < 25) {
                                //Server.gameState.getPlayers().elementAt(index).setState(Player.PlayerStates.SLIDING);
                                if (Server.gameState.getBall().getOwner().getCurrentTeam() != Server.gameState.getPlayers().elementAt(index).getCurrentTeam()) {
                                    if (Math.random() < 0.5) {
                                        Server.gameState.getBall().getOwner().setState(Player.PlayerStates.FAULT);
                                        Server.gameState.setPlayState("FOUL");
                                    } else if (Math.random() < 0.8) {
                                        if (Server.gameState.getBall().getOwner().getCurrentTeam() != Server.gameState.getPlayers().elementAt(index).getCurrentTeam()) {
                                            Server.gameState.getBall().getOwner().increaseNoRunSpeed();
                                            Server.gameState.getBall().getOwner().setHasBall(false);
                                            Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(index));
                                            Server.gameState.getBall().getOwner().decreaseNoRunSpeed();
                                            Server.gameState.getPlayers().elementAt(index).setHasBall(true);
                                            Server.gameState.getBall().getOwner().setCanShoot(0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else { // daca e portar
            
            if (!Server.gameState.getBall().isIsOwned()) { //mingea e libera => plonjeaza dupa ea
                if (Server.keyMap.isAPressed.elementAt(index)) {
                    if (distanceToBall(Server.gameState.getPlayers().elementAt(index)) < 35) {
                        if (Server.gameState.getPlayers().elementAt(index).isInsidePenaltyArea()) {
                            if (Math.random() < 0.8) {
                                Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(index));
                                Server.gameState.getBall().getOwner().decreaseNoRunSpeed();
                                Server.gameState.getBall().getOwner().setHasBall(true);
                                Server.gameState.getBall().getOwner().setHoldingBall(true);
                            }
                        }
                    }
                }
            } else { //incearca sa deposedeze un jucator (in careu)
                if (Server.keyMap.isAPressed.elementAt(index)) {
                    if (Server.gameState.getBall().getOwner().getState() == Player.PlayerStates.NORMAL) {
                        if (Server.gameState.getPlayers().elementAt(index).isInsidePenaltyArea()) {
                            if (distanceToBall(Server.gameState.getPlayers().elementAt(index)) < 35) {
                                if (Server.gameState.getBall().getOwner().getCurrentTeam() != Server.gameState.getPlayers().elementAt(index).getCurrentTeam()) {
                                    if (Math.random() < 0.1) {
                                        Server.gameState.getBall().getOwner().setState(Player.PlayerStates.FAULT);
                                        Server.gameState.setPlayState("FOUL");
                                    } else if (Math.random() < 0.85) {
                                        if (Server.gameState.getBall().getOwner().getCurrentTeam() != Server.gameState.getPlayers().elementAt(index).getCurrentTeam()) {
                                            System.out.println("slide portar");
                                            Server.gameState.getBall().getOwner().increaseNoRunSpeed();
                                            Server.gameState.getBall().getOwner().setHasBall(false);
                                            Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(index));
                                            Server.gameState.getBall().getOwner().decreaseNoRunSpeed();
                                            Server.gameState.getPlayers().elementAt(index).setHasBall(true);
                                            Server.gameState.getBall().getOwner().setCanShoot(0);
                                            Server.gameState.getBall().getOwner().setHoldingBall(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    void updatePlayers() {
        int i;
        boolean noPlayers = true;
        
        
//        if (Server.gameState.getPlayState().equals("INIT")) {
//            int t1counter = 0;
//            int t2counter = 0;
//            Server.gameState.getBall().setX(team1ActiveX[0]);
//            Server.gameState.getBall().setY(team1ActiveY[0]);
//            for (i = 0; i < Server.gameState.getPlayers().size(); i++) {
//                if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 1) {
//                    if (!Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
//                        Server.gameState.getPlayers().elementAt(i).setX(team1ActiveX[t1counter]);
//                        Server.gameState.getPlayers().elementAt(i).setY(team1ActiveY[t1counter]);
//                        t1counter++;
//                    } else {
//                        Server.gameState.getPlayers().elementAt(i).setX(team1ActiveX[155]);
//                        Server.gameState.getPlayers().elementAt(i).setY(team1ActiveY[418]);
//                    }
//
//                } else {
//                    if (!Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
//                        Server.gameState.getPlayers().elementAt(i).setX(team2PassiveX[t2counter]);
//                        Server.gameState.getPlayers().elementAt(i).setY(team2PassiveY[t2counter]);
//                        t2counter++;
//                    } else {
//                        Server.gameState.getPlayers().elementAt(i).setX(team1ActiveX[1275]);
//                        Server.gameState.getPlayers().elementAt(i).setY(team1ActiveY[418]);
//                    }
//                }
//            }
//            // <editor-fold defaultstate="collapsed" desc="sleep(2000)">
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                System.err.println(e.getMessage());
//            }// </editor-fold>
//            Server.gameState.setPlayState("PLAY");
//        }
//
//        if (Server.gameState.getPlayState().equals("GOAL")) {
//            int t1counter = 0;
//            int t2counter = 0;
//            if (Server.gameState.getBall().getX() > PLAYABLE_RIGHT_BOUND) { // echipa 1 a dat gol => echipa 1 kick-off
//                Server.gameState.getBall().setX(team1ActiveX[0]);
//                Server.gameState.getBall().setY(team1ActiveY[0]);
//                for (i = 0; i < Server.gameState.getPlayers().size(); i++) {
//                    if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 1) {
//
//                        Server.gameState.getPlayers().elementAt(i).setX(team1PassiveX[t1counter]);
//                        Server.gameState.getPlayers().elementAt(i).setY(team1PassiveY[t1counter]);
//                        t1counter++;
//                    } else {
//                        Server.gameState.getPlayers().elementAt(i).setX(team2ActiveX[t2counter]);
//                        Server.gameState.getPlayers().elementAt(i).setY(team2ActiveY[t2counter]);
//                        t2counter++;
//                    }
//                }
//
//            } else { //echipa 2 a dat gol => echipa 2 kick-off
//                Server.gameState.getBall().setX(team2ActiveX[0]);
//                Server.gameState.getBall().setY(team2ActiveY[0]);
//                for (i = 0; i < Server.gameState.getPlayers().size(); i++) {
//                    if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 1) {
//                        Server.gameState.getPlayers().elementAt(i).setX(team1ActiveX[t1counter]);
//                        Server.gameState.getPlayers().elementAt(i).setY(team1ActiveY[t1counter]);
//                        t1counter++;
//                    } else {
//                        Server.gameState.getPlayers().elementAt(i).setX(team2PassiveX[t2counter]);
//                        Server.gameState.getPlayers().elementAt(i).setY(team2PassiveY[t2counter]);
//                        t2counter++;
//                    }
//                }
//
//            }
//
//            // <editor-fold defaultstate="collapsed" desc="sleep(2000)">
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                System.err.println(e.getMessage());
//            }// </editor-fold>
//            Server.gameState.setPlayState("PLAY");
//        }
        
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
        if (Server.gameState.getPlayState().equals("INIT")) {
            
            int usedGK1 = 0, usedDef1 = 0, usedMid1 = 0, usedAtt1 = 0;
            int usedGK2 = 0, usedDef2 = 0, usedMid2 = 0, usedAtt2 = 0;
            int centerPlayers = 0, t1counter = 0, t2counter = 0; //numarul de jucatori activi
            int[] used1, used2;
            int divizor = 0;
            used1 = new int[team1.size()];
            used2 = new int[team1.size()];
            for (i = 0; i < used1.length; i++) {
                used1[i] = 0;
            }
            for (i = 0; i < used2.length; i++) {
                used2[i] = 0;
            }
            for (i = 0; i < team1.size(); i++) {
                if (team1.elementAt(i).getIsActive()) {
                    t1counter++;
                }
            }
            for (i = 0; i < team2.size(); i++) {
                if (team2.elementAt(i).getIsActive()) {
                    t2counter++;
                }
            }
            
            if (t1counter > 1) {
                for (i = 0; i < team1.size() && centerPlayers < 2; i++) {
                    if (team1.elementAt(i).getIsActive()) {
                        used1[i] = 1;
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.ATTACKER)) {
                            usedAtt1++;
                        }
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.MIDFIELDER)) {
                            usedMid1++;
                        }
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.DEFENDER)) {
                            usedDef1++;
                        }
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                            usedGK1++;
                        }
                        
                        if (centerPlayers == 0) {
                            centerPlayers++;
                            team1.elementAt(i).setX(715);
                            team1.elementAt(i).setY(400);
                            team1.elementAt(i).setHasBall(true);
                            Server.gameState.getBall().setOwner(team1.elementAt(i));
                            Server.gameState.getBall().setIsOwned(true);
                            Server.gameState.getBall().setX(Server.gameState.getBall().getOwner().getX());
                            Server.gameState.getBall().setY(Server.gameState.getBall().getOwner().getY());
                        } else {//centerPlayers = 1
                            centerPlayers++;
                            team1.elementAt(i).setX(715);
                            team1.elementAt(i).setY(445);
                        }
                        
                    }
                }
            } else if (t1counter == 1) {
                for (i = 0; i < team1.size(); i++) {
                    if (team1.elementAt(i).getIsActive()) {
                        used1[i] = 1;
                        centerPlayers++;
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.ATTACKER)) {
                            usedAtt1++;
                        }
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.MIDFIELDER)) {
                            usedMid1++;
                        }
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.DEFENDER)) {
                            usedDef1++;
                        }
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                            usedGK1++;
                        }
                        
                        team1.elementAt(i).setX(715);
                        team1.elementAt(i).setY(400);
                        centerPlayers++;
                        team1.elementAt(i).setHasBall(true);
                        Server.gameState.getBall().setOwner(team1.elementAt(i));
                        Server.gameState.getBall().setIsOwned(true);
                        Server.gameState.getBall().setX(Server.gameState.getBall().getOwner().getX());
                        Server.gameState.getBall().setY(Server.gameState.getBall().getOwner().getY());
                        
                        break;
                    }
                }
            } else {
                System.out.println("nu mai am jucatori intr-o echipa!");
                System.exit(0);
            }
            
            int nextY = 0;
            int INALTIME = PLAYABLE_LOWER_BOUND - PLAYABLE_UPPER_BOUND;
            if (centerPlayers == 2) {
                divizor = 1 + team1Att - usedAtt1;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = 0; i < team1Att; i++) {
                    if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                        team1.elementAt(i).setX(600);
                        team1.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                divizor = 1 + team1Mid - usedMid1;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = team1Att; i < team1Att + team1Mid; i++) {
                    if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                        team1.elementAt(i).setX(485);
                        team1.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                divizor = 1 + team1Def - usedDef1;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = team1Att + team1Mid; i < team1Att + team1Mid + team1Def; i++) {
                    if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                        team1.elementAt(i).setX(345);
                        team1.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                if (usedGK1 == 0 && team1GK == 1) {
                    for (i = team1Att + team1Mid + team1Def; i < team1.size(); i++) {
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                            team1.elementAt(i).setX(155);
                            team1.elementAt(i).setY(418);
                        }
                    }
                }
            }
            
            /////////////////////////echipa 2
            nextY = 0;
            divizor = 1 + team2Att - usedAtt2;
            nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
            for (i = 0; i < team2Att; i++) {
                if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                    team2.elementAt(i).setX(830);
                    team2.elementAt(i).setY(nextY);
                    nextY += INALTIME / divizor;
                }
            }
            divizor = 1 + team2Mid - usedMid2;
            nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
            for (i = team2Att; i < team2Att + team2Mid; i++) {
                if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                    team2.elementAt(i).setX(945);
                    team2.elementAt(i).setY(nextY);
                    nextY += INALTIME / divizor;
                }
            }
            divizor = 1 + team2Def - usedDef2;
            nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
            for (i = team2Att + team2Mid; i < team2Att + team2Mid + team2Def; i++) {
                if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                    team2.elementAt(i).setX(1085);
                    team2.elementAt(i).setY(nextY);
                    nextY += INALTIME / divizor;
                }
            }
            if (usedGK2 == 0 && team2GK == 1) {
                for (i = team2Att + team2Mid + team2Def; i < team2.size(); i++) {
                    if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                        team2.elementAt(i).setX(1275);
                        team2.elementAt(i).setY(418);
                    }
                }
            }
            
            
            // <editor-fold defaultstate="collapsed" desc="sleep(2000)">
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }// </editor-fold>
            Server.gameState.setPlayState("PLAY");
        }
        
        if (Server.gameState.getPlayState().equals("GOAL")) {
            int t1counter = 0;
            int t2counter = 0;
            if (Server.gameState.getBall().getX() > PLAYABLE_RIGHT_BOUND) { // echipa 1 a dat gol => echipa 2 kick-off
                
                
                int usedGK1 = 0, usedDef1 = 0, usedMid1 = 0, usedAtt1 = 0;
                int usedGK2 = 0, usedDef2 = 0, usedMid2 = 0, usedAtt2 = 0;
                int centerPlayers = 0;
                t1counter = 0;
                t2counter = 0;
                int[] used1, used2; //sper ca sunt initializate 0
                int divizor = 0;
                used1 = new int[team1.size()];
                used2 = new int[team1.size()];
                for (i = 0; i < used1.length; i++) {
                    used1[i] = 0;
                }
                for (i = 0; i < used2.length; i++) {
                    used2[i] = 0;
                }
                for (i = 0; i < team1.size(); i++) {
                    if (team1.elementAt(i).getIsActive()) {
                        t1counter++;
                    }
                }
                for (i = 0; i < team2.size(); i++) {
                    if (team2.elementAt(i).getIsActive()) {
                        t2counter++;
                    }
                }
                
                if (t2counter > 1) {
                    for (i = 0; i < team2.size() && centerPlayers < 2; i++) {
                        if (team2.elementAt(i).getIsActive()) {
                            used2[i] = 1;
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.ATTACKER)) {
                                usedAtt2++;
                            }
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.MIDFIELDER)) {
                                usedMid2++;
                            }
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.DEFENDER)) {
                                usedDef2++;
                            }
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                                usedGK2++;
                            }
                            
                            if (centerPlayers == 0) {
                                centerPlayers++;
                                team2.elementAt(i).setX(715);
                                team2.elementAt(i).setY(400);
                                team2.elementAt(i).setHasBall(true);
                                Server.gameState.getBall().setOwner(team2.elementAt(i));
                                Server.gameState.getBall().setIsOwned(true);
                                Server.gameState.getBall().setX(Server.gameState.getBall().getOwner().getX());
                                Server.gameState.getBall().setY(Server.gameState.getBall().getOwner().getY());
                            } else {//centerPlayers = 1
                                centerPlayers++;
                                team2.elementAt(i).setX(715);
                                team2.elementAt(i).setY(445);
                            }
                            
                        }
                    }
                } else if (t2counter == 1) {
                    for (i = 0; i < team2.size(); i++) {
                        if (team2.elementAt(i).getIsActive()) {
                            used2[i] = 1;
                            centerPlayers++;
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.ATTACKER)) {
                                usedAtt2++;
                            }
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.MIDFIELDER)) {
                                usedMid2++;
                            }
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.DEFENDER)) {
                                usedDef2++;
                            }
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                                usedGK2++;
                            }
                            
                            
                            team2.elementAt(i).setX(715);
                            team2.elementAt(i).setY(400);
                            centerPlayers++;
                            team2.elementAt(i).setHasBall(true);
                            Server.gameState.getBall().setOwner(team2.elementAt(i));
                            Server.gameState.getBall().setIsOwned(true);
                            Server.gameState.getBall().setX(Server.gameState.getBall().getOwner().getX());
                            Server.gameState.getBall().setY(Server.gameState.getBall().getOwner().getY());
                            
                            break;
                        }
                    }
                } else {
                    System.out.println("nu mai am jucatori intr-o echipa!");
                    System.exit(0);
                }
                
                int nextY = 0;
                int INALTIME = PLAYABLE_LOWER_BOUND - PLAYABLE_UPPER_BOUND;
                if (centerPlayers == 2) {
                    divizor = 1 + team2Att - usedAtt2;
                    nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                    for (i = 0; i < team2Att; i++) {
                        if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                            team2.elementAt(i).setX(830);
                            team2.elementAt(i).setY(nextY);
                            nextY += INALTIME / divizor;
                        }
                    }
                    divizor = 1 + team2Mid - usedMid2;
                    nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                    for (i = team2Att; i < team2Att + team2Mid; i++) {
                        if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                            team2.elementAt(i).setX(945);
                            team2.elementAt(i).setY(nextY);
                            nextY += INALTIME / divizor;
                        }
                    }
                    divizor = 1 + team2Def - usedDef2;
                    nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                    for (i = team2Att + team2Mid; i < team2Att + team2Mid + team2Def; i++) {
                        if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                            team2.elementAt(i).setX(1085);
                            team2.elementAt(i).setY(nextY);
                            nextY += INALTIME / divizor;
                        }
                    }
                    if (usedGK2 == 0 && team2GK == 1) {
                        for (i = team2Att + team2Mid + team2Def; i < team2.size(); i++) {
                            if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                                team2.elementAt(i).setX(1275);
                                team2.elementAt(i).setY(418);
                            }
                        }
                    }
                }
                
                /////////////////////////echipa 1
                nextY = 0;
                divizor = 1 + team1Att - usedAtt1;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = 0; i < team1Att; i++) {
                    if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                        team1.elementAt(i).setX(600);
                        team1.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                divizor = 1 + team1Mid - usedMid1;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = team1Att; i < team1Att + team1Mid; i++) {
                    if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                        team1.elementAt(i).setX(485);
                        team1.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                divizor = 1 + team1Def - usedDef1;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = team1Att + team1Mid; i < team1Att + team1Mid + team1Def; i++) {
                    if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                        team1.elementAt(i).setX(345);
                        team1.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                if (usedGK1 == 0 && team1GK == 1) {
                    for (i = team1Att + team1Mid + team1Def; i < team1.size(); i++) {
                        if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                            team1.elementAt(i).setX(155);
                            team1.elementAt(i).setY(418);
                        }
                    }
                }
                
                
                
                
                
                
                
            } else { //echipa 2 a dat gol => echipa 1 kick-off
                
                int usedGK1 = 0, usedDef1 = 0, usedMid1 = 0, usedAtt1 = 0;
                int usedGK2 = 0, usedDef2 = 0, usedMid2 = 0, usedAtt2 = 0;
                int centerPlayers = 0;
                t1counter = 0;
                t2counter = 0;
                int[] used1, used2; //sper ca sunt initializate 0
                int divizor = 0;
                used1 = new int[team1.size()];
                used2 = new int[team1.size()];
                for (i = 0; i < used1.length; i++) {
                    used1[i] = 0;
                }
                for (i = 0; i < used2.length; i++) {
                    used2[i] = 0;
                }
                for (i = 0; i < team1.size(); i++) {
                    if (team1.elementAt(i).getIsActive()) {
                        t1counter++;
                    }
                }
                for (i = 0; i < team2.size(); i++) {
                    if (team2.elementAt(i).getIsActive()) {
                        t2counter++;
                    }
                }
                
                if (t1counter > 1) {
                    for (i = 0; i < team1.size() && centerPlayers < 2; i++) {
                        if (team1.elementAt(i).getIsActive()) {
                            used1[i] = 1;
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.ATTACKER)) {
                                usedAtt1++;
                            }
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.MIDFIELDER)) {
                                usedMid1++;
                            }
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.DEFENDER)) {
                                usedDef1++;
                            }
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                                usedGK1++;
                            }
                            
                            if (centerPlayers == 0) {
                                centerPlayers++;
                                team1.elementAt(i).setX(715);
                                team1.elementAt(i).setY(400);
                                team1.elementAt(i).setHasBall(true);
                                Server.gameState.getBall().setOwner(team1.elementAt(i));
                                Server.gameState.getBall().setIsOwned(true);
                                Server.gameState.getBall().setX(Server.gameState.getBall().getOwner().getX());
                                Server.gameState.getBall().setY(Server.gameState.getBall().getOwner().getY());
                            } else {//centerPlayers = 1
                                centerPlayers++;
                                team1.elementAt(i).setX(715);
                                team1.elementAt(i).setY(445);
                            }
                            
                        }
                    }
                } else if (t1counter == 1) {
                    for (i = 0; i < team1.size(); i++) {
                        if (team1.elementAt(i).getIsActive()) {
                            used1[i] = 1;
                            centerPlayers++;
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.ATTACKER)) {
                                usedAtt1++;
                            }
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.MIDFIELDER)) {
                                usedMid1++;
                            }
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.DEFENDER)) {
                                usedDef1++;
                            }
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                                usedGK1++;
                            }
                            
                            
                            team1.elementAt(i).setX(715);
                            team1.elementAt(i).setY(400);
                            centerPlayers++;
                            team1.elementAt(i).setHasBall(true);
                            Server.gameState.getBall().setOwner(team1.elementAt(i));
                            Server.gameState.getBall().setIsOwned(true);
                            Server.gameState.getBall().setX(Server.gameState.getBall().getOwner().getX());
                            Server.gameState.getBall().setY(Server.gameState.getBall().getOwner().getY());
                            
                            break;
                        }
                    }
                } else {
                    System.out.println("nu mai am jucatori intr-o echipa!");
                    System.exit(0);
                }
                
                int nextY = 0;
                int INALTIME = PLAYABLE_LOWER_BOUND - PLAYABLE_UPPER_BOUND;
                if (centerPlayers == 2) {
                    divizor = 1 + team1Att - usedAtt1;
                    nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                    for (i = 0; i < team1Att; i++) {
                        if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                            team1.elementAt(i).setX(600);
                            team1.elementAt(i).setY(nextY);
                            nextY += INALTIME / divizor;
                        }
                    }
                    divizor = 1 + team1Mid - usedMid1;
                    nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                    for (i = team1Att; i < team1Att + team1Mid; i++) {
                        if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                            team1.elementAt(i).setX(485);
                            team1.elementAt(i).setY(nextY);
                            nextY += INALTIME / divizor;
                        }
                    }
                    divizor = 1 + team1Def - usedDef1;
                    nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                    for (i = team1Att + team1Mid; i < team1Att + team1Mid + team1Def; i++) {
                        if (team1.elementAt(i).getIsActive() && used1[i] != 1) {
                            team1.elementAt(i).setX(485);
                            team1.elementAt(i).setY(nextY);
                            nextY += INALTIME / divizor;
                        }
                    }
                    if (usedGK1 == 0 && team1GK == 1) {
                        for (i = team1Att + team1Mid + team1Def; i < team1.size(); i++) {
                            if (team1.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                                team1.elementAt(i).setX(155);
                                team1.elementAt(i).setY(418);
                            }
                        }
                    }
                }
                
                /////////////////////////echipa 2
                nextY = 0;
                divizor = 1 + team2Att - usedAtt2;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = 0; i < team2Att; i++) {
                    if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                        team2.elementAt(i).setX(830);
                        team2.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                divizor = 1 + team2Mid - usedMid2;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = team2Att; i < team2Att + team2Mid; i++) {
                    if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                        team2.elementAt(i).setX(945);
                        team2.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                divizor = 1 + team2Def - usedDef2;
                nextY = INALTIME / divizor + PLAYABLE_UPPER_BOUND;
                for (i = team2Att + team2Mid; i < team2Att + team2Mid + team2Def; i++) {
                    if (team2.elementAt(i).getIsActive() && used2[i] != 1) {
                        team2.elementAt(i).setX(1085);
                        team2.elementAt(i).setY(nextY);
                        nextY += INALTIME / divizor;
                    }
                }
                if (usedGK2 == 0 && team2GK == 1) {
                    for (i = team2Att + team2Mid + team2Def; i < team2.size(); i++) {
                        if (team2.elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                            team2.elementAt(i).setX(1275);
                            team2.elementAt(i).setY(418);
                        }
                    }
                }
                
                
                
            }
            
            
            // <editor-fold defaultstate="collapsed" desc="sleep(2000)">
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }// </editor-fold>
            Server.gameState.setPlayState("PLAY");
            
        }
        
        
        
        if (Server.gameState.getPlayState().equals("CORNER")) {
            Player p;
            
            boolean activeFound = false;
            if (Server.gameState.getBall().getX() < PLAYABLE_LEFT_BOUND) { //corner stanga
                if (Server.gameState.getBall().getY() < GOAL_START_Y) { //corner sus
                    //for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
                    p = Server.gameState.getRandomPlayer(2);
                    //if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 2 && Server.gameState.getPlayers().elementAt(i).getIsActive()) {
                    activeFound = true;
                    Server.gameState.getBall().setIsOwned(true);
                    Server.gameState.getBall().setOwner(p);
                    Server.gameState.getBall().getOwner().setHasBall(true);
                    Server.gameState.getBall().getOwner().setState(Player.PlayerStates.CORNER);
                    Server.gameState.getBall().getOwner().setDirection("S");
                    Server.gameState.getBall().getOwner().setX(PLAYABLE_LEFT_BOUND + 5);
                    Server.gameState.getBall().getOwner().setY(PLAYABLE_UPPER_BOUND + 5);
                    Server.gameState.getBall().setX(PLAYABLE_LEFT_BOUND + 5);
                    Server.gameState.getBall().setY(PLAYABLE_UPPER_BOUND + 5);
                    //}
                    //}
                } else { // corner jos
                    
                    //	for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
                    //	if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 2 && Server.gameState.getPlayers().elementAt(i).getIsActive()) {
                    p = Server.gameState.getRandomPlayer(2);
                    activeFound = true;
                    Server.gameState.getBall().setIsOwned(true);
                    Server.gameState.getBall().setOwner(p);
                    Server.gameState.getBall().getOwner().setHasBall(true);
                    Server.gameState.getBall().getOwner().setState(Player.PlayerStates.CORNER);
                    Server.gameState.getBall().getOwner().setDirection("N");
                    Server.gameState.getBall().getOwner().setX(PLAYABLE_LEFT_BOUND + 5);
                    Server.gameState.getBall().getOwner().setY(PLAYABLE_LOWER_BOUND - 5);
                    Server.gameState.getBall().setX(PLAYABLE_LEFT_BOUND + 5);
                    Server.gameState.getBall().setY(PLAYABLE_LOWER_BOUND - 5);
                }
                //}
                //}
            } else { // corner dreapta
                if (Server.gameState.getBall().getY() < GOAL_START_Y) { //corner sus
                    //	for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
                    //	if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 1 && Server.gameState.getPlayers().elementAt(i).getIsActive()) {
                    p = Server.gameState.getRandomPlayer(1);
                    activeFound = true;
                    Server.gameState.getBall().setIsOwned(true);
                    Server.gameState.getBall().setOwner(p);
                    Server.gameState.getBall().getOwner().setHasBall(true);
                    Server.gameState.getBall().getOwner().setState(Player.PlayerStates.CORNER);
                    Server.gameState.getBall().getOwner().setDirection("S");
                    Server.gameState.getBall().getOwner().setX(PLAYABLE_RIGHT_BOUND - 5);
                    Server.gameState.getBall().getOwner().setY(PLAYABLE_UPPER_BOUND + 5);
                    Server.gameState.getBall().setX(PLAYABLE_RIGHT_BOUND - 5);
                    Server.gameState.getBall().setY(PLAYABLE_UPPER_BOUND + 5);
                    //	}
                    //}
                } else { //corner jos
                    //for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
//					if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 1 && Server.gameState.getPlayers().elementAt(i).getIsActive()) {
                    p = Server.gameState.getRandomPlayer(1);
                    activeFound = true;
                    Server.gameState.getBall().setIsOwned(true);
                    Server.gameState.getBall().setOwner(p);
                    Server.gameState.getBall().getOwner().setHasBall(true);
                    Server.gameState.getBall().getOwner().setState(Player.PlayerStates.CORNER);
                    Server.gameState.getBall().getOwner().setDirection("N");
                    Server.gameState.getBall().getOwner().setX(PLAYABLE_RIGHT_BOUND - 5);
                    Server.gameState.getBall().getOwner().setY(PLAYABLE_LOWER_BOUND - 5);
                    Server.gameState.getBall().setX(PLAYABLE_RIGHT_BOUND - 5);
                    Server.gameState.getBall().setY(PLAYABLE_LOWER_BOUND - 5);
                    //	}
                    //	}
                }
                
            }
            clearArea(p);
            
            // <editor-fold defaultstate="collapsed" desc="sleep(2000)">
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }// </editor-fold>
            Server.gameState.setPlayState("PLAY");
            
        }
        if (Server.gameState.getPlayState().equals("GOALKICK")) {
            boolean activeFound = false;
            if (Server.gameState.getBall().getX() < PLAYABLE_LEFT_BOUND) { // gk stanga
                
                for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
                    if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 1 && Server.gameState.getPlayers().elementAt(i).getIsActive() && Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                        activeFound = true;
                        Server.gameState.getBall().setIsOwned(true);
                        Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(i));
                        Server.gameState.getBall().getOwner().setHasBall(true);
                        Server.gameState.getBall().getOwner().setState(Player.PlayerStates.GOALKICK);
                        Server.gameState.getBall().getOwner().setDirection("E");
                        Server.gameState.getBall().getOwner().setX(150);
                        Server.gameState.getBall().getOwner().setY(340);
                        Server.gameState.getBall().setX(150);
                        Server.gameState.getBall().setY(340);
                    }
                }
                for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
                    if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 1 && Server.gameState.getPlayers().elementAt(i).getIsActive()) {
                        activeFound = true;
                        Server.gameState.getBall().setIsOwned(true);
                        Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(i));
                        Server.gameState.getBall().getOwner().setHasBall(true);
                        Server.gameState.getBall().getOwner().setState(Player.PlayerStates.GOALKICK);
                        Server.gameState.getBall().getOwner().setDirection("E");
                        Server.gameState.getBall().getOwner().setX(150);
                        Server.gameState.getBall().getOwner().setY(340);
                        Server.gameState.getBall().setX(150);
                        Server.gameState.getBall().setY(340);
                    }
                }
            } else { //gk dreapta
                
                for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
                    if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 2 && Server.gameState.getPlayers().elementAt(i).getIsActive() && Server.gameState.getPlayers().elementAt(i).getPosition().equals(Player.PlayerPosition.GOALKEEPER)) {
                        activeFound = true;
                        Server.gameState.getBall().setIsOwned(true);
                        Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(i));
                        Server.gameState.getBall().getOwner().setHasBall(true);
                        Server.gameState.getBall().getOwner().setState(Player.PlayerStates.GOALKICK);
                        Server.gameState.getBall().getOwner().setDirection("W");
                        Server.gameState.getBall().getOwner().setX(1250);
                        Server.gameState.getBall().getOwner().setY(460);
                        Server.gameState.getBall().setX(1250);
                        Server.gameState.getBall().setY(460);
                    }
                }
                
                for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
                    if (Server.gameState.getPlayers().elementAt(i).getCurrentTeam() == 2 && Server.gameState.getPlayers().elementAt(i).getIsActive()) {
                        activeFound = true;
                        Server.gameState.getBall().setIsOwned(true);
                        Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(i));
                        Server.gameState.getBall().getOwner().setHasBall(true);
                        Server.gameState.getBall().getOwner().setState(Player.PlayerStates.GOALKICK);
                        Server.gameState.getBall().getOwner().setDirection("W");
                        Server.gameState.getBall().getOwner().setX(1250);
                        Server.gameState.getBall().getOwner().setY(460);
                        Server.gameState.getBall().setX(1250);
                        Server.gameState.getBall().setY(460);
                    }
                }
            }
            
            clearArea(Server.gameState.getBall().getOwner());
            
            // <editor-fold defaultstate="collapsed" desc="sleep(2000)">
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }// </editor-fold>
            
            Server.gameState.setPlayState("PLAY");
            
            
        }
        if (Server.gameState.getPlayState().equals("OUT")) {
            Player p;
            if (Server.gameState.getBall().getLastOwner().getCurrentTeam() == 1) {
                p = Server.gameState.getRandomPlayer(2);
            } else {
                p = Server.gameState.getRandomPlayer(1);
            }
            if (Server.gameState.getBall().getOwner() != null) {
                Server.gameState.getBall().getOwner().setHasBall(false);
            }
            Server.gameState.getBall().setIsOwned(true);
            Server.gameState.getBall().setOwner(p);
            Server.gameState.getBall().getOwner().setHasBall(true);
            Server.gameState.getBall().getOwner().setState(Player.PlayerStates.OUT);
            
            if (Server.gameState.getBall().getY() < PLAYABLE_UPPER_BOUND) {
                Server.gameState.getBall().getOwner().setDirection("S");
                Server.gameState.getBall().getOwner().setX((int) Server.gameState.getBall().getX());
                Server.gameState.getBall().getOwner().setY(PLAYABLE_UPPER_BOUND + 5);
                Server.gameState.getBall().setY(PLAYABLE_UPPER_BOUND + 5);
            } else {
                Server.gameState.getBall().getOwner().setDirection("N");
                Server.gameState.getBall().getOwner().setX((int) Server.gameState.getBall().getX());
                Server.gameState.getBall().getOwner().setY(PLAYABLE_LOWER_BOUND - 5);
                Server.gameState.getBall().setY(PLAYABLE_LOWER_BOUND - 5);
            }
            
            clearArea(Server.gameState.getBall().getOwner());
            
            // <editor-fold defaultstate="collapsed" desc="sleep(2000)">
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }// </editor-fold>
            
            Server.gameState.setPlayState("PLAY");
            
        }
        if (Server.gameState.getPlayState().equals("FOUL")) {
            boolean activeFound = false;
            for (i = 0; i < Server.gameState.getPlayers().size() && !activeFound; i++) {
                if (Server.gameState.getPlayers().elementAt(i).getState().equals(Player.PlayerStates.FAULT)) {
                    activeFound = true;
                    Server.gameState.getBall().setIsOwned(true);
                    Server.gameState.getBall().setOwner(Server.gameState.getPlayers().elementAt(i));
                    Server.gameState.getBall().getOwner().setHasBall(true);
                    Server.gameState.getBall().getOwner().setShootPower(0);
                    if (Server.gameState.getBall().getOwner().getCurrentTeam() == 1) {
                        Server.gameState.getBall().getOwner().setDirection("W");
                    } else {
                        Server.gameState.getBall().getOwner().setDirection("E");
                    }
                    Server.gameState.getBall().getOwner().setX((int) Server.gameState.getBall().getX());
                    Server.gameState.getBall().getOwner().setY((int) Server.gameState.getBall().getY());
                }
//                else if (i == Server.gameState.getPlayers().size()) {
//                    activeFound = true;
//                }
//                Player p = Server.gameState.getRandomPlayer(Server.gameState.getBall().getLastOwner().getCurrentTeam());
//                Server.gameState.getBall().setIsOwned(true);
//                Server.gameState.getBall().setOwner(p);
//                Server.gameState.getBall().getOwner().setHasBall(true);
//                Server.gameState.getBall().getOwner().setShootPower(0);
//                if (Server.gameState.getBall().getOwner().getCurrentTeam() == 1) {
//                    Server.gameState.getBall().getOwner().setDirection("W");
//                } else {
//                    Server.gameState.getBall().getOwner().setDirection("E");
//                }
//                Server.gameState.getBall().getOwner().setX((int) Server.gameState.getBall().getX());
//                Server.gameState.getBall().getOwner().setY((int) Server.gameState.getBall().getY());
            }
            
            clearArea(Server.gameState.getBall().getOwner());
            
            
            // <editor-fold defaultstate="collapsed" desc="sleep(2000)">
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }// </editor-fold>
            
            Server.gameState.setPlayState("PLAY");
            
            
        }
        for (i = 0; i < Server.gameState.getPlayers().size(); i++) {
            if (Server.gameState.getPlayers().elementAt(i).getIsActive()) {
                noPlayers = false;
                updatePlayer(i);
                tryTackle(i);
                trySlide(i);
            }
        }
        if (noPlayers) {
            System.out.println("no more players! closing server...");
            Server.gameState.setPlayState("FORFEIT");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
            }
            System.exit(0);
        }
    }
    
    public void run() {
        while (Server.gameState.getElapsedTime() < matchLengthMs) {
            try {
                Thread.sleep(40);
                updateTime();
                updatePlayers();
                testShoot();
                updateBall();
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
        Server.gameState.setPlayState("END");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
        }
        System.exit(0);
    }
}