package Session;

import java.util.*;

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
     * Mapping cards to values
     */
    public static HashMap<String, Integer> cardValue = new HashMap<>() {{
        put("2", 1); put("3", 2); put("4", 3); put("5", 4); put("6", 5);
        put("7", 6); put("8", 7); put("9", 8); put("10", 9); put("J", 10);
        put("Q", 11); put("K", 12); put("A", 13);
    }};

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

    public static void main(String[] args) {
        LinkedList<Card> hand = new LinkedList<>(Arrays.asList(
                new Card("Diamonds","2"), new Card("Clubs","6"),
                new Card("Clubs","5"), new Card("Spades","J"),
                new Card("Diamonds","A"), new Card("Clubs","4"),
                new Card("Clubs","3")
                ));

        Card.rankHand(hand);
    }

    /**
     * Sorts an hand in descending order
     * @param hand player hand
     */
    public static void sort(LinkedList<Card> hand) {
        hand.sort(Comparator.comparingInt(o -> Card.cardValue.get(((Card) o).rank)).reversed());
    }

    /**
     * Checks if there exists five cards in sequence
     * @param hand player hand
     * @return True if exists, false otherwise
     */
    public static boolean inSequence(LinkedList<Card> hand) {
        for(int i = 0; i < hand.size() - 4; i++)
            for(int j = i; j < i + 4; j++)
                if((Card.cardValue.get(hand.get(j).getRank()) - (Card.cardValue.get(hand.get(j+1).getRank()))) != 1)
                    break;
                else if(j == i + 3)
                    return true;

        return false;
    }

    /**
     * Checks if there exists five cards of the same suit
     * @param hand player hand
     * @return True if exists, false otherwise
     */
    public static boolean sameSuit(LinkedList<Card> hand) {
        HashMap<String, Integer> suitCards = new HashMap<>() {{
            put("Clubs", 0); put("Diamonds", 0);
            put("Hearts", 0); put("Spades", 0);
        }};

        for(Card card : hand)
            suitCards.put(card.getSuit(), suitCards.get(card.getSuit()) + 1);

        return suitCards.get("Clubs") >= 5 || suitCards.get("Diamonds") >= 5 || suitCards.get("Hearts") >= 5 ||
                suitCards.get("Spades") >= 5;
    }

    public static int rankHand(LinkedList<Card> hand) {
        // Sort hand
        Card.sort(hand);

        // Suit and sequence variables
        boolean sameSuit = Card.sameSuit(hand);
        boolean inSequence = Card.inSequence(hand);

        for (Card card : hand)
            System.out.print(card + " || ");

        System.out.println();

        if(sameSuit)
            System.out.println("SAME SUIT");
        else {
            System.out.println("NOT SAME SUIT");
        }

        if(inSequence)
            System.out.println("SEQ");
        else {
            System.out.println("NOT SEQ");
        }

        return Card.cardValue.get("4");
    }

    @Override
    public String toString() {
        return this.suit + "-" + this.rank;
    }
}
