package Dealer;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class StartingSession extends Behaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Number of replies received
     */
    private int repliesCnt = 0;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Default constructor
     * @param dealer agent
     */
    StartingSession(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        // Create new message to inform about session start
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        System.out.println(this.dealer.getName() + " :: Informing players about session start.");

        // Add all players as receivers
        for(int i = 0; i < this.dealer.getCurrPlayers().size(); i++)
            msg.addReceiver(this.dealer.getCurrPlayers().get(i));

        // Configure message
        msg.setContent("starting-session");
        msg.setConversationId("session-start");
        msg.setReplyWith("session-start" + System.currentTimeMillis());

        // Send message
        myAgent.send(msg);

        // Prepare the template to get the reply
        MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("session-start"),
                MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

        // Receive replies
        while (repliesCnt < this.dealer.getCurrPlayers().size()) {
            msg = myAgent.receive(msgTemplate);
            if(msg != null) {
                System.out.println(this.dealer.getName() + " :: " + msg.getSender().getName() +
                        " has sent session start confirmation.");
                repliesCnt++;
            }
            else {
                block();
            }
        }

        this.dealer.setDealerState(Dealer.State.DEALING);
        this.terminate();
    }

    /**
     * Terminates behaviour
     */
    private void terminate() {
        this.status = true;
    }

    @Override
    public boolean done() {
        return status;
    }
}
