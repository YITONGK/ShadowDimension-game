import java.util.ArrayList;
import java.lang.Math;

public class TimescaleControl {
    private final int MAX = 3;
    private final int MIN = -3;
    private final double CHANGE_RATE = 0.5;
    private final int ORIGINAL_SPEED = 0;
    private int timescale;

    public TimescaleControl(){
        this.timescale = 0;
    }

    // when increasing timescale, we need to make sure timescale <= 3
    public void increaseTimescale(ArrayList<Demon> demons, Navec navec){
        this.timescale ++;
        if(timescale > MAX){
            timescale = MAX;
        }
        setTimescale(demons, navec);
        System.out.println("Sped up, Speed: " + timescale);
    }

    // when decreasing timescale, we need to make sure timescale >= -3
    public void decreaseTimescale(ArrayList<Demon> demons, Navec navec){
        timescale --;
        if(timescale < MIN){
            timescale = MIN;
        }
        setTimescale(demons, navec);
        System.out.println("Sped down, Speed: " + timescale);
    }

    // as required, when timescale = 0, speed should return to original speed
    // every time timescale++, speed *= (1 + CHANGE_RATE)
    // every time timescale--, speed *= (1 - CHANGE_RATE)
    public void setTimescale(ArrayList<Demon> demons, Navec navec){
        if(timescale > ORIGINAL_SPEED){
            navec.setSpeed(navec.getInitialSpeed() * Math.pow(1 + CHANGE_RATE, timescale));
            for(Demon demon: demons){
                demon.setSpeed(demon.getInitialSpeed() * Math.pow(1 + CHANGE_RATE, timescale));
            }
        }
        if(timescale < ORIGINAL_SPEED){
            // since now timescale < 0, we take its absolute value, then put it into power method
            navec.setSpeed(navec.getInitialSpeed() * Math.pow(1 - CHANGE_RATE, Math.abs(timescale)));
            for(Demon demon: demons){
                demon.setSpeed(demon.getInitialSpeed() * Math.pow(1 - CHANGE_RATE, Math.abs(timescale)));
            }
        }
        // timescale = 0, speed = initial speed
        else{
            navec.setSpeed(navec.getInitialSpeed());
            for(Demon demon: demons) {
                demon.setSpeed(demon.getInitialSpeed());
            }
        }
    }
}
