// Sideways star field java.applet.Applet
// Arguments: NUMSTARS , X , Y (X and Y are java.applet.Applet size)
// Michael Ewert : morgansun@pinc.com

import java.awt.*;
import java.lang.*;

class star {
   int x,y,z; 
   int xa,ya;

   star(int i, int j, int k){
	x = i;
	y = j;
	z = k;
	xa = i;
	ya = j;
   }
         
}

public class stars extends java.applet.Applet implements Runnable {
   boolean first = true;
   int MAXX = 400;
   int MAXY = 100;
   final int MAXZ = 200;
   int numStars;
   star starA[];   
   java.awt.Color colorA[] = new java.awt.Color[MAXZ];
 
   Thread kicker = null;

   public void init(){

	String arg = getParameter("NUMSTARS");
	numStars = (arg != null) ? Integer.valueOf(arg).intValue() :  25;
	starA = new star[numStars];
        arg = getParameter("X");
	MAXX = (arg != null) ? Integer.valueOf(arg).intValue() : MAXX;
	arg = getParameter("Y");
	MAXY = (arg != null) ? Integer.valueOf(arg).intValue() : MAXY;
	resize(MAXX,MAXY);
	for(int i = 0 ; i < numStars ; i++) {
	   starA[i] = new star((int)(java.lang.Math.random()*MAXX),
       		(int)(java.lang.Math.random()*MAXY),
		(int)(java.lang.Math.random()*(MAXZ-1)));
	}
	for(int i = 1 ; i <= MAXZ ; i++) colorA[i-1] = new Color(256-i,256-i,256-i);

   }

   public void start(){
	if (kicker == null) {
	   kicker = new Thread(this);
           kicker.setPriority(kicker.MIN_PRIORITY);

	   kicker.start();
	}
   }

   public void stop(){
	kicker = null;
   }

   public void paint(Graphics g){
	
	  g.setColor(Color.black);
	  g.fillRect(0,0,MAXX,MAXY);
	  first = false;
   }	
   public void update(Graphics g) {

	for(int i = 0 ; i < numStars ; i++){
	   int z = starA[i].z;
	   starA[i].xa = starA[i].x;
	   starA[i].ya = starA[i].y;
	   int dx = (int)(9.*((200. - (double)z)/200.));  
	   starA[i].x += dx+1;

           if(starA[i].x > 6000){
	   starA[i] = new star(0,(int)(java.lang.Math.random()*MAXY),
			  (int)(java.lang.Math.random()*MAXZ));
	   }	
	   if(starA[i].x > MAXX) 
		starA[i].x = 6000;

         }		

	for(int i = 0 ; i < numStars ; i++){
	   int xa = starA[i].xa;
	   int ya = starA[i].ya;
	   int x = starA[i].x;
	   int y = starA[i].y;
	   int z = starA[i].z;
	   g.setColor(Color.black);
	   g.drawLine(xa,ya,xa+1,ya+1);
	   g.drawLine(xa+1,ya,xa,ya+1);		   

	   g.setColor(colorA[z]);
	   g.drawLine(x,y,x+1,y+1);
	   g.drawLine(x+1,y,x,y+1);	
	   g.setColor(Color.black);
	}
	if(first) paint(g);
}
   public void run() {
      while(kicker != null){
	repaint();
	try {kicker.sleep(25);} catch (InterruptedException e) {}
      }
   
}
   public boolean mouseDown(java.awt.Event evt, int x, int y) {
	first = true;
	repaint();
	return true;
   }

}
