package Player.GameLogic;

import Player.GameLogic.BetLogic.Bet;
import Player.Player;
import jade.core.behaviours.CyclicBehaviour;

public class Logic extends CyclicBehaviour {

    /**
     * Player agent
     */
    private Player player;

    /**
     * Possible states
     */
    public enum State {PRE_FLOP, FLOP, TURN, RIVER, BET, END_GAME, BETWEEN_GAMES, ON_HOLD}

    /**
     * Stores last to states to allow move current state to the next
     */
    private State[] lastStates = {State.PRE_FLOP, State.PRE_FLOP};

    private State state = State.PRE_FLOP;

    /**
     * Skip betting phases status. If true it means that no more bets will be made, false otherwise
     */
    private boolean skipBettingPhases = false;

    /**
     * Game logic constructor
     * @param player agent
     */
    public Logic(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        switch (state) {
            case PRE_FLOP:
                this.player.addBehaviour(new PreFlop(this.player, this));
                this.state = State.ON_HOLD;
                break;
            case FLOP:
                this.player.addBehaviour(new Flop(this.player, this));
                this.state = State.ON_HOLD;
                break;
            case TURN:
                this.player.addBehaviour(new TurnRiver(this.player, this, "turn"));
                this.state = State.ON_HOLD;
                break;
            case RIVER:
                this.player.addBehaviour(new TurnRiver(this.player, this, "river"));
                this.state = State.ON_HOLD;
                break;
            case END_GAME:
                this.player.addBehaviour(new EndGame(this.player, this, this.player.getFoldStatus()));
                this.state = State.ON_HOLD;
                break;
            case BETWEEN_GAMES:
                this.player.addBehaviour(new BetweenGames(this.player, this));
                this.state = State.ON_HOLD;
                break;
            case BET:
                this.player.addBehaviour(new Bet(this.player, this));
                this.state = State.ON_HOLD;
                break;
        }
    }

    public void nextState(String action) {
        if(action.equals("No more betting"))
            this.skipBettingPhases = true;

        if (lastStates[0] == State.PRE_FLOP) state = State.BET;
        else if (lastStates[0] == State.BET && lastStates[1] == State.PRE_FLOP) state = State.FLOP;
        else if (lastStates[0] == State.FLOP) {
            if(this.skipBettingPhases) state = State.TURN;
            else state = State.BET;
        }
        else if (lastStates[0] == State.BET && lastStates[1] == State.FLOP) state = State.TURN;
        else if (lastStates[0] == State.TURN) {
            if(this.skipBettingPhases) state = State.RIVER;
            else state = State.BET;
        }
        else if (lastStates[0] == State.BET && lastStates[1] == State.TURN) state = State.RIVER;
        else if (lastStates[0] == State.RIVER) {
            if(this.skipBettingPhases) state = State.END_GAME;
            else state = State.BET;
        }
        else if (lastStates[0] == State.BET && lastStates[1] == State.RIVER) state = State.END_GAME;
        else if (lastStates[0] == State.END_GAME) state = State.BETWEEN_GAMES;
        else if (lastStates[0] == State.BETWEEN_GAMES) state = State.PRE_FLOP;

        // Update last states
        this.lastStates[1] = this.lastStates[0];
        this.lastStates[0] = state;
    }

}
