package pegGame;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * @author Luis Edgardo Argote Bolio
 * 
 */
public class Main {
	public static Scanner in = new Scanner(System.in);

	/**
	 * This is the main method, it reads the input and handles the program flow
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int games = in.nextInt();
		for (int game = 0; game < games; game++) {
			// Read input
			int rows = in.nextInt();
			int columns = in.nextInt();

			/*
			 * Generates a Matrix with the game board, it contains Peg elements
			 * I made it larger for simplicity, it could be about half as "wide"
			 * but that would have complicated coding.
			 * 
			 * For example, this will be ultimately generated for
			 * 5 4 0 3 1 1 2 1 3 2
			 * 
			 * R X X X L
			 *  R M X L 
			 * R M X X L
			 *  R X M L 
			 * X X X X X
			 * *G*******
			 * 
			 * Note that:
			 * X is a regular peg
			 * R means the peg is on the left edge (always bounces right)
			 * L means the peg is on the right edge (always bounces left)
			 * M means the peg is missing
			 * G is the Goal position (where we want the ball to fall)
			 * '*' and ' ' are just fillers
			 * 
			 */
			Peg[][] pegGame = new Peg[rows + 1][(columns * 2) - 1];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < (columns * 2) - 1; j++) {
					pegGame[i][j] = new Peg();
					if (i % 2 == 0) { // Even rows
						if (j % 2 == 0) { // Even columns
							if (j <= 1) {
								// If it's on the left edge (always bounces
								// right)
								pegGame[i][j].setType('R');
							} else if (j >= (columns * 2) - 3) {
								// If it's on the right edge (always bounces
								// left)
								pegGame[i][j].setType('L');
							} else {
								pegGame[i][j].setType('X');
							}
						}
					} else { // Odd rows
						if (j % 2 != 0) { // Even columns
							if (j <= 1) {
								// If it's on the left edge (always bounces
								// right)
								pegGame[i][j].setType('R');
							} else if (j >= (columns * 2) - 3) {
								// If it's on the right edge (always bounces
								// left)
								pegGame[i][j].setType('L');
							} else {
								// If it is a regular peg (50% chance to bounce
								// left, 50% chance to bounce right)
								pegGame[i][j].setType('X');
							}
						}
					}
				}
			}
			
			// This basically creates the pegs for the bottom row
			// (where the goal will be set)
			for (int j = 0; j < (columns * 2) - 1; j++) {
				pegGame[rows][j] = new Peg('*', 0.0);
			}

			// Gets the goal peg and sets it in the Matrix
			int goal = in.nextInt();
			pegGame[rows][1 + (goal * 2)].setType('G');

			// Gets the missing pegs and sets them in the Matrix
			int missing = in.nextInt();
			for (int j = 0; j < missing; j++) {
				int missingRow = in.nextInt();
				int missingCol = in.nextInt();

				missingCol *= 2;
				missingCol += (missingRow % 2 == 0 ? 0 : 1);

				pegGame[missingRow][missingCol].setType('M');
			}

			// Calculates the initial result which is dropped in the same 
			// slot as where we want it to land, this is quite likely to
			// be the highest
			int bestDropPoint = goal;
			double bestResult = determineDropProbability(pegGame, goal);

			// Calculates the results for dropping the ball on all other
			// slots, this is probably unnecessary as the best slot tends to
			// be the goal slot or very close to it
			for (int dropPoint = 0; dropPoint < columns - 1; dropPoint++) {
				// Prevent recalculation of the initial slot
				if (dropPoint == bestDropPoint) {
					continue;
				}
				double result = determineDropProbability(pegGame, dropPoint);
				if (result > bestResult) {
					bestResult = result;
					bestDropPoint = dropPoint;
				}
			}
			
			boolean debug = false;
			
			if(debug) {
				for (int i = 0; i < rows + 1; i++) {
					String tmp = "";
					for (int j = 0; j < (columns * 2) - 1; j++) {
						tmp += pegGame[i][j].getType();
					}
					System.out.println(tmp);
				}
			}
			
			// Formats and prints the output
			DecimalFormat df = new DecimalFormat("0.000000");
			System.out.println(bestDropPoint + " " + df.format(bestResult));
		}
	}

	/**
	 * This method calculates the probability of the ball landing on the slot
	 * we want given a drop point for the ball
	 * @param board the matrix containing the board description 
	 * @param dropPoint the point where the ball will be dropped
	 * @return
	 */
	public static double determineDropProbability(Peg[][] board, int dropPoint) {
		// Displace the drop point to correspond to a board matrix column
		dropPoint = (dropPoint * 2) + 1;
		
		// Instantiate the comparator and the priority queue that will use it
		Comparator<String> comparator = new CoordinateStringComparator();
		PriorityQueue<String> pegsToCheck = new PriorityQueue<String>(20,
				comparator);
		
		// These variables will let us know where to retrieve the final result
		int targetRow = 0;
		int targetCol = 0;

		// Insert the initial drop point into the prioritized queue
		String coordinate = (1) + "," + dropPoint;
		pegsToCheck.add(coordinate);
		// Set the probability for the initial point to 100% (1.0)
		board[1][dropPoint].setProbability(1.0);

		// While there are pegs that have a probability of being hit, this will
		// check then and pass the probabilities on "downwards"
		while (!pegsToCheck.isEmpty()) {
			// Obtain the position on the matrix that the peg corresponds to
			String[] coo = pegsToCheck.poll().split(",");
			int row = Integer.parseInt(coo[0]);
			int col = Integer.parseInt(coo[1]);
			
			if (board[row][col].isType('X')) {
				// Standard peg, add half the probability of this peg to each
				// of the pegs the ball would fall on to
				board[row + 1][col - 1].addProbability(board[row][col]
						.getProbability() / 2.0);
				board[row + 1][col + 1].addProbability(board[row][col]
						.getProbability() / 2.0);
				
				// Add those two pegs to the queue if they're not already there
				String co2 = (row + 1) + "," + (col - 1);
				if (!pegsToCheck.contains(co2)) {
					pegsToCheck.add(co2);
				}
				String co3 = (row + 1) + "," + (col + 1);
				if (!pegsToCheck.contains(co3)) {
					pegsToCheck.add(co3);
				}
			} else if (board[row][col].isType('R')) {
				// Left edge peg, add its complete probability of this peg to
				// the right peg where the ball would fall on to
				board[row + 1][col + 1].addProbability(board[row][col]
						.getProbability());
				
				// Add the peg to the queue if it's not already there
				String co3 = (row + 1) + "," + (col + 1);
				if (!pegsToCheck.contains(co3)) {
					pegsToCheck.add(co3);
				}
			} else if (board[row][col].isType('L')) {
				// Right edge peg, add its complete probability of this peg to
				// the left peg where the ball would fall on to
				board[row + 1][col - 1].addProbability(board[row][col]
						.getProbability());
				
				// Add the peg to the queue if it's not already there
				String co2 = (row + 1) + "," + (col - 1);
				if (!pegsToCheck.contains(co2)) {
					pegsToCheck.add(co2);
				}
			} else if (board[row][col].isType('M')) {
				// Missing peg, the ball will fall on to the next peg below it,
				// it is the peg 2 rows below
				board[row + 2][col].addProbability(board[row][col]
						.getProbability());

				// Add the peg to the queue if it's not already there
				String co4 = (row + 2) + "," + (col);
				if (!pegsToCheck.contains(co4)) {
					pegsToCheck.add(co4);
				}
			} else if (board[row][col].isType('G')) {
				// If the queue gets to the goal peg, obtain its position on the
				// Matrix and continue (its probability is not reset to 0 yet)
				targetRow = row;
				targetCol = col;
				continue;
			}
			// Resets the Peg's probability to 0 so that another drop point can
			// be calculated without having to have made a copy of the matrix
			board[row][col].setProbability(0.0);
		}
		
		// Stores the result before reseting the goal slot to 0 as well, then
		// returns it
		double result = board[targetRow][targetCol].getProbability();
		board[targetRow][targetCol].setProbability(0.0);
		return result;
	}
}

