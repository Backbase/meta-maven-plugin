package com.backbase.oss.meta;

import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.NonPrintableStyle;

public enum MetaFormat {
    JSON {
        @Override
        String convert(String xml) {
            final String json = org.json.XML.toJSONObject(xml).toString(4);

            return json;
        }
    },
    XML {
        @Override
        String convert(String xml) {
            return xml;
        }
    },
    YAML {
        @Override
        String convert(String xml) {
            final Map<String, Object> jmap = org.json.XML.toJSONObject(xml).toMap();
            final String yaml = new Yaml(OPTIONS).dump(jmap);

            return yaml;
        }

    },
    ;

    static final DumperOptions OPTIONS = new DumperOptions();

    static {
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        OPTIONS.setNonPrintableStyle(NonPrintableStyle.ESCAPE);
    }

    abstract String convert(String xml);
}
