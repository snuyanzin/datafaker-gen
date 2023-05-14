package net.datafaker.datafaker_gen.formats;

import net.datafaker.transformations.JsonTransformer;

import java.util.Map;

public class JsonFormat implements Format<Object> {
    @Override
    public String getName() {
        return "json";
    }

    @Override
    public <IN> JsonTransformer<IN> getTransformer(Map<String, String> config) {
        JsonTransformer.JsonTransformerBuilder<Object> builder = JsonTransformer.builder();
        if (config == null) {
            return (JsonTransformer<IN>) builder.build();
        }
        for (Map.Entry<String, String> entry : config.entrySet()) {
            switch (entry.getKey()) {
                case "formattedAs":
                    builder.formattedAs("[]".equals(entry.getValue()) ? JsonTransformer.JsonTransformerBuilder.FormattedAs.JSON_ARRAY : JsonTransformer.JsonTransformerBuilder.FormattedAs.JSON_OBJECT);
                    break;
                case "quote":
                    builder.withCommaBetweenObjects(Boolean.parseBoolean(entry.getValue()));
                    break;
            }
        }
        return (JsonTransformer<IN>) builder.build();
    }
}
