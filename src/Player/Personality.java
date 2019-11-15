package Player;

import Session.Card;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Random;

public class Personality {
    static private Map<String, String> presets = Map.of(
                // Preset Name, handSelection:aggression
            "calling-station",  "0.40:0.20",
            "rock",             "0.90:0.20",
            "LAG",              "0.40:0.75",
            "TAG",              "0.75:0.75",
            "nit",              "0.90:0.10",
            "maniac",           "0.40:0.85"
    );

    private static final double VARIANCE_DEVIATION = 0.05;
    private PrimitiveIterator.OfDouble varianceStream =
            new Random().doubles(-VARIANCE_DEVIATION, VARIANCE_DEVIATION).iterator();

    private final Player player;
    private double handSelection, aggression;

    Personality(Player player, String presetAlias) {
        final String[] presetStats = presets.getOrDefault(presetAlias, "0.10:0.10").split(":");

        this.player = player;
        this.handSelection = Double.parseDouble(presetStats[0]);
        this.aggression = Double.parseDouble(presetStats[1]);
    }

    Personality(Player player, double handSelection, double aggression) {
        this.player = player;
        this.handSelection = handSelection;
        this.aggression = aggression;
    }

    // EHS = HS * (1 - NPOT) + (1 - HS) * PPOT
    private double effectiveHandStrength() {
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

    // Fold:Check:Bet-K:All in
    // Fold:Check:Raise-K:All in
    // Fold:All in
    // Fold:Call-K:Raise-K:All in
    // Fold:Call-K:All in
    // Fold:Check:All in
    public String betAction(String[] bettingOptions) {
        final double handEquity = effectiveHandStrength();
        final double requiredAllInEquity = (double) this.player.getBuyIn() /
                (this.player.getBuyIn() + this.player.getCurrBet());

        this.player.println("EHS: " + handEquity);
        this.player.println("All-in Equity: " + requiredAllInEquity);

        final boolean willingToPlay = handEquity > this.handSelection + varianceStream.next();

        if (bettingOptions.length == 2) {
            return willingToPlay && handEquity > requiredAllInEquity ? "All in" : "Fold";
        }
        else {
            final boolean canCheck = bettingOptions[1].equals("Check");
            final boolean canRaise = bettingOptions.length == 4;

            final int callValue = canCheck ? 0 : Integer.parseInt(bettingOptions[1].split("-")[1]);
            final int minPushValue = !canRaise ? 0 : Integer.parseInt(bettingOptions[2].split("-")[1]);

            final double requiredCallEquity = (double) callValue / (callValue + this.player.getCurrBet());
            final double minPushEquity = (double) minPushValue /
                    (minPushValue + this.player.getCurrBet());

            this.player.println("Call Equity: " + requiredCallEquity);
            this.player.println("Min Push Equity: " + minPushEquity);

            if (!willingToPlay)             return canCheck ? "Check" : "Fold";
            else if (this.player.getCurrBet() == 0) {
                if (this.aggression * handEquity > this.aggression + varianceStream.next()) {
                    return "All in";
                }
                else if (canRaise && this.aggression * handEquity > this.handSelection + varianceStream.next()) {
                    final int maxPush = (int) (.5 * this.aggression * this.player.getBuyIn());
                    final int raiseAmount = Integer.max(
                            minPushValue,
                            (int) Math.pow(maxPush, this.aggression) /
                                    this.player.getBigBlind() * this.player.getBigBlind()
                    );

                    return bettingOptions[2].split("-")[0] + "-" + raiseAmount;
                }
                else if (handEquity > this.handSelection) {
                    return canCheck ? "Check" : bettingOptions[1];
                }
                else {
                    return canCheck ? "Check" : "Fold";
                }
            }
            else {
                if (handEquity >= requiredAllInEquity) {
                    return "All in";
                }
                else if (canRaise && handEquity > minPushEquity) {
                    final int maxPush = (int) (handEquity * this.player.getCurrBet() / (1 - handEquity));
                    final int raiseAmount = Integer.max(
                            minPushValue,
                            maxPush / this.player.getBigBlind() * this.player.getBigBlind()
                    );
                    return bettingOptions[2].split("-")[0] + "-" + raiseAmount;
                }
                else if (handEquity > requiredCallEquity) {
                    return canCheck ? "Check" : bettingOptions[1];
                }
                else {
                    return canCheck ? "Check" : "Fold";
                }
            }
        }
    }
}
