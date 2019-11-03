package Player.GameLogic.BetLogic;

import Player.GameLogic.Logic;
import Player.Player;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BetEndServer extends Behaviour {

    /**
     * Player agent
     */
    private Player player;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Betting end server constructor
     * @param player agent
     */
    BetEndServer(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("end-betting-phase"),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        ACLMessage msg = myAgent.receive(msgTemplate);

        if(msg != null) {
            System.out.println(this.player.getName() + " :: Terminating betting phase");
            this.terminate();
        }
        else
            block();
    }

    /**
     * Terminates behaviour
     */
    private void terminate() {
        this.status = true;
    }

    @Override
    public boolean done() {
        return status;
    }
}
