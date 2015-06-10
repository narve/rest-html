package no.dv8.rest.html.support.rest;

import lombok.extern.slf4j.Slf4j;
import no.dv8.rest.html.annotations.Rel;
import no.dv8.rest.html.annotations.Target;
import no.dv8.rest.html.htmlgen.XHTMLAPIGenerator;
import no.dv8.rest.sample.semantic.Rels;
import no.dv8.rest.html.support.APIReader;
import no.dv8.rest.html.support.Endpoint;
import no.dv8.rest.html.support.X;
import no.dv8.xhtml.generation.elements.*;
import no.dv8.xhtml.generation.support.Element;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Slf4j
public class HTMLBodyWriter<T> implements MessageBodyWriter<T> {

    public static List<Class> clz = asList( String.class, Integer.class );

    public static List<Endpoint> autoLinks(Object er, Type type) {
        String sem = XHTMLAPIGenerator.getTypeName(er, type);
        List<Endpoint> l1 = ApiResource.getAPI().getDestinations().stream()
          .filter(e -> sem.equals(e.getTarget()))
          .collect(toList());

        return l1;
    }

    public Endpoint autoSelfLink(Object er, Type type) {
        List<Endpoint> selfLinks = autoSelfLinks(er, type);
        if (selfLinks.size() != 1) {
            throw new RuntimeException("Self-link not found - " + selfLinks.size() + " matching endpoints");
        }
        return selfLinks.get(0);
    }

    public List<Endpoint> autoSelfLinks(Object er, Type type) {
        List<Method> methods =
          clz
            .stream()
            .map(Class::getDeclaredMethods)
            .flatMap(ma -> asList(ma).stream())
            .collect(toList());

        String sem = XHTMLAPIGenerator.getTypeName(er, type);
        List<Endpoint> linkMethods = methods
          .stream()
          .filter(m -> hasAnnotatedValue(m, Target.class, sem))
          .filter(m -> m.isAnnotationPresent(Rel.class) && m.getAnnotation(Rel.class).value().equals(Rels.Self))
          .peek(m -> System.out.println("Stuff: " + m))
          .map(m -> new APIReader().endpoint(m))
          .collect(toList());
        log.info("Sem for " + er.getClass().getSimpleName() + ": " + sem + ", links: " + linkMethods.size());
        return linkMethods;
    }

    private static boolean hasAnnotatedValue(Method m, Class<? extends Target> annClass, String val) {
        return m.isAnnotationPresent(annClass) && m.getAnnotation(annClass).value().equals(val);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(T o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            log.info("Writing object of class " + type + ", " + genericType + ", " + mediaType);
            OutputStreamWriter writer = new OutputStreamWriter(entityStream);
            writer.write(toString(o, genericType));
            writer.close();
            log.info("Done writing object of class " + type);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public long getSize(T o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return toString(o, genericType).length();
    }


    public String getTitle(Object item) {
        if (item instanceof X<?>) {
            return ((X<?>) item).getTitle();
        } else {
            return item.getClass().getSimpleName();
        }
    }

    protected String toString(T o, Type what) {
        String title = getTitle(o);
        return new html()
          .add(
            new head()
              .add(asList(
                  new title().add(title),
                  new script().src("//code.jquery.com/jquery-2.1.3.js"),
                  new script().src("/static/jquery.rdfquery.rdfa-1.0.js"),
                  new script().src("/static/custom.js")
                )
              ).add(
              new link().rel("stylesheet").type("text/css").href("/static/css/default.css")
            )).add(
            new body()
              .vocab("http://example.org")
              .add(new h1("Auto-generated HTML version of " + what).clz("apiheader"))
              .add(itemToElement(o, what))
//              .add(new div().id("rdf").add("her kommer listen...").add(new h1("asdf")))


//              <p id="info">This paper was written by < span rel = "dc:creator"resource = "#me" > < span property = "foaf:name" > Ben
//        Adida</span >.</span ></p >


//        .add(new p().id("info")
//          .add("This paper was written by ")
//          .add(
//            new span()
//              .set("rel", "dc:creator" )
//              .set("resource", "#me")
//              .add( new span("Ben Adida").property( "foaf:name").add( "Ben Adida" )
//              )))
//


          ).toString();
    }

    public Element itemToElement(T o, Type what) {
        return new XHTMLAPIGenerator(o, what, autoLinks(o, what)).itemToXHTML();
    }


}
