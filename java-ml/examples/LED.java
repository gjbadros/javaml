/////////////////////////////////////////////////////////////////////
//  appLED.java  -- LED Sign V1.0f
//
//  The main for the LED Sign applet.  This applet mimics
//  an LED sign that you typically see displaying messages
//  at airport terminals and the such.
//
//  Revisions:
//     V1.0f: Written July 10 - August 6, 1995
//
//  By Darrick Brown
//     dbrown@cs.hope.edu
//     http://www.cs.hope.edu/~dbrown/
//
//  © Copyright 1995
/////////////////////////////////////////////////////////////////////

package LED;

import java.awt.*;
import java.io.*;
import java.net.*;
import LED.*;

// Just a small struct
// used in randomizing the pixels in the "Pixel" function
class Pixelize
{
   int x;
   int y;
}

/////////////////////////////////////////////////////////////////////
// The java.applet.Applet!!
/////////////////////////////////////////////////////////////////////
public class LED extends java.applet.Applet implements Runnable
{
   // my #defines
   int WIDTH = 400;
   int HEIGHT = 30;
   Color highlight;
   
   Script com;                  // The class that takes care of the script
   FuncInfo fi;                 // All the info for any funtion/transition
   Letters let;                 // The class that contains all the letters

   Color colors[];              // The array of possible colors
   LEDMessage msg;              // The class that takes care of the message to be displayed
   Color bhilite;               // The highlight color of the border
   Color bcolor;                // The color of the border
   Color bshadow;               // The shadow of the border
   
   Thread led = null;
   String scrpt,endspace,fnt;   // "command line" arguments
   String text;                 // the current message
   int place;                   // The place where we are in each transition.  How we know when we are done.
   int border;                  // The border size().width
   int offset;                  // The offset for the sign from the upper left
   int w,h;                     // Width & Height in LEDs
   int swidth;                  // The size().width of the space character.  Settable in the HTML command line.
   boolean beginning;           // make sure we init certain stuff only once
   int delay = 40;              // The global delay variable
   boolean delayed = true;      // Just to make sure the "delay" in run() works
   Image pixmapimg,offimg,tmpimg;    // The pixmaps!!  -- These are what make this program possible
   Graphics pixmap,offmap,tmpmap;    // Graphics for the pixmaps
   Pixelize pix[] = new Pixelize[1]; // Just so it loads this class NOW!!
      

   //////////////////////////////////////////////////////////////////
   // get the "command line" arguments from the HTML
   private void getAttrs()
   {
      String s;
      int r,g,b;

      if(getParameter("script") !=  null)
      {
         scrpt = new String(getParameter("script"));
      }
      else
         System.out.println("NO SCRIPT SPECIFIED OR BAD URL");

      if(getParameter("font") !=  null)
      {
         fnt = new String(getParameter("font"));
      }
      else
         System.out.println("NO FONTS FOUND!!!!  WHERE'D THEY GO?!?!?!?");
      
      if(getParameter("spacewidth") !=  null)
      {
         swidth = (new Integer(new String(getParameter("spacewidth")))).intValue();
      }
      else
         swidth = 5;
         
      if(getParameter("size().width") !=  null)
      {
         WIDTH = 5*(new Integer(new String(getParameter("size().width")))).intValue();
      }
      else
         WIDTH = 60*5;
         
      if(getParameter("border") !=  null)
      {
         border = new Integer(new String(getParameter("border"))).intValue();
      }
      else
         border = 0;

      if(getParameter("bordercolor") != null)
      {
         // User specified border color!!
         s = new String(getParameter("bordercolor"));
         s = s.trim();
         r = new Integer(s.substring(0,s.indexOf(","))).intValue();
         s = s.substring(s.indexOf(",")+1);
         g = new Integer(s.substring(0,s.indexOf(","))).intValue();
         s = s.substring(s.indexOf(",")+1);
         b = new Integer(s).intValue();
         
         // Forgive the "if" syntax, I didn't want to bother typing the
         // "normal" ifs for this small part. :)
         bhilite = new Color(r+40<256?r+40:255, g+40<256?g+40:255, b+40<256?b+40:255);
         bcolor = new Color(r,g,b);
         bshadow = new Color(r-40>=0?r-40:0, g-40>=0?g-40:0, b-40>=0?b-40:0);
      }
      else
      {
         // The default gray
         bhilite = Color.white;
         bcolor = Color.lightGray;
         bshadow = Color.gray;
      }
         
   } // end getAttrs()

