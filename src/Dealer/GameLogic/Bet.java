package Dealer.GameLogic;

import Dealer.Dealer;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Bet extends Behaviour {

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

    enum State {PLAYER_BET_TURN, RECEIVE_PLAYER_BET}

    private State state = State.PLAYER_BET_TURN;

    int playerTurn;

    /**
     *
     * @param dealer
     */
    Bet(Dealer dealer, int playerTurn) {
     this.dealer = dealer;
     this.playerTurn = playerTurn;
    }

    @Override
    public void action() {
        switch (this.state) {
            case PLAYER_BET_TURN:
                AID playerTurn = this.dealer.getSession().getInGamePlayers().get(this.playerTurn).getPlayer();

                String bettingOptions = "Fold:Raise " + this.dealer.getSession().getMaxBet() * 2 + "$ or more:";

                if(this.dealer.getSession().getMaxBet() == 0)
                    bettingOptions += "Check";
                else
                    bettingOptions += "Call for " + this.dealer.getSession().getMaxBet() + "$";

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                // Configure message
                msg.addReceiver(playerTurn);
                msg.setContent(bettingOptions);
                msg.setConversationId("betting-phase");
                msg.setReplyWith("betting-phase" + System.currentTimeMillis());

                // Send message
                myAgent.send(msg);

                // Prepare the template to get the reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("betting-phase"),
                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                System.out.println(this.dealer.getName() + " :: Send betting options to " + playerTurn.getName());
                this.state = State.RECEIVE_PLAYER_BET;
                break;
            case RECEIVE_PLAYER_BET:
                // Receive replies
                msg = myAgent.receive(msgTemplate);

                if(msg != null) {
                    System.out.println(this.dealer.getName() + " :: " + msg.getSender().getName() +
                            " has sent his betting option.");
                }
                else {
                    block();
                }
                break;
        }
    }

    @Override
    public boolean done() {
        for(int i = 0; i < this.dealer.getSession().getInGamePlayers().size(); i++) {
            String playerName = this.dealer.getSession().getInGamePlayers().get(i).getPlayer().getName();
            if(!this.dealer.getSession().getBets().containsKey(playerName))
                return false;
            else {
                int lastBet = Integer.parseInt(this.dealer.getSession().getBets().get(playerName).get(0).split("-")[1]);
                if(lastBet < this.dealer.getSession().getMaxBet())
                    return false;
            }
        }
        return true;
    }
}
