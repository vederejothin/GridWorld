import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class QLearning {
	public static Map<Integer, HashMap<Integer, Double>> actions;
	public static double[][][] qValues;
	public static double[][] rewards;
	public static final double ALPHA = 0.2;
	public static final double GAMMA = 0.9;
	
	public static void main(String[] args) {
		int episodeLimit = 10000;
		double actionReward = 0.0;
		Scanner io = new Scanner(System.in);
		System.out.println("How many episodes / iterations do you want to run? Default is 10000. (Type a positive \"integer\")");
		episodeLimit = io.nextInt();
		System.out.println("What is the reward for each action? (Type an \"integer\")");
		actionReward = (double) io.nextInt();
		io.close();
		
		HashMap<Integer, Double> action1 = new HashMap<Integer, Double>();
		action1.put(0, 0.8);
		action1.put(3, 0.2);
		
		HashMap<Integer, Double> action2 = new HashMap<Integer, Double>();
		action2.put(1, 0.8);
		action2.put(2, 0.2);	
		
		HashMap<Integer, Double> action3 = new HashMap<Integer, Double>();
		action3.put(2, 1.0);	
		
		HashMap<Integer, Double> action4 = new HashMap<Integer, Double>();
		action4.put(3, 1.0);
		
		actions = new HashMap<Integer, HashMap<Integer, Double>>();		
		actions.put(0, action1);
		actions.put(1, action2);
		actions.put(2, action3);
		actions.put(3, action4);
		
		rewards = new double[][]{
			{actionReward, actionReward, actionReward, 10.0},
			{actionReward, -50.0, actionReward, actionReward},
			{actionReward, actionReward, actionReward, actionReward},
			{actionReward, 0.0, actionReward, 0.0},
			{actionReward, actionReward, actionReward, actionReward}
		};
		
		double x = 0.0;
		qValues = new double[][][]{
			// {UP, RIGHT, DOWN, LEFT}
			{{x, x, x, x}, {x, x, x, x}, {x, x, x, x}, {x, x, x, x}},
			{{x, x, x, x}, {x, x, x, x}, {x, x, x, x}, {x, x, x, x}},
			{{x, x, x, x}, {x, x, x, x}, {x, x, x, x}, {x, x, x, x}},
			{{x, x, x, x}, {x, x, x, x}, {x, x, x, x}, {x, x, x, x}},
			{{x, x, x, x}, {x, x, x, x}, {x, x, x, x}, {x, x, x, x}}
		};
		int episode = 0;
		while(episode < episodeLimit) {
			int startRow = 4, startColumn = 0;
			int goalCount = 0;
			while(true) {
				int currentAction = getAction(startRow, startColumn);
				int[] position = move(startRow, startColumn, currentAction);
				int newRow = position[0];
				int newColumn = position[1];
				double maxQ = -50.0; // Default maximum of Q
				// Get best Q value in this cell
				for(int i = 0; i < 4; i++) {
					if(qValues[newRow][newColumn][i] > maxQ) {
						maxQ = qValues[newRow][newColumn][i];
					}
				}
				// Update Q value of current action in this grid
				qValues[startRow][startColumn][currentAction] = ALPHA * (rewards[newRow][newColumn] + GAMMA * (maxQ)) + (1 - ALPHA) * (qValues[startRow][startColumn][currentAction]);
				startRow = newRow;
				startColumn = newColumn;
				if(newRow == 0 && newColumn == 3) {
					goalCount++;
					episode++;
				}
				// Loop in goal for 10 steps
				if(goalCount > 10) {
					break;
				}
			}
		}
		
		System.out.println("Q - Values starting from TOP LEFT of the grid: \n");
		for(int i = 0; i < qValues.length; i++) {
			for(int j = 0; j < qValues[i].length; j++) {
				System.out.println("ROW - " + i + "\tCOLUMN - " + j);
				System.out.println("UP:" + qValues[i][j][0]);
				System.out.println("RIGHT:" + qValues[i][j][1]);
				System.out.println("DOWN:" + qValues[i][j][2]);
				System.out.println("LEFT:" + qValues[i][j][3] + "\n");
			}
			System.out.println("\n");			
		}
	}
	
	public static int getAction(int row, int column) {
		// Simitate the selection of actions based on Boltzmann distribution
		double[] actionProb = new double[4];
		double denom = 0.0;
		for(int i = 0; i < 4; i++) {
			denom += Math.exp(qValues[row][column][i]);
		}
		for(int i = 0; i < 4; i++) {
			actionProb[i] = Math.round(100 * (Math.exp(qValues[row][column][i]) / denom));
		}
		ArrayList<Integer> actionArray = new ArrayList<Integer>();
		for(int i = 0; i < 4; i++) {
			int count = 0;
			while(count < actionProb[i]) {
				actionArray.add(i);
				count++;
			}
		}
		return actionArray.get(new Random().nextInt(actionArray.size()));
	}
	
	public static int[] move(int row, int column, int action) {
		int possibleEffect = 0;
		// Simulate the probabilities of the effect
		if(actions.get(action).keySet().size() > 1) {
			ArrayList<Integer> effectArray = new ArrayList<Integer>();
			for(Integer effect : actions.get(action).keySet()) {
				for(int i = 0; i < actions.get(action).get(effect) * 10; i++) {
					effectArray.add(effect);
				}
			}
			possibleEffect = (Integer) effectArray.toArray()[new Random().nextInt(10)];
		} else {
			possibleEffect = (Integer) actions.get(action).keySet().toArray()[0];
		}
		
		// Move based on selected effect
		int newRow = row, newColumn = column;
		if(possibleEffect == 0) {
			newRow--;
		} else if(possibleEffect == 1) {
			newColumn++;
		} else if(possibleEffect == 2) {
			newRow++;
		} else if(possibleEffect == 3) {
			newColumn--;
		}
		
		// Revert if in wall or out of bounds
		if(newRow < 0 || newRow > 4 || newColumn < 0 || newColumn > 3 || (newRow == 3 && newColumn == 1) || (newRow == 3 && newColumn == 3)) {
			newRow = row;
			newColumn = column;
		}
		return new int[]{newRow, newColumn};
	}
}