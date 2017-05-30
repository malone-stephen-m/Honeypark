/*
  Add an element to specified index of Java ArrayList Example
  This Java Example shows how to add an element at specified index of java
  ArrayList object using add method.
*/

import java.util.*;

/**
 * Created by Adam Cantor and Stephen Malone on 7/6/2015.
 * This file is intended to generate/populate a list of agents and parking lots. These lists will be populated
 * according to a given destination in later implemantion. For now we are assuming that there is one destination with
 * five parking lots.
 *
 */
public class Environment {
    public static void main(String[] args) {
        //INITIALIZATION PARAMETERS
        //Create a variable to hold the time
        int time = 0;

        //Choose the size of simulation environment
        int bees = 15000;
        int lots = 5;

        //Select the agent algorithm: 1 is Random, 2 is Greedy, 3 is HoneyPark MK1
        int algo = 1;

        //Create an ArrayList object to hold the Vehicles and the Parking Lots
        ArrayList<Bees> beeList = new ArrayList<Bees>();
        ArrayList<Lots> lotList = new ArrayList<Lots>();

        // Generate individual lots and add them to a the lotList
        for(int index=0; index < lots; index++) {
            //Generate individual lots and add them to place holder array
            Lots p = new Lots(index);
            //Add place holder array to overall lotList
            lotList.add(index, p);
        }
        // Fill in lotList with known lots of interest
        float[] knownLotsX;                 float[] knownLotsY;
        knownLotsX = new float[lots];       knownLotsY = new float[lots];
        knownLotsX[0] = (float) 37.787903;  knownLotsX[0] = (float) -122.407453;
        knownLotsX[1] = (float) 37.790026;  knownLotsX[1] = (float) -122.406320;
        knownLotsX[2] = (float) 37.783766;  knownLotsX[2] = (float) -122.407614;
        knownLotsX[3] = (float) 37.777374;  knownLotsX[3] = (float) -122.418184;
        knownLotsX[4] = (float) 37.788718;  knownLotsX[4] = (float) -122.395128;


        // Generate individual lots and add them to a the beeList
        for(int index=0; index < bees; index++) {
            //Generate individual bee agents and add them to place holder array
            Bees holdBee = new Bees(index);

            //Add place holder array to overall beeList
            beeList.add(index, holdBee);

            // Initialize Parameters with Random Origin and Destination Coordinates
            Random randGen = new Random(index);
            int randDest = randGen.nextInt(lots);
            beeList.get(index).beeDestX = knownLotsX[randDest];
            beeList.get(index).beeDestY = knownLotsY[randDest];

            float randOriginX;
            float randOriginY;

            //Initialize random origin with San Francisco GPS Geo-fence in mind
            randOriginX = (float) (  37.76 + randGen.nextFloat()*4/10);
            randOriginY = (float) (-122.39 + randGen.nextFloat()*4/10);
            beeList.get(index).beeOriginX = randOriginX;
            beeList.get(index).beeOriginY = randOriginY;
        }

        // Display the current state of the environment
        System.out.println("lotList initialized...");
        //display elements of lotList
        for(int index=0; index < lotList.size(); index++) {
            System.out.print("Lot# " + lotList.get(index).lotID);
            System.out.print(", Total Spots " + lotList.get(index).lotSpots);
            System.out.print(", Empty Spots " + lotList.get(index).lotEmpty);
            System.out.print(", Location " + lotList.get(index).lotLocX + ", " + lotList.get(index).lotLocY);
            System.out.println(", Quality " + lotList.get(index).lotQuality);
        }
/*
        System.out.println("beeList initialized...");
        //display elements of BeeList
        for(int index=0; index < beeList.size(); index++) {
            System.out.print("Agent# " + beeList.get(index).beeID);
            System.out.print(", Start " + beeList.get(index).startTime);
            System.out.print(", Origin " + beeList.get(index).beeOrigin);
            System.out.print(", Current " + beeList.get(index).beeLocation);
            System.out.print(", Chosen Lot " + beeList.get(index).beeLot);
            System.out.print(", Destination " + beeList.get(index).beeDestination);
            System.out.print(", Parked " + beeList.get(index).huntTime);
            System.out.println(", Exit " + beeList.get(index).exitTime);
        }

        // display simulation start time
        System.out.println("Time " + time);
*/
        /** Move the simulation forward in time by 1 time step until an action is triggered:
         * Agent reaches the destination
         * Agent Chooses a Parking Lot
         * Agent reaches a spot
         * Agent leaves a spot
         *
         * At each time step allow the number of empty spots in each lot to change randomly
         */

        while (time < 1440) {
            // Increment the overall time variable until all agents have parked and exited
            time++;

            /*
            // Randomly change number of free spots to account for free agents. Use +/-5% of total spots
            for (int index = 0; index < lotList.size(); index++) {
                // Generate a random number to change available lot spots without overfilling or over emptying each lot
                Random randGen = new Random(index);
                int intNum;
                do {
                    // Rectify calculation variables to avoid 0's
                    int intLittle = Math.round(lotList.get(index).lotSpots / 20);
                    int intBig = Math.round(lotList.get(index).lotSpots / 10);
                    if (intLittle == 0) intLittle = 1;
                    if (intBig == 0) intBig = 1;
                    intNum = randGen.nextInt(intLittle) - randGen.nextInt(intBig);
                } while (((lotList.get(index).lotEmpty + intNum) < 0)||((lotList.get(index).lotEmpty) + intNum > lotList.get(index).lotSpots));
                // add the random pos or neg number to the number of empty spots
                lotList.get(index).lotEmpty = lotList.get(index).lotEmpty + intNum;
            }
            */

            // Move all agents forward by one time step if the agent has started and not parked
            for (int index = 0; index < beeList.size(); index++) {
                // Check to make sure that the agent is active in this time window
                if ((beeList.get(index).startTime <= time) && (beeList.get(index).huntTime <= 0)) {
                    // Generate a random Gaussian number to decide how far each vehicle moves per time step
                    Random randGen = new Random(index);
                    double gausNum;
                    int randLot;
                    do {
                        gausNum = randGen.nextGaussian()/2 + 1;
                    } while ((gausNum < 0) && (gausNum > 1.8));

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////
                    // Move agent towards destination (increment location if the destination is ahead, decrement if it is behind)
                    if (beeList.get(index).beeLocation < beeList.get(index).beeDestination) {
                        beeList.get(index).beeLocation = beeList.get(index).beeLocation + gausNum;
                    }
                    else {
                        beeList.get(index).beeLocation = beeList.get(index).beeLocation - gausNum;
                    }
                    //////////////////////////////////////////////////////////////////////////////////////////////////////

                    // If an agent reaches their destination (gets within 2 units)
                    if ((Math.abs(beeList.get(index).beeLocation - beeList.get(index).beeDestination)) < 2) {
                        //System.out.println("beeList.get(index).beeLot" + beeList.get(index).beeLot);
                        //System.out.println("beeList.get(index).huntTime" + beeList.get(index).huntTime);

                        // Check for park indicator value of -1 and if the chosen lot has an empty spot
                        if ((beeList.get(index).huntTime == -1) && (lotList.get(beeList.get(index).beeLot).lotEmpty > 0)) {
                            //System.out.println("Parked at " + time);
                            // Subtract a spot from the emptySpots
                            --lotList.get(beeList.get(index).beeLot).lotEmpty;

                            // Record the time that the agent parked
                            beeList.get(index).huntTime = time;

                        } // Next we select a parking lot destination if the bee isn't looking yet or the current lot is full
                        else if ((beeList.get(index).huntTime == 0) || (lotList.get(beeList.get(index).beeLot).lotEmpty == 0)) {
                            // This is the section where we select a parking lot and begin the hunt according to our algorithms

                            // Choose next lot Randomly with an Even Distribution (Random Search)
                            if (algo == 1) {
                                randLot = randGen.nextInt(lots);
                                // Save the chosen lot and set the lot location as the destination
                                beeList.get(index).beeLot = randLot;
                                beeList.get(index).beeDestX = knownLotsX[randLot];
                                beeList.get(index).beeDestY = knownLotsY[randLot];
                            }


                            // Choose closest lot to current Bee Destination (Greedy Search)
                            if (algo == 2) {
                                // Find the closest lot to the current Bee destination by distance in an outward direction
                                // calcDist - calculation variable to compare lot to bee distances
                                double calcDist;
                                // prev_calcDist - holds previous closest bee destination for continued comparisons
                                double prev_calcDist = 1000;

                                // Check all possible lots to see which one is the next closest
                                for (int closedex = 0; closedex < lotList.size(); closedex++) {
                                    calcDist = lotList.get(closedex).lotLocation - beeList.get(index).beeLocation;

                                    // looks for the closest lot starting from the destination and moving outward.
                                    if ((0 < calcDist) && (calcDist < prev_calcDist)) {

                                        // Save the chosen lot and set the lot location as the destination
                                        beeList.get(index).beeLot = closedex;
                                        beeList.get(index).beeDestination = lotList.get(closedex).lotLocation;

                                        // update prev_calcDist
                                        prev_calcDist = calcDist;
                                    }
                                }

                                // If all lots have been attempted go back to the first lot attempt and re-search
                                if (prev_calcDist == 1000) {
                                    // Check for the closest lot in the reverse direction
                                    for (int closedex = lotList.size(); closedex < lotList.size(); closedex--) {
                                        calcDist = lotList.get(closedex).lotLocation - beeList.get(index).beeOrigin;

                                        // looks for the closest lot starting from the destination and moving outward.
                                        if ((0 < calcDist) && (calcDist < prev_calcDist)) {

                                            // Save the chosen lot and set the lot location as the destination
                                            beeList.get(index).beeLot = closedex;
                                            beeList.get(index).beeDestination = lotList.get(closedex).lotLocation;
                                        }
                                    }
                                }
                            }

                            // Choose lot with highest quality given current location (HoneyPark)
                            if (algo == 3) {

                            }

                            // Initiate the search for parking by setting huntTime to the indicator value of -1
                            if (beeList.get(index).huntTime == 0) {
                                // Set indicator value for huntTime to show that the agent is hunting
                                beeList.get(index).huntTime = -1;
                                // Save the time for the start of the parking search
                                beeList.get(index).parkStart = time;
                            }
                        }
                    }
                }

                // Check if a parked agent has finished their errand and then removes them from the environment
                if ((beeList.get(index).huntTime > 0) && ((time - beeList.get(index).huntTime) >= beeList.get(index).needTime) && (beeList.get(index).exitTime == 0)) {
                    // Record the time that the agent leaves the lot and effectively leaves the environment
                    beeList.get(index).exitTime = time;
                    // Add the empty spot back into the parking lot
                    ++lotList.get(beeList.get(index).beeLot).lotEmpty;
                }

            }

        }

        // Display the final state of the environment
        System.out.println("lotList terminates...");
        //display elements of lotList
        for(int index=0; index < lotList.size(); index++) {
            System.out.print("Lot# " + lotList.get(index).lotID);
            System.out.print(", Total Spots " + lotList.get(index).lotSpots);
            System.out.print(", Empty Spots " + lotList.get(index).lotEmpty);
            System.out.print(", Location " + lotList.get(index).lotLocX + ", " + lotList.get(index).lotLocY);
            System.out.println(", Quality " + lotList.get(index).lotQuality);
        }
/*
        System.out.println("beeList terminates...");
        //display elements of BeeList
        for(int index=0; index < beeList.size(); index++) {
            System.out.print("Agent# " + beeList.get(index).beeID);
            //System.out.print(", Origin " + beeList.get(index).beeOrigin);
            System.out.print(", Current " + Math.round(beeList.get(index).beeLocation));
            System.out.print(", Chosen Lot " + beeList.get(index).beeLot);
            System.out.print(", Destination " + beeList.get(index).beeDestination);
            System.out.print(", Start " + beeList.get(index).startTime);
            System.out.print(", startPark " + beeList.get(index).parkStart);
            System.out.print(", Parked " + beeList.get(index).huntTime);
            System.out.println(", Exit " + beeList.get(index).exitTime);
        }
*/
        // display simulation end time
        System.out.println("Time " + time);

        /* Calculate the overall metrics
        - Total Number of Agents
        - Total Time spent by all agents driving, parking, and overall
        */

        // Initialize variables
        double totalTime = 0;
        double huntTime = 0;
        double maxhuntTime = 0;

        // Sum all of the overall metrics by looking at each agent
        for(int index=0; index < beeList.size(); index++) {
            if (beeList.get(index).exitTime != 0) {
                totalTime = totalTime + (beeList.get(index).exitTime - beeList.get(index).startTime);
                huntTime = huntTime + (beeList.get(index).huntTime - beeList.get(index).parkStart);
                if ((beeList.get(index).huntTime - beeList.get(index).parkStart) > maxhuntTime) maxhuntTime = (beeList.get(index).huntTime - beeList.get(index).parkStart);
            }
        }
        System.out.print("# of Lots =" + lots);
        System.out.print(", # of Agents =" + bees);
        System.out.print(", Total Time Spent = " + totalTime);
        System.out.print(", Total Spent Parking = " + huntTime);
        System.out.println(", Maximum Hunt Time = " + maxhuntTime);
    }
}
