package no.dv8.rest.html.support;

import lombok.Data;
import lombok.Builder;
import no.dv8.rest.sample.semantic.Semantics;
import no.dv8.rest.html.htmlgen.XHTMLAPIGenerator;
import no.dv8.xhtml.generation.elements.*;
import no.dv8.xhtml.generation.support.Element;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Builder
@Data
public class XSimpleCollection implements XAware, XResource {

    LinkedHashMap<String, String> urlToName = new LinkedHashMap<>();
    String itemResourceType, title, description;


    @Override
    public Element<?> toXHTML(List<Endpoint> links) {
        return new div()
                .clz(title)
                .add(new h2(getTitle() != null ? getTitle() : getTypeName()))
                .add( new p( description) )
                .add( new h3( "Items"))
                .add(itemList())
                .add(XHTMLAPIGenerator.builder().endpoints(links).build().linkSection("Collection links"));
    }

    private Element<?> itemList() {
        return new ul()
                .add(
                        urlToName
                                .entrySet()
                                .stream()
                                .map(entry -> new a(entry.getValue()).href(entry.getKey()))
                                .map(e -> new li().add(e))
                                .collect(toList())

                );
    }

    public String getTypeName() {
        return Semantics.collectionOf(itemResourceType);
    }
}
