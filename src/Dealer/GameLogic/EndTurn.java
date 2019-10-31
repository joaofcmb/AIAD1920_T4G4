package Dealer.GameLogic;

import Dealer.Dealer;
import Dealer.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class EndTurn extends Behaviour {

    Dealer dealer;

    boolean status = false;

    MessageTemplate msgTemplate;

    enum State {INFORM_PLAYER_TO_SHOW_HAND, RECEIVING_PLAYER_HAND, DISTRIBUTING_EARNINGS}

    private State state = State.INFORM_PLAYER_TO_SHOW_HAND;

    int targetPlayer = 0;

    HashMap<Integer, Integer> playerEarnings = new HashMap<>();

    EndTurn(Dealer dealer) {
        this.dealer = dealer;

        for(int i = 0; i < this.dealer.getSession().getInGamePlayers().size(); i++)
            this.playerEarnings.put(i, 0);
    }

    @Override
    public void action() {
        switch (state) {
            case INFORM_PLAYER_TO_SHOW_HAND:
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                // Configure message
                msg.addReceiver(this.dealer.getSession().getInGamePlayers().get(targetPlayer).getPlayer());
                msg.setConversationId("show-up-cards");
                msg.setReplyWith("show-up-cards" + System.currentTimeMillis());

                // Send message
                System.out.println(this.dealer.getName() + " :: Retrieving " +
                        this.dealer.getSession().getInGamePlayers().get(targetPlayer).getPlayer().getName() + " cards");
                myAgent.send(msg);

                // Prepare the template to get the reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("show-up-cards"),
                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                this.state = State.RECEIVING_PLAYER_HAND;
                break;
            case RECEIVING_PLAYER_HAND:
                // Receive reply
                ACLMessage reply = myAgent.receive(this.msgTemplate);

                if(reply != null) {
                    // Variables
                    String[] content = reply.getContent().split(":");
                    String[] firstCard = content[0].split("-");
                    String[] secondCard = content[1].split("-");

                    LinkedList<Card> playerHand = this.dealer.getSession().getTable();

                    playerHand.add(new Card(firstCard[0], firstCard[1]));
                    playerHand.add(new Card(secondCard[0], secondCard[1]));

                    // Retrieve hand value
                    int handValue = Card.rankHand(playerHand);

                    this.dealer.getSession().getInGamePlayers().get(this.targetPlayer).setCurrHandFinalValue(handValue);
                    System.out.println(this.dealer.getName() + " :: " + reply.getSender().getName() +
                            " has shown up his cards :: " + reply.getContent() + " :: Hand value = " + handValue);

                    if(this.targetPlayer == this.dealer.getSession().getInGamePlayers().size() - 1)
                        this.state = State.DISTRIBUTING_EARNINGS;
                    else {
                        this.targetPlayer++;
                        this.state = State.INFORM_PLAYER_TO_SHOW_HAND;
                    }
                }
                else {
                    block();
                }
                break;
            case DISTRIBUTING_EARNINGS:
                this.computeEarnings();

                this.terminate();
                break;
        }

    }
    // METo e dou sort
    //
    // [0-> X , 1-> Y , 2 -> Z]
    // 200 200 200

    private void computeEarnings() {
        while (this.valueInPot()) {
            System.out.println("asdasdasdasd");
        }
        ArrayList<Integer> winners = new ArrayList<>();
        int maxHandValue = this.dealer.getSession().getInGamePlayers().get(0).getCurrHandFinalValue();

        for(int i = 1; i < this.dealer.getSession().getInGamePlayers().size(); i++) {
            if(this.dealer.getSession().getInGamePlayers().get(i).getCurrHandFinalValue() > maxHandValue)
                maxHandValue = this.dealer.getSession().getInGamePlayers().get(i).getCurrHandFinalValue();
        }

        for(int i = 0; i < this.dealer.getSession().getInGamePlayers().size(); i++) {
            if(this.dealer.getSession().getInGamePlayers().get(i).getCurrHandFinalValue() == maxHandValue)
                winners.add(i);
        }

        int winnerPot = this.dealer.getSession().getInGamePlayers().get(winners.get(0)).getPot();
        // 200 1000 1000
        //
        for(int id : winners)
            System.out.println(id);
        System.out.println(maxHandValue);


    }

    /**
     * Checks if there are earnings to distribute
     * @return True it there are, false otherwise
     */
    private boolean valueInPot() {
        for(Player player : this.dealer.getSession().getInGamePlayers())
            if(player.getPot() > 0)
                return true;

        return false;
    }

    private void terminate() {
        this.status = true;
    }

    @Override
    public boolean done() {
        return this.status;
    }
}
