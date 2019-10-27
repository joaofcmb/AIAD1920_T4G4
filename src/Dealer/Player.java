package Dealer;

import jade.core.AID;

public class Player {

    /**
     * Player pot
     */
    private int pot;

    /**
     * Player current chips
     */
    private int chips;

    /**
     * Agent
     */
    private AID player;

    /**
     * Player constructor
     * @param chips player buy in
     * @param player agent
     */
    Player(int chips, AID player) {
        this.pot = 0;
        this.chips = chips;
        this.player = player;
    }

    /**
     * Returns player pot
     */
    public int getPot() {
        return pot;
    }

    /**
     * Resets player pot
     */
    void resetPot() {
        this.pot = 0;
    }

    /**
     * Updates player pot
     */
    public void updatePot(int value) {
        this.pot += value;
        this.updateChips(-value);
    }

    /**
     * Updates player chips
     */
    public void updateChips(int value) {
        this.chips += value;
    }

    /**
     * Returns player chips
     */
    public int getChips() {
        return this.chips;
    }

    /**
     * Returns player AID
     */
    public AID getPlayer() {
        return player;
    }
}
