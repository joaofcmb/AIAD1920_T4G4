package Dealer.GameLogic;

import Dealer.Dealer;
import Session.Card;
import jade.core.behaviours.Behaviour;

public class EndTurn extends Behaviour {

    Dealer dealer;

    boolean status = false;

    EndTurn(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        for(Card card : this.dealer.getSession().getTable()) {
            System.out.print(card + "  || ");
        }
        System.out.println();
        this.terminate();
    }

    private void terminate() {
        this.status = true;
    }

    @Override
    public boolean done() {
        return this.status;
    }
}
