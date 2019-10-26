package Dealer.GameLogic;

import Dealer.Dealer;
import jade.core.behaviours.Behaviour;

public class Flop extends Behaviour {

    Dealer dealer;

    boolean status = false;

    Flop(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {

    }

    void terminate() {
        status = true;
    }

    @Override
    public boolean done() {
        return status;
    }
}
