package Dealer;

import jade.core.behaviours.TickerBehaviour;

public class SessionPlayersServer extends TickerBehaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Default constructor
     * @param dealer Agent
     * @param period Tick period
     */
    SessionPlayersServer(Dealer dealer, long period) {
        super(dealer, period);
        this.dealer = dealer;
    }

    @Override
    protected void onTick() {
        if(this.dealer.getDealerState() == Dealer.State.SESSION_SETUP) {
            if(this.dealer.getCurrPlayers().size() >= this.dealer.getTableSettings().get("minPlayers")) {
                this.dealer.setDealerState(Dealer.State.STARTING_SESSION);
                this.dealer.addBehaviour(new StartingSession(this.dealer));
                this.stop();
            }
        }
    }
}
