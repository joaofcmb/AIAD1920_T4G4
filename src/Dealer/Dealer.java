package Dealer;

import jade.core.Agent;

public class Dealer extends Agent {
    private int smallBlind, bigBlind;
    private int loweBuyIn, upperBuyIn;

    @Override
    protected void setup() {
        final Object[] tableSettings = getArguments();

        if (tableSettings != null && tableSettings.length == 4) {
            this.smallBlind = Integer.parseInt((String) tableSettings[0]);
            this.bigBlind = Integer.parseInt((String) tableSettings[1]);
            this.loweBuyIn = Integer.parseInt((String) tableSettings[2]);
            this.upperBuyIn = Integer.parseInt((String) tableSettings[3]);
        }
    }
}
