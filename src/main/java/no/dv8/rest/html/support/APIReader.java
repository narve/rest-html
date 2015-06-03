package no.dv8.rest.html.support;

import lombok.extern.slf4j.Slf4j;
import no.dv8.rest.html.annotations.*;
import no.dv8.rest.html.support.reflect.Annotations;

import javax.ws.rs.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Slf4j
public class APIReader {

    public String basePath = "/myapp";

    public boolean isWebMethod(Method m) {
        if (m.isAnnotationPresent(GET.class)) {
            return true;
        } else if (m.isAnnotationPresent(POST.class)) {
            return true;
        } else if (m.isAnnotationPresent(PUT.class)) {
            return true;
        } else if (m.isAnnotationPresent(DELETE.class)) {
            return true;
        }
        return false;
    }

    public API read(Class<?>... resources) {
        return new API(
          asList(resources).stream()
            .map(Class::getDeclaredMethods)
            .map(Arrays::asList)
            .flatMap(l -> l.stream())
            .filter(this::isWebMethod)
            .map(this::endpoint)
            .peek(e -> {
                log.debug("Endpoint in read: {}", e);
            })
            .collect(toList())
        );
    }

    public Endpoint endpoint(Method m) {
        Class<?> clz = m.getDeclaringClass();
        StringBuilder p = new StringBuilder(basePath);
        if (clz.isAnnotationPresent(Path.class)) {
            p.append(clz.getAnnotation(Path.class).value());
        }
        if (m.isAnnotationPresent(Path.class)) {
            p.append(m.getAnnotation(Path.class).value());
        }

        String rel = "TBA";
        if (m.isAnnotationPresent(Rel.class)) {
            rel = m.getAnnotation(Rel.class).value();
        }

        Endpoint e = new Endpoint();

        e.setJavaClz(m.getDeclaringClass());
        e.setJavaMethod(m);


        e.setRelativePath(p.toString());
//        e.setFront(!m.isAnnotationPresent(WhenLinkedOnly.class));
        Annotation[] declaredAnnotations = m.getDeclaredAnnotations();
        if (m.isAnnotationPresent(ResourceType.class)) {
            e.setResourceType(m.getAnnotation(ResourceType.class).value());
        }
        if (m.isAnnotationPresent(Target.class)) {
            e.setTarget(m.getAnnotation(Target.class).value());
        }

        e.setTitle(m.getName());
        if (m.isAnnotationPresent(Description.class)) {
            e.setDescription(m.getAnnotation(Description.class).value());
            log.info("Description set to " + e.getDescription());
        }
        if (m.isAnnotationPresent(Title.class)) {
            e.setTitle(m.getAnnotation(Title.class).value());
            log.info("Title set to " + e.getTitle());
        }

        e.setRelationType(rel);
        if (m.isAnnotationPresent(Rel.class))
            e.setRelationType(m.getAnnotation(Rel.class).value());

        if (m.isAnnotationPresent(POST.class)) {
            e.setMethods(asList("POST"));
        }
        if (m.isAnnotationPresent(PUT.class)) {
            e.setMethods(asList("PUT"));
        }
        if (m.isAnnotationPresent(DELETE.class)) {
            e.setMethods(asList("DELETE"));
        }

        for (int i = 0; i < m.getParameters().length; i++) {
            java.lang.reflect.Parameter parameter = m.getParameters()[i];
            if (false ) { //&& parameter.getType().equals(ExpenseReport.class)) {
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(String.class);
                    List<PropertyDescriptor> pda = asList(beanInfo.getPropertyDescriptors())
                      .stream()
                      .filter(pd -> pd.getWriteMethod() != null)
                      .collect(toList());
                    for (PropertyDescriptor pd : pda) {
                        Parameter pm = Parameter
                          .builder()
                          .type(pd.getPropertyType().toString())
                          .name(pd.getName())
                          .build();
                        e.getParameters().add(pm);
                    }
                } catch (IntrospectionException e1) {
                    throw new RuntimeException(e1);
                }
            } else {
                Annotation[] annotations = m.getParameterAnnotations()[i];
                String name = parameter.getName();
                String jaxrsType = null;
                if (Annotations.hasAnnotation(QueryParam.class, annotations)) {
                    QueryParam qp = Annotations.getAnnotation(QueryParam.class, annotations).get();
                    name = qp.value();
                    jaxrsType = QueryParam.class.getSimpleName();
                } else if (Annotations.hasAnnotation(FormParam.class, annotations)) {
                    FormParam qp = Annotations.getAnnotation(FormParam.class, annotations).get();
                    name = (qp.value());
                    jaxrsType = FormParam.class.getSimpleName();
                } else if (Annotations.hasAnnotation(PathParam.class, annotations)) {
                    PathParam qp = Annotations.getAnnotation(PathParam.class, annotations).get();
                    name = qp.value();
                    jaxrsType = PathParam.class.getSimpleName();
                }
                Parameter pm = Parameter.builder()
                  .type(parameter.getType().getName())
                  .name(name)
                  .jaxrsType(jaxrsType)
                  .inputType("text")
                  .build();
                e.getParameters().add(pm);
            }
        }


        return e;
    }

}
