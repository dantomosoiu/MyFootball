package Server;

import Transmitable.toServer.ActionKey;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class KeyMap {

    Vector<Boolean> isUpPressed;
    Vector<Boolean> isDownPressed;
    Vector<Boolean> isLeftPressed;
    Vector<Boolean> isRightPressed;
    Vector<Boolean> isAPressed;
    Vector<Boolean> isSPressed;
    Vector<Boolean> isDPressed;
    Vector<Boolean> isEPressed;
    Vector<Boolean> isWPressed;

    KeyMap(int totalPlayers) {
        int i;
        isUpPressed = new Vector<Boolean>();
        isDownPressed = new Vector<Boolean>();
        isLeftPressed = new Vector<Boolean>();
        isRightPressed = new Vector<Boolean>();
        isAPressed = new Vector<Boolean>();
        isSPressed = new Vector<Boolean>();
        isDPressed = new Vector<Boolean>();
        isEPressed = new Vector<Boolean>();
        isWPressed = new Vector<Boolean>();


        isUpPressed.setSize(totalPlayers);
        isDownPressed.setSize(totalPlayers);
        isLeftPressed.setSize(totalPlayers);
        isRightPressed.setSize(totalPlayers);
        isAPressed.setSize(totalPlayers);
        isSPressed.setSize(totalPlayers);
        isDPressed.setSize(totalPlayers);
        isEPressed.setSize(totalPlayers);
        isWPressed.setSize(totalPlayers);

        for (i = 0; i < totalPlayers; i++) {
            isUpPressed.setElementAt(new Boolean(false), i);
            isDownPressed.setElementAt(new Boolean(false), i);
            isLeftPressed.setElementAt(new Boolean(false), i);
            isRightPressed.setElementAt(new Boolean(false), i);
            isAPressed.setElementAt(new Boolean(false), i);
            isSPressed.setElementAt(new Boolean(false), i);
            isDPressed.setElementAt(new Boolean(false), i);
            isEPressed.setElementAt(new Boolean(false), i);
            isWPressed.setElementAt(new Boolean(false), i);

        }
    }

    static void update(ActionKey actionKey, int index) {
//		if(actionKey.pressed == true) {
//			System.out.println(index + " -> am primit ca s-a apasat " + actionKey.keyCode);
//		}
//		else {
//			System.out.println(index + " -> am primit ca s-a ridicat " + actionKey.keyCode);
//		}

        switch (actionKey.keyCode) {
            case KeyEvent.VK_UP:
                Server.keyMap.setIsUpPressed(index, actionKey.pressed);
                break;
            case KeyEvent.VK_DOWN:
                Server.keyMap.setIsDownPressed(index, actionKey.pressed);
                break;
            case KeyEvent.VK_LEFT:
                Server.keyMap.setIsLeftPressed(index, actionKey.pressed);
                break;
            case KeyEvent.VK_RIGHT:
                Server.keyMap.setIsRightPressed(index, actionKey.pressed);
                break;
            case KeyEvent.VK_A:
                Server.keyMap.setIsAPressed(index, actionKey.pressed);
                break;
            case KeyEvent.VK_S:
                Server.keyMap.setIsSPressed(index, actionKey.pressed);
                break;
            case KeyEvent.VK_D:
                Server.keyMap.setIsDPressed(index, actionKey.pressed);
                break;
            case KeyEvent.VK_W:
                Server.keyMap.setIsWPressed(index, actionKey.pressed);
                break;
            case KeyEvent.VK_E:
                Server.keyMap.setIsEPressed(index, actionKey.pressed);
                break;

        }
    }

    public Vector<Boolean> getIsAPressed() {
        return isAPressed;
    }

    public Vector<Boolean> getIsDPressed() {
        return isDPressed;
    }

    public Vector<Boolean> getIsDownPressed() {
        return isDownPressed;
    }

    public Vector<Boolean> getIsLeftPressed() {
        return isLeftPressed;
    }

    public Vector<Boolean> getIsRightPressed() {
        return isRightPressed;
    }

    public Vector<Boolean> getIsSPressed() {
        return isSPressed;
    }

    public Vector<Boolean> getIsUpPressed() {
        return isUpPressed;
    }

    public Vector<Boolean> getIsWPressed() {
        return isWPressed;
    }

    public Vector<Boolean> getIsEPressed() {
        return isEPressed;
    }

    public void setIsUpPressed(int index, Boolean pressed) {
        isUpPressed.set(index, pressed);
    }

    public void setIsDownPressed(int index, Boolean pressed) {
        isDownPressed.set(index, pressed);
    }

    public void setIsRightPressed(int index, Boolean pressed) {
        isRightPressed.set(index, pressed);
    }

    public void setIsLeftPressed(int index, Boolean pressed) {
        isLeftPressed.set(index, pressed);
    }

    public void setIsAPressed(int index, Boolean pressed) {
        isAPressed.set(index, pressed);
    }

    public void setIsSPressed(int index, Boolean pressed) {
        isSPressed.set(index, pressed);
    }

    public void setIsDPressed(int index, Boolean pressed) {
        isDPressed.set(index, pressed);
    }

    public void setIsWPressed(int index, Boolean pressed) {
        isWPressed.set(index, pressed);
    }

    public void setIsEPressed(int index, Boolean pressed) {
        isEPressed.set(index, pressed);
    }
}
