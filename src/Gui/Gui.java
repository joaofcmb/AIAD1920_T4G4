package Gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

public class Gui {

    /**
     * Images folder path
     */
    public final static String IMAGE_FOLDER_LOCATION = ".." + File.separator + ".." + File.separator + "images" + File.separator;

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
    private JPanel pots;

    /**
     * Pot
     */
    private JLabel pot;

    /**
     * Side pots
     */
    private JLabel sidePot1;
    private JLabel sidePot2;
    private JLabel sidePot3;
    private JLabel sidePot4;
    private JLabel sidePot5;
    private JLabel sidePot6;
    private JLabel sidePot7;
    private JLabel[] potsList = {pot, sidePot1, sidePot2, sidePot3, sidePot4, sidePot5, sidePot6, sidePot7};

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

    public Gui() {
        float[] hsb = Color.RGBtoHSB(104,26,5, null);
        Color dark_red = Color.getHSBColor(hsb[0],hsb[1],hsb[2]);
        pots.setBorder(new LineBorder(dark_red, 5, true));
    }

    public static void main(String[] args) {
        JFrame gui = new JFrame("Gui");
        gui.setContentPane(new Gui().mainPanel);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setLocation(100,100);
        gui.pack();
        gui.setSize(750,600);
        gui.setVisible(true);
    }
}
