package Dealer.GameLogic;

import Dealer.Dealer;
import Dealer.Player;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
    private int maxPot;

    /**
     * Possible betting states
     */
    enum State {PLAYER_BET_TURN, RECEIVE_PLAYER_BET}

    /**
     * Current state
     */
    private State state = State.PLAYER_BET_TURN;

    /**
     * Logic behaviour
     */
    private Logic logic;

    private LinkedList<Player> playersToBet = new LinkedList<>();

    private int noFoldPlayers = 0;

    private int noAllInPlayers = 0;

    private int lastBet;

    /**
     * Bet constructor
     * @param dealer agent
     */
    Bet(Dealer dealer, Logic logic, int playerTurn, int lastBet) {
        this.dealer = dealer;
        this.logic = logic;
        this.lastBet = lastBet;
        this.maxPot = lastBet;
        this.initializePlayersToBet(playerTurn);
    }

    private void initializePlayersToBet(int playerTurn) {
        Player firstPlayer = this.dealer.getSession().getCurrPlayers().get(playerTurn);

        if(!firstPlayer.isFoldStatus() && !firstPlayer.isAllInStatus())
            this.playersToBet.add(firstPlayer);

        for(Player player : this.dealer.getCurrPlayers()) {
            if(!this.playersToBet.contains(player) && !player.isFoldStatus() && !player.isAllInStatus())
                this.playersToBet.add(player);
        }
    }

    @Override
    public void action() {
        switch (this.state) {
            case PLAYER_BET_TURN:
                // Updates GUI
                this.dealer.getWindow().updateDealerAction("Player bet turn.");

                AID playerTurn = this.playersToBet.getFirst().getPlayer();

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                // Configure message
                msg.addReceiver(playerTurn);
                msg.setContent(this.getBettingOptions());
                msg.setConversationId("betting-phase");
                msg.setReplyWith("betting-phase" + System.currentTimeMillis());

                // Send message
                System.out.println(this.dealer.getName() + " :: Sending betting options :: " + playerTurn.getName() + " :: " + msg.getContent());
                myAgent.send(msg);

                // Prepare the template to get the reply
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("betting-phase"),
                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                this.state = State.RECEIVE_PLAYER_BET;
                break;
            case RECEIVE_PLAYER_BET:
                // Updates GUI
                this.dealer.getWindow().updateDealerAction("Receive players bet");

                // Receive reply
                ACLMessage reply = myAgent.receive(msgTemplate);

                if(reply != null) {
                    System.out.println(this.dealer.getName() + " :: Received betting option :: " +
                            reply.getSender().getName() + " :: " + reply.getContent());

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

                    // Updates GUI
                    this.dealer.getWindow().updatePlayerAction(reply.getSender().getName(), reply.getContent());

                    // Parse bet
                    this.parseBet(reply.getSender(), reply.getContent());

                    // Pauses GUI
                    this.dealer.pauseGUI();

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
        if(this.maxPot == 0)
            return "Check:Bet-" + this.dealer.getTableSettings().get("bigBlind") + ":All in";
        else {
            int currChips = this.playersToBet.getFirst().getChips();
            int raiseValue = this.lastBet * 2;
            int callValue = this.maxPot - this.playersToBet.getFirst().getPot();

            if(callValue == 0)
                return "Fold:Check" + (raiseValue >= currChips ? ":" : ":Raise-" + raiseValue + ":") + "All in";
            else {
                if(callValue >= currChips)
                    return "Fold:All in";
                else
                    return "Fold:Call-" + callValue + (raiseValue >= currChips ? ":" : ":Raise-" + raiseValue + ":")
                            + "All in";
            }
        }
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
                this.noFoldPlayers++;
                this.playersToBet.getFirst().setFoldStatus();
                this.playersToBet.removeFirst();
                return;
            }
            else if(content[0].equals("All in")) {
                this.noAllInPlayers++;
                this.playersToBet.getFirst().setAllInStatus();
                value = this.playersToBet.getFirst().getChips();
            }
        }
        else
            value = Integer.parseInt(content[1]);

        // Add bet and update pot
        this.dealer.getSession().addBet(player.getName(), bet);

        // Update current player data
        Player currPlayer = this.playersToBet.removeFirst();

        currPlayer.updatePot(value);
        currPlayer.updateChips(-value);

        // Updates max pot and last bet values
        this.maxPot = Math.max(this.maxPot, currPlayer.getPot());
        this.lastBet = value;

        if(content[0].equals("Raise") || content[0].equals("All in")) {
            for(Player nextPlayer : this.dealer.getCurrPlayers()) {
                if(!this.playersToBet.contains(nextPlayer) && !nextPlayer.isFoldStatus() && !nextPlayer.isAllInStatus()
                        && !nextPlayer.getPlayer().getName().equals(currPlayer.getPlayer().getName()))
                    this.playersToBet.add(nextPlayer);
            }
        }

        // Updates GUI
        this.dealer.getWindow().addChipsToPot(player.getName(), value);
        this.dealer.getWindow().managePlayerChips(player.getName(), value, false);
    }

    /**
     * Terminates behaviour if all players made their bets and its value is the same for each player
     */
    private void terminate() {
        this.status = this.playersToBet.isEmpty() || (this.dealer.getCurrPlayers().size() - noFoldPlayers) <= 1 ||
                (this.dealer.getCurrPlayers().size() - noAllInPlayers) == 0;
        this.state = State.PLAYER_BET_TURN;
    }

    @Override
    public boolean done() {
        return this.status;
    }

    @Override
    public int onEnd() {
        // Update GUI
        this.dealer.getWindow().updateDealerAction("Terminating betting phase");

        // Inform other players that betting phase has ended
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        // Configure message
        for(Player player : this.dealer.getSession().getCurrPlayers())
            msg.addReceiver(player.getPlayer());

        String status = "Next State";
        msg.setConversationId("end-betting-phase");

        if((this.dealer.getCurrPlayers().size() - noFoldPlayers) <= 1)
            status = "Last player standing";
        else if((this.dealer.getCurrPlayers().size() - noAllInPlayers) == 0)
            status = "Every player all in";

        msg.setContent(status);
        msg.setReplyWith("end-betting-phase" + System.currentTimeMillis());

        // Send message
        System.out.println(this.dealer.getName() + " :: Terminating betting phase ");
        myAgent.send(msg);

        // Reset betting variables
        for(Player player : this.dealer.getSession().getCurrPlayers())
            player.resetCurrBet();

        this.dealer.pauseGUI();
        this.logic.nextState(status);

        return super.onEnd();
    }

}
