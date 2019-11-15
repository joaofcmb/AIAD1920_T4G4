package Player;

import java.util.HashMap;
import java.util.LinkedList;

public class ReactivePersonality extends Personality {
    private final Personality TAG;

    private LinkedList<String> activePlayers = new LinkedList<>();

    private final HashMap<String, Double> playerHandSelection   = new HashMap<>();
    private final HashMap<String, Double> playerAggression      = new HashMap<>();

    ReactivePersonality(Player player) {
        super(player);

        TAG = new NonReactivePersonality(player, "TAG");
    }

    @Override
    public String betAction(String[] bettingOptions) {
        return TAG.betAction(bettingOptions);
    }
}
