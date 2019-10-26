package Player.GameLogic;

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

    private void addBehaviours() {
        addSubBehaviour(new PreFlop(this.player));
        addSubBehaviour(new Flop(this.player));
    }

    @Override
    public int onEnd() {
        System.out.println(status);
        // reset();
        // this.dealer.addBehaviour(this);
        return super.onEnd();
    }
}
