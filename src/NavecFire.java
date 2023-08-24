import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class NavecFire{
    private double xCenterNavec;
    private double yCenterNavec;
    private double xCenterPlayer;
    private double yCenterPlayer;
    private double xCenter;
    private double yCenter;
    private DrawOptions rotation;
    private double navecHeight = new Image("res/navec/navecLeft.png").getHeight();
    private double navecWidth = new Image("res/navec/navecLeft.png").getWidth();
    private double fireHeight = new Image("res/navec/navecFire.png").getHeight();
    private double fireWidth = new Image("res/navec/navecFire.png").getWidth();
    private Image image;
    private int  damagePoint= 20;
    // below attributes are used to put in setRotation method
    private final double TOP_LEFT = 0;
    private final double BOTTOM_LEFT = Math.PI / 2;
    private final double TOP_RIGHT = Math.PI * 3 / 2;
    private final double BOTTOM_RIGHT = Math.PI;

    public NavecFire(double xCenterNavec, double yCenterNavec, double xCenterPlayer, double yCenterPlayer){
        this.xCenterNavec = xCenterNavec;
        this.yCenterNavec = yCenterNavec;
        this.xCenterPlayer = xCenterPlayer;
        this.yCenterPlayer = yCenterPlayer;
        this.image = new Image("res/navec/navecFire.png");
    }

    // Fire need to be drawn due to the position of player
    public void draw(Player player){
        // if X-P <= X-E and Y-P <= Y-E, fire should be drawn from top-left.
        if(xCenterPlayer <= xCenterNavec && yCenterPlayer <= yCenterNavec){
            rotation = new DrawOptions().setRotation(TOP_LEFT);
            this.xCenter = xCenterNavec - (navecWidth + fireWidth) / 2;
            this.yCenter = yCenterNavec - (navecHeight + fireHeight) / 2;
            image.draw(xCenter, yCenter, rotation);
        }
        // if X-P <= X-E and Y-P >Y-E, fire should be drawn from bottom-left.
        else if(xCenterPlayer <= xCenterNavec && yCenterPlayer > yCenterNavec){
            rotation = new DrawOptions().setRotation(BOTTOM_LEFT);
            this.xCenter = xCenterNavec - (navecWidth + fireWidth) / 2;
            this.yCenter = yCenterNavec + (navecHeight + fireHeight) / 2;
            image.draw(xCenter, yCenter, rotation);
        }
        // if X-P >X-E and Y-P <= Y-E, fire should be drawn from top-right.
        else if(xCenterPlayer > xCenterNavec && yCenterPlayer <= yCenterNavec){
            rotation = new DrawOptions().setRotation(TOP_RIGHT);
            this.xCenter = xCenterNavec + (navecWidth + fireWidth) / 2;
            this.yCenter = yCenterNavec - (navecHeight + fireHeight) / 2;
            image.draw(xCenter, yCenter, rotation);
        }
        // if X-P >X-E and Y-P >Y-E, fire should be drawn from bottom-right.
        else if(xCenterPlayer > xCenterNavec && yCenterPlayer > yCenterNavec){
            rotation = new DrawOptions().setRotation(BOTTOM_RIGHT);
            this.xCenter = xCenterNavec + (navecWidth + fireWidth) / 2;
            this.yCenter = yCenterNavec + (navecHeight + fireHeight) / 2;
            image.draw(xCenter, yCenter, rotation);
        }
        // player touch the fire and player is not invincible, player HP decreases and becomes invincible
        if(collidePlayer(player) && !player.getInvincibleState()){
            player.decreaseHP(damagePoint);
            player.setInvincibleSate(true);
            System.out.println("Navec inflicts 20 damage points on Fae. Fae's current health: " +
                    player.getHP() + "/" + player.getMAX_HP());
        }
    }

    // check whether Fae touch the fire
    public boolean collidePlayer(Player player){
        Rectangle playerRectangle = player.getBoundingBox();
        if(playerRectangle.intersects(this.image.getBoundingBoxAt(new Point(xCenter, yCenter)))){
            return true;
        }
        return false;
    }
}