   //////////////////////////////////////////////////////////////////
   // Initialize the java.applet.Applet
   public void init()
   {
      // Set up the different colors for the sign
      highlight = new Color(100,100,100);
      colors = new Color[7];
      colors[0] = new Color(80,80,80);  // off color
      colors[1] = new Color(255,0,0);   // Default red
      colors[2] = new Color(130,255,0); // green
      colors[3] = new Color(0,100,255); // blue
      colors[4] = new Color(255,255,0); // yellow
      colors[5] = new Color(255,160,0); // orange
      colors[6] = new Color(255,0,255); // purple

      // The the attributes from the HTML doc
      getAttrs();

      try {
	  let = new Letters(getDocumentBase(),fnt,swidth);
      } catch (IOException e) {
	  e.printStackTrace();
      }
      HEIGHT = let.height();

      h = HEIGHT/5;  // size().height in LEDs
      w = WIDTH/5;   // size().width in LEDs

      msg = new LEDMessage(h,w,let);
      
      // Set up the script
      try {
	  com = new Script(getDocumentBase(),scrpt);
      } catch (IOException e) {
	  e.printStackTrace();
      }

      fi = new FuncInfo();
      nextFunc();
      
      offset = 3*border;
      resize(WIDTH+2*(offset),HEIGHT+2*(offset));  // Set the applet size
      beginning = true;

   }  // End Init

   //////////////////////////////////////////////////////////////////
   // Start the applet running and fork the thread
   public void start()
   {
      if(led == null)
      {
         led = new Thread(this);
         led.start();
      }
   }

   //////////////////////////////////////////////////////////////////
   public void stop()
   {
      led = null;
   }

   //////////////////////////////////////////////////////////////////
   public void run()
   {
      while(led != null)
      {
         repaint();

         // The following "if" is to make sure that the delay
         // function actually has an effect.  I found that it
         // tends to get skipped if the function takes too
         // much time (i.e. Pixel with certain delay settings)
         if(!delayed)
            delayed = true;

try {Thread.sleep(fi.delay);} catch (InterruptedException e){}
         if((fi.func == 1 && delayed) || fi.func >= 97 || fi.func < 0)
            nextFunc();
      }
   }

   //////////////////////////////////////////////////////////////////
   // set the next function
   // This function is only called when the previous
   // function/transition has finished.
   void nextFunc()
   {
      int i,j;
      Pixelize temp;
      int rand;

      fi = com.nextFunc();
      msg.setmsg(fi);

      // Set up some initial stuff for each of the transitions
      switch(fi.func)
      {
         case 0:
            place = 0;
            break;
         case 1:
            place = 0;
            delayed = false;
            break;
         case 2:
            place = 0;
            break;
         case 3:
            place = msg.length()-1;
            break;
         case 4:
            place = 0;
            break;
         case 5:
            place = h-1;
            break;
         case 6:
            place = 0;

            // This randomizes the "LEDs" for the
            // Pixel function.

            pix = new Pixelize[w*h];

            for(i=0;i<w;i++)
            {
               for(j=0;j<h;j++)
               {
                  pix[h*i+j] = new Pixelize();
                  pix[h*i+j].x = i;
                  pix[h*i+j].y = j;
               }
            }
            
            // Randomly sort all the LED's so all we have to do
            // is draw them in "order" and they come out all pixelly
            for(i=0;i<WIDTH/5*h;i++)
            {
                  rand = (int)(Math.random()*(double)(WIDTH/5)*(double)h);
                  temp = pix[i];
                  pix[i] = pix[rand];
                  pix[rand] = temp;
            }
            break;
         case 7:
            place = fi.times*2;  // on AND off
            break;
         case 8:
            place = 0;
            break;
         case 9:
            place = 0;
            break;
         case 10:
            place = 0;
            break;
         case 11:
            place = w;
            break;
         case 12:
            place = h-1;
            break;
         case 13:
            place = 0;
            break;
      }
   }

   //////////////////////////////////////////////////////////////////
   // Draw a pretty little LED
   private void drawLED(int x, int y, boolean on, int col, Graphics gr)
   {
      if(on)
      {
         gr.setColor(colors[col]);
         gr.fillRect(x+1,y,2,4);
         gr.fillRect(x,y+1,4,2);
      }
      else  // its off
      {
         gr.setColor(colors[0]);
         gr.fillRect(x+1,y,2,4);
         gr.fillRect(x,y+1,4,2);
         gr.setColor(highlight);
         gr.fillRect(x+1,y+1,1,1);  // the cool little highlight
      }
   }
         
   //////////////////////////////////////////////////////////////////
   // My version of paint3DRect (variable size().width) 
   void draw3DRect(Graphics gr, int x, int y, int lx, int ly, int width, boolean raised)
   {
      int i;

      for(i=0; i<width; i++)
      {
         if(raised)
            gr.setColor(bhilite);
         else
            gr.setColor(bshadow);
            
         gr.drawLine(x+i,y+i,lx-i,y+i);
         gr.drawLine(x+i,y+i,x+i,ly-i);
         
         if(raised)
            gr.setColor(bshadow);
         else
            gr.setColor(bhilite);
            
         gr.drawLine(lx-i,y+i,lx-i,ly-i);
         gr.drawLine(x+i,ly-i,lx-i,ly-i);
      }
   }

