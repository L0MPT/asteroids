import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

class AsteroidPlayer {
	int width;
	int height;
	double x;
	double y;

	int type;

	final int ammoSize = 7;
	int ammoMax = 2;
	int ammo = ammoMax;

	Random rand = new Random();

	double xv = 0;
	double yv = 0;
	private double speed = 0.2;
	double rotation;
	double rotV;

	double rotSpeed = 0.25;
	double rotDecel = 0.95;

	double timeToNewBullet = 0;
	double fuel = 4000;
	double fuelMax = 4000;

	boolean propulsed = true;

	boolean alive = true;

	Bullet[] bullets = new Bullet[ammo];

	ShipParticle[] particles = new ShipParticle[200];

	DeathParticle[] deathParticles = new DeathParticle[50];

	ShipParticle[] shipShootParticle = new ShipParticle[150];

	Path2D.Double ship = new Path2D.Double();

	AffineTransform objectTransform = new AffineTransform();

	public Rectangle2D boundsRect;

	int xMin, xMax, yMin, yMax;

	AsteroidPlayer(int x, double y, int width, int height, int i) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rotation = rand.nextDouble() * 360;
		this.type = i;

		xMin = (int) this.x;
		xMax = (int) this.x;
		yMin = (int) this.y;
		yMax = (int) this.y;

