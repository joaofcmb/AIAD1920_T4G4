package Dealer.GameLogic;

import Dealer.Dealer;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;

public class Flop extends Behaviour {

    Dealer dealer;

    boolean status = false;

    enum State {DEALING_TABLE_CARDS, SMALL_BIG_BLIND, BETTING}

    State state = State.DEALING_TABLE_CARDS;

    Flop(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        switch (state){
            case DEALING_TABLE_CARDS:
                for(int i = 0; i < 3; i++)
                    this.dealer.getSession().getTable().add(this.dealer.getSession().getDeck().getCard());

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                // Add all players as receivers
                for(int i = 0; i < this.dealer.getSession().getCurrPlayers().size(); i++)
                    msg.addReceiver(this.dealer.getSession().getCurrPlayers().get(i).getPlayer());

                LinkedList<Card> table = this.dealer.getSession().getTable();

                // Configure message
                msg.setContent(table.get(0) + ":" + table.get(1) + ":" + table.get(2));
                msg.setConversationId("flop-table-cards");

                // Send message
                myAgent.send(msg);
                System.out.println(this.dealer.getName() + " :: Send table initial configuration: " + msg.getContent());

                this.state = State.SMALL_BIG_BLIND;
                break;
            case SMALL_BIG_BLIND:
                break;
            case BETTING:

                break;
        }
    }

    void terminate() {
        status = true;
    }

    @Override
    public boolean done() {
        return status;
    }
}
