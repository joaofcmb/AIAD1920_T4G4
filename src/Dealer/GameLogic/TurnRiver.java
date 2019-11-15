package Dealer.GameLogic;

import Dealer.Dealer;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class TurnRiver extends Behaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Type of moment: turn or river
     */
    private String moment;

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     * Turn and River constructor
     * @param dealer agent
     * @param moment type of moment
     */
    TurnRiver(Dealer dealer, Logic logic, String moment) {
        this.dealer = dealer;
        this.logic = logic;
        this.moment = moment;
        this.dealer.getSession().getDeck().getCard();   // Removes card from deck [RULE]
    }

    @Override
    public void action() {
        // Adds one more card to the table
        Card newCard =  this.dealer.getSession().getDeck().getCard();
        this.dealer.getSession().getTable().add(newCard);
        this.dealer.getWindow().addCardsToTable(newCard.toString());
        this.dealer.getWindow().updateDealerAction("Added new card.");

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        // Add all players as receivers
        for(int i = 0; i < this.dealer.getSession().getCurrPlayers().size(); i++)
            msg.addReceiver(this.dealer.getSession().getCurrPlayers().get(i).getPlayer());

        // Configure message
        msg.setContent(this.moment.equals("turn") ? this.dealer.getSession().getTable().get(3).toString() :
                this.dealer.getSession().getTable().get(4).toString());
        msg.setConversationId(this.moment + "-table-cards");

        // Send message
        System.out.println(this.dealer.getName() + " :: Send new added card :: " + msg.getContent());
        myAgent.send(msg);

        // Terminates behaviour
        this.terminate();
    }

    /**
     * Terminates behaviour
     */
    private void terminate() {
        status = true;
    }

    @Override
    public boolean done() {
        return status;
    }

    @Override
    public int onEnd() {
        this.dealer.pauseGUI();
        this.logic.nextState("Next State");
        return super.onEnd();
    }
}
