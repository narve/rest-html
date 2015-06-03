package no.dv8.rest.html.htmlgen.linker;

import no.dv8.rest.html.support.Endpoint;

import java.util.List;

public interface Linker {
    List<Endpoint> links(Object o);
}
