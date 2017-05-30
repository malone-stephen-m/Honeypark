import java.util.*;

/**
 * Created by Adam Cantor and Stephen Malone on 7/13/2015.
 *
 * This file creates the variable structure to define parking lot parameters
 */
public class Lots {

    public int lotID;
    public int lotSpots;
    public int lotEmpty;
    public float lotLocX;
    public float lotLocY;
    public float lotQuality;

    public Lots(int ID) {

        /*
        NOTE: For this simulation we make the following assumptions:
        1) All parking lots have known parameters(to us, but not to the swarm)
        2) Parameters of Interest - # of Spots, Location
        4) All parking events occur between 12 am and 12 pm (0 - 1440 minutes)
        */

        // Generate a random gaussian number for general simulation randomization
        Random randGen = new Random(ID);
/*
        double gausNum = randGen.nextGaussian();
        int intNum = randGen.nextInt(100);
        double doubNum = randGen.nextDouble();

        int lotAddress = intNum+100;
*/
        int lotSpot = randGen.nextInt(192)+8;

        this.lotID = ID;
        this.lotLocX = 0;
        this.lotLocY = 0;
        this.lotSpots = lotSpot;
        this.lotEmpty = randGen.nextInt(lotSpot);
        this.lotQuality = 1;
    }
}
