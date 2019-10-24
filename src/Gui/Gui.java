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
    public final static String POKER_TABLE_IMAGE = IMAGE_FOLDER_LOCATION + "poker_table.png";

    private JPanel panel1;
    private JButton init;
    private JPanel game;
    private JPanel players;
    private JLabel card1;
    private JLabel card2;
    private JLabel card3;
    private JLabel card4;
    private JLabel card5;
    private JLabel pot;
    private JPanel cards1;
    private JPanel player1;
    private JPanel chips1;
    private JLabel p1c1;
    private JLabel p1c2;
    private JPanel player2;
    private JPanel chips2;
    private JPanel cards2;
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
                gui.setSize(1200,700);
                gui.setVisible(true);
            }
        });
    }
}
