package Dealer.GameLogic;

import Dealer.Dealer;
import Dealer.Player;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Bet extends Behaviour {

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
     * Current game maximum bet
     */
    private int maxBet;

    /**
     * Stores players current betting phase bets history
     */
    private HashMap<String, LinkedList<String>> bets = new HashMap<>();

    /**
     * Possible betting states
     */
    enum State {PLAYER_BET_TURN, RECEIVE_PLAYER_BET}

    /**
     * Current state
     */
    private State state = State.PLAYER_BET_TURN;

    /**
     * Index referent to the player which is has to bet
     */
    private int playerTurn;

    /**
     * Logic behaviour
     */
    private Logic logic;

    /**
     * Bet constructor
     * @param dealer agent
     */
    Bet(Dealer dealer, Logic logic, int playerTurn, int maxBet) {
        this.dealer = dealer;
        this.logic = logic;
        this.playerTurn = playerTurn;
        this.maxBet = maxBet;

        // Special betting phase (small and big blind)
        if(this.maxBet != 0)
            this.bets = this.dealer.getSession().getBets();
    }

    @Override
    public void action() {
        switch (this.state) {
            case PLAYER_BET_TURN:
                this.dealer.getWindow().updateDealerAction("Player bet turn.");

                AID playerTurn = this.dealer.getSession().getCurrPlayers().get(this.playerTurn).getPlayer();

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                // Configure message
                msg.addReceiver(playerTurn);
                msg.setContent(this.getBettingOptions());
                msg.setConversationId("betting-phase");
                msg.setReplyWith("betting-phase" + System.currentTimeMillis());

                // Send message
                System.out.println(this.dealer.getName() + " :: Send betting options to " + playerTurn.getName()
                        + " :: " + msg.getContent());
                myAgent.send(msg);

                // Prepare the template to get the reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("betting-phase"),
                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                this.state = State.RECEIVE_PLAYER_BET;
                break;
            case RECEIVE_PLAYER_BET:
                this.dealer.getWindow().updateDealerAction("Receive players bet");

                // Receive replies
                ACLMessage reply = myAgent.receive(msgTemplate);

                if(reply != null) {
                    System.out.println(this.dealer.getName() + " :: " + reply.getSender().getName() +
                            " has sent his betting option :: " + reply.getContent());

                    // Inform other players about player bet
                    msg = new ACLMessage(ACLMessage.INFORM);

                    // Configure message
                    for(Player player : this.dealer.getSession().getCurrPlayers())
                        if(!player.getPlayer().getName().equals(reply.getSender().getName()))
                            msg.addReceiver(player.getPlayer());

                    msg.setContent(reply.getSender().getName() + ":" + reply.getContent());
                    msg.setConversationId("betting-storage");
                    msg.setReplyWith("betting-storage" + System.currentTimeMillis());

                    // Send message
                    System.out.println(this.dealer.getName() + " :: Sharing " + reply.getSender().getName() +
                            " bet with other players :: " + msg.getContent());
                    myAgent.send(msg);

                    this.dealer.getWindow().updatePlayerAction(reply.getSender().getName(), reply.getContent());

                    // Parse bet
                    this.parseBet(reply.getSender(), reply.getContent());

                    // Update player turn
                    this.playerTurn = (this.playerTurn == this.dealer.getSession().getCurrPlayers().size() - 1) ? 0 :
                            this.playerTurn + 1;

                    // Avoid folded players and all in players
                    while (this.dealer.getSession().getCurrPlayers().get(this.playerTurn).isFoldStatus() ||
                            this.dealer.getSession().getCurrPlayers().get(this.playerTurn).isAllInStatus()) {
                        this.playerTurn = (this.playerTurn == this.dealer.getSession().getCurrPlayers().size() - 1) ? 0 :
                                this.playerTurn + 1;;
                    }

                    // Determines whether betting phase has ended or not
                    this.terminate();
                }
                else {
                    block();
                }
                break;
        }
    }

    /**
     * Retrieves player betting options
     */
    private String getBettingOptions() {
        return this.maxBet == 0 ? "Check:Bet:Fold:All in" :
                "Call-" + (this.maxBet - this.dealer.getSession().getCurrPlayers().get(this.playerTurn).getCurrBet()) + ":Fold:Raise-" +
                        this.maxBet * 2 + ":All in";
    }

    /**
     * Parses bet and performs respective operations
     * @param player agent
     * @param bet bet in string format
     */
    private void parseBet(AID player, String bet) {
        String[] content = bet.split("-");
        int value = 0;  // Check as default value

        if(content.length == 1) {
            if(content[0].equals("Fold")) {
                this.dealer.getSession().getCurrPlayers().get(playerTurn).setFoldStatus();
                return;
            }
            else if(content[0].equals("All in")) {
                this.dealer.getSession().getCurrPlayers().get(playerTurn).setAllInStatus();
                value = this.dealer.getSession().getCurrPlayers().get(playerTurn).getChips();
                this.maxBet = value;
            }
        }
        else {
            System.out.println("->>> " + content[1]);
            value = Integer.parseInt(content[1]);

            if(!content[0].equals("Call"))
                this.maxBet = value;
        }

        // Add bet and update pot
        this.addBet(player.getName(), bet);
        this.dealer.getSession().getCurrPlayers().get(playerTurn).updatePot(value);
        this.dealer.getSession().getCurrPlayers().get(playerTurn).updateChips(-value);

        this.dealer.getWindow().addChipsToPot(player.getName(), value);
        this.dealer.getWindow().managePlayerChips(player.getName(), value, false);
    }

    /**
     * Adds a new bet to the betting phase
     * @param playerName player name
     * @param bet bet made
     */
    private void addBet(String playerName, String bet) {
        // Updates session bets history
        this.dealer.getSession().addBet(playerName, bet);

        if(this.bets.containsKey(playerName))
            this.bets.get(playerName).push(bet);
        else {
            LinkedList<String> bets = new LinkedList<>(); bets.push(bet);
            this.bets.put(playerName, bets);
        }
    }

    /**
     * Terminates behaviour if all players made their bets and its value is the same for each player
     */
    private void terminate() {
        if(this.bets.containsKey(this.dealer.getSession().getCurrPlayers().get(this.playerTurn).getPlayer().getName()))
            if(this.dealer.getSession().getCurrPlayers().get(this.playerTurn).getCurrBet() == this.maxBet)
                this.status = true;

        this.state = State.PLAYER_BET_TURN;
    }

    @Override
    public boolean done() {
        return this.status;
    }

    @Override
    public int onEnd() {
        this.dealer.getWindow().updateDealerAction("Terminating betting phase");

        // Inform other players that betting phase has ended
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        // Configure message
        for(Player player : this.dealer.getSession().getCurrPlayers())
            msg.addReceiver(player.getPlayer());

        msg.setConversationId("end-betting-phase");
        msg.setReplyWith("end-betting-phase" + System.currentTimeMillis());

        // Send message
        System.out.println(this.dealer.getName() + " :: Terminating betting phase ");
        myAgent.send(msg);

        // Reset betting variables
        for(Player player : this.dealer.getSession().getCurrPlayers())
            player.resetCurrBet();

        this.dealer.pauseGUI();
        this.logic.nextState();
        return super.onEnd();
    }

}
