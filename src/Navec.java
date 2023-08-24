import bagel.Image;
import java.util.Random;
import java.util.ArrayList;
import bagel.util.Point;

public class Navec extends Enemy implements Drawable{

    private final Image NAVEC_LEFT = new Image("res/navec/navecLeft.png");
    private final Image NAVEC_RIGHT = new Image("res/navec/navecRight.png");
    private final Image NAVEC_INVINCIBLE_LEFT = new Image("res/navec/navecInvincibleLeft.png");
    private final Image NAVEC_INVINCIBLE_RIGHT = new Image("res/navec/navecInvincibleRight.png");
    private final int MAX_HP = 80;
    private final int ATTACK_RANGE = 200;
    private NavecFire navecFire;
    private Random random = new Random();


    public Navec(double x, double y){
        super(x, y);
        this.setHP(MAX_HP);
        // when moving direction is up or down, navec face left or right will be randomised
        if(this.getDirection() <= UP){
            if(random.nextBoolean()){
                setCurrentImage(NAVEC_LEFT);
            }
            else{
                setCurrentImage(NAVEC_RIGHT);
            }
        }
        else if(this.getDirection() <= DOWN){
            if(random.nextBoolean()){
                setCurrentImage(NAVEC_LEFT);
            }
            else{
                setCurrentImage(NAVEC_RIGHT);
            }
        }
        // when moving direction is left or right, navec face left or right
        else if(this.getDirection() <= LEFT){
            setCurrentImage(NAVEC_LEFT);
        }
        else{
            setCurrentImage(NAVEC_RIGHT);
        }
    }

    public int getMAX_HP() {
        return MAX_HP;
    }

    // every time after navec move or get attacked, current image should be re decided
    public void decideCurrentImage(){
        // if face left (0.50 < direction <= 0.75), change to invincible_left when invincible
        if(getDirection() > DOWN && getDirection() <= LEFT){
            if(getInvincibleState()){
                setCurrentImage(NAVEC_INVINCIBLE_LEFT);
            }
            else{
                setCurrentImage(NAVEC_LEFT);
            }
        }
        // if face right (0.75 < direction <= 1.00), change to invincible_right when invincible
        else if(getDirection() <= RIGHT){
            if(getInvincibleState()){
                setCurrentImage(NAVEC_INVINCIBLE_RIGHT);
            }
            else{
                setCurrentImage(NAVEC_RIGHT);
            }
        }
    }

    // implement Drawable interface
    public void draw(ArrayList<Tree> trees, ArrayList<Sinkhole> sinkholes, Player player,
                     Point topLeft, Point bottomRight) {

        double xCenterNavec = getX() + getCurrentImage().getWidth() / 2;
        double yCenterNavec = getY() + getCurrentImage().getHeight() / 2;
        double xCenterPlayer = player.getX() + player.getCurrentImage().getWidth() / 2;
        double yCenterPlayer = player.getY() + player.getCurrentImage().getHeight() / 2;

        // since navec will never be passive, so call move method all the cases
        move();
        decideCurrentImage();
        // If navec collides with a sinkhole, tree or
        // reaches the level boundary, it will rebound and move in the opposite direction.
        if(whetherCollides(trees, sinkholes) || Outside(this, topLeft, bottomRight)){
            changeDirection();
        }
        // navec does not die and player come into the attack range, navec emits fire
        if(inAttackRange(player) && !Dead()){
            navecFire = new NavecFire(xCenterNavec, yCenterNavec, xCenterPlayer, yCenterPlayer);
            navecFire.draw(player);
        }
        // when invincible is true, invincible state starts timing
        if(getInvincibleState()){
            setInvincibleTiming(getInvincibleTiming() + 1);
        }
        // since update 60 times per seconds, when invincibleTiming reaches 180, 3 seconds passed
        if(getInvincibleTiming() * SECOND_TO_MILLISECOND / REFRESH_RATE > INVINCIBLE_TIME){
            setInvincibleState(false);
            setInvincibleTiming(0);
        }
        // still alive, draw the navec
        if(!Dead()){
            getCurrentImage().drawFromTopLeft(getX(), getY());
            drawHealthBar();
        }
    }

    // method distanceTo in Point class is useful for calculating the distance from center_enemy to center_player
    public boolean inAttackRange(Player player){
        double xCenterNavec = getX() + getCurrentImage().getWidth() / 2;
        double yCenterNavec = getY() + getCurrentImage().getHeight() / 2;
        double xCenterPlayer = player.getX() + player.getCurrentImage().getWidth() / 2;
        double yCenterPlayer = player.getY() + player.getCurrentImage().getHeight() / 2;
        Point centerNavec = new Point(xCenterNavec, yCenterNavec);
        Point centerPlayer = new Point(xCenterPlayer, yCenterPlayer);
        return centerNavec.distanceTo(centerPlayer) < ATTACK_RANGE;
    }

    // draw health bar at pre-determined position (x, y - 6), with corresponding colour
    public void drawHealthBar(){
        double percentageHP = ((double)getHP() / MAX_HP) * 100;
        setCOLOUR(percentageHP);
        ENEMY_HP_FONT.drawString(Math.round(percentageHP) + "%", getX(), getY() - 6, getCOLOUR());
    }
}
