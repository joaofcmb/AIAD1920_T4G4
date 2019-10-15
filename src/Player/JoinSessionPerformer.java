package Player;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class JoinSessionPerformer extends Behaviour {

    /**
     * Player object
     */
    private Player player;

    /**
     * The list of known dealers agents
     */
    private AID[] dealerAgents;

    /**
     * The template to receive replies
     */
    private MessageTemplate msgTemplate;

    /**
     * Number of replies received
     */
    private int repliesCnt;

    /**
     * Dealer agent
     */
    private AID dealer;

    /**
     * Process step
     */
    private int step = 0;

    /**
     * Default constructor
     * @param player Agent
     * @param dealerAgents Possible sessions that can be joined
     */
    JoinSessionPerformer(Player player, AID[] dealerAgents) {
        this.player = player;
        this.repliesCnt = 0;
        this.dealerAgents = dealerAgents;
    }

    @Override
    public void action() {
        switch (step) {
            case 0: // Send the CFP to all dealers
                ACLMessage CFP = new ACLMessage(ACLMessage.CFP);
                for (AID dealerAgent : this.dealerAgents) {
                    CFP.addReceiver(dealerAgent);
                }

                // Configure message
                CFP.setContent(Integer.toString(this.player.getBuyIn()));
                CFP.setConversationId("searching-session");
                CFP.setReplyWith("CFP" + System.currentTimeMillis()); // Unique value

                // Send CFP
                myAgent.send(CFP);

                // Prepare the template to get proposals
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("searching-session"),
                        MessageTemplate.MatchInReplyTo(CFP.getReplyWith()));
                step++;
                break;
            case 1: // Receive all proposals/refusals from dealer agents
                ACLMessage reply = myAgent.receive(this.msgTemplate);
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.PROPOSE) {
                        this.dealer = reply.getSender();
                    }

                    // Increment number of replies received
                    repliesCnt++;

                    // Check if there are some replies to be received
                    if (repliesCnt >= this.dealerAgents.length) {
                        step++;
                    }
                }
                else {
                    block();
                }
                break;
            case 2:
                // Send the purchase order to the seller that provided the best offer
                ACLMessage session = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

                // Configure message
                session.addReceiver(this.dealer);
                session.setContent("joining-session");
                session.setConversationId("session-join");
                session.setReplyWith("session" + System.currentTimeMillis());

                // Send message
                myAgent.send(session);

                // Prepare the template to get the purchase order reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("session-join"),
                        MessageTemplate.MatchInReplyTo(session.getReplyWith()));
                step++;
                break;
            case 3:
                // TODO 
                break;
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
