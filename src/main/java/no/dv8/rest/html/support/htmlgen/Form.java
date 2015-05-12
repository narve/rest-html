package no.dv8.rest.html.support.htmlgen;

import com.google.gson.GsonBuilder;
import lombok.Value;
import no.dv8.rest.html.support.rest.ApiResource;
import no.dv8.rest.html.support.Endpoint;
import no.dv8.rest.html.support.Parameter;
import no.dv8.rest.html.support.X;
import no.dv8.rest.html.support.reflect.Properties;
import no.dv8.xhtml.generation.elements.*;
import no.dv8.xhtml.generation.support.Element;
import no.dv8.xhtml.generation.support.Str;

import javax.ws.rs.PathParam;
import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static no.dv8.rest.html.support.htmlgen.XHTMLAPIGenerator.pathFromTemplate;


@Value
public class Form {

    private final Object item;
    private final Endpoint endpoint;


    public Element<?> form() {
        String path = pathFromTemplate(endpoint, item);
        return new form()
          .set("rel", endpoint.getRelationType())
          .set("href", path)
          .action(path)
          .method(endpoint.getMethods().get(0))
          .add(
            new fieldset()
              .add(new legend(endpoint.getTitle()))
              .add(new a("Link to form").href(ApiResource.linkToForm(endpoint)))
              .add(item == null ? new Str("") : new p("Editing: " + item))
              .add(endpoint.getDescription() != null ? endpoint.getDescription() : null)
              .add(inputs(endpoint))
              .add(new p().add("Action: " + path))
              .add(new p().add("Methods: " + endpoint.getMethods()))
              .add(submit(endpoint))
          );
    }

    private Element submit(Endpoint e) {
        return new input()
          .type("submit")
          .value("Submit! [rel=" + e.getRelationType() + "]");
    }

    public Collection<Element<?>> inputs(Endpoint e) {
        return e.getParameters()
          .stream()
          .map(p -> input(p))
          .collect(toList());
    }

    public Element<?> input(Parameter p) {
        return PathParam.class.getSimpleName().equals(p.getJaxrsType()) ? fakeInput(p) : realInput(p);
    }

    public Element<?> fakeInput(Parameter p) {
        Object val = p.getValue() != null ? p.getValue() : new Properties(item).get(p.getName());
        return new p().add(new label().add(p.getName() + ": [path param with value " + val + "]"));
    }

    public Element<?> realInput(Parameter p) {
        Element<?> asdf;
//        // TODO
        if (p.getType() != null && Properties.isBean(p.getType())) {
            try {
                Object sample = item == null ? Class.forName(p.getType()).newInstance() : item;
//                sample = p.getValue()==null?"no-value-for-param":p.getValue();
                String s = new GsonBuilder()
                  .setPrettyPrinting()
                  .serializeNulls()
                  .create().toJson(sample);
                asdf = new textarea().add(s);
                asdf.set("rows", "6");
                asdf.set("cols", "80");
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            Map<String, Object> map = new Properties(item).getProps();
            Object val = map.get(p.getName());


            asdf = new input()
              .type("text")
              .value(val == null ? null : val.toString())
              .placeholder(p.getPlaceholder());
        }

        asdf.set("name", p.getName())
          .set("readonly", p.isReadOnly() ? "readonly" : null)
          .set("required", p.isRequired() ? "required" : null);

        return new div().clz("input")
//          .add(new p("Type: " + p.getType()))
//          .add(new p("InputType: " + p.getInputType()))
          .add(new label()
            .add(p.getName() + "[" + p.getInputType() + "]: ")
            .add(asdf));
    }
}
