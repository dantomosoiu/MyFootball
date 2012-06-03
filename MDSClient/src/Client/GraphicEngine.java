package Client;

import Transmitable.toClient.GameState;
import Transmitable.toClient.Player.PlayerPosition;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Se ocupa cu afisarea pe ecran a jocului, in functie de ce primeste de la server prin GameState
 * Instantiata in ServerReceiver
 *
 */
public class GraphicEngine extends Frame implements MouseMotionListener, MouseListener {

	GraphicsEnvironment env;
	final GraphicsDevice device;
	RenderingHints rh;
	static boolean runFullScreen = false;
	static String screenResolution;
	private int width, height, bitsPerPixel;
	private boolean isMenuVisible;
	int mouseX = 0, mouseY = 0;
	Color playerEnergyLow = Color.red;
	Color playerEnergyMedium = Color.yellow;
	Color playerEnergyHigh = Color.green;
	Color playerEnergyBar = Color.darkGray;
	int viewportX, viewportY; // coordonatele de la care este afisat pe ecran terenul
	int myPlayerX, myPlayerY;
	int scrollThresholdX = 300, scrollThresholdY = 200;
	int windowChromeY = 0;
	final int imgFieldWidth = 1800, imgFieldHeight = 735, imgFieldTilt = 376;
	int radarX, radarY, radarWidth = 137, radarHeight = 85;
	int PLAYABLE_UPPER_BOUND = 40;
	int PLAYABLE_LEFT_BOUND = 127;
	int PLAYABLE_RIGHT_BOUND = 1300;
	int PLAYABLE_LOWER_BOUND = 796;
	int FIELDSTARTX = 423;
	int FIELDSTARTY = 0;
	int PLAYER_WIDTH = 7;
	int PLAYER_HEIGHT = 20;
	int GOAL_HEIGHT = 27;
	int GOAL_START_Y = (PLAYABLE_LOWER_BOUND - PLAYABLE_UPPER_BOUND) / 2 + PLAYABLE_UPPER_BOUND - 42;
	int GOAL_END_Y = (PLAYABLE_LOWER_BOUND - PLAYABLE_UPPER_BOUND) / 2 + PLAYABLE_UPPER_BOUND + 42;
	static BufferedImage teren = null;
	VolatileImage vTeren;
	BufferedImage minge[];
	VolatileImage vMinge[];
	BufferedImage playerTeam1, playerTeam2, goalkeeperTeam1, goalkeeperTeam2;
	VolatileImage vPlayerTeam1, vPlayerTeam2, vGoalkeeperTeam1, vGoalkeeperTeam2;
	Font playerNameFont;
	Font playStateFont;
	Font loadingFont;
	// variabile folosite in render()
	// pentru a evita realocarea lor la fiecare tact, le definim aici
	int x, y;
	int ballX = 0, ballY = 0;
	int currentBallSprite = 0;
	FontMetrics fontMetrics;
	int stringWidth;
	Random random;
	String tmpStr;
	long matchSec, matchMin;
	// Meniu
	Color backgroundOverlayColor = new Color(0, 0, 0, 100);
	Color menuWindowColor = new Color(0, 0, 0, 150);
	Color menuTextColor = Color.white;
	Color menuButtonColor = new Color(0, 0, 0, 200);
	Color menuActiveButtonColor = new Color(0, 116, 210, 230);
	Font menuTitleFont = new Font("Verdana", Font.PLAIN, 22);
	Font menuButtonFont = new Font("Verdana", Font.BOLD, 18);
	Font menuTextFont = new Font("Verdana", Font.PLAIN, 12);
	Rectangle menuWindowChrome;
	int menuWindowChromeWidth = 600, menuWindowChromeHeight = 280;
	int crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight;

