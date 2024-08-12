package dev.matiaspg.annotationsmapping.utils.annotations;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
    // TODO: DELETE (not used)
    private static final Map<Field, Method> FIELD_SETTER_CACHE =
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

    @Nullable
    // TODO: DELETE (not used)
    public static Method getSetter(Field field) {
        return FIELD_SETTER_CACHE.computeIfAbsent(field, f -> {
            final String fieldName = field.getName();
            final String setterName = "set"
                + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1);
            for (Method method : field.getDeclaringClass().getDeclaredMethods()) {
                // A setter is a method that:
                // - Matches the name of the field (field -> setField)
                // - Has only one parameter of the same type as the field
                // - Is public
                // - Returns void
                Parameter[] parameters = method.getParameters();
                if (setterName.equals(method.getName())
                    && parameters.length == 1
                    && parameters[0].getType().equals(field.getType())
                    && Modifier.isPublic(method.getModifiers())
                    && Void.TYPE == method.getReturnType()
                ) {
                    return method;
                }
            }
            return null;
        });
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

    /**
     * Internal utility class that sets members as accessible for an amount of
     * time before restoring them to being NOT accessible.
     * <p>
     * This is necessary because when doing operations in parallel, a thread
     * may restore a member to NOT accessible just before another thread needs
     * to use that member, which would lead to an {@link
     * IllegalAccessException}.
     * <p>
     * Note that this has a small performance overhead (20ms when mapping
     * ~11500 items on a Ryzen 5950X), which could be higher on other devices/
     * runtimes. If you need maximum performance, you could simply not call
     * {@code member.setAccessible(false)} and delete this class. However, I do
     * this to try to maintain the original values.
     */
    // TODO: DELETE (not necessary)
    private static class AccessibleScheduler {
        private static final Scheduler SCHEDULER = Schedulers.single();
        private static final Map<AccessibleObject, Disposable> SCHEDULES =
            new ConcurrentHashMap<>();

        /**
         * Sets a member as accessible for an amount of time before restoring
         * it to being NOT accessible.
         */
        private static void setAccessible(AccessibleObject member) {
            SCHEDULES.compute(member, (m, setNotAccessible) -> {
                if (setNotAccessible != null) {
                    // If a "restore" was scheduled, dispose it
                    setNotAccessible.dispose();
                } else {
                    // Otherwise, set as accessible so that it can be modified/invoked
                    member.setAccessible(true);
                }
                return setNotAccessible(member);
            });
        }

        private static Disposable setNotAccessible(AccessibleObject member) {
            final long MAX_DURATION = 1L;
            // Set as not accessible in 10ms instead of immediately to prevent
            // race conditions
            return SCHEDULER.schedule(() -> {
                member.setAccessible(false);
                SCHEDULES.remove(member);
            }, MAX_DURATION, TimeUnit.MILLISECONDS);
        }
    }
}
