package Player;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;

public class Player extends Agent {
    private int buyIn;
    private Personality personality;

    public static enum State {INIT, SEARCHING_SESSION, JOINING_SESSION, PLAYING};

    private State playerState = State.INIT;

    @Override
    protected void setup() {
        final Object[] playerSettings = getArguments();

        if (playerSettings != null && playerSettings.length == 2) {
            this.buyIn = Integer.parseInt((String) playerSettings[0]);
            //this.personality = (Personality) playerSettings[1];

            // Updates player state and adds a TickerBehaviour that schedules a request for a session every X seconds
            this.playerState = State.SEARCHING_SESSION;
            addBehaviour(new SearchSessionServer(this, 1000));
        }
        else {
            System.out.println("Usage:: <name>:<package_name>.<class_name>(buy_in, personality)");
            doDelete();
        }
    }

    // Agent clean-up operations
    protected void takeDown() {
        System.out.println("Dealer " + getAID().getName() + " terminating.");
    }

    State getPlayerState() {
        return playerState;
    }

    public void setPlayerState(State playerState) {
        this.playerState = playerState;
    }
}
