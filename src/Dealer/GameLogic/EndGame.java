package Dealer.GameLogic;

import Dealer.Dealer;
import Dealer.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class EndGame extends Behaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Behaviour status
     */
    private boolean status = false;

    /**
     * Message template
     */
    private MessageTemplate msgTemplate;

    /**
     * Behaviour possible states
     */
    enum State {INFORM_PLAYER_TO_SHOW_HAND, RECEIVING_PLAYER_HAND, DISTRIBUTING_EARNINGS}

    /**
     * Current behaviour state
     */
    private State state = State.INFORM_PLAYER_TO_SHOW_HAND;

    /**
     * Player which dealer is referencing to
     */
    private int targetPlayer;

    /**
     * Each player earnings
     */
    private HashMap<Integer, Integer> playerEarnings = new HashMap<>();

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     * End turn constructor
     * @param dealer agent
     */
    EndGame(Dealer dealer, Logic logic) {
        this.dealer = dealer;
        this.logic = logic;
        this.targetPlayer = 0;

        // First player who has not folded
        while(this.dealer.getSession().getCurrPlayers().get(targetPlayer).isFoldStatus()) {
            this.targetPlayer++;
        }

        // Initialize player earnings with 0 values
        for(int i = 0; i < this.dealer.getSession().getCurrPlayers().size(); i++)
            this.playerEarnings.put(i, 0);
    }

    @Override
    public void action() {
        switch (state) {
            case INFORM_PLAYER_TO_SHOW_HAND:
                this.dealer.getWindow().updateDealerAction("Inform players to show hand");

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                // Configure message
                msg.addReceiver(this.dealer.getSession().getCurrPlayers().get(targetPlayer).getPlayer());
                msg.setConversationId("show-up-cards");
                msg.setReplyWith("show-up-cards" + System.currentTimeMillis());

                // Send message
                System.out.println(this.dealer.getName() + " :: Retrieving " +
                        this.dealer.getSession().getCurrPlayers().get(targetPlayer).getPlayer().getName() + " cards");
                myAgent.send(msg);

                // Prepare the template to get the reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("show-up-cards"),
                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                this.state = State.RECEIVING_PLAYER_HAND;
                break;
            case RECEIVING_PLAYER_HAND:
                this.dealer.getWindow().updateDealerAction("Receiving players hand");

                // Receive reply
                ACLMessage reply = myAgent.receive(this.msgTemplate);

                if(reply != null) {
                    // Variables
                    String[] content = reply.getContent().split(":");
                    String[] firstCard = content[0].split("-");
                    String[] secondCard = content[1].split("-");

                    LinkedList<Card> playerHand = (LinkedList) this.dealer.getSession().getTable().clone();

                    playerHand.add(new Card(firstCard[0], firstCard[1]));
                    playerHand.add(new Card(secondCard[0], secondCard[1]));

                    // Retrieve hand value
                    StringBuilder hand = new StringBuilder();
                    int handValue = Card.rankHand(playerHand);

                    for(Card card : playerHand)
                        hand.append(card.toString()).append(" ");

                    this.dealer.getSession().getCurrPlayers().get(this.targetPlayer).setCurrHandFinalValue(handValue);
                    System.out.println(this.dealer.getName() + " :: " + reply.getSender().getName() +
                            " has shown up his cards :: " + reply.getContent() + " :: Hand (" + hand + ") value = " +
                            handValue);

                    if(this.targetPlayer == this.dealer.getSession().getCurrPlayers().size() - 1) {
                        this.targetPlayer = 0;
                        this.computeEarnings();
                        this.state = State.DISTRIBUTING_EARNINGS;
                    }
                    else {
                        this.targetPlayer++;

                        while(this.dealer.getSession().getCurrPlayers().get(targetPlayer).isFoldStatus()) {
                            if(this.targetPlayer == this.dealer.getSession().getCurrPlayers().size() - 1) {
                                this.targetPlayer = 0;
                                this.computeEarnings();
                                this.state = State.DISTRIBUTING_EARNINGS;
                            }
                            else
                                this.targetPlayer++;
                        }

                        this.state = State.INFORM_PLAYER_TO_SHOW_HAND;
                    }
                }
                else {
                    block();
                }
                break;
            case DISTRIBUTING_EARNINGS:
                this.dealer.getWindow().updateDealerAction("Distributing Earnings");
                this.dealer.getWindow().resetAllPots();

                msg = new ACLMessage(ACLMessage.INFORM);

                // Configure message
                msg.addReceiver(this.dealer.getSession().getCurrPlayers().get(targetPlayer).getPlayer());
                msg.setContent(Integer.toString(this.playerEarnings.get(this.targetPlayer)));
                msg.setConversationId("earnings");
                msg.setReplyWith("earnings" + System.currentTimeMillis());

                // Send message
                System.out.println(this.dealer.getName() + " :: " +
                        this.dealer.getSession().getCurrPlayers().get(targetPlayer).getPlayer().getName()
                        + (this.playerEarnings.get(targetPlayer) > 0 ?
                        " has won " + this.playerEarnings.get(targetPlayer) : " has lost"));

                this.dealer.getWindow().updatePlayerAction(
                        this.dealer.getSession().getCurrPlayers().get(targetPlayer).getPlayer().getName(),
                        (this.playerEarnings.get(targetPlayer) > 0 ?
                                "Won " + this.playerEarnings.get(targetPlayer) : "Lost"));

                // Update GUI chips
                this.dealer.getWindow().managePlayerChips(
                        this.dealer.getSession().getCurrPlayers().get(targetPlayer).getPlayer().getName(),
                        this.playerEarnings.get(targetPlayer),
                        this.playerEarnings.get(targetPlayer) > 0);

                this.dealer.getSession().getCurrPlayers().get(targetPlayer).updateChips(
                        this.playerEarnings.get(targetPlayer));
                myAgent.send(msg);

                if(targetPlayer == this.playerEarnings.size() - 1)
                    this.terminate();
                else
                    targetPlayer++;

                break;
        }

    }

    /**
     * Computes earnings por each player in game
     */
    private void computeEarnings() {
        while (this.valueInPot()) {
            // Initial variables
            int maxHandValue = 0;
            ArrayList<Integer> winners = new ArrayList<>();

            for(int i = 0; i < this.dealer.getSession().getCurrPlayers().size(); i++) {
                if(this.dealer.getSession().getCurrPlayers().get(i).getCurrHandFinalValue() > maxHandValue &&
                   this.dealer.getSession().getCurrPlayers().get(i).getPot() > 0)
                    maxHandValue = this.dealer.getSession().getCurrPlayers().get(i).getCurrHandFinalValue();
            }

            for(int i = 0; i < this.dealer.getSession().getCurrPlayers().size(); i++) {
                if(this.dealer.getSession().getCurrPlayers().get(i).getCurrHandFinalValue() == maxHandValue &&
                   this.dealer.getSession().getCurrPlayers().get(i).getPot() > 0)
                    winners.add(i);
            }

            // Intermediate variables
            int winnerPot = 0;
            int earnings = 0;

            // Due to possible existence of side pots always select the smaller pot
            for(int i = 0; i < winners.size(); i++) {
                if(winnerPot == 0)
                    winnerPot = this.dealer.getSession().getCurrPlayers().get(i).getPot();
                else if(winnerPot > this.dealer.getSession().getCurrPlayers().get(i).getPot())
                    winnerPot = this.dealer.getSession().getCurrPlayers().get(i).getPot();
            }

            // Calculate earnings
            for(int i = 0; i < this.dealer.getSession().getCurrPlayers().size(); i++) {
                earnings += Math.min(this.dealer.getSession().getCurrPlayers().get(i).getPot(), winnerPot);
                this.dealer.getSession().getCurrPlayers().get(i).updatePot(-Math.min(this.dealer.getSession().getCurrPlayers().get(i).getPot(), winnerPot));
            }

            // Update earnings
            for(int i = 0; i < winners.size(); i++) {
                this.playerEarnings.put(winners.get(i), this.playerEarnings.get(i) + earnings/winners.size());
            }
        }

    }

    /**
     * Checks if there are earnings to distribute
     * @return True it there are, false otherwise
     */
    private boolean valueInPot() {
        for(Player player : this.dealer.getSession().getCurrPlayers())
            if(player.getPot() > 0)
                return true;

        return false;
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

    @Override
    public int onEnd() {
        this.dealer.pauseGUI();
        this.dealer.getWindow().removeAllCardsFromPlayers();
        this.dealer.getWindow().removeCardsFromTable();
        this.logic.nextState();
        return super.onEnd();
    }
}
