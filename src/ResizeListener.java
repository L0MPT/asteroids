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
        Asteroids.width = newSize.width;
        Asteroids.height = newSize.height;

        // The stars are repositioned
        Asteroids.redistributeStars();
    }
}