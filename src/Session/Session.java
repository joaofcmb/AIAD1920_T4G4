package Session;

import Dealer.Player;

import java.util.HashMap;
import java.util.LinkedList;

public class Session {

    /**
     * Session deck
     */
    private Deck deck;

    /**
     * Session players structure
     */
    private LinkedList<Player> currPlayers;

    /**
     * Players in current game. Don´t contains those players who fold theirs hands
     */
    private LinkedList<Player> inGamePlayers;

    /**
     * Current game maximum bet
     */
    private int maxBet;

    /**
     * Stores players bets history
     */
    private HashMap<String, LinkedList<String>> bets = new HashMap<>();

    /**
     * Table cards
     */
    private LinkedList<Card> table = new LinkedList<>();

    /**
     * Session constructor
     * @param currPlayers Players in session
     */
    public Session(LinkedList<Player> currPlayers, int bigBlind) {
        this.deck = new Deck();
        this.currPlayers = currPlayers;
        this.inGamePlayers = currPlayers;
        this.maxBet = bigBlind;
    }

    /**
     * Returns deck of cards
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Returns the current players list
     */
    public LinkedList<Player> getCurrPlayers() {
        return currPlayers;
    }

    /**
     * Returns session table
     */
    public LinkedList<Card> getTable() {
        return table;
    }

    /**
     * Returns current small blind player
     */
    public Player getSmallBlind() {
        return this.currPlayers.get(0);
    }

    /**
     * Returns current big blind player
     */
    public Player getBigBlind() {
        return this.currPlayers.get(1);
    }


    public HashMap<String, LinkedList<String>> getBets() {
        return bets;
    }

    public void addBet(String playerName, String bet) {
        if(this.bets.containsKey(playerName)) {
            this.bets.get(playerName).push(bet);
        }
        else {
            LinkedList<String> bets = new LinkedList<>(); bets.push(bet);
            this.bets.put(playerName, bets);
        }
    }

    public int getMaxBet() {
        return maxBet;
    }

    public void updateMaxBet(int value) {
        this.maxBet = value;
    }

    public LinkedList<Player> getInGamePlayers() {
        return inGamePlayers;
    }
}

