package no.dv8.rest.html.support.rest;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;

public class InstantAdapter extends XmlAdapter<String, Instant> {
    @Override
    public Instant unmarshal(String v) throws Exception {
        return java.time.Instant.parse(v);
    }

    @Override
    public String marshal(Instant v) throws Exception {
        return v.toString();
    }
}
