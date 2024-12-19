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
        display.waitForPlayerToClickRoll(1);
        int[] dice = new int[5];
        rollDice(dice);

        while (true) {}
	}

    private void rerollDice(int[] dice) {
        display.waitForPlayerToChooseDice();
        for (int i = 0; i < 5; i++) {
            if (display.isDiceSelected(i)) {
                dice[i] = rgen.nextInt(1, 6);
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
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();

}
