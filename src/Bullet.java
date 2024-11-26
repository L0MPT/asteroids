import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;

class Bullet {
	double x;
	double y;
	int width;
	int height;
	double velocity;
	double rotation;

	Shape bulletShape;

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

		AffineTransform bulletTransform = new AffineTransform();

		Rectangle2D.Double bullet = new Rectangle2D.Double(0, 0, width, height);

		bulletTransform.setToIdentity();
		bulletTransform.translate(x, y);
		bulletTransform.rotate(Math.toRadians(rotation));
		bulletTransform.translate(-width / 2, -height / 2);
		bulletTransform.translate(height, 0);

		bulletShape = bulletTransform.createTransformedShape(bullet);

		g.fill(bulletShape);

		Rectangle bounds = bulletShape.getBounds();
		xMin = bounds.x;
		xMax = bounds.x + bounds.width;
		yMin = bounds.y;
		yMax = bounds.y + bounds.height;

	}

	public void move() {
		x += velocity * Math.cos(Math.toRadians(rotation));
		y += velocity * Math.sin(Math.toRadians(rotation));

	}

	/**
	 * Checks if the bullet collides with the given rectangular bounds. This method
	 * uses the {@code rectRect} method of the {@code Asteroids} class.
	 *
	 * @deprecated This method is deprecated and should not be used. Use the other
	 *             {@code collide} methods instead.
	 * @param tX the x-coordinate of the top-left corner of the rectangular bounds
	 * @param tY the y-coordinate of the top-left corner of the rectangular bounds
	 * @param tW the width of the rectangular bounds
	 * @param tH the height of the rectangular bounds
	 * @return {@code true} if the bullet collides with the given rectangular
	 *         bounds,
	 *         {@code false} otherwise
	 */

	public boolean collide(int tX, int tY, int tW, int tH) {
		return Asteroids.rectRect(xMin, yMin, xMax - xMin, yMax - yMin, tX, tY, tW, tH);
	}

	/**
	 * Checks if the bullet's shape collides with the given rectangular bounds. This
	 * method uses the {@code intersects} method of the {@code Shape} class of the
	 * Bullet.
	 *
	 * @param bounds the {@code Rectangle2D} bounds to check for collision
	 * @return {@code true} if the bullet's shape intersects with the given bounds,
	 *         {@code false} otherwise
	 */
	public boolean collide(Rectangle2D bounds) {
		if (bulletShape == null) {
			return false;
		} else {
			return bulletShape.intersects(bounds);
		}
	}

	/**
	 * Checks if the bullet's shape collides with the given rectangular bounds. This
	 * method uses the {@code intersects} method of the {@code Shape} class of the
	 * Bullet.
	 *
	 * @param bounds the {@code Rectangle} bounds to check for collision
	 * @return {@code true} if the bullet's shape intersects with the given bounds,
	 *         {@code false} otherwise
	 */
	public boolean collide(Rectangle bounds) {
		if (bulletShape == null)
			return false;
		return bulletShape.intersects(bounds);
	}

	/**
	 * Checks if the bullet collides with the given shape. This method uses the
	 * {@code intersects} method of the {@code Shape} class. Additionally, this
	 * method uses the {@code Shape} class as a parameter to better represent the
	 * collision with a larger inputted shape.
	 *
	 * @param shape the shape to check for collision with the bullet
	 * @return {@code true} if the bullet collides with the given shape,
	 *         {@code false} otherwise
	 */
	public boolean collide(Shape shape) {
		if (bulletShape == null)
			return false;
		return shape.intersects(bulletShape.getBounds2D());
	}
}