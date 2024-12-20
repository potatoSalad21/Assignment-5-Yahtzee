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
        int turnCount = 0;
        scores = new int[13][nPlayers];

        for (int i = 1; i <= nPlayers; i++) {
            while (turnCount < 13) {
                System.out.println("[INFO] starting turn"); // testing
                display.printMessage(playerNames[i - 1] + "'s turn.");
                display.waitForPlayerToClickRoll(i);
                runTurn(i, turnCount);
                turnCount++;
            }
            turnCount = 0;
        }

        System.out.println("[INFO] end of the game"); // testing
	}

    private void runTurn(int player, int turnCount) {
        int[] dice = new int[5];
        rollDice(dice);
        rerollDice(dice);
        rerollDice(dice);

        int category = display.waitForPlayerToSelectCategory();
        if (isValidCategory(category, dice)) {
            scores[turnCount][player - 1] = sum(dice);
            display.updateScorecard(category, player, sum(dice));
        } else {
            display.updateScorecard(category, player, 0);
        }
    }

    // check if the selected category matches the dice
    private boolean isValidCategory(int category, int[] dice) {
        // TODO check for
        // - 3 of the kind, 4 of the kind, yahtzee, full house
        // - small straight, big straight
        // - anything for one through six and chance


        return true;
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

    // generate random values for the dice and display them
    private void rollDice(int[] dice) {
        for (int i = 0; i < 5; i++) {
            dice[i] = rgen.nextInt(1, 6);
        }

        display.displayDice(dice);
    }

    private int sum(int[] dice) {
        int sum = 0;
        for (int die : dice) {
            sum += die;
        }
        return sum;
    }

/* Private instance variables */
	private int nPlayers;
    private int[][] scores;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();

}
