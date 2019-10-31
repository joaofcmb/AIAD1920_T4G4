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
        put("7", 6); put("8", 7); put("9", 8); put("10", 9); put("Jack", 10);
        put("Queen", 11); put("King", 12); put("Ace", 13);
    }};

    /**
     * Poker hand ratings
     */
    public static enum State {ROYAL_FLUSH, STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH, STRAIGHT,
                              THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD}

    /**
     * Card constructor
     * @param suit Card suit
     * @param rank Card rank
     */
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
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

    /**
     * Sorts an hand in descending order
     * @param hand player hand
     */
    public static void sort(LinkedList<Card> hand) {
        hand.sort(Comparator.comparingInt(o -> Card.cardValue.get(((Card) o).rank)).reversed());
    }

    /**
     * Parse end to obtain as much information as possible
     * @param hand player hand
     * @return Returns information
     */
    private static HashMap<String, Integer> parseHand(LinkedList<Card> hand) {
        HashMap<String, Integer> info = new HashMap<>() {{
                put("2", 0); put("3", 0); put("4", 0); put("5", 0); put("6", 0);
                put("7", 0); put("8", 0); put("9", 0); put("10", 0); put("Jack", 0);
                put("Queen", 0); put("King", 0); put("Ace", 0); put("Clubs", 0);
                put("Diamonds", 0); put("Hearts", 0); put("Spades", 0);
            }};

        for(Card card : hand) {
            info.put(card.getRank(), info.get(card.getRank()) + 1);
            info.put(card.getSuit(), info.get(card.getSuit()) + 1);
        }

        return info;
    }

    /**
     * Checks if there exists five cards of the same suit
     * @param handInfo player hand information
     * @return Suit if exists, empty string otherwise
     */
    private static String getFlush(HashMap<String, Integer> handInfo) {
        String[] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};

        for(String suit : suits)
            if(handInfo.get(suit) >= 5)
                return suit;

        return "";
    }

    /**
     * Checks existence of possible groups
     * @param handInfo hand information
     * @param X group size
     * @return Array list of encountered groups
     */
    private static ArrayList<String> groupOfX(HashMap<String, Integer> handInfo, int X) {
        ArrayList<String> result = new ArrayList<>();
        String[] ranks = {"Ace", "King", "Queen", "Jack", "10", "9", "8", "7", "6", "5", "4", "3", "2"};

        for(String rank : ranks)
            if(handInfo.get(rank) == X)
                result.add(rank);

        return result;
    }

    /**
     * Checks if there exists five cards in sequence
     * @param hand player hand
     * @return Array with sequences, empty array otherwise
     */
    public static ArrayList<ArrayList<String>> getSequences(LinkedList<Card> hand) {
        ArrayList<ArrayList<String>> sequences = new ArrayList<>();

        // 0 1 2 3 4 5 6
        for(int i = 0; i <= hand.size() - 5; i++) {
            // Internal variables
            int j = i;
            int limit = j + 4;  // Maximum limit 6
            ArrayList<String> sequence = new ArrayList<>();

            while (j < limit) {
                int inc = 1;
                int diff = Card.cardValue.get(hand.get(j).getRank()) - Card.cardValue.get(hand.get(j + inc++).getRank());

                while (diff == 0 && limit <= 6) {
                    diff = Card.cardValue.get(hand.get(j).getRank()) - Card.cardValue.get(hand.get(j + inc++).getRank());
                    limit++;
                }

                if(limit > 6 || diff > 1)
                    break;
                else {  // Diff equal 1
                    sequence.add(hand.get(j).getRank() + "-" + hand.get(j).getSuit());

                    if(j == limit - 1)
                        sequence.add(hand.get(j+1).getRank() + "-" + hand.get(j+1).getSuit());

                    j += inc - 1;
                }
            }

            if(j == limit && sequence.size() == 5)
                sequences.add(sequence);
        }
        return sequences;
    }

    /**
     * Ranks hand to a value from 1 to 900
     * @param hand player hand
     * @return hand ranking
     */
    public static int rankHand(LinkedList<Card> hand) {
        // Sort hand
        Card.sort(hand);

        // Hand variables
        HashMap<String, Integer> handInfo = Card.parseHand(hand);
        String sameSuit = Card.getFlush(handInfo);
        ArrayList<ArrayList<String>> sequences = Card.getSequences(hand);

        // State machine variables
        State state = !sameSuit.equals("") && !sequences.isEmpty() ? State.ROYAL_FLUSH : State.FOUR_OF_A_KIND;

        while(true) {
            switch (state) {
                case ROYAL_FLUSH: // Points [900]
                    for(ArrayList<String> seq : sequences) {
                        String[] card = seq.get(0).split("-");
                        if(card[0].equals("Ace") && card[1].equals(sameSuit))
                            return 900;
                    }
                    state = State.STRAIGHT_FLUSH;
                    break;
                case STRAIGHT_FLUSH: // Points [800-899]
                    for(ArrayList<String> seq : sequences) {
                        String[] card = seq.get(0).split("-");
                        if(card[1].equals(sameSuit))
                            return Card.cardValue.get(card[0]) + 800;
                    }
                    state = State.FOUR_OF_A_KIND;
                    break;
                case FOUR_OF_A_KIND: // Points [700-799]
                    ArrayList<String> foak = Card.groupOfX(handInfo, 4);
                    if(foak.size() > 0)
                        return Card.cardValue.get(foak.get(0)) + 700;
                    else
                        state = State.FULL_HOUSE;
                    break;
                case FULL_HOUSE: // Points [600-699]
                    ArrayList<String> toak = Card.groupOfX(handInfo, 3);
                    ArrayList<String> pair = Card.groupOfX(handInfo, 2);

                    if(toak.size() > 0 && pair.size() > 0)
                        return Card.cardValue.get(toak.get(0))*2 + Card.cardValue.get(pair.get(0)) + 600;
                    else
                        state = State.FLUSH;
                    break;
                case FLUSH: // Points [500-599]
                    if(!sameSuit.equals("")) {
                        int handValue = 500;
                        int cardCounter = 1;

                        for(Card card : hand)  {
                            if(card.getSuit().equals(sameSuit)) {
                                handValue += Card.cardValue.get(card.getRank());
                                cardCounter++;
                            }
                            if(cardCounter == 5)
                                return handValue;
                        }
                    }
                    else
                        state = State.STRAIGHT;
                    break;
                case STRAIGHT: // Points [400-499]
                    if(sequences.size() > 0)
                        return Card.cardValue.get(sequences.get(0).get(0).split("-")[0]) + 400;
                    else
                        state = State.THREE_OF_A_KIND;
                    break;
                case THREE_OF_A_KIND: // Points [300-399]
                    toak = Card.groupOfX(handInfo, 3);
                    if(toak.size() > 0)
                        return Card.cardValue.get(toak.get(0)) + 300;
                    else
                        state = State.TWO_PAIR;
                    break;
                case TWO_PAIR: // Points [200-299]
                    ArrayList<String> twoPair = Card.groupOfX(handInfo, 2);
                    if(twoPair.size() > 1)
                        return Card.cardValue.get(twoPair.get(0)) + Card.cardValue.get(twoPair.get(1)) + 200;
                    else
                        state = State.ONE_PAIR;
                    break;
                case ONE_PAIR:  // Points [100-199]
                    pair = Card.groupOfX(handInfo, 2);
                    if(pair.size() > 0)
                        return Card.cardValue.get(pair.get(0)) + 100;
                    else
                        state = State.HIGH_CARD;
                    break;
                case HIGH_CARD: // Points [1-99]
                    return Card.cardValue.get(hand.get(0).getRank());
            }
        }
    }

    @Override
    public String toString() {
        return this.rank + "-" + this.suit;
    }
}
