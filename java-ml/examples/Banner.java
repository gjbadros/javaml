/*----------------------------------------------------------------------*/
/*  Banner -- animates text into a box 
           **based on James Gosling's DancingText.java (in this dir)    */ 
/*----------------------------------------------------------------------*/
/* public variables
           fontName="TimesRoman36bi"
           style=..0f-7 means {none, starburst, stage right shuffle, 
                    stage left shuffle , slide in from right, 
                    reverse order, alternate sides, random}  
           time=4000 ..time it takes to finsih animation 
           sound= the URL of a sound (played after animation)
           delay= time between frames (50 is default)  
           pause= pause before the animation starts
           amplitude=36 ..amplitude of the wave in pixels 
           length=1 .. a multiplier for the wave length
           trans=15 ..translates the wave--use to get the right end 
                    position   
           foreground=java.awt.Color.blue  
           background=java.awt.Color.lightGray  
           box = true ..draw a box around the text 
           centre = true ..centre text           
           y_space = 1 ..vertical spacing more thanfont size().height
           x,y = position in window
           xborder, yborder = blank space around the window (text formating)
                                                                        */
/*----------------------------------------------------------------------*/
/* If you want to do some custom animation for the text I have marked 
     easy spots for customization with "**" in the comments (namely 
     at the initial position, timing function (I like sqrt), and 
     the algorithm (this one is a linear combination of the initial and 
     the final positions with a wave in the y direction)                */
/*----------------------------------------------------------------------*/
/*  I left the original audio code in (commented out) but I can't use it 
    since I don't have a speaker                                        */
/*----------------------------------------------------------------------*/
/*               Jim Morey  -  morey@math.ubc.ca  - Aug 5               */
/*----------------------------------------------------------------------*/
  
import java.io.InputStream;
import java.awt.*;
import java.net.*;
import java.applet.*;

/*----------------------------------------------------------------------*/

class Banner implements Runnable{
  int x,y,time,delay,pause,style,amplitude,trans,xborder,yborder,y_space=1;
  float length;
  Color foreground = null, background = null;
  String audioName;
  String fontName;
  boolean fetchingAudio, playRequested, ready=false, keep_going, box;
  boolean centre;

  private long t0;
  private int x0[],xd[],y0[],yd[],W,H,len,xlen,ylen;
  private Graphics offscreen;
  private Image im;
  private Font thefont, font;
  private java.applet.Applet parent;
  private char text[];
  private AudioClip ad;

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  Banner(java.applet.Applet parent,int wid, int heig) {
    this.parent = parent;
    time = 1000; 
    foreground = java.awt.Color.black;
    background = java.awt.Color.lightGray;
    delay = 50; 
    amplitude = 0; 
    trans = 0; 
    length = 5.0f;
    pause = 0; 
    style = 1; 
    x = 0; y = 0;
    xborder=0;  yborder=0;
    box = false;
    centre = false;

    W = wid;
    H = heig;

    im = parent.createImage(W,H);
    offscreen = im.getGraphics();
    offscreen.setColor(background);
    offscreen.fillRect(0, 0, W, H);
  }
  
  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  public void setup_words(Component comp, String s){
    /* .. format the final postions of the Banner .. */
    if (s == null) s = "nothing to say";
    text = new char[s.length() + 2];
    s.getChars(0, s.length(), text, 0);
    if (fontName == null) fontName = "TimesRoman36i";

    int fstyle = 0;
    int size = 0;
    int c = 0;
    int pos = fontName.length();
    while (pos > 0) {
      c = fontName.charAt(--pos);
      switch (c) {
        case 'b':
	  fstyle |= Font.BOLD;
          continue;
        case 'i':
	  fstyle |= Font.ITALIC;
          continue;
      }
      break;
    }
    int fac = 1;
    while ('0' <= c && c <= '9') {
      size += (c - '0') * fac;
      if (--pos <= 0) break;
      c = fontName.charAt(pos);
      fac = fac * 10;
    }
    if (size <= 0) size = 24;

    fontName = fontName.substring(0, pos + 1);
    thefont = new Font(fontName, fstyle, size);
    FontMetrics fm = comp.getFontMetrics(thefont);
    if (thefont == null) thefont = font;
    else font = thefont;

    len = text.length;
    x0 = new int[len];
    xd = new int[len];
    y0 = new int[len];
    yd = new int[len];

    int x_cur = 0;
    int y_cur = fm.getAscent(); 

    int word=0;
    boolean startword=true;

    if (box) {
      /* .. make room for the box unless other borders were picked .. */
      if (xborder == 0 && yborder ==0){
        xborder=4;  yborder=4;
      }
    }
  
    /* .. figure out where the text should end up .. */
    int xmax = 0;
    xlen = len;
    ylen = 1;
    for (int i = 0; i < len; i++) {
      xd[i] = x_cur;
      if (text[i] == ' ') {
        startword = true;
        if (x_cur>xmax) xmax = x_cur;
      }
      x_cur += fm.getWidths()[text[i]];
      yd[i] = y_cur;
      if (x_cur>W-2*xborder) {
        y_cur += (fm.getAscent() + y_space);
        x_cur = 0;
        xlen = i;
        i = word-1;
        ylen++;
      }
      if (startword && text[i] != ' ') {
        word = i;
        startword = false;
      }
    }

    if (x_cur>xmax) xmax = x_cur;

    if (centre){
      int dy = (H-y_cur)/2-yborder;
      int dx = (W-xmax)/2-xborder;
      for (int i = 0; i < len; i++) {
        xd[i] += dx;
        yd[i] += dy;
      }
    }
  }

