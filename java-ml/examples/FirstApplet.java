import java.applet.*;   // do not forget this import statement!
import java.awt.*;      // Or this one for the graphics!


public class FirstApplet extends Applet {
  // this method displays the applet.
  // the Graphics class is how you do all the drawing in Java
  public void paint(Graphics g) {
    g.drawString("Hello World", 25, 50);
  }
}
