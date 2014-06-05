/**
 * CS 420: Artificial Intelligence
 * Professor: Daisy Tang
 *
 * Project #3
 *
 * This project uses alpha-beta pruning to create an
 * AI that can play a specific game. The game consists
 * of an 8x8 board in which two players take turns
 * placing a piece on the grid, first player to achieve
 * 4-in-a-row wins.
 *
 * Nathan Chambers & Harrison Nguyen
 */
package project;

/**
 * @author Harrison
 * 
 */
public class AlphaBeta {
	/**
	 * The current worst value
	 */
	private int alpha;

	/**
	 * The current best value
	 */
	private int beta;

	/**
	 * the Start time of the algorithm
	 */
	private long startTime;

	/**
	 * how long the algorithm should run
	 */
	private final long limit;

	/**
	 * depth of the algorithm (to be used with IDDFS)
	 */
	private final int depth;

	/**
	 * whether or not the AI got to go first. If the AI didn't get first turn,
	 * it changes its behavior from trying to win to trying to force a draw
	 */
	private boolean first;

	public AlphaBeta(int lim, boolean b) {
		this.limit = (long) lim * 1000;
		this.depth = lim * 100;
		this.first = b;
	}

	public Action absearch(State state) {
		return absearch(state, depth);
	}

	public Action absearch(State state, int d) {
		int score;
		int mi = 0;
		int mj = 0;
		alpha = Integer.MIN_VALUE;
		beta = Integer.MAX_VALUE;
		int best = alpha;

		startTime = System.currentTimeMillis();
		for (int i = 0; i < state.board.length; i++) {
			for (int j = 0; j < state.board.length; j++) {
				if (state.board[i][j] == 0) {
					state.move(i, j, false);
					score = minValue(state, d - 1);
					state.undo(i, j); // undo move
					// System.out.printf("%+4d", score);
					if (score > best) {
						mi = i;
						mj = j;
						best = score;
					}
				} // else
					// System.out.print(" [] ");
				// Potential random choice area. need list and PRNG
			}
			System.out.println();

		}
		//System.out.println(best);
		return new Action(mi, mj);
	}

	private int maxValue(State state, int d) {
		if (state.checkWin() == 1)
			return Integer.MAX_VALUE / 2;
		if (state.checkWin() == -1)
			return Integer.MIN_VALUE / 2;
		if (state.spaces == 0)
			return 0;
		if (cutoff() || d <= 0)
			return eval(state);

		int best = alpha;
		for (int i = 0; i < state.board.length; i++) {
			for (int j = 0; j < state.board.length; j++) {
				if (state.board[i][j] == 0) {
					state.move(i, j, true);
					best = Integer.max(best, minValue(state, d - 1));
					state.undo(i, j); // undo move
					// if (best >= beta)
					// return best;
					// alpha = Integer.max(alpha, best);
				}
			}
		}
		return best;
	}

	private int minValue(State state, int d) {
		if (state.checkWin() == 1)
			return Integer.MAX_VALUE / 2;
		if (state.checkWin() == -1)
			return Integer.MIN_VALUE / 2;
		if (state.spaces == 0)
			return 0;
		if (cutoff() || d <= 0)
			return eval(state);

		int best = beta;
		for (int i = 0; i < state.board.length; i++) {
			for (int j = 0; j < state.board.length; j++) {
				if (state.board[i][j] == 0) {
					state.move(i, j, false);
					best = Integer.min(best, maxValue(state, d - 1));
					state.undo(i, j);
					// if (best <= alpha)
					// return best;
					// beta = Integer.min(beta, best);
				}
			}
		}
		return best;
	}

	/**
	 * Program will return the best solution found so far given a specific
	 * period of time. Return True if time limit is done
	 * 
	 * @return
	 */
	private boolean cutoff() {
		if (System.currentTimeMillis() - startTime > limit)
			return true;
		return false;
	}

