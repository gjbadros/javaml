public class OuterClassBad {
  static class StaticInnerClass
  {
    public StaticInnerClass() { 
      System.out.println("static inner class created."); 
    }
  }
  
  public static void main (String[] args){
    StaticInnerClass sio = new OuterClass().new StaticInnerClass();
  }
}
