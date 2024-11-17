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
	private final static double rumbleMax = 5;

	double timeSincePlay = 0;

	Random r = new Random();

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	final static int width = (int) screenSize.getWidth();;
	final static int height = (int) screenSize.getHeight();;
	static boolean[] keys = new boolean[227];

	static int rotation = 50;

	static int x = 100;
	static int y = 100;
	static int rectWidth = 15 * 2;
	static int rectHeight = 20 * 2;

	static double rotV = 0;

	static String enteredRot = "";

	AsteroidPlayer player1;
	AsteroidPlayer player2;

	int score1 = 0;
	int score2 = 0;

	static double deltaTime;

	long pastTime = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
		window.setResizable(false);
		window.setUndecorated(true);

		// fit the window size around the components (just our jpanel).
		// pack() should be called after setResizable() to avoid issues on some
		// platforms
		window.pack();
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

		new Thread(this::gameLoop).start();
	}

	public void createPlayers() {
		player1 = new AsteroidPlayer(r.nextInt(width - 20) + 10,
				r.nextInt(height - 20) + 10,
				rectWidth,
				rectHeight,
				0);
		player2 = new AsteroidPlayer(r.nextInt(width - 20) + 10,
				r.nextInt(height - 20) + 10,
				rectWidth,
				rectHeight,
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
				createPlayers();
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
					if (bullet.collide(player2.xMin, player2.yMin, player2.xMax - player2.xMin,
							player2.yMax - player2.yMin)) {
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
					if (bullet.collide(player1.xMin, player1.yMin, player1.xMax - player1.xMin,
							player1.yMax - player1.yMin)) {
						player1.die();
						player2.bullets[i] = null;
						score2++;
					}
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
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setFont(new Font("Impact", Font.BOLD, 40));
		super.paintComponent(g2D);
		// when calling g.drawImage() we can use "this" for the ImageObserver
		// because Component implements the ImageObserver interface, and JPanel
		// extends from Component. So "this" Board instance, as a Component, can
		// react to imageUpdate() events triggered by g.drawImage()
		g2D.setColor(Color.blue);

		g2D.translate(r.nextDouble() * getRumble(), r.nextDouble() * getRumble());
		// drawOriginal(g2D);

		drawRot(g2D);
		drawPoint(g2D);

		g2D.drawString(String.valueOf(score1), 10, 50);
		g2D.setColor(Color.green);
		g2D.drawString(String.valueOf(score2), width - 50, 50);

		player1.display(g2D);
		player2.display(g2D);

		// this smoothes out animations on some systems
		Toolkit.getDefaultToolkit().sync();
	}

	private void drawRot(Graphics2D g) {
		// TODO Auto-generated method stub
		g.drawString(enteredRot, 20, 50);
	}

	private void drawPoint(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	static void rumble() {
		// TODO: Sound
		setRumble(getRumblemax());
	}

	// Audio
	public static void playThrust() {
		SoundPlayer.playSound("Asteroids/thrust4.wav", true);
	}

	public static void playShoot() {
		SoundPlayer.playSound("Asteroids/laserShoot.wav", false);
	}

	public static void playExplode() {
		SoundPlayer.playSound("Asteroids/explosion.wav", false);
	}

	public static void preloadSounds() {
		for (int i = 0; i < 6; i++) {
			SoundPlayer.loadStoppedSound("Asteroids/thrust4.wav");
		}
	}

	// Draws
	// Not Called
	public void drawOriginal(Graphics2D g) {
		int[] x1 = { x - rectWidth / 2, x + rectWidth / 2, x };
		int[] y1 = { y - rectHeight / 3, y - rectHeight / 3, y + rectHeight * 2 / 3 };
		g.drawPolygon(x1, y1, 3);
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
