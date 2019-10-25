package Player;

import jade.core.AID;
import jade.core.Agent;

public class Player extends Agent {

    /**
     * Player buy in
     */
    private int buyIn;

    /**
     * Player personality
     */
    private Personality personality;

    /**
     * Player state machine
     */
    public enum State {INIT, SEARCHING_SESSION, JOINING_SESSION, PLAYING}

    /**
     * Initial state
     */
    private State playerState = State.INIT;

    /**
     * Dealer of the current session
     */
    private AID dealer = null;

    /**
     * Agent initializations
     */
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

    /**
     * Agent clean-up operations
     */
    protected void takeDown() {
        System.out.println(this.getName() + " :: Terminating.");
    }

    /**
     * Get player state
     */
    State getPlayerState() {
        return playerState;
    }

    /**
     * Get player buy in
     */
    int getBuyIn() {
        return buyIn;
    }

    /**
     * Get player's current session dealer
     */
    public AID getDealer() {
        return dealer;
    }

    /**
     * Set player state
     */
    void setPlayerState(State playerState) {
        this.playerState = playerState;
    }

    /**
     * Set player current session dealer
     */
    void setDealer(AID dealer) {
        this.dealer = dealer;
    }
}
