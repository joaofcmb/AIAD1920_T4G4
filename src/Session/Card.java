package Session;

public class Card {

    /**
     * Card suit
     */
    private String suit;

    /**
     * Card rank
     */
    private String rank;

    /**
     * Card constructor
     * @param suit Card suit
     * @param rank Card rank
     */
    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Returns card rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * Returns card suit
     */
    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return this.suit + "-" + this.rank;
    }
}
