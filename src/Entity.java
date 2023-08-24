import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * This class is the element on the game map,
 * will be used as the super class for wall and sinkhole
 */
public class Entity{
    private double x;
    private double y;
    public final Image image;

    // element constructor, pass the image path into it
    public Entity(double x, double y, String filename) {
        this.x = x;
        this.y = y;
        image = new Image(filename);
    }

    // Since getBoundingBox() can only create a rectangle with top-left at (0,0)
    // we resort to another method call getBoundingBoxAt() and pass the center point into it
    // and return the rectangle
    public Rectangle getBoundingBox(){
        double centerX = this.x + image.getWidth() / 2.0;
        double centerY = this.y + image.getHeight() / 2.0;
        Point center = new Point(centerX, centerY);
        return image.getBoundingBoxAt(center);
    }

    // draw the corresponding image
    public void draw(){
        image.drawFromTopLeft(this.x, this.y);
    }
}
