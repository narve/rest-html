package no.dv8.rest.html.support.htmlgen;

import no.dv8.xhtml.generation.support.Element;

public class MicroDataDecorator implements SemanticDecorator {

    private String typePrefix = "http://schema.org/";

    @Override
    public <T extends Element<T>> T typed(String type, T element) {
        return element
          .itemscope()
          .itemtype(getItemType(type));
    }

    @Override
    public <T extends Element<T>> T propped(String name, T element) {
        return element.itemprop( name);
    }

    public String getItemType(String type) {
        return typePrefix + type;
    }


}
