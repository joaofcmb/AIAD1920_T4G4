package Session;

import java.util.ArrayList;
import java.util.Collections;

class Deck {

    /**
     * Decks suits
     */
    private String[] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};

    /**
     * Deck ranks
     */
    private String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10",
            "Jack", "Queen", "King", "Ace"};

    /**
     * Deck structure
     */
    private ArrayList<Card> deck = new ArrayList<>();


    /**
     * Deck default constructor
     */
    Deck() {
        for (String rank : this.ranks) {
            for (String suit : this.suits) {
                this.deck.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(this.deck);
    }

    /**
     * Retrieves the top card and removes it from deck
     * @return Top card
     */
    Card getCard() {
        return this.deck.remove(0);
    }
}
