/*
 * @(#)AudioItem.java	1.26f 95/03/22 James Gosling
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
 */

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;

import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.applet.AudioClip;

/**
 * A simple Item class to play an audio clip.
 * @author James Gosling
 */

public class AudioItem extends java.applet.Applet implements MouseListener {
    /**
     * The sounds to be played.
     */
    private String sounds;

    /**
     * The index of the next sound in the sounds strings.
     */
    private int index;

    /**
     * The currently playing audio stream, or null
     * when no audio is playing.
     */
    private AudioClip audio;

    /**
     * Play the next sound. The sound URLS are obtained
     * from the "snd" attribute. You can specify a list
     * of them by seperating the sounds by '|'s.<p>
     * Note that the URL is constructed relative to the
     * getDocumentBase(), that is because the url is obtained
     * from within the document
     */
    public void next() {
	try {
	    if (audio != null) {
		audio.stop();
		audio = null;
	    }
	    
	    String url = sounds;
	    if (sounds.indexOf('|') >= 0) {
		int start = index;
		if ((index = sounds.indexOf('|', index)) < 0) {
		    url = sounds.substring(start);
		    index = start;
		} else {
		    url = sounds.substring(start, index++);
		}
	    }
	    if (url.length() > 0) {
		audio = getAudioClip(new URL(getDocumentBase(), url));
		audio.play();
	    }
	} catch(Exception e) {
	}
    }

    /**
     * Initialize the applet. First resize it, then get the
     * "snd" attribute.
     */
    public void init() {

        addMouseListener(this);
	setSize(10, 12);

	sounds = getParameter("snd");
	if (sounds == null) {
	    sounds = "doc:/demo/audio/ding.au";
	}
    }

    /**
     * When the applet is started play the next sound.
     */
    public void start() {
	next();
    }

    /**
     * When the applet is stopped, stop playing the current sound.
     */
    public void stop() {
	if (audio != null) {
	    audio.stop();
	    audio = null;
	}
    }

  /* MouseListener methods */
  public void mouseClicked(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
    e.consume();
    next();  // When the user clicks in the applet, play the next sound.
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

    /**
     * Paint an audio icon.
     */
    public void paint(Graphics g) {
	double f = ((double)(getSize().height - 1)) / ((getSize().width - 1) * 2);
	int offset = getSize().height / 2;
	for (int i = getSize().width - 1; i >= 0; i -= 3) {
	    int h = (int)(i * f);
	    g.drawLine(i, offset - h, i, offset + h);
	}
    }
  public String getAppletInfo() {
    return "Title: AudioItem\nAuthor: James Gosling\nA simple Item class to play an audio clip.";
  }  
  
  public String[][] getParameterInfo() 
  {
    String pinfo[][] = 
    {
      {"snd", "string", "The sound URL(s).You can specify a list of them by seperating the sounds by '|'s. Note that the URL is constructed relative to the getDocumentBase() because the url is obtained from within the document."}
    };
    return pinfo;
  }
}
