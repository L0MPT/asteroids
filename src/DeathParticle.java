import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

class DeathParticle {
	double x;
	double y;
	double xv;
	double yv;
	float size;
	int time;
	int timeOriginal;
	int id;
	Color color;
	AsteroidPlayer parentPlayer;

	static AffineTransform deathTransform = new AffineTransform();
	static Ellipse2D.Double circle = new Ellipse2D.Double(-0.5, -0.5, 1, 1);


	DeathParticle(double x, double y, double xv, double yv, float size, int time, int id, Color color,
			AsteroidPlayer parentPlayer) {
		this.x = x;
		this.y = y;
		this.xv = xv;
		this.yv = yv;
		this.size = size;
		this.time = time;
		this.timeOriginal = time;
		this.id = id;
		this.color = color;
		this.parentPlayer = parentPlayer;
	}

	public void display(Graphics2D g) {
		this.color = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(),
				(int) Math.max(Math.min(255 * (this.time / (double) this.timeOriginal), 255), 0));
		g.setColor(this.color);

		float trueSize = 5 + size * (timeOriginal - time) / this.timeOriginal;

		deathTransform.setToIdentity();
		deathTransform.translate(x, y);

		deathTransform.scale(trueSize, trueSize);

		g.fill(deathTransform.createTransformedShape(circle));

	}

	public void move() {
		this.time -= 1;
		// Paricles are deleted here
		if (this.time <= 0) {
			this.parentPlayer.particles[id] = null;
		}
		this.x += this.xv;
		this.y += this.yv;
	}
}