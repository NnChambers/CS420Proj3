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
public class Action {
	protected int i;
	protected int j;

	public Action(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public void print() {
		System.out.println("Move: (" + i + ", " + j + ")\n");
	}
}
