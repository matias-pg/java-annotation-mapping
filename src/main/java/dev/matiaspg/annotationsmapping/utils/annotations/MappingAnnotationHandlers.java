package dev.matiaspg.annotationsmapping.utils.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class MappingAnnotationHandlers {
    private final Map<Class<?>, MappingAnnotationHandler<?>> handlersMap = new HashMap<>();

    @Autowired
    private void setHandlersMap(Collection<MappingAnnotationHandler<?>> handlers) {
        for (MappingAnnotationHandler<?> handler : handlers) {
            Class<?> annotation = handler.getSupportedAnnotation();

            // Ensure an annotation has only one handler
            if (handlersMap.containsKey(annotation)) {
                throw new IllegalStateException("The annotation "
                    + annotation.getSimpleName() + " has more than one handler");
            }

            handlersMap.put(annotation, handler);
        }
    }

    /**
     * Gets only handlers for annotations we care, ignoring the rest,
     * e.g. @Nonnull or @Nullable
     *
     * @param annotations The annotations to which a handler will be get
     * @return A stream of handlers
     */
    public Stream<? extends MappingAnnotationHandler<?>> getHandlers(Annotation[] annotations) {
        return Stream.of(annotations)
            .map(Annotation::annotationType)
            .map(handlersMap::get)
            .filter(Objects::nonNull);
    }
}
