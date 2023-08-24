import bagel.*;
import bagel.util.Point;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 2, 2022
 *
 * Please enter your name below
 * @Yitong_Kong
 */

public class ShadowDimension extends AbstractGame {
    // messages
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String WIN_MESSAGE = "CONGRATULATIONS!";
    private final static String LOSE_MESSAGE = "GAME OVER!";
    private final static String LEVEL0_INSTRUCTION = "PRESS SPACE TO START\nUSE ARROW KEYS TO FIND GATE";
    private final static String LEVEL1_INSTRUCTION = "PRESS SPACE TO START\nPRESS A TO ATTACK\nDEFEAT NAVEC TO WIN";
    // filename
    private final Image BACKGROUND_IMAGE_0 = new Image("res/background0.png");
    private final Image BACKGROUND_IMAGE_1 = new Image("res/background1.png");
    private final String LEVEL0_DATA = "res/level0.csv";
    private final String LEVEL1_DATA = "res/level1.csv";
    private final String FONT_FILE = "res/frostbite.ttf";
    // game entities
    private Player player;
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private ArrayList<Tree> trees = new ArrayList<Tree>();
    private ArrayList<Sinkhole> sinkholes = new ArrayList<Sinkhole>();
    private ArrayList<Demon> demons = new ArrayList<Demon>();
    private Navec navec;
    private TimescaleControl timescaleControl;
    private Point topLeft;
    private Point bottomRight;
    //define font size and set the font
    private final int FONT_SIZE = 75;
    private final int INSTRUCTION_FONT_SIZE = 40;
    private final Font FONT = new Font(FONT_FILE, FONT_SIZE);
    private final Font INSTRUCTION_FONT = new Font(FONT_FILE, INSTRUCTION_FONT_SIZE);
    // some predefined coordinates to avoid magic number
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final int X_GAME_TITLE = 260;
    private final int Y_GAME_TITLE = 250;
    private final int X_INSTRUCTION_MESSAGE_LEVEL0 = X_GAME_TITLE + 90;
    private final int Y_INSTRUCTION_MESSAGE_LEVEL0 = Y_GAME_TITLE + 190;
    private final int X_INSTRUCTION_MESSAGE_LEVEL1 = 350;
    private final int Y_INSTRUCTION_MESSAGE_LEVEL1 = 350;
    // several useful boolean
    private boolean gameStart;
    private boolean gameLose;
    private boolean gameWin;
    private boolean level0_finished;
    private boolean level1_started;
    // attributes for transition from level0 to level1
    private int level1EntryTiming;
    private final static int LEVEL1_ENTRY_TIME = 3000;
    private final static int SECOND_TO_MILLISECOND = 1000;
    private final static int REFRESH_RATE = 60;

