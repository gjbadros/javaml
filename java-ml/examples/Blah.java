public class Blah {

  public Object getObj () {
    return new Object() {
        final static int j = 2;
        int i;
        { i = 7; }
        
        public String toString() { return "i is " + i; }
      };
  }
  
  public void runshow() {
    Object foo = getObj();
    
    System.out.println("Foo is '" + foo + "'");
  }
  
  public static void main(String args[]) {
    new Blah().runshow();
  }
  
}
