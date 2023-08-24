import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class DemonFire {
    private double xCenterDemon;
    private double yCenterDemon;
    private double xCenterPlayer;
    private double yCenterPlayer;
    private double xCenter;
    private double yCenter;
    private DrawOptions rotation;
    private double demonHeight = new Image("res/demon/demonLeft.png").getHeight();
    private double demonWidth = new Image("res/demon/demonLeft.png").getWidth();
    private double fireHeight = new Image("res/demon/demonFire.png").getHeight();
    private double fireWidth = new Image("res/demon/demonFire.png").getWidth();
    private Image image;
    private int damagePoint = 10;
    // below attributes are used to put in setRotation method
    private final double TOP_LEFT = 0;
    private final double BOTTOM_LEFT = Math.PI / 2;
    private final double TOP_RIGHT = Math.PI * 3 / 2;
    private final double BOTTOM_RIGHT = Math.PI;


    public DemonFire(double xCenterDemon, double yCenterDemon, double xCenterPlayer, double yCenterPlayer) {
        this.xCenterDemon = xCenterDemon;
        this.yCenterDemon = yCenterDemon;
        this.xCenterPlayer = xCenterPlayer;
        this.yCenterPlayer = yCenterPlayer;
        this.image = new Image("res/demon/demonFire.png");
    }

    // Fire need to be drawn due to the position of player
    public void draw(Player player){
        // if X-P <= X-E and Y-P <= Y-E, fire should be drawn from top-left.
        if(xCenterPlayer <= xCenterDemon && yCenterPlayer <= yCenterDemon){
            rotation = new DrawOptions().setRotation(TOP_LEFT);
            this.xCenter = xCenterDemon - (demonWidth + fireWidth) / 2;
            this.yCenter = yCenterDemon - (demonHeight + fireHeight) / 2;
            image.draw(xCenter, yCenter, rotation);
        }
        // if X-P <= X-E and Y-P >Y-E, fire should be drawn from bottom-left.
        else if(xCenterPlayer <= xCenterDemon && yCenterPlayer > yCenterDemon){
            rotation = new DrawOptions().setRotation(BOTTOM_LEFT);
            this.xCenter = xCenterDemon - (demonWidth + fireWidth) / 2;
            this.yCenter = yCenterDemon + (demonHeight + fireHeight) / 2;
            image.draw(xCenter, yCenter, rotation);
        }
        // if X-P >X-E and Y-P <= Y-E, fire should be drawn from top-right.
        else if(xCenterPlayer > xCenterDemon && yCenterPlayer <= yCenterDemon){
            rotation = new DrawOptions().setRotation(TOP_RIGHT);
            this.xCenter = xCenterDemon + (demonWidth + fireWidth) / 2;
            this.yCenter = yCenterDemon - (demonHeight + fireHeight) / 2;
            image.draw(xCenter, yCenter, rotation);
        }
        // if X-P >X-E and Y-P >Y-E, fire should be drawn from bottom-right.
        else if(xCenterPlayer > xCenterDemon && yCenterPlayer > yCenterDemon){
            rotation = new DrawOptions().setRotation(BOTTOM_RIGHT);
            this.xCenter = xCenterDemon + (demonWidth + fireWidth) / 2;
            this.yCenter = yCenterDemon + (demonHeight + fireHeight) / 2;
            image.draw(xCenter, yCenter, rotation);
        }
        // player touch the fire and player is not invincible, player HP decreases and becomes invincible
        if(collidePlayer(player) && !player.getInvincibleState()){
            player.decreaseHP(damagePoint);
            player.setInvincibleSate(true);
            System.out.println("Demon inflicts 10 damage points on Fae. Fae's current health: " +
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
