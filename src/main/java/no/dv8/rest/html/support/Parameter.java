package no.dv8.rest.html.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameter implements Cloneable{
    boolean required;
    boolean readOnly;
    String type;
    String name;
    String description;
    String placeholder;
    Object value;
    String jaxrsType;
    String inputType;
    public Parameter clone() {
        try {
            return (Parameter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Not possible");
        }
    }
}
