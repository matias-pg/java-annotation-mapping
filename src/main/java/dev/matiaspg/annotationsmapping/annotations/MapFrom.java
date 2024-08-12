package dev.matiaspg.annotationsmapping.annotations;

import com.fasterxml.jackson.databind.node.TextNode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MapFrom {
    String NO_DEFAULT_VALUE = "";

    /**
     * Paths from where to map the field.
     * <p>
     * If nothing is found with a path, the next one will be used.
     */
    String[] value();

    /**
     * Default value to use when all paths were null or missing.
     * <p>
     * Its value will be passed to {@link TextNode#valueOf(String)}.
     */
    String defaultValue() default NO_DEFAULT_VALUE;

    /**
     * Whether you want to use an empty string as a default value.
     *
     * @implNote Since an empty string is used to indicate that you don't want
     * a default value, by setting this to true an empty string will be used as
     * a default value.
     */
    boolean defaultEmptyString() default false;
}
