package no.dv8.rest.html.htmlgen;

import no.dv8.xhtml.generation.support.Element;

public class MicroformatDecorator implements SemanticDecorator {
    @Override
    public <T extends Element<T>> T typed(String type, T element) {
        return element.clz(type);
    }

    @Override
    public <T extends Element<T>> T propped(String name, T element) {
        return element.clz(name);
    }
}
