import java.applet.*;   // do not forget this import statement!
import java.awt.*;      // Or this one for the graphics!

public class AnotherApplet extends Applet {
  public void Labels() {
    StartMethod("Labels");
  lab: {
  }

  lab2: while (true) {
    break lab2;
  }
  }


  public void Quoting() {
    String s1 = "\"";
    String s2 = "'";
    String s3 = "<";
    String s4 = "&";
    String s5 = ">";
    char c1 = '"';
    char c2 = '\'';
    char c3 = '<';
    char c4 = '&';
    char c5 = '>';
    System.err.println("method entered");
  }

  public void if_foo() {
    if (false)
      while (true);
  }

  public void if2_foo() {
    if (false) {
      while (true);
    }
  }
  public void foo() {
    while (true);
  }
  public void Foo() {
    int a;
    while (true);
  }
  public void bar() {
    do {
    } while (true);
  }
  public void Bar() {
    int a;
    do {
    } while (true);
  }
  public void baz() {
    for (;;) {
    }
  }
  public void Baz() {
    int a;
    for (;;) {
    }
  }

  public void sw() {
    switch (3) {
    default:
    case 1:
      int a;
      break;
    case 2:
      int b;
    }
  }


  public int foo_ret() {
    while (false);
    return 1;
  }
  public int Foo_ret() {
    int a;
    while (false);
    return 2;
  }
  public int bar_ret() {
    do {
    } while (false);
    return 3;
  }
  public int Bar_ret() {
    int a;
    do {
    } while (false);
    return 4;
  }
  public int baz_ret() {
    for (;false;) {
    }
    return 5;
  }
  public int Baz_ret() {
    int a;
    for (;false;) {
    }
    return 6;
  }
}
