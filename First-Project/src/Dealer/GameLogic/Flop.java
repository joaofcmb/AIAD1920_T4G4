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
     * Logic behaviour
     */
    private Logic logic;

    /**
     * Flop constructor
     * @param dealer agent
     */
    Flop(Dealer dealer, Logic logic) {
        this.dealer = dealer;
        this.logic = logic;
        this.dealer.getSession().getDeck().getCard();

        this.dealer.getWindow().cleanPlayerAction();
    }

    @Override
    public void action() {
        this.dealer.getWindow().updateDealerAction("Table initial configuration");

        // Adds three cards on table
        for(int i = 0; i < 3; i++) {
            Card card = this.dealer.getSession().getDeck().getCard();
            this.dealer.getSession().getTable().add(card);
            this.dealer.getWindow().addCardsToTable(card.toString());
        }

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        // Add all players as receivers
        for(int i = 0; i < this.dealer.getSession().getCurrPlayers().size(); i++)
            msg.addReceiver(this.dealer.getSession().getCurrPlayers().get(i).getPlayer());

        LinkedList<Card> table = this.dealer.getSession().getTable();

        // Configure message
        msg.setContent(table.get(0) + ":" + table.get(1) + ":" + table.get(2));
        msg.setConversationId("flop-table-cards");

        // Send message
        System.out.println(this.dealer.getName() + " :: Send table initial configuration: " + msg.getContent());
        myAgent.send(msg);

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

    @Override
    public int onEnd() {
        this.dealer.pauseGUI();
        this.logic.nextState("Next State");
        return super.onEnd();
    }
}
