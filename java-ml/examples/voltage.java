package voltage;

import java.awt.*;
import java.lang.Math;
import java.lang.Integer;

/**
 * Parameters:
 *   VOLTAGE=int     Voltage level of the battery.  Defaults to 10
 *   AMPERAGE=int    Amperage level of the lightbulb.  Defaults to 2
 *
 * Synopsis:
 *
 * - Mon Aug 21 14:53:02 PDT 1995
 *   This tool allows users to derive the relationship between voltage,
 * resistance, and amplitude through experimentation.  Given a circuit
 * containing a battery with a voltage and a lightbulb with an amperage,
 * the user inserts resistors into the circuit.  If the resistance is
 * too low, the lightbulb explodes.  If the resistance is too high, 
 * the lightbulb fails to light.
 * 
 * History is located at the end of the sourcecode.
 *
 * @author Sean Russell
 * Department of Computing and Information Sciences, University of Oregon
 * Department of Physics, University of Oregon
 */
public class voltage extends java.applet.Applet
{
   Resistance resistance;	// The resistance array
   int rwidth, rheight;		// Resistor glyph idth and height
   int bwidth, bheight;
   int cx, cy, cw, ch;		// Clipping area.
   int ocx, ocy, ocw, och;
   int ix = 0, iy = 0;		// The x/y coords of whatever we're dragging
   Image bang;
   Image lightcircuit;
   int voltage;			// Voltage of the battery
   Image lightbulb_on, lightbulb_broken;
   Image lightswitch_on, battery;
   Battery batts[] = new Battery[4];
   int amperage;		// Amperage of the lightbulb
   boolean switch_on=false;	// Whether the switch is on or not.
   int rangeXStart, rangeYStart, // The location of the circuit area into
       rangeXEnd, rangeYEnd;	 // which resistors can be dropped
   int max_resistors = 6;	 // Max number of resistors
   int max_width = 600;		 // Max width of the app window
   int max_height = 150;	 // The max height of the app window
   int bar = 30;		 // The y value on which the circuit
				 // lies.
   int res_selected = 0;	 // 0 if no resistor is being dragged,
				 // <0 if a new resistor is being dragged,
				 // >0 if an old resistor is being dragged.
   int bat_selected = 0;	 // 0 if no battery is being dragged,
				 // >0 if a new battery is being dragged.
   boolean hide;		 // Determines whether the amperage is
				 // displayed.

   boolean auplay = false;

   /**
    * gets the images and initializes all of the drawing area values.
    * Reads arguments to the applet and makes sure that they are legal;
    * if not, adjusts them until they are legal, and if arguments weren't
    * given, generates random values.
    */
   public void init()
      {
      int sv;
      
      amperage = Integer.parseInt((getParameter("amperage") == null) ? 
				  "2" : getParameter("amperage"));
      voltage = Integer.parseInt((getParameter("voltage") == null) ? 
				 "12" :
				 getParameter("voltage"));
      hide = (getParameter("hide") == null) ? false : true;
      
      if (voltage < amperage) throw new IllegalArgumentException("Bad circuit values");
      while ((voltage % amperage) != 0)
	 {
	 voltage--;
	 if (voltage < amperage) voltage *= 10;
	 }
      
      Image r = getImage(getCodeBase(), "images/resistor.gif");
      lightswitch_on = getImage(getCodeBase(), "images/lightswitch-on.gif");
      
      // Resistor dimensions
      rwidth = 50;
      rheight = 18;
      
      battery = getImage(getCodeBase(), "images/battery.gif");
      if (battery == null)
	 throw new IllegalArgumentException("images/battery.gif not found");

      sv = ((voltage/2)<amperage) ? voltage : voltage/2 ;
      sv = ((sv % amperage) != 0) ? voltage : sv;

      for (int i=0; i<4; i++)
	{
	 batts[i] = new Battery(this, battery, (i+1)*sv, 
				123 + ((battery.getWidth(this)+10)*i), 
				3+rheight+2);
	}

      // Main Battery dimensions (more or less... used for other calculations)
      bwidth = 110;
      bheight = 57;

      lightcircuit = getImage(getCodeBase(), "images/light-circuit.gif");
      bang = getImage(getCodeBase(), "images/bang1.gif");
      lightbulb_on = getImage(getCodeBase(), "images/bulb-glow.gif");
      lightbulb_broken = getImage(getCodeBase(), "images/bulb-broken.gif");

      // Set up the area where the resistor glyphs in the circuit go
      rangeXStart = bwidth;
      rangeXEnd = bwidth + (rwidth*max_resistors);
      rangeYStart = bar + bheight;
      rangeYEnd = bar + bheight + 18;

      resistance = new Resistance(this, max_resistors, r, rangeXStart, rangeYStart);

      // Default clipping area
      cx = cy = 0;
      cw = max_width;
      ch = max_height;

      resize(max_width, max_height);
      }

