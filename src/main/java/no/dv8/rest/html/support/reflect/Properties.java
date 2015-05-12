package no.dv8.rest.html.support.reflect;

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

public class Properties {

    Object o;

    public Properties(Object o) {
        this.o = o;
    }

    public static boolean isBean(Object o) {
        return o != null && isBean( o.getClass().getName());
    }

    public static boolean isBean(String n ) {
        return !n.startsWith("java");
    }

    public Map<String, Object> getProps() {
        return putProps( new LinkedHashMap<>(), "" );
    }

    private Map<String, Object> putProps(Map<String, Object> map, String prefix) {
        if (o == null)
            return map;

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
            List<PropertyDescriptor> props = asList(beanInfo.getPropertyDescriptors())
              .stream()
              .filter(pd -> !pd.getName().equals("class"))
              .collect(toList());
            for (PropertyDescriptor pd : props) {
                Object val = pd.getReadMethod().invoke(o);
                map.put(prefix + pd.getName(), val);
                if (isBean(o)) {
                    new Properties(val).putProps(map, pd.getName() + Semantics.PROPSEP);
                }
            }
            return map;
        } catch (InvocationTargetException | IllegalAccessException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Object get(String name) {
        return (T)getProps().get(name);
    }
}
