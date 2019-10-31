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
     * Player current bet in a specific betting phase
     */
    private int currBet;

    /**
     * Player current hand final value
     */
    private int currHandFinalValue;

    /**
     * Player constructor
     * @param chips player buy in
     * @param player agent
     */
    Player(int chips, AID player) {
        this.pot = 0;
        this.currBet = 0;
        this.currHandFinalValue = 0;
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
    public void resetPot() {
        this.pot = 0;
    }

    /**
     * Updates player pot
     */
    public void updatePot(int value) {
        this.pot += value;
        this.currBet += value;
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

    /**
     * Returns current bet
     */
    public int getCurrBet() {
        return currBet;
    }

    /**
     * Updates current bet value
     */
    public void updateCurrBet(int value) {
        this.currBet += value;
    }

    /**
     * Resets current bet value to 0
     */
    public void resetCurrBet() {
        this.currBet = 0;
    }

    /**
     * Returns current hand final value
     */
    public int getCurrHandFinalValue() {
        return currHandFinalValue;
    }

    /**
     * Updates current hand final value
     * @param currHandFinalValue new value
     */
    public void setCurrHandFinalValue(int currHandFinalValue) {
        this.currHandFinalValue = currHandFinalValue;
    }
}
