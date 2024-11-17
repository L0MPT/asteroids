import java.awt.Color;
import java.awt.Graphics2D;

class ShipParticle {
	double x;
	double y;
	double xv;
	double yv;
	int size;
	int time;
	int timeOriginal;
	int id;
	Color color;
	AsteroidPlayer parentPlayer;

	ShipParticle(double x, double y, double xv, double yv, int size, int time, int id, Color color,
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
		g.setColor(this.color);
		int trueSize = (int) (this.size * (this.time / (double) this.timeOriginal));
		g.fillOval((int) this.x - trueSize / 2, (int) this.y - trueSize / 2, trueSize, trueSize);

		// Looping screen
		// I don't think I need any of the other ones since this is aligned to the top
		// left point.
		boolean rightOver = this.x + trueSize > Asteroids.width;
		boolean leftOver = this.y + trueSize > Asteroids.height;
		if (rightOver) {
			g.fillOval((int) this.x - trueSize / 2 - Asteroids.width, (int) this.y - trueSize / 2, trueSize, trueSize);
		}
		if (leftOver) {
			g.fillOval((int) this.x - trueSize / 2, (int) this.y - trueSize / 2 - Asteroids.height, trueSize, trueSize);
		}
		if (leftOver && rightOver) {
			g.fillOval((int) this.x - trueSize / 2 - Asteroids.width, (int) this.y - trueSize / 2 - Asteroids.height,
					trueSize, trueSize);
		}
	}

	public void move() {
		this.time -= 1;
		// Paricles are deleted here
		if (this.time <= 0) {
			this.parentPlayer.particles[id] = null;
		}
		this.x += this.xv;
		this.y += this.yv;
		this.xv *= 0.99;
		this.yv *= 0.99;
		this.x = (this.x + Asteroids.width) % Asteroids.width;
		this.y = (this.y + Asteroids.height) % Asteroids.height;
	}
}