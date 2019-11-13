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
            "calling-station",  "0.15:0.40",
            "rock",             "0.75:0.40",
            "LAG",              "0.15:0.75",
            "TAG",              "0.75:0.75",
            "nit",              "0.85:0.25",
            "maniac",           "0.10:0.85"
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
        if (this.player.getTable().size() < 3) {
            if (playerCards.get(0).getRank().equals(playerCards.get(1).getRank()))
                return Math.max(1d, 2 * handStrength - .5);
            else
                return 2 * handStrength - 1;
        }

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


    // Hand Selection (handSelection = 0 to 1 <=> Loose to Tight) (Determines Min of Range for any type of play (Non-folds))
    // Aggression (aggression = 0 to 1 <=> Passive to Aggressive) (Determines Min of Range for Raise/Bet instead of Call/Check)

    // Effective Hand Strength (handStrength = 0 to 1 <=> Weak to Strong)

    // handValue = 2 * handStrength - 1 (Transform handStrength from [0, 1] range to [-1, 1] range)
    //   handSelection aggression
    //               v v
    // (----------|--}-]--  )
    // -1         0      ^  1

    //               handValue

    // IF handValue <= handSelection + variance ==> Check/Fold (variance varies each turn between predefined values)
    // denormalizedAggressionRatio = (handValue - 1 + aggression) ([aggression - 1, aggression] <=> Worse to Best Hand)

    // aggressionRatio = denormalizedAggressionRatio / aggression (Ratio goes to [1-(1/agg), 1] range)
    // IF aggressionRatio <= variance ==> Check/Call

    // aggressionRatio isn't negative moving on, so we can consider aggressionRatio range 0:1)
    // Now we're in the [1-agg, 1] range (the aggressionRatio shows where the play stands within this range, normalized to [0, 1]),

    // we'll have 2 thresholds within this range (=== Min raise/bet, --- bet k*BB, .... All-in)
    //  (=========|---|..)
    // 1-agg              1

    //  0               1 (Considering our aggressionRatio is normalized, this is our effective range)
    // The first threshold is at 1-agg ([0, 1] range) (This means that the lower the aggression, the more likely you'll min-bet and vice-versa)

    // Considering the remaining range ([1-agg, 1]), the second threshold is at 1-agg of this range (The lower the aggression, less likely to all-in and vice-versa)

    // IF aggressionRatio <= 1 - aggression ==> Min Bet/Raise

    // IF aggressionRatio > (1 - aggression) * aggression + (1 - aggression) == (1 - aggression) * (1 + aggression) ==> All In

    // At this stage, aggressionRatio is within range [1 - aggression, (1 - aggression) * (1 + aggression)]:

    //                                 [1, 1+agg]  [0, agg]  [0, chips*agg]
    // k = floor( (aggressionRatio / (1 - aggression) - 1) * chips / BB)
    // ==> Bet/Raise Amount = max(minAmount, k*BB);

    public String betAction(String[] bettingOptions, int playerChips, int bigBlind) {
        final double handValue = effectiveHandStrength(), oppositeAggression = 1 - this.aggression;

        this.player.println("Hand Value: " + handValue);

        if (handValue <= this.handSelection + varianceStream.next())
            return bettingOptions[0];

        double aggressionRatio = (handValue - oppositeAggression) / aggression;

        if (aggressionRatio <= varianceStream.next())
            return bettingOptions[1];

        aggressionRatio = Double.min(0, aggressionRatio);

        if (aggressionRatio <= oppositeAggression + varianceStream.next())
            return bettingOptions[2];
        else if (aggressionRatio > oppositeAggression * (1 + this.aggression) + varianceStream.next())
            return bettingOptions[3];
        else {
            final String[] betPair = bettingOptions[2].split("-");
            return betPair[0] + "-" + Integer.max(
                    Integer.parseInt(betPair[1]),
                    (int) ((aggressionRatio / (oppositeAggression) - 1) * playerChips / bigBlind) * bigBlind
            );
        }
    }
}
