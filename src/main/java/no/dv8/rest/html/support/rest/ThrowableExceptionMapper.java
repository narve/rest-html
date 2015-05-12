package no.dv8.rest.html.support.rest;

import lombok.extern.slf4j.Slf4j;
import no.dv8.xhtml.generation.elements.p;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Provider
@Slf4j
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

//    @Context
//    HttpServletRequest request;

    @Override
    public Response toResponse(Throwable t) {
        t.printStackTrace();
        if (t instanceof WebApplicationException) {
            return ((WebApplicationException) t).getResponse();
        } else {
            String errorMessage = t.toString() + ": " + asList(t.getStackTrace())
              .stream()
              .map(StackTraceElement::toString)
              .map(p::new)
              .collect(toList())
              .toString();
//            log.error(errorMessage, t);
            return Response.serverError().entity(errorMessage).build();
        }
    }


}