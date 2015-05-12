package no.dv8.rest.html.support;

import no.dv8.rest.html.support.htmlgen.XHTMLAPIGenerator;
import no.dv8.xhtml.generation.support.Element;

import java.util.ArrayList;
import java.util.List;

public abstract class X<T extends X> implements Cloneable, XAware, XResource {

    public String getTypeName() {
        return getClass().getSimpleName();
    }

    public String getTitle() {
        return toString();
    }

    public Element<?> toXHTML(List<Endpoint> links) {
        return new XHTMLAPIGenerator( this, this.getClass(), links).itemToXHTML();
    }

    public List<Endpoint> links = new ArrayList<>();

    public List<Endpoint> getLinks() {
        return links;
    }


}
