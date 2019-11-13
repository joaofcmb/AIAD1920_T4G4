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
    public enum State {CARD_RECEPTION, SMALL_BIG_BLIND}

    /**
     * Current player state
     */
    private State state = State.CARD_RECEPTION;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     * Pre-flop constructor
     * @param player agent
     */
    PreFlop(Player player, Logic logic) {
        this.player = player;
        this.logic = logic;
    }

    @Override
    public void action() {
        switch (this.state) {
            case CARD_RECEPTION:
                MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = myAgent.receive(msgTemplate);

                if (msg != null) {
                    System.out.println(this.player.getName() + " :: Received " + msg.getContent());
                    String[] content = msg.getContent().split("-");

                    this.player.getCards().add(new Card(content[0], content[1]));

                    // Create reply
                    ACLMessage reply = msg.createReply();

                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent("Card-reception-confirmation");

                    myAgent.send(reply);

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
                    System.out.println(this.player.getName() + " :: Current bet " + this.player.getCurrBet());

                    String[] content = msg.getContent().split(":");
                    String[] smallBlind = content[0].split("-");
                    String[] bigBlind = content[1].split("-");

                    if(smallBlind[0].equals(this.player.getName()))
                        this.player.updateCurrBet(Integer.parseInt(smallBlind[1]));
                    else if(bigBlind[0].equals(this.player.getName()))
                        this.player.updateCurrBet(Integer.parseInt(bigBlind[1]));
                    else {
                        this.player.addBet(smallBlind[0], smallBlind[1]);
                        this.player.addBet(bigBlind[0], bigBlind[1]);
                        this.player.setBigBlind(Integer.parseInt(bigBlind[1]));
                    }

                    this.terminate();
                }
                else {
                    block();
                }
                break;
        }
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

    @Override
    public int onEnd() {
        this.logic.nextState();
        return super.onEnd();
    }
}
