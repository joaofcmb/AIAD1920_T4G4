package Player;

import Session.Card;

import java.util.ArrayList;
import java.util.LinkedList;

abstract public class Personality {
    /**
     * Agent associated with the Personality
     */
    final Player player;

    /**
     * Constructor with Dependency Injection of the Player
     * @param player agent associated with the personality
     */
    Personality(Player player) {
        this.player = player;
    }

    /**
     * Chooses an action to perform based on the available options
     * @param bettingOptions actions that the Player can choose
     *
     * @return Chosen action
     */
    abstract public String betAction(String[] bettingOptions);

    /**
     * Calculates the Effective Hand Strength ( urrent hand strength and future potential)
     * @param rangePercentage range of opponent cards to consider
     * @return Effective Hand Strength, as a value between 0 and 1
     */
    double effectiveHandStrength(double rangePercentage) {
        final ArrayList<Card> playerCards = this.player.getCards();
        Card.sort(playerCards);

        // Pre-flop Analysis
        if (this.player.getTable().size() < 3) {
            return Math.min(1d, Card.rankCards(playerCards));
        }

        final LinkedList<Card> playerHand = new LinkedList<>(this.player.getTable());
        playerHand.addAll(playerCards);

        int wins = 0, loses = 0, ties = 0;

        final int aheadIndex = 0, tieIndex = 0, behindIndex = 2;
        int[][] handPotential = new int[3][3];
        int[] hpTotal = new int[3];

        final ArrayList<ArrayList<Card>> oppCombinations = Card.playerCombinations();
        for (ArrayList<Card> oppCards : oppCombinations.subList(0, (int) (oppCombinations.size() * rangePercentage))) {
            Card.sort(oppCards);
            if (oppCards.equals(playerCards)) continue;

            LinkedList<Card> oppHand = new LinkedList<>(this.player.getTable());
            oppHand.addAll(oppCards);

            final int playerRank = Card.rankHand(playerHand), oppRank = Card.rankHand(oppHand);
            int statusIndex;

            if      (playerRank > oppRank)  { wins++;   statusIndex = aheadIndex; }
            else if (playerRank < oppRank)  { loses++;  statusIndex = tieIndex; }
            else                            { ties++;   statusIndex = behindIndex; }

            for (ArrayList<Card> tableCards : Card.possibleTables(this.player.getTable(), playerCards, oppCards)) {
                final LinkedList<Card> playerPotentialHand = new LinkedList<>(tableCards);
                playerPotentialHand.addAll(playerCards);

                final LinkedList<Card> oppPotentialHand = new LinkedList<>(tableCards);
                oppPotentialHand.addAll(oppCards);

                final int playerPotentialRank = Card.rankHand(playerPotentialHand);
                final int oppPotentialRank = Card.rankHand(oppPotentialHand);

                if      (playerPotentialRank > oppPotentialRank)    handPotential[statusIndex][aheadIndex]++;
                else if (playerPotentialRank == oppPotentialRank)   handPotential[statusIndex][tieIndex]++;
                else                                                handPotential[statusIndex][behindIndex]++;

                hpTotal[statusIndex]++;
            }
        }

        final double handStrength = (wins + ties / 2d) / (wins + ties + loses);
        final double positivePotential = (
                handPotential[behindIndex][aheadIndex] +
                        handPotential[behindIndex][tieIndex] / 2d +
                        handPotential[tieIndex][aheadIndex] / 2d
        ) / (hpTotal[behindIndex] + hpTotal[tieIndex]);
        final double negativePotential = (
                handPotential[aheadIndex][behindIndex] +
                        handPotential[tieIndex][behindIndex] / 2d +
                        handPotential[aheadIndex][tieIndex] / 2d
        ) / (hpTotal[aheadIndex] + hpTotal[tieIndex]);

        return handStrength * (1 - negativePotential) + (1 - handStrength) * positivePotential;
    }

    /**
     * Callback that allows the Personality to deal with other players' actions
     *
     * @param playerAlias player name
     * @param action action of that player
     */
    public void updateInfo(String playerAlias, String action) {}

    /**
     * Callback that allows the Personality to reset between games
     */
    void reset() {}
}
