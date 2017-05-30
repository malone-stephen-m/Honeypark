import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Bee agent that interacts with an Artificial Bee Colony parking simulation evironment.
 * @author Adam Cantor
 */
public class Bee {

    Set<Cell> spotFound = new HashSet<Cell>();

    public static double dropoffRate = .9;
    public static double bestCellNext = 0.8;
    public static boolean allSpotRequired = false;

    private int x;
    private int y;

    private boolean returnToHive;

    Cell[][] world;

    double maxPheromone = 10.0;

    int steps = 0;

    private Bees bees;

    public Bee(Cell startCell, Cell[][] world, Bees bees){
        this.x = startCell.c;
        this.y = startCell.r;
        this.world = world;
        this.bees = bees;
    }


    public void die(){
        returnToHive = false;
        steps = 0;
        spotFound.clear();
        Set<Cell> hives = bees.getHives();
        if(!hives.isEmpty()){
            int hiveIndex = (int) (hives.size() * Math.random());
            Cell hive = (Cell) hives.toArray()[hiveIndex];
            x = hive.c;
            y = hive.r;
        }
    }

    public void step(){

        double chanceToTakeBest = Math.random();

        steps++;

        spotFound.retainAll(bees.getSpot());

        if(returnToHive){
            if(world[x][y].hasHive()){
                die();
            }
            else{

                double maxHiveSoFar = 0;
                Map<Cell, Double> maxSpotSoFarMap = new HashMap<Cell, Double>();
                List<Cell> maxHiveCells = new ArrayList<Cell>();
                List<Cell> allNeighborCells = new ArrayList<Cell>();
                double totalNeighborPheromones = 0;
                for(int c = -1; c <=1; c++){

                    if(x+c < 0 || x+c >= world.length){
                        continue;
                    }

                    for(int r = -1; r <= 1; r++){
                        if(c == 0 && r == 0){
                            continue;
                        }
                        else if(y+r < 0 || y+r >= world[0].length){
                            continue;
                        }

                        if(!world[x+c][y+r].isBlocked()){

                            allNeighborCells.add(world[x+c][y+r]);
                            totalNeighborPheromones += world[x+c][y+r].nestPheromoneLevel;

                            if(world[x+c][y+r].getNestPheromoneLevel() > maxHiveSoFar){
                                maxHiveSoFar = world[x+c][y+r].getHivePheromoneLevel();

                                maxHiveCells.clear();
                                maxHiveCells.add(world[x+c][y+r]);
                            }
                            else if(world[x+c][y+r].getHivePheromoneLevel() == maxHiveSoFar){
                                maxHiveCells.add(world[x+c][y+r]);
                            }

                            for(Cell spot : spotFound){

                                if(!maxSpotSoFarMap.containsKey(spot) || world[x+c][y+r].getSpotPheromoneLevel(spot) > maxSpotSoFarMap.get(spot)){
                                    maxSpotSoFarMap.put(spot, world[x+c][y+r].getSpotPheromoneLevel(spot));
                                }
                            }
                        }
                    }
                }

                if(world[x][y].isGoal()){
                    maxFoodSoFarMap.put(world[x][y], Cell.maxFoodPheromoneLevel);
                }

                for(Cell food : foodFound){
                    world[x][y].setFoodPheromone(food, maxFoodSoFarMap.get(food) * Ant.dropoffRate);
                }

                if(Ant.bestCellNext > chanceToTakeBest){
                    if(!maxNestCells.isEmpty()){
                        int cellIndex = (int) (maxNestCells.size()*Math.random());
                        Cell bestNestCellSoFar = maxNestCells.get(cellIndex);

                        x = bestNestCellSoFar.c;
                        y = bestNestCellSoFar.r;
                    }
                }
                else{ //give cells chance based on pheremone
                    double pheremonesSoFar = 0;
                    double goalPheromoneLevel = totalNeighborPheromones * Math.random();
                    for(Cell neighbor : allNeighborCells){
                        pheremonesSoFar+=neighbor.getNestPheromoneLevel();
                        if(pheremonesSoFar > goalPheromoneLevel){
                            x = neighbor.c;
                            y = neighbor.r;
                            break;
                        }
                    }
                }
            }
        }
        else{ //look for food

            if(world[x][y].isGoal()){
                foodFound.add(world[x][y]);
                if(Ant.allFoodRequired){
                    if(foodFound.size() >= ants.getFood().size()){
                        steps = 0;
                        returnToNest = true;
                        return;
                    }
                }
                else{
                    steps = 0;
                    returnToNest = true;
                    return;
                }
            }
            else if(world[x][y].hasNest()){
                if(steps > 1){
                    die();
                    return;
                }
            }

            double maxFoodSoFar = 0;
            double maxNestSoFar = 0;
            List<Cell> maxFoodCells = new ArrayList<Cell>();
            List<Cell> allNeighborCells = new ArrayList<Cell>();
            double totalNeighborPheromones = 0;
            Map<Cell, Double> maxFoodSoFarMap = new HashMap<Cell, Double>();
            for(int c = -1; c <=1; c++){

                if(x+c < 0 || x+c >= world.length){
                    continue;
                }

                for(int r = -1; r <= 1; r++){
                    //don't count yourself
                    if(c == 0 && r == 0){
                        continue;
                    }
                    else if(y+r < 0 || y+r >= world[0].length){
                        continue;
                    }

                    if(!world[x+c][y+r].isBlocked()){

                        allNeighborCells.add(world[x+c][y+r]);

                        if(maxFoodSoFar == 0){
                            maxFoodCells.add(world[x+c][y+r]);
                        }

                        for(Cell food : foodFound){


                            if(!maxFoodSoFarMap.containsKey(food) || world[x+c][y+r].getFoodPheromoneLevel(food) > maxFoodSoFarMap.get(food)){
                                maxFoodSoFarMap.put(food, world[x+c][y+r].getFoodPheromoneLevel(food));
                            }

                        }

                        if(world[x][y].isGoal()){
                            maxFoodSoFarMap.put(world[x][y], Cell.maxFoodPheromoneLevel);
                        }

                        for(Cell food : foodFound){
                            world[x][y].setFoodPheromone(food, maxFoodSoFarMap.get(food) * Ant.dropoffRate);
                        }

                        if(world[x+c][y+r].getNestPheromoneLevel() > maxNestSoFar){
                            maxNestSoFar = world[x+c][y+r].getNestPheromoneLevel();
                        }

                        if(ants.getFood().isEmpty()){
                            totalNeighborPheromones += 1;
                        }
                        else{
                            for(Cell food : ants.getFood()){

                                if(foodFound.contains(food)){
                                    continue;
                                }
                                totalNeighborPheromones += world[x+c][y+r].getFoodPheromoneLevel(food);

                                if(world[x+c][y+r].getFoodPheromoneLevel(food) > maxFoodSoFar){
                                    maxFoodSoFar = world[x+c][y+r].getFoodPheromoneLevel(food);
                                    maxFoodCells.clear();
                                    maxFoodCells.add(world[x+c][y+r]);
                                }
                                else if(world[x+c][y+r].getFoodPheromoneLevel(food) == maxFoodSoFar){
                                    maxFoodCells.add(world[x+c][y+r]);
                                }
                            }
                        }
                    }
                }
            }

            if(world[x][y].hasNest()){
                maxNestSoFar = Cell.maxNestPheromoneLevel;
            }

            world[x][y].setNestPheromone(maxNestSoFar * Ant.dropoffRate);

            if(Ant.bestCellNext > chanceToTakeBest){
                if(!maxFoodCells.isEmpty()){
                    int cellIndex = (int) (maxFoodCells.size()*Math.random());
                    Cell bestCellSoFar = maxFoodCells.get(cellIndex);

                    x = bestCellSoFar.c;
                    y = bestCellSoFar.r;
                }
            }
            else{ //give cells chance based on pheremone
                double pheremonesSoFar = 0;
                double goalPheromoneLevel = totalNeighborPheromones * Math.random();

                for(Cell neighbor : allNeighborCells){
                    if(ants.getFood().isEmpty()){
                        pheremonesSoFar += 1;
                        if(pheremonesSoFar > goalPheromoneLevel){

                            x = neighbor.c;
                            y = neighbor.r;
                            break;
                        }
                    }
                    else{

                        for(Cell food : ants.getFood()){
                            if(foodFound.contains(food)){
                                continue;
                            }
                            pheremonesSoFar+=neighbor.getFoodPheromoneLevel(food);
                            if(pheremonesSoFar > goalPheromoneLevel){

                                x = neighbor.c;
                                y = neighbor.r;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public int getCol() {
        return x;
    }

    public int getRow(){
        return y;
    }

    public boolean isReturningHome() {
        return returnToNest;
    }
}