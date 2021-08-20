package com.backbase.oss.meta;

import java.util.Map;
import org.json.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.NonPrintableStyle;
import org.yaml.snakeyaml.Yaml;

public enum MetaFormat {
    JSON {
        @Override
        String convert(JSONObject json) {
            final String text = json.toString(4);

            return text;
        }
    },
    XML {
        @Override
        String convert(JSONObject json) {
            return org.json.XML.toString(json, "meta");
        }
    },
    YAML {
        @Override
        String convert(JSONObject json) {
            final Map<String, Object> jmap = json.toMap();
            final String yaml = new Yaml(OPTIONS).dump(jmap);

            return yaml;
        }
    },
    YML {
        @Override
        String convert(JSONObject json) {
            return YAML.convert(json);
        }
    },
    ;

    static final DumperOptions OPTIONS = new DumperOptions();

    static {
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        OPTIONS.setNonPrintableStyle(NonPrintableStyle.ESCAPE);
    }

    abstract String convert(JSONObject json);
}