   //////////////////////////////////////////////////////////////////
   public void paint(Graphics gr)
   {
      int i,j;
      int p,p2;
      
      if(border > 0)
      {
         draw3DRect(gr,0,0,WIDTH+2*offset-1,HEIGHT+2*offset-1,border,true);
         gr.setColor(bcolor);
         gr.fillRect(border,border,WIDTH+4*border,HEIGHT+4*border);
         draw3DRect(gr,2*border,2*border,WIDTH+4*border-1,HEIGHT+4*border-1,border,false);
      }

      // If the applet has just start, set up the pixmap
      // and draw all the LEDs off
      if(beginning)
      {
         // Set up some pixmaps!
         pixmapimg = createImage(WIDTH, HEIGHT);
         offimg = createImage(WIDTH, HEIGHT);  // A copy of the sign with all the LEDs off
         tmpimg = createImage(WIDTH, HEIGHT);
         
         
         pixmap = pixmapimg.getGraphics();
         offmap = offimg.getGraphics();
         tmpmap = tmpimg.getGraphics();
         
         pixmap.setColor(Color.black);
         pixmap.fillRect(0,0,WIDTH,HEIGHT);

         offmap.setColor(Color.black);
         offmap.fillRect(0,0,WIDTH,HEIGHT);

         for(i=0;i<HEIGHT;i+=5)
            for(j=0;j<WIDTH;j+=5)
            {
               drawLED(j,i,false,1,pixmap);
               drawLED(j,i,false,1,offmap);
            }
               
         gr.drawImage(pixmapimg,offset,offset, this);
         
         beginning = false;
      }
      else if (pixmapimg != null) 
      {
         gr.drawImage(pixmapimg,offset,offset, this);
      }

   }


