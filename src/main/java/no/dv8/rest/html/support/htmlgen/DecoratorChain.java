package no.dv8.rest.html.support.htmlgen;

import no.dv8.xhtml.generation.support.Element;

import java.util.ArrayList;
import java.util.List;

public class DecoratorChain implements SemanticDecorator {

    List<SemanticDecorator> decorators = new ArrayList<>();

    public DecoratorChain() {
    }

    public DecoratorChain(List<SemanticDecorator> decorators) {
        this.decorators = decorators;
    }

    @Override
    public <T extends Element<T>> T typed(String type, T element) {
        decorators.forEach( dec -> dec.typed(type,element));
        return element;
    }

    @Override
    public <T extends Element<T>> T propped(String name, T element) {
        decorators.forEach( dec -> dec.propped(name, element));
        return element;
    }
}
