package Dealer;

import jade.core.behaviours.Behaviour;

public class GameLogic extends Behaviour {

    Dealer dealer;
    
    GameLogic(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {

    }

    @Override
    public boolean done() {
        return false;
    }
}
