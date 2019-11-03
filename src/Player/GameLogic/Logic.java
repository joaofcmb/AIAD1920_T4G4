package Player.GameLogic;

import Player.GameLogic.BetLogic.Bet;
import Player.Player;
import jade.core.behaviours.CyclicBehaviour;

public class Logic extends CyclicBehaviour {

    /**
     * Player agent
     */
    private Player player;

    public enum State {PRE_FLOP, FLOP, TURN, RIVER, BET, END_GAME, BETWEEN_GAMES, ON_HOLD}

    private State firstLastState = State.PRE_FLOP;

    private State secondLastState = State.PRE_FLOP;

    private State state = State.PRE_FLOP;

    /**
     * Game logic constructor
     * @param player agent
     */
    public Logic(Player player) {
        this.player = player;
        //this.addBehaviours();
    }

//    /**
//     * Adds logic behaviours
//     */
//    private void addBehaviours() {
//        addSubBehaviour(new PreFlop(this.player));
//        addSubBehaviour(new Bet(this.player));
//        addSubBehaviour(new Flop(this.player));
//        addSubBehaviour(new Bet(this.player));
//        addSubBehaviour(new TurnRiver(this.player, "turn"));
//        addSubBehaviour(new Bet(this.player));
//        addSubBehaviour(new TurnRiver(this.player, "river"));
//        addSubBehaviour(new Bet(this.player));
//        addSubBehaviour(new EndGame(this.player));
//        addSubBehaviour(new BetweenGames(this.player));
//    }

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
                this.player.addBehaviour(new EndGame(this.player, this));
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

    public void nextState() {
        if(firstLastState ==  State.PRE_FLOP) state = State.BET;
        else if(firstLastState == State.BET && secondLastState == State.PRE_FLOP) state = State.FLOP;
        else if(firstLastState == State.FLOP) state = State.BET;
        else if(firstLastState == State.BET && secondLastState == State.FLOP) state = State.TURN;
        else if(firstLastState == State.TURN) state = State.BET;
        else if(firstLastState == State.BET && secondLastState == State.TURN) state = State.RIVER;
        else if(firstLastState == State.RIVER) state = State.BET ;
        else if(firstLastState == State.BET && secondLastState == State.RIVER) state = State.END_GAME;
        else if(firstLastState == State.END_GAME) state = State.BETWEEN_GAMES;
        else if(firstLastState == State.BETWEEN_GAMES) state = State.PRE_FLOP;

        // Update last states
        this.secondLastState = this.firstLastState;
        this.firstLastState = state;
    }

//    @Override
//    public int onEnd() {
//        restart();
//        return super.onEnd();
//    }
}
