/*
 * @(#)ImageLoopItem.java	1.22f 95/03/28 James Gosling
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * A simple Item class to play an image loop.  The "img" tag parameter
 * indicates what image loop to play.
 *
 * @author 	James Gosling
 * @version 	1.22f, 28 Mar 1995
 */

import java.io.InputStream;
import java.awt.*;
import java.net.*;
import java.awt.event.*;

public class ImageLoopItem extends java.applet.Applet implements Runnable, MouseListener {
    /**
     * The current loop slot.
     */
    int loopslot = 0;

    /**
     * The directory or URL from which the images are loaded
     */
    String dir;

    /**
     * The image loop.
     */
    ImageLoop loop;

    /**
     * The thread animating the images.
     */
    Thread kicker = null;

    /**
     * The length of the pause between revs.
     */
    int pause;

    /**
     * Whether or not the thread has been paused by the user.
     */
    boolean threadSuspended = false;

    /**
     * The offscreen image.
     */
    Image	im;

    /**
     * The number of images.
     */
    int nimgs;

    /**
     * The offscreen graphics context
     */
    Graphics	offscreen;

    /**
     * Initialize the applet. Get attributes.
     */
   public void init()
   {

   setBackground(Color.white);

	String at = getParameter("img");
	dir = (at != null) ? at : "doc:/demo/images/duke";
	at = getParameter("pause");
	pause = (at == null) ? 0 : Integer.valueOf(at).intValue();
	nimgs = Integer.valueOf(getParameter("nimgs")).intValue();
	addMouseListener(this);
    }

    /**
     * Run the image loop. This methods is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	loop = new ImageLoop(getDocumentBase(), dir, this, nimgs);

	if (loop.nimgs > 1) {
	    while (getSize().width > 0 && getSize().height > 0 && kicker != null) {
		if (++loopslot >= loop.nimgs) {
		    loopslot = 0;
		}
		repaint();
		try {
		    Thread.sleep(100 + ((loopslot == 0) ? pause : 0));
		} catch (InterruptedException e) {
		    return;
		}
	    }
	}
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	update(g);
    }
    public void update(Graphics g) {
	if ((loop != null) && (loop.imgs != null) &&
	    (loopslot < loop.nimgs) && (loop.imgs[loopslot] != null)) {
	    if (im == null) {
		im = createImage(getSize().width, getSize().height);
		offscreen = im.getGraphics();
		offscreen.setColor(Color.white);
	    }
	    offscreen.fillRect(0, 0, getSize().width, getSize().height);
	    offscreen.drawImage(loop.imgs[loopslot], 0, 0, this);
	    g.drawImage(im, 0, 0, this);
	}
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
	if (kicker == null) {
	    kicker = new Thread(this);
	    kicker.start();
	}
    }

    /**
     * Stop the applet. The thread will exit because kicker is set to null.
     */
    public void stop() {
	kicker = null;
    }

    

  public void mouseClicked(MouseEvent e) {
  }

  /**
   * Pause the thread when the user clicks the mouse in the applet.
   */  
  public void mousePressed(MouseEvent e) {
    if (threadSuspended)
      kicker.resume();
    else
      kicker.suspend();
    threadSuspended = !threadSuspended;
    e.consume();
  }
  
  public void mouseReleased(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }
  
  public  void mouseExited(MouseEvent e) {
  }
  

public String getAppletInfo() {
    return "Title: ImageLoopItem\nAuthor: James Gosling\nA simple Item class to play an image loop.";
  }
  
  public String[][] getParameterInfo() 
  {
    String pinfo[][] = {
    {"img", "string", "The directory or URL from which the images are loaded."},
    {"pause", "integer", "The length of the pause between revs."},
    {"nimgs", "integer", "The number of images."}
    }; 
    return pinfo;
  }
}
