import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class Star {
    float x;
    float y;
    float xv;
    float yv;
    float xvv;
    float yvv;

    static Color[] colors = new Color[50];
    static {
        for (int i = 0; i < colors.length; i++) {
            int b = 255 - i;
            colors[i] = new Color(b, b, b);
        }
    }

    // may remove for performance
    float size;
    int color;
    AffineTransform starTransform = new AffineTransform();
    static Ellipse2D.Float circle = new Ellipse2D.Float(-0.5f, -0.5f, 1, 1);
    public static final float jerk = 0.01f;

    Star() {
        this.x = (float) (Math.random() * Asteroids.width);
        this.y = (float) (Math.random() * Asteroids.height);
        this.xv = (float) (Math.random() * 2 - 1);
        this.yv = (float) (Math.random() * 2 - 1);
        this.size = (float) (Math.random() * 3 + 1);
        this.color = (int) (Math.random() * colors.length);
    }

    public void display(Graphics2D g) {
        g.setColor(colors[color]);
        starTransform.setToIdentity();
        starTransform.translate(x, y);
        starTransform.scale(size, size);
        starTransform.translate(-0.5, -0.5);
        g.fill(starTransform.createTransformedShape(circle));
    }

    public void move() {
        x += xv;
        y += yv;

        // Randomly adds acceleration
        xvv += (float) (Math.random() * jerk * 2 - jerk) * size / 5;
        yvv += (float) (Math.random() * jerk * 2 - jerk) * size / 5;

        xvv *= 0.9;
        yvv *= 0.9;

        xv += xvv;
        yv += yvv;

        // Sligth Deceleration
        xv *= 0.95;
        yv *= 0.95;

        // Clamps velocity to 1
        xv = Math.min(1, Math.max(-1, xv));
        yv = Math.min(1, Math.max(-1, yv));
        // Clamps acceleration
        xvv = Math.min(0.2f, Math.max(-0.2f, xvv));
        yvv = Math.min(0.2f, Math.max(-0.2f, yvv));

        // Looping screen
        x = (x + Asteroids.width) % Asteroids.width;
        y = (y + Asteroids.height) % Asteroids.height;

        colorDrift();
    }

    private void colorDrift() {
        double rand = Math.random();
        if (rand < 0.1) {
            color = Math.min(color + 1, colors.length - 1);
        } else if (rand > 0.9) {
            color = Math.max(color - 1, 0);
        }
    }

    /**
     * Randomly redistributes the star's position within the boundaries of the game screen.
     * The new position is determined by generating random x and y coordinates
     * within the width and height of the game screen.
     * @see Asteroids#redistributeStars()
     */
    public void redistribute() {
        x = (float) (Math.random() * Asteroids.width);
        y = (float) (Math.random() * Asteroids.height);
    }



    public void redistribute(int width, int height) {
        x = x / Asteroids.width * width;
        y = y / Asteroids.height * height;
    }
}
