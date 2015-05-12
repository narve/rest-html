package no.dv8.rest.html.support;

import lombok.*;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import no.dv8.xhtml.generation.elements.a;
import no.dv8.xhtml.generation.support.Element;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Endpoint implements Cloneable, XAware {
    String targetType;
    String relativePath;
    String relationType;
    String title, description;
    String target;
    String resourceType;
    Class javaClz;
    Method javaMethod;
    List<String> methods = asList("GET");
    List<Parameter> parameters = new ArrayList<>();

//    public Endpoint setParam( String name, Object value ) {
//        List<Parameter> collect = parameters.stream().filter(p -> name.equals(p.getName())).collect(toList());
//        if( collect.size() != 1 ) {
////            throw new RuntimeException(collect.size() + " != 1");
//        } else {
//            collect.get(0).setValue(value);
//        }
//        return this;
//
//    }

    public Endpoint clone() {
        try {
            Endpoint e = (Endpoint) super.clone();
            e.setParameters(new ArrayList<>(
                    getParameters()
                    .stream()
                    .map( Parameter::clone)
                    .collect(toList())
                    ));
            return e;
        } catch (CloneNotSupportedException e1) {
            throw new RuntimeException(e1);
        }

    }

    @Override
    public Element<?> toXHTML(List<Endpoint> links) {
        return new a(getRelativePath()).href(getRelativePath());
    }
}
