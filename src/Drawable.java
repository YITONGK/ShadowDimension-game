import bagel.util.Point;
import java.util.ArrayList;
public interface Drawable {
    public void draw(ArrayList<Tree> trees, ArrayList<Sinkhole> sinkholes, Player player,
                     Point topLeft, Point bottomRight);
}
