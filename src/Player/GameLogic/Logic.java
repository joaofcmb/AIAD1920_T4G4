package Player.GameLogic;

import Player.GameLogic.BetLogic.Bet;
import Player.Player;
import jade.core.behaviours.SequentialBehaviour;

public class Logic extends SequentialBehaviour {

    /**
     * Player agent
     */
    private Player player;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Game logic constructor
     * @param player agent
     */
    public Logic(Player player) {
        this.player = player;
        this.addBehaviours();
    }

    /**
     * Adds logic behaviours
     */
    private void addBehaviours() {
        addSubBehaviour(new PreFlop(this.player));
        addSubBehaviour(new Bet(this.player));
        addSubBehaviour(new Flop(this.player));
        addSubBehaviour(new Bet(this.player));
    }

    @Override
    public int onEnd() {
        // reset();
        // this.dealer.addBehaviour(this);
        return super.onEnd();
    }
}
