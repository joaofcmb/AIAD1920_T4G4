package Dealer;

import jade.core.AID;

public class Player {

    /**
     * Player total pot
     */
    private int pot;

    /**
     * Player specific bet pot
     */
    private int betPot;

    /**
     * Player current chips
     */
    private int chips;

    /**
     * Agent
     */
    private AID player;

    /**
     * Player current hand final value
     */
    private int currHandFinalValue;

    /**
     * All in status: true if all in false otherwise
     */
    private boolean allInStatus = false;

    /**
     * Fold status: true if all in false otherwise
     */
    private boolean foldStatus = false;

    /**
     * Player constructor
     * @param chips player buy in
     * @param player agent
     */
    Player(int chips, AID player) {
        this.pot = 0;
        this.betPot = 0;
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
     * Returns player bet pot
     */
    public int getBetPot() {
        return betPot;
    }

    /**
     * Updates player pot
     */
    public void updatePot(int value) {
        this.pot += value;
    }

    /**
     * Updates player bet pot
     */
    public void updateBetPot(int value) {
        this.betPot += value;
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

    /**
     * Returns all in status
     * @return True if made all in, false otherwise
     */
    public boolean getAllInStatus() {
        return allInStatus;
    }

    /**
     * Sets all in status to true
     */
    public void setAllInStatus() {
        this.allInStatus = true;
    }

    /**
     * Returns fold status
     * @return True if made fold, false otherwise
     */
    public boolean getFoldStatus() {
        return foldStatus;
    }

    /**
     * Sets fold status to true
     */
    public void setFoldStatus() {
        this.foldStatus = true;
    }

    /**
     * Reset bet pot
     */
    public void resetBetPot() {
        this.betPot = 0;
    }

    /**
     * Resets all needed variables to start new session
     */
    public void resetAll() {
        this.pot = 0;
        this.currHandFinalValue = 0;
        this.allInStatus = false;
        this.foldStatus = false;
    }
}
