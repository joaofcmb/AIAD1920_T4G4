package Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ReactivePersonality extends Personality {
    private final Personality TAG;

    private Set<String> activePlayers = new HashSet<>();

    private final HashMap<String, Double> playerHandSelection   = new HashMap<>();
    private final HashMap<String, Double> playerAggression      = new HashMap<>();

    private int iterationsCount = 0;
    ReactivePersonality(Player player) {
        super(player);

        TAG = new NonReactivePersonality(player, "TAG");
    }

    @Override
    public String betAction(String[] bettingOptions) {
        final double handEquity = effectiveHandStrength();

        this.player.printInfo("EHS: " + handEquity);

        if (this.player.getTable().isEmpty()) {
            return preFlopAction(bettingOptions[1], handEquity);
        }
        else {
            this.player.printInfo("Pro Analysis -- Active Players: " + activePlayers);

            if (iterationsCount++ < 10)
                return TAG.betAction(bettingOptions);
            else {
                // TODO Analysis usage
                return TAG.betAction(bettingOptions);
            }
        }
    }

    private String preFlopAction(String passiveOption, double handEquity) {
        final int raiseAmount = this.player.getBigBlind() * 4;

        if (passiveOption.equals("Check") ||
                Integer.parseInt(passiveOption.split("-")[1]) == this.player.getBigBlind()
        ) {
            if (handEquity > 0.75) {
                return this.player.getBuyIn() > raiseAmount ? "Raise-" + raiseAmount : "All in";
            }
            else if (activePlayers.size() > 2) {
                return passiveOption;
            }
            else {
                return "Fold";
            }
        }
        else {
            return handEquity > 0.75 ? passiveOption : "Fold";
        }
    }

    @Override
    public void updateInfo(String playerAlias, String action) {
        if (action.equals("Fold")) {
            activePlayers.remove(playerAlias);
        }
        else {
            activePlayers.add(playerAlias);

            // TODO Update player personality info
            playerHandSelection.put(playerAlias, 0d);
            playerAggression.put(playerAlias, 1d);
        }
    }

    @Override
    void reset() {
        activePlayers = new HashSet<>();
    }
}
