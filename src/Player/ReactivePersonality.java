package Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ReactivePersonality extends Personality {
    private final Personality TAG;

    private Set<String> activePlayers = new HashSet<>();

    private final HashMap<String, Integer> playerActionCount    = new HashMap<>();
    private final HashMap<String, Double> playerHandSelection   = new HashMap<>();
    private final HashMap<String, Double> playerAggression      = new HashMap<>();

    private int iterationsCount = 0;
    ReactivePersonality(Player player) {
        super(player);

        TAG = new NonReactivePersonality(player, "TAG");
    }

    @Override
    public String betAction(String[] bettingOptions) {
        if (this.player.getTable().isEmpty()) {
            final double handEquity = effectiveHandStrength(1d);
            this.player.printInfo("EHS: " + handEquity);

            return preFlopAction(bettingOptions[1], handEquity);
        }
        else {
            this.player.printInfo("Pro Analysis -- Active Players: " + activePlayers);
            activePlayers.forEach((player) -> {
                this.player.printInfo(player + " -- Hand Selection: " + playerHandSelection.get(player));
                this.player.printInfo(player + " -- Aggression: " + playerAggression.get(player));
            });

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

        if (passiveOption.equals("All in")) {
            return handEquity > 0.9 ? "All in" : "Fold";
        }
        else if (!passiveOption.equals("Check") &&
                Integer.parseInt(passiveOption.split("-")[1]) > this.player.getBigBlind()
        ) {
            return handEquity > 0.75 ? passiveOption : "Fold";
        }
        else {
            if (handEquity > 0.75) {
                return this.player.getBuyIn() > raiseAmount ? "Raise-" + raiseAmount : "All in";
            }
            else if (handEquity > 0.55 && activePlayers.size() > 2) {
                return passiveOption;
            }
            else {
                return "Fold";
            }
        }
    }

    @Override
    public void updateInfo(String playerAlias, String action) {
        if (action.equals("Fold"))  activePlayers.remove(playerAlias);
        else                        activePlayers.add(playerAlias);

        int actionCount = playerActionCount.compute(playerAlias, (player, count) -> count == null ? 1 : count++);

        playerHandSelection.putIfAbsent(playerAlias, .5d);
        playerAggression.putIfAbsent(playerAlias, .5d);

        switch (action) {
            case "Check":
                break;
            case "Fold":
                playerHandSelection.compute(playerAlias, (player, handSelection) ->
                    handSelection == null ? 0 : actionCount * handSelection / (actionCount - 1)
                );
                break;
            default:
                playerHandSelection.compute(playerAlias, (player, handSelection) ->
                        handSelection == null ? 1 : actionCount * handSelection / (actionCount - 1) + 1 / actionCount
                );

                if (action.startsWith("Call")) {
                    playerAggression.compute(playerAlias, (player, aggression) ->
                            aggression == null ? 0 : actionCount * aggression / (actionCount - 1)
                    );
                }
                else { // All in / Bet / Raise
                    playerAggression.compute(playerAlias, (player, aggression) ->
                            aggression == null ? 1 : actionCount * aggression / (actionCount - 1) + 1 / actionCount
                    );
                }
                break;
        }

        playerHandSelection.put(playerAlias, 0d);
        playerAggression.put(playerAlias, 1d);
    }

    @Override
    void reset() {
        activePlayers = new HashSet<>();
    }
}
