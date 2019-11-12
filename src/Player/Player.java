package Player;

import Player.SessionServer.SearchSessionServer;
import Session.Card;
import jade.core.AID;
import jade.core.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
    public enum State {INIT, SEARCHING_SESSION, JOINING_SESSION, IN_SESSION;}
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
     * Table cards
     */
    private ArrayList<Card> table = new ArrayList<>();

    /**
     * Player cards
     */
    private ArrayList<Card> cards = new ArrayList<>();

    /**
     * Stores players bets history
     */
    private HashMap<String, LinkedList<String>> bets = new HashMap<>();

    /**
     * Agent initializations
     */
    protected void setup() {
        final Object[] playerSettings = getArguments();

        if (playerSettings != null && playerSettings.length == 2) {
            this.buyIn = Integer.parseInt((String) playerSettings[0]);
            this.personality = new Personality(this, (String) playerSettings[1]);

            // Updates player state and adds a TickerBehaviour that schedules a request for a session every X seconds
            this.playerState = State.SEARCHING_SESSION;
            addBehaviour(new SearchSessionServer(this, 1000));
        }
        else if (playerSettings != null && playerSettings.length == 3) {
            this.buyIn = Integer.parseInt((String) playerSettings[0]);
            this.personality = new Personality(this,
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
     * Updates current bet value
     */
    public void updateCurrBet(int value) {
        this.currBet += value;
    }

    /**
     * Retrieves other players bets
     */
    public HashMap<String, LinkedList<String>> getBets() {
        return bets;
    }

    /**
     * Adds a new bet
     * @param playerName player name
     * @param bet player bet
     */
    public void addBet(String playerName, String bet) {
        if(this.bets.containsKey(playerName)) {
            this.bets.get(playerName).push(bet);
        }
        else {
            LinkedList<String> bets = new LinkedList<>(); bets.push(bet);
            this.bets.put(playerName, bets);
        }
    }

    public void println(String msg) {
        System.out.println(this.getName() + ":: " + msg);
    }
}
