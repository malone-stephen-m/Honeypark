import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import static java.lang.System.out;
/**
 * Class for displaying and updating an ant colony simulation.
 * Another class should be used to control the simulation
 * by calling the methods of this class.
 * 
 * Original author: Kevin Workman
 * Modified by: Ari Kapusta and Adam Cantor
 */
@SuppressWarnings("serial")
public class Ants extends JPanel{

	public static enum Pattern{
		Clear, Random, Filled;
	}

	public static enum Tile{
		OBSTACLE, NEST, GOAL, CLEAR; 
	}

	private Tile tile = Tile.GOAL;

	int rows = 25;
	int columns = 25;
	Cell [][] cellArray = new Cell[columns][rows];

	private int maxAnts = 100;
	private List<Ant> ants = new ArrayList<Ant>();

	private Set<Cell> nests = new HashSet<Cell>();
	private Set<Cell> food = new HashSet<Cell>();
	private Set<Cell> bfs_frontier = new HashSet<Cell>();
	private Set<Cell> bfs_explored = new HashSet<Cell>();
	
	private List<State> bfs_frontier_list = new ArrayList<State>();
	private List<State> bfs_explored_list = new ArrayList<State>();
	

	AdvancedControlPanel advancedControlPanel;
	
	final JInternalFrame aboutFrame = new JInternalFrame("About", false, true);
	final JInternalFrame messageFrame = new JInternalFrame("Getting Started", false, true);
	

	public Ants(){

		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		
		

		

		

		

		setBackground(Color.WHITE);

		killAllCells();
	}

	public void setGridSize(int columns, int rows){
		this.columns = columns;
		this.rows = rows;
		cellArray = new Cell[columns][rows];
		killAllCells();
		ants.clear();

		cellArray[columns/2][rows/2].setHasNest(true);
		nests.add(cellArray[columns/2][rows/2]);
		bfs_frontier_list.add(new State(new NodePlace(rows/2,columns/2),null,0));

		repaint();
	}

	public void killAllCells(){
		nests.clear();
		food.clear();
		for(int column = 0; column < columns; column++){
			for(int row = 0; row < rows; row++){
				cellArray[column][row] = new Cell(column, row);
			}
		}
		if(advancedControlPanel != null){
			advancedControlPanel.environmentChanged();
		}
		repaint();
	}

	public void setPattern(Pattern newPattern){

		killAllCells();

		if(newPattern.equals(Pattern.Filled)){
			for(int column = 0; column < columns; column++){
				for(int row = 0; row < rows; row++){
					cellArray[column][row].setIsObstacle(true);
				}
			}
		}
		else if(newPattern.equals(Pattern.Random)){
			for(int column = 0; column < columns; column++){
				for(int row = 0; row < rows; row++){
					if(Math.random() < .3){
						cellArray[column][row].setIsObstacle(true);
					}
				}
			}
			
			cellArray[columns/2][rows/2].setIsObstacle(false);
			cellArray[columns/2][rows/2].setHasNest(true);
			nests.add(cellArray[columns/2][rows/2]);
		}

		repaint();
	}

