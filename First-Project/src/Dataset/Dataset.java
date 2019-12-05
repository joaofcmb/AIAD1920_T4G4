package Dataset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

class Dataset {
    private final static Path DATASET_DIRECTORY = Path.of("datasets");
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
}
