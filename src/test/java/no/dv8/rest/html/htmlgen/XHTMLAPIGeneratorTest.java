package no.dv8.rest.html.htmlgen;

import no.dv8.rest.html.support.Endpoint;
import no.dv8.rest.html.support.Parameter;
import no.dv8.xhtml.generation.attributes.Clz;
import org.junit.Test;

import javax.xml.bind.annotation.XmlTransient;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;


public class XHTMLAPIGeneratorTest {

    public static class ClzWithId {
        public Long getId() {
            return 123L;
        }
        @XmlTransient
        public Long getTransient() {
            return 123L;
        }
    }

    @Test
    public void testXmlTransientIgnore() throws IntrospectionException {
        PropertyDescriptor transPD =
                asList( Introspector.getBeanInfo(ClzWithId.class).getPropertyDescriptors() )
                .stream()
                .filter( x -> x.getName().equals( "transient" ) )
                .findFirst()
                .get();

        PropertyDescriptor idPD =
                asList( Introspector.getBeanInfo(ClzWithId.class).getPropertyDescriptors() )
                .stream()
                .filter( x -> x.getName().equals( "id" ) )
                .findFirst()
                .get();

        assertThat("should ignore trans", XHTMLAPIGenerator.shouldIgnore(transPD), equalTo(true));
        assertThat("should not ignore id", XHTMLAPIGenerator.shouldIgnore(idPD), equalTo(false));
    }

    @Test
    public void testPathFromTemplate() throws Exception {
        Endpoint e = createEndpoint();
        e.setRelativePath( "/items/{id: \\d+}/asdf");

        String p = XHTMLAPIGenerator.pathFromTemplate(e, new ClzWithId());
        assertThat( p, equalTo( "/items/123/asdf"));
    }
    @Test
    public void testSimplePathFromTemplate() throws Exception {
        Endpoint e = createEndpoint();
        String p = XHTMLAPIGenerator.pathFromTemplate(e, new ClzWithId());
        assertThat( p, equalTo( "/items/123/asdf"));
    }

    private Endpoint createEndpoint() {
        Parameter param = Parameter.builder().jaxrsType("PathParam")
                .name("id").build();
        Endpoint e = new Endpoint();
        e.setRelativePath( "/items/{id}/asdf");
        e.setParameters(asList(param));
        return e;
    }
}