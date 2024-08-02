package dev.matiaspg.annotationsmapping.mapping.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MapFrom {
    /**
     * Path from where to map the field.
     */
    String value();
}