	GraphicEngine() {

		env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = env.getDefaultScreenDevice();

		String[] res = screenResolution.split("x");

		width = Integer.parseInt(res[0]);
		height = Integer.parseInt(res[1]);
		bitsPerPixel = Integer.parseInt(res[2]);
		isMenuVisible = false;

		menuWindowChrome = new Rectangle(width / 2 - menuWindowChromeWidth / 2, height / 2 - menuWindowChromeHeight / 2, menuWindowChromeWidth, menuWindowChromeHeight);

		setIgnoreRepaint(true);

		if (runFullScreen) {
			setResizable(false);
			setUndecorated(true);
			device.setFullScreenWindow(this);
			DisplayMode dsp = new DisplayMode(width, height, bitsPerPixel, DisplayMode.REFRESH_RATE_UNKNOWN);
			device.setDisplayMode(dsp);

		} else {
			setSize(width, height);
			windowChromeY = 30;
			height -= windowChromeY;
		}

		addKeyListener(new ActionKeyListener(this));
		addMouseMotionListener(this);
		addMouseListener(this);
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				Client.exit();
			}
		});
		addWindowStateListener(new WindowAdapter() {

			public void windowStateChanged(WindowEvent evt) {
				int oldState = evt.getOldState();
				int newState = evt.getNewState();
				if ((oldState & GraphicEngine.MAXIMIZED_BOTH) == 0 && (newState & GraphicEngine.MAXIMIZED_BOTH) != 0) {
					GraphicEngine.changeResolution(NetworkCommunication.serverReceiver.graphicEngine, GraphicEngine.screenResolution, true);
				}
			}
		});

		setTitle("MyFootball");
		setVisible(true);
		createBufferStrategy(4);

		random = new Random();

		renderLoading("Loading images...", 20);

		minge = new BufferedImage[13];
		vMinge = new VolatileImage[13];

		try {
			if (teren == null) {
				teren = ImageIO.read(getClass().getResource("/images/teren.png"));
			}
			renderLoading("Loading images...", 40);
			minge[0] = ImageIO.read(getClass().getResource("/images/ball-left0.png"));
			minge[1] = ImageIO.read(getClass().getResource("/images/ball-left1.png"));
			minge[2] = ImageIO.read(getClass().getResource("/images/ball-left2.png"));
			minge[3] = ImageIO.read(getClass().getResource("/images/ball-left3.png"));
			minge[4] = ImageIO.read(getClass().getResource("/images/ball-left4.png"));
			minge[5] = ImageIO.read(getClass().getResource("/images/ball-left5.png"));
			minge[6] = ImageIO.read(getClass().getResource("/images/ball-right0.png"));
			minge[7] = ImageIO.read(getClass().getResource("/images/ball-right1.png"));
			minge[8] = ImageIO.read(getClass().getResource("/images/ball-right2.png"));
			minge[9] = ImageIO.read(getClass().getResource("/images/ball-right3.png"));
			minge[10] = ImageIO.read(getClass().getResource("/images/ball-right4.png"));
			minge[11] = ImageIO.read(getClass().getResource("/images/ball-right5.png"));
			minge[12] = ImageIO.read(getClass().getResource("/images/ball-right6.png"));
			renderLoading("Loading images...", 50);
			playerTeam1 = ImageIO.read(getClass().getResource("/images/jucator1.gif"));
			playerTeam2 = ImageIO.read(getClass().getResource("/images/jucator2.gif"));
			goalkeeperTeam1 = ImageIO.read(getClass().getResource("/images/portar1.gif"));
			goalkeeperTeam2 = ImageIO.read(getClass().getResource("/images/portar2.gif"));
		} catch (IOException ex) {
			System.err.println("Nu am reusit sa incarc imaginile ! " + ex.getMessage());
		}


		renderLoading("Loading fonts...", 60);

		playerNameFont = new Font("Tahoma", Font.BOLD, 11);
		playStateFont = new Font("Tahoma", Font.BOLD, 92);
		loadingFont = new Font("Tahoma", Font.BOLD, 12);
		radarX = width - radarWidth - 10;
		radarY = 10;

		if (height > imgFieldHeight) {
			FIELDSTARTY = height / 2 - imgFieldHeight / 2;
		}

		renderLoading("Waiting for data from the server...", 95);
	}

	int isometricX(int x, int y) {
		return x - (int) Math.round(y / 2);
	}

	int isometricY(int y) {
		return (int) Math.round(y * 0.865);
	}

	public void render(GameState gs) {
		int i;
		BufferStrategy bfr = getBufferStrategy();
		Graphics g = bfr.getDrawGraphics();
		Graphics2D g2 = (Graphics2D) g;
		//g2.setRenderingHints(rh);

		g2.translate(0, windowChromeY);

		// golesc ecranul
		g2.setColor(Color.white);
		g2.fillRect(0, 0, width, height);

		// Mai intai aflu coordonatele jucatorului meu, actualizez viewportX si viewportY pentru ca
		// restul jucatorilor si mingea vor fi afisati relativ la aceaste coordonate
		for (i = 0; i < gs.getPlayers().size(); i++) {
			if (gs.getPlayers().elementAt(i).getIsActive()) {
				if (gs.getPlayers().elementAt(i).getPlayerID().compareTo(Client.playerID) == 0) {
					myPlayerX = gs.getPlayers().elementAt(i).getX();
					myPlayerY = gs.getPlayers().elementAt(i).getY();
				}
			}
		}

		myPlayerX = isometricX(myPlayerX, myPlayerY) + FIELDSTARTX;
		myPlayerY = isometricY(myPlayerY);

		// scroll orizontal
		if (viewportX + scrollThresholdX > myPlayerX) {
			// trebuie deplasat terenul spre stanga
			viewportX = Math.max(myPlayerX - scrollThresholdX, 0);
		} else if (viewportX + width - scrollThresholdX < myPlayerX) {
			// trebuie deplasat terenul spre dreapta
			viewportX = Math.min(myPlayerX + scrollThresholdX - width, imgFieldWidth - width);
		}

		// scroll vertical
		if (height <= 600) {
			if (viewportY + scrollThresholdY > myPlayerY) {
				// trebuie deplasat terenul spre stanga
				viewportY = Math.max(myPlayerY - scrollThresholdY, 0);
			} else if (viewportY + height - scrollThresholdY < myPlayerY) {
				// trebuie deplasat terenul spre dreapta
				viewportY = Math.min(myPlayerY + scrollThresholdY - height, imgFieldHeight - height);
			}
		}

		// desenez terenul
		vTeren = drawVolatileImage(g2, vTeren, 0, FIELDSTARTY, width, Math.min(height, imgFieldHeight + FIELDSTARTY), viewportX, viewportY, viewportX + width, viewportY + Math.min(height, imgFieldHeight), teren);

		// afisez radarul ( 117x75 - de 10 ori mai mic decat terenul )
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRect(radarX, radarY, radarWidth, radarHeight);
		// afisez tusa de pe radar
		g2.setColor(new Color(255, 255, 255, 150));
		g2.drawLine(radarX + 12, radarY + 4, radarX + 12, radarY + 79);    // stanga
		g2.drawLine(radarX + 130, radarY + 4, radarX + 130, radarY + 79);  // drapta
		g2.drawLine(radarX + 12, radarY + 4, radarX + 130, radarY + 4);    // sus
		g2.drawLine(radarX + 12, radarY + 79, radarX + 130, radarY + 79);    // jos
		g2.drawLine(radarX + 71, radarY + 4, radarX + 71, radarY + 79); // tusa centru
		g2.drawOval(radarX + 61, radarY + 31, 20, 20); // cerc centru
		g2.drawLine(radarX + 12, radarY + 30, radarX + 18, radarY + 30); // careu mic stanga tusa sus
		g2.drawLine(radarX + 18, radarY + 30, radarX + 18, radarY + 52); // careu mic stanga tusa verticala
		g2.drawLine(radarX + 12, radarY + 52, radarX + 18, radarY + 52); // careu stanga tusa jos
		g2.drawLine(radarX + 12, radarY + 17, radarX + 31, radarY + 17); // careu mare stanga tusa sus
		g2.drawLine(radarX + 31, radarY + 17, radarX + 31, radarY + 65); // careu mare stanga tusa verticala
		g2.drawLine(radarX + 12, radarY + 65, radarX + 31, radarY + 65); // careu mare stanga tusa sus
		g2.drawLine(radarX + 123, radarY + 30, radarX + 129, radarY + 30); // careu mic stanga tusa sus
		g2.drawLine(radarX + 123, radarY + 30, radarX + 123, radarY + 52); // careu mic stanga tusa verticala
		g2.drawLine(radarX + 123, radarY + 52, radarX + 129, radarY + 52); // careu stanga tusa jos
		g2.drawLine(radarX + 110, radarY + 17, radarX + 129, radarY + 17); // careu mare stanga tusa sus
		g2.drawLine(radarX + 110, radarY + 17, radarX + 110, radarY + 65); // careu mare stanga tusa verticala
		g2.drawLine(radarX + 110, radarY + 65, radarX + 129, radarY + 65); // careu mare stanga tusa sus
		g2.drawArc(radarX + 23, radarY + 31, 18, 18, 90, -180); // semicerc stanga
		g2.drawArc(radarX + 101, radarY + 31, 18, 18, 90, 180); // semicerc dreapta

		// setez fontul folosit pentru numele jucatorilor
		fontMetrics = g2.getFontMetrics(playerNameFont);
		g2.setFont(playerNameFont);

		Collections.sort(gs.getPlayers());

		for (i = 0; i < gs.getPlayers().size(); i++) {
			if (gs.getPlayers().elementAt(i).getIsActive()) {
				x = gs.getPlayers().elementAt(i).getX();
				y = gs.getPlayers().elementAt(i).getY();

				// afisez jucatorul pe teren
				if (gs.getPlayers().elementAt(i).getCurrentTeam() == 1) {
					if (gs.getPlayers().elementAt(i).getPosition() == PlayerPosition.GOALKEEPER) {
						vGoalkeeperTeam1 = drawTransparentVolatileImage(g2, vGoalkeeperTeam1, isometricX(x, y) - PLAYER_WIDTH / 2 - viewportX + FIELDSTARTX, isometricY(y) - PLAYER_HEIGHT - viewportY + FIELDSTARTY, goalkeeperTeam1);
					} else {
						vPlayerTeam1 = drawTransparentVolatileImage(g2, vPlayerTeam1, isometricX(x, y) - PLAYER_WIDTH / 2 - viewportX + FIELDSTARTX, isometricY(y) - PLAYER_HEIGHT - viewportY + FIELDSTARTY, playerTeam1);
					}
				} else {
					if (gs.getPlayers().elementAt(i).getPosition() == PlayerPosition.GOALKEEPER) {
						vGoalkeeperTeam2 = drawTransparentVolatileImage(g2, vGoalkeeperTeam2, isometricX(x, y) - PLAYER_WIDTH / 2 - viewportX + FIELDSTARTX, isometricY(y) - PLAYER_HEIGHT - viewportY + FIELDSTARTY, goalkeeperTeam2);
					} else {
						vPlayerTeam2 = drawTransparentVolatileImage(g2, vPlayerTeam2, isometricX(x, y) - PLAYER_WIDTH / 2 - viewportX + FIELDSTARTX, isometricY(y) - PLAYER_HEIGHT - viewportY + FIELDSTARTY, playerTeam2);
					}
				}

				//g2.fillRect(isometricX(x, y) - PLAYER_WIDTH / 2 - viewportX + FIELDSTARTX, isometricY(y) - PLAYER_HEIGHT - viewportY + FIELDSTARTY, PLAYER_WIDTH, PLAYER_HEIGHT);


				// afisez jucatorul pe radar
				if (gs.getPlayers().elementAt(i).getCurrentTeam() == 1) {
					g2.setColor(Color.red);
				} else {
					g2.setColor(new Color(51, 133, 255));
				}
				if (gs.getPlayers().elementAt(i).getPlayerID().compareTo(Client.playerID) == 0) {
					// daca e jucatorul meu, il afisez diferentiat fata de ceilalti
					g2.setColor(Color.white);
				}
				g2.fillRect(radarX + x / 10, radarY + y / 10, 3, 3);

				// afisez numele jucatorilor
				stringWidth = fontMetrics.stringWidth(gs.getPlayers().elementAt(i).getPlayerName());
				g2.setColor(Color.black);
				g2.drawString(gs.getPlayers().elementAt(i).getPlayerName(), isometricX(x, y) - viewportX + FIELDSTARTX + 1 - stringWidth / 2 + PLAYER_WIDTH / 2, isometricY(y) - 4 - PLAYER_HEIGHT - viewportY + FIELDSTARTY + 1);
				if (gs.getPlayers().elementAt(i).isRequestingBall() == true && gs.getBall().getLastOwner() != gs.getPlayers().elementAt(i)) {
					g2.setColor(Color.green);
				} else {
					g2.setColor(Color.white);
				}
				g2.drawString(gs.getPlayers().elementAt(i).getPlayerName(), isometricX(x, y) - viewportX + FIELDSTARTX - stringWidth / 2 + PLAYER_WIDTH / 2, isometricY(y) - 4 - PLAYER_HEIGHT - viewportY + FIELDSTARTY);

				// afisez chestii care tin doar de jucatorul meu
				if (gs.getPlayers().elementAt(i).getPlayerID().compareTo(Client.playerID) == 0) {
					// afisez bara cu energia jucatorului
					g2.setColor(playerEnergyBar);
					g2.fillRect(10, height - 16, 102, 7);
					if (gs.getPlayers().elementAt(i).getEnergy() > 140) {
						g2.setColor(playerEnergyHigh);
					} else if (gs.getPlayers().elementAt(i).getEnergy() > 60) {
						g2.setColor(playerEnergyMedium);
					} else {
						g2.setColor(playerEnergyLow);
					}
					g2.fillRect(11, height - 15, gs.getPlayers().elementAt(i).getEnergy() / 2, 5);

					// afisez bara cu puterea sutului ( valori intre 0 si 25 )
					if (gs.getPlayers().elementAt(i).getShootPower() > 0) {
						g2.setColor(Color.white);
						g2.drawRect(isometricX(x, y) - 12 + PLAYER_WIDTH / 2 - viewportX + FIELDSTARTX, isometricY(y) + PLAYER_HEIGHT + 3 - viewportY + FIELDSTARTY, 25, 4);
						//g2.drawLine(isometricX(x - 2, y + 24) - viewportX + FIELDSTARTX, y + 24 - viewportY + FIELDSTARTY, isometricX(x - 2 + gs.getPlayers().elementAt(i).getShootPower(), y + 24) - viewportX + FIELDSTARTX, y + 24 - viewportY + FIELDSTARTY);
						g2.fillRect(isometricX(x, y) - 12 + PLAYER_WIDTH / 2 - viewportX + FIELDSTARTX, isometricY(y) + PLAYER_HEIGHT + 4 - viewportY + FIELDSTARTY, gs.getPlayers().elementAt(i).getShootPower(), 3);
					}
				}
			}

		}
		//afisez mingea
		//g2.setColor(Color.orange);
		//g2.fillOval(isometricX((int) Math.round(gs.getBall().getX()), (int) Math.round(gs.getBall().getY())) - 4 - viewportX + FIELDSTARTX, isometricY((int) Math.round(gs.getBall().getY())) - 4 - viewportY + FIELDSTARTY, 8, 8);


		if (ballX != (int) Math.round(gs.getBall().getX()) || ballY != (int) Math.round(gs.getBall().getY())) {
			ballX = (int) Math.round(gs.getBall().getX());
			ballY = (int) Math.round(gs.getBall().getY());
			if ((gs.getBall().isIsOwned() == true && gs.getBall().getOwner().isHoldingBall() == false) || gs.getBall().isIsOwned() == false) {
				currentBallSprite = random.nextInt(13);
			}
		}

		vMinge[currentBallSprite] = drawTransparentVolatileImage(g2, vMinge[currentBallSprite], isometricX(ballX, ballY) - 4 - viewportX + FIELDSTARTX, isometricY(ballY) - 4 - viewportY + FIELDSTARTY, minge[currentBallSprite]);
		// afisez mingea pe radar
		g2.setColor(Color.orange);
		g2.drawRect(radarX + ((int) Math.round(gs.getBall().getX())) / 10 - 1, radarY + ((int) Math.round(gs.getBall().getY())) / 10 - 1, 4, 4);

		// afisez portile
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		//stanga
		g2.drawLine(isometricX(PLAYABLE_LEFT_BOUND, GOAL_START_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_START_Y) - GOAL_HEIGHT - viewportY + FIELDSTARTY, isometricX(PLAYABLE_LEFT_BOUND, GOAL_END_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_END_Y) - GOAL_HEIGHT - viewportY + FIELDSTARTY);
		g2.drawLine(isometricX(PLAYABLE_LEFT_BOUND, GOAL_START_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_START_Y) - GOAL_HEIGHT - viewportY + FIELDSTARTY, isometricX(PLAYABLE_LEFT_BOUND, GOAL_START_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_START_Y) - viewportY + FIELDSTARTY);
		g2.drawLine(isometricX(PLAYABLE_LEFT_BOUND, GOAL_END_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_END_Y) - GOAL_HEIGHT - viewportY + FIELDSTARTY, isometricX(PLAYABLE_LEFT_BOUND, GOAL_END_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_END_Y) - viewportY + FIELDSTARTY);
		// dreapta
		g2.drawLine(isometricX(PLAYABLE_RIGHT_BOUND, GOAL_START_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_START_Y) - GOAL_HEIGHT - viewportY + FIELDSTARTY, isometricX(PLAYABLE_RIGHT_BOUND, GOAL_END_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_END_Y) - GOAL_HEIGHT - viewportY + FIELDSTARTY);
		g2.drawLine(isometricX(PLAYABLE_RIGHT_BOUND, GOAL_START_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_START_Y) - GOAL_HEIGHT - viewportY + FIELDSTARTY, isometricX(PLAYABLE_RIGHT_BOUND, GOAL_START_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_START_Y) - viewportY + FIELDSTARTY);
		g2.drawLine(isometricX(PLAYABLE_RIGHT_BOUND, GOAL_END_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_END_Y) - GOAL_HEIGHT - viewportY + FIELDSTARTY, isometricX(PLAYABLE_RIGHT_BOUND, GOAL_END_Y) - viewportX + FIELDSTARTX, isometricY(GOAL_END_Y) - viewportY + FIELDSTARTY);

		if (gs.getPlayState().equals("INIT")) {
			Date timp = new Date();
			if ((timp.getTime() % 2) == 0) {
				g2.setFont(playStateFont);
				g2.drawString("PLAY!", width / 2 - 100, height / 2);
				g2.setFont(playerNameFont);
			}
		}
		if (gs.getPlayState().equals("OUT")) {
			Date timp = new Date();
			if ((timp.getTime() % 2) == 0) {
				g2.setFont(playStateFont);
				g2.drawString("OUT!", width / 2 - 100, height / 2);
				g2.setFont(playerNameFont);
			}
		}
		if (gs.getPlayState().equals("GOAL")) {
			Date timp = new Date();
			if ((timp.getTime() % 2) == 0) {
				g2.setFont(playStateFont);
				g2.drawString("GOAL!", width / 2 - 100, height / 2);
				g2.setFont(playerNameFont);
			}
		}
		if (gs.getPlayState().equals("CORNER")) {
			Date timp = new Date();
			if ((timp.getTime() % 2) == 0) {
				g2.setFont(playStateFont);
				g2.drawString("CORNER!", width / 2 - 100, height / 2);
				g2.setFont(playerNameFont);
			}
		}
		if (gs.getPlayState().equals("GOALKICK")) {
			Date timp = new Date();
			if ((timp.getTime() % 2) == 0) {
				g2.setFont(playStateFont);
				g2.drawString("GOAL KICK!", width / 2 - 100, height / 2);
				g2.setFont(playerNameFont);
			}
		}
		if (gs.getPlayState().equals("FOUL")) {
			Date timp = new Date();
			if ((timp.getTime() % 2) == 0) {
				g2.setFont(playStateFont);
				g2.drawString("FOUL!", width / 2 - 100, height / 2);
				g2.setFont(playerNameFont);
			}
		}
		if (gs.getPlayState().equals("END")) {
			g2.setFont(playStateFont);
			g2.drawString("END OF MATCH", width / 2 - 200, height / 2);
			g2.setFont(playerNameFont);

		}
		if (gs.getPlayState().equals("FORFEIT")) {
			g2.setFont(playStateFont);
			g2.drawString("FORFEIT", width / 2 - 100, height / 2);
			g2.setFont(playerNameFont);

		}


		// afisez scorul
		tmpStr = Integer.toString(gs.getScorTeam2()) + " - " + Integer.toString(gs.getScorTeam1());
		g2.setColor(Color.black);
		g2.drawString(tmpStr, radarX + 6, radarY + radarHeight + 11);
		g2.setColor(Color.white);
		g2.drawString(tmpStr, radarX + 5, radarY + radarHeight + 10);

		// afisez timpul
		matchSec = gs.getElapsedTime() / 1000;
		matchMin = matchSec / 60;
		matchSec = matchSec - 60 * matchMin;

		tmpStr = "";
		if (matchMin < 10) {
			tmpStr += "0";
		}
		tmpStr += matchMin + ":";
		if (matchSec < 10) {
			tmpStr += "0";
		}
		tmpStr += matchSec;

		stringWidth = fontMetrics.stringWidth(tmpStr);
		g2.setColor(Color.black);
		g2.drawString(tmpStr, radarX + radarWidth - stringWidth - 4, radarY + radarHeight + 11);
		g2.setColor(Color.white);
		g2.drawString(tmpStr, radarX + radarWidth - stringWidth - 5, radarY + radarHeight + 10);


		// afisez meniul, daca este necesar
		// <editor-fold defaultstate="collapsed" desc="Meniu">
		if (isMenuVisible) {
			g2.setColor(backgroundOverlayColor);
			g2.fillRect(0, 0, width, height);
			g2.setColor(menuWindowColor);
			g2.fillRect(menuWindowChrome.x, menuWindowChrome.y, menuWindowChrome.width, menuWindowChrome.height);
			g2.setColor(menuTextColor);
			g2.setFont(menuTitleFont);
			fontMetrics = g2.getFontMetrics(menuTitleFont);
			stringWidth = fontMetrics.stringWidth("Game Menu");
			g2.drawString("Game Menu", menuWindowChrome.x + menuWindowChrome.width / 2 - stringWidth / 2, menuWindowChrome.y + 35);

			g2.setFont(menuTextFont);
			g2.drawString("Change screen resolution", menuWindowChrome.x + 315, menuWindowChrome.y + 80);
			g2.drawString("Windowed", menuWindowChrome.x + 270, menuWindowChrome.y + 105);
			g2.drawString("Fullscreen", menuWindowChrome.x + 470, menuWindowChrome.y + 105);

			// Butoane
			g2.setFont(menuButtonFont);
			fontMetrics = g2.getFontMetrics(menuButtonFont);

			stringWidth = fontMetrics.stringWidth("Return to game");
			crtButtonX = menuWindowChrome.x + 15;
			crtButtonY = menuWindowChrome.y + 120;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				g2.setColor(menuActiveButtonColor);
			} else {
				g2.setColor(menuButtonColor);
			}
			g2.fillRect(crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight);
			g2.setColor(menuTextColor);
			g2.drawString("Return to game", crtButtonX + crtButtonWidth / 2 - stringWidth / 2, crtButtonY + 25);
			stringWidth = fontMetrics.stringWidth("Exit game");
			crtButtonX = menuWindowChrome.x + 15;
			crtButtonY = menuWindowChrome.y + 170;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				g2.setColor(menuActiveButtonColor);
			} else {
				g2.setColor(menuButtonColor);
			}
			g2.fillRect(crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight);
			g2.setColor(menuTextColor);
			g2.drawString("Exit game", crtButtonX + crtButtonWidth / 2 - stringWidth / 2, crtButtonY + 25);

			// Butoane rezolutie
			//Windowed
			stringWidth = fontMetrics.stringWidth("800x600x16");
			crtButtonX = menuWindowChrome.x + 215;
			crtButtonY = menuWindowChrome.y + 120;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				g2.setColor(menuActiveButtonColor);
			} else {
				g2.setColor(menuButtonColor);
			}
			g2.fillRect(crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight);
			g2.setColor(menuTextColor);
			g2.drawString("800x600x16", crtButtonX + crtButtonWidth / 2 - stringWidth / 2, crtButtonY + 25);

			stringWidth = fontMetrics.stringWidth("1024x768x16");
			crtButtonX = menuWindowChrome.x + 215;
			crtButtonY = menuWindowChrome.y + 170;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				g2.setColor(menuActiveButtonColor);
			} else {
				g2.setColor(menuButtonColor);
			}
			g2.fillRect(crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight);
			g2.setColor(menuTextColor);
			g2.drawString("1024x768x16", crtButtonX + crtButtonWidth / 2 - stringWidth / 2, crtButtonY + 25);

			stringWidth = fontMetrics.stringWidth("1280x1024x16");
			crtButtonX = menuWindowChrome.x + 215;
			crtButtonY = menuWindowChrome.y + 220;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				g2.setColor(menuActiveButtonColor);
			} else {
				g2.setColor(menuButtonColor);
			}
			g2.fillRect(crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight);
			g2.setColor(menuTextColor);
			g2.drawString("1280x1024x16", crtButtonX + crtButtonWidth / 2 - stringWidth / 2, crtButtonY + 25);


			//Fullscreen
			stringWidth = fontMetrics.stringWidth("800x600x16");
			crtButtonX = menuWindowChrome.x + 415;
			crtButtonY = menuWindowChrome.y + 120;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				g2.setColor(menuActiveButtonColor);
			} else {
				g2.setColor(menuButtonColor);
			}
			g2.fillRect(crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight);
			g2.setColor(menuTextColor);
			g2.drawString("800x600x16", crtButtonX + crtButtonWidth / 2 - stringWidth / 2, crtButtonY + 25);

			stringWidth = fontMetrics.stringWidth("1024x768x16");
			crtButtonX = menuWindowChrome.x + 415;
			crtButtonY = menuWindowChrome.y + 170;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				g2.setColor(menuActiveButtonColor);
			} else {
				g2.setColor(menuButtonColor);
			}
			g2.fillRect(crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight);
			g2.setColor(menuTextColor);
			g2.drawString("1024x768x16", crtButtonX + crtButtonWidth / 2 - stringWidth / 2, crtButtonY + 25);

			stringWidth = fontMetrics.stringWidth("1280x1024x16");
			crtButtonX = menuWindowChrome.x + 415;
			crtButtonY = menuWindowChrome.y + 220;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				g2.setColor(menuActiveButtonColor);
			} else {
				g2.setColor(menuButtonColor);
			}
			g2.fillRect(crtButtonX, crtButtonY, crtButtonWidth, crtButtonHeight);
			g2.setColor(menuTextColor);
			g2.drawString("1280x1024x16", crtButtonX + crtButtonWidth / 2 - stringWidth / 2, crtButtonY + 25);
		}// </editor-fold>

		// chestii care tin de motorul grafic
		bfr.show();
		g2.clearRect(0, 0, width, height);
		g2.dispose();
		g.dispose();
	}

	public void renderLoading(String message, int loadingProgress) {
		BufferStrategy bfr = getBufferStrategy();
		Graphics g = bfr.getDrawGraphics();
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(0, windowChromeY);

		// golesc ecranul
		g2.setColor(new Color(0, 0, 0));
		g2.fillRect(0, 0, width, height);

		g2.setColor(Color.white);
		g2.setFont(loadingFont);

		// verticala
		g2.drawLine(width / 2 - 50, height / 2 - 50, width / 2 - 50, height / 2 + 70);
		// orizontala
		g2.drawLine(width / 2 - 70, height / 2 + 50, width / 2 + 50, height / 2 + 50);

		g2.drawString(message, width / 2 - 40, height / 2 - 20);
		g2.drawRect(width / 2 - 40, height / 2, 100, 10);
		g2.fillRect(width / 2 - 40, height / 2, loadingProgress, 10);

		// chestii care tin de motorul grafic
		bfr.show();
		g2.clearRect(0, 0, width, height);
		g2.dispose();
		g.dispose();
	}

	/**
	 * Desenaza o imagine pe ecran folosind metoda VolatileImage. Apelurile ulterioare
	 * la aceasta metoda ar trebui sa foloseasca ca parametru img valoarea returnata de functie.
	 * @param img Imaginea care va fi afisata. Ar trebui sa se foloseasca valoarea intoarsa de functie la un apel anterior.
	 */
	public VolatileImage drawVolatileImage(Graphics2D g, VolatileImage img, int x, int y, Image orig) {
		final int MAX_TRIES = 100;
		for (int i = 0; i < MAX_TRIES; i++) {
			if (img != null) {
				// Draw the volatile image
				g.drawImage(img, x, y, null);

				// Check if it is still valid
				if (!img.contentsLost()) {
					return img;
				}
			} else {
				// Create the volatile image
				img = g.getDeviceConfiguration().createCompatibleVolatileImage(
						orig.getWidth(null), orig.getHeight(null));
			}

			// Determine how to fix the volatile image
			switch (img.validate(g.getDeviceConfiguration())) {
				case VolatileImage.IMAGE_OK:
					// This should not happen
					break;
				case VolatileImage.IMAGE_INCOMPATIBLE:
					// Create a new volatile image object;
					// this could happen if the component was moved to another device
					img.flush();
					img = g.getDeviceConfiguration().createCompatibleVolatileImage(
							orig.getWidth(null), orig.getHeight(null));
				case VolatileImage.IMAGE_RESTORED:
					// Copy the original image to accelerated image memory
					Graphics2D gc = (Graphics2D) img.createGraphics();
					gc.drawImage(orig, 0, 0, null);
					gc.dispose();
					break;
			}
		}

		// The image failed to be drawn after MAX_TRIES;
		// draw with the non-accelerated image
		g.drawImage(orig, x, y, null);
		return img;
	}

	public VolatileImage drawVolatileImage(Graphics2D g, VolatileImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Image orig) {
		final int MAX_TRIES = 100;
		for (int i = 0; i < MAX_TRIES; i++) {
			if (img != null) {
				// Draw the volatile image
				g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);

				// Check if it is still valid
				if (!img.contentsLost()) {
					return img;
				}
			} else {
				// Create the volatile image
				img = g.getDeviceConfiguration().createCompatibleVolatileImage(
						orig.getWidth(null), orig.getHeight(null));
			}

			// Determine how to fix the volatile image
			switch (img.validate(g.getDeviceConfiguration())) {
				case VolatileImage.IMAGE_OK:
					// This should not happen
					break;
				case VolatileImage.IMAGE_INCOMPATIBLE:
					// Create a new volatile image object;
					// this could happen if the component was moved to another device
					img.flush();
					img = g.getDeviceConfiguration().createCompatibleVolatileImage(
							orig.getWidth(null), orig.getHeight(null));
				case VolatileImage.IMAGE_RESTORED:
					// Copy the original image to accelerated image memory
					Graphics2D gc = (Graphics2D) img.createGraphics();
					gc.drawImage(orig, 0, 0, null);
					gc.dispose();
					break;
			}
		}

		// The image failed to be drawn after MAX_TRIES;
		// draw with the non-accelerated image
		g.drawImage(orig, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
		return img;
	}

	public VolatileImage drawTransparentVolatileImage(Graphics2D g, VolatileImage img, int x, int y, Image orig) {
		final int MAX_TRIES = 100;
		for (int i = 0; i < MAX_TRIES; i++) {
			if (img != null) {
				// Draw the volatile image
				g.drawImage(img, x, y, null);

				// Check if it is still valid
				if (!img.contentsLost()) {
					return img;
				}
			} else {
				// Create the volatile image
				img = g.getDeviceConfiguration().createCompatibleVolatileImage(orig.getWidth(null), orig.getHeight(null), Transparency.BITMASK);
			}

			// Determine how to fix the volatile image
			switch (img.validate(g.getDeviceConfiguration())) {
				case VolatileImage.IMAGE_OK:
					// This should not happen
					break;
				case VolatileImage.IMAGE_INCOMPATIBLE:
					// Create a new volatile image object;
					// this could happen if the component was moved to another device
					img.flush();
					img = g.getDeviceConfiguration().createCompatibleVolatileImage(
							orig.getWidth(null), orig.getHeight(null));
				case VolatileImage.IMAGE_RESTORED:
					// Copy the original image to accelerated image memory
					Graphics2D gc = (Graphics2D) img.createGraphics();
					gc.setComposite(AlphaComposite.Src);
					gc.setColor(new Color(0, 0, 0, 0));
					gc.fillRect(0, 0, orig.getWidth(null), orig.getHeight(null)); // Clears the image.
					gc.drawImage(orig, 0, 0, null);
					gc.dispose();
					break;
			}
		}

		// The image failed to be drawn after MAX_TRIES;
		// draw with the non-accelerated image
		g.drawImage(orig, x, y, null);
		return img;
	}

	public void showMenu() {
		isMenuVisible = true;
	}

	public void hideMenu() {
		isMenuVisible = false;
	}

	public void toggleMenu() {
		isMenuVisible = !isMenuVisible;
	}

	public boolean isMenuVisible() {
		return isMenuVisible;
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY() - windowChromeY;
	}

	public void mouseClicked(MouseEvent e) {
		if (isMenuVisible) {
			// Return to game
			crtButtonX = menuWindowChrome.x + 15;
			crtButtonY = menuWindowChrome.y + 120;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				toggleMenu();
			}
			// Exit game
			crtButtonX = menuWindowChrome.x + 15;
			crtButtonY = menuWindowChrome.y + 170;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				Client.exit();
			}

			// Windowed 800x600x16
			crtButtonX = menuWindowChrome.x + 215;
			crtButtonY = menuWindowChrome.y + 120;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				changeResolution(this, "800x600x16", false);
			}
			// Windowed 1024x768x16
			crtButtonX = menuWindowChrome.x + 215;
			crtButtonY = menuWindowChrome.y + 170;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				changeResolution(this, "1024x768x16", false);
			}
			// Windowed 1280x1024x16
			crtButtonX = menuWindowChrome.x + 215;
			crtButtonY = menuWindowChrome.y + 220;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				changeResolution(this, "1280x1024x16", false);
			}

			// Fullscreen 800x600x16
			crtButtonX = menuWindowChrome.x + 415;
			crtButtonY = menuWindowChrome.y + 120;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				changeResolution(this, "800x600x16", true);
			}
			// Fullscreen 1024x768x16
			crtButtonX = menuWindowChrome.x + 415;
			crtButtonY = menuWindowChrome.y + 170;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				changeResolution(this, "1024x768x16", true);
			}
			// Fullscreen 1280x1024x16
			crtButtonX = menuWindowChrome.x + 415;
			crtButtonY = menuWindowChrome.y + 220;
			crtButtonWidth = 170;
			crtButtonHeight = 40;
			if (crtButtonX < mouseX && mouseX < crtButtonX + crtButtonWidth && crtButtonY < mouseY && mouseY < crtButtonY + crtButtonHeight) {
				changeResolution(this, "1280x1024x16", true);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public static void changeResolution(GraphicEngine oldGraphicEngine, String newResolution, boolean fullscreen) {
		NetworkCommunication.serverReceiver.graphicEngine = null;
		oldGraphicEngine.dispose();
		GraphicEngine.screenResolution = newResolution;
		GraphicEngine.runFullScreen = fullscreen;
		GraphicEngine ge = new GraphicEngine();
		NetworkCommunication.serverReceiver.graphicEngine = ge;
	}
}
