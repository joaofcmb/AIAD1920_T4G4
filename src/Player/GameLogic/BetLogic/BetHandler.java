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
     * Message template
     */
    private MessageTemplate msgTemplate;

    /**
     * Bet handler constructor
     * @param player agent
     */
    BetHandler(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("betting-phase"),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        ACLMessage msg = myAgent.receive(msgTemplate);

        if (msg != null) {
            String[] bettingOptions = msg.getContent().split(":");

            // TODO - Personality to determine which action to perform. Default will be always be check/call
            // TODO - Update all needed variables after action got determined
            // TODO - Reply content must be exactly in this format TYPE-AMOUNT. The amount for: CALL, RAISE, BET

            // Create reply
            ACLMessage reply = msg.createReply();

            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(bettingOptions[0]);

            System.out.println(this.player.getName() + " :: Send betting option :: " + reply.getContent());
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
