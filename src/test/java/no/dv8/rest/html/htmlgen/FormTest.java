package no.dv8.rest.html.htmlgen;

import lombok.ToString;
import org.junit.Test;

import static org.junit.Assert.*;

public class FormTest {


    public static enum TestEnum {
        A, B, C;
    }

    @ToString
    public static class Inner {
        String string;
        public String getString() {return string;}
        public void setString( String string ) {
            this.string = string;
        }

        TestEnum testEnum;

        public TestEnum getTestEnum() {
            return testEnum;
        }

        public void setTestEnum(TestEnum testEnum) {
            this.testEnum = testEnum;
        }

    }

    @ToString
    public static class Outer {
        Inner inner;

        public Inner getInner() {
            return inner;
        }
        public void setInner(Inner i) {
            inner = i;
        }
    }

    @Test
    public void testSample() throws Exception {
        Outer o = (Outer) new Form(null, null).sample(Outer.class.getName());
        //fail( ""+o);
        assertNotNull( o);
        assertNotNull( o.getInner());
        assertNotNull( "enum in " + o, o.getInner().getTestEnum() );
    }

}