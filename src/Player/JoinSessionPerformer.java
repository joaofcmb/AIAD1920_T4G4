package Player;

import Player.GameLogic.Logic;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class JoinSessionPerformer extends Behaviour {

    /**
     * Player object
     */
    private Player player;

    /**
     * The list of known dealers agents
     */
    private AID[] dealerAgents;

    /**
     * The template to receive replies
     */
    private MessageTemplate msgTemplate;

    /**
     * Number of replies received
     */
    private int repliesCnt;

    /**
     * Dealer agent
     */
    private AID dealer;

    /**
     * Process step
     */
    private int step = 0;

    /**
     * Default constructor
     * @param player Agent
     * @param dealerAgents Possible sessions that can be joined
     */
    JoinSessionPerformer(Player player, AID[] dealerAgents) {
        this.player = player;
        this.repliesCnt = 0;
        this.dealerAgents = dealerAgents;
    }

    @Override
    public void action() {
        if(this.player.getPlayerState() == Player.State.JOINING_SESSION) {
            switch (step) {
                case 0: // Send the CFP to all dealers
                    ACLMessage CFP = new ACLMessage(ACLMessage.CFP);
                    for (AID dealerAgent : this.dealerAgents) {
                        CFP.addReceiver(dealerAgent);
                    }

                    // Configure message
                    CFP.setContent(Integer.toString(this.player.getBuyIn()));
                    CFP.setConversationId("searching-session");
                    CFP.setReplyWith("CFP" + System.currentTimeMillis()); // Unique value

                    // Send CFP
                    myAgent.send(CFP);

                    // Prepare the template to get proposals
                    this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("searching-session"),
                            MessageTemplate.MatchInReplyTo(CFP.getReplyWith()));
                    step++;
                    break;
                case 1: // Receive all proposals/refusals from dealer agents
                    ACLMessage reply = myAgent.receive(this.msgTemplate);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            this.dealer = reply.getSender();
                            step++;
                        }

                        // Increment number of replies received
                        repliesCnt++;

                        // Check if there are some replies to be received
                        if (repliesCnt >= this.dealerAgents.length && this.dealer == null) {
                            this.player.setPlayerState(Player.State.SEARCHING_SESSION);
                            this.player.addBehaviour(new SearchSessionServer(this.player, 1000));

                            System.out.println(this.player.getName() + " :: Could not join "
                                    + reply.getSender().getName() + " session. Error: " + reply.getContent() );
                            step = 5;
                        }
                    }
                    else {
                        block();
                    }
                    break;
                case 2: // Send the intention to join the seller
                    ACLMessage session = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

                    // Configure message
                    session.addReceiver(this.dealer);
                    session.setContent(Integer.toString(this.player.getBuyIn()));
                    session.setConversationId("session-join");
                    session.setReplyWith("session" + System.currentTimeMillis());

                    // Send message
                    myAgent.send(session);

                    // Prepare the template to get the reply
                    this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("session-join"),
                            MessageTemplate.MatchInReplyTo(session.getReplyWith()));
                    step++;
                    break;
                case 3: // Receive the joining session reply
                    reply = myAgent.receive(this.msgTemplate);
                    if (reply != null) {

                        // Successfully joined a session
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            this.player.setDealer(reply.getSender());
                            System.out.println(this.getAgent().getName() + " :: Successfully joined " +
                                    reply.getSender().getName() + " session.");
                            step++;
                        }
                        else {
                            // Reset player state
                            this.player.setPlayerState(Player.State.SEARCHING_SESSION);
                            this.player.addBehaviour(new SearchSessionServer(this.player, 1000));

                            System.out.println(this.getAgent().getName() + " :: Attempt failed: Could not join" +
                                    reply.getSender().getName() + " session.");
                        }
                    }
                    else {
                        block();
                    }
                    break;
                case 4:
                    this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                            MessageTemplate.MatchConversationId("session-start"));
                    ACLMessage msg = myAgent.receive(msgTemplate);
                    if(msg != null) {
                        System.out.println(this.player.getName() + " :: " + msg.getSender().getName() +
                                        " starting session.");
                        // Create reply
                        reply = msg.createReply();

                        reply.setPerformative(ACLMessage.CONFIRM);
                        reply.setContent("Session-start-confirmation");
                        myAgent.send(reply);

                        this.player.setPlayerState(Player.State.IN_SESSION);
                        this.player.addBehaviour(new Logic(this.player));
                        step++;
                    }
                    else {
                        block();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean done() {
        return this.step >= 5;
    }
}
