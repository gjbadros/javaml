import java.applet.*;   // do not forget this import statement!

// From Chart.java
public class BigInit extends Applet {
  public String[][] getParameterInfo() {
    String[][] info = {
      {"title", "string", "The title of bar graph.  Default is 'Chart'"},
      {"scale", "int", "The scale of the bar graph.  Default is 10."},
      {"columns", "int", "The number of columns/rows.  Default is 5."},
      {"orientation", "{VERTICLE, HORIZONTAL}", "The orienation of the bar graph.  Default is VERTICLE."},
      {"c#", "int", "Subsitute a number for #.  The value/size of bar #.  Default is 0."},
      {"c#_label", "string", "The label for bar #.  Default is no label."},
      {"c#_style", "{SOLID, STRIPED}", "The style of bar #.  Default is SOLID."},
      {"c#_color", "{RED, GREEN, BLUE, PINK, ORANGE, MAGENTA, CYAN, WHITE, YELLOW, GRAY, DARKGRAY}", "The color of bar #.  Default is GRAY."}
    };
    return info;
  }
}
