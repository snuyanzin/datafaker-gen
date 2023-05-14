package net.datafaker.datafaker_gen.formats;

import net.datafaker.transformations.sql.SqlTransformer;

import java.util.Map;

public class SqlFormat implements Format<CharSequence> {
    @Override
    public String getName() {
        return "sql";
    }

    @Override
    public <IN> SqlTransformer<IN> getTransformer(Map<String, String> config) {
        SqlTransformer.SqlTransformerBuilder<Object> builder = SqlTransformer.builder();
        if (config == null) {
            return (SqlTransformer<IN>) builder.build();
        }
        for (Map.Entry<String, String> entry : config.entrySet()) {
            switch (entry.getKey()) {
                case "tableName":
                    builder.tableName(entry.getValue());
                    break;
                case "quote":
                    builder.quote(entry.getValue().charAt(0));
                    break;
                case "schemaName":
                    builder.schemaName(entry.getValue());
                    break;
                case "sqlIdentifierQuote":
                    builder.sqlQuoteIdentifier(entry.getValue());
                    break;
                case "batch":
                    if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                        builder.batch();
                    } else {
                        builder.batch(Integer.parseInt(entry.getValue()));
                    }
                    break;
            }
        }
        return (SqlTransformer<IN>) builder.build();
    }
}
