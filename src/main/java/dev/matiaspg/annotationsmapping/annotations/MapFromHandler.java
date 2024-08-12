package dev.matiaspg.annotationsmapping.annotations;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.TextNode;
import dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProvider;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingContext;
import dev.matiaspg.annotationsmapping.utils.annotations.types.MappingAnnotationHandler;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import static dev.matiaspg.annotationsmapping.annotations.MapFrom.NO_DEFAULT_VALUE;
import static dev.matiaspg.annotationsmapping.utils.annotations.MappingUtils.isBlankText;
import static dev.matiaspg.annotationsmapping.utils.annotations.MappingUtils.isNullOrMissing;

@Getter
@Component
@RequiredArgsConstructor
public class MapFromHandler implements MappingAnnotationHandler<MapFrom> {
    private final AnnotationsProvider annotationsProvider;

    @Override
    public Class<MapFrom> getSupportedAnnotation() {
        return MapFrom.class;
    }

    @Override
    public ValueGetter createValueGetter(
        Type type, MapFrom annotation, MappingContext ctx
    ) {
        // Cache things that will be reused
        ValueGetter valueGetter = createValueGetter((Class<?>) type, ctx);
        JsonPointer[] paths = Stream.of(annotation.value())
            .map(JsonPointer::compile)
            .toArray(JsonPointer[]::new);
        JsonNode defaultValue = createDefaultValue(annotation);

        return node -> {
            JsonNode valueNode = getValueNode(node, paths, defaultValue);

            // Don't do anything if the node was not found and there's no default
            if (valueNode.isMissingNode()) {
                return null;
            }

            return valueGetter.apply(valueNode);
        };
    }

    private ValueGetter createValueGetter(Class<?> type, MappingContext ctx) {
        return node -> ctx.recurse(node, type);
    }

    private JsonNode createDefaultValue(MapFrom annotation) {
        if (!NO_DEFAULT_VALUE.equals(annotation.defaultValue())) {
            // Return a default value if one was set
            return TextNode.valueOf(annotation.defaultValue());
        } else if (annotation.defaultEmptyString()) {
            // Return an empty string as default value
            return TextNode.valueOf(NO_DEFAULT_VALUE);
        }
        // Return a MissingNode so no mapping is done
        return MissingNode.getInstance();
    }

    private JsonNode getValueNode(
        JsonNode node,
        JsonPointer[] paths,
        JsonNode defaultValue
    ) {
        // Try to get a value node with each path
        for (JsonPointer path : paths) {
            JsonNode valueNode = node.at(path);
            // If a value node is found and is not empty, return it
            if (!isNullOrMissing(valueNode) && !isBlankText(valueNode)) {
                return valueNode;
            }
        }
        return defaultValue;
    }
}
