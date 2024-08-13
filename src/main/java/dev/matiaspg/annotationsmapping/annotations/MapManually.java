package dev.matiaspg.annotationsmapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation you can use on a method so that it will be called during the
 * mapping of the current object.
 * <p>
 * The method will be called with the JsonNode used to map the current object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MapManually {
}
