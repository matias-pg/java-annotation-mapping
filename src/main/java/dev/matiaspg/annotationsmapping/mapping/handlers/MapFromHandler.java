package dev.matiaspg.annotationsmapping.mapping.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import dev.matiaspg.annotationsmapping.mapping.MappingContext;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapFrom;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static dev.matiaspg.annotationsmapping.mapping.annotations.MapFrom.NO_DEFAULT_VALUE;

@Component
public class MapFromHandler implements MappingAnnotationHandler<MapFrom> {
    @Override
    public Class<MapFrom> getSupportedAnnotation() {
        return MapFrom.class;
    }

    @Override
    public Function<JsonNode, Optional<Object>> createValueGetter(
        Type type, MapFrom annotation, MappingContext<?> ctx) {
        return node -> {
            JsonNode valueNode = getValueNode(node, annotation);

            // Don't do anything if the node was not found and there's no default
            if (valueNode.isMissingNode()) {
                return Optional.empty();
            }

            return getValue(valueNode, (Class<?>) type)
                .or(() -> Optional.ofNullable(
                    // TODO: Prevent deserializing data before executing mappers.
                    //  Test it with RedditPost#images and Image#source
                    ctx.mapper().convertValue(valueNode, (Class<?>) type)));
        };
    }

    // TODO: Improve this! There are better ways to do the same
    private static Optional<Object> getValue(JsonNode node, Class<?> type) {
        Map<Class<?>, Function<JsonNode, Object>> map = Map.of(
            String.class, JsonNode::asText,
            Boolean.class, JsonNode::asBoolean,
            Long.class, JsonNode::asLong,
            Integer.class, JsonNode::asInt,
            Double.class, JsonNode::asDouble
        );
        return Optional.ofNullable(map.get(type))
            .map(getter -> getter.apply(node));
    }

    private JsonNode getValueNode(JsonNode node, MapFrom annotation) {
        JsonNode valueNode = node.at(annotation.value());

        if ((valueNode.isMissingNode() || valueNode.isNull())
            && (!NO_DEFAULT_VALUE.equals(annotation.defaultValue())
            || annotation.allowDefaultEmptyString())
        ) {
            return TextNode.valueOf(annotation.defaultValue());
        }

        return valueNode;
    }
}
