package Dataset;

import Player.Personality;
import Session.Card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class Dataset {
    private final static Path DATASET_DIRECTORY = Path.of("First-Project/datasets");
    private final static int COMMIT_SIZE = 1;

    private final Path datasetPath;
    private final LinkedList<String> data = new LinkedList<>();

    private int dataCount = 0;

    Dataset(String name) {
        datasetPath = DATASET_DIRECTORY.resolve(name + ".csv");
    }

    void commitDataset() {
        try {
            Files.write(datasetPath, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.clear();
    }

    void writeData(LinkedList<String> data) {
        if (dataCount++ >= COMMIT_SIZE)
            commitDataset();

        this.data.add(String.join(",", data));
    }

    void alternativeRound() {
        try {
            List<String> lines = Files.readAllLines(datasetPath);
            Files.write(DATASET_DIRECTORY.resolve("alt-round.csv"), lines.stream().map((String line) -> {
                String[] cards = line.split(",", 6);
                double ehs = Personality.effectiveHandStrength(1,
                        new ArrayList<>(List.of(new Card(cards[0]), new Card(cards[1]))),
                        new ArrayList<>(List.of(new Card(cards[2]), new Card(cards[3]), new Card(cards[4]))));
                System.out.println(ehs);
                return String.join(",", String.valueOf(ehs), cards[5]);
            }).collect(Collectors.toList()), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