    Image offimg;
    Graphics offg;

    public void paint(Graphics g) {
        update(g);
    }
   public void update(Graphics g)
      {
	  if (resistance == null) {
	      return;
	  }
	  if (offimg == null) {
	      Dimension d = size();
	      offimg = createImage(d.width, d.height);
	      offg = offimg.getGraphics();
	  }
      String total = new String("Total Resistance: ");
      int resist = resistance.resistance();

      // Clear the screen
      offg.setColor(getBackground());
      offg.fillRect(0,0,max_width,max_height);

      // Draw the dropzone for the circuit
      offg.draw3DRect(rangeXStart-2, rangeYStart-2,
		    rangeXEnd - rangeXStart + 4,
		    rangeYEnd - rangeYStart + 4,
		    false);

      offg.drawImage(lightcircuit, 0, bar, this);
      offg.setColor(Color.black);
      
      Output.print(offg, voltage, 30, bar+65);
      if (!hide)
	Output.print(offg, amperage, max_width - 47, bar+37);

      total += Integer.toString(resist);
      Output.print(offg, total, rangeXStart, rangeYEnd+16);

      // Draw the resistors in the toolbox
      offg.fill3DRect(100, 0, ((rwidth+10)*5)- 4, 
		    3 + rheight + 2 + battery.getHeight(this) + 2, 
		    false);
      for (int i=0; i < 5; i++)
	 resistance.draw(offg, (int)Math.pow(2, i), 
			 103 + ((rwidth+10)*i), 3); 

      for (int i=0; i < 4; i++)
	  batts[i].draw(offg);

      // Draw the resistors in the circuit
      resistance.draw(offg);

      // Handle switch events
      if (switch_on)
	 {
	 offg.setColor(getBackground());
	 offg.fillRect(469, bar, 32, 70);
	 offg.drawImage(lightswitch_on, 465, bar+23, this);

	 if ((amperage * resist) < voltage) // Blow up: too little resistance
	    {
   	    if (auplay)
	       {
	       // Blow up: too little resistance
	       if ((amperage * resistance.resistance()) < voltage)
		  play(getCodeBase(), "sounds/electricity.au");
	try {Thread.sleep(200);} catch (InterruptedException e){}
	       offg.drawImage(bang, 530, bar + 10, this);
	try {Thread.sleep(100);} catch (InterruptedException e){}
	       auplay = false;
	       }
	    offg.setColor(getBackground());
	    offg.fillRect(530, bar + 8, bang.getWidth(this), bang.getHeight(this)+2);
	    offg.drawImage(lightbulb_broken, 542, bar + 20, this);
	    Output.print(offg, amperage, max_width + 20, bar-10);
	    }
	 else if ((amperage * resist) == voltage) // V=IR: Just right. 
	    {					  // Light the bulb
	    offg.drawImage(lightbulb_on, 542, bar + 20, this);
	    Output.print(offg, amperage, max_width + 20, bar-10);
	    }
	 }

      if (res_selected != 0)	// We're dragging a resistor; we draw it
	 resistance.draw(offg, Math.abs(res_selected), ix, iy);

      if (bat_selected != 0)	// We're dragging a battery; we draw it
	  batts[bat_selected - 1].draw(offg, ix, iy);
      
      cx = cy = 0;
      cw = max_width;
      ch = max_height;
      g.drawImage(offimg, 0, 0, null);
      }

