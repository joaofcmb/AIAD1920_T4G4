package Player.GameLogic;

import Player.Player;
import Session.Card;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Flop extends Behaviour {

    Player player;

    MessageTemplate msgTemplate;

    boolean status = false;

    enum State {RECEIVING_TABLE_CARDS, BETTING}

    State state = State.RECEIVING_TABLE_CARDS;

    Flop(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        switch (state) {
            case RECEIVING_TABLE_CARDS:
                this.msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                        MessageTemplate.MatchConversationId("flop-table-cards"));
                ACLMessage msg = myAgent.receive(msgTemplate);
                if(msg != null) {
                    System.out.println(this.player.getName() + " :: Received table initial configuration: " +
                            msg.getContent());

                    String[] content = msg.getContent().split(":");

                    for(int i = 0; i < 3; i++) {
                        String[] card = content[i].split("-");
                        this.player.getTable().add(new Card(card[1], card[0]));
                    }

                    this.state = State.BETTING;
                }
                else {
                    block();
                }
                break;
            case BETTING:
                break;
        }
    }

    void terminate() {
        status = true;
    }

    @Override
    public boolean done() {
        return status;
    }
}
