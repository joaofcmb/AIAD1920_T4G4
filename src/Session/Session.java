package Session;

import Dealer.Player;

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

    private LinkedList<Card> table = new LinkedList<>();

    /**
     * Session constructor
     * @param currPlayers Players in session
     */
    public Session(LinkedList<Player> currPlayers) {
        this.deck = new Deck();
        this.currPlayers = currPlayers;
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
     *
     */
    public LinkedList<Card> getTable() {
        return table;
    }

    public Player getSmallBlind() {
        return this.currPlayers.get(0);
    }

    public Player getBigBlind() {
        return this.currPlayers.get(1);
    }
}
