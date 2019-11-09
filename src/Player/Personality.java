package Player;

import java.util.Map;
import java.util.Random;
import java.util.stream.DoubleStream;

public class Personality {
    static private Map<String, String> presets = Map.of(
                // Preset Name, handSelection:aggression
            "calling-station",  "0.25:0.25",
            "rock",             "0.75:0.25",
            "LAG",              "0.25:0.75",
            "TAG",              "0.75:0.75",
            "nit",              "0.85:0.15",
            "maniac",           "0.15:0.85"
    );

    private static final double VARIANCE_DEVIATION = 0.1;
    private DoubleStream varianceStream = new Random().doubles(-VARIANCE_DEVIATION, VARIANCE_DEVIATION);

    private double handSelection, aggression;

    Personality(String presetAlias) {
        final String[] presetStats = presets.getOrDefault(presetAlias, "0.10:0.10").split(":");

        this.handSelection = Double.parseDouble(presetStats[0]);
        this.aggression = Double.parseDouble(presetStats[1]);
    }

    Personality(double handSelection, double aggression) {
        this.handSelection = handSelection;
        this.aggression = aggression;
    }

    // Hand Selection (handSelection = 0 to 1 <=> Loose to Tight) (Determines Min of Range for any type of play (Non-folds))
    // Aggression (aggression = 0 to 1 <=> Passive to Aggressive) (Determines Min of Range for Raise/Bet instead of Call/Check)
    // Relative Hand Strength (handStrength = 0 to 1 <=> Weak to Strong)

    // handValue = 2 * handStrength - 1 (Normalize handStrength from 0:1 range to -1:1 range)

    //   handSelection aggression
    //               v v
    // (----------|--}-]--  )
    // -1         0      ^  1
    //               handValue

    // IF handValue <= handSelection + variance ==> Fold (variance varies each turn between predefined values)

    // denormalizedAggressionRatio = (handValue - aggression) (0-aggression to 1-aggression <=> Worse to Best Hand)
    // aggressionRatio = denormalizedAggressionRatio / (1 - aggression) (Ratio goes to -agg/(1-agg):1 range)

    // IF aggressionRatio <= variance ==> Call/Check
    // aggressionRatio isn't negative moving on, so we can consider aggressionRatio range 0:1)

    // Now we're in the agg:1 range (the aggressionRatio shows where the play stands within this range, normalized to 0:1),
    // we'll have 2 thresholds within this range (=== Min raise/bet, --- bet k*BB, .... All-in)

    //  (========|---|..)
    // agg              1
    //  0               1 (Considering our aggressionRatio is normalized, this is our effective range)

    // The first threshold is at 1-agg (0:1 range) (This means that the lower the aggression, the more likely you'll min-bet and vice-versa)
    // Considering the remaining range (1-agg:1), the second threshold is at 1-agg of this range (The lower the aggression, less likely to all-in and vice-versa)

    // IF aggressionRatio < 1 - aggression ==> Min Bet/Raise

    //   We need to adjust the second threshold to the range of the aggressionRatio
    //   0:1 to 0:agg  0:agg to 1-agg:1
    // TODO Finish the test of the second threshold and the calculate k for the k*BB bet multiplier
    // IF aggressionRatio > (1 - aggression) * aggression + (1 - aggression) == (1 - aggression) * (1 + aggression) ==> All In


    public String betAction(String[] bettingOptions) {
        return bettingOptions[0];
    }
}
