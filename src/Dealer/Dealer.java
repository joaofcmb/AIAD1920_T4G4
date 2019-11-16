package Dealer;

import Dealer.SessionServer.JoinSessionServer;
import Dealer.SessionServer.OfferSessionServer;
import Dealer.SessionServer.SessionPlayersServer;
import GUI.GUI;
import Session.Session;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;
import java.util.LinkedList;

import static java.lang.Thread.sleep;

public class Dealer extends Agent {

    /**
     * Game GUI
     */
    private GUI window;

    /**
     * Table settings
     */
    private HashMap<String, Integer> tableSettings = new HashMap<>();

    /**
     * Dealer state machine
     */
    public enum State {INIT, SESSION_SETUP, STARTING_SESSION, IN_SESSION}

    /**
     * Initial state
     */
    private State dealerState = State.INIT;

    /**
     * Current players
     */
    private LinkedList<Player> currPlayers = new LinkedList<>();

    /**
     * Current session
     */
    private Session session = null;

    /**
     * Agent initialization
     */
    protected void setup() {
        final Object[] tableSettings = getArguments();

        if (tableSettings != null && tableSettings.length == 4) {
            this.tableSettings.put("smallBlind", Integer.parseInt((String) tableSettings[0]));
            this.tableSettings.put("bigBlind", Integer.parseInt((String) tableSettings[1]));
            this.tableSettings.put("lowerBuyIn", Integer.parseInt((String) tableSettings[2]));
            this.tableSettings.put("upperBuyIn", Integer.parseInt((String) tableSettings[3]));

            this.tableSettings.put("minPlayers", 2);
            this.tableSettings.put("maxPlayers", 8);

            // Register the poker session in the yellow pages
            DFAgentDescription DFD = new DFAgentDescription();
            DFD.setName(getAID());

            ServiceDescription SD = new ServiceDescription();
            SD.setType("poker-session");
            SD.setName("JADE-poker");

            DFD.addServices(SD);
            try {
                DFService.register(this, DFD);
                window = new GUI(this.getName());
                window.updateDealerAction("Has started a new poker session :: " +
                        "SMALL_BLIND[" + this.tableSettings.get("smallBlind") + "] - " +
                        "BIG_BLIND[" + this.tableSettings.get("bigBlind") + "] - " +
                        "LOWER_BUY_IN[" + this.tableSettings.get("lowerBuyIn") + "] - " +
                        "UPPER_BUY_IN[" + this.tableSettings.get("upperBuyIn") + "]");

                System.out.println(this.getName() + " :: Has started a new poker session :: " +
                        "SMALL_BLIND[" + this.tableSettings.get("smallBlind") + "] - " +
                        "BIG_BLIND[" + this.tableSettings.get("bigBlind") + "] - " +
                        "LOWER_BUY_IN[" + this.tableSettings.get("lowerBuyIn") + "] - " +
                        "UPPER_BUY_IN[" + this.tableSettings.get("upperBuyIn") + "]"
                );
            }
            catch (FIPAException fe) {
                fe.printStackTrace();
            }

            // Update dealer state
            this.dealerState = State.SESSION_SETUP;

            // Periodically checks for players in current session
            addBehaviour(new SessionPlayersServer(this, 1000));

            // Manages players looking for sessions
            addBehaviour(new OfferSessionServer(this));

            // Manages players that want to join current session
            addBehaviour(new JoinSessionServer(this));
        }
        else {
            System.out.println("Usage:: <name>:<package_name>.<class_name>(small_blind, big_blind, " +
                    "lower_buy_in, upper_buy_in");
            doDelete();
        }
    }

    /**
     * Agent clean-up operations
     */
    protected void takeDown() {
        window.updateDealerAction("Terminating");
        System.out.println(this.getName() + " :: Terminating");
    }

    /**
     * Updates current players structure
     * @param player New player to be added
     * @return True if new player is added false otherwise
     */
    public boolean updateCurrPlayers(AID player, int buyIn) {
        if(this.currPlayers.size() < this.tableSettings.get("maxPlayers") &&
                !this.containsPlayer(player.getName())) {
            this.currPlayers.add(new Player(buyIn, player));
            return true;
        }
        return false;
    }

    /**
     * Pauses GUI for visual purposes
     */
    public void pauseGUI() {
        window.setWaitingAction(true);
        while (window.isWaitingAction()){
            try {
                sleep(10);
            } catch (InterruptedException e) {
                System.exit(0);
            }
        }
    }

    /**
     * Retrieve game GUI
     */
    public GUI getWindow() { return window; }

    /**
     * Retrieve table settings
     */
    public HashMap<String, Integer> getTableSettings() {
        return tableSettings;
    }

    /**
     * Retrieve current players structure
     */
    public LinkedList<Player> getCurrPlayers() {
        return currPlayers;
    }

    /**
     * Checks whether player already joined session.
     * @param playerName Player name
     * @return True if already joined, false otherwise.
     */
    private boolean containsPlayer(String playerName) {
        for (Player currPlayer : this.currPlayers) {
            if (currPlayer.getPlayer().getName().equals(playerName))
                return true;
        }
        return false;
    }

    /**
     * Retrieve dealer current state
     */
    public State getDealerState() {
        return dealerState;
    }

    /**
     * Set dealer's new state
     * @param dealerState New state
     */
    public void setDealerState(State dealerState) {
        this.dealerState = dealerState;
    }

    /**
     * Create new session
     */
    public void createNewSession() {
        this.session = new Session(this.currPlayers);
    }

    /**
     * Returns current session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Prints dealer information
     * @param msg msg to be printed
     */
    public void printInfo(String msg) {
        System.out.println(this.getName() + " :: " + msg);
    }
}
