package Player.GameLogic;

import Player.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TurnRiver extends Behaviour {

    /**
     * PLayer agent
     */
    private Player player;

    /**
     * Type of moment: turn or river
     */
    private String moment;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     * Class constructor
     * @param player agent
     * @param logic logic class
     * @param moment moment (turn or river)
     */
    TurnRiver(Player player, Logic logic, String moment) {
        this.player = player;
        this.logic = logic;
        this.moment = moment;
    }

    @Override
    public void action() {
        MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId(this.moment + "-table-cards"));
        ACLMessage msg = myAgent.receive(msgTemplate);

        if(msg != null) {
            System.out.println(this.player.getName() + " :: Received new card added to the table :: " +
                    msg.getContent());

            // Adds new card to table
            String[] card = msg.getContent().split("-");
            this.player.getTable().add(new Card(card[0], card[1]));

            // Terminates behaviour
            this.terminate();
        }
        else {
            block();
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
        this.logic.nextState("Next State");
        return super.onEnd();
    }
}
