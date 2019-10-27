package GUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class GUI {

    /**
     * Images folder path
     */
    public final static String IMAGE_FOLDER_LOCATION = "resources" + File.separator + "images" + File.separator;

    /**
     * Window of the game
     */
    private JFrame mainFrame;

    /**
     * Main panel, include all the window
     */
    private JPanel mainPanel;

    /**
     * Panel representing the game table
     */
    private JPanel game;

    /**
     * Cards in the table
     */
    private JLabel card1;
    private JLabel card2;
    private JLabel card3;
    private JLabel card4;
    private JLabel card5;
    private JLabel[] cards = {card1, card2, card3, card4, card5};

    /**
     * Panel include all the pots and side pots
     */
    public JPanel pots;

    /**
     * Side pots
     */
    private JLabel pot1;
    private JLabel pot2;
    private JLabel pot3;
    private JLabel pot4;
    private JLabel pot5;
    private JLabel pot6;
    private JLabel pot7;
    private JLabel pot8;
    private JLabel[] potsList = {pot1, pot2, pot3, pot4, pot5, pot6, pot7, pot8};

    /**
     * Panel include all the players
     */
    private JPanel players;

    /**
     * Name of the player 1
     */
    private JLabel p1name;

    /**
     * Chips of the player 1
     */
    private JLabel p1chips;

    /**
     * First card of the player 1
     */
    private JLabel p1c1;

    /**
     * Second card of the player 1
     */
    private JLabel p1c2;

    /**
     * Last action of the player 1
     */
    private JLabel action1;

    /**
     * Components of the player 2
     * More details on player 1
     */
    private JLabel p2name;
    private JLabel p2chips;
    private JLabel action2;
    private JLabel p2c1;
    private JLabel p2c2;

    /**
     * Components of the player 3
     * More details on player 1
     */
    private JLabel p3name;
    private JLabel p3chips;
    private JLabel p3c1;
    private JLabel p3c2;
    private JLabel action3;

    /**
     * Components of the player 4
     * More details on player 1
     */
    private JLabel p4name;
    private JLabel p4chips;
    private JLabel p4c1;
    private JLabel p4c2;
    private JLabel action4;

    /**
     * Components of the player 5
     * More details on player 1
     */
    private JLabel p5name;
    private JLabel p5chips;
    private JLabel p5c1;
    private JLabel p5c2;
    private JLabel action5;

    /**
     * Components of the player 6
     * More details on player 1
     */
    private JLabel p6name;
    private JLabel p6chips;
    private JLabel p6c1;
    private JLabel p6c2;
    private JLabel action6;

    /**
     * Components of the player 7
     * More details on player 1
     */
    private JLabel p7name;
    private JLabel p7chips;
    private JLabel p7c1;
    private JLabel p7c2;
    private JLabel action7;

    /**
     * Components of the player 8
     * More details on player 1
     */
    private JLabel p8name;
    private JLabel p8chips;
    private JLabel p8c1;
    private JLabel p8c2;
    private JLabel action8;

    /**
     * Action of the dealer
     */
    private JLabel dealerAction;

    /**
     * Number of player in the table
     */
    private int playerCounter;

    /**
     * List with all the attributes of each player
     */
    private JLabel[][] playersList = {{p1name, p1chips, p1c1, p1c2, action1}, {p2name, p2chips, p2c1, p2c2, action2},
            {p3name, p3chips, p3c1, p3c2, action3}, {p4name, p4chips, p4c1, p4c2, action4},
            {p5name, p5chips, p5c1, p5c2, action5}, {p6name, p6chips, p6c1, p6c2, action6},
            {p7name, p7chips, p7c1, p7c2, action7}, {p8name, p8chips, p8c1, p8c2, action8}};

    /**
     * Index of the player with small blind
     */
    private int smallBlind;

    /**
     * Index of the player with big blind
     */
    private int bigBlind;

    /**
     * Map associating cards name to image path
     * Card name: rank-suits
     */
    private HashMap<String,String> cardMap;

    private int cardCounter = 0;

    /**
     * Create and display a GUI for a poker game
     * @param frameTitle title of the window
     */
    public GUI(String frameTitle) {
        // Initialize all cards as empty
        for (JLabel card : cards) {
            card.setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
        }
        for (JLabel[] player : playersList) {
            player[2].setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
            player[3].setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
        }

        // Create Red border in pots panel
        float[] hsb = Color.RGBtoHSB(104,26,5, null);
        Color dark_red = Color.getHSBColor(hsb[0],hsb[1],hsb[2]);
        pots.setBorder(new LineBorder(dark_red, 2, true));

        // Initialization of map with cardName -> imagePath
        cardMap = new HashMap<>();
        String[] suits = {"Clubs", "Spades", "Hearts", "Diamonds"};
        String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};

        for(int i = 0; i < 52; i++) {
            int rankIndex = i % 13;
            int suitsIndex = i / 13;

            cardMap.put(ranks[rankIndex] + "-" + suits[suitsIndex], IMAGE_FOLDER_LOCATION + (i+1) + ".png");
        }

        // Create GUI frame
        mainFrame = new JFrame(frameTitle);
        mainFrame.setContentPane(mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocation(100,100);
        mainFrame.pack();
        mainFrame.setSize(750,600);
        mainFrame.setVisible(true);
    }

    /**
     * Add a player to the table
     * @param name name of the player
     * @param buyIn initial buy-in of the player
     * @return true if the player is added or false if the table is full
     */
    public boolean addPlayer(String name, float buyIn) {
        if (playerCounter == 8) return false;

        playersList[playerCounter][0].setText(name);

        String chips = "";

        if (buyIn > 1000000) chips += buyIn/1000000.0 + "M";
        else if (buyIn > 1000) chips += buyIn/1000.0 + "K";
        else chips += buyIn;

        playersList[playerCounter][1].setText(chips + " €");
        playerCounter++;
        return true;
    }

    public void removePlayer(int playerIndex) {
        playerCounter--;
        playersList[playerIndex][0].setText("Player" + playerIndex);
        playersList[playerIndex][0].setForeground(Color.white);
        playersList[playerIndex][1].setText("");
        playersList[playerIndex][2].setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
        playersList[playerIndex][3].setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
        playersList[playerIndex][4].setText("");
    }

    /**
     * Add a blind to player
     * @param playerIndex index of the player in the table
     * @param blind blind to add
     */
    public void addPlayerBlind(int playerIndex, String blind) {
        playersList[playerIndex][0].setText(playersList[playerIndex][0].getText() + " (" + blind + ")");

        if (blind == "B") {
            playersList[playerIndex][0].setForeground(Color.red);
            bigBlind = playerIndex;
        } else if (blind == "S") {
            playersList[playerIndex][0].setForeground(Color.blue);
            smallBlind = playerIndex;
        }
    }

    /**
     * Allows know if is a blind in the table
     * @param blind blind to know
     * @return true if the blind is associated with some player, false otherwise
     */
    public boolean hasBlind(String blind) {
        if (blind == "B") {
            return bigBlind != -1;
        } else if (blind == "S") {
            return smallBlind != -1;
        }
        return false;
    }

    /**
     * Move the blind to the next player in the table
     */
    public void rotatePlayerBlinds() {
        int newBigBlind = bigBlind + 1;
        int newSmallBlind = smallBlind + 1;

        removePlayerBlind("B");
        removePlayerBlind("S");
        addPlayerBlind(newBigBlind % playerCounter, "B");
        addPlayerBlind(newSmallBlind % playerCounter, "S");
    }

    /**
     * Remove a blind from player
     * @param blind blind to remove
     */
    public void removePlayerBlind(String blind) {
        String playerName;
        if (blind == "B") {
            playerName = playersList[bigBlind][0].getText();
            playerName = playerName.substring(0, playerName.length()-4);
            playersList[bigBlind][0].setText(playerName);
            playersList[bigBlind][0].setForeground(Color.white);
            bigBlind = -1;
        } else if (blind == "S") {
            playerName = playersList[smallBlind][0].getText();
            playerName = playerName.substring(0, playerName.length()-4);
            playersList[smallBlind][0].setText(playerName);
            playersList[smallBlind][0].setForeground(Color.white);
            smallBlind = -1;
        }
    }

    /**
     * Add cards to the table
     * @param cardsName list of names of cards to add
     */
    public void addCardsToTable(String[] cardsName) {
        for (String card :
                cardsName) {
            cards[cardCounter].setIcon(new ImageIcon(cardMap.get(card)));
            cardCounter++;
        }
    }

    /**
     * Remove the cards from the table
     */
    public void removeCardsFromTable() {
        for (JLabel card :
                cards) {
            card.setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
        }
    }

    /**
     * Add card to the players
     * @param cardsName list of names of cards to add
     * @return true if the cards are added to the player, false if don't have sufficient card for all the players
     */
    public boolean addCardsToPlayers(String[] cardsName) {
        if (cardsName.length < playerCounter)
            return false;

        int cardNameIndex = 0;

        for (int i = 0 ; i < playerCounter ; i++) {
            playersList[i][2].setIcon(new ImageIcon(cardMap.get(cardsName[cardNameIndex++])));
            playersList[i][3].setIcon(new ImageIcon(cardMap.get(cardsName[cardNameIndex++])));
        }

        return true;
    }

    /**
     * Collects a player's cards
     * @param playerIndex index of the player
     * @return true if the cards are collected, false if the index is invalid
     */
    public boolean  removeCardFromPlayer(int playerIndex) {
        if (playerIndex >= playerCounter || playerIndex < 0)
            return false;

        playersList[playerIndex][2].setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
        playersList[playerIndex][3].setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));

        return true;
    }

    /**
     * Collect cards from all players
     */
    public void removeAllCardsFromPlayers() {
        for (int i = 0 ; i < playerCounter ; i++) {
            playersList[i][2].setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
            playersList[i][3].setIcon(new ImageIcon(IMAGE_FOLDER_LOCATION + "emptyCard.png"));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        GUI g = new GUI("GUI");

        g.addPlayer("Rio", 1563000);
        g.addPlayer("Paul", 5000);
        g.addPlayer("John", 150);
        g.addPlayer("Mary", 341000);
        g.addPlayer("Ellen", 17590);

        g.addPlayerBlind(0, "S");
        g.addPlayerBlind(2, "B");

        System.out.println(g.hasBlind("B"));
        System.out.println(g.hasBlind("S"));

        sleep(1000);
        g.rotatePlayerBlinds();
        sleep(1000);
        g.rotatePlayerBlinds();
        sleep(1000);
        g.addCardsToTable(new String[]{"Ace-Hearts", "8-Hearts", "Ace-Clubs"});
        sleep(1000);
        g.addCardsToTable(new String[]{"Ace-Spades"});
        sleep(1000);
        g.addCardsToTable(new String[]{"Ace-Diamonds"});
        sleep(1000);

        g.addCardsToPlayers(new String[]{"2-Hearts", "4-Hearts", "6-Clubs", "King-Hearts", "9-Hearts", "10-Clubs",
                "8-Spades", "4-Spades", "3-Clubs", "7-Diamonds" });
        sleep(1000);
        g.removeCardFromPlayer(2);
        sleep(1000);
        g.removeAllCardsFromPlayers();

        sleep(1000);
        g.removeCardsFromTable();
    }
}