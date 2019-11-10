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
            final String[] messageContent = msg.getContent().split("=");
            final String[] bettingOptions = messageContent[0].split(":");
            final int bigBlind = Integer.parseInt(messageContent[1]);

            // TODO - Personality to determine which action to perform. Default will be always be check/call
            // TODO - Update all needed variables after action got determined
            // TODO - Reply content must be exactly in this format TYPE-AMOUNT. The amount for: CALL, RAISE, BET

            // Create reply
            ACLMessage reply = msg.createReply();

            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(this.player.getPersonality().betAction(bettingOptions, this.player.getBuyIn(), bigBlind));

            this.player.println("Send betting option :: " + reply.getContent() + " -- " + bettingOptions[0]);
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
