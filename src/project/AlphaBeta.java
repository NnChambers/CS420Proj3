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
 * The AlphaBeta class is used to create the object that acts as an AI and
 * determines all of the AI's moves. It uses alpha-beta pruning along with an
 * overly complicated evaluation method to produce seemingly intelligent moves
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
	private long limit;

	/**
	 * depth of the algorithm (to be used with IDDFS)
	 */
	private int depth;

	/**
	 * whether or not the AI got to go first. If the AI didn't get first turn,
	 * it changes its behavior from trying to win to trying to force a draw
	 */
	private boolean first;

	public AlphaBeta(int lim, boolean b) {
		this.limit = (long) (lim * 1000);
		this.first = b;
	}

	public Action absearch(State state) {
		depth = Integer.MAX_VALUE;
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
					score = minValue(state);
					System.out.printf("%+4d", score);
					if (score > best) {
						mi = i;
						mj = j;
						best = score;
					}
					state.undo(i, j); // undo move
				} else
					System.out.print(" [] ");

				// Potential random choice area. need list and PRNG
			}
			System.out.println();

		}
		System.out.println(best);
		return new Action(mi, mj);
	}

	private int maxValue(State state) {
		if (cutoff() || depth <= 0 || state.spaces <= 0
				|| state.checkWin() != 0)
			return eval(state);

		int best = alpha;
		depth--;
		for (int i = 0; i < state.board.length; i++) {
			for (int j = 0; j < state.board.length; j++) {
				if (state.board[i][j] == 0) {
					state.move(i, j, true);
					best = Integer.max(best, minValue(state));
					state.undo(i, j);
					if (best >= beta)
						return beta;
				}
			}
		}
		return best;
	}

	private int minValue(State state) {
		if (cutoff() || depth <= 0 || state.spaces <= 0
				|| state.checkWin() != 0)

			return eval(state);
		int best = beta;
		depth--;
		for (int i = 0; i < state.board.length; i++) {
			for (int j = 0; j < state.board.length; j++) {
				if (state.board[i][j] == 0) {
					state.move(i, j, false);
					best = Integer.min(best, maxValue(state));
					state.undo(i, j);
					if (best <= alpha)
						return alpha;
				}
			}
		}
		return best;
	}

	/**
	 * Program will return the best solution found so far given a specific
	 * period of time
	 * 
	 * @return
	 */
	private boolean cutoff() {
		if (System.currentTimeMillis() - startTime > limit)
			return true;
		return false;
	}

	/**
	 * Calculates the score of a board state
	 * 
	 * @param s
	 * @return
	 */
	private int eval(State s) {
		if (s.checkWin() == 1)
			return Integer.MAX_VALUE / 2;
		if (s.checkWin() == -1)
			return Integer.MIN_VALUE / 2;
		if (s.spaces == 0)
			return 0;
		int score = 0;

		for (int i = 0; i < s.board.length; i++)
			for (int j = 0; j < s.board.length; j++)
				score += eval(s, i, j);
		return score;
	}

	/**
	 * Checks 3 spaces in each direction from a space to count for potential
	 */
	private int eval(State s, int i, int j) {
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

		// check immediate diagonals, mostly to help break ties
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