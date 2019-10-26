package Player.GameLogic;

import Player.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PreFlop extends Behaviour {

    /**
     * Player agent
     */
    private Player player;

    /**
     * Pre-flop constructor
     * @param player agent
     */
    PreFlop(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(msgTemplate);

        if (msg != null) {
            String[] content = msg.getContent().split("-");

            this.player.getCards().add(new Card(content[1], content[0]));

            // Create reply
            ACLMessage reply = msg.createReply();

            reply.setPerformative(ACLMessage.CONFIRM);
            reply.setContent("Card-reception-confirmation");

            myAgent.send(reply);

            System.out.println(this.player.getName() + " :: Received " + msg.getContent() +
                    ". Send card reception confirmation.");
        }
        else {
            block();
        }
    }

    @Override
    public boolean done() {
        return this.player.getCards().size() == 2;
    }
}
