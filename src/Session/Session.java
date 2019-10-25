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

    /**
     * Session constructor
     * @param currPlayers Players in session
     */
    public Session(LinkedList<Player> currPlayers) {
        this.deck = new Deck();
        this.currPlayers = currPlayers;
    }

}
