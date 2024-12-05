import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;

public class ScoreIndicator {
    int score;
    Color color;
    boolean horizontalSide;
    boolean verticalSide;
    boolean direction;
    int scoreMax;

    static final int size = 20;
    static final double spacing = 1.4;

    Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, 1, 1);
    AffineTransform aT = new AffineTransform();

    ScoreIndicator(int score, Color color, boolean horizontalSide, boolean verticalSide, int scoreMax) {
        this.score = score;
        this.color = color;
        this.horizontalSide = horizontalSide;
        this.verticalSide = verticalSide;
        this.scoreMax = scoreMax;
    }

    public void display(Graphics2D g) {
        // Display the score
        g.setColor(color);
        
        double x = 0;
        double y = 0;
        if(!horizontalSide) {
            x = size;
        } else {
            x = Asteroids.width - size;
        }
        if(!verticalSide) {
            y = size;
        } else {
            y = Asteroids.height - size;
        }

        aT.setToIdentity();
        aT.translate(x, y);
        aT.scale(size, size);
        aT.translate(-0.5, -0.5);


        for(int i = 0; i < scoreMax; i++) {
            if(i < score) {
                g.fill(aT.createTransformedShape(circle));
            } else {
                g.draw(aT.createTransformedShape(circle));
            }
            // at the end so the first is not translated
            if(!horizontalSide) {
                aT.translate(spacing, 0);
            } else {
                aT.translate(-spacing, 0);
            }
        }
    }
}