		createShip();
	}

	private void createShip() {
		ship.moveTo(-1 * this.width / 2, -1 * this.height / 3);
		ship.lineTo(this.width / 2, -1 * this.height / 3);
		ship.lineTo(0, this.height * 2 / 3);
		ship.closePath();
	}

	void respawn(int x, int y) {
		this.x = x;
		this.y = y;
		this.rotation = rand.nextDouble() * 360;
		this.alive = true;
		this.fuel = fuelMax;
		this.ammo = ammoMax;
		this.xv = 0;
		this.yv = 0;
		this.rotV = 0;
		for (int i = 0; i < particles.length; i++) {
			particles[i] = null;
		}
		for (int i = 0; i < deathParticles.length; i++) {
			deathParticles[i] = null;
		}
		for (int i = 0; i < shipShootParticle.length; i++) {
			shipShootParticle[i] = null;
		}
		for (int i = 0; i < bullets.length; i++) {
			bullets[i] = null;
		}
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
				g.setPaint(Asteroids.p1Color);
				break;
			}

			case 1: {
				g.setPaint(Asteroids.p2Color);
				break;
			}

			default:
				break;
		}

		AffineTransform shipTransformation = new AffineTransform();

		shipTransformation.translate(x, y);
		shipTransformation.rotate(Math.toRadians(rotation));

		// bounding box:
		boundsRect = ship.createTransformedShape(shipTransformation).getBounds2D();
		// g.draw(boundsRect);

		// draws the ship 9 times so overflow works
		for (byte i = -1; i <= 1; i++) {
			for (byte j = -1; j <= 1; j++) {
				shipTransformation.setToIdentity();
				shipTransformation.translate(x + i * Asteroids.width, y + j * Asteroids.height);
				shipTransformation.rotate(Math.toRadians(rotation));
				g.fill(ship.createTransformedShape(shipTransformation));
			}
		}

		g.setColor(Color.gray);
		// Ammo Counter
		drawAmmo(g);

		// draws bullets
		for (int i = 0; i < bullets.length; i++) {
			if (bullets[i] != null) {
				bullets[i].display(g);
			}
		}

		// fuel
		drawFuel(g);

		// draws particles

		for (int i = 0; i < particles.length; i++) {
			if (particles[i] != null) {
				particles[i].display(g);
			}
		}
		for (int i = 0; i < shipShootParticle.length; i++) {
			if (shipShootParticle[i] != null) {
				shipShootParticle[i].display(g);
			}
		}
	}

	public void drawAmmo(Graphics2D g) {
		// copies the code for drawing originally because it's basically the same
		// ammoSize / 2 is multiplied by the angle because ovals are drawn stupidly from
		// the top left corner.

		ArrayList<Ellipse2D> circles = new ArrayList<Ellipse2D>();

		double difference = this.width / 2;
		double yOffset = -this.height * 0.45;
		if (this.ammoMax % 2 == 0) {
			for (int i = 1; i <= ammoMax / 2; i++) {
				circles.add(new Ellipse2D.Double((i - 0.5) * difference, yOffset, ammoSize, ammoSize));
				circles.add(new Ellipse2D.Double(-1 * (i - 0.5) * difference, yOffset, ammoSize, ammoSize));
			}
		} else {
			circles.add(new Ellipse2D.Double(0, yOffset, ammoSize, ammoSize));
			for (int i = 1; i <= this.ammoMax / 2; i++) {
				circles.add(new Ellipse2D.Double(i * difference, yOffset, ammoSize, ammoSize));
				circles.add(new Ellipse2D.Double(-1 * i * difference, yOffset, ammoSize, ammoSize));
			}
		}

		AffineTransform bulletTransform = new AffineTransform();

		for (int i = 0; i < circles.size(); i++) {
			bulletTransform.setToIdentity();
			bulletTransform.translate(x, y);
			bulletTransform.rotate(Math.toRadians(rotation));

			// translates the ammo by half of their width so that they are drawn from the
			// center
			bulletTransform.translate(-ammoSize / 2, -ammoSize / 2);
			if (i < ammo) {
				g.fill(bulletTransform.createTransformedShape(circles.get(i)));
			} else {
				g.draw(bulletTransform.createTransformedShape(circles.get(i)));
			}
		}
	}

	public void drawFuel(Graphics2D g) {

		Rectangle2D.Double fuelBack = new Rectangle2D.Double(
				-this.width / 2.0,
				-1 * this.height * (37.0 / 48.0),
				this.width,
				this.height / 8.0);

		Rectangle2D.Double fuelFront = new Rectangle2D.Double(
				(-this.width / 2.0) + this.width / 2 * (1.0 - this.fuel / this.fuelMax),
				-1 * this.height * (37.0 / 48.0),
				this.width * (this.fuel / this.fuelMax),
				this.height / 8.0);

		// increases the stroke
		Stroke strokeOriginal = g.getStroke();

		g.setStroke(new BasicStroke(2.0f));

		g.setColor(new Color(100, 100, 100));

		g.draw(objectTransform.createTransformedShape(fuelBack));

		g.setStroke(strokeOriginal);

		g.setColor(new Color(200, 50, 50));

		if(propulsed) {
			g.setColor(new Color(200, 50, 50));
		} else {
			g.setColor(new Color(200, 120, 120));
		}

		g.fill(objectTransform.createTransformedShape(fuelFront));

	}

	/**
	 * Spawns particles around the ship.
	 */
	public void spawnParticles() {
		Asteroids.rumble();
		fuel -= Asteroids.deltaTime * 2;
		for (int i = 0; i < particles.length; i++) {
			if (particles[i] == null) {
				double pX = (x + (this.height * Math.sin(rotation * Math.PI / 180)) * .9);
				double pY = (y - ((this.height * Math.cos(rotation * Math.PI / 180)) * .9));
				double pXv = xv * -0.3 + rand.nextDouble() * 1.5 - 0.1;
				double pYv = yv * -0.3 + rand.nextDouble() * 1.5 - 0.1;
				int pSize = 20;
				int pTime = 50;
				int pId = i;
				Color pColor = new Color(200 + rand.nextInt(55), rand.nextInt(60), rand.nextInt(60));

				particles[i] = new ShipParticle(pX, pY, pXv, pYv, pSize, pTime, pId, pColor, this.particles);
				break;
			}
		}
	}

	public void die() {
		Asteroids.playExplode();
		alive = false;
		for (int i = 0; i < deathParticles.length; i++) {
			if (deathParticles[i] == null) {
				double pX = (x + (this.height * Math.sin(rotation * Math.PI / 180)) * 2.0 / 3.0);
				double pY = (y - ((this.height * Math.cos(rotation * Math.PI / 180)) * 2.0 / 3.0));
				double pXv = xv * -0.3 + rand.nextDouble() * 6 - 3;
				double pYv = yv * -0.3 + rand.nextDouble() * 6 - 3;
				float pSize = 100;
				int pTime = 200;
				int pId = i;
				Color pColor = new Color(200 + rand.nextInt(55), rand.nextInt(70), rand.nextInt(70));

				deathParticles[i] = new DeathParticle(pX, pY, pXv, pYv, pSize, pTime, pId, pColor, this);
			}
		}
	}

	public void shoot() {

		Asteroids.playShoot();

		timeToNewBullet = 3000;

		int bulletWidth = 20;

		// Could adjust such that it shoots from the tip of the triangle
		double bX = this.x - ((this.height * Math.cos((rotation - 90) * Math.PI / 180)) * 2.0 / 3.0);
		double bY = this.y - ((this.height * Math.sin((rotation - 90) * Math.PI / 180)) * 2.0 / 3.0);

		bullets[ammoMax - ammo] = new Bullet(bX, bY, 10, bulletWidth, this.rotation + 90);
		ammo -= 1;
		int spawned = 0;
		for (int i = 0; i < shipShootParticle.length; i++) {
			if (shipShootParticle[i] == null) {
				double pX = bX;
				double pY = bY;
				double pXv = xv * 0.4 + (rand.nextDouble() - 0.5) * 1.5;
				double pYv = yv * 0.4 + (rand.nextDouble() - 0.5) * 1.5;
				int pSize = 20;
				int pTime = 50;
				int pId = i;
				Color pColor = new Color(200 + rand.nextInt(55), 200 + rand.nextInt(55), 200 + rand.nextInt(55));

				// Xv is negated to make sure the particles go in the right direction
				// I am not sure why it is nescassary
				pXv -= Math.sin(Math.toRadians(rotation));
				pYv += Math.cos(Math.toRadians(rotation));

				shipShootParticle[i] = new ShipParticle(pX, pY, pXv, pYv, pSize, pTime, pId, pColor,
						this.shipShootParticle);
				spawned++;
				if (spawned > 10) {
					break;
				}
			}
		}
	}

	public void move() {

		if (fuel <= 0) {
			propulsed = false;
		}
		if (fuel >= fuelMax) {
			propulsed = true;
		}

		if (!alive) {
			for (int i = 0; i < deathParticles.length; i++) {
				if (deathParticles[i] != null) {
					deathParticles[i].move();
				}
			}
			return;
		}

		timeToNewBullet -= Asteroids.deltaTime;
		if (timeToNewBullet <= 0 && ammo < ammoMax) {
			timeToNewBullet = 3000;
			ammo += 1;
		}

		x = (x + Asteroids.width) % Asteroids.width;
		y = (y + Asteroids.height) % Asteroids.height;

		// particles

		for (int i = 0; i < particles.length; i++) {
			if (particles[i] != null) {
				particles[i].move();
			}
		}
		for (int i = 0; i < shipShootParticle.length; i++) {
			if (shipShootParticle[i] != null) {
				shipShootParticle[i].move();
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

				if (Asteroids.keys[38] && fuel > 0 && propulsed) {
					yv += speed * Math.sin((rotation + 90) * Math.PI / 180);
					xv += speed * Math.cos((rotation + 90) * Math.PI / 180);
					spawnParticles();
				}
				// if (Asteroids.keys[40]) {
				// yv -= speed;
				// }
				if (Asteroids.keys[39]) {
					rotV += rotSpeed;
				}
				if (Asteroids.keys[37]) {
					rotV -= rotSpeed;
				}
				break;
			}
			case (1): {
				if (Asteroids.keys[83] && ammo > 0) {
					// Could adjust such that it shoots from the tip of the triangle
					shoot();
					Asteroids.keys[83] = false;
				}

				if (Asteroids.keys[87] && fuel > 0 && propulsed) {
					yv += speed * Math.sin((rotation + 90) * Math.PI / 180);
					xv += speed * Math.cos((rotation + 90) * Math.PI / 180);
					spawnParticles();
				}
				// if (Asteroids.keys[40]) {
				// yv -= speed;
				// }
				if (Asteroids.keys[68]) {
					rotV += rotSpeed;
				}
				if (Asteroids.keys[65]) {
					rotV -= rotSpeed;
				}
				break;
			}
		}
		x += xv;
		y += yv;
		rotation += rotV;
		rotV *= rotDecel;
		xv *= 0.99;
		yv *= 0.99;

		for (int i = 0; i < bullets.length; i++) {
			if (bullets[i] != null) {
				bullets[i].move();
			}
		}

		fuel = Math.min(fuel + Asteroids.deltaTime / 2, fuelMax);
		fuel = Math.max(fuel, 0);

		objectTransform.setToIdentity();
		objectTransform.translate(x, y);
		objectTransform.rotate(Math.toRadians(rotation));

	}
}