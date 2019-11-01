package Dealer.GameLogic;

import Dealer.Dealer;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import Dealer.Player;

import java.io.IOException;

public class PreFlop extends Behaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Message template
     */
    private MessageTemplate msgTemplate;

    /**
     * Player index which the message should be sent
     */
    private int targetPlayer = 0;

    /**
     * Pre-flop state machine
     */
    public enum State {CARD_DELIVERY, CARD_RECEPTION_CONFIRMATION, SMALL_BIG_BLIND}

    /**
     * Current state
     */
    public State state = State.CARD_DELIVERY;

    /**
     * Pre-flop constructor
     * @param dealer agent
     */
    PreFlop(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        switch (state){
            case CARD_DELIVERY:
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                // Add all players as receivers
                msg.addReceiver(this.dealer.getSession().getCurrPlayers().get(this.targetPlayer %
                        this.dealer.getSession().getCurrPlayers().size()).getPlayer());

                // Configure message
                msg.setContent(this.dealer.getSession().getDeck().getCard().toString());
                msg.setConversationId("pre-flop");
                msg.setReplyWith("pre-flop" + System.currentTimeMillis());

                // Send message
                myAgent.send(msg);

                // Prepare the template to get the reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("pre-flop"),
                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                this.targetPlayer++;
                this.state = State.CARD_RECEPTION_CONFIRMATION;
                break;
            case CARD_RECEPTION_CONFIRMATION:
                // Receive replies
                msg = myAgent.receive(msgTemplate);

                if(msg != null) {
                    System.out.println(this.dealer.getName() + " :: " + msg.getSender().getName() +
                            " has sent card reception confirmation");
                    this.state = State.CARD_DELIVERY;
                    if(targetPlayer >= this.dealer.getSession().getCurrPlayers().size()*2)
                        this.state = State.SMALL_BIG_BLIND;
                }
                else {
                    block();
                }
                break;
            case SMALL_BIG_BLIND:
                // Small blind bet
                this.dealer.getSession().getSmallBlind().updatePot(this.dealer.getTableSettings().get("smallBlind"));
                this.dealer.getSession().getSmallBlind().updateChips(-this.dealer.getTableSettings().get("smallBlind"));
                this.dealer.getSession().addBet(this.dealer.getSession().getSmallBlind().getPlayer().getName(),
                        "Bet-" + this.dealer.getTableSettings().get("smallBlind"));

                // Big blind bet
                this.dealer.getSession().getBigBlind().updatePot(this.dealer.getTableSettings().get("bigBlind"));
                this.dealer.getSession().getBigBlind().updateChips(-this.dealer.getTableSettings().get("bigBlind"));
                this.dealer.getSession().addBet(this.dealer.getSession().getBigBlind().getPlayer().getName(),
                        "Bet-" + this.dealer.getTableSettings().get("bigBlind"));

                // Send small and big blind information
                msg = new ACLMessage(ACLMessage.INFORM);

                // Add all players as receivers
                for(Player player : this.dealer.getSession().getCurrPlayers())
                    msg.addReceiver(player.getPlayer());

                // Configure message
                msg.setContent(this.dealer.getSession().getSmallBlind().getPlayer().getName() + "-" +
                        this.dealer.getTableSettings().get("smallBlind") + ":" +
                        this.dealer.getSession().getBigBlind().getPlayer().getName() + "-" +
                        this.dealer.getTableSettings().get("bigBlind"));
                msg.setConversationId("pre-flop-blinds");
                msg.setReplyWith("pre-flop-blinds" + System.currentTimeMillis());

                // Send message
                myAgent.send(msg);
                System.out.println(this.dealer.getName() + " :: Sent information about blinds: " + msg.getContent());

                this.terminate();
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

//    @Override
//    public int onEnd() {
//        try {
//            System.in.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return super.onEnd();
//    }
}
