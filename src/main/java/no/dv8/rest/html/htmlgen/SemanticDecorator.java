package no.dv8.rest.html.htmlgen;

import no.dv8.xhtml.generation.support.Element;

public interface SemanticDecorator {

    <T extends Element<T>> T typed( String type, T element );

    <T extends Element<T>> T propped(String name, T element);
}
