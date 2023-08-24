import java.util.ArrayList;
import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Colour;
import bagel.Font;
import java.lang.Math;

public class Enemy implements Movable{
    private double x;
    private double y;
    private Image currentImage;
    private boolean aggressive;
    // use random to generate speed.
    // initialSpeed is to store speed at the beginning, which will be useful in timescale control class
    private final double MIN_SPEED = 0.2;
    private final double MAX_SPEED = 0.7;
    private double speed = MAX_SPEED - Math.random() * (MAX_SPEED - MIN_SPEED);
    private double initialSpeed;
    // generate a random double between 0 and 1, if 0 < d <= 0.25, direction up; if 0.25 < d <= 0.50, direction down;
    //  if 0.50 < d <= 0.75, direction left; if 0.75 < d <= 1.00, direction right.
    protected final double UP = 0.25;
    protected final double DOWN = 0.50;
    protected final double LEFT = 0.75;
    protected final double RIGHT = 1.00;
    private double direction = Math.random();
    // the 9 attributes below are used to draw health bar
    private int HP;
    private final int ORANGE_HEALTH = 65;
    private final int RED_HEALTH = 35;
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    private final DrawOptions COLOUR = new DrawOptions();
    private final static int ENEMY_HP_FONT_SIZE = 15;
    protected final static Font ENEMY_HP_FONT = new Font("res/frostbite.ttf", ENEMY_HP_FONT_SIZE);
    // the 5 attributed below are used to deal with invincible state
    protected final static int INVINCIBLE_TIME = 3000;
    protected final static int REFRESH_RATE = 60;
    protected final static int SECOND_TO_MILLISECOND = 1000;
    private boolean invincibleState;
    private int invincibleTiming;


    public Enemy(double x, double y){
        this.x = x;
        this.y = y;
        this.invincibleState = false;
        this.invincibleTiming = 0;
        this.initialSpeed = speed;
    }

    // useful getter and setter
    public double getX() {return x;}
    public void setX(double x) {this.x = x;}
    public double getY() {return y;}
    public void setY(double y) {this.y = y;}
    public Image getCurrentImage() {return currentImage;}
    public void setCurrentImage(Image currentImage) {this.currentImage = currentImage;}
    public boolean getAggressive() {return aggressive;}
    public void setAggressive(boolean aggressive) {this.aggressive = aggressive;}
    public int getHP() {return HP;}
    public double getDirection() {return direction;}
    public void setHP(int HP){this.HP = HP;}
    public void setSpeed(double speed) {this.speed = speed;}
    public double getInitialSpeed() {return initialSpeed;}
    public boolean getInvincibleState() {return invincibleState;}
    public void setInvincibleState(boolean invincibleState) {this.invincibleState = invincibleState;}
    public int getInvincibleTiming() {return invincibleTiming;}
    public void setInvincibleTiming(int invincibleTiming) {this.invincibleTiming = invincibleTiming;}

    public DrawOptions getCOLOUR() {return COLOUR;}
    public void setCOLOUR(double HP){
        if(HP <= RED_HEALTH){
            COLOUR.setBlendColour(RED);
        }
        else if (HP <= ORANGE_HEALTH) {
            COLOUR.setBlendColour(ORANGE);
        }
        else{
            COLOUR.setBlendColour(GREEN);
        }
    }

    // get a rectangle of the current image
    public Rectangle getBoundingBox(){
        double centerX = this.x + currentImage.getWidth() / 2.0;
        double centerY = this.y + currentImage.getHeight() / 2.0;
        Point center = new Point(centerX, centerY);
        return currentImage.getBoundingBoxAt(center);
    }

    // used when a demon gets attacked by Fae
    public void decreaseHP(int damagePoint){
        this.HP -= damagePoint;
    }

    // check whether a demon dies
    public boolean Dead(){
        return this.HP <= 0;
    }

    // implement Movable interface, move at fixed speed in corresponding direction
    public void move(){
        if(direction <= UP){
            this.setY(this.getY() - speed);
        }
        else if(direction <= DOWN){
            this.setY(this.getY() + speed);
        }
        else if(direction <= LEFT){
            this.setX(this.getX() - speed);
        }
        else if(direction <= RIGHT){
            this.setX(this.getX() + speed);
        }
    }

    // If an aggressive demon collides with a sinkhole, tree or reaches the level boundary,
    // it will rebound and move in the opposite direction.
    public void changeDirection(){
        if(direction <= UP){
            direction = DOWN;
        }
        else if(direction <= DOWN){
            direction = UP;
        }
        else if(direction <= LEFT){
            direction = RIGHT;
        }
        else if(direction <= RIGHT){
            direction = LEFT;
        }
    }

    // whether it goes outside the map
    public boolean Outside(Enemy enemy, Point topLeft, Point bottomRight){
        boolean outside = ((enemy.getX() < topLeft.x) || (enemy.getX() > bottomRight.x) ||
                (enemy.getY() > bottomRight.y) || (enemy.getY() < topLeft.y));
        return outside;
    }

    // whether a demon collides sinkhole or tree
    public boolean whetherCollides(ArrayList<Tree> trees, ArrayList<Sinkhole> sinkholes){
        Rectangle enemyRectangle = this.getBoundingBox();
        for(Tree tree: trees){
            Rectangle treeRectangle = tree.getBoundingBox();
            if(enemyRectangle.intersects(treeRectangle)){
                return true;
            }
        }
        for(Sinkhole sinkhole: sinkholes){
            Rectangle sinkholeRectangle = sinkhole.getBoundingBox();
            if(enemyRectangle.intersects(sinkholeRectangle) && sinkhole.getExisting()){
                return true;
            }
        }
        return false;
    }
}
