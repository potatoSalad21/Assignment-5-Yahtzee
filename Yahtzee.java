/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

	public static void main(String[] args) {
		new Yahtzee().start(args);
	}

	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
        scores = new int[TURNS_PER_PLAYER][nPlayers];

        for (int t = 0; t < TURNS_PER_PLAYER; t++) {
            for (int p = 1; p <= nPlayers; p++) {
                System.out.println("[INFO] starting turn"); // testing
                display.printMessage(playerNames[p - 1] + "'s turn. Click \"Roll Dice\" button to roll the dice");
                display.waitForPlayerToClickRoll(p);
                runTurn(p, t);
            }
        }

        calcTotal(scores);
        System.out.println("[INFO] end of the game"); // testing
	}

    private void runTurn(int player, int turn) {
        int[] dice = new int[5];
        rollDice(dice);
        display.printMessage("Select the dice you wish to reroll and click the button.");

        rerollDice(dice);
        display.printMessage("Select the dice you wish to reroll and click the button.");

        rerollDice(dice);
        display.printMessage("Select a category for this roll.");

        int category = display.waitForPlayerToSelectCategory();
        countValidPoints(category, dice, player, turn)
        display.updateScorecard(category, player, scores[turn][player - 1]);
    }

    // counts the points and puts them in the scoresheet, IF the category is valid
    // TODO check for
    // - 3 of the kind, 4 of the kind, yahtzee, full house
    // - small straight, big straight
    // - anything for one through six and chance
    //
    private void countValidPoints(int category, int[] dice, int player, int turn) {
        if (category < 7) { // check if the category is valid
            for (int i = 0; i < dice.length; i++) {
                if (dice[i] == category) {
                    scores[turn][player - 1] += category;
                }
            }
        }

        // TODO some general way of checking several categories maybe
        //switch (category) {
        //    case THREE_OF_A_KIND:
        //    case FOUR_OF_A_KIND:
        //    case YAHTZEE:
        //    case FULL_HOUSE:

        //}
    }

    private void rerollDice(int[] dice) {
        display.waitForPlayerToSelectDice();

        for (int i = 0; i < 5; i++) {
            if (display.isDieSelected(i)) {
                dice[i] = rgen.nextInt(1, 6);
            }
        }

        display.displayDice(dice);
    }

    // calculate and display upper and lower scores + total
    // 1. iterate over the matrix
    // 2. sum the first part of the column (upper score)
    // 2.5. check if the upper score >= 63 and add the bonus if it is so
    // 3. calculate lower score from the column
    // 4. calculate total by summing upper and lower scores
    private void calcTotal(int[][] scores) {
        for (int t = 0; t < TURNS_PER_PLAYER; t++) {
            for (int p = 1; p <= nPlayers; p++) {

            }
        }
    }

    // generate random values for the dice and display them
    private void rollDice(int[] dice) {
        for (int i = 0; i < 5; i++) {
            dice[i] = rgen.nextInt(1, 6);
        }

        display.displayDice(dice);
    }

/* Private instance variables */
	private int nPlayers;
    private int[][] scores;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();

    private static final int TURNS_PER_PLAYER = 13;
}
