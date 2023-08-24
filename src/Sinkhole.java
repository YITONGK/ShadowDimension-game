/**
 * sinkhole inherited from entity
 *  but has one more attribute which is whether the sinkhole exist on the map
 */
public class Sinkhole extends Entity {
    // since sinkhole may disappear, the condition whether one sinkhole exists can be an attribute
    private boolean existing;

    // construct sinkhole and set the sinkhole to be existing at the beginning
    public Sinkhole(double x, double y, String filename){
        super(x, y, filename);
        this.existing = true;
    }

    public void setExisting(boolean existing){
        this.existing = existing;
    }
    public boolean getExisting(){
        return this.existing;
    }

    // override the draw method in entity, because if the sinkhole disappear, no need to draw
    @Override
    public void draw(){
        if (this.existing){
            super.draw();
        }
    }
}

