package dev.matiaspg.annotationsmapping.mapping.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConcatMapFrom {
    /**
     * Delimiter used to join the values that were found.
     */
    String delimiter() default "";

    /**
     * Paths from where to map the field.
     */
    String[] paths();
}
