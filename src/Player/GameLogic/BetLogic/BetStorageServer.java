package Player.GameLogic.BetLogic;

import Player.Player;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BetStorageServer extends Behaviour {

    /**
     * Player agent
     */
    private Player player;

    /**
     * Bet storage server
     * @param player agent
     */
    BetStorageServer(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("betting-storage"),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        ACLMessage msg = myAgent.receive(msgTemplate);

        if(msg != null) {
            // Receive other player bet
            String[] content = msg.getContent().split(":");
            System.out.println(this.player.getName() + " :: Received " + content[0] + " bet :: " + content[1]);

            // Store bet
            this.player.getPersonality().updateInfo(content[0], content[1]);
        }
        else
            block();
    }

    @Override
    public boolean done() {
        return false;
    }
}
