package no.dv8.rest.html.support.htmlgen;

import no.dv8.xhtml.generation.support.Element;

public class RDFADecorator implements SemanticDecorator {

    private String vocab = null;

    @Override
    public <T extends Element<T>> T typed(String type, T t) {
        return t.typeof(type);
    }

    @Override
    public <T extends Element<T>> T propped(String name, T item) {
        return item.property(name);
    }

}
