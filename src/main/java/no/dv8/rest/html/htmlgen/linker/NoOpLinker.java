package no.dv8.rest.html.htmlgen.linker;

import no.dv8.rest.html.support.Endpoint;

import java.util.ArrayList;
import java.util.List;

public class NoOpLinker implements Linker {

    @Override
    public List<Endpoint> links(Object o) {
        return new ArrayList<>();
    }
}
