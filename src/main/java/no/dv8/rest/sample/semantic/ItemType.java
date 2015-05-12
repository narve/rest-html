package no.dv8.rest.sample.semantic;

public @interface ItemType {
    String value() default "";
    String href();
}
