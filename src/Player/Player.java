package Player;

import jade.core.Agent;

public class Player extends Agent {
    private int buyIn;
    private Personality personality;

    @Override
    protected void setup() {
        final Object[] playerSettings = getArguments();

        if (playerSettings != null && playerSettings.length == 2) {
            this.buyIn = Integer.parseInt((String) playerSettings[0]);
            this.personality = (Personality) playerSettings[1];
        }
    }
}
