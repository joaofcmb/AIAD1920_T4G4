package Dealer.GameLogic;

import Dealer.Dealer;
import jade.core.behaviours.SequentialBehaviour;

public class Logic extends SequentialBehaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Game logic constructor
     * @param dealer agent
     */
    public Logic(Dealer dealer) {
        this.dealer = dealer;
        this.addBehaviours();
    }

    /**
     * Adds logic sequential behaviours
     */
    private void addBehaviours() {
        addSubBehaviour(new PreFlop(this.dealer));
        addSubBehaviour(new Bet(this.dealer, this.dealer.getSession().getInGamePlayers().size() == 2 ? 0 : 2, this.dealer.getTableSettings().get("bigBlind")));
        addSubBehaviour(new Flop(this.dealer));
        addSubBehaviour(new Bet(this.dealer, 0, 0));
    }

    @Override
    public int onEnd() {
        // reset();
        // this.dealer.addBehaviour(this);
        return super.onEnd();
    }
}
