
/*
 * Copyright (c) 1995 Luis Fernandes
 *
 * Permission to use, copy, hack, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies.
 * 
 */

/* $Id$ */

/* $Log$
# Revision 1.3f  1995/06/01  22:59:31  elf
# Value of each column is now displayed in the top frame.
#
# Redraw (via update) is clipped to a region that surrounds the
# appropriate column that needs updating.
#
# Revision 1.2f  1995/04/17  16:40:13  elf
# Implemented user-defined resource "VALUE" which may to used to set
# the initial condition (bead-configuration) of the abacus.
#
# Eliminated *some* re-draw flicker by moving the repaint() call to
# animateBead() rather than keeping it in moveBead() where it caused
# the applet to redraw everytime a bead moved (this was especially bad
# when several beads moved at once).
# */

import java.awt.*;

/* This is a java-applet simulation of a Chinese abacus. 

   Each column is stored internally in an integer array called
   'column' that is initialized to represent the resting positions of
   the beads: 2 bead on top deck row-pos 0 & 1, pos 2 empty; 5 beads
   lower deck pos 4-8, pos 3 empty. The initial value is
   499d=111110011 where a '1' represents a position that is occupied
   by a bead and a '0' represents an empty position. As the beads are
   moved, the 1's are shifted (using << and >>) accordingly to
   represent their new locations.

*/

/* The illustration below represents 1 column of the abacus.
 * O represents the bead; 
 * | represents an empty position (the rod)
 * = represents the frame
 *
 *	=============	Row Position (index 'r' )
 *		O			0
 *		O			1			Upper Deck
 *		|			2
 *	=============
 *		|			3
 *		O			4
 *		O			5			Lower Deck
 *		O			6
 *		O			7
 *		O			8
 *	=============
 *	
 */

public class Abacus extends java.applet.Applet 
{
  /*  initial attributes of the abacus*/
  static final int MAXCOLS=10;	/* number of columns the abacus will have */
  
  static final int BEADHEIGHT=17;
  static final int BEADWIDTH=17;
  static final int FRAMEWIDTH=10; /* thickness of the frame*/

  static final int COLGAP=2;	/* gap between 2 cols*/
  static final int ROWGAP=2;	/* gap between 2 rows*/
  
  static final int NTOPROWS=3;	/*(2 beads, 1 gap on top-deck)*/
  static final int NBOTROWS=6;	/*(5 beads, 1 gap on top-deck)*/

  static final int NCOLS=MAXCOLS; /* number of columns*/
  static final int NROWS=(NTOPROWS+NBOTROWS); /* number of rows*/
  
  static final int MIDFRAME=(FRAMEWIDTH+(NTOPROWS*BEADHEIGHT)+((NTOPROWS+1)*ROWGAP));
  
  //size().width & size().height of window depends on attributes of the abacus
  static final int INITWIDTH=((2*FRAMEWIDTH)+(NCOLS*BEADWIDTH)+((NCOLS+1)*COLGAP));
  static final int INITHEIGHT=((3*FRAMEWIDTH)+(NROWS*BEADHEIGHT)+((NROWS+1)*ROWGAP));
  
  private int column[]=new int[MAXCOLS];
  
  private int cx, cy;	// for xlating x,y to row,col
  private int ux, uy;	// for clipping region

  private boolean overflow;	// when the abacus overflows this is true
  private boolean carry; // when a calc causes a carry to the next col 
  
  Image bead, nobead;			// holds picture of a bead and an empty loc
  Font valueFont;				// to paint the value
  
  public 
	void init()
	  {
		
		String valattr;
		
		/* Init the internal configuration of the beads: 499d=111110011
		 * (2 bead on top deck pos 0 & 1; 5 beads lower deck pos 4-8,
		 * pos 2 & 3 are empty initially) */
		for(int i=0; i<MAXCOLS; i++) column[i]=499;
		
		/* check for user-specified value resource*/

		valattr=getParameter("value");
/*		System.out.println("==>"+valattr); */

		if((valattr==null) || (valattr.length()>MAXCOLS) )
		  {
			/* if no attribute is specified, or the value is too big,
			 * use default*/
			System.out.println(valattr+"(VALUE resource) is either too big or unspecified; ignoring.\n"); 

		  }
		else		/* set each column according to the user-specified value*/
		  {
			int len=valattr.length();
			
			for(int i=0; i<len; i++)
			  {
				int val=Integer.valueOf(valattr.substring(i,i+1)).intValue();
				
				// set value in the upper-deck
				if(val>4)
				  {	
/*					System.out.println("Col "+i+"%5="+val%5); */
					
					animateBead(1, i);
				  }

				// set value in the lower-deck 
				int remainder=val%5;
				
				if(remainder>0)
				  {
					animateBead(3+remainder, i);
				  }
			  }
		  }
		
		bead=getImage(getCodeBase(), "images/diamond.gif");
		nobead=getImage(getCodeBase(), "images/nodiamond.gif");

		valueFont=new java.awt.Font("Courier", Font.BOLD, 10);
		
		resize(INITWIDTH, INITHEIGHT); /* initial size */
		
	  }
  
