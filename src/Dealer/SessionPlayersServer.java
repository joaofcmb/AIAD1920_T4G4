package Dealer;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class SessionPlayersServer extends TickerBehaviour {

    public Dealer agent;

    SessionPlayersServer(Dealer agent, long period) {
        super(agent, period);
        this.agent = agent;
    }

    @Override
    protected void onTick() {
        //System.out.println(this.agent.getTableSettings().get("currPlayers"));
    }
}
