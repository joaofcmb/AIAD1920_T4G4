package Dealer;

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
    JoinSessionServer(Dealer dealer) {
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
                    System.out.println(this.dealer.getName() + " :: " + msg.getSender().getName() +
                            " successfully joined current session.");
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("join-not-possible");
                }

                // Send reply
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
}
