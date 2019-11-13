package Dealer.SessionServer;

import Dealer.Dealer;
import Dealer.GameLogic.Logic;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class StartingSession extends Behaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Number of replies received
     */
    private int repliesCnt = 0;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Default constructor
     * @param dealer agent
     */
    StartingSession(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void action() {
        // Create new message to inform about session start
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        this.dealer.getWindow().updateDealerAction("Informing players about session start");
        System.out.println(this.dealer.getName() + " :: Informing players about session start");

        // Add all players as receivers
        for(int i = 0; i < this.dealer.getCurrPlayers().size(); i++)
            msg.addReceiver(this.dealer.getCurrPlayers().get(i).getPlayer());

        // Configure message
        msg.setContent("starting-session");
        msg.setConversationId("session-start");
        msg.setReplyWith("session-start" + System.currentTimeMillis());

        // Send message
        myAgent.send(msg);

        // Session start
        this.dealer.setDealerState(Dealer.State.IN_SESSION);
        this.dealer.createNewSession();

        this.dealer.getWindow().addPlayerBlind(
                this.dealer.getSession().getSmallBlind().getPlayer().getName(),"S");
        this.dealer.getWindow().addPlayerBlind(
                this.dealer.getSession().getBigBlind().getPlayer().getName(),"B");
        this.dealer.getWindow().updateDealerAction("Session has started");

        // Sessions starts
        System.out.println(this.dealer.getName() + " :: Session has started");
        this.dealer.pauseGUI();

        // Add game logic behaviour
        this.dealer.addBehaviour(new Logic(this.dealer));

        // Terminates behaviour
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
        return status;
    }
}
