package billboards;

import java.util.Scanner;

/**
 * @author Luis Edgardo Argote Bolio
 * 
 */
public class Main {
	private static Scanner in = new Scanner(System.in);

	public static void main(String[] args) {
		// Gets the number of cases that we need to do
		int cases = Integer.parseInt(in.nextLine());
		for (int caseNo = 0; caseNo < cases; caseNo++) {
			// This section just parses the input so it can be used later
			String line = in.nextLine();
			int billboardWidth = Integer.parseInt(line.split(" ")[0]);
			int billboardHeight = Integer.parseInt(line.split(" ")[1]);
			String text = line.substring(line.indexOf(' ',
					line.indexOf(' ') + 1) + 1);

			// Calls the method that actually obtains the desired result
			int bestSize = getBest(text, billboardWidth, billboardHeight);

			// Prints out the results in the desired format
			System.out.println("Case #" + (caseNo + 1) + ": " + bestSize);
		}
	}

	/**
	 * This method initiates a call for different text distributions to see
	 * which one allows for the biggest font size
	 * 
	 * @param text
	 *            The actual text that must be printed out on the billboard
	 * @param bbWidth
	 *            The width of the billboard
	 * @param bbHeight
	 *            The height of the billboard
	 * @return Returns the largest possible font size given the above parameters
	 */
	private static int getBest(String text, int bbWidth, int bbHeight) {
		// Obtains the best case for the scenario where everything is written on
		// a single line
		int currentBest = (int) bbWidth / text.length() < bbHeight ? (int) bbWidth
				/ text.length()
				: bbHeight;

		String[] words = text.split(" ");
		int[] wordLength = new int[words.length];

		int longestWord = 0;
		// Determines the length of each word that needs to be placed as well as
		// the longest one
		for (int ii = 0; ii < words.length; ii++) {
			wordLength[ii] = words[ii].length();
			// Determines the length of the longest word that needs to be
			// placed which is used for pruning later on.
			if (wordLength[ii] > longestWord) {
				longestWord = wordLength[ii];
			}
		}

		int totalLength = text.length();

		// Determines the max font size for a given line count >= 2
		for (int lines = 2; lines <= words.length; lines++) {
			int bestHeight = (int) bbHeight / lines;

			// In case there is no possible way that more lines can allow for
			// bigger fonts, there is no need to keep looking at more lines
			if (currentBest >= bestHeight) {
				return currentBest;
			}

			// Decrease the totalLength count by one as there will be a space
			// replaced by a newline
			totalLength--;

			// Determine the line length cutoff
			int lineLengthCutoff = longestWord > (int) Math
					.ceil((totalLength + 0.0) / lines) ? longestWord
					: (int) Math.ceil((totalLength + 0.0) / lines);

			// Determines the max text width size for the line count in
			// characters
			int nLinesWidth = getBestWidth(wordLength, 0, lines,
					lineLengthCutoff);

			int bestWidth = (int) bbWidth / nLinesWidth;
			int bestSize = bestWidth < bestHeight ? bestWidth : bestHeight;

			// If using N lines allows for a better result than the current
			// best, use that
			if (bestSize > currentBest) {
				currentBest = bestSize;
			}
		}

		return currentBest;
	}

	/**
	 * This method gets the best possible width (in number of characters)
	 * required to accommodate the words for the initial line value in the
	 * optimum arrangement
	 * 
	 * This method is recursive in nature. This method should be called with a
	 * value for index that exists in the wordLengths array.
	 * 
	 * @param wordLengths
	 *            An int array containing the lengths of the words to put on the
	 *            billboard
	 * @param index
	 *            The index in the array of the next word that needs to be added
	 * @param lines
	 *            The remaining number of lines that should be accommodated
	 * @param lineLengthCutoff
	 *            A reference cutoff point for when to go into the next line
	 * @return The width (in number of characters) required to accommodate the
	 *         words for the initial line value in the optimum arrangement
	 */
	private static int getBestWidth(int[] wordLengths, int index, int lines,
			int lineLengthCutoff) {
		// Takes the first word
		int maxWidth = wordLengths[index];
		index++;

		// If there are no more words, return
		if (index >= wordLengths.length) {
			return maxWidth;
		}

		// The base case, all the remaining words NEED to be on the final line
		if (lines <= 1) {
			while (index < wordLengths.length) {
				maxWidth += wordLengths[index] + 1;
				index++;
			}
		} else {
			// Keeps adding words while adding it does not pass the
			// lineLengthCutoff
			while (maxWidth + wordLengths[index] + 1 <= lineLengthCutoff) {
				maxWidth += wordLengths[index] + 1; // The +1 is for the space
													// (' ')
				index++;
				// Returns if it runs out of words
				if (index >= wordLengths.length) {
					return maxWidth;
				}
			}

			// Recursive call, calculates the best possible scenario for the
			// remaining words if we exclude the border word
			int maxExcludeLast = getBestWidth(wordLengths, index, lines - 1,
					lineLengthCutoff);

			// Include the border word for the local calculation
			int maxWidth2 = maxWidth + wordLengths[index] + 1;
			index++;

			// Determines whether this line or some line "below" it is the
			// longest one for the case where the border word is excluded
			if (maxExcludeLast > maxWidth) {
				maxWidth = maxExcludeLast;
			}

			// If there is still something to add to a new line, do it
			if (index < wordLengths.length) {
				// Recursive call, calculates the best possible scenario for the
				// remaining words if we include the border word
				int maxIncludeLast = getBestWidth(wordLengths, index,
						lines - 1, lineLengthCutoff);

				// Determines whether this line or some line "below" it is the
				// longest one for the case where the border word is included
				if (maxIncludeLast > maxWidth2) {
					maxWidth2 = maxIncludeLast;
				}
			}

			// Determines whether taking the second path (including the border
			// word) eventually yields a better result than the first one
			// (excluding it).
			if (maxWidth2 < maxWidth) {
				maxWidth = maxWidth2;
			}
		}

		return maxWidth;
	}
}