/**
 * This is a simple data structure for each peg that includes its type and the
 * probability it'll be hit The methods on it are very basic and, thus, not
 * commented
 * 
 * @author Luis Edgardo Argote Bolio
 */
class Peg {
	private char type;
	private double probability;

	Peg() {
		this.type = ' ';
		this.probability = 0.0;
	}

	Peg(char type, double probability) {
		this.type = type;
		this.probability = probability;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public boolean isType(char typeToCheck) {
		return (this.type == typeToCheck);
	}

	public void addProbability(double probabilityToAdd) {
		this.probability += probabilityToAdd;
	}
}

/**
 * This is the comparator I use for the Prioritized Queue for peg analysis The
 * Queue contains a String in the format "Row,Column" with the position of the
 * next peg to analyze, it is prioritized by level, that way the probability
 * accumulator value for each peg is guaranteed to contain ALL possible routes
 * that could fall onto it as all of them will be analyzed before.
 * 
 * @author Luis Edgardo Argote Bolio
 */
class CoordinateStringComparator implements Comparator<String> {
	@Override
	public int compare(String o1, String o2) {
		// Basically, split the string and parse the part we want, then subtract
		// to get which is larger.
		String[] coords1 = o1.split(",");
		String[] coords2 = o2.split(",");
		return Integer.parseInt(coords1[0]) - Integer.parseInt(coords2[0]);
	}
}
