package no.dv8.rest.html.support.htmlgen;

import no.dv8.xhtml.generation.support.Element;
import no.dv8.xhtml.generation.support.ElementBase;

public interface SemanticDecorator {

    <T extends Element<T>> T typed( String type, T element );

    <T extends Element<T>> T propped(String name, T element);
}
