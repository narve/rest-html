package no.dv8.rest.html.support.reflect;

import java.lang.annotation.Annotation;
import java.util.Optional;

import static java.util.Arrays.asList;

public class Annotations {
    public static boolean hasAnnotation(Class<?> annotationClass, Annotation[] annotations) {
        return asList(annotations).stream()
          .filter(a -> annotationClass.isAssignableFrom(a.getClass()))
          .count() > 0;
    }

    public static <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass, Annotation[] annotations) {
        return (Optional<T>) asList(annotations).stream()
          .filter(a -> annotationClass.isAssignableFrom(a.getClass()))
          .findFirst();
    }
}
