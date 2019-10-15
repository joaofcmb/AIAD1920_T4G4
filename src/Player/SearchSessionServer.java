package Player;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class SearchSessionServer extends TickerBehaviour {

    /**
     * Player object
     */
    private Player player;

    /**
     * The list of known dealers agents
     */
    private AID[] dealerAgents;

    SearchSessionServer(Player player, long period) {
        super(player, period);
        this.player = player;
    }

    @Override
    protected void onTick() {
        if(this.player.getPlayerState() == Player.State.SEARCHING_SESSION) {
            System.out.println(this.player.getName() + " :: Searching for session.");

            // Update the list of seller agents
            DFAgentDescription DFD = new DFAgentDescription();

            ServiceDescription SD = new ServiceDescription();
            SD.setType("poker-session");

            DFD.addServices(SD);
            try {
                DFAgentDescription[] data = DFService.search(myAgent, DFD);

                if(data.length > 0) {
                    System.out.print(this.player.getName() + " :: Found the following dealer agents: ");
                    this.dealerAgents = new AID[data.length];
                    for (int i = 0; i < data.length; ++i) {
                        this.dealerAgents[i] = data[i].getName();
                        System.out.print(this.dealerAgents[i].getName() + " ");
                    }
                    System.out.println();
                }
            }
            catch (FIPAException fe) {
                fe.printStackTrace();
            }

            // Updates player state
            this.player.setPlayerState(Player.State.JOINING_SESSION);

            // Perform the session request
            myAgent.addBehaviour(new JoinSessionPerformer(this.player, this.dealerAgents));
        }
    }
}

