package Dealer.GameLogic;

import Dealer.Dealer;
import Dealer.Player;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BetweenGames extends Behaviour {

    /**
     * Dealer agent
     */
    private Dealer dealer;

    /**
     * Behaviour status. True if ended, false otherwise
     */
    private boolean status = false;

    /**
     * Message template
     */
    private MessageTemplate msgTemplate;

    /**
     *
     */
    private enum State {LEAVING_PLAYERS, RECEIVING_PLAYER_ANSWER, NEW_GAME}

    private State state = State.LEAVING_PLAYERS;

    private int targetPlayer = 0;

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     *
     * @param dealer
     */
    BetweenGames(Dealer dealer, Logic logic) {
        this.dealer = dealer;
        this.logic = logic;
    }

    @Override
    public void action() {
        switch (state) {
            case LEAVING_PLAYERS:
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                this.dealer.getWindow().updateDealerAction("Check if players want to leave.");

                // Configure message
                msg.addReceiver(this.dealer.getSession().getCurrPlayers().get(this.targetPlayer).getPlayer());
                msg.setConversationId("between-games");
                msg.setReplyWith("between-games" + System.currentTimeMillis());

                // Send message
                System.out.println(this.dealer.getName() + " :: Check if " +
                        this.dealer.getSession().getCurrPlayers().get(this.targetPlayer).getPlayer().getName() + " wants to leave");
                myAgent.send(msg);

                // Prepare the template to get the reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("between-games"),
                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                this.state = State.RECEIVING_PLAYER_ANSWER;
                break;
            case RECEIVING_PLAYER_ANSWER:
                // Receive reply
                ACLMessage reply = myAgent.receive(this.msgTemplate);

                if(reply != null) {
                    if(reply.getContent().equals("Yes")) {
                        System.out.println(this.dealer.getName() + " :: Removing " + reply.getSender().getName() + " from session");
                        this.dealer.getWindow().removePlayer(reply.getSender().getName());
                        this.dealer.getSession().getCurrPlayers().remove(this.targetPlayer);

                        if(this.targetPlayer > this.dealer.getSession().getCurrPlayers().size() - 1)
                            this.state = State.NEW_GAME;
                        else
                            this.state = State.LEAVING_PLAYERS;

                        break;
                    }

                    if(this.targetPlayer == this.dealer.getSession().getCurrPlayers().size() - 1)
                        this.state = State.NEW_GAME;
                    else {
                        this.targetPlayer++;
                        this.state = State.LEAVING_PLAYERS;
                    }
                }
                else
                    block();
                break;
            case NEW_GAME:
                msg = new ACLMessage(ACLMessage.INFORM);

                // Configure message
                for(Player player : this.dealer.getSession().getCurrPlayers())
                    msg.addReceiver(player.getPlayer());

                msg.setConversationId("new-game");
                msg.setContent(this.dealer.getSession().getCurrPlayers().size() >= 2 ? "Yes" : "No");
                msg.setReplyWith("new-game" + System.currentTimeMillis());

                // Send message
                System.out.println(this.dealer.getName() + " :: Will start new game :: " + msg.getContent());
                myAgent.send(msg);

                if(msg.getContent().equals("No"))
                    myAgent.doDelete();
                else {
                    this.terminate();
                }
                break;
        }
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
    public int onEnd(){
        this.dealer.createNewSession();
        for(Player player : this.dealer.getCurrPlayers())
            player.resetAll();

        this.dealer.getWindow().addPlayerBlind(
                this.dealer.getSession().getSmallBlind().getPlayer().getName(),"S");
        this.dealer.getWindow().addPlayerBlind(
                this.dealer.getSession().getBigBlind().getPlayer().getName(),"B");
        this.dealer.getWindow().updateDealerAction("Session has started");

        System.out.println(this.dealer.getName() + " :: Prepared new session");
        this.dealer.getWindow().updateDealerAction("Prepared new session.");
        this.logic.nextState();
        return super.onEnd();
    }
}
