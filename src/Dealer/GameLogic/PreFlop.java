package Dealer.GameLogic;

import Dealer.Dealer;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PreFlop extends Behaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Message template
     */
    private MessageTemplate msgTemplate;

    /**
     * Player index which the message should be sent
     */
    private int targetPlayer = 0;

    /**
     * Pre-flop state machine
     */
    public enum State {SENDING_MESSAGE, RECEIVING_MESSAGE}

    /**
     * Current state
     */
    public State state = State.SENDING_MESSAGE;

    /**
     * Pre-flop constructor
     * @param dealer agent
     */
    PreFlop(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        switch (state){
            case SENDING_MESSAGE:
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                // Add all players as receivers
                msg.addReceiver(this.dealer.getSession().getCurrPlayers().get(this.targetPlayer %
                        this.dealer.getSession().getCurrPlayers().size()).getPlayer());

                // Configure message
                msg.setContent(this.dealer.getSession().getDeck().getCard().toString());
                msg.setConversationId("pre-flop");
                msg.setReplyWith("pre-flop" + System.currentTimeMillis());

                // Send message
                myAgent.send(msg);

                // Prepare the template to get the reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("pre-flop"),
                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                this.targetPlayer++;
                this.state = State.RECEIVING_MESSAGE;
                break;
            case RECEIVING_MESSAGE:
                // Receive replies
                msg = myAgent.receive(msgTemplate);

                if(msg != null) {
                    System.out.println(this.dealer.getName() + " :: " + msg.getSender().getName() +
                            " has sent session start confirmation.");
                    this.state = State.SENDING_MESSAGE;
                    if(targetPlayer >= this.dealer.getSession().getCurrPlayers().size()*2)
                        this.terminate();
                }
                else {
                    block();
                }
                break;
        }
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
