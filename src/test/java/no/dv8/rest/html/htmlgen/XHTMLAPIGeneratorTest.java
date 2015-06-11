package no.dv8.rest.html.htmlgen;

import no.dv8.rest.html.support.Endpoint;
import no.dv8.rest.html.support.Parameter;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;


public class XHTMLAPIGeneratorTest {

    public static class ClzWithId {
        public Long getId() {
            return 123L;
        }
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