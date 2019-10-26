package Dealer;

import Session.Session;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import Session.Card;

public class GameLogic extends Behaviour {

    /**
     * Dealer agent
     */
    Dealer dealer;

    /**
     * Current session
     */
    Session session;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Game logic state machine
     */
    private enum State {PRE_FLOP}

    /**
     * Current game state
     */
    private State gameState = State.PRE_FLOP;

    /**
     * Game logic constructor
     * @param dealer agent
     */
    GameLogic(Dealer dealer) {
        this.dealer = dealer;
        this.session = this.dealer.getSession();
    }

    @Override
    public void action() {
        if(this.dealer.getDealerState() == Dealer.State.IN_SESSION) {
            switch (this.gameState) {
                case PRE_FLOP:
                    // Create new message to inform about session start
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                    for(int i = 0; i < 2; i++) {
                        for (Player player: this.session.getCurrPlayers()) {
                            // Retrieve top card
                            Card card = this.dealer.getSession().getDeck().getCard();

                            // Reset receivers
                            msg.clearAllReceiver();

                            // Add new receiver
                            msg.addReceiver(player.getPlayer());

                            // Configure message
                            msg.setContent(card.toString());
                            msg.setConversationId("pre-flop");
                            msg.setReplyWith("pre-flop" + System.currentTimeMillis());

                            // Send message
                            myAgent.send(msg);
                        }
                    }

                    // Prepare the template to get the reply
                    MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("pre-flop"),
                            MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                    // Receive replies
                    int repliesCnt = 0;

                    while (repliesCnt < this.session.getCurrPlayers().size()*2) {
                        msg = myAgent.receive(msgTemplate);
                        if(msg != null) {
                            System.out.println(this.dealer.getName() + " :: " + msg.getSender().getName() +
                                    " has received both cards.");
                            repliesCnt++;
                        }
                        else {
                            block();
                        }
                    }
                    this.terminate();
                    break;
            }
        }
        else
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
        return this.status;
    }
}