   /**
    * Initializes clipping variables; checks to see if the user clicked
    * in the toolbox, in the resistance box, or on the switch, and
    * reacts accordingly.
    */
   public boolean mouseDown(java.awt.Event evt, int x, int y)
      {
      // We have to set up some variables for erasing
      ix = ocx = (x - (rwidth/2));
      iy = ocy = (y - (rheight/2));

      // Check to see if they mouse downed in the new resistor
      // area.
      for (int i=0; i < 5; i++)
	 {
	 if ((x > ((i*(rwidth+10))+102)) && 
	     (x < ((i*(rwidth+10))+102+rwidth)) &&
	     (y > 2) && (y < rheight+3))
	    res_selected = (int)Math.pow(2, i)*-1;
	 }

      // Check to see if they mouse downed on a new battery
      if (res_selected == 0)
	{
	  for (int i=0; i<4; i++)
	    if (batts[i].in(x, y)) bat_selected = i+1;
	}
      
      // If they didn't click on a new resistor, check to see
      // if they clicked on an old resistor.
      if ((res_selected == 0) && (bat_selected == 0))
	 res_selected = resistance.mouseDown(x, y);

      // Check to see if they clicked on the onswitch
      // to turn it on.
      if ((res_selected == 0) && (bat_selected == 0))
	{
	  if ((x<504) && (x>465) &&
	      (y<(bar+56)) && (y>bar))
	    switch_on = (switch_on)?false:true;
	  else
	    switch_on = false;
	  auplay = true;
	}

      repaint();
      return true;
      }

   /** 
    * Adjusts clipping values for drags, and ignores the event otherwise.
    */
   public boolean mouseDrag(java.awt.Event evt, int x, int y)
     {
       int w = 0, 
           h = 0;

       if (res_selected != 0)	// We only care about drag events if
	 {			// we're carrying something
	   w = rwidth;
	   h = rheight;
	 }	 
       else if (bat_selected != 0)
	 {
	   w = battery.getWidth(this);
	   h = battery.getHeight(this);
	 }

       if (w != 0)
	 {
	   // The following centers the object on the mouse
	   x -= w/2;
	   y -= h/2;
	   cx = Math.max(0, Math.min(Math.min(ocx, x), max_width));
	   cy = Math.max(0, Math.min(Math.min(ocy, y), max_height));
	   cw  = Math.min(Math.abs(ocx-x)+ w , max_width-cx);
	   ch  = Math.min(Math.abs(ocy-y)+ h , max_height-cy);
	   ocx = ix = x;
	   ocy = iy = y;
	   repaint();
	 }
      return true;
     }

   /**
    * Checks for dropping a resistor in the droprange and
    * adds a resistor if so.
    * Also checks for dropping a new battery on the main battery,
    * and changes the main batt value if so.
    */
   public boolean mouseUp(java.awt.Event evt, int x, int y)
      {
      if (res_selected != 0)	// We only care about mouseUp events
	 {			// if we were carrying something
	 // Check to see if we're in the drop range
	 if ((x<rangeXEnd) && (x>rangeXStart) &&
	     (y<rangeYEnd) && (y>rangeYStart))
	    {
	    resistance.add(Math.abs(res_selected));
	    }			// that they were dragging something:
	 res_selected = 0;		// Add a new, or re-add an old
	 repaint();
	 }
      if (bat_selected != 0)
	{
	  if ((x < 73) && (y > (bar+50)) && (y < (bar+107)))
	    voltage = batts[bat_selected - 1].voltage;
	  bat_selected = 0;
	  repaint();
	}
      return true;
      }

   /**
    * Just calls repaint()
    */
   public void start()
      {
      repaint();
      }
}


/**
 * Dummy class to consolidate output functions in one place
 *
 * @author Sean Russell
 */
class Output
{
   /**
    * Print an integer value in g at (x,y)
    */
   public static void print(Graphics g, int val, int x, int y)
      {
      g.setColor(Color.black);
      g.drawString(Integer.toString(val), x+1, y+1);
      g.drawString(Integer.toString(val), x+1, y);
      g.setColor(Color.blue);
      g.drawString(Integer.toString(val), x, y);
      }

   /**
    * Print a string in g at (x,y)
    */
   public static void print(Graphics g, String val, int x, int y)
      {
      g.setColor(Color.black);
      g.drawString(val, x+1, y+1);
      g.drawString(val, x+1, y);
      g.setColor(Color.blue);
      g.drawString(val, x, y);
      }
   }


/** 
 * Resistance represents the total resistance along a circuit, and is made
 * up of resistors.  The resistance in the wire itself can be represented
 * by an additional resistor.  
 *
 * @author Sean Russell 
 */