  private 
	void displayValue(Graphics g)
	  {
		char valchars[]= new char [MAXCOLS*3];

		String value= new String (valchars);
		
		if(overflow) value+="*";
		
		/* look at each column*/
		for(int col=0; col<MAXCOLS; col++)
		  {
			int r;
			int val=0;
			
			/* find the empty row, and determine what the value is*/
			/* top-deck*/
			for(r=2; r>=0; r--)
			  {
				if(!RowOccupied(r, column[col])) break;
			  }

			val+=((2-r)*5);
			
			/* bottom-deck*/
			for(r=3; r<9; r++)
			  {
				if(!RowOccupied(r, column[col])) break;
			  }

			val+=(r-3);
			
/*			System.out.println("\tColumn"+col+"value="+val);*/

			value=value+" "+Integer.toString(val);
			
		  }

		if(overflow)
		  g.setColor(Color.red);
		else 
		  g.setColor(Color.yellow);

		g.drawString(value, 5, 10);
		
	  }
  
  /* draw the abacus */
  public 
	void paint(Graphics g)
	  {
	      if (bead == null) {
		  return;
	      }
		drawFrame(g);
		for(int i=0; i<MAXCOLS; i++)
		  {
			drawColumn(g, i);

		  }

		displayValue(g);
		
	  }/* init()*/
  
  public void update(Graphics g) 
	{
	  paint(g);
	  g.setColor(Color.black);
	  g.fillRect(0,0,INITWIDTH, FRAMEWIDTH);
	  displayValue(g);
    }

  public 
	boolean mouseUp(java.awt.Event evt, int x, int y) {
	  
	  /* row,col returned in cx,cy*/
	  boolean i=translateXY2RowCol(x, y);
	  
	  if(i)						/*valid row,col...*/
		{

		  if(RowOccupied(cy,column[cx]))
			{
			  animateBead(cy,cx);

			  ux=FRAMEWIDTH+(cx*(COLGAP+BEADWIDTH));

			  uy=FRAMEWIDTH+(cy*(ROWGAP+BEADHEIGHT));
			  repaint();
			  
			}
		}
	  return true;
	} /*mouseUp()*/


  private 
	void drawColumn(Graphics g, int col)
	  {
		
		for(int beadnum=0; beadnum<9; beadnum++) 
		  {
			if(RowOccupied(beadnum,column[col]))
			  {
				drawBead(g, beadnum, col);
			  }
			else
			  {
				undrawBead(g, beadnum, col);
			  }
		  }
		
	  } /*drawColumn()*/
  
  private 
	void drawBead(Graphics g, int row, int col)
	  {
		
		if(row<3)				/* beads in the top-deck */
		  {
			g.drawImage(bead, FRAMEWIDTH+COLGAP+(col*(BEADWIDTH+COLGAP)), 
						FRAMEWIDTH+(row*(BEADHEIGHT+ROWGAP))+2, this);
		  }
		else					/* account for the middle-rail */
		  {
			g.drawImage(bead, FRAMEWIDTH+COLGAP+(col*(BEADWIDTH+COLGAP)), 
						(2*FRAMEWIDTH)+(row*(BEADHEIGHT+ROWGAP))+2, this); 
		  }
		
	  } /*drawBead()*/

  private 
	void undrawBead(Graphics g, int row, int col)
	  {
		
		if(row<3)				/* beads in the top-deck */
		  {
			g.drawImage(nobead, FRAMEWIDTH+COLGAP+(col*(BEADWIDTH+COLGAP)), 
						FRAMEWIDTH+(row*(BEADHEIGHT+ROWGAP))+2, this);
		  }
		else					/* account for the middle-rail */
		  {
			g.drawImage(nobead, FRAMEWIDTH+COLGAP+(col*(BEADWIDTH+COLGAP)), 
						(2*FRAMEWIDTH)+(row*(BEADHEIGHT+ROWGAP))+2, this); 
		  }
		
	  } /*undrawBead()*/



