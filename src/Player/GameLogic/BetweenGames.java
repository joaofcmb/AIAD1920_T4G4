package Player.GameLogic;

import Player.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

public class BetweenGames extends Behaviour {

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
    enum State {LEAVING_SESSION, NEW_GAME}

    /**
     * Current behaviour state
     */
    private State state = State.LEAVING_SESSION;

    /**
     * Logic behaviour
     */
    private Logic logic;

    BetweenGames(Player player, Logic logic) {
        this.player = player;
        this.logic = logic;
    }

    @Override
    public void action() {
        switch (state) {
            case LEAVING_SESSION:
                MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("between-games"),
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                ACLMessage msg = myAgent.receive(msgTemplate);

                if(msg != null) {
                    // Create reply
                    ACLMessage reply = msg.createReply();

                    reply.setPerformative(ACLMessage.INFORM);
                    if(this.player.getAID().getName().equals("Player03@JADE"))
                        reply.setContent("Yes"); // YES or NO
                    else
                        reply.setContent("No"); // YES or NO

                    System.out.println(this.player.getName() + " :: Intention to leave session :: " + reply.getContent());
                    myAgent.send(reply);

                    this.state = State.NEW_GAME;
                }
                else
                    block();
                break;
            case NEW_GAME:
                msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("new-game"),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                msg = myAgent.receive(msgTemplate);

                if(msg != null) {
                    if(msg.getContent().equals("No"))
                        myAgent.doDelete();
                    else
                        this.terminate();
                }
                else
                    block();
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
        // TODO - Reset all need variables
        this.player.getCards().clear();
        this.logic.nextState();
        return super.onEnd();
    }
}