package chess2;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Luis Edgardo Argote Bolio http://argote.mx/
 * @see http://argote.mx/
 */
public class Main {
	public static Scanner in = new Scanner(System.in);
	private static int boardSize = 16;
	private static Square[][] board = new Square[boardSize][boardSize];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int cases = in.nextInt();
		for (int caseNum = 0; caseNum < cases; caseNum++) {
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					board[i][j] = new Square();
				}
			}
			LinkedList<Piece> pieces = new LinkedList<Piece>();
			int pieceCount = in.nextInt();
			for (int pieceNum = 0; pieceNum < pieceCount; pieceNum++) {
				String type = in.next();
				int rank = in.nextInt() - 1;
				int file = in.nextInt() - 1;
				pieces.add(new Piece(type.charAt(0), rank, file));
			}
			for (int pieceNum = 0; pieceNum < pieceCount; pieceNum++) {
				placePiece(pieces.get(pieceNum));
			}
			for (int pieceNum = 0; pieceNum < pieceCount; pieceNum++) {
				calculateAttacks(pieces.get(pieceNum));
			}
			int threatened = 0;
			for (int pieceNum = 0; pieceNum < pieceCount; pieceNum++) {
				if (isThreatenend(pieces.get(pieceNum))) {
					threatened++;
				}
			}
			printBoard();
			System.out.println(threatened);
		}
	}

	/**
	 * This is a fairly simple function that basically just prints out the board
	 * status for each square
	 */
	public static void printBoard() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				System.out.print(board[i][j].toString() + "\t");
			}
			System.out.println();
		}
	}

	/**
	 * This method determines if a piece is "en prise", that is, threatened by
	 * another piece
	 * 
	 * @param piece
	 *            The piece for which the threat will be analyzed
	 * @return true if the piece is threatened, false if it's not
	 */
	public static boolean isThreatenend(Piece piece) {
		int rank = piece.getRank();
		int file = piece.getFile();

		return board[rank][file].getHazardCount() > 0 ? true : false;
	}

	/**
	 * This places a given piece in a location in the board, this is done before
	 * calculating the attacks so that blocking pieces are considered
	 * 
	 * @param piece
	 *            The piece that will be placed on the board
	 */
	public static void placePiece(Piece piece) {
		char type = piece.getType();
		int rank = piece.getRank();
		int file = piece.getFile();
		board[rank][file].setType(type);
		// System.out.println("Placing a " + type + " at " + rank + "," + file);
	}

	/**
	 * This calculates the squares that can be attacked by a given piece
	 * 
	 * @param piece
	 *            The piece that the attack will be calculated for
	 */
	public static void calculateAttacks(Piece piece) {
		char type = piece.getType();
		int rank = piece.getRank();
		int file = piece.getFile();
		switch (type) {
		// King
		case 'K':
			captureKing(rank, file);
			break;
		// Rook
		case 'R':
			captureRook(rank, file);
			break;
		// Bishop
		case 'B':
			captureBishop(rank, file);
			break;
		// Queen
		case 'Q':
			captureQueen(rank, file);
			break;
		// Knight
		case 'N':
			captureKnight(rank, file);
			break;
		// Archbishop
		case 'A':
			captureArchbishop(rank, file);
			break;
		// Nightrider
		case 'S':
			captureNightrider(rank, file);
			break;
		// Kraken
		case 'E':
			captureKraken(rank, file);
			break;
		}
	}

	/**
	 * This method calculates which positions can be captured by a King, since a
	 * King can attack any of the 8 adjacent squares at any given time, they are
	 * accessed directly.
	 * 
	 * If the index is out of bounds, the exception is caught and ignored. This
	 * decision was made for simplicity's sake.
	 * 
	 * @param rank
	 *            the rank on which the piece is located
	 * @param file
	 *            the file on which the piece is located
	 */
	public static void captureKing(int rank, int file) {
		try { // Up and left movement
			board[rank - 1][file - 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try { // Up movement
			board[rank - 1][file].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try { // Up and right movement
			board[rank - 1][file + 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try { // Left movement
			board[rank][file - 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try { // Right movement
			board[rank][file + 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try { // Down and Left movement
			board[rank + 1][file - 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try { // Down movement
			board[rank + 1][file].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try { // Down and Right movement
			board[rank + 1][file + 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * This method calculates which positions can be captured by a Rook, since a
	 * Rook can attack any number of squares up, down, left or right this moves
	 * in each of that direction from the position of the piece.
	 * 
	 * In this case, if there is a piece between the squares where piece passes
	 * through and a square after that in the same direction, its movement is
	 * blocked
	 * 
	 * If the index is out of bounds, the exception is caught and ignored. That
	 * shouldn't happen on this method though. This decision was made for
	 * simplicity's sake.
	 * 
	 * @param rank
	 *            the rank on which the piece is located
	 * @param file
	 *            the file on which the piece is located
	 */
	public static void captureRook(int rank, int file) {
		int tmpRank;
		int tmpFile;

		// Up movements
		tmpRank = rank - 1;
		while (tmpRank >= 0) {
			try {
				board[tmpRank][file].incrementHazard();
				if (board[tmpRank][file].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank--;
		}

		// Down movements
		tmpRank = rank + 1;
		while (tmpRank <= boardSize) {
			try {
				board[tmpRank][file].incrementHazard();
				if (board[tmpRank][file].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank++;
		}

		// Left movements
		tmpFile = file - 1;
		while (tmpFile >= 0) {
			try {
				board[rank][tmpFile].incrementHazard();
				if (board[rank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpFile--;
		}

		// Right movements
		tmpFile = file + 1;
		while (tmpFile <= boardSize) {
			try {
				board[rank][tmpFile].incrementHazard();
				if (board[rank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpFile++;
		}
	}

	/**
	 * This method calculates which positions can be captured by a Bishop, since
	 * a Bishop can attack any number of squares up-left, up-right, down-left or
	 * down-right this moves in each of that direction from the position of the
	 * piece.
	 * 
	 * In this case, if there is a piece between the squares where piece passes
	 * through and a square after that in the same direction, its movement is
	 * blocked
	 * 
	 * If the index is out of bounds, the exception is caught and ignored. That
	 * shouldn't happen on this method though. This decision was made for
	 * simplicity's sake.
	 * 
	 * @param rank
	 *            the rank on which the piece is located
	 * @param file
	 *            the file on which the piece is located
	 */
	public static void captureBishop(int rank, int file) {
		int tmpRank;
		int tmpFile;

		// Up and left movements
		tmpRank = rank - 1;
		tmpFile = file - 1;
		while (tmpRank >= 0 && tmpFile >= 0) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank--;
			tmpFile--;
		}

		// Up and right movements
		tmpRank = rank - 1;
		tmpFile = file + 1;
		while (tmpRank >= 0 && tmpFile <= boardSize) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank--;
			tmpFile++;
		}

		// Down and left movements
		tmpRank = rank + 1;
		tmpFile = file - 1;
		while (tmpRank <= boardSize && tmpFile >= 0) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank++;
			tmpFile--;
		}

		// Down and right movements
		tmpRank = rank + 1;
		tmpFile = file + 1;
		while (tmpRank <= boardSize && tmpFile <= boardSize) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank++;
			tmpFile++;
		}
	}

	/**
	 * This method calculates which positions can be captured by a Queen, since
	 * a Queen is basically the fusion between a Bishop and a Rook, it just
	 * calls both of those capture methods
	 * 
	 * @param rank
	 *            the rank on which the piece is located
	 * @param file
	 *            the file on which the piece is located
	 */
	public static void captureQueen(int rank, int file) {
		captureRook(rank, file);
		captureBishop(rank, file);
	}

	/**
	 * 
	 * If the index is out of bounds, the exception is caught and ignored. This
	 * decision was made for simplicity's sake.
	 * 
	 * @param rank
	 *            the rank on which the piece is located
	 * @param file
	 *            the file on which the piece is located
	 */
	public static void captureKnight(int rank, int file) {
		try {
			board[rank - 2][file - 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			board[rank - 1][file - 2].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			board[rank + 1][file - 2].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			board[rank + 2][file - 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			board[rank - 2][file + 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			board[rank - 1][file + 2].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			board[rank + 2][file + 1].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			board[rank + 1][file + 2].incrementHazard();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * This method calculates which positions can be captured by an Archbishop,
	 * since an Archbishop is basically the fusion between a Bishop and a
	 * Knight, it just calls both of those capture methods
	 * 
	 * @param rank
	 *            the rank on which the piece is located
	 * @param file
	 *            the file on which the piece is located
	 */
	public static void captureArchbishop(int rank, int file) {
		captureBishop(rank, file);
		captureKnight(rank, file);
	}

	/**
	 * This method calculates which positions can be captured by a Nightrider,
	 * since a Nightrider can attack any number of squares 1up-2left, 2up-1left,
	 * 2up-1right, 1up-2right, 1down-2right, 2down-1right, 2down-1left or
	 * 1down-2left this moves in each of that direction from the position of the
	 * piece.
	 * 
	 * In this case, if there is a piece between the squares where piece passes
	 * through and a square after that in the same direction, its movement is
	 * blocked
	 * 
	 * If the index is out of bounds, the exception is caught and ignored. That
	 * shouldn't happen on this method though. This decision was made for
	 * simplicity's sake.
	 * 
	 * @param rank
	 *            the rank on which the piece is located
	 * @param file
	 *            the file on which the piece is located
	 */
	public static void captureNightrider(int rank, int file) {
		int tmpRank;
		int tmpFile;

		// Up and left movements
		tmpRank = rank - 2;
		tmpFile = file - 1;
		while (tmpRank >= 0 && tmpFile >= 0) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank -= 2;
			tmpFile--;
		}

		// Left and up movements
		tmpRank = rank - 1;
		tmpFile = file - 2;
		while (tmpRank >= 0 && tmpFile >= 0) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank--;
			tmpFile -= 2;
		}

		// Up and right movements
		tmpRank = rank - 2;
		tmpFile = file + 1;
		while (tmpRank >= 0 && tmpFile <= boardSize) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank -= 2;
			tmpFile++;
		}

		// Right and up movements
		tmpRank = rank - 1;
		tmpFile = file + 2;
		while (tmpRank >= 0 && tmpFile <= boardSize) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank--;
			tmpFile += 2;
		}

		// Down and left movements
		tmpRank = rank + 2;
		tmpFile = file - 1;
		while (tmpRank <= boardSize && tmpFile >= 0) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank += 2;
			tmpFile--;
		}

		// Left and down movements
		tmpRank = rank + 1;
		tmpFile = file - 2;
		while (tmpRank <= boardSize && tmpFile >= 0) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank++;
			tmpFile -= 2;
		}

		// Down and right movements
		tmpRank = rank + 2;
		tmpFile = file + 1;
		while (tmpRank <= boardSize && tmpFile <= boardSize) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank += 2;
			tmpFile++;
		}

		// Right and down movements
		tmpRank = rank + 1;
		tmpFile = file + 2;
		while (tmpRank <= boardSize && tmpFile <= boardSize) {
			try {
				board[tmpRank][tmpFile].incrementHazard();
				if (board[tmpRank][tmpFile].getType() != ' ') {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			tmpRank++;
			tmpFile += 2;
		}
	}

	/**
	 * This method calculates which positions can be captured by a Kraken, since
	 * it can capture any other square it iterates through all the squares in
	 * the board and marks them as attackable.
	 * 
	 * If the index is out of bounds, the exception is caught and ignored. That
	 * shouldn't happen on this method though. This decision was made for
	 * simplicity's sake.
	 * 
	 * @param rank
	 *            the rank on which the piece is located
	 * @param file
	 *            the file on which the piece is located
	 */
	public static void captureKraken(int rank, int file) {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (i == rank && j == file) { // Skip the square it's at
					continue;
				}
				try {
					board[i][j].incrementHazard();
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}
	}
}

/**
 * This class defines each square for the board, it keeps track of a threat
 * counter and the type of piece on the square. It is very straightforward so
 * the methods are not commented
 * 
 * @author Luis Edgardo Argote Bolio
 * @see http://argote.mx/
 */
class Square {
	private int hazardCount;
	private char type;

	public Square() {
		this.hazardCount = 0;
		this.type = ' ';
	}

	public void incrementHazard() {
		this.hazardCount++;
	}

	public int getHazardCount() {
		return hazardCount;
	}

	public void setHazardCount(int hazardCount) {
		this.hazardCount = hazardCount;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String toString() {
		return "(" + type + "," + hazardCount + ")";
	}
}

/**
 * This class defines a piece object, it keeps track of its location on the
 * board (rank, file) and the type of piece it is. It is very straightforward so
 * the methods are not commented
 * 
 * @author Luis Edgardo Argote Bolio
 * @see http://argote.mx/
 */
class Piece {
	private char type;
	private int rank;
	private int file;

	public Piece(char type, int rank, int file) {
		this.type = type;
		this.rank = rank;
		this.file = file;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getFile() {
		return file;
	}

	public void setFile(int file) {
		this.file = file;
	}
}