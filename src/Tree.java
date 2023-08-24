/**
 * Since the trees, unlike sinkholes, will not disappear,
 * we just let this wall class inherited from entity,
 * and nothing more is needed to be implemented.
 */
public class Tree extends Entity {
    public Tree(double x, double y, String filename){
        super(x, y, filename);
    }
}
