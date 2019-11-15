package Player.GameLogic.BetLogic;

import Player.Player;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BetHandler extends Behaviour {
    /**
     * Player agent
     */
    private Player player;

    /**
     * Bet handler constructor
     * @param player agent
     */
    BetHandler(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(
                MessageTemplate.and(MessageTemplate.MatchConversationId("betting-phase"),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM))
        );

        if (msg != null) {
            final String[] bettingOptions = msg.getContent().split(":");

            // Create reply
            ACLMessage reply = msg.createReply();

            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(this.player.getPersonality().betAction(bettingOptions)
            );

            this.player.updateChips(reply.getContent());

            if(reply.getContent().equals("Fold"))
                this.player.setFoldStatus();

            this.player.printInfo("Send betting option :: " + reply.getContent());
            myAgent.send(reply);
        }
        else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