  private 
	void drawFrame(Graphics g)
	  {

		g.setColor(Color.blue);

		/* the rails*/
		for(int i=0; i<NCOLS; i++)
		  g.drawLine(FRAMEWIDTH+COLGAP+(BEADWIDTH/2)+(i*(BEADWIDTH+COLGAP)), 0,
					 FRAMEWIDTH+COLGAP+(BEADWIDTH/2)+(i*(BEADWIDTH+COLGAP)), 
					 INITHEIGHT);

		g.setColor(Color.black);
		
		g.fillRect(0,0,INITWIDTH, FRAMEWIDTH); /*top bar*/
		g.fillRect(0,INITHEIGHT-FRAMEWIDTH, INITWIDTH, FRAMEWIDTH); /*bot bar*/
		
		g.fillRect(INITWIDTH-FRAMEWIDTH, 0, INITWIDTH, INITHEIGHT); /*right bar*/
		g.fillRect(0,0, FRAMEWIDTH, INITHEIGHT-FRAMEWIDTH); /*left bar*/

		/* middle bar*/
		g.fillRect(0, MIDFRAME, INITWIDTH, FRAMEWIDTH);

	  }/*drawFrame()*/
  

  public 
	void animateBead(int r, int c)
	  {
		
		if(r<3)						/* selection in upper deck*/
		  {
			int i;
			
			/* find empty row in upperdeck; pos 0,1,2*/
			for(i=0; i<3; i++) if(!RowOccupied(i,column[c])) break;
			
			if(i<r)		/* if empty row is above cur bead...*/
			  moveBeadUp(r,c);
			else
			  moveBeadDn(r,c);
		  }
		else						/* selection in lower deck*/
		  {
			int i;
			
			/* find empty row in lowerdeck; pos 3-8 */
			for(i=3; i<9; i++) if(!RowOccupied(i,column[c])) break;
			
			if(i<r)     /* if empty row is above cur bead...*/
			  moveBeadUp(r,c);
			else
			  moveBeadDn(r,c);

		  }


	  }/*animateBead()*/
  
  private 
	void moveBeadUp(int r, int c)
	  {
		/* keep looking for an empty position directly above*/
		if(RowOccupied(r-1,column[c])) moveBeadUp(r-1,c);
		
		column[c]=column[c]-(1<<r); /* row 'r' is now empty */
		column[c]=column[c]+(1<<((r-1))); /* row 'r-1' now is occupied*/

		// check whether we have to carry to next column
		if(!RowOccupied(0,column[c]))
		  {
			System.out.println("Carry to col"+(c-1));
			carry=true;
		  }
		else if(c==0 && r==1)
		  {
			overflow=false;
		  }			
		
	  } /*moveBeadUp()*/
  
  private 
	void moveBeadDn(int r, int c)
	  {

		/* keep looking for an empty position directly below*/
		if(RowOccupied(r+1,column[c])) moveBeadDn(r+1,c);

		column[c]=column[c]-(1<<r); /* row 'r' is now empty */
		column[c]=column[c]+(1<<((r+1))); /* row 'r+1' now is occupied*/

		// check whether we have to carry to next column if in the top frame
		if(!RowOccupied(0,column[c]) && r<3)
		  {
			if(c==0)
			  {
				System.out.println("***OVERFLOW***");
				overflow=true;
				return;
			  }
			
			
			System.out.println("Carry to col"+(c-1));
			carry=true;
		  }


	  }/* moveBeadDown()*/

  
  /*  ///////////////Misc Functions///////////////////////*/
	
	/* translateXY2RowCol, converts x,y coord a row,col coord. The top
	 * deck has 3 rows (1 is empty), the bottom deck has 6 rows (1 is
	 * empty). Row # 3 is invalid because the middle-frame occupies this
	 * position. Coordinates for the bottom deck are adjusted for this
	 * anomaly, i.e. row #4 on the screen, is actually index #3 into the
	 * Column array.
	 *
	 *		passed: (pointers to) absolute x,y coordinates representing 
	 *              the click-location
	 *		returns: - as globals cx, cy, the  row (0-9), col (0-NCOLS)
	 *                 index
	 *               - True if click was on a bead or empty position
	 *               - False if click was at mid-frame location 
	 */
	
	private 
	  boolean translateXY2RowCol(int x, int y)
		{
		  
		  cx=(x-FRAMEWIDTH)/(COLGAP+BEADWIDTH);
		  
		  if(cy<MIDFRAME)
			{
			  cy=(y-FRAMEWIDTH)/(ROWGAP+BEADHEIGHT);
			}
		  else	/* account for the middle-frame (+1 bead size().height) (+4 is fudge)*/
			{
			  cy=(y-((2*FRAMEWIDTH)-BEADHEIGHT))/(ROWGAP+BEADHEIGHT);
			}
		  
		  /* technically, the mid-frame occupies position 3 & click is invalid*/
		  if(cy==3) return(false);
		  
		  else if(cy>2)				/* if pos (row) is greater than 3 ...*/
			{	
			  cy--;					/* ...adjust by 1 row*/
			  return(true);
			}
		  else 
			{
			  return(true);
			}
		  
		}/*translateXY2RowCol()*/
  
  
  final private 
	boolean RowOccupied(int r, int c)
	  {
		
		if((c & 1<<(r))==0)
		  {
			return false;
		  }
		else
		  {
			return true;
		  }
		
	  }/*RowOccupied()*/

}

