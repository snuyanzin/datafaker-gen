package net.datafaker.datafaker_gen;

import net.datafaker.transformations.Field;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaLoaderTest {

    @Test
    void generateJsonWith10LineAndFileOutput() throws IOException {
        String resourceName = "./schemas/config_test.yaml";
        URL resourceUrl = getClass().getClassLoader().getResource(resourceName);

        assert resourceUrl != null;
        Configuration configuration = DatafakerGen.parseArg(new String[]{"-f", "xml", "-n", "10", "-sink", "textfile", "-s", resourceUrl.getPath()});
        List<Field> fields = SchemaLoader.getFields(configuration);

        assertThat(fields).hasSize(1);
        assertThat(fields.get(0).getName()).isEqualTo("test");
    }
}