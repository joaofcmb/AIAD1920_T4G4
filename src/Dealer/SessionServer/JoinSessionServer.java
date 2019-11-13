package Dealer.SessionServer;

import Dealer.Dealer;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class JoinSessionServer extends CyclicBehaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Default constructor
     * @param dealer Agent
     */
    public JoinSessionServer(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        if(this.dealer.getDealerState() == Dealer.State.SESSION_SETUP) {
            MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(msgTemplate);
            if (msg != null) {
                // Create reply
                ACLMessage reply = msg.createReply();

                // Update current players structure
                if (this.dealer.updateCurrPlayers(msg.getSender(), Integer.parseInt(msg.getContent()))) {
                    reply.setPerformative(ACLMessage.INFORM);

                    // Add player to Game window
                    this.dealer.getWindow().addPlayer(msg.getSender().getName(), Float.parseFloat(msg.getContent()));
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("join-not-possible");
                }

                // Send reply
                myAgent.send(reply);
                if(reply.getPerformative() == ACLMessage.INFORM)
                    System.out.println(this.dealer.getName() + " :: Joined successfully :: "
                            + msg.getSender().getName());
            }
            else
                block();

        }
    }
}
