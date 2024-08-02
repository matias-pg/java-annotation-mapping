package dev.matiaspg.annotationsmapping.mapping;

import dev.matiaspg.annotationsmapping.mapping.handlers.MappingAnnotationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MappingAnnotationHandlers {
    private final Map<Class<?>, MappingAnnotationHandler<?>> handlersMap = new HashMap<>();

    @Autowired
    private void setHandlersMap(Collection<MappingAnnotationHandler<?>> handlers) {
        for (MappingAnnotationHandler<?> handler : handlers) {
            Class<?> annotation = handler.getSupportedAnnotation();

            // Ensure an annotation has only one handler
            if (handlersMap.containsKey(annotation)) {
                throw new IllegalStateException("The annotation " + annotation.getSimpleName() + " has more than one handler");
            }

            handlersMap.put(annotation, handler);
        }
    }

    public Optional<MappingAnnotationHandler<?>> getHandler(Class<?> annotation) {
        return Optional.ofNullable(handlersMap.get(annotation));
    }
}
