package dev.matiaspg.annotationsmapping.utils.annotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * Provides instances of annotations applied to classes, fields, methods, and
 * method parameters.
 * <p>
 * It's main purpose is to provide annotation instances whose values may be
 * replaced by configuration, without having to modify the code.
 */
@Component
public class AnnotationsProvider {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final AnnotationReplacements[] annotationReplacements;

    // TODO: See a way to improve this injection, maybe by creating a bean or exploring
    //  other options (e.g. how will this work with YAML or Helm Charts?)
    @Autowired
    public AnnotationsProvider(
        @Value("${dev.matiaspg.mapping.annotationReplacements:[]}") String replacements
    ) throws JsonProcessingException {
        this.annotationReplacements = mapper.readValue(replacements, AnnotationReplacements[].class);
    }

    public <T extends Annotation> T getAnnotation(
        Class<?> targetClass,
        Class<T> annotationClass
    ) {
        T annotation = targetClass.getAnnotation(annotationClass);
        if (annotation == null) {
            return null;
        }
        // Check if there are any replacements for the annotation
        for (AnnotationReplacements r : annotationReplacements) {
            if (annotationClass.equals(r.annotationClass()) && r.isFor(targetClass)) {
                // If there are, then use a proxy to use the replacements
                return AnnotationProxy.newInstance(annotation, r.replacements());
            }
        }
        // If there are no replacements, return the annotation as is
        return annotation;
    }

    public <T extends Annotation> T getAnnotation(
        Field field,
        Class<T> annotationClass
    ) {
        T annotation = field.getAnnotation(annotationClass);
        if (annotation == null) {
            return null;
        }
        // Check if there are any replacements for the annotation
        for (AnnotationReplacements r : annotationReplacements) {
            if (annotationClass.equals(r.annotationClass()) && r.isFor(field)) {
                // If there are, then use a proxy to use the replacements
                return AnnotationProxy.newInstance(annotation, r.replacements());
            }
        }
        // If there are no replacements, return the annotation as is
        return annotation;
    }

    public <T extends Annotation> T getAnnotation(
        Method method,
        Class<T> annotationClass
    ) {
        T annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            return null;
        }
        // Check if there are any replacements for the annotation
        for (AnnotationReplacements r : annotationReplacements) {
            if (annotationClass.equals(r.annotationClass()) && r.isFor(method)) {
                // If there are, then use a proxy to use the replacements
                return AnnotationProxy.newInstance(annotation, r.replacements());
            }
        }
        // If there are no replacements, return the annotation as is
        return annotation;
    }

    public <T extends Annotation> T getAnnotation(
        Parameter parameter,
        Class<T> annotationClass
    ) {
        T annotation = parameter.getAnnotation(annotationClass);
        if (annotation == null) {
            return null;
        }
        // Check if there are any replacements for the annotation
        for (AnnotationReplacements r : annotationReplacements) {
            if (annotationClass.equals(r.annotationClass()) && r.isFor(parameter)) {
                // If there are, then use a proxy to use the replacements
                return AnnotationProxy.newInstance(annotation, r.replacements());
            }
        }
        // If there are no replacements, return the annotation as is
        return annotation;
    }

    public record AnnotationReplacements(
        Class<?> targetClass,
        String targetField,
        String targetMethod,
        String targetMethodParam,
        Class<?> annotationClass,
        ObjectNode replacements
    ) {
        boolean isFor(Class<?> clazz) {
            return clazz.equals(this.targetClass);
        }

        boolean isFor(Field field) {
            return isFor(field.getDeclaringClass())
                && field.getName().equals(targetField);
        }

        boolean isFor(Executable method) {
            return isFor(method.getDeclaringClass())
                && method.getName().equals(targetMethod);
        }

        boolean isFor(Parameter parameter) {
            return isFor(parameter.getDeclaringExecutable())
                && parameter.getName().equals(targetMethodParam);
        }
    }

    public record AnnotationProxy<A extends Annotation>(
        A obj,
        ObjectNode replacements
    ) implements InvocationHandler {
        @SuppressWarnings("unchecked")
        public static <A extends Annotation> A newInstance(
            A obj, ObjectNode replacements
        ) {
            return (A) Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new AnnotationProxy<>(obj, replacements));
        }

        public Object invoke(Object proxy, Method method, Object[] args)
            throws InvocationTargetException, IllegalAccessException {
            // If the method value has a replacement
            if (replacements.has(method.getName())) {
                // Transform the replacement value to the method's return type
                return new ObjectMapper().convertValue(
                    replacements.path(method.getName()), method.getReturnType());
            }
            // Otherwise, call the original method
            return method.invoke(obj, args);
        }
    }
}
