package Gui;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

public class Gui {
    public final static String IMAGE_FOLDER_LOCATION = ".." + File.separator + ".." + File.separator + "images" + File.separator;

    private JPanel panel1;
    private JPanel game;
    private JLabel card1;
    private JLabel card2;
    private JLabel card3;
    private JLabel card4;
    private JLabel card5;
    private JLabel[] cards = {card1, card2, card3, card4, card5};
    private JLabel pot;
    private JLabel sidePot1;
    private JLabel sidePot2;
    private JLabel sidePot3;
    private JLabel sidePot4;
    private JLabel sidePot5;
    private JLabel sidePot6;
    private JLabel sidePot7;
    private JLabel[] potsList = {pot, sidePot1, sidePot2, sidePot3, sidePot4, sidePot5, sidePot6, sidePot7};

    private JPanel players;
    private JLabel p1name;
    private JLabel p1chips;
    private JLabel p1c1;
    private JLabel p1c2;
    private JLabel action1;

    private JLabel p2name;
    private JLabel p2chips;
    private JLabel action2;
    private JLabel p2c1;
    private JLabel p2c2;

    private JLabel p3name;
    private JLabel p3chips;
    private JLabel p3c1;
    private JLabel p3c2;
    private JLabel action3;

    private JLabel p4name;
    private JLabel p4chips;
    private JLabel p4c1;
    private JLabel p4c2;
    private JLabel action4;

    private JLabel p5name;
    private JLabel p5chips;
    private JLabel p5c1;
    private JLabel p5c2;
    private JLabel action5;

    private JLabel p6name;
    private JLabel p6chips;
    private JLabel p6c1;
    private JLabel p6c2;
    private JLabel action6;

    private JLabel p7name;
    private JLabel p7chips;
    private JLabel p7c1;
    private JLabel p7c2;
    private JLabel action7;

    private JLabel p8name;
    private JLabel p8chips;
    private JLabel p8c1;
    private JLabel p8c2;
    private JLabel action8;

    private static HashMap<Integer, String> cardMap;

    public Gui() {

        cardMap = new HashMap<Integer, String>();
        for (int i = 1; i <= 52; i++) {
            cardMap.put(i, IMAGE_FOLDER_LOCATION + i + ".png");
        }

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame gui = new JFrame("Gui");
                gui.setContentPane(new Gui().panel1);
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gui.setLocation(100,100);
                gui.pack();
                gui.setSize(750,600);
                gui.setVisible(true);
            }
        });
    }
}
