package no.dv8.rest.html.support.rest;

import lombok.extern.slf4j.Slf4j;
import no.dv8.rest.html.support.API;
import no.dv8.rest.html.support.APIReader;
import no.dv8.rest.html.support.Endpoint;
import no.dv8.rest.html.htmlgen.Form;
import no.dv8.rest.html.htmlgen.XHTMLAPIGenerator;
import no.dv8.xhtml.generation.elements.div;
import no.dv8.xhtml.generation.elements.h2;
import no.dv8.xhtml.generation.elements.li;
import no.dv8.xhtml.generation.elements.ul;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.*;
import static no.dv8.rest.html.support.rest.GenericResource.genDests;

@Path("api")
@Slf4j
@Produces(MediaType.TEXT_HTML)
public class ApiResource {

    static final java.nio.file.Path base = new File("src/main/resources").toPath();
    //    static final java.nio.file.Path base = new File("/users/narve/dev/rest-test/simple-service/src/main/resources").toPath();
//        static final java.nio.file.Path base = new File("/home/narve/dev/rest-test/simple-service/src/main/resources").toPath();
    static final String SEP = "\r\n";

    static APIReader reader() {
        APIReader reader = new APIReader();
        reader.basePath = "/myapp";
        return reader;
    }

    public static API getAPI() {
        API api = reader().read(GenericResource.class);
        // api.getDestinations().addAll(genDests());
        return api;
    }

    public static API getAPI( String bp, Class[] clz, Class[] objs) {
        APIReader reader = reader();
        reader.basePath = bp;
        API api = reader.read(clz);
        api.getDestinations().addAll(genDests(objs));
        return api;
    }

    public static String linkToForm( no.dv8.rest.html.support.Endpoint e ) {
        return "/myapp/api/form/" + e.getJavaClz().getName() + "/" + e.getJavaMethod().getName();
    }

    @GET
    @Path("/form/{resource}/{method}")
    public String form(@PathParam("resource") String resource, @PathParam("method") String methodName) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> clz = Class.forName(resource);
//        Method method = asList(clz.getDeclaredMethods()).stream().filter(m -> m.getName().equals(methodName)).findFirst().get();
        List<no.dv8.rest.html.support.Endpoint> endpoint = getAPI()
          .getDestinations()
          .stream()
          .filter(e -> e.getJavaClz().equals(clz))
          .filter( e -> e.getJavaMethod().getName().equals(methodName))
          .collect( toList());
        if( endpoint.size() != 1 ) {
            throw new RuntimeException("UPS: " + endpoint.size() + " matches for " + resource + "." + methodName );
        }
        XHTMLAPIGenerator gen = new XHTMLAPIGenerator(null, null, null);
        return new Form(null, endpoint.get(0) ).form().toString();
    }

    @GET
    public String getIt() {
        log.info("api...");
        try {
            final String alps = new String(Files.readAllBytes(base.resolve("alps.xml")))
              .replace("&", "&amp;")
              .replace("<", "&lt;");
            API api = getAPI();
            List<Endpoint> dests = api.getDestinations();

//            String x =
            String apiString = apiString(dests);
            StringBuilder sb = new StringBuilder();
            String res = new String(Files.readAllBytes(base.resolve("api.html")))
              .replaceAll(quote("{{ALPS}}"), quoteReplacement(alps))
              .replaceAll(quote("{{API}}"), quoteReplacement(apiString));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String apiString(List<Endpoint> dests) {
        ul items = new ul().add(
          dests.stream()
//                            .filter(e -> e.getParameters().isEmpty())
//                            .filter(e -> e.getMethods().equals( asList( "GET")))
            .filter(e -> e.getTarget() == null)
            .collect(
              groupingBy(
                e -> e.getJavaClz().getSimpleName(),
                TreeMap::new,
                mapping(e -> e, toSet())
              )
            ).entrySet()
            .stream()
            .map(m -> new div().add(new h2(m.getKey())).add(
                m.getValue()
                  .stream()
                  .sorted((e1, e2) -> e1.getParameters().size() - e2.getParameters().size())
                  .map(e -> new XHTMLAPIGenerator().generateElement(e))
                  .map(e -> new li().add(e))
                  .collect(toList()))
            )
//                    .map(k -> new li(k))
            .collect(toList())
          );


//                    .map(XHTMLAPIGenerator::generateElement)
//                    .map(e -> new li().add(e))
//                    .collect(toList())
//                ).toString();
        return items.toString();
    }

    @GET
    @Path("/get/{path: [\\.a-zA-Z0-9_/]+}")
    public String get(@PathParam("path") String path, @Context UriInfo uriInfo) throws ClassNotFoundException {
        log.info("gotten: " + path + ", " + uriInfo + ", " + uriInfo.getQueryParameters());

        String clz = path.split("/")[0];
        String mname = path.split("/")[1];

        log.info("clz and name: " + clz + ", " + mname);
        Class.forName(clz);

        Optional<Endpoint> endpoint = getAPI().getDestinations().stream()
          .filter(e -> e.getJavaClz().getName().equals(clz))
          .filter(e -> e.getJavaMethod().getName().equals(mname))
          .findAny();

        log.info("endpoint: " + endpoint);

        return endpoint.isPresent() ? endpoint.toString() : "404: " + clz + "->" + mname + ", " + clz;

    }
}
