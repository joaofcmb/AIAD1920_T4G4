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

    private MessageTemplate mt; // The template to receive replies
    private int step = 0;

    JoinSessionPerformer(Player player, AID[] dealerAgents) {
        this.player = player;
        this.dealerAgents = dealerAgents;
    }

    @Override
    public void action() {
        switch (step) {
            case 0:
                // Send the cfp to all sellers
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < this.dealerAgents.length; ++i) {
                    cfp.addReceiver(this.dealerAgents[i]);
                }
                cfp.setContent(myAgent.getName() + " is searching for a session");
                cfp.setConversationId("searching-session");
                cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                myAgent.send(cfp);
                // Prepare the template to get proposals
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                step = 1;
                break;
            case 1:
                break;
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
