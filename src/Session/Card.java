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
    private static HashMap<String, Integer> cardValue = new HashMap<>() {{
        put("2", 1); put("3", 2); put("4", 3); put("5", 4); put("6", 5);
        put("7", 6); put("8", 7); put("9", 8); put("10", 9); put("Jack", 10);
        put("Queen", 11); put("King", 12); put("Ace", 13);
    }};

    /**
     * Generates Lists of Cards
     * @param n number of cards
     * @return Lists of Cards
     */
    public static ArrayList<ArrayList<Card>> cardsGenerator(int n) {
        return cardsGenerator(n, new Deck().getDeck());
    }

    /**
     * Generates Lists of Cards
     * @param n number of cards
     * @param cardCombinations deck of cards
     * @return Lists of Cards
     */
    private static ArrayList<ArrayList<Card>> cardsGenerator(int n, ArrayList<Card> cardCombinations) {
        final ArrayList<ArrayList<Card>> listOfCards = new ArrayList<>();

        if (n == 1) {
            for (Card card : cardCombinations) {
                final ArrayList<Card> cards = new ArrayList<>();
                cards.add(card);
                listOfCards.add(new ArrayList<>(cards));
            }
        }
        else {
            while (!cardCombinations.isEmpty()) {
                Card card = cardCombinations.remove(0);

                ArrayList<ArrayList<Card>> genCards = cardsGenerator(n - 1, new ArrayList<>(cardCombinations));
                genCards.forEach((cards -> cards.add(0, card)));
                listOfCards.addAll(genCards);
            }
        }

        return listOfCards;
    }

    public static ArrayList<ArrayList<Card>> possibleTables(ArrayList<Card> table,
                                                            ArrayList<Card> playerCards,
                                                            ArrayList<Card> oppCards) {
        if (table.size() < 3) {
            return new ArrayList<>();
        } else if (table.size() == 5)  {
            final ArrayList<ArrayList<Card>> ret = new ArrayList<>();
            ret.add(table);
            return ret;
        }

        final ArrayList<Card> outCards = new ArrayList<>(table);
        outCards.addAll(playerCards);
        outCards.addAll(oppCards);

        final ArrayList<Card> cardCombinations = new Deck().getDeck();
        cardCombinations.removeAll(outCards);

        final ArrayList<ArrayList<Card>> genCards = cardsGenerator(5 - table.size(), cardCombinations);
        genCards.forEach((cards) -> cards.addAll(table));

        return genCards;
    }

    public static double rankCards(ArrayList<Card> playerCards) {
        int rankValue = Card.cardValue.get(playerCards.get(0).getRank()) +
                Card.cardValue.get(playerCards.get(1).getRank());

        System.out.println("Grande rank crl: " + rankValue);

        if (playerCards.get(0).getSuit().equals(playerCards.get(1).getSuit()) ||
                playerCards.get(0).getRank().equals(playerCards.get(1).getRank()))
            return rankValue / 52d + .5;
        else
            return rankValue / 26d;
    }


    /**
     * Poker hand ratings
     */
    public enum State {ROYAL_FLUSH, STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH, STRAIGHT,
        THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD;
    }

    /**
     * Card constructor
     * @param suit Card suit
     * @param rank Card rank
     */
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (!suit.equals(card.suit)) return false;
        return rank.equals(card.rank);
    }

    @Override
    public int hashCode() {
        int result = suit.hashCode();
        result = 31 * result + rank.hashCode();
        return result;
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

    public static void sort(ArrayList<Card> hand) {
        sort(new LinkedList<>(hand));
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
     * Returns flush sequence if exists. This function is separated from the getBestSequence
     * due to the fact that, that function computes the best sequence based on card ranks only
     * and tries to match the suit so solve the royal flush case. This function only checks for
     * flush sequences
     * @param hand player hand
     * @param handInfo player hand parsed information
     * @param suit flush suit
     * @return highest card value, or -1 in case no sequence is found
     */
    public static Integer getFlushSequence(LinkedList<Card> hand, HashMap<String, Integer> handInfo, String suit) {
        // Variables
        String initSeqCard = "";
        int noSeqCards = 0;
        String[] ranks = {"Ace", "King", "Queen", "Jack", "10", "9", "8", "7", "6", "5", "4", "3", "2"};

        // Determine sequence
        for (String rank : ranks) {
            if (handInfo.get(rank) == 0) {
                initSeqCard = "";
                noSeqCards = 0;
            }
            else {
                for(Card card : hand)
                    if(rank.equals(card.getRank()) && card.getSuit().equals(suit)) {
                        initSeqCard = initSeqCard.equals("") ? rank : initSeqCard;
                        noSeqCards++;
                        break;
                    }

                if (noSeqCards == 5)
                    break;
            }
        }

        // Allow Ace being the lowest sequence card
        if(initSeqCard.equals("5") && handInfo.get("Ace") > 0)
            noSeqCards++;

        // Enough cards to compose a sequence
        if(noSeqCards < 5)
            return -1;

        return Card.cardValue.get(initSeqCard);
    }

    /**
     * Checks if there exists five cards in sequence
     * @param hand player hand
     * @param handInfo player hand parsed information
     * @return Array with the best sequence, otherwise empty
     */
    public static ArrayList<String> getBestSequence(LinkedList<Card> hand, HashMap<String, Integer> handInfo) {
        // Variables
        String seq = "";
        int noSeqCards = 0;
        String[] ranks = {"Ace", "King", "Queen", "Jack", "10", "9", "8", "7", "6", "5", "4", "3", "2"};

        // Determine sequence
        for (String rank : ranks) {
            if (handInfo.get(rank) == 0) {
                seq = "";
                noSeqCards = 0;
            }
            else {
                seq += rank + "-";
                noSeqCards++;

                if (noSeqCards == 5)
                    break;
            }
        }

        // Remove last character
        seq = seq.equals("") ? "" : seq.substring(0, seq.length() - 1);

        // Allow Ace being the lowest sequence card
        if(seq.equals("5-4-3-2") && handInfo.get("Ace") > 0) {
            seq += "-Ace";
            noSeqCards++;
        }

        // Enough cards to compose a sequence
        if(noSeqCards < 5)
            return new ArrayList<>();

        // Retrieve suit with most amount of cards. Royal/Straight flush exceptions
        String biggestSuit = "";
        String[] remainingSuits = {"Clubs", "Diamonds", "Hearts", "Spades"};

        for(String suit : remainingSuits)
            if(biggestSuit.equals("") || handInfo.get(biggestSuit) < handInfo.get(suit))
                biggestSuit = suit;

        // Compose best sequence
        int cardIndex = 0;
        ArrayList<String> bestSequence = new ArrayList<>(Arrays.asList(seq.split("-")));

        while (cardIndex < noSeqCards) {
            for(Card card : hand) {
                if(bestSequence.get(cardIndex).contains(card.getRank()))
                    if(bestSequence.get(cardIndex).equals(card.getRank()) || card.getSuit().equals(biggestSuit))
                        bestSequence.set(cardIndex, card.getRank() + "-" + card.getSuit());
            }
            cardIndex++;
        }

        return bestSequence;
    }

    /**
     * Ranks hand to a value from 1 to 900
     * @param hand player hand
     * @return hand ranking
     */
    public static int rankHand(LinkedList<Card> hand) {
        // Player cards
        int playerCardsValue = Card.cardValue.get(hand.get(hand.size() - 2).rank) +
                Card.cardValue.get(hand.get(hand.size() - 1).rank);

        // Sort hand
        Card.sort(hand);

        // Hand variables
        HashMap<String, Integer> handInfo = Card.parseHand(hand);
        String sameSuit = Card.getFlush(handInfo);
        ArrayList<String> bestSequence = Card.getBestSequence(hand, handInfo);

        // State machine variables
        State state = sameSuit.equals("") ? State.FOUR_OF_A_KIND : State.ROYAL_FLUSH;

        while(true) {
            switch (state) {
                case ROYAL_FLUSH: // Points [900]
                    if(bestSequence.isEmpty()) {
                        state = State.STRAIGHT_FLUSH;
                        break;
                    }

                    String[] biggestCard = bestSequence.get(0).split("-");
                    if(biggestCard[0].equals("Ace") && biggestCard[1].equals(sameSuit))
                        return 900;
                    else
                        state = State.STRAIGHT_FLUSH;
                    break;
                case STRAIGHT_FLUSH: // Points [800-899]
                    int flushSequence = getFlushSequence(hand, handInfo, sameSuit);
                    if(flushSequence != -1)
                        return flushSequence + 800;
                    else
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
                                return handValue + playerCardsValue;
                        }
                    }
                    else
                        state = State.STRAIGHT;
                    break;
                case STRAIGHT: // Points [400-499]
                    if(bestSequence.size() > 0)
                        return Card.cardValue.get(bestSequence.get(0).split("-")[0]) + playerCardsValue + 400;
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
                        return Card.cardValue.get(pair.get(0)) + playerCardsValue + 100;
                    else
                        state = State.HIGH_CARD;
                    break;
                case HIGH_CARD: // Points [1-99]
                    return playerCardsValue;
            }
        }
    }

    @Override
    public String toString() {
        return this.rank + "-" + this.suit;
    }
}
