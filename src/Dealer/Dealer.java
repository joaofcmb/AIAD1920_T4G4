package Dealer;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;

public class Dealer extends Agent {

    /**
     * Table settings
     */
    private HashMap<String, Integer> tableSettings = new HashMap<>();

    private HashMap<String, AID> currPlayers = new HashMap<>();

    // Agent initializations here
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
            SD.setName("JADE-book-trading");

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

            // Periodically checks for players in current session
            addBehaviour(new SessionPlayersServer(this, 1000));

            // Manages players looking for sessions
            addBehaviour(new OfferSessionServer(this));

            // Manages players that want to join current session
            addBehaviour(new JoinSessionServer(this));
        }
        else {
            System.out.println("Usage:: <name>:<package_name>.<class_name>(small_blind, big_blind, lower_buy_in, upper_buy_in");
            doDelete();
        }
    }

    // Agent clean-up operations
    protected void takeDown() {
        System.out.println(this.getName() + " :: Terminating.");
    }

    /**
     * Updates current players structure
     * @param player New player to be added
     * @return True if new player is added false otherwise
     */
    public boolean updateCurrPlayers(AID player) {
        if(this.currPlayers.size() < this.tableSettings.get("maxPlayers") && !this.currPlayers.containsKey(player.getName())) {
            this.currPlayers.put(player.getName(), player);
            return true;
        }
        return false;
    }

    HashMap<String, Integer> getTableSettings() {
        return tableSettings;
    }

    public HashMap<String, AID> getCurrPlayers() {
        return currPlayers;
    }
}
