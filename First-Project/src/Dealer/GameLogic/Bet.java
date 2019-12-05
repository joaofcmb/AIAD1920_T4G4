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
    private int maxBetPot;

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

    /**
     * Players that need to bet before ending betting phase
     */
    private LinkedList<Player> playersToBet = new LinkedList<>();

    /**
     * Number of players who have folded
     */
    private int noFoldPlayers;

    /**
     * Number of players who have made all in
     */
    private int noAllInPlayers;

    /**
     * Last bet made. Always considering the biggest value
     */
    private int lastBet;

    /**
     * Bet constructor
     * @param dealer agent
     */
    Bet(Dealer dealer, Logic logic, int playerTurn, int initValue) {
        this.dealer = dealer;
        this.logic = logic;
        this.lastBet = initValue;
        this.maxBetPot = initValue;

        // Adds the first player to bet if possible
        Player firstBetPlayer = this.dealer.getSession().getCurrPlayers().get(playerTurn);
        if(!firstBetPlayer.getFoldStatus() && !firstBetPlayer.getAllInStatus())
            this.playersToBet.add(firstBetPlayer);

        this.addPlayersToBet(playerTurn);

        // Initializes correct number of folded and all in players
        this.noFoldPlayers = 0;
        this.noAllInPlayers = 0;

        for(Player player : this.dealer.getSession().getCurrPlayers()) {
            if(player.getAllInStatus())
                this.noAllInPlayers++;
            else if(player.getFoldStatus())
                this.noFoldPlayers++;
        }
    }

    /**
     * Adds in order the remaining players that must bet before ending betting phase
     * @param currBetPlayer Current player that made the bet
     */
    private void addPlayersToBet(int currBetPlayer) {
        int playerIndex = this.dealer.getSession().getCurrPlayers().size() == currBetPlayer + 1 ? 0 : currBetPlayer + 1;

        while (playerIndex != currBetPlayer) {
            Player player = this.dealer.getCurrPlayers().get(playerIndex);
            if(!this.playersToBet.contains(player) && !player.getFoldStatus() && !player.getAllInStatus())
                this.playersToBet.add(player);

            playerIndex = this.dealer.getSession().getCurrPlayers().size() == playerIndex + 1 ? 0 : playerIndex + 1;
        }
    }

    @Override
    public void action() {
        switch (this.state) {
            case PLAYER_BET_TURN:
                // Updates GUI
                this.dealer.getWindow().updateDealerAction("Player bet turn.");

                // Gets first player to retrieve its betting option
                AID currBetPlayer = this.playersToBet.getFirst().getPlayer();

                // Configure message
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

                msg.addReceiver(currBetPlayer);
                msg.setContent(this.getBettingOptions());
                msg.setConversationId("betting-phase");
                msg.setReplyWith("betting-phase" + System.currentTimeMillis());

                // Send message
                this.dealer.printInfo("Sending betting options :: "
                        + currBetPlayer.getName() + " :: " + msg.getContent());
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
                    // Print player betting option
                    this.dealer.printInfo("Received betting option :: "
                            + reply.getSender().getName() + " :: " + reply.getContent());

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
                    this.dealer.printInfo("Sharing " + reply.getSender().getName() +
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
        if(this.maxBetPot == 0)
            return "Fold:Check:Bet-" + this.dealer.getTableSettings().get("bigBlind") + ":All in";
        else {
            int currChips = this.playersToBet.getFirst().getChips();
            int raiseValue = this.lastBet * 2;
            int callValue = this.maxBetPot - this.playersToBet.getFirst().getBetPot();

            if(callValue == 0)
                return "Fold:Check" + (raiseValue >= currChips || raiseValue < this.dealer.getTableSettings().get("bigBlind") ? ":" : ":Raise-" + raiseValue + ":") + "All in";
            else {
                if(callValue >= currChips)
                    return "Fold:All in";
                else
                    return "Fold:Call-" + callValue + (raiseValue >= currChips || raiseValue < this.dealer.getTableSettings().get("bigBlind") ? ":" : ":Raise-" + raiseValue + ":")
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

        currPlayer.updatePot(value); currPlayer.updateBetPot(value);
        currPlayer.updateChips(-value);

        // Updates max pot and last bet values
        this.maxBetPot = Math.max(this.maxBetPot, currPlayer.getBetPot());
        this.lastBet = Math.max(this.lastBet, value);

        if(!content[0].equals("Call") && !content[0].equals("Check")) {
            for(int i = 0; i < this.dealer.getSession().getCurrPlayers().size(); i++)
                if(this.dealer.getSession().getCurrPlayers().get(i).getPlayer().getName().equals(currPlayer.getPlayer().getName())) {
                    this.addPlayersToBet(i);
                    break;
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
        this.status = this.playersToBet.isEmpty() || ((this.dealer.getCurrPlayers().size() - noFoldPlayers) <= 1) ||
                ((this.dealer.getCurrPlayers().size() - noAllInPlayers - noFoldPlayers) <= 1 && this.playersToBet.isEmpty());
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

        if((this.dealer.getCurrPlayers().size() - noAllInPlayers - noFoldPlayers) <= 1)
            status = "No more betting";

        msg.setContent(status);
        msg.setReplyWith("end-betting-phase" + System.currentTimeMillis());

        // Send message
        System.out.println(this.dealer.getName() + " :: Terminating betting phase ");
        myAgent.send(msg);

        // Reset players bet pots
        for(Player player : this.dealer.getSession().getCurrPlayers())
            player.resetBetPot();

        this.dealer.pauseGUI();
        this.logic.nextState(status);

        return super.onEnd();
    }

}