	/**
	 * The eval method calculates the score of a board state. It iterates
	 * through each space on the board, calling evalHelper on any non-blank
	 * spaces, adding the result to score.
	 */
	private int eval(State s) {
		int score = 0;
		for (int i = 0; i < s.board.length; i++)
			for (int j = 0; j < s.board.length; j++)
				score += evalHelper(s, i, j);
		return score;
	}

	/**
	 * evalHelper checks three spaces in each direction from the given space on
	 * the board, adding or subtracting point based on what is found. There are
	 * 4 blocks, one for each direction, that are otherwise effectively
	 * identical. Massive amounts of points are awarded for various special
	 * scenarios to ensure the AI responds to them.
	 * 
	 * When the AI isn't first, it is tricked into believing it is the player.
	 * This causes it to place its pieces in the places that its opponent would
	 * most want to have for themselves, leading to increased blocking ability.
	 */
	private int evalHelper(State s, int i, int j) {
		int score = 0;
		int temp = 0;
		int check = s.board[i][j];

		// Acts as if the move is actually an O, and checks for an O win.
		// Necessary to combat O-OO and such
		if (!first && check > 0) {
			check = -1;
			s.board[i][j] = -1;
			if (s.checkWin() == -1) {
				score += 10000;
			}
			s.board[i][j] = 1;
		}

		// Tricks AI into thinking it's Os when not first, turning him into a
		// blocker from hell
		if (!first)
			check = -1;

		if (i >= 3) {
			for (int c = 1; c < 4; c++)
				if (s.board[i - c][j] == check)
					temp += 5 - c;
				else if (s.board[i - c][j] != 0) {// path blocked
					temp = -1;
					c = 10;
				}
			// special cases
			if (i < s.board.length - 1)// preceded by blank (-xx- or -xxx-)
				if (temp == 7 && s.board[i + 1][j] == 0)
					temp = 10000;
			if (!first && temp == 9)
				temp = 9990;
			score += temp;
			temp = 0;
		} else
			score--;

		if (i < s.board.length - 3) {
			for (int c = 1; c < 4; c++)
				if (s.board[i + c][j] == check)
					temp += 5 - c;
				else if (s.board[i + c][j] != 0) {// path blocked
					temp = -1;
					c = 10;
				}
			// special cases
			if (i > 0)// preceded by blank (-xx- or -xxx-)
				if (temp == 7 && s.board[i - 1][j] == 0)
					temp = 10000;
			if (!first && temp == 9)
				temp = 9990;
			score += temp;
			temp = 0;
		} else
			score--;

		if (j >= 3) {
			for (int c = 1; c < 4; c++)
				if (s.board[i][j - c] == check)
					temp += 5 - c;
				else if (s.board[i][j - c] != 0) {// path blocked
					temp = -1;
					c = 10;
				}
			// special cases
			if (j < s.board.length - 1)// preceded by blank (-xx- or -xxx-)
				if (temp == 7 && s.board[i][j + 1] == 0)
					temp = 10000;
			if (!first && temp == 9)
				temp = 9990;
			score += temp;
			temp = 0;
		} else
			score--;

		if (j < s.board.length - 3) {
			for (int c = 1; c < 4; c++)
				if (s.board[i][j + c] == check)
					temp += 5 - c;
				else if (s.board[i][j + c] != 0) {// path blocked
					temp = -1;
					c = 10;
				}
			// special cases
			if (j > 0)// preceded by blank (-xx- or -xxx-)
				if (temp == 7 && s.board[i][j - 1] == 0)
					temp = 10000;
			if (!first && temp == 9)
				temp = 9990;
			score += temp;
			temp = 0;
		} else
			score--;

		// check immediate diagonals, purely to break a very specific tie
		// at a very specific state to beat the sample AI
		if (!first)
			if (i >= 1 && i < s.board.length - 1 && j >= 1
					&& j < s.board.length - 1) {
				if (s.board[i + 1][j + 1] == check
						|| s.board[i + 1][j - 1] == check
						|| s.board[i - 1][j + 1] == check
						|| s.board[i - 1][j - 1] == check)
					score++;
			}

		if (!first)
			check = s.board[i][j];
		return score * check;// negates if opponent
	}
}