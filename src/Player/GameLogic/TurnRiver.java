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

    TurnRiver(Player player, String moment) {
        this.player = player;
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
            this.player.getTable().add(new Card(card[1], card[0]));

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
}
