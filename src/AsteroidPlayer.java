import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class AsteroidPlayer {
	int width;
	int height;
	double x;
	double y;

	int type;

	final int ammoSize = 7;
	int ammoMax = 3;
	int ammo = ammoMax;

	Random rand = new Random();

	double xv = 0;
	double yv = 0;
	private double speed = 0.2;
	double rotation;
	double rotV;

	double timeToNewBullet = 0;
	double fuel = 4000;
	double fuelMax = 4000;

	boolean propulsed = true;

	boolean alive = true;

	Bullet[] bullets = new Bullet[ammo];

	ShipParticle[] particles = new ShipParticle[200];

	DeathParticle[] deathParticles = new DeathParticle[50];

	ShootParticle[] shootParticles = new ShootParticle[150];

	int xMin, xMax, yMin, yMax;

	AsteroidPlayer(int x, int y, int width, int height, int i) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = i;

		xMin = (int) this.x;
		xMax = (int) this.x;
		yMin = (int) this.y;
		yMax = (int) this.y;
	}

	public void display(Graphics2D g) {
		if (!alive) {
			// draws particles
			for (int i = 0; i < deathParticles.length; i++) {
				if (deathParticles[i] != null) {
					deathParticles[i].display(g);
				}
			}
			return;
		}

		// sets color:
		// double fuelPercent = (fuel / fuelMax);
		switch (type) {
			case 0: {
				float[] hsb = Color.RGBtoHSB(0, 0, 255, null);
				g.setColor(new Color(Color.HSBtoRGB(hsb[0], (float) (hsb[1]), (float) (hsb[2]))));
				break;
			}

			case 1: {
				float[] hsb = Color.RGBtoHSB(0, 255, 0, null);
				g.setColor(new Color(Color.HSBtoRGB(hsb[0], (float) (hsb[1]), (float) (hsb[2]))));
				break;
			}

			default:
				break;
		}

		int[] x1 = { (int) (this.x - this.width / 2), (int) (this.x + this.width / 2), (int) this.x };
		int[] y1 = { (int) (this.y - this.height / 3), (int) (this.y - this.height / 3),
				(int) (this.y + this.height * 2 / 3) };
		int[] x2 = new int[3];
		int[] y2 = new int[3];

		// first adjusts the values of the original
		for (int i = 0; i < x1.length; i++) {
			x1[i] += -this.x;
			y1[i] += -this.y;
		}

		// Then changes it for
		for (int i = 0; i < x1.length; i++) {
			// x2 starts as 0
			x2[i] = (int) (x1[i] * Math.cos(this.rotation * Math.PI / 180)
					- (y1[i] * Math.sin(this.rotation * Math.PI / 180)));
			x2[i] += this.x;
			// y2 starts at 0
			y2[i] = (int) (y1[i] * Math.cos(this.rotation * Math.PI / 180)
					+ x1[i] * Math.sin(this.rotation * Math.PI / 180));
			y2[i] += this.y;
		}

		g.fillPolygon(x2, y2, 3);

		// bounding box:
		xMin = x2[0];
		xMax = x2[0];
		yMin = y2[0];
		yMax = y2[0];
		for (int i = 1; i < x2.length; i++) {
			if (x2[i] < xMin) {
				xMin = x2[i];
			}
			if (x2[i] > xMax) {
				xMax = x2[i];
			}
			if (y2[i] < yMin) {
				yMin = y2[i];
			}
			if (y2[i] > yMax) {
				yMax = y2[i];
			}
		}

		// g.drawRect(xMin, yMin, xMax - xMin, yMax - yMin);

		// these functions modify x2 so these are deletas
		repeatDraw(x2, y2, g, 1, 1);
		repeatDraw(x2, y2, g, 0, -1);
		repeatDraw(x2, y2, g, 0, -1);
		repeatDraw(x2, y2, g, -1, 0);
		repeatDraw(x2, y2, g, -1, 0);
		repeatDraw(x2, y2, g, 0, 1);
		repeatDraw(x2, y2, g, 0, 1);
		repeatDraw(x2, y2, g, 1, 0);

		g.setColor(Color.gray);
		// Ammo Counter
		this.drawAmmo(g);

		// draws bullets
		for (int i = 0; i < bullets.length; i++) {
			if (bullets[i] != null) {
				bullets[i].display(g);
			}
		}

		// fuel
		this.drawFuelBack(g);
		this.drawFuelFront(g);

		// draws particles

		for (int i = 0; i < particles.length; i++) {
			if (particles[i] != null) {
				particles[i].display(g);
			}
		}
		for (int i = 0; i < shootParticles.length; i++) {
			if (shootParticles[i] != null) {
				shootParticles[i].display(g);
			}
		}
	}

	public void repeatDraw(int[] x2, int[] y2, Graphics2D g, int widthMult, int heightMult) {
		// overflow:
		// to properly make position overflow work, i have to make 8 copies of the
		// object. Start at top left and move clockwise
		for (int i = 0; i < x2.length; i++) {
			x2[i] += Asteroids.width * widthMult;
			y2[i] += Asteroids.height * heightMult;
		}
		g.fillPolygon(x2, y2, 3);
	}

	public void drawAmmo(Graphics2D g) {
		// copies the code for drawing originally because it's basically the same
		// ammoSize / 2 is multiplied by the angle because ovals are drawn stupidly from
		// the top left corner.
		ArrayList<Double> x0 = new ArrayList<>();
		double difference = this.width / 2;
		if (this.ammoMax % 2 == 0) {
			for (int i = 1; i <= this.ammoMax / 2; i++) {
				x0.add(this.x + ((i - 0.5) * difference));
				x0.add(this.x - ((i - 0.5) * difference));
			}
		} else {
			x0.add(this.x);
			for (int i = 1; i <= this.ammoMax / 2; i++) {
				x0.add(this.x + (i * difference));
				x0.add(this.x - (i * difference));
			}
		}

		Collections.sort(x0);

		ArrayList<Double> y0 = new ArrayList<>();
		double y = (this.y - this.height * 0.45);

		for (int i = 0; i < this.ammoMax; i++) {
			y0.add(y);
		}

		int[] x2 = new int[x0.size()];
		int[] y2 = new int[y0.size()];

		// first adjusts the values of the original
		for (int i = 0; i < x0.size(); i++) {
			x0.set(i, x0.get(i) - this.x);
			y0.set(i, y0.get(i) - this.y);
		}

		// Rotates I made these the same for neatness
		for (int i = 0; i < x0.size(); i++) {
			// x2 starts as 0
			x2[i] = (int) (x0.get(i) * Math.cos(this.rotation * Math.PI / 180)
					- (y0.get(i) * Math.sin(this.rotation * Math.PI / 180)));
			x2[i] += this.x - this.ammoSize / 2;
			// y2 starts at 0
			y2[i] = (int) (y0.get(i) * Math.cos(this.rotation * Math.PI / 180)
					+ x0.get(i) * Math.sin(this.rotation * Math.PI / 180));
			y2[i] += this.y - this.ammoSize / 2;
		}

		// Fills the ovals
		for (int i = 0; i < y2.length && i < x2.length; i++) {
			if (ammo > i)
				g.fillOval(x2[i], y2[i], this.ammoSize, this.ammoSize);
			else
				g.drawOval(x2[i], y2[i], this.ammoSize, this.ammoSize);
		}
	}

	private int[][] translatePoints(double xPos0, double xPos1, double yPos0, double yPos1) {
		double[] x0 = { xPos0, xPos1, xPos1, xPos0 };
		double[] y0 = { yPos0, yPos0, yPos1, yPos1 };
		double[] x1 = new double[4];
		double[] y1 = new double[4];

		for (int i = 0; i < x1.length; i++) {
			x0[i] += -this.x;
			y0[i] += -this.y;
		}

		for (int i = 0; i < x0.length; i++) {
			// x2 starts as 0
			x1[i] = (x0[i] * Math.cos(this.rotation * Math.PI / 180)
					- (y0[i] * Math.sin(this.rotation * Math.PI / 180)));
			x1[i] += this.x;
			// y2 starts at 0

			y1[i] = (x0[i] * Math.sin(this.rotation * Math.PI / 180)
					+ y0[i] * Math.cos(this.rotation * Math.PI / 180));
			y1[i] += this.y;
		}

		int[] x2 = new int[4];
		int[] y2 = new int[4];

		for (int i = 0; i < x1.length; i++) {
			x2[i] = (int) x1[i];
			y2[i] = (int) y1[i];
		}

		// oh boy do I miss my touples
		int[][] points = { x2, y2 };

		return points;
	}

	public void drawFuelBack(Graphics2D g) {

		double xPos0 = this.x - this.width / 2;
		double xPos1 = this.x + this.width / 2;
		double yPos0 = this.y - this.height * (16 / 24.0);
		double yPos1 = this.y - this.height * (19 / 24.0);

		int[][] points = translatePoints(xPos0, xPos1, yPos0, yPos1);

		int[] x2 = points[0];
		int[] y2 = points[1];

		g.setColor(new Color(50, 50, 50));

		g.drawPolygon(x2, y2, 4);

	}

	public void drawFuelFront(Graphics2D g) {

		double xPos0 = this.x - (this.width / 2) * this.fuel / this.fuelMax;
		double xPos1 = this.x + (this.width / 2) * this.fuel / this.fuelMax;
		double yPos0 = this.y - this.height * (16 / 24.0);
		double yPos1 = this.y - this.height * (19 / 24.0);

		int[][] points = translatePoints(xPos0, xPos1, yPos0, yPos1);

		int[] x2 = points[0];
		int[] y2 = points[1];

		g.setColor(new Color(200, 50, 50));

		g.fillPolygon(x2, y2, 4);

	}

	/**
	 * Spawns particles around the ship.
	 */
	public void spawnParticles() {
		Asteroids.rumble();
		this.fuel -= Asteroids.deltaTime * 2;
		for (int i = 0; i < particles.length; i++) {
			if (particles[i] == null) {
				double pX = (this.x + (this.height * Math.sin(rotation * Math.PI / 180)) * .9);
				double pY = (this.y - ((this.height * Math.cos(rotation * Math.PI / 180)) * .9));
				double pXv = this.xv * -0.3 + rand.nextDouble() * 1.5 - 0.1;
				double pYv = this.yv * -0.3 + rand.nextDouble() * 1.5 - 0.1;
				int pSize = 20;
				int pTime = 50;
				int pId = i;
				Color pColor = new Color(200 + rand.nextInt(55), rand.nextInt(60), rand.nextInt(60));

				particles[i] = new ShipParticle(pX, pY, pXv, pYv, pSize, pTime, pId, pColor, this);
				break;
			}
		}
	}

	public void die() {
		Asteroids.playExplode();
		this.alive = false;
		for (int i = 0; i < deathParticles.length; i++) {
			if (deathParticles[i] == null) {
				double pX = (this.x + (this.height * Math.sin(rotation * Math.PI / 180)) * 2.0 / 3.0);
				double pY = (this.y - ((this.height * Math.cos(rotation * Math.PI / 180)) * 2.0 / 3.0));
				double pXv = this.xv * -0.3 + rand.nextDouble() * 6 - 3;
				double pYv = this.yv * -0.3 + rand.nextDouble() * 6 - 3;
				int pSize = 100;
				int pTime = 200;
				int pId = i;
				Color pColor = new Color(200 + rand.nextInt(55), rand.nextInt(70), rand.nextInt(70));

				deathParticles[i] = new DeathParticle(pX, pY, pXv, pYv, pSize, pTime, pId, pColor, this);
			}
		}
	}

	public void shoot() {

		Asteroids.playShoot();

		this.timeToNewBullet = 3000;

		int bulletWidth = 20;

		// Could adjust such that it shoots from the tip of the triangle
		double bX = this.x - ((this.height * Math.cos((rotation - 90) * Math.PI / 180)) * 2.0 / 3.0);
		double bY = this.y - ((this.height * Math.sin((rotation - 90) * Math.PI / 180)) * 2.0 / 3.0);

		bullets[ammoMax - ammo] = new Bullet(bX, bY, 10, bulletWidth, this.rotation + 90);
		ammo -= 1;
		int spawned = 0;
		for (int i = 0; i < shootParticles.length; i++) {
			if (shootParticles[i] == null) {
				double pX = bX;
				double pY = bY;
				double pXv = this.xv * 0.4 + rand.nextDouble() * 1.5 - 0.1;
				double pYv = this.yv * 0.4 + rand.nextDouble() * 1.5 - 0.1;
				int pSize = 20;
				int pTime = 50;
				int pId = i;
				Color pColor = new Color(200 + rand.nextInt(55), 200 + rand.nextInt(55), 200 + rand.nextInt(55));

				shootParticles[i] = new ShootParticle(pX, pY, pXv, pYv, pSize, pTime, pId, pColor, this);
				spawned++;
				if (spawned > 10) {
					break;
				}
			}
		}
	}

	public void move() {

		if (this.fuel <= 0) {
			this.propulsed = false;
		}
		if (this.fuel >= this.fuelMax) {
			this.propulsed = true;
		}

		if (!alive) {
			for (int i = 0; i < deathParticles.length; i++) {
				if (deathParticles[i] != null) {
					deathParticles[i].move();
				}
			}
			return;
		}

		this.timeToNewBullet -= Asteroids.deltaTime;
		if (this.timeToNewBullet <= 0 && ammo < ammoMax) {
			this.timeToNewBullet = 3000;
			ammo += 1;
		}

		this.x = (this.x + Asteroids.width) % Asteroids.width;
		this.y = (this.y + Asteroids.height) % Asteroids.height;

		// particles

		for (int i = 0; i < particles.length; i++) {
			if (particles[i] != null) {
				particles[i].move();
			}
		}
		for (int i = 0; i < shootParticles.length; i++) {
			if (shootParticles[i] != null) {
				shootParticles[i].move();
			}
		}

		switch (type) {
			// Down Arrow
			case (0): {
				if (Asteroids.keys[40] && ammo > 0) {
					// Could adjust such that it shoots from the tip of the triangle
					shoot();
					Asteroids.keys[40] = false;
				}

				if (Asteroids.keys[38] && this.fuel > 0 && this.propulsed) {
					this.yv += speed * Math.sin((this.rotation + 90) * Math.PI / 180);
					this.xv += speed * Math.cos((this.rotation + 90) * Math.PI / 180);
					this.spawnParticles();
				}
				// if (Asteroids.keys[40]) {
				// this.yv -= speed;
				// }
				if (Asteroids.keys[39]) {
					this.rotV += speed;
				}
				if (Asteroids.keys[37]) {
					this.rotV -= speed;
				}
				break;
			}
			case (1): {
				if (Asteroids.keys[83] && ammo > 0) {
					// Could adjust such that it shoots from the tip of the triangle
					shoot();
					Asteroids.keys[83] = false;
				}

				if (Asteroids.keys[87] && fuel > 0 && this.propulsed) {
					this.yv += speed * Math.sin((this.rotation + 90) * Math.PI / 180);
					this.xv += speed * Math.cos((this.rotation + 90) * Math.PI / 180);
					this.spawnParticles();
				}
				// if (Asteroids.keys[40]) {
				// this.yv -= speed;
				// }
				if (Asteroids.keys[68]) {
					this.rotV += speed;
				}
				if (Asteroids.keys[65]) {
					this.rotV -= speed;
				}
				break;
			}
		}
		this.x += this.xv;
		this.y += this.yv;
		this.rotation += this.rotV;
		this.rotV *= 0.98;
		this.xv *= 0.99;
		this.yv *= 0.99;

		for (int i = 0; i < bullets.length; i++) {
			if (bullets[i] != null) {
				bullets[i].move();
			}
		}

		this.fuel = Math.min(this.fuel + Asteroids.deltaTime / 2, this.fuelMax);
		this.fuel = Math.max(this.fuel, 0);

	}
}