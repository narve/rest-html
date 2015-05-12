package no.dv8.rest.sample.support;

import no.dv8.rest.html.support.reflect.Annotations;
import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class APIReaderTest {

    public void someMethod( @PathParam( "pathparam1") int i ) {

    }

    @Test
    public void testHasAnnotation() throws Exception {
        Method method = APIReaderTest.class.getMethod("someMethod", Integer.TYPE);
        Annotation[] annotations = method.getParameterAnnotations()[0];
        assertThat( annotations.length, equalTo( 1 ) );
        assertThat( "no 0 should be instanceof PathParam", annotations[0] instanceof PathParam, equalTo(true) );
//        assertThat( "no 0 should be class PathParam", annotations[0].getClass(), equalTo(PathParam.class) );
//        assertThat( "no 0 should be assignable from PathParam", annotations[0].getClass().isAssignableFrom(PathParam.class), equalTo(true) );
        assertThat( "no 0 should be assignable to PathParam", PathParam.class.isAssignableFrom(annotations[0].getClass()), equalTo(true) );
        assertThat( "hasAnnotation should return true", Annotations.hasAnnotation(PathParam.class, annotations), equalTo( true ) );
        assertThat( "hasAnnotation should return false", Annotations.hasAnnotation(FormParam.class, annotations), equalTo( false ) );
    }

    @Test
    public void testGetAnnotation() throws Exception {

    }
}