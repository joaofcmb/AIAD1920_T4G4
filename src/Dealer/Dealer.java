package Dealer;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;

public class Dealer extends Agent {

    private HashMap<String, Integer> tableSettings = new HashMap<>();

    public int number = 0;

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
            this.tableSettings.put("currPlayers", 0);

            // Register the poker session in the yellow pages
            DFAgentDescription DFD = new DFAgentDescription();
            DFD.setName(getAID());

            ServiceDescription SD = new ServiceDescription();
            SD.setType("poker-session");
            SD.setName("JADE-book-trading");

            DFD.addServices(SD);
            try {
                DFService.register(this, DFD);
                System.out.println("Agent " + getAID().getName() + " has started a new poker session: " +
                        "SMALL_BLIND (" + this.tableSettings.get("smallBlind") + "$), " +
                        "BIG_BLIND (" + this.tableSettings.get("bigBlind") + "$), " +
                        "LOWER_BUY_IN (" + this.tableSettings.get("lowerBuyIn") + "$), " +
                        "UPPER_BUY_IN (" + this.tableSettings.get("upperBuyIn") + "$) "
                );
            }
            catch (FIPAException fe) {
                fe.printStackTrace();
            }

            // Periodically checks for players in table
            addBehaviour(new SessionPlayersServer(this, 1000));

            // Manages players entering table
            addBehaviour(new OfferSessionServer());
        }
        else {
            System.out.println("Usage:: <name>:<package_name>.<class_name>(small_blind, big_blind, lower_buy_in, upper_buy_in");
            doDelete();
        }
    }

    // Agent clean-up operations
    protected void takeDown() {
        System.out.println("Dealer " + getAID().getName() + " terminating.");
    }

    public HashMap<String, Integer> getTableSettings() {
        return tableSettings;
    }
}
