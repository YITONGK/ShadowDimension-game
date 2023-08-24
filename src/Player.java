import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Colour;
import bagel.Font;
import java.lang.Math;
import java.util.ArrayList;

public class Player {
    private double x;
    private double y;
    private double prevX;
    private double prevY;
    private int HP;
    private final int MAX_HP = 100;
    private final int speed = 2;
    private final int SINKHOLE_DAMAGE = 30;
    private final int DAMAGE_POINT = 20;
    private Image currentImage;
    private boolean faceRight;
    private final Image FAE_LEFT = new Image("res/fae/faeLeft.png");
    private final Image FAE_RIGHT = new Image("res/fae/faeRight.png");
    private final Image FAE_ATTACK_LEFT = new Image("res/fae/faeAttackLeft.png");
    private final Image FAE_ATTACK_RIGHT = new Image("res/fae/faeAttackRight.png");
    private final int xGate = 950;
    private final int yGate = 670;
    // 10 attributes below are for health bar drawing
    private final int xHP = 20;
    private final int yHP = 25;
    private final int ORANGE_HEALTH = 65;
    private final int RED_HEALTH = 35;
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    private final DrawOptions COLOUR = new DrawOptions();
    private final int HP_Font_Size = 30;
    private final Font HP_FONT = new Font("res/frostbite.ttf", HP_Font_Size);
    // below are level 1 attributes, used to deal with attack, cool down, invincible states
    private final static int REFRESH_RATE = 60;
    private final static int SECOND_TO_MILLISECOND = 1000;
    private boolean attackState = false;
    private final int ATTACK_TIME = 1000;
    private int attackTiming = 0;
    private boolean coolDownState = false;
    private final int COOL_DOWN_TIME = 2000;
    private int coolDownTiming = 0;
    private boolean invincibleState = false;
    private final int INVINCIBLE_TIME = 3000;
    private int invincibleTiming = 0;


    public Player(double x, double y){
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
        this.HP = MAX_HP;
        faceRight = true;
        currentImage = FAE_RIGHT;
    }

    // useful getter and setter
    public double getX() {return x;}
    public void setX(double x) {this.x = x;}
    public double getY() {return y;}
    public void setY(double y) {this.y = y;}
    public Image getCurrentImage() {return currentImage;}
    public boolean getInvincibleState(){return invincibleState;}
    public void setInvincibleSate(boolean invincibleSate){this.invincibleState = invincibleSate;}
    public int getHP(){return HP;}
    public int getMAX_HP(){return MAX_HP;}

    // deduct HP when going into sinkholes or getting attacked by enemies, minimum HP is 0
    public void decreaseHP(int damage){
        this.HP -= damage;
        if (this.HP < 0) {
            this.HP = 0;
        }
    }

    // check whether Fae is alive or dead
    public boolean Dead(){
        return (this.HP <= 0);
    }

    // justify whether Fae reaches the gate or not in level0
    public boolean level0_Complete(){
        return (this.x >= xGate && this.y >= yGate);
    }

    // draw player image
    public void draw(Input input, ArrayList<Wall> walls, ArrayList<Tree> trees, ArrayList<Sinkhole> sinkholes,
                     ArrayList<Demon> demons, Navec navec, Point topLeft, Point bottomRight){

        if(input.isDown(Keys.UP)){
            setPreviousPoint();
            move(0, -speed);
        }
        else if(input.isDown(Keys.DOWN)){
            setPreviousPoint();
            move(0, speed);
        }
        else if(input.isDown(Keys.LEFT)){
            setPreviousPoint();
            faceRight = false;
            currentImage = FAE_LEFT;
            move(-speed, 0);
        }
        else if(input.isDown(Keys.RIGHT)){
            setPreviousPoint();
            faceRight = true;
            currentImage = FAE_RIGHT;
            move(speed, 0);
        }
        // for attack state, we need to do more work which is to decide fae image
        if(input.wasPressed(Keys.A) && !coolDownState){
            attackState = true;
            if(faceRight){
                currentImage = FAE_ATTACK_RIGHT;
            }
            else{
                currentImage = FAE_ATTACK_LEFT;
            }
        }
        // when fae is attacking, check whether fae will make damage to enemies, attack frame starts timing
        if(attackState){
            collideEnemy(demons, navec);
            attackTiming ++;
        }
        if(attackTiming * SECOND_TO_MILLISECOND / REFRESH_RATE > ATTACK_TIME){
            attackState = false;
            coolDownState = true;
            attackTiming = 0;
            if(faceRight){
                currentImage = FAE_RIGHT;
            }
            else{
                currentImage = FAE_LEFT;
            }
        }
        // when fae is invincible, invincible frame starts timing
        if(invincibleState){
            invincibleTiming ++;
        }
        if(invincibleTiming * SECOND_TO_MILLISECOND / REFRESH_RATE > INVINCIBLE_TIME){
            invincibleState = false;
            invincibleTiming = 0;
        }
        // when attack is over, start counting cool down timing
        if(coolDownState){
            coolDownTiming ++;
        }
        if(coolDownTiming * SECOND_TO_MILLISECOND / REFRESH_RATE > COOL_DOWN_TIME){
            coolDownState = false;
            coolDownTiming = 0;
        }
        // this method is to make sure fae not overlap stationary entities
        if(collideEntity(walls, trees, sinkholes) || Outside(this, topLeft, bottomRight)){
            stay();
        }
        currentImage.drawFromTopLeft(getX(), getY());
        drawHealthBar();
    }

