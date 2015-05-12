package no.dv8.rest.html.support.rest;

import lombok.extern.slf4j.Slf4j;

import no.dv8.rest.sample.semantic.Semantics;
import no.dv8.rest.html.support.Endpoint;
import no.dv8.rest.html.support.Parameter;

import javax.ws.rs.*;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Path("/items")
@Slf4j
public class GenericResource {

    private String thePackage;

    public static Collection<? extends Endpoint> genDests(Class clz) {
        return asList(endpointAll(clz), searchExact(clz), searchSubstring(clz), endpointCreate(clz));
    }

    public static Collection<? extends Endpoint> genDests() {
        return asList(String.class)
          .stream()
          .map(GenericResource::genDests)
          .flatMap(l -> l.stream())
          .collect(toList());
    }

    public static Endpoint endpointAll(Class clz) {
        try {
            Class javaClass = GenericResource.class;
            Method m = javaClass.getDeclaredMethod("all", String.class);
            String type = clz.getSimpleName();

            Endpoint e1 = Endpoint
              .builder()
              .description("GET all " + clz.getSimpleName())
              .javaClz(clz)
              .javaMethod(m)
              .relationType(Semantics.collectionOf(clz.getSimpleName()))
              .relativePath("/myapp/items/" + type)
              .parameters(asList())
              .methods(asList("GET"))
              .resourceType(clz.getSimpleName())
              .build();
            return e1;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Endpoint endpointCreate(Class<T> clz) {
        try {
            Class javaClass = GenericResource.class;
            Method m = javaClass.getDeclaredMethod("get", String.class, String.class, String.class);
            String type = clz.getSimpleName();

            Map<String, Object> props = new no.dv8.rest.html.support.reflect.Properties(clz.newInstance()).getProps();

            List<Parameter> params = props
              .entrySet()
              .stream()
              .map(e -> Parameter.builder().name(e.getKey()).value(e.getValue()).build())
              .collect(toList());

            Endpoint e1 = Endpoint
              .builder()
              .description("POST single " + clz.getSimpleName())
              .javaClz(clz)
              .javaMethod(m)
              .relationType(Semantics.collectionOf(clz.getSimpleName()))
              .relativePath("/myapp/items/" + type )
              .parameters(params)
              .methods(asList("POST"))
              .resourceType(clz.getSimpleName())
              .build();
            return e1;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Endpoint searchExact(Class clz) {
        try {
            Class javaClass = GenericResource.class;
            Method m = javaClass.getDeclaredMethod("searchExact", String.class, String.class, String.class);
            String type = clz.getSimpleName();
            Parameter typeParam = Parameter
              .builder()
              .name("type")
              .inputType("text")
              .value(type)
              .jaxrsType(PathParam.class.getSimpleName())
              .build();
            Parameter propParam = Parameter
              .builder()
              .name("property")
              .inputType("text")
              .jaxrsType(QueryParam.class.getSimpleName())
              .build();
            Parameter termParam = Parameter
              .builder()
              .name("value")
              .inputType("text")
              .jaxrsType(QueryParam.class.getSimpleName())
              .build();

            Endpoint e1 = Endpoint
              .builder()
              .description("Search " + type)
              .javaClz(clz)
              .javaMethod(m)
              .relationType(Semantics.collectionOf(type))
              .relativePath("/myapp/items/{type}/searchExact")
              .parameters(asList(typeParam, propParam, termParam))
              .methods(asList("GET"))
              .resourceType(type)
              .build();
            return e1;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
//
//    @GET
//    @Path("/{type}/searchSubstring")
//    public <T> T searchSubstring(
//      @PathParam("type") String type, @QueryParam("property") String property, @QueryParam("value") String value ) throws ClassNotFoundException {
//        Class<T> clz = (Class<T>) thePackage + "." + type);
//        GenericJSONStorage<T> storage = GenericJSONStorage.getOrCreate(clz);
//        Set<T> all = storage.searchSubstring(property, value);
//        log.info("For " + type + ", returning " + all.size() + " items");
//        return all;
//    }

    public static Endpoint searchSubstring(Class clz) {
        try {
            Class javaClass = GenericResource.class;
            Method m = javaClass.getDeclaredMethod("searchExact", String.class, String.class, String.class);
            String type = clz.getSimpleName();
            Parameter typeParam = Parameter
              .builder()
              .name("type")
              .inputType("text")
              .value(type)
              .jaxrsType(PathParam.class.getSimpleName())
              .build();
            Parameter propParam = Parameter
              .builder()
              .name("property")
              .inputType("text")
              .jaxrsType(QueryParam.class.getSimpleName())
              .build();
            Parameter termParam = Parameter
              .builder()
              .name("value")
              .inputType("text")
              .jaxrsType(QueryParam.class.getSimpleName())
              .build();

            Endpoint e1 = Endpoint
              .builder()
              .description("Search " + type)
              .javaClz(clz)
              .javaMethod(m)
              .relationType(Semantics.collectionOf(type))
              .relativePath("/myapp/items/{type}/searchSubstring")
              .parameters(asList(typeParam, propParam, termParam))
              .methods(asList("GET"))
              .resourceType(type)
              .build();
            return e1;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
//
//    public static Endpoint update(Class clz) {
//        try {
//            Class javaClass = GenericResource.class;
//            Method m = javaClass.getDeclaredMethod("update", String.class, String.class, String.class);
//            String type = clz.getSimpleName();
//            Parameter typeParam = Parameter
//              .builder()
//              .name("type")
//              .inputType("text")
//              .value(type)
//              .jaxrsType(PathParam.class.getSimpleName())
//              .build();
//            Parameter propParam = Parameter
//              .builder()
//              .name("property")
//              .inputType("text")
//              .jaxrsType(QueryParam.class.getSimpleName())
//              .build();
//            Parameter termParam = Parameter
//              .builder()
//              .name("value")
//              .inputType("text")
//              .jaxrsType(QueryParam.class.getSimpleName())
//              .build();
//
//            Endpoint e1 = Endpoint
//              .builder()
//              .description("Search " + type)
//              .javaClz(clz)
//              .javaMethod(m)
//              .relationType(Semantics.collectionOf(type))
//              .relativePath("/myapp/items/{type}/searchSubstring")
//              .parameters(asList(typeParam, propParam, termParam))
//              .methods(asList("GET"))
//              .resourceType(type)
//              .build();
//            return e1;
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @GET
    @Path("/{type}")
    public <T> Set<T> all(@PathParam("type") String type) throws ClassNotFoundException {
        Class<T> clz = (Class<T>) Class.forName( thePackage + "." + type);
        GenericJSONStorage<T> storage = GenericJSONStorage.getOrCreate(clz);
        Set<T> all = storage.all();
        log.info("For " + type + ", returning " + all.size() + " items");
        return all;
    }

    @GET
    @Path("/{type}/searchExact")
    public <T> Set<T> searchExact(
      @PathParam("type") String type, @QueryParam("property") String property, @QueryParam("value") String value) throws ClassNotFoundException {
        Class<T> clz = (Class<T>) Class.forName( thePackage + "." + type);
        GenericJSONStorage<T> storage = GenericJSONStorage.getOrCreate(clz);
        Set<T> all = storage.searchExact(property, value);
        log.info("For " + type + ", returning " + all.size() + " items");
        return all;
    }

    @GET
    @Path("/{type}/searchSubstring")
    public <T> Set<T> searchSubstring(
      @PathParam("type") String type, @QueryParam("property") String property, @QueryParam("value") String value) throws ClassNotFoundException {
        Class<T> clz = (Class<T>) Class.forName( thePackage + "." + type);
        GenericJSONStorage<T> storage = GenericJSONStorage.getOrCreate(clz);
        Set<T> all = storage.searchSubstring(property, value);
        log.info("For " + type + ", returning " + all.size() + " items");
        return all;
    }

    @GET
    @Path("/{type}/{property}/{value}")
    public <T> Object get(
      @PathParam("type") String type,
      @PathParam("property") String property,
      @PathParam("value") String value) throws ClassNotFoundException {
        Class<T> clz = (Class<T>) Class.forName( thePackage + "." + type);
        GenericJSONStorage<T> storage = GenericJSONStorage.getOrCreate(clz);
        Set<T> all = storage.searchSubstring(property, value);
        if (all.size() != 1) {
            throw new RuntimeException("UPS: " + all.size() + " matches for " + property + ", " + value);
        }
        log.info("For single get " + type + ", " + property + ", " + value + ": returning " + all.size() + " items");
        return all.iterator().next();
    }

    @POST
    @Path("/{type}")
    public <T> Object create( @PathParam("type") String type, MultivaluedMap<String, String> form) throws ClassNotFoundException {
        Class<T> clz = (Class<T>) Class.forName( thePackage + "." + type);
        GenericJSONStorage<T> storage = GenericJSONStorage.getOrCreate(clz);
        Set<T> all = storage.all();
        log.info("For POST " + type + ", form=" + form + ": returning " + all.size() + " items");
        return all.iterator().next();
    }

//
//    @POST
//    @Path("/{type}/{property}/{value}")
//    public <T> Object update(
//      @PathParam("type") String type, @PathParam("property") String property, @PathParam("value") String value) throws ClassNotFoundException {
//        Class<T> clz = (Class<T>) thePackage + "." + type);
//        GenericJSONStorage<T> storage = GenericJSONStorage.getOrCreate(clz);
//        Set<T> all = storage.searchSubstring(property, value);
//        if (all.size() != 1) {
//            throw new RuntimeException("UPS: " + all.size() + " matches for " + property + ", " + value);
//        }
//        log.info("For single get " + type + ", " + property + ", " + value + ": returning " + all.size() + " items");
//        return all.iterator().next();
//    }
//

}
