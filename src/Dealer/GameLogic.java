package Dealer;

import Session.Session;
import jade.core.behaviours.Behaviour;

public class GameLogic extends Behaviour {

    /**
     * Dealer agent
     */
    Dealer dealer;

    /**
     * Current session
     */
    Session session;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Game logic state machine
     */
    private enum State {PRE_FLOP}

    /**
     * Current game state
     */
    private State gameState = State.PRE_FLOP;

    /**
     * Game logic constructor
     * @param dealer agent
     */
    GameLogic(Dealer dealer) {
        this.dealer = dealer;
        this.session = this.dealer.getSession();
    }

    @Override
    public void action() {
        if(this.dealer.getDealerState() == Dealer.State.IN_SESSION) {
            switch (this.gameState) {
                case PRE_FLOP:
                    // TODO - Deal two cards for each player one at time
                    for(int i = 0; i < 2; i++) {
                        for (Player player: this.session.getCurrPlayers()) {
                            System.out.println(player.getPlayer().getName() + " - " + this.session.getDeck().getCard());
                        }
                    }

                    this.terminate();
                    break;
            }
        }
        else
            this.terminate();
    }

    /**
     * Terminates behaviour
     */
    private void terminate() {
        this.status = true;
    }

    @Override
    public boolean done() {
        return this.status;
    }
}
