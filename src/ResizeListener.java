import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

class ResizeListener implements ComponentListener {

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        // If the window is resized, the width and height of Asteroid is updated
        Dimension newSize = e.getComponent().getSize();

        int oldWidth = Asteroids.width;
        int oldHeight = Asteroids.height;

        Asteroids.trueWidth = newSize.width;
        Asteroids.trueHeight = newSize.height;

        // finds the new scale factor
        Asteroids.scaleFactor = (double) newSize.width / oldWidth;

        Asteroids.height = (int) (newSize.height / Asteroids.scaleFactor);

        // The stars are repositioned
        Asteroids.redistributeStars(oldWidth, oldHeight);
        Asteroids.redistributePlayer(oldWidth, oldHeight);

    }
}