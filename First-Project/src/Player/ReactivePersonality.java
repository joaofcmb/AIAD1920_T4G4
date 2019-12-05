package Player;

import Dataset.DatasetManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ReactivePersonality extends Personality {
    /**
     * NonReactivePersonality TAG, used as default when there's insuficient information
     */
    private final Personality TAG;

    /**
     * List of players active in the current game (Not folded)
     */
    private Set<String> activePlayers = new HashSet<>();

    /**
     * Number of Actions performed by each player and each player's ratios, inferred from these actions on the fly
     */
    private final HashMap<String, Integer> playerActionCount    = new HashMap<>();
    private final HashMap<String, Double> playerHandSelection   = new HashMap<>();
    private final HashMap<String, Double> playerAggression      = new HashMap<>();

    /**
     * Number of times the personality acted using its reactive analysis
     */
    private int iterationsCount = 0;

    /**
     * Constructor with Dependency Injection of the Player
     * @param player agent associated with the personality
     */
    ReactivePersonality(Player player) {
        super(player);
        TAG = new NonReactivePersonality(player, "TAG");
    }

    /**
     * Chooses an action to perform based on the available options
     * @param bettingOptions actions that the Player can choose
     *
     * @return Chosen action
     */
    @Override
    public String betAction(String[] bettingOptions) {
        DatasetManager.processRound(this.player.getCards(), this.player.getTable(),
                this.activePlayers, this.player.getBuyIn()
        );

        if (this.player.getTable().isEmpty()) {
            final double handEquity = effectiveHandStrength(1d);
            this.player.printInfo("EHS: " + handEquity);

            return preFlopAction(bettingOptions[1], handEquity);
        }
        else {
            final LinkedList<Double> handSelections = new LinkedList<>();
            final LinkedList<Double> aggressions    = new LinkedList<>();

            this.player.printInfo("Pro Analysis -- Active Players: " + activePlayers);
            activePlayers.forEach((player) -> {
                final Double handSelection = playerHandSelection.get(player);
                final Double aggression = playerAggression.get(player);

                this.player.printInfo(player + " -- Hand Selection: " + handSelection);
                this.player.printInfo(player + " -- Aggression: " + aggression);

                handSelections.add(handSelection);
                aggressions.add(aggression);
            });

            if (iterationsCount++ < 2)
                return TAG.betAction(bettingOptions);
            else if (this.player.getTable().size() == 3) { // Flop
                final double handEquity = effectiveHandStrength(Math.max(
                        handSelections.stream().min(Double::compareTo).get(),
                        .5
                ));

                this.player.printInfo("EHS: " + handEquity);
                return flopAction(bettingOptions, handEquity < 0.5 ? 2 * handEquity : handEquity, handSelections);
            }
            else {
                return TAG.betAction(bettingOptions);
            }
        }
    }

    /**
     * Choses an action to perform on the pre-flop
     * @param passiveOption passive action the player can take (Usually Call or Check)
     * @param handEquity effective hand strength
     * @return action to perform
     */
    private String preFlopAction(String passiveOption, double handEquity) {
        final int raiseAmount = this.player.getBigBlind() * 4;

        if (passiveOption.equals("All in")) {
            return handEquity > 0.95 ? "All in" : "Fold";
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

    /**
     * Choses an action to perform on the flop
     * @param bettingOptions list of actions the player can take
     * @param handEquity effective hand strength
     * @param handSelections list containing the inferred hand selection of each active player
     * @return action to perform
     */
    private String flopAction(String[] bettingOptions, double handEquity, LinkedList<Double> handSelections) {
        if (handEquity > 0.85) {
            if (bettingOptions.length == 4) {
                final int minBet = Integer.parseInt(bettingOptions[2].split("-")[1]);

                if (minBet < this.player.getBigBlind() * 2)
                    return bettingOptions[2].split("-")[0] + "-" + 2 * minBet;
                else
                    return bettingOptions[1];
            }
            else {
                return bettingOptions[1];
            }
        }
        else if (handEquity > 0.55) {
            if (bettingOptions.length == 4) {
                final int minBet = Integer.parseInt(bettingOptions[2].split("-")[1]);

                if (minBet < this.player.getBuyIn() / 16) {
                    return bettingOptions[1].split("-")[0] + "-" + 4 * minBet;
                }
                else {
                    if (bettingOptions[1].equals("Check"))
                        return "Check";
                    else
                        return Integer.parseInt(bettingOptions[1].split("-")[1]) < this.player.getBuyIn() / 16 ?
                            bettingOptions[1] : "Fold";
                }
            }
            else {
                return bettingOptions.length == 3 ? bettingOptions[1] : "Fold";
            }
        }
        else {
            // Bluff
            if (bettingOptions.length == 4 && handSelections.stream().max(Double::compareTo).get() > 0.70) {
                return bettingOptions[1];
            }
            else {
                return bettingOptions[1].equals("Check") ? "Check" : "Fold";
            }
        }
    }

    /**
     * Updates a player's inferred ratios based on its action
     *
     * @param playerAlias player name
     * @param action action of that player
     */
    @Override
    public void updateInfo(String playerAlias, String action) {
        if (action.length() > 2)
            DatasetManager.processMove(playerAlias, action);

        if (action.equals("Fold"))  activePlayers.remove(playerAlias);
        else                        activePlayers.add(playerAlias);

        int actionCount = playerActionCount.compute(playerAlias, (player, count) -> count == null ? 1 : ++count);

        switch (action) {
            case "Check":
                playerHandSelection.compute(playerAlias, (player, handSelection) ->
                        handSelection == null ? .5 : (actionCount - 1) * handSelection / actionCount + .5 / actionCount
                );
                break;
            case "Fold":
                playerHandSelection.compute(playerAlias, (player, handSelection) ->
                    handSelection == null ? .2 : (actionCount - 1) * handSelection / actionCount
                );
                break;
            default:
                playerHandSelection.compute(playerAlias, (player, handSelection) ->
                        handSelection == null ? .8 : (actionCount - 1) * handSelection / actionCount + 1 / actionCount
                );

                if (action.startsWith("Call")) {
                    playerAggression.compute(playerAlias, (player, aggression) ->
                            aggression == null ? .2 : (actionCount - 1) * aggression / actionCount
                    );
                }
                else { // All in / Bet / Raise
                    playerAggression.compute(playerAlias, (player, aggression) ->
                            aggression == null ? 1 : (actionCount - 1) * aggression / actionCount + 1 / actionCount
                    );
                }
                break;
        }
    }

    /**
     * Resets the personality between games
     */
    @Override
    void reset() {
        activePlayers = new HashSet<>();
    }
}
