package Player.GameLogic.BetLogic;

import Player.Player;
import jade.core.behaviours.ParallelBehaviour;

public class Bet extends ParallelBehaviour {

    /**
     * Player agent
     */
    private Player player;

    /**
     * Bet constructor
     * @param player agent
     */
    public Bet(Player player) {
        super(WHEN_ANY);
        this.player = player;
        this.addBehaviours();
    }

    /**
     * Adds bet behaviours
     */
    private void addBehaviours() {
        addSubBehaviour(new BetHandler(this.player));
        addSubBehaviour(new BetStorageServer(this.player));
        addSubBehaviour(new BetEndServer(this.player));
    }

}
