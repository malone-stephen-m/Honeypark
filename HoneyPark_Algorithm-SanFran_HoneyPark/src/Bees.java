import java.util.*;

/**
 * Created by Adam Cantor on 7/13/2015.
 *
 * This file creates the variable structure to define Car/Bee Agent parameters
 */
public class Bees {

    public int beeID;
    public double startTime;
    public float beeOriginX;
    public float beeOriginY;
    public float beeLocX;
    public float beeLocY;
    public float beeDestX;
    public float beeDestY;
    public int parkStart;
    public int beeLot;
    public double needTime;
    public double huntTime;
    public double exitTime;

    public Bees(int ID) {
        /*
        NOTE: For this simulation we make the following assumptions:
        1) All agents start at the same point (beeOrigin = 0)
        2) All agents go to the same destination (beeDestination = 100)
        3) Time to reach dest from origin is a gaussian distribution with average of 100 and lower bound of 80.
        4) All parking events occur between 12 am and 12 pm (0 - 1440 minutes)
        */

        // Generate a random gaussian number for general simulation randomization
        Random randGen = new Random(ID);

        double gausNum = randGen.nextGaussian();
        double intNum = randGen.nextInt();
        double doubNum = randGen.nextDouble();

        int randStart = randGen.nextInt(1440);

        // Generate a random number for travel time and park time on a gaussian distribution with a minimum
        double tripGaus;
        double stayGaus;
        double needGaus;
        do {
            double rawTripGaus = randGen.nextGaussian();
            double rawStayGaus = randGen.nextGaussian();
            double rawNeedGaus = randGen.nextGaussian();
            tripGaus = (int) Math.round(rawTripGaus);
            stayGaus = (int) Math.round(rawStayGaus);
            needGaus = rawNeedGaus*30 + 30;
        } while ((tripGaus < .8) && (tripGaus > 2) && (stayGaus < .5) && (stayGaus > 3) && (needGaus > 5));

        this.beeID = ID;
        this.startTime = randStart;
        this.beeOriginX = 0;
        this.beeOriginY = 0;
        this.beeLocX = 0;
        this.beeLocY = 0;
        this.beeDestX = 0;
        this.beeDestY = 0;
        this.parkStart = -1;
        this.beeLot = -1;
        this.needTime = needGaus;
        this.huntTime = 0;
        this.exitTime = 0;
    }
}