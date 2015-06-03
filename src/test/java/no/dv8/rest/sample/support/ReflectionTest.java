package no.dv8.rest.sample.support;

import org.junit.Test;

import javax.ws.rs.core.GenericEntity;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class ReflectionTest {

    public <ZX> List<String> listString(ZX t) {
        return null;
    };

    @Test
    public <T> void testReflection() throws NoSuchMethodException {
        List<T> l = new ArrayList<T>();

        l = new ArrayList<T>( l ) {};





//        l = new GenericEntity<List<T>>(l){};

//        T[] array = (T[])Array.newInstance(l.getClass(), 1);



        Object[] array = {l};

        System.out.println("1: " + array.getClass().getComponentType());


        System.out.println("2: " + l.getClass().getTypeName());
        System.out.println("3: " + l.getClass().getGenericSuperclass());

        Method m = ReflectionTest.class.getDeclaredMethod("listString", Object.class);
        System.out.println("m4: " + m.getGenericReturnType());
        System.out.println("m5: " + asList(m.getGenericParameterTypes()));
    }
}
