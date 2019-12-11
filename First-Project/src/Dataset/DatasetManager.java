package Dataset;

import Player.NonReactivePersonality;
import Session.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatasetManager {
    private static final Dataset PERSONALITY_DATASET = new Dataset("personality");
    private static final Dataset ROUND_DATASET = new Dataset("round");

    private static final Function<String, Double> SELECTION_FROM_PRESET = preset ->
            NonReactivePersonality.presetSelection(preset.split("@")[0]);
    private static final Function<String, Double> AGGRESSION_FROM_PRESET = preset ->
            NonReactivePersonality.presetAggression(preset.split("@")[0]);

    private static final int MOVES_SIZE = 30;
    private static final HashMap<String, LinkedList<String>> playerMoves = new HashMap<>();

    private static boolean roundFlag = false;
    private static ArrayList<Card> hand;
    private static ArrayList<Card> table;
    private static Integer chips;
    private static LinkedList<Double> opponentSelection;
    private static LinkedList<Double> opponentAggression;


    public static void main(String[] args) {
        ROUND_DATASET.alternativeRound();
    }

    public static void processMove(String player, String move) {
        playerMoves.compute(player, (k, v) -> {
            if (v == null || v.size() > MOVES_SIZE)
                return new LinkedList<>(Collections.singleton(move));
            else {
                v.add(move);
                return v;
            }
        });

        final LinkedList<String> moves = playerMoves.get(player);
        if (moves.size() == MOVES_SIZE)
            writePersonalityData(moves, SELECTION_FROM_PRESET.apply(player), AGGRESSION_FROM_PRESET.apply(player));
    }

    public static void processRound(ArrayList<Card> hand, ArrayList<Card> table,
                                    Set<String> activePlayers, Integer chips) {
        if (roundFlag && table.isEmpty()) {
            roundFlag = false;
            writeRoundData(DatasetManager.hand, DatasetManager.table,
                    opponentSelection.stream(), opponentAggression.stream(),
                    chips > DatasetManager.chips
            );
        }
        else if (!roundFlag && table.size() == 3 && !activePlayers.isEmpty()) {
            roundFlag = true;
            DatasetManager.hand = new ArrayList<>(hand);
            DatasetManager.table = new ArrayList<>(table);
            DatasetManager.chips = chips;
            opponentSelection = activePlayers.stream().map(SELECTION_FROM_PRESET)
                    .collect(Collectors.toCollection(LinkedList::new));
            opponentAggression = activePlayers.stream().map(AGGRESSION_FROM_PRESET)
                    .collect(Collectors.toCollection(LinkedList::new));
        }
    }

    private static void writePersonalityData(LinkedList<String> moves, double handSelection, double aggression) {
        LinkedList<String> data = new LinkedList<>(moves);
        data.add(String.valueOf(handSelection));
        data.add(String.valueOf(aggression));

        PERSONALITY_DATASET.writeData(data);
    }

    private static void writeRoundData(ArrayList<Card> hand, ArrayList<Card> table,
                                       Stream<Double> opponentSelection, Stream<Double> opponentAggression,
                                       boolean result) {
        LinkedList<String> data = hand.stream().map(Card::toString).collect(Collectors.toCollection(LinkedList::new));
        data.addAll(table.stream().map(Card::toString).collect(Collectors.toList()));
        data.addAll(opponentSelection.map(String::valueOf).collect(Collectors.toList()));
        data.addAll(opponentAggression.map(String::valueOf).collect(Collectors.toList()));
        data.add(String.valueOf(result));

        ROUND_DATASET.writeData(data);
    }
}
