package no.dv8.rest.html.support;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import no.dv8.rest.sample.semantic.Semantics;
import no.dv8.rest.html.support.htmlgen.XHTMLAPIGenerator;
import no.dv8.xhtml.generation.elements.*;
import no.dv8.xhtml.generation.support.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@XmlRootElement
public class XPropCollection<T> implements XAware, XResource {


    @Getter
    @XmlElement
    String itemResourceType, title, description;
    @XmlTransient
    List<T> items = new ArrayList<>();
    LinkedHashMap<String, Function<T, Object>> mappers = new LinkedHashMap<>();

    @Builder
    public XPropCollection(String itemResourceType, String title, String description) {
        this.itemResourceType = itemResourceType;
        this.title = title;
        this.description = description;
    }

    public String getTypeName() {
        return Semantics.collectionOf(itemResourceType);
    }

    @XmlTransient
    public LinkedHashMap<String, Function<T, Object>> getMappers() {
        return mappers;
    }

    @XmlList
    public List<T> getItems() {
        return items;
    }


    @Override
    public Element<?> toXHTML(List<Endpoint> links) {
        XHTMLAPIGenerator<Object> x = XHTMLAPIGenerator.builder()
          .endpoints(links)
          .build();
        return new div()
          .clz(title)
          .add(new h2(getTitle() != null ? getTitle() : getTypeName()))
          .add(new p(description))
          .add(new h3("Items"))
          .add(itemList())
          .add(x.linkSection("Collection links"));
    }

    private Element<?> itemList() {
        return new table()
          .add(
            new thead()
              .add(headerRow())
          ).add(
            new tbody()
              .add(
                items
                  .stream()
                  .map(entry -> new tr().add(dataRow(entry)))
                  .collect(toList())
              )
          );
    }

    private Element<?> headerRow() {
        return new tr()
          .add(
            mappers
              .entrySet()
              .stream()
              .map(e -> e.getKey())
              .map(th::new)
              .collect(toList())
          );
    }

    private Element<?> dataRow(T o) {
        return new tr()
          .add(
            mappers
              .entrySet()
              .stream()
              .map(e -> e.getValue().apply(o))
              .map(s -> s == null ? "" : s.toString())
              .map(td::new)
              .collect(toList())
          );
    }


//    private Element<?> headerRow() {
//        return new tr()
//          .add(
//            propertyDescriptors
//              .stream()
//              .map(pd -> new th(pd.getName()))
//              .collect(toList())
//          );
//    }
//
//    private Element<?> dataRow(Object o) {
//        return new tr()
//          .add(
//            propertyDescriptors
//              .stream()
//              .map(pd -> new td(valueOf(pd, o)))
//              .collect(toList())
//          );
//    }
//
//    private String valueOf(PropertyDescriptor pd, Object o) {
//        try {
//            Object val = pd.getReadMethod().invoke(o);
//            return val == null ? null : val.toString();
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
