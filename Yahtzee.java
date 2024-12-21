/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import java.util.*;

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
        // 2d array for keeping scores
        scoresheet = new int[TURNS_PER_PLAYER][nPlayers];
        fillScoresheet(-1); // -1 as default values

        // runs each turn for every player
        for (int t = 0; t < TURNS_PER_PLAYER; t++) {
            for (int p = 1; p <= nPlayers; p++) {
                System.out.println("[LOG] starting turn"); // testing
                runTurn(p, t);
            }
        }

        calcTotal(scoresheet);
        System.out.println("[LOG] end of the game"); // testing
	}

    private void runTurn(int player, int turn) {
        int[] dice = new int[DICE_NUM];
        rollDice(dice, player);
        rerollDice(dice);
        rerollDice(dice);

        selectCategory(dice, player, turn);
    }

    private void selectCategory(int[] dice, int player, int turn) {
        display.printMessage("Select a category for this roll.");
        while (true) {
            int category = display.waitForPlayerToSelectCategory();
            // check if the category is available
            if (scoresheet[category][player - 1] == -1) {
                int score = 0;
                if (validCategory(category, dice)) {
                    score = calculateScore(category, dice);
                }
                scoresheet[category][player - 1] = score;
                display.updateScorecard(category, player, score);
                break;
            }

            display.printMessage("Category is already filled. Choose something else.");
        }
    }

/*
 * Calculates scores for valid categories.
 * if the category is 1-6 returns the number of occurences
 * of
 */
    private int calculateScore(int category, int[] dice) {
        if (category <= SIXES) {
            int score = 0;
            for (int die : dice) {
                if (die == category) {
                    score += category;
                }
            }
            return score;
        }
        return scoreCategories(category, dice);
    }

    // returns scores according to valid categories
    private int scoreCategories(int category, int[] dice) {
        switch (category) {
        case THREE_OF_A_KIND:
        case FOUR_OF_A_KIND:
            return sum(dice);
        case FULL_HOUSE:
            return 25;
        case SMALL_STRAIGHT:
            return 30;
        case LARGE_STRAIGHT:
            return 40;
        case YAHTZEE:
            return 50;
        case CHANCE:
            return sum(dice);
        }
        return 0;
    }

/*
 * Checks if the selected category is valid.
 * Three of a Kind, Four of a Kind, Full House and Yahtzee have the same general pattern;
 * So do Small Straight and Large Straight, so their checks are done together.
 */
    private boolean validCategory(int category, int[] dice) {
        switch (category) {
        case THREE_OF_A_KIND:
        case FOUR_OF_A_KIND:
        case FULL_HOUSE:
        case YAHTZEE:
            return isThreeFourYahtzeeOrFullHouse(category, dice);
        case SMALL_STRAIGHT:
        case LARGE_STRAIGHT:
            return isStraight(category, dice);
        }
        return true; // for "chance"
    }

    private boolean isThreeFourYahtzeeOrFullHouse(int category, int[] dice) {
        HashMap<Integer, Integer> occurences = new HashMap<>();
        for (int die : dice) {
            occurences.put(die, occurences.getOrDefault(die, 0) + 1);
        }

        System.out.println("[INFO] OCCURENCES: "); // testing
        occurences.forEach((k, v) -> System.out.println(k + ": " + v)); // testing
        if (category == FULL_HOUSE &&
            occurences.containsValue(3) &&
            occurences.containsValue(2)) {
            System.out.println("[LOG] FULL HOUSE -> TRUE"); // testing
            return true;
        } else if (category == THREE_OF_A_KIND && occurences.containsValue(3)) {
            System.out.println("[LOG] THREE -> TRUE"); // testing
            return true;
        } else if (category == FOUR_OF_A_KIND && occurences.containsValue(4)) {
            System.out.println("[LOG] FOUR -> TRUE"); // testing
            return true;
        } else if (category == YAHTZEE && occurences.containsValue(DICE_NUM)) {
            System.out.println("[LOG] YAHTZEE -> TRUE"); // testing
            return true;
        }
        return false;
    }

    // check if the dice array has consecutive numbers (small straight or large straight)
    private boolean isStraight(int category, int[] dice) {
        Arrays.sort(dice);
        int count = 0;
        for (int i = 0; i < dice.length - 1; i++) {
            if (dice[i+1] - dice[i] == 1) count++;
        }

        if (category == SMALL_STRAIGHT && count >= 3) {
            return true;
        } else if (category == LARGE_STRAIGHT && count >= 4) {
            return true;
        }
        return false;
    }

    // TODO sum up lower and upper scores (bonus included)
    private void calcTotal(int[][] scores) {
    }

    // reroll the selected dice and display them
    private void rerollDice(int[] dice) {
        display.printMessage("Select the dice you wish to reroll and click the button.");
        display.waitForPlayerToSelectDice();

        for (int i = 0; i < DICE_NUM; i++) {
            if (display.isDieSelected(i)) {
                dice[i] = rgen.nextInt(1, 6);
            }
        }

        display.displayDice(dice);
    }

    // generate random values for the dice and display them
    private void rollDice(int[] dice, int player) {
        display.printMessage(playerNames[player - 1] + "'s turn. Click \"Roll Dice\" button to roll the dice");
        display.waitForPlayerToClickRoll(player);
        for (int i = 0; i < DICE_NUM; i++) {
            dice[i] = rgen.nextInt(1, 6);
        }

        display.displayDice(dice);
    }

    // returns the sum of all dice values
    private int sum(int[] dice) {
        int sum = 0;
        for (int die : dice) {
            sum += die;
        }
        return sum;
    }

    // fills the scoresheet with default values
    private void fillScoresheet(int value) {
        for (int t = 0; t < TURNS_PER_PLAYER; t++) {
            for (int p = 0; p < nPlayers; p++) {
                scoresheet[t][p] = value;
            }
        }
    }

/* Private instance variables */
	private int nPlayers;
    private int[][] scoresheet;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
    private int upperScoreTotal = 0;
    private int lowerScore = 0;

    private static final int TURNS_PER_PLAYER = 13;
    private static final int DICE_NUM = 5;
}
