import bagel.Image;
import java.util.Random;
import java.util.ArrayList;
import bagel.util.Point;

public class Demon extends Enemy implements Drawable{
    private final Image DEMON_LEFT = new Image("res/demon/demonLeft.png");
    private final Image DEMON_RIGHT = new Image("res/demon/demonRight.png");
    private final Image DEMON_INVINCIBLE_LEFT = new Image("res/demon/demonInvincibleLeft.png");
    private final Image DEMON_INVINCIBLE_RIGHT = new Image("res/demon/demonInvincibleRight.png");
    private final int MAX_HP = 40;
    private final int ATTACK_RANGE = 150;
    private DemonFire demonFire;
    private Random random = new Random();

    public Demon(double x, double y){
        super(x, y);
        this.setHP(MAX_HP);
        // whether aggressive or passive should be randomised
        this.setAggressive(random.nextBoolean());
        // when moving direction is up or down, demon face left or right will also be randomised
        if(this.getDirection() <= UP){
            if(random.nextBoolean()){
                setCurrentImage(DEMON_LEFT);
            }
            else{
                setCurrentImage(DEMON_RIGHT);
            }
        }
        else if(this.getDirection() <= DOWN){
            if(random.nextBoolean()){
                setCurrentImage(DEMON_LEFT);
            }
            else{
                setCurrentImage(DEMON_RIGHT);
            }
        }
        // when moving direction is left, demon face left; when moving direction is right, demon face right
        else if(this.getDirection() <= LEFT){
            setCurrentImage(DEMON_LEFT);
        }
        else{
            setCurrentImage(DEMON_RIGHT);
        }
    }

    public int getMAX_HP() {
        return MAX_HP;
    }

    // every time after demon move or get attacked, current image should be re decided
    public void decideCurrentImage(){
        // if face left (0.50 < direction <= 0.75), change to invincible_left when invincible
        if(getDirection() > DOWN && getDirection() <= LEFT){
            if(getInvincibleState()){
                setCurrentImage(DEMON_INVINCIBLE_LEFT);
            }
            else{
                setCurrentImage(DEMON_LEFT);
            }
        }
        // if face right (0.75 < direction <= 1.00), change to invincible_right when invincible
        else if(getDirection() <= RIGHT){
            if(getInvincibleState()){
                setCurrentImage(DEMON_INVINCIBLE_RIGHT);
            }
            else{
                setCurrentImage(DEMON_RIGHT);
            }
        }
    }

    // implement Drawable interface
    public void draw(ArrayList<Tree> trees, ArrayList<Sinkhole> sinkholes, Player player,
                     Point topLeft, Point bottomRight){

        double xCenterDemon = getX() + getCurrentImage().getWidth() / 2;
        double yCenterDemon = getY() + getCurrentImage().getHeight() / 2;
        double xCenterPlayer = player.getX() + player.getCurrentImage().getWidth() / 2;
        double yCenterPlayer = player.getY() + player.getCurrentImage().getHeight() / 2;

        if(getAggressive()){
            move();
            decideCurrentImage();
        }
        else{
            decideCurrentImage();
        }
        // If an aggressive demon collides with a sinkhole, tree or
        // reaches the level boundary, it will rebound and move in the opposite direction.
        if(whetherCollides(trees, sinkholes) || Outside(this, topLeft, bottomRight)){
            changeDirection();
        }
        // demon exists and player come into the attack range, demon emits fire
        if(inAttackRange(player) && !Dead()){
            demonFire = new DemonFire(xCenterDemon, yCenterDemon, xCenterPlayer, yCenterPlayer);
            demonFire.draw(player);
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
        // still alive draw the demon
        if(!Dead()){
            getCurrentImage().drawFromTopLeft(getX(), getY());
            drawHealthBar();
        }
    }

    // method distanceTo in Point class is useful for calculating the distance from center_enemy to center_player
    public boolean inAttackRange(Player player){
        double xCenterDemon = getX() + getCurrentImage().getWidth() / 2;
        double yCenterDemon = getY() + getCurrentImage().getHeight() / 2;
        double xCenterPlayer = player.getX() + player.getCurrentImage().getWidth() / 2;
        double yCenterPlayer = player.getY() + player.getCurrentImage().getHeight() / 2;
        Point centerDemon = new Point(xCenterDemon, yCenterDemon);
        Point centerPlayer = new Point(xCenterPlayer, yCenterPlayer);
        return centerDemon.distanceTo(centerPlayer) < ATTACK_RANGE;
    }

    // draw health bar at pre-determined position (x, y - 6), with corresponding colour
    public void drawHealthBar(){
        double percentageHP = ((double)getHP() / MAX_HP) * 100;
        setCOLOUR(percentageHP);
        ENEMY_HP_FONT.drawString(Math.round(percentageHP) + "%", getX(), getY() - 6, getCOLOUR());
    }
}