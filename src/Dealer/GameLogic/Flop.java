package Dealer.GameLogic;

import Dealer.Dealer;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;

public class Flop extends Behaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Flop constructor
     * @param dealer agent
     */
    Flop(Dealer dealer) {
        this.dealer = dealer;
        this.dealer.getSession().getDeck().getCard();   // Removes card from deck [RULE]
    }

    @Override
    public void action() {
        // Adds three cards on table
        for(int i = 0; i < 3; i++)
            this.dealer.getSession().getTable().add(this.dealer.getSession().getDeck().getCard());

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        // Add all players as receivers
        for(int i = 0; i < this.dealer.getSession().getInGamePlayers().size(); i++)
            msg.addReceiver(this.dealer.getSession().getInGamePlayers().get(i).getPlayer());

        LinkedList<Card> table = this.dealer.getSession().getTable();

        // Configure message
        msg.setContent(table.get(0) + ":" + table.get(1) + ":" + table.get(2));
        msg.setConversationId("flop-table-cards");

        // Send message
        myAgent.send(msg);
        System.out.println(this.dealer.getName() + " :: Send table initial configuration: " + msg.getContent());

        // Terminates behaviour
        this.terminate();
    }

    /**
     * Terminates behaviour
     */
    private void terminate() {
        status = true;
    }

    @Override
    public boolean done() {
        return status;
    }
}
