package Player.GameLogic;

import Player.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Flop extends Behaviour {

    /**
     * PLayer agent
     */
    private Player player;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     * Flop constructor
     * @param player agent
     */
    Flop(Player player, Logic logic) {
        this.player = player;
        this.logic = logic;
    }

    @Override
    public void action() {
        MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("flop-table-cards"));
        ACLMessage msg = myAgent.receive(msgTemplate);

        if(msg != null) {
            System.out.println(this.player.getName() + " :: Received table initial configuration: " +
                    msg.getContent());

            String[] content = msg.getContent().split(":");

            for(int i = 0; i < 3; i++) {
                String[] card = content[i].split("-");
                this.player.getTable().add(new Card(card[0], card[1]));
            }

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
        this.logic.nextState();
        return super.onEnd();
    }
}
