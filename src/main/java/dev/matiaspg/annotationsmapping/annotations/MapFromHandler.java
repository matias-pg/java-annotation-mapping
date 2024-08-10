package dev.matiaspg.annotationsmapping.annotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.TextNode;
import dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProvider;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingAnnotationHandler;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingContext;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingUtils;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Optional;

import static dev.matiaspg.annotationsmapping.annotations.MapFrom.NO_DEFAULT_VALUE;

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
        ValueGetter valueGetter = createValueGetter((Class<?>) type, ctx);

        return node -> {
            JsonNode valueNode = getValueNode(node, annotation);

            // Don't do anything if the node was not found and there's no default
            if (valueNode.isMissingNode()) {
                return Optional.empty();
            }

            return valueGetter.apply(valueNode);
        };
    }

    private ValueGetter createValueGetter(Class<?> type, MappingContext ctx) {
        return node -> Optional.ofNullable(ctx.recurse(node, type));
    }

    private JsonNode getValueNode(JsonNode node, MapFrom annotation) {
        // Try to get a value node with each path
        for (String path : annotation.value()) {
            Optional<JsonNode> valueNode = MappingUtils.getValueNode(node, path);
            // If a value node is found, return it
            if (valueNode.isPresent()) {
                return valueNode.get();
            }
        }
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
}
