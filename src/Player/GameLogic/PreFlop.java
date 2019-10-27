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
     * Pre-flop state machine
     */
    public enum State {CARD_RECEPTION, SMALL_BIG_BLIND, BETTING}

    private State state = State.CARD_RECEPTION;

    /**
     * Pre-flop constructor
     * @param player agent
     */
    PreFlop(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        switch (this.state) {
            case CARD_RECEPTION:
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

                    if(this.player.getCards().size() == 2) {
                        this.state = State.SMALL_BIG_BLIND;
                    }
                }
                else {
                    block();
                }
                break;
            case SMALL_BIG_BLIND:

                msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                msg = myAgent.receive(msgTemplate);

                if (msg != null) {
                    String[] content = msg.getContent().split(":");
                    String[] smallBlind = content[0].split("-");
                    String[] bigBlind = content[1].split("-");

                    if(smallBlind[0].equals(this.player.getName()))
                        this.player.updateCurrBet(Integer.parseInt(smallBlind[1]));
                    else if(bigBlind[0].equals(this.player.getName()))
                        this.player.updateCurrBet(Integer.parseInt(bigBlind[1]));

                    System.out.println(this.player.getName() + " :: Current bet " + this.player.getCurrBet());
                    this.state = State.BETTING;
                }
                else {
                    block();
                }

                break;
            case BETTING:
                break;
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
