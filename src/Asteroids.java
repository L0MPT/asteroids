import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Asteroids extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -8869205056996498774L;

	private final int targetDelay = 16; // 1000/60 or ~60 fps

	private static double rumble = 0;
	private final static double rumbleMax = 4;

	double timeSincePlay = 0;

	Random r = new Random();

	private static final String soundPath = "src/resources/sounds/";

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	/**
	 * The virtual width of the game window in scaled pixels.
	 */
	public static int width = 16 * 70;
	/**
	 * The virtual height of the game window in scaled pixels.
	 */
	public static int height = 9 * 70;

	/**
	 * The width of the game window in actual pixels.
	 */
	public static int trueWidth = width;
	/**
	 * The height of the game window in actual pixels.
	 */
	public static int trueHeight = height;

	public static double scaleFactor = 1;

	public static boolean fullscreenEnabled = false;

	static boolean[] keys = new boolean[227];

	static int x = 100;
	static int y = 100;

	static AsteroidPlayer player1;
	static AsteroidPlayer player2;

	ScoreIndicator p1ScoreIndicator = new ScoreIndicator(0, p1Color, false, false, 5);
	ScoreIndicator p2ScoreIndicator = new ScoreIndicator(0, p2Color, true, true, 5);

	int score1 = 0;
	int score2 = 0;

	static double deltaTime;

	long pastTime = 0;

	public static Color p1Color = new Color(60, 100, 255);
	public static Color p2Color = new Color(60, 255, 60);

	static Star[] stars = new Star[150];

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initWindow();
			}
		});
	}

	public static void initWindow() {
		// create a window frame and set the title in the toolbar
		JFrame window = new JFrame("Asteroids");
		// when we close the window, stop the app
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// create the jpanel to draw on.
		// this also initializes the game loop
		Asteroids boardy = new Asteroids();
		// add the jpanel to the window
		window.add(boardy);
		// pass keyboard inputs to the jpanel
		window.addKeyListener(boardy);

		window.addMouseListener(boardy);
		window.addMouseMotionListener(boardy);
		// don't allow the user to resize the window
		window.setResizable(true);

		window.setUndecorated(false);

		// fit the window size around the components (just our jpanel).
		// pack() should be called after setResizable() to avoid issues on some
		// platforms
		window.pack();

		window.getContentPane().addComponentListener(new ResizeListener());

		// open window in the center of the screen
		window.setLocationRelativeTo(null);
		// display the window
		window.setVisible(true);
	}

	public Asteroids() {
		// setup:
		setPreferredSize(new Dimension(width, height));
		setBackground(new Color(70, 70, 70));

		createPlayers();
		preloadSounds();
		createStars();

		setDoubleBuffered(true);

		new Thread(this::gameLoop).start();
	}

	public void createPlayers() {
		int x1 = r.nextInt(width - 20) + 10;
		int y1 = r.nextInt(height - 20) + 10;
		player1 = new AsteroidPlayer(x1,
				y1,
				0);
		int x2 = r.nextInt(width - 20) + 10;
		int y2 = r.nextInt(height - 20) + 10;
		for (int i = 0; i < 8; i++) {
			x2 = r.nextInt(width - 20) + 10;
			y2 = r.nextInt(height - 20) + 10;
			if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) > 500) {
				break;
			}
		}

		player2 = new AsteroidPlayer(x2,
				y2,
				1);
	}

	private void gameLoop() {
		while (true) {

			// game close:

			if (keys[27]) {
				keys[27] = false;
				System.exit(0);
			}

			// restarting

			if (keys[32]) {
				keys[32] = false;
				respawnPlayers();
				if (score1 >= 5 || score2 >= 5) {
					score1 = 0;
					score2 = 0;
				}
			}

			// fullscreen f11
			if (keys[122]) {
				keys[122] = false;
				if (fullscreenEnabled) {
					fullscreenEnabled = false;
					enterWindowMode();
				} else {
					fullscreenEnabled = true;
					enterFullscreenMode();
				}
			}

			// mute m
			if (keys[77]) {
				keys[77] = false;
				SoundPlayer.muteToggle();
				System.out.println("Mute toggled");
			}

			long currentFrameTime = System.nanoTime();
			deltaTime = (currentFrameTime - pastTime) / 1000000;

			if (deltaTime >= targetDelay) {
				// Update and render game here

				// reduces rumble

				if (getRumble() < 0.01) {
					setRumble(0);
				}

				if (timeSincePlay < 0) {
					playThrust();
					timeSincePlay = 50;
				}
				timeSincePlay -= Asteroids.deltaTime;

				setRumble(getRumble() * 0.8);

				player1.move();
				player2.move();
				for (int i = 0; i < player1.bullets.length; i++) {
					Bullet bullet = player1.bullets[i];
					if (bullet == null || !player2.alive) {
						continue;
					} // stops if null
					if (bullet.collide(player2.boundsRect)) {
						player2.die();
						player1.bullets[i] = null;
						score1++;
					}
				}
				for (int i = 0; i < player2.bullets.length; i++) {
					Bullet bullet = player2.bullets[i];
					if (bullet == null || !player1.alive) {
						continue;
					} // stops if null
					if (bullet.collide(player1.boundsRect)) {
						player1.die();
						player2.bullets[i] = null;
						score2++;
					}
				}

				p1ScoreIndicator.score = score1;
				p2ScoreIndicator.score = score2;

				if (score1 >= 5 || score2 >= 5 && (player1.alive || player2.alive)) {
					player1.die();
					player2.die();
				}

				for (Star s : stars) {
					if (s == null) {
						continue;
					}
					s.move();
				}

				SwingUtilities.invokeLater(this::repaint); // multithreading or something

				pastTime = System.nanoTime();
			}

			// Sleep for a short time to avoid maxing out the CPU
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setPaint(getBackground());
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setFont(new Font("Impact", Font.ITALIC, 40));
		super.paintComponent(g2D);
		// when calling g.drawImage() we can use "this" for the ImageObserver
		// because Component implements the ImageObserver interface, and JPanel
		// extends from Component. So "this" Board instance, as a Component, can
		// react to imageUpdate() events triggered by g.drawImage()
		g2D.setColor(p1Color);

		// apply rumble
		g2D.translate(r.nextDouble() * getRumble(), r.nextDouble() * getRumble());

		// Global Scale
		g2D.scale(scaleFactor, scaleFactor);

		g2D.setColor(p2Color);

		// Draw the stars below everything
		for (Star s : stars) {
			if (s == null) {
				continue;
			}
			s.display(g2D);
		}

		player1.display(g2D);
		player2.display(g2D);

		p1ScoreIndicator.display(g2D);
		p2ScoreIndicator.display(g2D);

		// Win screen
		if (score1 >= 5) {
			g2D.setColor(p1Color);
			g2D.drawString("Player 1 Wins!", width / 2 - 150, height / 2);
		}
		if (score2 >= 5) {
			g2D.setColor(p2Color);
			g2D.drawString("Player 2 Wins!", width / 2 - 150, height / 2);
		}

		// this smoothes out animations on some systems
		Toolkit.getDefaultToolkit().sync();
	}

	static void rumble() {
		setRumble(getRumblemax());
	}

	// Audio
	public static void playThrust() {
		SoundPlayer.playSound(soundPath + "thrust4.wav", true);
	}

	public static void playShoot() {
		SoundPlayer.playSound(soundPath + "laserShoot.wav");
	}

	public static void playExplode() {
		SoundPlayer.playSound(soundPath + "explosion.wav");
	}

	public static void preloadSounds() {
		for (int i = 0; i < 6; i++) {
			SoundPlayer.loadStoppedSound(soundPath + "thrust4.wav");
		}
		SoundPlayer.loadStoppedSound(soundPath + "laserShoot.wav");
		SoundPlayer.loadStoppedSound(soundPath + "explosion.wav");
	}

	public static void createStars() {
		for (int i = 0; i < stars.length; i++) {
			stars[i] = new Star();
		}
	}

	static void redistributeStars() {
		for (Star s : stars) {
			if (s == null) {
				continue;
			}
			s.redistribute();
		}
	}

	static void redistributeStars(int w, int h) {
		for (Star s : stars) {
			if (s == null) {
				continue;
			}
			s.redistribute(w, h);
		}
	}

	static void redistributePlayer(int w, int h) {
		player1.redistribute(w, h);
		player2.redistribute(w, h);
	}

	void enterFullscreenMode() {
		JFrame window = (JFrame) SwingUtilities.getWindowAncestor(this);
		window.dispose();
		window.setUndecorated(true);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setVisible(true);
	}

	void enterWindowMode() {
		JFrame window = (JFrame) SwingUtilities.getWindowAncestor(this);
		window.dispose();
		window.setUndecorated(false);
		window.setExtendedState(JFrame.NORMAL);
		window.setVisible(true);
	}

	void respawnPlayers() {
		int x1 = r.nextInt(width);
		int y1 = r.nextInt(height);

		int x2 = r.nextInt(width);
		int y2 = r.nextInt(height);

		for (int i = 0; i < 15; i++) {
			x2 = r.nextInt(width);
			y2 = r.nextInt(height);
			if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) > 500) {
				break;
			}
		}

		player1.respawn(x1, y1);
		player2.respawn(x2, y2);
	}

	public static boolean rectRect(double rx1, double ry1, int w1, int h1, double rx2, double ry2, int w2, int h2) {
		return rx1 < rx2 + w2 && rx1 + w1 > rx2 && ry1 < ry2 + h2 && ry1 + h1 > ry2;
	}

	// LINE/LINE
	// I made this so long ago
	public static boolean lineLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4,
			double y4) {
		// calculate the direction of the lines
		double uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))
				/ ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
		double uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))
				/ ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
		// angle = atan2((y4-y3)/(x4-x3))

		// if uA and uB are between 0-1, lines are colliding
		if (uA >= 0 && uA < 1 && uB > 0 && uB < 1) {
			return true;
		}
		return false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int code = e.getKeyCode(); // an arrow pressed is read as an integer
		if (code < 227) {
			keys[code] = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int code = e.getKeyCode(); // an arrow pressed is read as an integer
		if (code < 227) {
			keys[code] = false;
		}

	}

	// mouse code
	public void mouseClicked(java.awt.event.MouseEvent mouse) {

		// repaint();
	}

	public void mouseEntered(java.awt.event.MouseEvent mouse) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(java.awt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub
		// your code here
	}

	public void mousePressed(java.awt.event.MouseEvent mouse) {
		// TODO Auto-generated method stub

		// System.out.println(mouse.getX());//display the x and y coordinate of the
		// mouse click
		// System.out.println(mouse.getY());
	}

	public void mouseReleased(java.awt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void mouseMoved(java.awt.event.MouseEvent mouse) { // *****
		// rotationPointX = mouse.getX() - 10;
		// rotationPointY = mouse.getY() - 35;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public static double getRumble() {
		return rumble;
	}

	public static void setRumble(double rumble) {
		Asteroids.rumble = rumble;
	}

	public static double getRumblemax() {
		return rumbleMax;
	}

}
