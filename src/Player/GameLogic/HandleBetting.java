package Player.GameLogic;



import Player.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleBetting extends Behaviour {

    private Player player;

    /**
     * Message template
     */
    private MessageTemplate msgTemplate;

    enum State {TURN_WAITING, BETTING}

    private State state = State.TURN_WAITING;

    HandleBetting(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        this.msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(msgTemplate);

        if (msg != null) {
            System.out.println(msg.getContent());

//            // Create reply
//            ACLMessage reply = msg.createReply();
//
//            reply.setPerformative(ACLMessage.CONFIRM);
//            reply.setContent("Card-reception-confirmation");
//
//            myAgent.send(reply);
//
//            System.out.println(this.player.getName() + " :: Received " + msg.getContent() +
//                    ". Send card reception confirmation.");
        }
        else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
