/*----------------------------------------------------------------------*/
/* A demonstration of a geometrical proof                               */
/*----------------------------------------------------------------------*/
/* Pythagoras - is the main object that controls all the events for this 
                Pythogorean Proof display
           - it extensively uses Banner for all text (buttons and the
             step explanations) 
   diagram - handles all the picture steps of the proofs (some are 
             animated)                                                  */
/*----------------------------------------------------------------------*/
/* To remove a lot of flicker problems all of the drawing occurs off    */
/* the screen.  Also if a process calls for a redraw when another is    */
/* drawing the process says skip me in that redraw.                     */
/*----------------------------------------------------------------------*/
/*               Jim Morey  -  morey@math.ubc.ca  - Aug 5               */
/*----------------------------------------------------------------------*/
  
import java.io.InputStream;
import java.awt.*;
import java.net.*;
import Banner;
import fillTriangle;

/*----------------------------------------------------------------------*/

class Diagram implements Runnable{
  Color foreground,background;
  int x,y,time,pause,delay,step,a,b,mousex,mousey;
  boolean keep_going;
 
  private static double pi = 3.1415295f;
  private int W,H,xb,yb;
  private Graphics offscreen;
  private Image im;
  private java.applet.Applet parent;
  private boolean ready;
  private float scaling;

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  Diagram(java.applet.Applet parent,int wid, int heig) {
    this.parent = parent; 
    time = 1000; 
    foreground = java.awt.Color.black;
    background = java.awt.Color.white;
    delay = 50; 
    x = 0; y = 0;
    step = 0;
    pause = 0;

    W = wid;
    H = heig;
    a = 70;
    b = 150;
    /* .. set xb,yb at the upper corner of the triangle .. */
    xb = (W - 2*b - a)/2+b;
    yb = (H - 2*a - b)/2+a;
    mousex = a+x+xb; 
    mousey = b+y+yb;

    im = parent.createImage(W,H);
    offscreen = im.getGraphics();
    offscreen.setColor(background);
    offscreen.fillRect(0, 0, W, H);
    ready = true;
  }

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  public void run(){
    fillTriangle b_triangle, a_triangle, init_triangle;
    boolean three_squares = true, b_tri=false,a_tri=false,line=false;
    boolean init_tri=true,vert=false;
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    
    b_triangle = new fillTriangle(offscreen);
    a_triangle = new fillTriangle(offscreen);
    init_triangle = new fillTriangle(offscreen);
    int c2 = a*a+b*b;

    init_triangle.vect[0][0] = xb;
    init_triangle.vect[0][1] = yb;
    init_triangle.vect[1][0] = xb;
    init_triangle.vect[1][1] = yb+b;
    init_triangle.vect[2][0] = xb+a;
    init_triangle.vect[2][1] = yb+b;

    b_triangle.vect[0][0] = xb-b;
    b_triangle.vect[0][1] = yb;
    b_triangle.vect[1][0] = xb;
    b_triangle.vect[1][1] = yb;
    b_triangle.vect[2][0] = xb-b;
    b_triangle.vect[2][1] = yb+b;

    a_triangle.vect[0][0] = xb+a;
    a_triangle.vect[0][1] = yb+a+b;
    a_triangle.vect[1][0] = xb+a;
    a_triangle.vect[1][1] = yb+b;
    a_triangle.vect[2][0] = xb;
    a_triangle.vect[2][1] = yb+a+b;

    offscreen.setColor(background);
    offscreen.fillRect(0, 0, W, H);
    offscreen.setColor(foreground);

    long t0 = System.currentTimeMillis()+pause;
    keep_going = true;
    while(keep_going){
      long t = System.currentTimeMillis() - t0;
      double done =(double)(t)/(double)time; 
      if (t>time) {
        keep_going = false;
        done = 1.0f;
      }
      if (t<0)  done = 0.0f;
      
      /* .. break done the steps .. */

      switch (step){
        default:
        case 0:  /* .. let user define the triangle .. */
          vert = true;
          a = mousex-x-xb;
          b = mousey-y-yb;
          init_triangle.vect[0][0] = xb;
          init_triangle.vect[0][1] = yb;
          init_triangle.vect[1][0] = xb;
          init_triangle.vect[1][1] = yb+b;
          init_triangle.vect[2][0] = xb+a;
          init_triangle.vect[2][1] = yb+b;
          three_squares = false;
          keep_going = false;
          break;
        case 1:  /* .. centre and scale the triangle and add squares .. */
          if (b<0) b*=-1;
          if (a<0) a*=-1;
          xb = (W - 2*b - a)/2+b;
          if (xb < b) {
            scaling = (float)(2*b+a+10)/(float)W;
            a = (int)(a/scaling);
            b = (int)(b/scaling);
            xb = (W - 2*b - a)/2+b;
          }
          yb = (H - 2*a - b)/2+a;
          if (yb < a) {
            scaling = (float)(2*a+b+10)/(float)H;
            a = (int)(a/scaling);
            b = (int)(b/scaling);
            xb = (W - 2*b - a)/2+b;
            yb = (H - 2*a - b)/2+a;
          }

          init_triangle.vect[0][0] = xb;
          init_triangle.vect[0][1] = yb;
          init_triangle.vect[1][0] = xb;
          init_triangle.vect[1][1] = yb+b;
          init_triangle.vect[2][0] = xb+a;
          init_triangle.vect[2][1] = yb+b;
          keep_going = false;
          break;
        case 2: /* .. put on half the squares .. */
          b_tri = true;
          keep_going = false;
          a_tri = true;
          break;
        case 3: /* .. sheer the blue triangle over to the hypotenuse .. */
          b_tri = true;
          b_triangle.vect[2][0] = xb-b + (int)(done*(a+b));
          a_tri = true;
          break;
        case 4: /* .. rotate it out of it's original square .. */
          b_tri = true;
          b_triangle.vect[2][0] = xb + (int)(b*Math.sin(pi/2*done)+a*Math.cos(pi/2*done));
          b_triangle.vect[2][1] = yb + (int)(-a*Math.sin(pi/2*done)+b*Math.cos(pi/2*done));
          b_triangle.vect[0][0] = xb + (int)(-b*Math.cos(pi/2*done));
          b_triangle.vect[0][1] = yb + (int)(+b*Math.sin(pi/2*done));
          a_tri = true;
          break;
        case 5:  /* .. draw in the hieght .. */
          b_tri = true;
          b_triangle.vect[2][0] = xb +b;
          b_triangle.vect[2][1] = yb-a;
          b_triangle.vect[0][0] = xb;
          b_triangle.vect[0][1] = yb +b;
          line = true;
          a_tri = true;
          keep_going = false;
          break;
        case 6: /* .. sheer it into the large square .. */
          b_tri = true;
          b_triangle.vect[2][0] = xb +b;
          b_triangle.vect[2][1] = yb-a;
          b_triangle.vect[0][0] = xb  +(int)(done*(b+(b*b*a)/c2));
          b_triangle.vect[0][1] = yb +b -(int)(done*(a+(a*a*b)/c2));
          line = true;
          a_tri = true;
          break;
        case 7: /* .. sheer the green triangle over to the hypotenuse .. */
          b_tri = true;
          b_triangle.vect[2][0] = xb +b;
          b_triangle.vect[2][1] = yb-a;
          b_triangle.vect[0][0] = xb +b +(b*b*a)/c2;
          b_triangle.vect[0][1] = yb-a+b -(a*a*b)/c2;
          line = true;
          a_tri = true;
          a_triangle.vect[2][1] =  yb+a+b -(int)(done*(a+b));
          break;
        case 8: /* .. rotate it out of it's original square .. */
          b_tri = true;
          b_triangle.vect[2][0] = xb +b;
          b_triangle.vect[2][1] = yb-a;
          b_triangle.vect[0][0] = xb +b +(b*b*a)/c2;
          b_triangle.vect[0][1] = yb -a+b -(a*a*b)/c2;
          line = true;
          a_tri = true;
          a_triangle.vect[2][0] = xb+a+ (int)(b*Math.sin(pi/2*done)-a*Math.cos(pi/2*done));
          a_triangle.vect[2][1] = yb+b+ (int)(-a*Math.sin(pi/2*done)-b*Math.cos(pi/2*done));
          a_triangle.vect[0][0] = xb+a+ (int)(-a*Math.sin(pi/2*done));
          a_triangle.vect[0][1] = yb+b+ (int)(+a*Math.cos(pi/2*done));
          break;
        case 9: /* .. sheer it into the large square .. */
          b_tri = true;
          b_triangle.vect[2][0] = xb +b;
          b_triangle.vect[2][1] = yb-a;
          b_triangle.vect[0][0] = xb +b +(b*b*a)/c2;
          b_triangle.vect[0][1] = yb-a+b -(a*a*b)/c2;
          line = true;
          a_tri = true;
          a_triangle.vect[2][0] = xb+b+a;
          a_triangle.vect[2][1] = yb-a+b;
          a_triangle.vect[0][0] = xb+ (int)(done*(b+(b*b*a)/c2));
          a_triangle.vect[0][1] = yb+b -(int)(done*(a+(a*a*b)/c2));
          break;
        case 10: /* .. leave everything .. */
          b_tri = true;
          b_triangle.vect[2][0] = xb +b;
          b_triangle.vect[2][1] = yb-a;
          b_triangle.vect[0][0] = xb +b +(b*b*a)/c2;
          b_triangle.vect[0][1] = yb-a+b -(a*a*b)/c2;
          line = true;
          a_tri = true;
          a_triangle.vect[2][0] = xb+b+a;
          a_triangle.vect[2][1] = yb-a+b;
          a_triangle.vect[0][0] = xb+ (b+(b*b*a)/c2);
          a_triangle.vect[0][1] = yb+b- (a+(a*a*b)/c2);
          keep_going = false;
          break;
      }
 
      /* .. draw the stuff .. */

      ready = false;  /* .. to prevent flick .. */
      offscreen.setColor(background);
      offscreen.fillRect(0, 0, W, H);
      offscreen.setColor(foreground);
      if (init_tri){
        offscreen.setColor(Color.lightGray);
        init_triangle.run();
        offscreen.setColor(foreground);
      }
      if (b_tri){
        offscreen.setColor(Color.blue);
        b_triangle.run();
        offscreen.setColor(foreground);
      }
      if (a_tri){
        offscreen.setColor(Color.green);
        a_triangle.run();
        offscreen.setColor(foreground);
      }
      if (three_squares){
        offscreen.drawRect(xb-b, yb, b, b);
        offscreen.drawRect(xb, yb+b, a, a);
        offscreen.drawLine(xb, yb, xb+b, yb-a);
        offscreen.drawLine(xb+b+a, yb-a+b, xb+b, yb-a);
        offscreen.drawLine(xb+b+a, yb-a+b, xb+a, yb+b);
        offscreen.drawLine(xb, yb, xb+a, yb+b);
      }
      if (line){
        offscreen.setColor(Color.red);
        offscreen.drawLine(xb, yb+b, xb+b+(b*b*a)/c2, yb-a+b-(a*a*b)/c2);
        offscreen.setColor(foreground);
      }
      if (vert){
        offscreen.setColor(Color.red);
        offscreen.fillRect(xb+a-3, yb+b-3, 6,6);
        offscreen.setColor(foreground);
      }
      ready = true;  

      parent.repaint();
try {Thread.sleep(delay);} catch (InterruptedException e){}
    }
  }

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  public void paint(Graphics g) {
    if (ready) g.drawImage(im, x, y, null);
  }
}

