package dev.matiaspg.annotationsmapping.mapping.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MapAnyOf {
    /**
     * Paths from where to map the field.
     * <p>
     * Only the first path found will be used.
     */
    String[] value();
}
