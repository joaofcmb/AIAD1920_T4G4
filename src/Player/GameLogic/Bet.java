package Player.GameLogic;

import Player.Player;
import jade.core.behaviours.ParallelBehaviour;

public class Bet extends ParallelBehaviour {

    /**
     *
     */
    Player player;

    /**
     *
     * @param player
     */
    Bet(Player player) {
        super(WHEN_ANY);
        this.player = player;
        this.addBehaviours();

    }

    /**
     *
     */
    private void addBehaviours() {
        addSubBehaviour(new HandleBetting(this.player));
        addSubBehaviour(new BettingEndServer(this.player));
    }

}