/*----------------------------------------------------------------------*/
/* Pythagoras -- this runs the show.                                    */
/*----------------------------------------------------------------------*/

public class Pythagoras extends java.applet.Applet implements Runnable {
  private Banner caption, restart_but, next_but, back_but;
  private Diagram picture;
  private Thread kicker;
  private int delay = 100, step=0,nstep, steps = 10,pause;
  private boolean caption_ready=false, pic_ready=false;
  private boolean restart_ready=false, next_ready=false, back_ready=false;
  private int y_button, y_picture, y_caption, y_max=500;
  private int x_button1, x_button2, x_max=500;
  private int styles[] =     {3,7,1,5,0,6,2,4,5,3,4,7,7};
  private int amplitudes[] = {0,0,0,0,3,0,0,3,0,0,0,0,0};
  private String caps[] = { "Start with a generic right angled triangle.  Each time you click on the picture window the vertex labeled by the red square will be moved to the mouse position.  After you are done the choosing your triangle it will be centered (and possibly flipped so that the right angle is in the bottom left corner of the triangle)",
                     "Now we draw squares off each side of the triangle.  We need to show that the sum of the areas of the smaller squares equals the area of the large one -- Pythagoras' Theorem.",
                     "Instead of showing that the areas of the smaller squares add up to the area of the big one, we will show that half the sum of the areas of the small ones add up to half the area of the big one.",
                     "First we will move the blue area over to the big square.  Consider the top of the blue triangle the base, and the side is the height.  Since the area of the triangle only depends on the base and the height, we can move the bottom vertex without changing the area, provided the height stays fixed.  That movement is called a sheer.",
                     "Now we will rotate the triangle.  This also does not affect the area of the blue triangle.",
                     "Now if we consider the side of the blue triangle that shares a side with the big square as the base, the red line is the same height as the height of the triangle.",
                     "As before, we leave the height and base fixed and move, or sheer, the blue region into the big square (the area of the blue region is still equal to half of the area of the square it came from).",
                     "Similarly, we move the green region to the big square, first with a sheer,",
                     "then rotate,",
                     "and another sheer.",
                     "Since half of each of the rectangles that make up the big square are filled, we can conclude that the sum of the blue and green region equals half the big square; therefore, the sum of the areas of the small squares equals the area of the big square. QED"};

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  public void init() { 
    /* .. setup window layout .. */
    y_button=44; 
    y_picture=394;
    y_caption=y_max;
    x_button1=133;
    x_button2=x_max-233;

    try{
      pause = Integer.parseInt(getParameter("pause"));
    } catch(Exception e) { pause=1000; }
     
    resize(x_max,y_max);

    kicker = new Thread(this);
    new Thread(kicker).start();
  }

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  public void run(){
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
try {Thread.sleep(pause);} catch (InterruptedException e){}
    step = 0;

    caption = new Banner(this,x_max,y_caption-y_picture);
    caption.fontName = "TimesRoman16i";
    caption.x = 0;
    caption.y = y_picture;
    caption.foreground = java.awt.Color.black;
    caption.background = java.awt.Color.lightGray;
    caption.centre = true;
    caption.time = 1000; 
    caption.box = true;
    caption.y_space = 3;
    caption.style = styles[step];
    caption.amplitude = amplitudes[step]; 
    caption.setup_words(this, caps[step]);
    new Thread(caption).start();
    caption_ready = true; 

    restart_but = new Banner(this,x_max-x_button2,y_button);
    restart_but.fontName = "TimesRoman24b";
    restart_but.x = x_button2;
    restart_but.y = 0;
    restart_but.foreground = java.awt.Color.lightGray;
    restart_but.background = java.awt.Color.gray;
    restart_but.centre = true;
    restart_but.setup_words(this, "RESTART");
    restart_but.time = 500; 
    restart_but.box = true;
    restart_but.style = 3;
    new Thread(restart_but).start();
    restart_ready = true; 

    next_but = new Banner(this,x_button1,y_button);
    next_but.fontName = "TimesRoman24b";
    next_but.x = 0;
    next_but.y = 0;
    next_but.foreground = java.awt.Color.lightGray;
    next_but.background = java.awt.Color.gray;
    next_but.centre = true;
    next_but.setup_words(this, "NEXT");
    next_but.time = 500; 
    next_but.box = true;
    next_but.style = 2;
    new Thread(next_but).start();
    next_ready = true; 

    back_but = new Banner(this,x_button2-x_button1,y_button);
    back_but.fontName = "TimesRoman24b";
    back_but.x = x_button1;
    back_but.y = 0;
    back_but.foreground = java.awt.Color.lightGray; 
    back_but.background = java.awt.Color.gray;
    back_but.centre = true;
    back_but.setup_words(this, "BACK");
    back_but.time = 500; 
    back_but.box = true;
    back_but.style = 1; 
    new Thread(back_but).start();
    back_ready = true; 

    picture = new Diagram(this,x_max,y_picture-y_button);
    picture.x = 0;
    picture.y = y_button;
    picture.step = step;
    picture.pause = 1100;
    new Thread(picture).start();
    pic_ready = true; 
  }

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  public void paint(Graphics g) {
    update(g);
  }

