package no.dv8.rest.html.htmlgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.java.Log;
import no.dv8.rest.html.htmlgen.linker.Linker;
import no.dv8.rest.html.htmlgen.linker.NoOpLinker;
import no.dv8.rest.html.support.reflect.Annotations;
import no.dv8.rest.sample.semantic.Rels;
import no.dv8.rest.sample.semantic.Semantics;
import no.dv8.rest.html.support.Endpoint;
import no.dv8.rest.html.support.Parameter;
import no.dv8.rest.html.support.XAware;
import no.dv8.rest.html.support.XResource;
import no.dv8.rest.html.support.reflect.Properties;
import no.dv8.rest.html.support.rest.HTMLBodyWriter;
import no.dv8.xhtml.generation.elements.*;
import no.dv8.xhtml.generation.support.Custom;
import no.dv8.xhtml.generation.support.Element;
import no.dv8.xhtml.generation.support.Str;

import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlTransient;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.toList;

@Builder
@AllArgsConstructor
@Log
public class XHTMLAPIGenerator<T> {

    private T item;
    private String title;
    private Type what;
    private List<Endpoint> endpoints;
    //    private SemanticDecorator semdec = new DecoratorChain( asList( new MicroDataDecorator(), new RDFADecorator()));
    public SemanticDecorator semdec = new DecoratorChain(asList(new RDFADecorator()));
    public Linker linker = new NoOpLinker();

    public XHTMLAPIGenerator() {
    }

    public XHTMLAPIGenerator(String title, T item, Type what, List<Endpoint> endpoints) {
        this.item = item;
        this.what = what;
        this.endpoints = endpoints;
        this.title = title == null ? getTypeName(item, what) : title;
    }

    public static String pathFromTemplate(Endpoint e, Object item) {
        String path = e.getRelativePath();
        for (Parameter pm : e.getParameters()) {
            if (PathParam.class.getSimpleName().equals(pm.getJaxrsType())) {
//                String rex = "(\\{" + pm.getName() + "(\\:[^}]*)\\})";
                String rex = "(\\{" + pm.getName() + "(:[^}]*)?\\})";
                log.fine("Searching for: " + rex + " in " + path);
                Pattern patt = Pattern.compile(rex);
                Matcher matcher = patt.matcher(path);
                if (matcher.find()) {
                    Object val = pm.getValue() != null ? pm.getValue() : new Properties(item).get(pm.getName());
                    if (val != null) {
                        StringBuffer sb = new StringBuffer();
                        matcher = patt.matcher(path);
                        while(matcher.find()) {
                            matcher.appendReplacement(sb, quoteReplacement(val.toString()));
                        }
                        matcher.appendTail(sb);
                        path = sb.toString();
                        log.fine("Found: " + rex + " in " + path);
                    } else {
                        log.info("Hm... " + pm + " does not have value");
                    }
                } else {
                    log.info("Hm... " + path + " does not match " + rex);
                }
            }
        }
//        log.fine( "Path for " + item + " based on " + e + ": " + path);
        return path;
    }

    public static String getTypeName(Object x, Type type) {
        if (x instanceof XResource) {
            return ((XResource) x).getTypeName();
        } else if (x instanceof Collection && type != null && type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            Type type1 = ptype.getActualTypeArguments()[0];
            if (type1 instanceof Class) {
                String itemTypeName = ((Class) type1).getSimpleName();
                return Semantics.collectionOf(itemTypeName);
            } else {
                return Semantics.collectionOf(type1.getTypeName());
            }
        } else {
            return x.getClass().getSimpleName();
        }
    }

    public Element<?> generateElement(Endpoint e) {
        if (!asList("GET").equals(e.getMethods())) {
            return new Form(item, e).form();
        }
        long count = e
                .getParameters()
                .stream()
                .filter(pm -> pm.getValue() != null || !PathParam.class.getSimpleName().equals(pm.getJaxrsType()))
                .count();
        return count == 0 ? link(e) : new Form(item, e).form();
    }


    public Element<?> link(Endpoint e) {
//        log.info("generateElementLink, " + e);
        String path = pathFromTemplate(e, item);
        return new a()
                .rel(e.getRelationType())
//          .clz(e.getResourceType())
                .set("data-alpsid", e.getResourceType())
                .href(path)
                .add(e.getTitle())
                .add(" [rel=" + e.getRelationType() + "]")
                .add(" [href=" + path);
    }

