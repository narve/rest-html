package no.dv8.rest.html.support.reflect;

import lombok.extern.slf4j.Slf4j;
import no.dv8.rest.sample.semantic.Semantics;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Slf4j
public class Properties {

    Object o;

    public Properties(Object o) {
        this.o = o;
    }

    public static boolean isBean(Object o) {
        return o != null && isBean(o.getClass().getName());
    }

    public static boolean isBean(String cname) {
        return !cname.startsWith("java") && !cname.startsWith("org.hibernate");
    }

    public Map<String, Object> getProps() {
        return putProps(new LinkedHashMap<>(), "");
    }

    private Map<String, Object> putProps(Map<String, Object> map, String prefix) {
        if (o == null)
            return map;

        log.info("For prefix {} {} putting props into {}", new Object[]{prefix, o.getClass(), map});
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
            List<PropertyDescriptor> props = asList(beanInfo.getPropertyDescriptors())
                    .stream()
                    .filter(pd -> !pd.getName().equals("class"))
                    .collect(toList());

            for (PropertyDescriptor pd : props) {
                try {
                    Object val = pd.getReadMethod().invoke(o);
                    if (isBean(val)) {
                        log.info("   Recursing for {}, prop={}, path={}", new Object[]{o.getClass().getSimpleName(), pd.getName(), prefix});
                        new Properties(val).putProps(map, pd.getName() + Semantics.PROPSEP);
                    } else {
                        map.put(prefix + pd.getName(), val);
                    }
                } catch (InvocationTargetException e) {
                    log.info("{} when trying to access property {} of {}, path=", new Object[]{e, o.getClass(), pd.getName(), prefix});
                }
            }
            return map;
        } catch (IllegalAccessException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Object get(String name) {
        return (T) getProps().get(name);
    }
}
