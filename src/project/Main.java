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

import java.util.Scanner;

/**
 * The main method starts by asking the player for how long the AI should take
 * to move and whether or not the player wants to go first. Both of these are
 * used to create the AI by creating an object of the AlphaBeta class. The game
 * is then played in a while loop that ends either when someone wins or the
 * number of available spaces reaches 0
 */
public class Main {
	final static int N = 8; // I like to keep such things customizable

	public static void main(String[] args) throws InterruptedException {
		Scanner sc = new Scanner(System.in);
		String input;
		AlphaBeta ai;
		Action a;
		boolean player;
		State state = new State(new int[N][N], N * N);

		System.out.println("Would you like to go first? (Y/N):");
		if (sc.nextLine().toUpperCase().charAt(0) != 'Y')
			player = false;
		else
			player = true;

		System.out
				.println("How long should the computer think about its moves (in seconds)? :");
		int limit = sc.nextInt();
		sc.nextLine();
		
		ai = new AlphaBeta(limit, !player);
		state.print();
		
		while (state.spaces > 0) {
			if (player) {
				// player move
				System.out.print("Choose your next move: ");
				input = sc.nextLine();
				while (!state.move(input, player)) {
					System.out.print("Invalid input, try again: ");
					input = sc.nextLine();
				}
			} else {
				// program move
				a = ai.absearch(state);
				// a = ai.makeMove(state);
				state.move(a.i, a.j, player);
				a.print();
			}
			if (player)
				player = false;
			else
				player = true;
			state.print();
			if (state.checkWin() != 0)
				break;
		}
		sc.close();
		switch (state.checkWin()) {
		case 0:
			System.out.println("DRAW");
			break;
		case 1:
			System.out.println("AI WINS");
			break;
		case -1:
			System.out.println("PLAYER WINS");
		}

	}
}
