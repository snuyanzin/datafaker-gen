package net.datafaker.datafaker_gen;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DatafakerGenTest {

    @Test
    void generateJsonWith10LineAndFileOutput() throws IOException {

        DatafakerGen.main(new String[]{"-f", "xml", "-n", "10", "-sink", "textfile", "-oc", "output.yaml"});

        File tempFile = new File("res");
        List<String> lines = Files.readAllLines(Path.of(tempFile.getPath()));
        assertThat(lines).hasSize(10);

        // Clean up
        tempFile.delete();
    }

    @Test
    void parseArgWhenFormatJsonAndTenLinesAndSinkCliTest() {
        Configuration configuration = DatafakerGen.parseArg(new String[]{"-f", "json", "-n", "10", "-sink", "cli"});

        assertThat(configuration.getFormat()).isEqualTo("json");
        assertThat(configuration.getNumberOfLines()).isEqualTo(10);
        assertThat(configuration.getSink()).isEqualTo("cli");
        assertThat(configuration.getSchema()).isEqualTo("config.yaml");
        assertThat(configuration.getOutputConf()).isEqualTo("output.yaml");
    }
}