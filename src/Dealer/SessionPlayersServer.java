package Dealer;

import jade.core.behaviours.TickerBehaviour;

public class SessionPlayersServer extends TickerBehaviour {

    public Dealer agent;

    SessionPlayersServer(Dealer agent, long period) {
        super(agent, period);
        this.agent = agent;
    }

    @Override
    protected void onTick() {
        //System.out.println(this.agent.getCurrPlayers().size());
        // TODO - Must decide wheter a game is dynamic or static (number of players is fixed)
    }
}