    public ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        readCSV(LEVEL0_DATA);
        gameStart = false;
        gameLose = false;
        gameWin = false;
        level0_finished = false;
        level1_started = false;
        level1EntryTiming = 0;
        timescaleControl = new TimescaleControl();
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension();
        game.run();
    }

    /**
     * Method used to read file and create objects (You can change this
     * method as you wish).
     */
    private void readCSV(String filename){
        double xCor;
        double yCor;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String text;
            while ((text = br.readLine()) != null) {
                String[] entities = text.split(",");
                if(entities[0].equals("Fae")){
                    xCor = Double.parseDouble(entities[1]);
                    yCor = Double.parseDouble(entities[2]);
                    player = new Player(xCor, yCor);
                }
                else if(entities[0].equals("Wall")){
                    xCor = Double.parseDouble(entities[1]);
                    yCor = Double.parseDouble(entities[2]);
                    walls.add(new Wall(xCor, yCor, "res/wall.png"));
                }
                else if(entities[0].equals("Sinkhole")){
                    xCor = Double.parseDouble(entities[1]);
                    yCor = Double.parseDouble(entities[2]);
                    sinkholes.add(new Sinkhole(xCor, yCor, "res/sinkhole.png"));
                }
                else if(entities[0].equals("Tree")){
                    xCor = Double.parseDouble(entities[1]);
                    yCor = Double.parseDouble(entities[2]);
                    trees.add(new Tree(xCor, yCor, "res/tree.png"));
                }
                else if(entities[0].equals("Demon")){
                    xCor = Double.parseDouble(entities[1]);
                    yCor = Double.parseDouble(entities[2]);
                    demons.add(new Demon(xCor, yCor));
                }
                else if(entities[0].equals("Navec")){
                    xCor = Double.parseDouble(entities[1]);
                    yCor = Double.parseDouble(entities[2]);
                    navec = new Navec(xCor, yCor);
                }
                else if(entities[0].equals("TopLeft")){
                    xCor = Double.parseDouble(entities[1]);
                    yCor = Double.parseDouble(entities[2]);
                    topLeft = new Point(xCor, yCor);
                }
                else if(entities[0].equals("BottomRight")){
                    xCor = Double.parseDouble(entities[1]);
                    yCor = Double.parseDouble(entities[2]);
                    bottomRight = new Point(xCor, yCor);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }
        // before game start
        if(!gameStart){
            drawGAME_TITLE();
            drawLEVEL0_INSTRUCTION(input);
        }
        // fae dies
        if(gameLose){
            drawEndInterface(LOSE_MESSAGE);
        }
        // fae defeats navec
        if(gameWin){
            drawEndInterface(WIN_MESSAGE);
        }
        // level0 complete, level1 about to start
        if(player.level0_Complete() && !level1_started){
            drawLEVEL1_INSTRUCTION(input);
            if(level0_finished) {
                level1EntryTiming++;
            }
        }
        // level1 countdown complete
        if(level1EntryTiming * SECOND_TO_MILLISECOND / REFRESH_RATE > LEVEL1_ENTRY_TIME){
            level1_started = true;
            level1EntryTiming = 0;
            // in level1, we need a new sinkhole array list to store new sinkhole data and the wall array list should be empty
            sinkholes = new ArrayList<Sinkhole>();
            walls = new ArrayList<Wall>();
            readCSV(LEVEL1_DATA);
        }
        // game going on
        if(gameStart && !gameWin && !gameLose){
            if(player.Dead()){
                gameLose = true;
            }
            // during level0
            if(!player.level0_Complete()){
                BACKGROUND_IMAGE_0.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
                for(Wall wall: walls){
                    wall.draw();
                }
                for(Sinkhole sinkhole: sinkholes){
                    sinkhole.draw();
                }
                player.draw(input, walls, trees, sinkholes, demons, navec, topLeft, bottomRight);
            }
            // during level1
            if(level1_started){
                BACKGROUND_IMAGE_1.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
                if(input.wasPressed(Keys.L)){
                    timescaleControl.increaseTimescale(demons, navec);
                }
                if(input.wasPressed(Keys.K)) {
                    timescaleControl.decreaseTimescale(demons, navec);
                }
                for(Tree tree: trees){
                    tree.draw();
                }
                for(Sinkhole sinkhole: sinkholes){
                    sinkhole.draw();
                }
                for(Demon demon: demons){
                    if(!demon.Dead()){
                        demon.draw(trees, sinkholes, player, topLeft, bottomRight);
                    }
                }
                navec.draw(trees, sinkholes, player, topLeft, bottomRight);
                if(navec.Dead()){
                    gameWin = true;
                }
                player.draw(input, walls, trees, sinkholes, demons, navec, topLeft, bottomRight);
            }
        }
    }

    // four methods below are used to draw strings on the screen
    private void drawGAME_TITLE(){
        FONT.drawString(GAME_TITLE, X_GAME_TITLE, Y_GAME_TITLE);
    }
    private void drawLEVEL0_INSTRUCTION(Input input){
        INSTRUCTION_FONT.drawString(LEVEL0_INSTRUCTION, X_INSTRUCTION_MESSAGE_LEVEL0, Y_INSTRUCTION_MESSAGE_LEVEL0);
        // allows the game to begin when the space key is pressed.
        if (input.wasPressed(Keys.SPACE)){
            gameStart = true;
        }
    }
    private void drawLEVEL1_INSTRUCTION(Input input){
        INSTRUCTION_FONT.drawString(LEVEL1_INSTRUCTION, X_INSTRUCTION_MESSAGE_LEVEL1, Y_INSTRUCTION_MESSAGE_LEVEL1);
        // allows the game to begin when the space key is pressed.
        if (input.wasPressed(Keys.SPACE)){
            level0_finished = true;
        }
    }
    // use getWidth and getHeight to calculate the center
    // and Font.drawString will draw the provided string with its bottom-left at the given (x, y) location
    private void drawEndInterface(String message){
        double xMessage = Window.getWidth() / 2.0 - FONT.getWidth(message) / 2.0;
        double yMessage = Window.getHeight() / 2.0 + FONT_SIZE / 2.0;
        FONT.drawString(message, xMessage, yMessage);
    }
}
