import java.awt.Color;
import java.awt.Graphics2D;

class Bullet {
	double x;
	double y;
	int width;
	int height;
	double velocity;
	double rotation;

	int xMin, xMax, yMin, yMax;

	Bullet(double x, double y, double velocity, int width, double rotation) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = width / 2;
		this.velocity = velocity;
		this.rotation = rotation;
		this.y -= this.height / 2;
		xMin = (int) this.x;
		xMax = (int) this.x;
		yMin = (int) this.y;
		yMax = (int) this.y;
	}

	public void display(Graphics2D g) {
		g.setColor(Color.white);

		int[] x1 = { (int) (this.x), (int) this.x + this.width, (int) (this.x + this.width), (int) (this.x) };

		int[] y1 = { (int) (this.y), (int) (this.y), (int) (this.y + this.height), (int) (this.y + this.height) };
		int[] x2 = new int[4];
		int[] y2 = new int[4];

		// first adjusts the values of the original
		for (int i = 0; i < x1.length; i++) {
			x1[i] += -this.x;
			y1[i] += -this.y;
		}

		// Rotates. I made these the same for neatness
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

		g.fillPolygon(x2, y2, 4);

	}

	public void move() {
		this.x += this.velocity * Math.cos(this.rotation * Math.PI / 180);
		this.y += this.velocity * Math.sin(this.rotation * Math.PI / 180);

	}

	public boolean collide(int tX, int tY, int tW, int tH) {
		return Asteroids.rectRect(this.xMin, this.yMin, this.xMax - this.xMin, this.yMax - this.yMin, tX, tY, tW, tH);
	}
}