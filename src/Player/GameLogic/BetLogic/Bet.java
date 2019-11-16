package Player.GameLogic.BetLogic;

import Player.GameLogic.Logic;
import Player.Player;
import jade.core.behaviours.ParallelBehaviour;

public class Bet extends ParallelBehaviour {

    /**
     * Player agent
     */
    private Player player;

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     *
     */
    public String status = "Next State";

    /**
     * Bet constructor
     * @param player agent
     */
    public Bet(Player player, Logic logic) {
        super(WHEN_ANY);
        this.player = player;
        this.logic = logic;
        this.addBehaviours();
    }

    /**
     * Adds bet behaviours
     */
    private void addBehaviours() {
        addSubBehaviour(new BetHandler(this.player));
        addSubBehaviour(new BetStorageServer(this.player));
        addSubBehaviour(new BetEndServer(this, this.player));
    }

    @Override
    public int onEnd() {
        this.logic.nextState(this.status);
        return super.onEnd();
    }

}
