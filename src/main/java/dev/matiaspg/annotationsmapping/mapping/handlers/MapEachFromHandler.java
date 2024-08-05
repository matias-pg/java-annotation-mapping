package dev.matiaspg.annotationsmapping.mapping.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.mapping.MappingContext;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapEachFrom;
import dev.matiaspg.annotationsmapping.utils.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class MapEachFromHandler implements MappingAnnotationHandler<MapEachFrom> {
    @Override
    public Class<MapEachFrom> getSupportedAnnotation() {
        return MapEachFrom.class;
    }

    @Override
    public Function<JsonNode, Optional<Object>> createValueGetter(
        Type type, MapEachFrom annotation, MappingContext<?> ctx) {
        Class<?> collectionType = ReflectionUtils.getRawType(type);
        ensureCollection(collectionType);
        Class<?> itemType = getItemType(type);

        return node -> {
            JsonNode iterableNode = node.at(annotation.value());

            // Don't do anything if the node was not found
            if (iterableNode.isMissingNode()) {
                return Optional.empty();
            }

            // Map each item recursively
            Stream<?> stream = StreamSupport
                .stream(iterableNode.spliterator(), annotation.parallel())
                .map(itemNode -> ctx.recursive().apply(itemNode, itemType));

            return Optional.of(collect(stream, collectionType));
        };
    }

    private void ensureCollection(Class<?> type) {
        if (!type.isArray() && !Collection.class.isAssignableFrom(type)) {
            throw new IllegalStateException(
                getAnnotationName() + " can only be used on arrays or collections");
        }
    }

    private Class<?> getItemType(Type type) {
        Class<?> itemType = ReflectionUtils.getItemType(type).orElse(Object.class);

        if (itemType.equals(Object.class)) {
            throw new IllegalArgumentException(
                "Unable to get the item type to use with " + getAnnotationName()
                    + " (Object is not supported)");
        }

        return itemType;
    }

    private Object collect(Stream<?> stream, Class<?> collectionType) {
        if (collectionType.isArray()) {
            return stream.toArray(size -> (Object[])
                Array.newInstance(collectionType.getComponentType(), size));
        } else if (Set.class.isAssignableFrom(collectionType)) {
            return stream.collect(Collectors.toSet());
        } else {
            return stream.collect(Collectors.toList());
        }
    }
}