    //    public Element<?> itemToXHTML(Object item, Type type, List<Endpoint> links) {
    public Element<?> itemToXHTML() {
        if (item instanceof Element<?>) {
            return (Element<?>) item;
        } else if (item instanceof XAware) {
            return ((XAware) item).toXHTML(endpoints);
        } else if (item instanceof Collection) {
            return listToXHTML((Collection) item);
        } else {
            return introspect();
        }
    }

    private Element<?> listToXHTML(Collection<?> items) {
        String tname = getTypeName(items, what);
        return new div()
                .add(new h2(tname))
                .add(
                        new ul()
//              .clz(Semantics.collectionOf(tname))
                                .add(
                                        items
                                                .stream()
                                                .map(i ->
                                                        {
                                                            if (i instanceof String) {
                                                                return new span(i == null ? "<null>" : i.toString());
                                                            }
                                                            return new XHTMLAPIGenerator<>(getTypeName(i, null), i, null, linker.links(i)).itemToXHTML();
                                                        }
                                                )
                                                .map(l -> new li().add(l))
//              .map(e -> asList(e, new Custom("hr")))
                                                .collect(Collectors.toList())
                                )
                ).add(linkSection("Collection links"));
    }

    public Element<?> introspect() {
        String tname = getTypeName(item, null);
        return semdec.typed(tname, new div()
                        .add(new h3(title))
                        .add(propList(item))
                        .add(linkSection("Links"))
        );
    }

    public static boolean shouldIgnore( PropertyDescriptor pd ) {
        Set<String> ignorables = new HashSet<>( asList( "jsonignore", "xmltransient" ) );
        List<Annotation> annotations = asList( pd.getReadMethod().getAnnotations());
        Annotation[] aa = annotations.toArray(new Annotation[0]);
        if( Annotations.hasAnnotation(XmlTransient.class, aa) )
            return true;
        return annotations.stream().anyMatch(a -> ignorables.contains(a.getClass().getSimpleName().toLowerCase()));
    }

    public dl propList(Object item) {
        dl props = new dl().clz("props");
        try {
            List<PropertyDescriptor> pda = asList(Introspector.getBeanInfo(item.getClass()).getPropertyDescriptors());
            pda
                    .stream()
                    .filter( pd -> !XHTMLAPIGenerator.shouldIgnore(pd))
                    .filter(pd -> !"class".equals(pd.getName()))
                    .forEach(pd -> props.add(toXHTML(pd, item)));
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return props;
    }

    public Element<?> linkSection(String title) {
        return new div()
                .add(new h3(title + " (" + endpoints.size() + ")"))
                .add(new ul()
                        .add(
                                endpoints.stream()
                                        .map(endpoint -> new li().add(generateElement(endpoint)))
                                        .collect(toList())
                        ));
    }

    public Element<?> toXHTML(PropertyDescriptor f, Object item) {
        Object val = null;
        try {
//            val = f.get(item);
            val = f.getReadMethod().invoke(item);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        if (val instanceof Collection) {
            return collectionSection(f.getName(), (Collection<?>) val);
        } else {
            return definition(f.getName(), val);
        }
    }

    public Element<?> collectionSection(String fname, Collection<?> col) {
        ul listHolder = new ul();
        div d = new div()
                .add(new h("Collection '" + fname + "': " + col.size() + " items"))
                .add(listHolder);

        col.forEach(x -> {
            if (!Properties.isBean(x)) {
                listHolder.add(new li().add(x.toString()));
            } else {
                List<Endpoint> links = linker.links(x);
                log.info("Links for " + x + "=" + links);
                listHolder.add( new li().add( new XHTMLAPIGenerator<>("", x, null, links).itemToXHTML() ));
            }
        });
        return d;
    }

    public Element<?> definition(String name, Object val) {
        Element<?> content;
        if( val instanceof Enum ) {
            content = new span(val.toString());
        } else if (Properties.isBean(val)) {
            content = semdec.typed(getTypeName(val, null), propList(val));
        } else {
            content = new Str(val == null ? null : val.toString());
        }

        return new Custom("di")
                .add(
                        new dt()
                                .add(name)
                ).add(
                        semdec.<Element>propped(name, new dd().add(content))
                );
    }

}
