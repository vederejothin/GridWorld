import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ValueIteration {
	public static final double GAMMA = 0.9;
	public static void main(String[] args) {
		int actionReward = 0;
		Scanner io = new Scanner(System.in);
		System.out.println("What is the reward for each action? (Type an \"integer\")");
		actionReward = io.nextInt();
		io.close();
		
		HashMap<String, Double> action1 = new HashMap<String, Double>();
		action1.put("UP", 0.8);
		action1.put("LEFT", 0.2);
		
		HashMap<String, Double> action2 = new HashMap<String, Double>();
		action2.put("RIGHT", 0.8);
		action2.put("DOWN", 0.2);	
		
		HashMap<String, Double> action3 = new HashMap<String, Double>();
		action3.put("DOWN", 1.0);	
		
		HashMap<String, Double> action4 = new HashMap<String, Double>();
		action4.put("LEFT", 1.0);
		
		Map<String, HashMap<String, Double>> actions = new HashMap<String, HashMap<String, Double>>();		
		actions.put("UP", action1);
		actions.put("RIGHT", action2);
		actions.put("DOWN", action3);
		actions.put("LEFT", action4);
		
		double x = 0.0;
		double[][] grid = new double[][]{
			{x, x, x, x},
			{x, x, x, x},
			{x, x, x, x},
			{x, x, x, x},
			{x, x, x, x}
		};
		
		boolean converged = false;
		
		// Loop until convergence
		while(!converged) {		
			// Copy the values of the grid
			double[][] copyGrid = new double[5][4];
			for(int i = 0; i < grid.length; i++) {
				for(int j = 0; j < grid[i].length; j++) {
					copyGrid[i][j] = grid[i][j];
				}
			}
			
			// For each cell in the grid
			for(int i = 0; i < grid.length; i++) {
				for(int j = 0; j < grid[i].length; j++) {
					boolean wall = (i == 3 && j == 1) || (i == 3 && j == 3);
					if(!wall) { // Execute this if the cell is not a wall
						double start = -50.0; // Start with the least value in the grid
						for(String action : actions.keySet()) {
							double value = (double) actionReward; // -1 for each action
							boolean goal = (i == 0 && j == 3);
							boolean pit = (i == 1 && j == 1);
							if(goal) {
								value = 10.0; // +10 if the cell is the goal
							} else if(pit) {
								value = -50.0; // -50 if the cell is a pit
							}
							
							// For each effect of the action
							for(String effect : actions.get(action).keySet()) {
								// Move
								int newRow = i, newColumn = j;
								if(effect == "UP") {
									newRow--;
								} else if(effect == "DOWN") {
									newRow++;
								} else if(effect == "LEFT") {
									newColumn--;
								} else if(effect == "RIGHT") {
									newColumn++;
								}
								
								try {
									// Add the value of the new cell;
									value += GAMMA * actions.get(action).get(effect) * copyGrid[newRow][newColumn];
								} catch(ArrayIndexOutOfBoundsException e) {
									// If new cell is out of bounds, add value of current cell
									value += GAMMA * actions.get(action).get(effect) * copyGrid[i][j];
								}
							}
							
							// If the calculated value is greater than start value, it becomes the new start value
							if(value > start) {
								start = value;
							}
						}
						grid[i][j] = start;	// Assign the calculated value or start value					
					}
				}
			}
			
			// If the difference in the values of new state of grid and previous state of the grid is more that 0, then it not converged yet 
			converged = true;
			for(int i = 0; i < grid.length; i++) {
				for(int j = 0; j < grid[i].length; j++) {
					if(copyGrid[i][j] - grid[i][j] > 0.0 || grid[i][j] - copyGrid[i][j] > 0.0) {
						converged = false;
					}
				}
			}
		}
		
		// Print the grid values
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				System.out.print(String.valueOf(grid[i][j]) + "\t");
			}
			System.out.println("\n");			
		}		
	}
}
