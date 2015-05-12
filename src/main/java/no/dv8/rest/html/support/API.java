package no.dv8.rest.html.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class API {

    List<Endpoint> destinations = new ArrayList<>();

    public List<Endpoint> getStartDestinations() {
        return destinations
                .stream()
                .filter( e -> e.getTarget()==null )
                .collect( toList() );
    }

    public Optional<Endpoint> find(String rel) {
        return destinations
                .stream()
                .filter(e -> e.getRelationType().equals(rel) )
                .findFirst();
    }

    public List<Endpoint> find(String rel, String resourceType) {
        return destinations.stream()
                .filter( e -> rel.equals(e.getRelationType()) && resourceType.equals( e.getResourceType()))
                .collect(toList());
    }
}
