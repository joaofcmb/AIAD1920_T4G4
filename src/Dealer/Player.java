package Dealer;

import jade.core.AID;

public class Player {

    private int pot;

    private int buyIn;

    private AID player;

    Player(int buyIn, AID player) {
        this.pot = 0;
        this.buyIn = buyIn;
        this.player = player;
    }

    public int getPot() {
        return pot;
    }

    void resetPot() {
        this.pot = 0;
    }

    public void updatePot(int value) {
        this.pot += value;
    }

    public void updateBuyIn(int value) {
        this.buyIn += value;
    }

    public int getBuyIn() {
        return buyIn;
    }

    AID getPlayer() {
        return player;
    }
}
