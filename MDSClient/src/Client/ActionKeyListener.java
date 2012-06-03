package Client;

import Transmitable.toServer.ActionKey;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ActionKeyListener implements KeyListener {

	GraphicEngine ge;

	ActionKeyListener(GraphicEngine ge) {
		this.ge = ge;
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if(ge.isMenuVisible() == false) {
			ActionKey ak = new ActionKey(e.getKeyCode(), true);
			NetworkCommunication.send(ak);
			//System.out.println("Am apasat " + e.getKeyCode());
		}
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			ge.toggleMenu();
		}
		if(ge.isMenuVisible() == false) {
			ActionKey ak = new ActionKey(e.getKeyCode(), false);
			NetworkCommunication.send(ak);
			//System.out.println("Am ridicat " + e.getKeyCode());
		}
	}
}
