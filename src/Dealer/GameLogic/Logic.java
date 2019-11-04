package Dealer.GameLogic;

import Dealer.Dealer;
import jade.core.behaviours.CyclicBehaviour;

public class Logic extends CyclicBehaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    public enum State {PRE_FLOP, FLOP, TURN, RIVER, SPECIAL_BET, BET, END_GAME, BETWEEN_GAMES, ON_HOLD}

    private State firstLastState = State.PRE_FLOP;

    private State secondLastState = State.PRE_FLOP;

    private State state = State.PRE_FLOP;

    /**
     * Game logic constructor
     * @param dealer agent
     */
    public Logic(Dealer dealer) {
        this.dealer = dealer;
        // this.addBehaviours();
    }

    @Override
    public void action() {
        switch (state) {
            case PRE_FLOP:
                this.dealer.addBehaviour(new PreFlop(this.dealer, this));
                this.state = State.ON_HOLD;
                break;
            case FLOP:
                this.dealer.addBehaviour(new Flop(this.dealer, this));
                this.state = State.ON_HOLD;
                break;
            case TURN:
                this.dealer.addBehaviour(new TurnRiver(this.dealer, this, "turn"));
                this.state = State.ON_HOLD;
                break;
            case RIVER:
                this.dealer.addBehaviour(new TurnRiver(this.dealer, this, "river"));
                this.state = State.ON_HOLD;
                break;
            case END_GAME:
                this.dealer.addBehaviour(new EndGame(this.dealer, this));
                this.state = State.ON_HOLD;
                break;
            case BETWEEN_GAMES:
                this.dealer.addBehaviour(new BetweenGames(this.dealer, this));
                this.state = State.ON_HOLD;
                break;
            case SPECIAL_BET:
                this.dealer.addBehaviour(new Bet(this.dealer, this, this.dealer.getSession().getCurrPlayers().size() == 2 ? 0 : 2, this.dealer.getTableSettings().get("bigBlind")));
                this.state = State.ON_HOLD;
                break;
            case BET:
                this.dealer.addBehaviour(new Bet(this.dealer, this, 0, 0));
                this.state = State.ON_HOLD;
                break;
        }

    }

    void nextState() {

        if(firstLastState == State.PRE_FLOP) state = State.SPECIAL_BET;
        else if(firstLastState == State.SPECIAL_BET) state = State.FLOP ;
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

    //    /**
//     * Adds logic sequential behaviours
//     */
//    private void addBehaviours() {
//        addSubBehaviour(new PreFlop(this.dealer, this));
//        addSubBehaviour(new Bet(this.dealer, this.dealer.getSession().getCurrPlayers().size() == 2 ? 0 : 2, this.dealer.getTableSettings().get("bigBlind")));
//        addSubBehaviour(new Flop(this.dealer));
//        addSubBehaviour(new Bet(this.dealer, 0, 0));
//        addSubBehaviour(new TurnRiver(this.dealer, "turn"));
//        addSubBehaviour(new Bet(this.dealer, 0, 0));
//        addSubBehaviour(new TurnRiver(this.dealer, "river"));
//        addSubBehaviour(new Bet(this.dealer, 0, 0));
//        addSubBehaviour(new EndGame(this.dealer));
//        addSubBehaviour(new BetweenGames(this.dealer));
//        addSubBehaviour(new PreFlop(this.dealer, this));
//
//    }

//    @Override
//    public int onEnd() {
////         reset();
////         this.dealer.addBehaviour(this);
//
//         return super.onEnd();
//    }
}
