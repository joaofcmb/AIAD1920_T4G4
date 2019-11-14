package Dealer.GameLogic;

import Dealer.Dealer;
import jade.core.behaviours.CyclicBehaviour;

public class Logic extends CyclicBehaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Possible states
     */
    public enum State {PRE_FLOP, FLOP, TURN, RIVER, SPECIAL_BET, BET, END_GAME, BETWEEN_GAMES, ON_HOLD}

    /**
     * Stores last to states to allow move current state to the next
     */
    private State[] lastStates = {State.PRE_FLOP, State.PRE_FLOP};

    /**
     * Current state
     */
    private State state = State.PRE_FLOP;

    /**
     * All in status. If true it means that every player made all in, false otherwise
     */
    private boolean allInStatus = false;

    /**
     * Game logic constructor
     * @param dealer agent
     */
    public Logic(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        switch (state) {
            case PRE_FLOP:
                System.out.println(this.dealer.getName() + " :: PRE-FLOP PHASE");
                this.dealer.addBehaviour(new PreFlop(this.dealer, this));
                this.state = State.ON_HOLD;
                break;
            case FLOP:
                System.out.println(this.dealer.getName() + " :: FLOP PHASE");
                this.dealer.addBehaviour(new Flop(this.dealer, this));
                this.state = State.ON_HOLD;
                break;
            case TURN:
                System.out.println(this.dealer.getName() + " :: TURN PHASE");
                this.dealer.addBehaviour(new TurnRiver(this.dealer, this, "turn"));
                this.state = State.ON_HOLD;
                break;
            case RIVER:
                System.out.println(this.dealer.getName() + " :: RIVER PHASE");
                this.dealer.addBehaviour(new TurnRiver(this.dealer, this, "river"));
                this.state = State.ON_HOLD;
                break;
            case END_GAME:
                System.out.println(this.dealer.getName() + " :: END GAME PHASE");
                this.dealer.addBehaviour(new EndGame(this.dealer, this));
                this.state = State.ON_HOLD;
                break;
            case BETWEEN_GAMES:
                System.out.println(this.dealer.getName() + " :: BETWEEN GAMES PHASE");
                this.dealer.addBehaviour(new BetweenGames(this.dealer, this));
                this.state = State.ON_HOLD;
                break;
            case SPECIAL_BET:
                System.out.println(this.dealer.getName() + " :: BET PHASE");
                this.dealer.addBehaviour(new Bet(this.dealer, this, this.dealer.getSession().getCurrPlayers().size() == 2 ? 0 : 2, this.dealer.getTableSettings().get("bigBlind")));
                this.state = State.ON_HOLD;
                break;
            case BET:
                System.out.println(this.dealer.getName() + " :: BET PHASE");
                this.dealer.addBehaviour(new Bet(this.dealer, this, 0, 0));
                this.state = State.ON_HOLD;
                break;
        }

    }

    /**
     * Updates logic current state
     */
    void nextState(String action) {
        if(action.equals("Last player standing"))
            state = State.END_GAME;
        else {
            if(action.equals("Every player all in"))
                this.allInStatus = true;

            if (lastStates[0] == State.PRE_FLOP) state = State.SPECIAL_BET;
            else if (lastStates[0] == State.SPECIAL_BET) state = State.FLOP;
            else if (lastStates[0] == State.FLOP) {
                if(this.allInStatus) state = State.TURN;
                else state = State.BET;
            }
            else if (lastStates[0] == State.BET && lastStates[1] == State.FLOP) state = State.TURN;
            else if (lastStates[0] == State.TURN) {
                if(this.allInStatus) state = State.RIVER;
                else state = State.BET;
            }
            else if (lastStates[0] == State.BET && lastStates[1] == State.TURN) state = State.RIVER;
            else if (lastStates[0] == State.RIVER) {
                if(this.allInStatus) state = State.END_GAME;
                else state = State.BET;
            }
            else if (lastStates[0] == State.BET && lastStates[1] == State.RIVER) state = State.END_GAME;
            else if (lastStates[0] == State.END_GAME) state = State.BETWEEN_GAMES;
            else if (lastStates[0] == State.BETWEEN_GAMES) state = State.PRE_FLOP;
        }

        // Update last states structure
        this.lastStates[1] = this.lastStates[0];
        this.lastStates[0] = state;
    }

}
