package net.datafaker.datafaker_gen.sink;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

public class TextFileSink implements Sink {
    @Override
    public String getName() {
        return "textfile";
    }

    @Override
    public void run(Map<String, String> config, Function<Integer, ?> function, int numberOfLines) {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(config.get("filename")))) {
            int numberOfLinesToPrint = numberOfLines;
            int batchSize = getBatchSize(config);
            while (numberOfLinesToPrint > 0) {
                int numberOfLinesToPrintCurrentIteration = Math.min(batchSize, numberOfLinesToPrint);
                bw.write((String) function.apply(numberOfLinesToPrintCurrentIteration));
                numberOfLinesToPrint -= numberOfLinesToPrintCurrentIteration;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
