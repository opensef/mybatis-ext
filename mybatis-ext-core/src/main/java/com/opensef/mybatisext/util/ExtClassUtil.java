package com.opensef.mybatisext.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ExtClassUtil {

    /**
     * Maps names of primitives to their corresponding primitive {@code Class}es.
     */
    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<>();

    static {
        namePrimitiveMap.put("boolean", Boolean.TYPE);
        namePrimitiveMap.put("byte", Byte.TYPE);
        namePrimitiveMap.put("char", Character.TYPE);
        namePrimitiveMap.put("short", Short.TYPE);
        namePrimitiveMap.put("int", Integer.TYPE);
        namePrimitiveMap.put("long", Long.TYPE);
        namePrimitiveMap.put("double", Double.TYPE);
        namePrimitiveMap.put("float", Float.TYPE);
        namePrimitiveMap.put("void", Void.TYPE);
    }

    /**
     * Maps a primitive class name to its corresponding abbreviation used in array class names.
     */
    private static final Map<String, String> abbreviationMap;

    static {
        final Map<String, String> m = new HashMap<>();
        m.put("int", "I");
        m.put("boolean", "Z");
        m.put("float", "F");
        m.put("long", "J");
        m.put("short", "S");
        m.put("byte", "B");
        m.put("double", "D");
        m.put("char", "C");

        abbreviationMap = Collections.unmodifiableMap(m);
    }

    /**
     * The package separator character: {@code '&#x2e;' == {@value}}.
     */
    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    /**
     * The inner class separator character: {@code '$' == {@value}}.
     */
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    /**
     * Returns the (initialized) class represented by {@code className}
     * using the current thread's context class loader. This implementation
     * supports the syntaxes "{@code java.util.Map.Entry[]}",
     * "{@code java.util.Map$Entry[]}", "{@code [Ljava.util.Map.Entry;}",
     * and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param className the class name
     * @return the class represented by {@code className} using the current thread's context class loader
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(final String className) throws ClassNotFoundException {
        return getClass(className, true);
    }

    /**
     * Returns the class represented by {@code className} using the
     * current thread's context class loader. This implementation supports the
     * syntaxes "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}",
     * "{@code [Ljava.util.Map.Entry;}", and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param className  the class name
     * @param initialize whether the class must be initialized
     * @return the class represented by {@code className} using the current thread's context class loader
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(final String className, final boolean initialize) throws ClassNotFoundException {
        final ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        final ClassLoader loader = contextCL == null ? ExtClassUtil.class.getClassLoader() : contextCL;
        return getClass(loader, className, initialize);
    }

    /**
     * Returns the class represented by {@code className} using the
     * {@code classLoader}.  This implementation supports the syntaxes
     * "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}",
     * "{@code [Ljava.util.Map.Entry;}", and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param classLoader the class loader to use to load the class
     * @param className   the class name
     * @param initialize  whether the class must be initialized
     * @return the class represented by {@code className} using the {@code classLoader}
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(final ClassLoader classLoader, final String className, final boolean initialize) throws ClassNotFoundException {
        try {
            final Class<?> clazz;
            if (namePrimitiveMap.containsKey(className)) {
                clazz = namePrimitiveMap.get(className);
            } else {
                clazz = Class.forName(toCanonicalName(className), initialize, classLoader);
            }
            return clazz;
        } catch (final ClassNotFoundException ex) {
            // allow path separators (.) as inner class name separators
            final int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);

            if (lastDotIndex != -1) {
                try {
                    return getClass(classLoader, className.substring(0, lastDotIndex) +
                                    INNER_CLASS_SEPARATOR_CHAR + className.substring(lastDotIndex + 1),
                            initialize);
                } catch (final ClassNotFoundException ex2) { // NOPMD
                    // ignore exception
                }
            }

            throw ex;
        }
    }

    /**
     * Converts a class name to a JLS style class name.
     *
     * @param className the class name
     * @return the converted name
     */
    private static String toCanonicalName(String className) {
        className = ExtStringUtil.deleteWhitespace(className);
        Validate.notNull(className, "className");
        if (className.endsWith("[]")) {
            final StringBuilder classNameBuffer = new StringBuilder();
            while (className.endsWith("[]")) {
                className = className.substring(0, className.length() - 2);
                classNameBuffer.append("[");
            }
            final String abbreviation = abbreviationMap.get(className);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(className).append(";");
            }
            className = classNameBuffer.toString();
        }
        return className;
    }

    /**
     * Gets all fields of the given class and its parents (if any).
     *
     * @param cls the {@link Class} to query
     * @return an array of Fields (possibly empty).
     * @throws IllegalArgumentException if the class is {@code null}
     * @since 3.2
     */
    public static Field[] getAllFields(final Class<?> cls) {
        final List<Field> allFieldsList = getAllFieldsList(cls);
        return allFieldsList.toArray(new Field[0]);
    }

    /**
     * Gets all fields of the given class and its parents (if any).
     *
     * @param cls the {@link Class} to query
     * @return an List of Fields (possibly empty).
     * @throws IllegalArgumentException if the class is {@code null}
     * @since 3.2
     */
    public static List<Field> getAllFieldsList(final Class<?> cls) {
        Validate.notNull(cls, "cls");
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    /**
     * Gets an accessible {@link Field} by name, breaking scope if requested. Superclasses/interfaces will be
     * considered.
     *
     * @param cls         the {@link Class} to reflect, must not be {@code null}
     * @param fieldName   the field name to obtain
     * @param forceAccess whether to break scope restrictions using the
     *                    {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} method. {@code false} will only
     *                    match {@code public} fields.
     * @return the Field object
     * @throws NullPointerException     if the class is {@code null}
     * @throws IllegalArgumentException if the field name is blank or empty or is matched at multiple places
     *                                  in the inheritance hierarchy
     */
    public static Field getField(final Class<?> cls, final String fieldName, final boolean forceAccess) {
        Validate.notNull(cls, "cls");
        Validate.isTrue(ExtStringUtil.hasText(fieldName), "The field name must not be blank/empty");

        // check up the superclass hierarchy
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                final Field field = acls.getDeclaredField(fieldName);
                // getDeclaredField checks for non-public scopes as well
                // and it returns accurate results
                if (!Modifier.isPublic(field.getModifiers())) {
                    if (forceAccess) {
                        field.setAccessible(true);
                    } else {
                        continue;
                    }
                }
                return field;
            } catch (final NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        // check the public interface case. This must be manually searched for
        // incase there is a public supersuperclass field hidden by a private/package
        // superclass field.
        Field match = null;
        for (final Class<?> class1 : getAllInterfaces(cls)) {
            try {
                final Field test = class1.getField(fieldName);
                Validate.isTrue(match == null, "Reference to field %s is ambiguous relative to %s"
                        + "; a matching field exists on two or more implemented interfaces.", fieldName, cls);
                match = test;
            } catch (final NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        return match;
    }

    /**
     * <p>Gets a {@code List} of all interfaces implemented by the given
     * class and its superclasses.</p>
     *
     * <p>The order is determined by looking through each interface in turn as
     * declared in the source file and following its hierarchy up. Then each
     * superclass is considered in the same way. Later duplicates are ignored,
     * so the order is maintained.</p>
     *
     * @param cls the class to look up, may be {@code null}
     * @return the {@code List} of interfaces in order,
     * {@code null} if null input
     */
    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    /**
     * Gets the interfaces for the specified class.
     *
     * @param cls             the class to look up, may be {@code null}
     * @param interfacesFound the {@code Set} of interfaces for the class
     */
    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    private static class Validate {

        /**
         * <p>Validate that the specified argument is not {@code null};
         * otherwise throwing an exception with the specified message.
         *
         * <pre>Validate.notNull(myObject, "The object must not be null");</pre>
         *
         * @param <T>     the object type
         * @param object  the object to check
         * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
         * @param values  the optional values for the formatted exception message
         * @return the validated object (never {@code null} for method chaining)
         * @throws NullPointerException if the object is {@code null}
         */
        public static <T> T notNull(final T object, final String message, final Object... values) {
            return Objects.requireNonNull(object, () -> String.format(message, values));
        }

        /**
         * <p>Validate that the argument condition is {@code true}; otherwise
         * throwing an exception with the specified message. This method is useful when
         * validating according to an arbitrary boolean expression, such as validating a
         * primitive number or using your own custom validation expression.</p>
         *
         * <pre>
         * Validate.isTrue(i &gt;= min &amp;&amp; i &lt;= max, "The value must be between &#37;d and &#37;d", min, max);
         * Validate.isTrue(myObject.isOk(), "The object is not okay");</pre>
         *
         * @param expression the boolean expression to check
         * @param message    the {@link String#format(String, Object...)} exception message if invalid, not null
         * @param values     the optional values for the formatted exception message, null array not recommended
         * @throws IllegalArgumentException if expression is {@code false}
         */
        public static void isTrue(final boolean expression, final String message, final Object... values) {
            if (!expression) {
                throw new IllegalArgumentException(String.format(message, values));
            }
        }

    }

}
