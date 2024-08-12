package dev.matiaspg.annotationsmapping.annotations;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProvider;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingContext;
import dev.matiaspg.annotationsmapping.utils.annotations.ReflectionUtils;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ItemFilter;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ItemFilter.AllowAllItems;
import dev.matiaspg.annotationsmapping.utils.annotations.types.MappingAnnotationHandler;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Getter
@Component
@RequiredArgsConstructor
public class MapEachFromHandler implements MappingAnnotationHandler<MapEachFrom> {
    private final AnnotationsProvider annotationsProvider;

    @Override
    public Class<MapEachFrom> getSupportedAnnotation() {
        return MapEachFrom.class;
    }

    @Override
    public ValueGetter createValueGetter(
        Type type, MapEachFrom annotation, MappingContext ctx
    ) {
        // Cache things that will be reused
        Class<?> collectionType = ReflectionUtils.getRawType(type);
        Class<?> itemType = ReflectionUtils.getItemType(type);
        Function<Stream<?>, Object> collector = createCollector(collectionType);
        ItemFilter filter = getItemFilter(annotation);
        JsonPointer path = JsonPointer.compile(annotation.value());

        return node -> {
            JsonNode iterableNode = node.at(path);

            // Don't do anything if the node was not found
            if (iterableNode.isMissingNode()) {
                return null;
            }

            Stream<JsonNode> stream = StreamSupport
                .stream(iterableNode.spliterator(), annotation.parallel());
            if (filter != null) {
                stream = stream.filter(filter);
            }

            return collector.apply(stream
                // Map each item recursively
                .map(itemNode -> ctx.recurse(itemNode, itemType)));
        };
    }

    private Function<Stream<?>, Object> createCollector(Class<?> type) {
        if (!type.isArray() && !Iterable.class.isAssignableFrom(type)) {
            throw new IllegalStateException(
                getAnnotationName() + " can only be used on arrays or collections");
        }

        if (type.isArray()) {
            return stream -> stream.toArray(size -> (Object[])
                Array.newInstance(type.getComponentType(), size));
        } else if (Set.class.isAssignableFrom(type)) {
            return stream -> stream.collect(Collectors.toSet());
        }
        return stream -> stream.collect(Collectors.toList());
    }

    private ItemFilter getItemFilter(MapEachFrom annotation) {
        if (AllowAllItems.class.equals(annotation.itemFilter())) {
            return null;
        }
        return ReflectionUtils.newInstance(
            annotation.itemFilter(),
            (Object[]) annotation.itemFilterArgs()
        );
    }
}
