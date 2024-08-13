package dev.matiaspg.annotationsmapping.utils.annotations;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * For some edge cases see:
 * <li> {@link com.fasterxml.jackson.databind.util.ClassUtil}
 * <li> {@link org.springframework.util.ClassUtils}
 * <li> {@link org.springframework.util.ReflectionUtils}
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReflectionUtils {
    private static final Map<Class<?>, Class<?>[]> CONSTRUCTOR_TYPES_CACHE =
        new ConcurrentHashMap<>();

    public static Field[] getClassFields(Class<?> clazz) {
        // To support inheritance
        Stream<Field> parentFields = clazz.getSuperclass() == null
            ? Stream.empty()
            : Stream.of(getClassFields(clazz.getSuperclass()));
        Stream<Field> classFields = Stream.of(clazz.getDeclaredFields());
        // Return an array with fields from the class and its ancestors
        return Stream.concat(parentFields, classFields).toArray(Field[]::new);
    }

    public static Method[] getClassMethods(Class<?> clazz) {
        // To support inheritance
        Stream<Method> parentMethods = clazz.getSuperclass() == null
            ? Stream.empty()
            : Stream.of(getClassMethods(clazz.getSuperclass()));
        Stream<Method> classMethods = Stream.of(clazz.getDeclaredMethods());
        // Return an array with methods from the class and its ancestors
        return Stream.concat(parentMethods, classMethods).toArray(Method[]::new);
    }

    @SneakyThrows({
        NoSuchMethodException.class,
        InvocationTargetException.class,
        InstantiationException.class,
        IllegalAccessException.class,
    })
    public static <T> T newInstance(Class<T> targetClass, Object... args) {
        Class<?>[] types = CONSTRUCTOR_TYPES_CACHE
            .computeIfAbsent(targetClass,
                t -> Stream.of(args)
                    .map(arg -> arg == null ? null : arg.getClass())
                    .toArray(Class<?>[]::new));
        return targetClass.getDeclaredConstructor(types).newInstance(args);
    }

    @SneakyThrows(IllegalAccessException.class)
    public static void setFieldValue(Object object, Field field, Object value) {
        // Set as accessible so that it can be modified
        // It is not necessary to call setAccessible(false), since only this
        // instance is set as accessible, not future instances of this field
        field.setAccessible(true);
        field.set(object, value);
    }

    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    public static void invokeMethod(Object object, Method method, Object... args) {
        // Set as accessible so that it can be invoked
        // It is not necessary to call setAccessible(false), since only this
        // instance is set as accessible, not future instances of this method
        method.setAccessible(true);
        method.invoke(object, args);
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            return toClass(parameterizedType.getRawType());
        }
        if (type instanceof Class<?> clazz) {
            return clazz;
        }
        if (type instanceof GenericArrayType genericArrayType) {
            // TODO: Check if it works, I haven't tested generic arrays
            return toClass(genericArrayType.getGenericComponentType());
        }
        throw new UnsupportedOperationException("Unable to get raw type from " + type);
    }

    public static Class<?> getItemType(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            return toClass(parameterizedType.getActualTypeArguments()[0]);
        }
        if (type instanceof Class<?> clazz && clazz.isArray()) {
            return clazz.getComponentType();
        }
        if (type instanceof GenericArrayType genericArrayType) {
            // TODO: Check if it works, I haven't tested generic arrays
            return toClass(genericArrayType.getGenericComponentType());
        }
        throw new UnsupportedOperationException(
            "Unable to get item type from " + type);
    }

    @SneakyThrows(ClassNotFoundException.class)
    private static Class<?> toClass(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz;
        }
        return Class.forName(type.getTypeName());
    }
}
