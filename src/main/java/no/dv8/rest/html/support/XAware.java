package no.dv8.rest.html.support;

import no.dv8.xhtml.generation.support.Element;

import java.util.List;

public interface XAware {

    Element<?> toXHTML(List<Endpoint> links);

}
