package Player.GameLogic;

import Player.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

public class EndTurn extends Behaviour {

    /**
     * PLayer agent
     */
    private Player player;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    MessageTemplate msgTemplate;

    enum State {SHOWING_HAND, EARNINGS_DISTRIBUTION}

    private State state = State.SHOWING_HAND;

    EndTurn(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        switch (state) {
            case SHOWING_HAND:
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("show-up-cards"),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                ACLMessage msg = myAgent.receive(msgTemplate);

                if(msg != null) {
                    ArrayList<Card> cards = this.player.getCards();

                    // Create reply
                    ACLMessage reply = msg.createReply();

                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(cards.get(0).toString() + ":" + cards.get(1).toString());

                    System.out.println(this.player.getName() + " :: Showing up cards :: " + reply.getContent());
                    myAgent.send(reply);

                    //this.state = State.EARNINGS_DISTRIBUTION;
                }
                else {
                    block();
                }
                break;
            case EARNINGS_DISTRIBUTION:
                break;
        }
    }

    /**
     * Terminates behaviour
     */
    private void terminate() {
        status = true;
    }

    @Override
    public boolean done() {
        return status;
    }
}
