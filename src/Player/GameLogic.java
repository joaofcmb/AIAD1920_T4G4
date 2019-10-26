package Player;

import Session.Card;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

public class GameLogic extends Behaviour {

    /**
     * Player agent
     */
    private Player player;

    /**
     * Dealer agent
     */
    private AID dealer;

    /**
     * The template to receive replies
     */
    private MessageTemplate msgTemplate;

    /**
     * Game logic state machine
     */
    public enum State {PRE_FLOP, FLOP}

    /**
     * Current game state
     */
    private State gameState = State.PRE_FLOP;

    /**
     * Player cards
     */
    private ArrayList<Card> cards = new ArrayList<>();

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Game logic constructor
     * @param player agent
     */
    GameLogic(Player player) {
        this.player = player;
        this.dealer = player.getDealer();
    }

    @Override
    public void action() {
        if(this.player.getPlayerState() == Player.State.IN_SESSION) {
            switch (gameState) {
                case PRE_FLOP:
                    while (this.cards.size() < 2) {
                        this.msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                        ACLMessage msg = myAgent.receive(this.msgTemplate);
                        if (msg != null) {
                            String[] content = msg.getContent().split("-");

                            this.cards.add(new Card(content[1], content[0]));

                            // Create reply
                            ACLMessage reply = msg.createReply();

                            reply.setPerformative(ACLMessage.CONFIRM);
                            reply.setContent("Card-reception-confirmation");
                            myAgent.send(reply);
                            System.out.println(this.player.getName() + " :: Received " + msg.getContent() + ". Send card reception confirmation.");
                        } else {
                            block();
                        }
                    }

                    this.gameState = State.FLOP;
                    break;
                case FLOP:
                    break;
            }
        }
        else
            this.terminate();
    }

    /**
     * Terminates behaviour
     */
    private void terminate() {
        this.status = true;
    }

    @Override
    public boolean done() {
        return this.status;
    }
}
