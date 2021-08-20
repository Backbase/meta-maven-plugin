package com.backbase.oss.meta;

import java.util.Objects;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.atteo.evo.inflector.English;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.json.JSONObject;

final class MetaConverter {
    private final ExpressionEvaluator expEval;
    private boolean prettyArrays;

    MetaConverter(MavenSession session, MojoExecution execution) {
        this(new PluginParameterExpressionEvaluator(session, execution));
    }

    MetaConverter(ExpressionEvaluator eval) {
        this.expEval = eval;
    }

    MetaConverter prettyArrays(boolean prettyArrays) {
        this.prettyArrays = prettyArrays;

        return this;
    }

    JSONObject toJson(PlexusConfiguration config) throws MojoExecutionException {
        final JSONObject json = new JSONObject();

        for (final PlexusConfiguration child : config.getChildren()) {
            toJson(json, child);
        }

        return json;
    }

    private void toJson(JSONObject json, PlexusConfiguration config) throws MojoExecutionException {
        final String name = config.getName();
        final PlexusConfiguration[] children = config.getChildren();

        if (children.length > 0) {
            toJson(json, name, children);
        } else {
            json.accumulate(name, evaluate(config.getValue()));
        }
    }

    private void toJson(JSONObject json, String name, PlexusConfiguration[] children) throws MojoExecutionException {
        final JSONObject cjson = new JSONObject();

        for (final PlexusConfiguration child : children) {
            toJson(cjson, child);
        }

        if (this.prettyArrays && cjson.length() == 1) {
            final String cname = cjson.keys().next();

            if (name.equals(English.plural(cname))) {
                json.put(name, cjson.get(cname));

                return;
            }
        }

        json.accumulate(name, cjson);
    }

    private String evaluate(String value) throws MojoExecutionException {
        try {
            return Objects.toString(this.expEval.evaluate(value), null);
        } catch (final ExpressionEvaluationException e) {
            throw new MojoExecutionException(value, e);
        }
    }
}
