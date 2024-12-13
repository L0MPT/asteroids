import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

class ShipParticle {
	double x;
	double y;
	double xv;
	double yv;
	double size;
	int time;
	int timeOriginal;
	int id;
	Color color;
	ShipParticle[] arrayReference;

	static Ellipse2D circle = new Ellipse2D.Double(-0.5, -0.5, 1, 1);
	static AffineTransform shipParticleTransform = new AffineTransform();

	ShipParticle(double x, double y, double xv, double yv, int size, int time, int id, Color color, ShipParticle[] arrayReference) {
		this.x = x;
		this.y = y;
		this.xv = xv;
		this.yv = yv;
		this.size = size;
		this.time = time;
		this.timeOriginal = time;
		this.id = id;
		this.color = color;
		this.arrayReference = arrayReference;
	}

	public void display(Graphics2D g) {
		g.setColor(color);
		double trueSize = size * time / timeOriginal;

		shipParticleTransform.setToIdentity();
		shipParticleTransform.translate(x, y);
		shipParticleTransform.scale(trueSize, trueSize);
		g.fill(shipParticleTransform.createTransformedShape(circle));


		// Looping screen
		boolean rightOver = this.x + trueSize > Asteroids.width;
		boolean bottomOver = this.y + trueSize > Asteroids.height;
		if (rightOver) {
			shipParticleTransform.translate(-Asteroids.width, 0);
			g.fill(shipParticleTransform.createTransformedShape(circle));
			shipParticleTransform.translate(Asteroids.width, 0);
		}
		if (bottomOver) {
			shipParticleTransform.translate(0, -Asteroids.height);
			g.fill(shipParticleTransform.createTransformedShape(circle));
			shipParticleTransform.translate(0, Asteroids.height);
		}
		if (bottomOver && rightOver) {
			shipParticleTransform.translate(-Asteroids.width, -Asteroids.height);
			g.fill(shipParticleTransform.createTransformedShape(circle));
		}
	}

	public void move() {
		time -= 1;
		// Paricles are deleted here
		if (time <= 0) {
			arrayReference[id] = null;
		}
		x += xv;
		y += yv;

		// Slight Deceleration
		xv *= 0.99;
		yv *= 0.99;

		// Loops the particles across the screen
		x = (x + Asteroids.width) % Asteroids.width;
		y = (y + Asteroids.height) % Asteroids.height;
	}
}