    // get a rectangle of the current image
    public Rectangle getBoundingBox(){
        double centerX = this.x + currentImage.getWidth() / 2.0;
        double centerY = this.y + currentImage.getHeight() / 2.0;
        Point center = new Point(centerX, centerY);
        return currentImage.getBoundingBoxAt(center);
    }

    // check whether the player goes outside the map
    public boolean Outside(Player player, Point topLeft, Point bottomRight){
        boolean outside = ((player.getX() < topLeft.x) || (player.getX() > bottomRight.x) ||
                (player.getY() > bottomRight.y) || (player.getY() < topLeft.y));
        return outside;
    }

    // move method to change the point coordinate for the player
    public void move(double deltaX, double deltaY){
        this.x += deltaX;
        this.y += deltaY;
    }

    // method to update previous point coordinate as Fae goes
    public void setPreviousPoint(){
        prevX = this.getX();
        prevY = this.getY();
    }

    // method to let the player remain at the previous place when going somewhere forbidden
    public void stay(){
        this.setX(prevX);
        this.setY(prevY);
    }

    // set three different colours at different HP level
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

    // draw health bar at pre-determined position with corresponding colour
    public void drawHealthBar(){
        this.setCOLOUR(((double)HP / MAX_HP) * 100);
        HP_FONT.drawString(Math.round((double)HP / MAX_HP * 100) + "%", xHP, yHP, COLOUR);
    }

    // check whether player goes overlap with walls, trees,or sinkholes,
    // when going into sinkholes, HP will decrease and sinkhole will disappear
    public boolean collideEntity(ArrayList<Wall> walls, ArrayList<Tree> trees, ArrayList<Sinkhole> sinkholes){
        Rectangle playerRectangle = this.getBoundingBox();
        for (Wall wall: walls){
            if (playerRectangle.intersects(wall.getBoundingBox())) {
                return true;
            }
        }
        for (Tree tree: trees){
            if (playerRectangle.intersects(tree.getBoundingBox())) {
                return true;
            }
        }
        for (Sinkhole sinkhole: sinkholes) {
            if (playerRectangle.intersects(sinkhole.getBoundingBox()) && sinkhole.getExisting()){
                sinkhole.setExisting(false);
                this.decreaseHP(SINKHOLE_DAMAGE);
                System.out.println("Sinkhole inflicts 30 damage points on Fae. " +
                        "Fae's current health: " + HP + "/" + MAX_HP);
            }
        }
        return false;
    }

    // when faeAttack collide enemy which is not invincible, enemy's HP decrease and becomes invincible
    public void collideEnemy(ArrayList<Demon> demons, Navec navec){
        Rectangle playerRectangle = this.getBoundingBox();
        if(playerRectangle.intersects(navec.getBoundingBox()) && !navec.getInvincibleState()){
            navec.decreaseHP(DAMAGE_POINT);
            navec.setInvincibleState(true);
            System.out.println("Fae inflicts 20 damage points on Navec. Navec's current health: "
                    + navec.getHP() + "/" + navec.getMAX_HP());
        }
        for(Demon demon: demons){
            if(playerRectangle.intersects(demon.getBoundingBox()) && !demon.getInvincibleState()){
                demon.decreaseHP(DAMAGE_POINT);
                demon.setInvincibleState(true);
                System.out.println("Fae inflicts 20 damage points on Demon. Demon's current health: "
                    + demon.getHP() + "/" + demon.getMAX_HP());
            }
        }
    }
}