   //////////////////////////////////////////////////////////////////
   // This procedure contains all the different transitions
   // Each transition does one iteration and returns to the
   // "run" procedure to use its delay.  This also allows
   // the applet to be redrawn (if needed) more quickly.
   public void update(Graphics gr)
   {
      int i,j;
      int count;

      if (pixmap == null) {
	  return;
      }

      switch(fi.func)
      {
         case 0:  // Appear
            if(fi.text == null)
            {
               gr.drawImage(offimg,offset,offset, this);  // Turn all the LEDs off
            }
            else
            {
               for(i=0;i<w;i++)
                  for(j=0;j<h;j++)
                     drawLED(i*5,j*5,msg.getLED(i,j),msg.getColor(i),pixmap);

               gr.drawImage(pixmapimg,offset,offset, this);
            }

            nextFunc();
            
            break;

         case 2:  // ScrollLeft
            pixmap.copyArea(5,0,WIDTH-5,HEIGHT,-5,0);

            for(i=0;i<HEIGHT;i+=5)
               drawLED(WIDTH-5,i,msg.getLED(place,i/5),msg.getColor(place),pixmap);

            gr.drawImage(pixmapimg,offset,offset, this);

            place++;

            if(!msg.inRange(place))
               nextFunc();
               
            break;

         case 3:  // ScrollRight
            pixmap.copyArea(0,0,WIDTH-5,HEIGHT,5,0);

            for(i=0;i<HEIGHT;i+=5)
               drawLED(0,i,msg.getLED(place,i/5),msg.getColor(place),pixmap);

            gr.drawImage(pixmapimg,offset,offset, this);

            place--;

            if(place < 0)
               nextFunc();
               
            break;

         case 4:  // ScrollUp
            pixmap.copyArea(0,5,WIDTH,HEIGHT-5,0,-5);
            
            for(i=0;i<WIDTH;i+=5)
               if(msg.inRange(i/5))
                  drawLED(i,HEIGHT-5,msg.getLED(i/5,place),msg.getColor(i/5),pixmap);
               else
                  drawLED(i,HEIGHT-5,false,1,pixmap);

            gr.drawImage(pixmapimg,offset,offset, this);
            
            place++;

            if(place >= h)
               nextFunc();
               
            break;

         case 5:  // ScrollDown
            pixmap.copyArea(0,0,WIDTH,HEIGHT-5,0,5);
            
            for(i=0;i<WIDTH;i+=5)
               if(msg.inRange(i/5))
               {
                  drawLED(i,0,msg.getLED(i/5,place),msg.getColor(i/5),pixmap);
               }
               else
               {
                  drawLED(i,0,false,1,pixmap);
               }

            gr.drawImage(pixmapimg,offset,offset, this);
            
            place--;

            if(place < 0)
               nextFunc();

            break;

         case 6: // Pixel
            i = place + fi.times;
            while(place < WIDTH/5*h && place < i)
            {
               if(msg.inRange(pix[place].x))
               {
                  drawLED(pix[place].x*5,pix[place].y*5,msg.getLED(pix[place].x,pix[place].y),msg.getColor(pix[place].x),pixmap);
               }
               else
               {
                  drawLED(pix[place].x*5,pix[place].y*5,false,1,pixmap);
               }

               place++;
            }
            gr.drawImage(pixmapimg,offset,offset, this);
            
            if(place >= w*h)
               nextFunc();
            
            break;
            
         case 7:  // Blink
            if(place%2 == 0)
               gr.drawImage(offimg,offset,offset, this);
            else
               gr.drawImage(pixmapimg,offset,offset, this);

            place--;

            if(place == 0)
               nextFunc();

            break;

         case 8:  // OverRight
            if(msg.inRange(place))
               for(i=0;i<h;i++)
                  drawLED(place*5,i*5,msg.getLED(place,i),msg.getColor(place),pixmap);
            else
               for(i=0;i<h;i++)
                  drawLED(place*5,i*5,false,1,pixmap);

            gr.drawImage(pixmapimg,offset,offset, this);

            place++;

            if(place >= w)
               nextFunc();

            break;
                  
         case 9:  // ScrollCenter
            // The right side
            if(w >= place*2)
            {
               pixmap.copyArea(WIDTH/2,0,WIDTH-WIDTH/2-5,HEIGHT,5,0);
               for(i=0;i<h;i++)
                  if(msg.inRange(w-place))
                     drawLED(WIDTH/2,i*5,msg.getLED(w-place,i),msg.getColor(w-place),pixmap);
                  else
                     drawLED(WIDTH/2,i*5,false,1,pixmap);
            }

            if(place < w/2)
            {
               pixmap.copyArea(5,0,WIDTH/2-5,HEIGHT,-5,0);
               for(i=0;i<h;i++)
                  if(msg.inRange(place))
                     drawLED(WIDTH/2-5,i*5,msg.getLED(place,i),msg.getColor(place),pixmap);
                  else
                     drawLED(WIDTH/2-5,i*5,false,1,pixmap);
            }

            gr.drawImage(pixmapimg,offset,offset, this);
            
            place++;

            if(place >= w/2 && place*2 > w)
               nextFunc();

            break;

         case 10:  // OverCenter
            // The right side
            if(w >= place+w/2)
            {
               for(i=0;i<h;i++)
                  if(msg.inRange(w/2+place+1))
                     drawLED(WIDTH/2+place*5+5,i*5,msg.getLED(w/2+place+1,i),msg.getColor(w/2+place+1),pixmap);
                  else
                     drawLED(WIDTH/2+place*5+5,i*5,false,1,pixmap);
            }

            if(place < w/2)
            {
               for(i=0;i<h;i++)
                  if(msg.inRange(w/2-place))
                     drawLED(WIDTH/2-place*5,i*5,msg.getLED(w/2-place,i),msg.getColor(w/2-place),pixmap);
                  else
                     drawLED(WIDTH/2-place*5,i*5,false,1,pixmap);
            }

            gr.drawImage(pixmapimg,offset,offset, this);
            
            place++;

            if(w < w/2+place && place >= w/2)
               nextFunc();

            break;

         case 11:  // OverLeft
            if(msg.inRange(place))
               for(i=0;i<h;i++)
                  drawLED(place*5,i*5,msg.getLED(place,i),msg.getColor(place),pixmap);
            else
               for(i=0;i<h;i++)
                  drawLED(place*5,i*5,false,1,pixmap);

            gr.drawImage(pixmapimg,offset,offset, this);

            place--;

            if(place == 0)
               nextFunc();

            break;
            
         case 12:  // OverUp
            for(i=0;i<w;i++)
            {
               if(msg.inRange(i))
                  drawLED(i*5,place*5,msg.getLED(i,place),msg.getColor(i),pixmap);
               else
                  drawLED(i*5,place*5,false,1,pixmap);
            }

            gr.drawImage(pixmapimg,offset,offset, this);

            place--;

            if(place < 0)
               nextFunc();

            break;

         case 13:  // OverDown
            for(i=0;i<w;i++)
            {
               if(msg.inRange(i))
                  drawLED(i*5,place*5,msg.getLED(i,place),msg.getColor(i),pixmap);
               else
                  drawLED(i*5,place*5,false,1,pixmap);
            }

            gr.drawImage(pixmapimg,offset,offset, this);

            place++;

            if(place >= h)
               nextFunc();

            break;
      }  // End switch() statement
   }  // End update()
}  // End LED class
