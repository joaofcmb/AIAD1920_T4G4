package Player;

import Session.Card;

import java.util.ArrayList;
import java.util.LinkedList;

abstract public class Personality {
    final Player player;

    Personality(Player player) {
        this.player = player;
    }

    abstract public String betAction(String[] bettingOptions);

    // EHS = HS * (1 - NPOT) + (1 - HS) * PPOT
    double effectiveHandStrength() {
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
        for (ArrayList<Card> oppCards : Card.cardsGenerator(2)) {
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

    public void updateInfo(String playerAlias, String action) {}

    void reset() {}
}