class Resistance
{ 
   Resistor resistor[]; 
   Image res; 
   int num_resistors = 0; 
   int max_resistors = 0; 
   int xs, ys;
   voltage parent;

   /**
    * Creates a resistance circuit given the number of resistors
    * and the glyph to represent the resistors.
    */
   Resistance(voltage parent, int num, Image i, int xs, int ys)
    {
    this.parent = parent;
    res = i;
    resistor = new Resistor[num];
    max_resistors = num;
    this.xs = xs;
    this.ys = ys;
    }
   
   /**
    * Draws all resistors on the circuit.  Given a graphics context
    * and the (x,y) start coordinates.
    */
   public void draw(Graphics g)
      {
     for (int i=0; i < num_resistors; i++)
	{
	g.drawImage(res, resistor[i].x, resistor[i].y, parent);
	Output.print(g, resistor[i].resistance, 
	      resistor[i].x + 20, resistor[i].y + 14);
	}
     }

   /**
    * Draws a single resistor given graphics context g, the resistance
    * val, and the (x,y) coordinates to draw the glyph at.
    * val is the factor of the resistor, not the actual resistance
    */
   public void draw(Graphics g, int val, int x, int y)
      {
      g.drawImage(res, x, y, parent);
      Output.print(g, val, x+20, y+14);
      }

   /**
    * Adds a resistor with resistance size to the circuit.
    * size is the factor of the resistor
    */
   public void add(int size)
      {
      if (num_resistors < max_resistors)
	 {
	 num_resistors++;
	 resistor[num_resistors-1] = 
	    new Resistor(size, res,
			 xs + ((num_resistors-1)*res.getWidth(parent)),
			 ys);
	 }
      }

   /**
    * Checks to see if the mouseDown occurs over a resistor in
    * the circuit, and if so, selects that resistor.
    */
   public int mouseDown(int x, int y)
      {
      int sel = 0;
      int val = 0;

      for (int i=0; (i < num_resistors) && (sel==0); i++)
	 if (resistor[i].in(x, y)) sel = i+1;

      if (sel != 0) val = resistor[sel-1].resistance;
      if (sel != 0) remove(sel);

      return val;
      }
      
   /**
    * Removes the indexed resistor.  
    * which = [1, num_resistors]
    */
   public void remove(int which)
      {
      for (int i=which-1; i < (num_resistors-1); i++)
	 {
	 resistor[i].resistance = resistor[i+1].resistance;
	 }

      if (num_resistors > 0) num_resistors--;
      }
   
   /**
    * Totals and returns the resistance of the circuit.
    */
   public int resistance()
      {
      int val = 0;
      
      for (int i=0; i < num_resistors; i++) val += resistor[i].resistance;
      return val;
      }
   }


/**
 * Represents a resistor, including the resistance, width and height
 * of the glyph graphically representing the resistor, and the x/y
 * location of this resistor.
 * 
 * @author Sean Russell
 *
 */
class Resistor
{
   public int resistance;
   int x, y;
   int w, h;
   
   Resistor()
      {
      resistance = x = y = w = h = 0;
      }
   
   /**
    * Creates a resistor given:
    * res  resistance
    * i    the glyph, from which we draw height and width data
    * x,y  the (x,y) coordinates of the glyph
    */
   Resistor(int res, Image i, int x, int y)
      {
      resistance = res;
      this.x = x;
      this.y = y;
      w = 50;
      h = 18;
      }

   /**
    * Initializes a resistor with the values:
    * res  resistance
    * i    the glyph, from which we draw height and size().width data
    * x,y  the (x,y) coordinates of the glyph
    */
   public void init(int res, Image i, int x, int y)
      {
      resistance = res;
      this.x = x;
      this.y = y;
      w = 50;
      h = 18;
      }

   /**
    * Checks to see if (x,y) are within the space taken up by this
    * resistor's glyph.  Returns true if so, otherwise false.
    */
   public boolean in(int x, int y)
      {
      if ((x > this.x) && (x < (this.x + w)) &&
	  (y > this.y) && (y < (this.y + w)))
	 return true;
      return false;
      }

   /**
    * Zeros the resistor's values.
    */
   public void clear()
      {
      resistance = x = y = w = h = 0;
      }
   }


