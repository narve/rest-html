package no.dv8.rest.html.support.rest;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Slf4j
public class GenericJSONStorage<T> {

    public static final String baseDir = "data/storage";
    private static Map<Class<?>, GenericJSONStorage<?>> pool = new HashMap<>();
    public final Class<T> clz;
    private Set<T> items = new HashSet<>();

    public GenericJSONStorage(Class<T> clz) {
        this.clz = clz;
    }

    public static final <T> GenericJSONStorage<T> getOrCreate(Class<T> clz) {
        return (GenericJSONStorage<T>) pool.getOrDefault(clz, create(clz));
    }

    public static final <T> GenericJSONStorage<T> create(Class<T> clz) {
        GenericJSONStorage<T> res = new GenericJSONStorage<>(clz);
        try {
            res.items = res.load();
            log.info( "For " + clz + ", loaded " + res.all().size() + " items");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public Set<T> all() {
        return items;
    }

    public T add(T item) {
        items.add(item);
        save();
        return item;
    }

    public T remove(T item) {
        items.remove(item);
        save();
        return item;
    }

    public T update(T t) {
        items.remove(t);
        items.add(t);
        save();
        return t;
    }

    public Set<T> searchSubstring(String pname, String term) {
        return new HashSet<>(
          all()
            .stream()
            .filter(t -> matchSubstring(t, pname, term))
            .collect(toList())
        );
    }


    public Set<T> searchExact(String pname, String term) {
        return new HashSet<>(
          all()
            .stream()
            .filter(t -> matchExact(t, pname, term))
            .collect(toList())
        );
    }

    private boolean matchExact(T t, String pname, String value) {
        Map<String, Object> props = new no.dv8.rest.html.support.reflect.Properties(t).getProps();
        return props.containsKey(pname) && props.get(pname).equals(value);
    }

    private boolean matchSubstring(T t, String pname, String value) {
        Map<String, Object> props = new no.dv8.rest.html.support.reflect.Properties(t).getProps();
        return props.containsKey(pname) && props.get(pname).toString().toUpperCase().contains(value.toUpperCase());
    }


    public Set<T> save() {
        String s = builder()
          .create()
          .toJson(items);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file(clz)));
            bw.write(s);
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        log.info("Saved " + items.size() + " items");
        return items;
    }

    private GsonBuilder builder() {
        return new GsonBuilder();
    }

    private Reader reader(Class<T> clz) throws IOException {
        try {
            return new BufferedReader(new FileReader(file(clz)));
        } catch (FileNotFoundException e) {
            file(clz).createNewFile();
            return reader(clz);
        }
    }

    private File file(Class clz) {
        if (!new File(baseDir).exists()) {
            boolean mkdirs = new File(baseDir).mkdirs();
            log.info("Created dir " + new File(baseDir).getAbsolutePath() + ": " + mkdirs);
        }
        return new File(baseDir, clz.getName() + ".json");
    }

    public Set<T> load() throws IOException {
        log.info("Loading objects for " + clz);
        T[] items = (T[])builder()
          .create()
          .fromJson(reader(clz), Array.newInstance(clz, 1).getClass());
        return items == null ? new HashSet<>() : new HashSet(asList(items));
    }


//    public T getSingleByProperty( String prop, String value ) {
//        return all().stream().filter( )
//    }

}