  public void update(Graphics g) {
    /* .. these if statements only stop errors when starting .. */
    if (caption_ready) caption.paint(g);
    if (restart_ready) restart_but.paint(g);
    if (next_ready) next_but.paint(g);
    if (back_ready) back_but.paint(g);
    if (pic_ready) picture.paint(g);
  }

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  public boolean mouseDown(java.awt.Event evt, int x, int y) {
    /* .. respond to a mouse click in the Pythagoras window .. */
    boolean cap = false, pic = false;

    if (y<y_button){ /* .. a button was pressed .. */
      if (x<x_button1){ /* .. NEXT .. */
        new Thread(next_but).start();
        nstep= step +1;
        if (nstep > steps) nstep = step;
      } else if (x<x_button2){ /* .. BACK .. */
        new Thread(back_but).start();
        nstep= step -1;
        if (nstep < 0) nstep = step;
      } else{ /* .. RESTART .. */
        nstep = 0;
        step = 1; 
        new Thread(next_but).start();
        new Thread(back_but).start();
        new Thread(restart_but).start();
      }
    } else if (y<y_picture){ /* .. the picture was clicked on .. */
      picture.pause = 0;
      pic = true;
    } else { /* .. the caption was clicked on .. */
      cap = true;
    }

    if (nstep != step) { 
      cap = true;
      pic = true;
      picture.pause = 1100;
      step = nstep;
    }

    if (cap){  /*.. redraw the caption .. */
      caption.keep_going = false; 
try {Thread.sleep(delay*2);} catch (InterruptedException e){}
      caption.fontName = "TimesRoman16i";
      caption.style = styles[step];
      caption.amplitude = amplitudes[step]; 
      caption.setup_words(this, caps[step]);
      new Thread(caption).start();
    }
    if (pic){ /*.. restart the picture .. */
      picture.keep_going = false; 
try {Thread.sleep(delay*4);} catch (InterruptedException e){}
      picture.step = step;
      if (picture.pause==0){
        picture.mousex = x;
        picture.mousey = y;
      }
      new Thread(picture).start();
    }
    return true;
  }
}