/**
 * A battery, with voltage, glyph and graphics coordinates
 */
class Battery
{
  voltage parent;
  Image glyph;
  public int voltage;
  int x, y;

  /**
   * Create a battery with the glyph i, the voltage v,
   * and the x/y coordinates of (xloc, ylox)
   */
  Battery(voltage parent, Image i, int v, int xloc, int yloc)
    {
      this.parent = parent;
      glyph = i;
      voltage = v;
      x = xloc;
      y = yloc;
    }

  /**
   * Changes the x and y coordinates of this battery
   */
  public void change(int xloc, int yloc)
    {
      x = xloc;
      y = yloc;
    }

  /**
   * Answers whether the given coordinate is within this battery's
   * glyph area with true or false
   */
  public boolean in(int xloc, int yloc)
    {
      if (
	  (xloc > x) && (yloc > y) &&
	  (xloc < (x + glyph.getWidth(parent))) &&
	  (yloc < (y + glyph.getHeight(parent)))
	  )
	return true;
      return false;
    }

  /**
   * Paints a battery at its coordinates, and adds the voltage
   */
  public void draw(Graphics g)
    {
      g.drawImage(glyph, x, y, parent);
      Output.print(g, voltage, x+15, y+14);
    }

  /**
   * Paints a battery at the given coordinates, and adds the voltage
   */
  public void draw(Graphics g, int xloc, int yloc)
    {
      g.drawImage(glyph, xloc, yloc, parent);
      Output.print(g, voltage, xloc+15, yloc+14);
    }
}


/**
 * History
 *
 * - Wed Aug 23 16:26:40 PDT 1995
 *   First major progress in the program.  Everything is going pretty
 * smoothly, since most of the non-UI AWT (of which there is very little
 * in this code) stuff is straightforward.  Java comes across as
 * increasingly elegant, as long as one understands that it only
 * resembles C++.  The absence of a preprocessor makes life more
 * difficult, and some constructions are a bit unnatural for those of
 * us more used to traditional C/C++, but I'm pleased.  Most of the
 * implementation of this code was done within about a day and a half
 * of reasonable effort.
 *   The dragging and clipping example supplied with Java was a bit
 * oddly implemented, but it helped.
 *   Left to do:
 *   + On/Off switch
 *   + Lightbulb animation
 *   + Cosmetics
 *   + Text gadgets for voltage and amperage
 *
 * - Thu Aug 24 12:15:43 PDT 1995
 *   It is done, unless Greg wants me to add thos text gadgets for the
 * battery and the lightbulb.  I don't think its neccessary, since we
 * hava a parameter list now.  The on/off switch took all of 10 minutes
 * to implement.  Lightbulb animation is simple, but it works.  Had
 * remarkably few bugs.
 *   Modified the reisitor levels to be powers of 2.
 *   Still waiting on graphics from Amy.
 *
 * - Fri Aug 25 12:00:27 PDT 1995
 *   Late yesterday I completely revamped the display.  Amy draw a really
 * nice circuit diagram so that the entire thing looks really slick.
 * It took about 1.5f hours to implement and debug.
 *   Today mostly cosmetic modifications.  Made the resistor toolbar
 * horizontal rather than vertical, which saves a lot of space.  Changed
 * dropbox from yellow to a 3DRect.  Added exception handling in the case
 * of missing GIFs.  Threw in some animation for when the lightbulb blows
 * up.  Added some quasi-external output functions so all of the printing
 * is centralized.  Cleaned up the code a lot, and optimized.  I had to
 * streamline the code a lot; I was doing things like calling update()
 * unneccessarily, etc.
 *
 * - Tue Sep  5 11:42:01 PDT 1995
 *   Greg came back from his vacation and is not completely satisfied with
 * the circuit simulator.  Still to do:
 *  + Drag-n-drop values for the battery (done: Tue Sep  5 13:25:49 PDT 1995)
 *  + Hidden but fixed amp value (done: Tue Sep  5 13:25:49 PDT 1995)
 *  + require that the user turn off the circuit, and shock them (virtually)
 *    if they don't.
 * Little modifications.  Most notably:
 *  + Added "hide" parameter, for optional hiding of amperage
 *  + Batterys are now multiples of the "voltage" argument
 *  + Text output is darker (more readable?)
 */

interface HISTORY {}
