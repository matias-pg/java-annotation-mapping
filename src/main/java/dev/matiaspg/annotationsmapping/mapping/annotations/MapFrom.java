package dev.matiaspg.annotationsmapping.mapping.annotations;

import com.fasterxml.jackson.databind.node.TextNode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MapFrom {
    String NO_DEFAULT_VALUE = "";

    /**
     * Path from where to map the field.
     */
    String value();

    /**
     * Default value to use when the node is null or missing.
     * <p>
     * It will be passed to {@link TextNode#valueOf(String)} in such case.
     */
    String defaultValue() default NO_DEFAULT_VALUE;

    /**
     * Since an empty string is used to indicate that you don't want to have a
     * default value, by setting this to true you will be able to use an empty
     * string as a default value.
     */
    boolean allowDefaultEmptyString() default false;
}
