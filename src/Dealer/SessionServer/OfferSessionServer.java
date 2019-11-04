package Dealer.SessionServer;

import Dealer.Dealer;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class OfferSessionServer extends CyclicBehaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Default constructor
     * @param dealer Agent
     */
    public OfferSessionServer(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        if(this.dealer.getDealerState() == Dealer.State.SESSION_SETUP) {
            MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(msgTemplate);
            if (msg != null) {
                // CFP Message received. Process it
                int playerBuyIn = Integer.parseInt(msg.getContent());

                // Create reply
                ACLMessage reply = msg.createReply();

                if(this.dealer.getTableSettings().get("lowerBuyIn") <= playerBuyIn &&
                        this.dealer.getTableSettings().get("upperBuyIn") >= playerBuyIn) {
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent("Session-available");
                    System.out.println(this.dealer.getName() + " :: Session available in response to " +
                            msg.getSender().getName());

                    this.dealer.getWindow().updateDealerAction("Session available in response to " +
                            msg.getSender().getName());
                }
                else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("Player buy in do not fit the session buy in range [" +
                            this.dealer.getTableSettings().get("lowerBuyIn") + " : " +
                            this.dealer.getTableSettings().get("upperBuyIn") + "]");
                    System.out.println(this.dealer.getName() + " :: Player buy in do not fit the session buy " +
                            "in range [" + this.dealer.getTableSettings().get("lowerBuyIn") + " : " +
                            this.dealer.getTableSettings().get("upperBuyIn") + "]");

                    this.dealer.getWindow().updateDealerAction("Player buy in do not fit the session buy " +
                            "in range [" + this.dealer.getTableSettings().get("lowerBuyIn") + " : " +
                            this.dealer.getTableSettings().get("upperBuyIn") + "]");
                }
                myAgent.send(reply);
            }
            else
                block();
        }
    }
}
