import java.util.*;

public class AnonymousClass {
  Object Foo() {
    System.err.println("foo");
    return new Object();
}

  Enumeration myEnumerate(final Object array[]) {
    return new Enumeration() {
        int count = 0;
        public boolean hasMoreElements()
        { return count < array.length; }
        public Object nextElement()
        { return array[count++]; }
      };
  }
}
