package Dealer;

import Session.Session;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;
import java.util.LinkedList;

public class Dealer extends Agent {

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
                System.out.println(this.getName() + " :: Has started a new poker session: " +
                        "SMALL_BLIND (" + this.tableSettings.get("smallBlind") + "$), " +
                        "BIG_BLIND (" + this.tableSettings.get("bigBlind") + "$), " +
                        "LOWER_BUY_IN (" + this.tableSettings.get("lowerBuyIn") + "$), " +
                        "UPPER_BUY_IN (" + this.tableSettings.get("upperBuyIn") + "$)."
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
        System.out.println(this.getName() + " :: Terminating.");
    }

    /**
     * Updates current players structure
     * @param player New player to be added
     * @return True if new player is added false otherwise
     */
    boolean updateCurrPlayers(AID player, int buyIn) {
        if(this.currPlayers.size() < this.tableSettings.get("maxPlayers") &&
                !this.containsPlayer(player.getName())) {
            this.currPlayers.add(new Player(buyIn, player));
            return true;
        }
        return false;
    }

    /**
     * Retrieve table settings
     */
    public HashMap<String, Integer> getTableSettings() {
        return tableSettings;
    }

    /**
     * Retrieve current players structure
     */
    LinkedList<Player> getCurrPlayers() {
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
    State getDealerState() {
        return dealerState;
    }

    /**
     * Set dealer's new state
     * @param dealerState New state
     */
    void setDealerState(State dealerState) {
        this.dealerState = dealerState;
    }

    /**
     * Create new session
     */
    void createNewSession() {
        this.session = new Session(this.currPlayers);
    }

    /**
     * Returns current session
     */
    public Session getSession() {
        return session;
    }
}
