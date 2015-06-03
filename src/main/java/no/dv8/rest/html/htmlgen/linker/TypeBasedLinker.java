package no.dv8.rest.html.htmlgen.linker;

import no.dv8.rest.html.support.Endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeBasedLinker implements Linker {

    Map<String, Linker> typeMap = new HashMap<>();

    @Override
    public List<Endpoint> links(Object o) {
        Linker l = typeMap.get(typeStr(o));
        return l.links(o);
    }

    public String typeStr(Object o) {
        if( o == null ) {
            return null;
        }
        return o.getClass().getTypeName();
    }
}
