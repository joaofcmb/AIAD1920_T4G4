package Player.GameLogic;

import Player.Player;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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

    /**
     * Class constructor
     * @param player agent
     * @param logic logic
     */
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

                    System.out.println(this.player.getName() + " :: " + this.player.getBuyIn() + " -- " + this.player.getBigBlind());
                    if(this.player.getBuyIn() < this.player.getBigBlind())
                        reply.setContent("Yes");
                    else
                        reply.setContent("No");

                    System.out.println(this.player.getName() + " :: Intention to leave session :: " + reply.getContent());
                    myAgent.send(reply);

                    // Terminates agent if necessary or moves it to next state
                    if(this.player.getBuyIn() < this.player.getBigBlind())
                        this.player.doDelete();
                    else
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
        this.player.resetAll();
        this.logic.nextState("Next State");
        return super.onEnd();
    }
}
