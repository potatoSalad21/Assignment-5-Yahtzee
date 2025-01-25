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
		nPlayers = readPlayerNum(dialog);
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
        // 2d array for keeping scores
        scoresheet = new int[N_CATEGORIES][nPlayers];
        fillScoresheet(-1); // -1 as default values (for empty slots)

        // runs each turn for every player
        for (int t = 0; t < N_SCORING_CATEGORIES; t++) {
            for (int p = 1; p <= nPlayers; p++) {
                runTurn(p);
            }
        }

        sumScoresheet();
        endGame();
	}

    // determines the winner according to the scoresheet
    private void endGame() {
        int winner = 0;
        int topScore = 0;
        for (int p = 1; p <= nPlayers; p++) {
            if (scoresheet[TOTAL - 1][p - 1] > topScore) {
                topScore = scoresheet[TOTAL - 1][p - 1];
                winner = p;
            }
        }
        display.printMessage("Congratulations, " +
                            playerNames[winner - 1] +
                            " you won with a total score of " + topScore);
    }

    // runs a single turn for the given player
    private void runTurn(int player) {
        int[] dice = new int[N_DICE];
        rollDice(dice, player);
        rerollDice(dice);
        rerollDice(dice);

        handleCategories(dice, player);
    }

    // handles category selection and calculates scores accordingly
    private void handleCategories(int[] dice, int player) {
        display.printMessage("Select a category for this roll.");
        while (true) {
            int category = display.waitForPlayerToSelectCategory();
            // check if the category is available
            if (scoresheet[category - 1][player - 1] == -1) {
                int score = 0;
                if (validCategory(category, dice)) {
                    score = calculateScore(category, dice);
                }
                scoresheet[category - 1][player - 1] = score;
                display.updateScorecard(category, player, score);
                break;
            }

            display.printMessage("Category is already filled. Choose something else.");
        }
    }

    // sums the upper and lower parts of the scoresheet and fills it
    private void sumScoresheet() {
        for (int p = 1; p <= nPlayers; p++) {
            int upperScore = 0;
            int lowerScore = 0;
            // sum upper scores
            upperScore = sumScoresheetPart(p, ONES, SIXES, UPPER_SCORE);
            handleBonusPoints(upperScore, p);
            // sum lower score
            lowerScore = sumScoresheetPart(p, THREE_OF_A_KIND, CHANCE, LOWER_SCORE);

            displayTotalScore(p, upperScore, lowerScore);
        }
    }

    // calculates and displays total score based on upper and lower ones + bonus
    private void displayTotalScore(int player, int upperScore, int lowerScore) {
        int sum = upperScore + lowerScore + scoresheet[UPPER_BONUS - 1][player - 1];
        scoresheet[TOTAL - 1][player - 1] = sum;
        display.updateScorecard(TOTAL, player, sum);
    }

    // calculates sum of the given part of scoresheet
    private int sumScoresheetPart(int player, int from, int to, int category) {
        int score = 0;
        for (int i = from; i <= to; i++) {
            score += scoresheet[i - 1][player - 1];
        }
        display.updateScorecard(category, player, score);
        scoresheet[category - 1][player - 1] = score;

        return score;
    }

    // check if player got the bonus points
    private void handleBonusPoints(int upperScore, int player) {
        if (upperScore >= BONUS_EDGE) {
            scoresheet[UPPER_BONUS - 1][player - 1] = BONUS_POINTS;
            display.updateScorecard(UPPER_BONUS, player, BONUS_POINTS);
        } else {
            scoresheet[UPPER_BONUS - 1][player - 1] = 0;
            display.updateScorecard(UPPER_BONUS, player, 0);
        }
    }

/*
 * Calculates scores for valid categories.
 * If the category is 1-6 returns the number of occurences,
 * otherwise calculates score according to lower categories.
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

        if (category == FULL_HOUSE &&
            occurences.containsValue(3) &&
            occurences.containsValue(2)) {
            return true;
        } else if (category == THREE_OF_A_KIND && occurences.containsValue(3)) {
            return true;
        } else if (category == FOUR_OF_A_KIND && occurences.containsValue(4)) {
            return true;
        } else if (category == YAHTZEE && occurences.containsValue(N_DICE)) {
            return true;
        }
        return false;
    }

    // check if the dice array has consecutive numbers (small straight or large straight)
    private boolean isStraight(int category, int[] dice) {
        Arrays.sort(dice);
        int count = 0;
        for (int i = 0; i < dice.length - 1; i++) {
            if (dice[i + 1] - dice[i] == 1) {
                count++;
            }
        }

        if (category == SMALL_STRAIGHT && count >= 3) {
            return true;
        } else if (category == LARGE_STRAIGHT && count >= 4) {
            return true;
        }
        return false;
    }

    // prompts the user to enter player names and validates input
    private int readPlayerNum(IODialog dialog) {
        int players = dialog.readInt("Enter number of players");
        while (players < 1 || players > MAX_PLAYERS) {
            players = dialog.readInt("Enter a valid number of players");
        }

        return players;
    }

    // reroll the selected dice and display them
    private void rerollDice(int[] dice) {
        display.printMessage("Select the dice you wish to reroll and click the button.");
        display.waitForPlayerToSelectDice();

        for (int i = 0; i < N_DICE; i++) {
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
        for (int i = 0; i < N_DICE; i++) {
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
        for (int t = 0; t < N_CATEGORIES; t++) {
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

    private static final int BONUS_EDGE = 63;
    private static final int BONUS_POINTS = 35;
}