	public void paintComponent(Graphics g){

		super.paintComponent(g);

		g.setColor(Color.BLACK);

		double cellWidth = (double)getWidth()/columns;
		double cellHeight = (double)getHeight()/rows;

		if(columns <= 50 && rows <= 50){

			for(int column = 0; column < columns; column++){
				int cellX = (int) (cellWidth * column);
				g.drawLine(cellX, 0, cellX, getHeight());
			}

			for(int row = 0; row < rows; row++){
				int cellY = (int) (cellHeight * row);
				g.drawLine(0, cellY, getWidth(), cellY);
			}
		}

		for(int column = 0; column < columns; column++){
			for(int row = 0; row < rows; row++){

				int cellX = (int) (cellWidth * column);
				int cellY = (int) (cellHeight * row);

				int thisCellWidth = (int) (cellWidth*(column+1) - cellX);
				int thisCellHeight = (int) (cellHeight*(row+1) - cellY);

				if(cellArray[column][row].hasNest()){
					g.setColor(Color.ORANGE);
				}
				else if(cellArray[column][row].isGoal()){
					Random random = new Random(cellArray[column][row].hashCode());
					g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 255));
				}
				else if(cellArray[column][row].isBlocked()){
					g.setColor(Color.GRAY);
				}
				else{

					double nestPheromone = Math.min(1, (cellArray[column][row].nestPheromoneLevel-1)/Cell.maxNestPheromoneLevel);
					double foodPheromone = 0;
					double maxFood = 0;
					Cell maxFoodCell = null;

					for(Cell food : getFood()){
						if(cellArray[column][row].getFoodPheromoneLevel(food) > maxFood){
							maxFood = cellArray[column][row].getFoodPheromoneLevel(food);
							maxFoodCell = food;
						}
						foodPheromone = Math.max(foodPheromone, Math.min(1, (cellArray[column][row].getFoodPheromoneLevel(food)-1)/Cell.maxFoodPheromoneLevel));
					}

					if(nestPheromone > foodPheromone){

						g.setColor(new Color(0, 255, 0, (int) (255*nestPheromone)));
					}
					else if(maxFoodCell != null ){

						Random random = new Random(maxFoodCell.hashCode());
						g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), (int) (255*foodPheromone)));

					}
					else{
						g.setColor(Color.white);
					}
				}
				g.fillRect(cellX+1, cellY+1, Math.max(thisCellWidth-1, 1), Math.max(thisCellHeight-1, 1));
			}
		}

		for(Ant ant : ants){
			int column = ant.getCol();
			int row = ant.getRow();
			int cellX = (int) (cellWidth * column);
			int cellY = (int) (cellHeight * row);
			int thisCellWidth = (int) (cellWidth*(column+1) - cellX);
			int thisCellHeight = (int) (cellHeight*(row+1) - cellY);

			if(ant.isReturningHome()){
				g.setColor(Color.BLUE);
			}
			else{
				g.setColor(Color.BLACK);
			}

			g.fillRect(cellX+2, cellY+2, thisCellWidth-3, thisCellHeight-3);
		}
	}

	public Set<Cell> getFood(){
		return food;
	}

	public Set<Cell> getNests(){
		return nests;
	}
	
	public Set<Cell> getBfsFrontier(){
		return bfs_frontier;
	}
	
	public Set<Cell> getBfsExplored(){
		return bfs_explored;
	}

	public void step(){

		if(ants.size() < maxAnts){
			if(!nests.isEmpty()){
				int nestIndex = (int) (nests.size() * Math.random());
				ants.add(new Ant((Cell) nests.toArray()[nestIndex], cellArray, this));
			}
		}
		else if(ants.size() > maxAnts){
			ants.remove(0);
		}

		for(Ant ant : ants){
			ant.step();
		}
		
		
		/*
		 * Stuff for BFS implementation
			
		//private Node newNode = new Node(Triplet([clickedCellColumn],[clickedCellRow],0),null,0);
		//bfs_frontier_list.add(newNode);
		/*private List<State> bfs_frontier_list = new ArrayList<State>();
		private List<State> bfs_explored_list = new ArrayList<State>();
		*/
		/*
		if (bfs_frontier_list.size()==0){
			out.println("Frontier is fully explored");
		}
		for(int count = 0; count < bfs_frontier_list.size(); count++){
			State n = bfs_frontier_list.remove(0);
			NodePlace locNode = n.getNode();
			int curCost = n.getCost();
			bfs_explored_list.add(n);
			if (cellArray[locNode.getY()][locNode.getX()].isGoal()){
				out.println("found goal!");
			}
			for (int i = -1;i < 1; i ++){
				out.println(i);
				if (locNode.getX()+i < rows && locNode.getX()+i>0){
					NodePlace childx = new NodePlace(locNode.getX()+i,locNode.getY());
					int flag_exp = 0;
					int flag_fro = 0;
					//out.println("here");
					for (int j = 0; count < bfs_explored_list.size(); j++){
						if (bfs_explored_list.get(j).getNode() == childx){
							flag_exp = 1;
						}
					}
					for (int j = 0; count < bfs_frontier_list.size(); j++){
						if (bfs_frontier_list.get(j).getNode() == childx){
							flag_fro = 1;
						}
					}
					if(flag_exp == 0 && flag_fro ==0){
						bfs_frontier_list.add(new State(childx,n,curCost+1));
						out.println("added item to frontier");
					}
					
				}
				if (locNode.getY()+i < columns && locNode.getY()+i>0){
					NodePlace childy = new NodePlace(locNode.getX(),locNode.getY()+i);
					int flag_exp = 0;
					int flag_fro = 0;
					for (int j = 0; count < bfs_explored_list.size(); j++){
						if (bfs_explored_list.get(j).getNode() == childy){
							flag_exp = 1;
							out.println("item found in explored");
						}
					}
					for (int j = 0; count < bfs_frontier_list.size(); j++){
						if (bfs_frontier_list.get(j).getNode() == childy){
							flag_fro = 1;
							out.println("item found in frontier");
						}
					}
					if(flag_exp==0 && flag_fro == 0){
						bfs_frontier_list.add(new State(childy,n,curCost+1));
					}
					
				}
			}
		}*/
			
	

		for(int column = 0; column < columns; column++){
			for(int row = 0; row < rows; row++){
				cellArray[column][row].step();
			}
		}

		repaint();
	}
	
	public void addFrontierList(State a){
		bfs_frontier_list.add(a);
	}
	
	public void showAboutFrame(){
		messageFrame.dispose();
		aboutFrame.setVisible(true);
	}

	public void setTileToAdd(Tile tile) {
		this.tile = tile;
	}

	public void setMaxAnts(int maxAnts) {
		this.maxAnts = maxAnts;
		while(ants.size() > maxAnts){
			ants.remove(0);
		}
	}
	
	public void set_scenario1(){
		cellArray[rows/2][columns/2].setHasNest(false);
		nests.remove(cellArray[rows/2][columns/2]);
		cellArray[26][49].setIsGoal(true);
		food.add(cellArray[26][49]);
		
		for (int i = 0;i<40;i++){
			cellArray[i][10].setIsObstacle(true);
		}
		
		for (int i = 10;i<50;i++){
			cellArray[i][25].setIsObstacle(true);
		}
		
		for (int i = 0;i<40;i++){
			cellArray[i][40].setIsObstacle(true);
		}
		
		cellArray[26][0].setHasNest(true);
		nests.add(cellArray[26][0]);
	}
	
	public void set_scenario2(){
		cellArray[rows/2][columns/2].setHasNest(false);
		nests.remove(cellArray[rows/2][columns/2]);
		cellArray[26][49].setIsGoal(true);
		food.add(cellArray[26][49]);
		
		for (int i = 4;i<42;i++){
			cellArray[i][10].setIsObstacle(true);
		}
		for (int i = 4;i<42;i++){
			cellArray[i][20].setIsObstacle(true);
		}
		for (int i = 4;i<42;i++){
			cellArray[i][30].setIsObstacle(true);
		}
		for (int i = 4;i<42;i++){
			cellArray[i][40].setIsObstacle(true);
		}
		
		for (int i = 8;i<46;i++){
			cellArray[i][5].setIsObstacle(true);
		}
		for (int i = 8;i<46;i++){
			cellArray[i][15].setIsObstacle(true);
		}
		for (int i = 8;i<46;i++){
			cellArray[i][25].setIsObstacle(true);
		}
		for (int i = 8;i<46;i++){
			cellArray[i][35].setIsObstacle(true);
		}
		for (int i = 4;i<42;i++){
			cellArray[i][45].setIsObstacle(true);
		}
		
		for (int i = 5;i<45;i++){
			cellArray[4][i].setIsObstacle(true);
		}
		for (int i = 5;i<45;i++){
			cellArray[46][i].setIsObstacle(true);
		}
			
		cellArray[26][0].setHasNest(true);
		nests.add(cellArray[26][0]);
	}
	public void set_scen_fail1(){
		cellArray[rows/2][columns/2].setHasNest(false);
		nests.remove(cellArray[rows/2][columns/2]);
		cellArray[26][49].setIsGoal(true);
		food.add(cellArray[26][49]);
		
		for (int i = 0;i<50;i++){
			cellArray[i][20].setIsObstacle(true);
		}
		
		cellArray[26][0].setHasNest(true);
		nests.add(cellArray[26][0]);
	}
	
	public void set_scen_spiral(){
		cellArray[rows/2][columns/2].setHasNest(false);
		nests.remove(cellArray[rows/2][columns/2]);
		cellArray[25][25].setIsGoal(true);
		food.add(cellArray[25][25]);
		
		cellArray[22][7].setIsObstacle(true);
		cellArray[22][8].setIsObstacle(true);
		cellArray[23][8].setIsObstacle(true);
		cellArray[23][9].setIsObstacle(true);
		cellArray[24][9].setIsObstacle(true);
		cellArray[24][10].setIsObstacle(true);
		cellArray[25][10].setIsObstacle(true);
		cellArray[25][11].setIsObstacle(true);
		cellArray[26][11].setIsObstacle(true);
		cellArray[26][12].setIsObstacle(true);
		cellArray[27][12].setIsObstacle(true);
		cellArray[27][13].setIsObstacle(true);
		cellArray[27][14].setIsObstacle(true);
		cellArray[28][14].setIsObstacle(true);
		cellArray[28][15].setIsObstacle(true);
		cellArray[29][15].setIsObstacle(true);
		cellArray[29][16].setIsObstacle(true);
		cellArray[30][16].setIsObstacle(true);
		cellArray[30][17].setIsObstacle(true);
		cellArray[31][17].setIsObstacle(true);
		cellArray[31][18].setIsObstacle(true);
		cellArray[32][18].setIsObstacle(true);
		cellArray[32][19].setIsObstacle(true);
		
		for (int i = 20;i<30;i++){
			cellArray[32][i].setIsObstacle(true);
		}
		cellArray[31][29].setIsObstacle(true);
		for (int i = 31;i>19;i--){
			cellArray[i][30].setIsObstacle(true);
		}
		
		for (int i = 30;i>20;i--){
			cellArray[19][i].setIsObstacle(true);
		}
				
		cellArray[25][0].setHasNest(true);
		nests.add(cellArray[25][0]);
	}
	
}