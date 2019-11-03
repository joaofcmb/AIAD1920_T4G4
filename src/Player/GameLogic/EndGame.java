package Player.GameLogic;

import Player.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

public class EndGame extends Behaviour {

    /**
     * PLayer agent
     */
    private Player player;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Behaviour possible states
     */
    enum State {SHOWING_HAND, EARNINGS_DISTRIBUTION}

    /**
     * Current behaviour state
     */
    private State state = State.SHOWING_HAND;

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     * End turn constructor
     * @param player agent
     */
    EndGame(Player player, Logic logic) {
        this.player = player;
        this.logic = logic;
    }

    @Override
    public void action() {
        switch (state) {
            case SHOWING_HAND:
                MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("show-up-cards"),
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

                    this.state = State.EARNINGS_DISTRIBUTION;
                }
                else {
                    block();
                }
                break;
            case EARNINGS_DISTRIBUTION:
                msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("earnings"),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                msg = myAgent.receive(msgTemplate);

                if(msg != null) {
                    System.out.println(this.player.getName() + " :: Receiving earnings :: " + msg.getContent());
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
        status = true;
    }

    @Override
    public boolean done() {
        return status;
    }

    @Override
    public int onEnd() {
        this.logic.nextState();
        return super.onEnd();
    }
}
