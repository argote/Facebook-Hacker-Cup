package doubleSquares;

import java.util.Scanner;


/**
 * @author Luis Edgardo Argote Bolio
 * This solution is O(n)
 */
public class Main {
	public static Scanner in = new Scanner(System.in);

	public static void main(String[] args) {
		// This gets the maximum number that can be squared
		long limit = 2147483647;
		int maxNumber = (int) Math.sqrt(limit) + 1;
		long[] squares = new long[maxNumber];

		// Calculates the squares for all the relevant numbers < maxNumber
		// There is no point in exploring other possibilities
		// This is done once at program startup
		for (int i = 0; i < maxNumber; i++) {
			squares[i] = i * i;
		}

		int inputs = in.nextInt();
		for (int i = 0; i < inputs; i++) {
			long number = in.nextInt();
			// Set the lower pointer at zero to consider all squares from 0
			int pos1 = 0;
			// Set the higher pointer at sqrt(number) to consider all squares up
			// to number
			int pos2 = (int) Math.sqrt(number);
			int count = 0;
			while (pos1 <= pos2) {
				// System.out.println("p1: " + pos1 + "\tp2: " + pos2 + "\n" +
				// squares[pos1] + " + " + squares[pos2] + " = " +
				// (squares[pos1] + squares[pos2]));
				if (squares[pos1] + squares[pos2] > number) {
					// If the sum is lower than the number, increase the right
					// (higher) number pointer
					pos2--;
				} else if (squares[pos1] + squares[pos2] < number) {
					// If the sum is lower than the number, increase the left
					// (lower) number pointer
					pos1++;
				} else {
					// Increase the match count
					count++;
					// No point in reusing the same numbers as moving only one
					// pointer will cause the sum to be different from the number
					pos1++;
					pos2--;
				}
			}
			System.out.println(count);
		}
	}
}