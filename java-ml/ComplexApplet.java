package foo;

import java.applet.*;   // do not forget this import statement!
import java.awt.*;      // Or this one for the graphics!

public abstract class ComplexApplet extends Applet {

  ComplexApplet(int c) {
    afield = c;
  }

  // this method displays the applet.
  // the Graphics class is how you do all the drawing in Java
  public void paint(Graphics g) {
    int i = 64, j;
    g.drawString("Hello World", 25, 50);
    System.err.println(Math.sqrt(49));
    System.err.println(Math.sqrt(i));
    System.err.println(r[1]);
    tgt().toString();
  }
  public abstract int foo(Graphics g);

  public Object tgt() {
    for (int i = 0; i < 10; ++i);
    for (int i = 0; i < 10; ++i) {
      System.out.println(i);
    }
    return o;
  }

  private int wacky(final Graphics g) {
    return 5;
  }

  protected final int bar() {
    return 1 + 2 + 3;
  }
  double baz(int i) throws Exception {
    throw new Exception();
  }
  Object o;
  Object[] r;
  public int afield;
  double anotherfield;
  static int count;
}
