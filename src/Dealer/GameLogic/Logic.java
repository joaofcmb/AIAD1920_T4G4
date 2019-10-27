package Dealer.GameLogic;

import Dealer.Dealer;
import jade.core.behaviours.SequentialBehaviour;

public class Logic extends SequentialBehaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Game logic constructor
     * @param dealer agent
     */
    public Logic(Dealer dealer) {
        this.dealer = dealer;
        this.addBehaviours();
    }

    private void addBehaviours() {
        addSubBehaviour(new PreFlop(this.dealer));
        addSubBehaviour(new Bet(this.dealer, this.dealer.getSession().getInGamePlayers().size() == 2 ? 0 : 2));
        addSubBehaviour(new Flop(this.dealer));
    }

    @Override
    public int onEnd() {
        System.out.println(status);
       // reset();
       // this.dealer.addBehaviour(this);
        return super.onEnd();
    }
}
