package net.datafaker.datafaker_gen;

import net.datafaker.Faker;
import net.datafaker.datafaker_gen.formats.Format;
import net.datafaker.datafaker_gen.sink.Sink;
import net.datafaker.shaded.snakeyaml.Yaml;
import net.datafaker.transformations.Field;
import net.datafaker.transformations.Schema;
import net.datafaker.transformations.Transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

public class DatafakerGen {

    public static void main(String[] args) {
        final Configuration conf = parseArg(args);
        final Map<String, Object> outputs;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(conf.getOutputConf()), StandardCharsets.UTF_8)) {
            outputs = new Yaml().loadAs(br, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Map<String, Object> formats = (Map<String, Object>) outputs.get("formats");
        final Map<String, Object> sinksFromConfig = (Map<String, Object>) outputs.get("sinks");
        final Locale defaultLocale;
        final List<Field> fields;
        final Faker faker;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(conf.getSchema()), StandardCharsets.UTF_8)) {
            final Map<String, Object> valuesMap = new Yaml().loadAs(br, Map.class);
            defaultLocale = Locale.forLanguageTag(
                    (String) Objects.requireNonNullElse(valuesMap.get("default_locale"), Locale.ENGLISH.toLanguageTag()));
            faker = new Faker(defaultLocale);
            final List<Object> list = (List<Object>) valuesMap.get("fields");
            fields = new ArrayList<>();
            for (Object o : list) {
                final Map<String, Object> stringObjectMap = (Map<String, Object>) o;
                final Field f = FieldFactory.getInstance().get(faker, stringObjectMap, defaultLocale);
                if (f != null) {
                    fields.add(f);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ServiceLoader<Format> fs = ServiceLoader.load(Format.class);
        final Map<String, Transformer<?, ?>> name2Transformer = new HashMap<>();
        for (Format<?> f : fs) {
            name2Transformer.put(
                    f.getName().toUpperCase(Locale.ROOT),
                    f.getTransformer((Map<String, String>) formats.get(f.getName()))
            );
        }

        final ServiceLoader<Sink> sinks = ServiceLoader.load(Sink.class);
        final Map<String, Sink> name2sink = new HashMap<>();
        for (Sink s : sinks) {
            name2sink.put(s.getName().toLowerCase(Locale.ROOT), s);
        }
        final String sinkName = conf.getSink().toLowerCase(Locale.ROOT);
        final Map<String, String> sinkConf = (Map<String, String>) sinksFromConfig.get(sinkName);
        final Sink sink = name2sink.get(sinkName);
        Objects.requireNonNull(sink,
                "Sink '" + conf.getSink() + "' is not available. The list of available sinks: " + name2sink.keySet());
        final Schema schema = Schema.of(fields.toArray(new Field[0]));
        sink.run(sinkConf,
                n -> findTransformerByName(conf.getFormat(), name2Transformer)
                        .generate(schema, n), conf.getNumberOfLines());
    }


    public static Configuration parseArg(String[] args) {
        final Configuration.ConfigurationBuilder builder = Configuration.builder();
        if (args == null || args.length == 0) {
            return builder.build();
        }
        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-n":
                    builder.numberOfLines(Integer.parseInt(args[i + 1]));
                    i++;
                    break;
                case "-s":
                    builder.schema(args[i + 1]);
                    i++;
                    break;
                case "-f":
                    builder.format(args[i + 1]);
                    i++;
                    break;
                case "-oc":
                    builder.outputConf(args[i + 1]);
                    i++;
                    break;
                case "-sink":
                    builder.sink(args[i + 1]);
                    i++;
                    break;
                default:
                    System.err.println("Unknown arg '" + args[i] + "'");
            }
        }
        return builder.build();
    }

    private static Transformer<?, ?> findTransformerByName(String formatName,
                                                           Map<String, Transformer<?, ?>> format2Transformer) {
        final String formatNameUpper = formatName.toUpperCase(Locale.ROOT);
        if (format2Transformer.containsKey(formatNameUpper)) {
            return format2Transformer.get(formatNameUpper);
        }

        var errorMessage = "'" + formatName + "'" + " is not supported yet. Available formats: ["
                + String.join(", ", format2Transformer.keySet()) + "]";
        throw new IllegalArgumentException(errorMessage);
    }
}
