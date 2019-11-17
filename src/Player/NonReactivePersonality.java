package Player;

import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Random;

public class NonReactivePersonality extends Personality {

    /**
     * Personality presets with corresponding hand selection and aggression ratio
     */
    static private Map<String, String> presets = Map.of(
            // Preset Name, handSelection:aggression
            "calling-station",  "0.40:0.20",
            "rock",             "0.90:0.20",
            "LAG",              "0.40:0.50",
            "TAG",              "0.75:0.50",
            "nit",              "0.90:0.10",
            "maniac",           "0.30:0.70"
    );

    /**
     * Determines the deviation of the variance (0+-deviation)
     */
    static private final double VARIANCE_DEVIATION = 0.05;

    /**
     * Variance stream containing random variances bounded by its deviation
     */
    private PrimitiveIterator.OfDouble varianceStream =
            new Random().doubles(-VARIANCE_DEVIATION, VARIANCE_DEVIATION).iterator();

    /**
     * Hand selection determining the ratio of hands the player is willing to play and
     * aggression ratio determining how aggressive the player is (More Bets/Raises, less calls)
     */
    private final double handSelection, aggression;

    /**
     * Personality Constructor using one of the preset personalities
     * @param player agent
     * @param presetAlias preset personality
     */
    NonReactivePersonality(Player player, String presetAlias) {
        super(player);
        final String[] presetStats = presets.getOrDefault(presetAlias, "0.10:0.10").split(":");

        this.handSelection = Double.parseDouble(presetStats[0]);
        this.aggression = Double.parseDouble(presetStats[1]);
    }

    /**
     * Personality Constructor using specific values for the personality ratios
     * @param player agent
     * @param handSelection hand selection value
     * @param aggression aggression value
     */
    NonReactivePersonality(Player player, double handSelection, double aggression) {
        super(player);

        this.handSelection = handSelection;
        this.aggression = aggression;
    }

    /**
     * Chooses an action to perform based on the available options
     * @param bettingOptions actions that the Player can choose
     *
     * @return Chosen action
     */
    @Override
    public String betAction(String[] bettingOptions) {
        final double handEquity = effectiveHandStrength(this.player.getTable().isEmpty() ? 1d : .5d);
        final double requiredAllInEquity = (double) this.player.getBuyIn() /
                (this.player.getBuyIn() + this.player.getCurrBet());

        this.player.printInfo("EHS: " + handEquity);
        this.player.printInfo("All-in Equity: " + requiredAllInEquity);

        final boolean willingToPlay = (this.player.getTable().isEmpty() || handEquity < 0.5 ? 1 : 2) *
                handEquity > this.handSelection + varianceStream.next();

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

            this.player.printInfo("Call Equity: " + requiredCallEquity);
            this.player.printInfo("Min Push Equity: " + minPushEquity);

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
