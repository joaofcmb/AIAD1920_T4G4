package Player;

import Player.SessionServer.SearchSessionServer;
import Session.Card;
import jade.core.AID;
import jade.core.Agent;

import java.util.ArrayList;

public class Player extends Agent {
    /**
     * Player buy in
     */
    private int buyIn;

    /**
     * Player personality
     */
    private Personality personality;

    /**
     * Player state machine
     */
    public enum State {INIT, SEARCHING_SESSION, JOINING_SESSION, IN_SESSION}

    /**
     * Initial state
     */
    private State playerState = State.INIT;

    /**
     * Dealer of the current session
     */
    private AID dealer = null;
    /**
     * Player current bet
     */
    private int currBet = 0;

    /**
     * Session big blind value
     */
    private int bigBlind;

    /**
     * Fold status. True if folded false otherwise
     */
    private boolean foldStatus = false;

    /**
     * Table cards
     */
    private ArrayList<Card> table = new ArrayList<>();

    /**
     * Player cards
     */
    private ArrayList<Card> cards = new ArrayList<>();

    /**
     * Agent initializations
     */
    protected void setup() {
        final Object[] playerSettings = getArguments();

        if (playerSettings != null && playerSettings.length == 2) {
            this.buyIn = Integer.parseInt((String) playerSettings[0]);


            String personalityAlias = (String) playerSettings[1];
            if (personalityAlias.equals("pro"))
                this.personality = new ReactivePersonality(this);
            else
                this.personality = new NonReactivePersonality(this, personalityAlias);

            // Updates player state and adds a TickerBehaviour that schedules a request for a session every X seconds
            this.playerState = State.SEARCHING_SESSION;
            addBehaviour(new SearchSessionServer(this, 1000));
        }
        else if (playerSettings != null && playerSettings.length == 3) {
            this.buyIn = Integer.parseInt((String) playerSettings[0]);
            this.personality = new NonReactivePersonality(this,
                    Double.parseDouble((String) playerSettings[1]),
                    Double.parseDouble((String) playerSettings[2])
            );

            // Updates player state and adds a TickerBehaviour that schedules a request for a session every X seconds
            this.playerState = State.SEARCHING_SESSION;
            addBehaviour(new SearchSessionServer(this, 1000));
        }
        else {
            System.out.println("Usage:: <name>:<package_name>.<class_name>(buy_in, personality)");
            doDelete();
        }
    }

    /**
     * Agent clean-up operations
     */
    protected void takeDown() {
        System.out.println(this.getName() + " :: Terminating");
    }

    /**
     * Gets player personality
     */
    public Personality getPersonality() {
        return this.personality;
    }

    /**
     * Get player state
     */
    public State getPlayerState() {
        return playerState;
    }

    /**
     * Get player buy in
     */
    public int getBuyIn() {
        return buyIn;
    }

    /**
     * Get player's current session dealer
     */
    public AID getDealer() {
        return dealer;
    }

    /**
     * Set player state
     */
    public void setPlayerState(State playerState) {
        this.playerState = playerState;
    }

    /**
     * Set player current session dealer
     */
    public void setDealer(AID dealer) {
        this.dealer = dealer;
    }

    /**
     * Sets big blind value
     * @param bigBlind value
     */
    public void setBigBlind(int bigBlind) {
        this.bigBlind = bigBlind;
    }

    /**
     * Sets fold status to true
     */
    public void setFoldStatus() {
        this.foldStatus = true;
    }

    /**
     * Returns big blind value
     */
    public int getBigBlind() {
        return bigBlind;
    }

    /**
     * Returns fold status
     */
    public boolean getFoldStatus() {
        return foldStatus;
    }

    /**
     * Returns player cards
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * Returns table cards
     */
    public ArrayList<Card> getTable() {
        return table;
    }

    /**
     * Returns current bet value
     */
    public int getCurrBet() {
        return currBet;
    }

    /**
     * Updates the current chips and current bet based on the player bet
     *
     * @param bettingContent String containg the player bet
     */
    public void updateChips(String bettingContent) {
        final String[] bettingOptions = bettingContent.split("-");

        if (bettingOptions.length == 2) {
            int betAmount = Integer.parseInt(bettingOptions[1]);
            updateCurrBet(betAmount);
            this.buyIn -= betAmount;
        }
        else if (bettingContent.equals("All in")){
            updateCurrBet(buyIn);
            buyIn = 0;
        }
    }

    /**
     * Updates player chips
     * @param earnings total amount earned
     */
    public void updateChips(int earnings) {
        this.buyIn += earnings;
    }

    /**
     * Updates current bet value
     */
    public void updateCurrBet(int value) {
        this.currBet += value;
    }

    /**
     * Resets the current bet value to 0
     */
    private void resetCurrBet() {
        this.currBet = 0;
    }

    /**
     * Resets fold status to its default value
     */
    private void resetFoldStatus() {
        this.foldStatus = false;
    }

    /**
     * Prints player information
     * @param msg msg to be printed
     */
    public void printInfo(String msg) {
        System.out.println(this.getName() + " :: " + msg);
    }

    /**
     * Resets all variables needed to new poker session
     */
    public void resetAll() {
        this.resetFoldStatus();
        this.resetCurrBet();
        this.getPersonality().reset();
        this.getCards().clear();
        this.getTable().clear();
    }
}