  /* -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - */
  public void run(){
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

/*
   .. I can't play with this stuff since I don't have a speaker ..

    if (audioName != null && !fetchingAudio) {
      fetchingAudio = true;
      kicker = new Thread(this);
      kicker.start();
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
      ad = getAudioData(audioName);
      if (playRequested) play(getCodeBase(), ad);
      return;
    }
    if (ad != null)
      play(getCodeBase(), ad);
    else
      playRequested = true;
*/

    /* .. get a blank window to draw the text .. */
    offscreen.setFont(thefont); 
    offscreen.setColor(background);
    offscreen.fillRect(0, 0, W, H);
    offscreen.setColor(foreground);

    if (box) offscreen.drawRect(1, 1, W-3, H-3);

    /* .. **there is a lot of room to have new start positions .. */
    switch (style) {
      default:
      case 0: /* .. still .. */
	for (int i = 0; i < len; i++) { x0[i] = xd[i];  y0[i]=yd[i];}
	break;
      case 1: /* .. below and away  .. */
        for (int i = 0; i < len; i++) { x0[i] = W/2;  y0[i]=H*2;}
        break;
      case 2: /* .. stretch from the right .. */
	for (int i = 0; i < len; i++) { x0[i] = W;  y0[i]=yd[i]; }
	break;
      case 3: /* .. stretch form the left .. */
	for (int i = 0; i < len; i++){ x0[i] = 0;  y0[i]=yd[i]; }
        break;
      case 4: /* .. slide in from the right .. */
	for (int i = 0; i < len; i++){ x0[i] = xd[i] + W;  y0[i]=yd[i]; }
	break;
      case 5: /* .. in 2 dimension this one is not too predictable .. */ 
	for (int i = 0; i < len; i++){ x0[i] = xd[len - i - 1];  y0[i]=yd[i]; }
	break;
      case 6: /* .. stretch from both sides .. */
	for (int i = 0; i < len; i++){ x0[i] = (i & 1) == 0 ? W : 0;  y0[i]=yd[i]; }
	break;
      case 7: /* .. random .. */
	for (int i = 0; i < len; i++){ x0[i] = (int) (W * Math.random());  y0[i]=(int) (H*Math.random()); }
	break;
    }

    t0 = System.currentTimeMillis();
    float wave;

    keep_going=true;
    while (keep_going) {
      ready = false;
      offscreen.setColor(background);
      offscreen.fillRect(0, 0, W, H);
      offscreen.setColor(foreground);

      if (box) offscreen.drawRect(1, 1, W-3, H-3);

      long t = System.currentTimeMillis() - t0;
      wave = 1.0f;
      if (t > time) keep_going=false;
      /* .. **lots of possibilities for timing functions  .. */
      else wave = (float) Math.sqrt((double)t/(double)time);

      int lim = text.length - 2;
      for (int i = 0; i < lim; i++) {
        /* .. **lots of possibilities for algorthims too .. */
        offscreen.drawChars(text, i, 1  
          , (int) (wave*xd[i] + (1-wave)*x0[i] +xborder)  
          , (int) (wave*yd[i] + (1-wave)*y0[i] +yborder) 
         +(int)(amplitude*Math.sin(3.14f* (xd[i]/length+wave*100+trans)/16)) );
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
