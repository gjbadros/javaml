/*
 * @(#)Hangman.java	1.5  03 Apr 1996 10:56:25
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 *
 * @author Patrick Chan
 * modified to use MediaTracker by
 * @author Herb Jellinek
 * modified for 1.1 by
 * @author Chris Bucchere
 *
 */

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import java.net.*;

public class Hangman 
extends java.applet.Applet 
implements Runnable, MouseListener, KeyListener {
  
  /* This is the maximum number of incorrect guesses. */
  final int maxTries = 5;
  
  /* This is the maximum length of a secret word. */
  final int maxWordLen = 20;
  
  /* This buffer holds the letters in the secret word. */
  char secretWord[];
  
  /* This is the length of the secret word. */
  int secretWordLen;
  
  /* This buffer holds the letters which the user typed
     but don't appear in the secret word. */
  char wrongLetters[];
  
  /* This is the current number of incorrect guesses. */
  int wrongLettersCount;
  
  /* This buffer holds letters that the user has successfully
     guessed. */
  char word[];
  
  /* Number of correct letters in 'word'. */
  int wordLen;
  
  /* This is the font used to paint correctly guessed letters. */
  Font wordFont;
  FontMetrics wordFontMetrics;
  
  /* This is the MediaTracker that looks after loading the images. */
  MediaTracker tracker;
  
  /* These are the various classes of images we load */
  static final int DANCECLASS = 0;
  static final int HANGCLASS = 1;
  
  /* This is the sequence of images for Duke hanging on the gallows. */
  Image hangImages[];
  final int hangImagesWidth = 39;
  final int hangImagesHeight = 58;
  
  // Dancing Duke related variables
  
  /* This thread makes Duke dance. */
  Thread danceThread;
  
  /* These are the images that make up the dance animation. */
  Image danceImages[];
  private int danceImageWidths[] = { 70, 85, 87, 90, 87, 85, 70 };
  
  /* This is the maximum width and height of all the dance images. */
  int danceHeight = 68;
  
  /* This variable holds the number of valid images in danceImages. */
  int danceImagesLen = 0;
  
  /* These offsets refer to the dance images.  The dance images
     are not of the same size so we need to add these offset 
     in order to make the images "line" up. */
  private int danceImageOffsets[] = { 8, 0, 0, 8, 18, 21, 27 };
  
  /* This represents the sequence to display the dance images
     in order to make Duke "dance".  */
  private int danceSequence[] = { 3, 4, 5, 6, 6, 5, 6, 6, 5, 4, 3, 
				  2, 1, 0, 0, 1, 2, 2, 1, 0, 0, 1, 2 };
  
  /* This is the current sequence number.  -1 implies
     that Duke hasn't begun to dance. */
  int danceSequenceNum = -1;
  
  
  /* This variable is used to adjust Duke's x-position while
     he's dancing. */
  int danceX = 0;
  
  /* This variable specifies the currently x-direction of
     Duke's dance.  1=>right and -1=>left. */
  int danceDirection = 1;
  
  /* This is the stream for the dance music. */
  AudioClip danceMusic;
  
  /**
   * Initialize the applet. Resize and load images.
   */
  public void init() {
    int i;
    
    // create tracker
    tracker = new MediaTracker(this);
    
    // load in dance animation
    danceMusic = getAudioClip(getDocumentBase(), "audio/dance.au");
    danceImages = new Image[40];
    
    for (i = 1; i < 8; i++) {
      Image im = getImage(getDocumentBase(), "images/dancing-duke/T" + i + ".gif");
      tracker.addImage(im, DANCECLASS);
      danceImages[danceImagesLen++] = im;
    }
    
    // load in hangman image sequnce
    hangImages = new Image[maxTries];
    for (i=0; i<maxTries; i++) {
      Image im = getImage(getDocumentBase(), "images/hanging-duke/h"+(i+1)+".gif");
      tracker.addImage(im, HANGCLASS);
      hangImages[i] = im;
    }
    
    // initialize the word buffers.
    wrongLettersCount = 0;
    wrongLetters = new char[maxTries];
    
    secretWordLen = 0;
    secretWord = new char[maxWordLen];
    
    word = new char[maxWordLen];
    
    wordFont = new java.awt.Font("Courier", Font.BOLD, 24);
    wordFontMetrics = getFontMetrics(wordFont);
    
    resize((maxWordLen+1) * wordFontMetrics.charWidth('M') + maxWordLen * 3,
	   hangImagesHeight * 2 + wordFontMetrics.getHeight());
    
    addMouseListener(this);
    addKeyListener(this);
  }
  
  /**
   * Paint the screen.
   */
  public void paint(Graphics g) {
    int imageW = hangImagesWidth;
    int imageH = hangImagesHeight;
    int baseH = 10;
    int baseW = 30;
    Font font;
    FontMetrics fontMetrics;
    int i, x, y;
    
    // draw gallows pole
    g.drawLine(baseW/2, 0, baseW/2, 2*imageH - baseH/2);
    g.drawLine(baseW/2, 0, baseW+imageW/2, 0);
    
    // draw gallows rope
    g.drawLine(baseW+imageW/2, 0, baseW+imageW/2, imageH/3);
    
    // draw gallows base
    g.fillRect(0, 2*imageH-baseH, baseW, baseH);
    
    
    // draw list of wrong letters
    font = new java.awt.Font("Courier", Font.PLAIN, 15);
    fontMetrics = getFontMetrics(font);
    x = imageW + baseW;
    y = fontMetrics.getHeight();
    g.setFont(font);
    g.setColor(Color.red);
    for (i=0; i<wrongLettersCount; i++) {
      g.drawChars(wrongLetters, i, 1, x, y);
      x += fontMetrics.charWidth(wrongLetters[i]) 
+ fontMetrics.charWidth(' ');
    }
    
    if (secretWordLen > 0) {
      // draw underlines for secret word
      int Mwidth = wordFontMetrics.charWidth('M');
      int Mheight = wordFontMetrics.getHeight();
      g.setFont(wordFont);
      g.setColor(Color.black);
      x = 0;
      y = getSize().height - 1;
      for (i=0; i<secretWordLen; i++) {
	g.drawLine(x, y, x + Mwidth, y);
	x += Mwidth + 3;
      }
      
      // draw known letters in secret word
      x = 0;
      y = getSize().height - 3;
      g.setColor(Color.blue);
      for (i=0; i<secretWordLen; i++) {
	if (word[i] != 0) {
	  g.drawChars(word, i, 1, x, y);
	}
	x += Mwidth + 3;
      }
      
      if (wordLen < secretWordLen && wrongLettersCount > 0) {
	// draw Duke on gallows
	g.drawImage(hangImages[wrongLettersCount-1], 
                    baseW, imageH/3, this);
      }
    }
    
    }
  
  public void update(Graphics g) {
    if (wordLen == 0) {
      g.clearRect(0, 0, getSize().width, getSize().height);
      paint(g);
    } else if (wordLen == secretWordLen) {
      if (danceSequenceNum < 0) {
	g.clearRect(0, 0, getSize().width, getSize().height);
	paint(g);
	danceSequenceNum = 0;
      }
      updateDancingDuke(g);
    } else {
      paint(g);
    }
  }
  
  void updateDancingDuke(Graphics g) {
    int baseW = 30;
    int imageH = hangImagesHeight;
    int danceImageNum = danceSequence[danceSequenceNum];
    
    // first, clear Duke's current image
    g.clearRect(danceX+baseW, imageH*2 - danceHeight, 
		danceImageOffsets[danceImageNum]+danceImageWidths[danceImageNum], 
		danceHeight);
    
    // update dance position
    danceX += danceDirection;
    if (danceX < 0) {
      danceX = danceDirection = (int)Math.floor(Math.random() * 12) + 5;
    } else if (danceX + baseW > getSize().width / 2) {
      //danceDirection = -(int)Math.floor(Math.random() * 12) - 5;
      danceDirection *= -1;
    } else if (Math.random() > .9f) {
      danceDirection *= -1;
    }
    
    // update dance sequence
    danceSequenceNum++;
    if (danceSequenceNum >= danceSequence.length) {
      danceSequenceNum = 0;
    }
    
    // now paint Duke's new image
    danceImageNum = danceSequence[danceSequenceNum];
    if ((danceImageNum < danceImagesLen) && (danceImages[danceImageNum] != null)) {
      g.drawImage(danceImages[danceImageNum], 
		  danceX+baseW+danceImageOffsets[danceImageNum], 
		  imageH*2 - danceHeight, this);
    }
  }
  
  /**
   * Starts a new game.  Chooses a new secret word
   * and clears all the buffers
   */
  public void newGame() {
    int i;
    
    // stop animation thread.
    danceThread = null;
    
    // pick secret word
    String s = wordlist[(int)Math.floor(Math.random() * wordlist.length)];
    
    secretWordLen = Math.min(s.length(), maxWordLen);
    for (i=0; i<secretWordLen; i++) {
      secretWord[i] = s.charAt(i);
    }
    
    // clear word buffers
    for (i=0; i<maxWordLen; i++) {
      word[i] = 0;
    }
    wordLen = 0;
    for (i=0; i<maxTries; i++) {
      wrongLetters[i] = 0;
    }
    wrongLettersCount = 0;
    
    repaint();
  }
  
  /**
   * Start the applet.
   */
  public void start() {
    requestFocus();
    try {
      tracker.waitForID(HANGCLASS);
    } catch (InterruptedException e) {}
    tracker.checkAll(true);
    // Start a new game only if user has won or lost; otherwise
    // retain the same game.
    if (secretWordLen == wordLen || wrongLettersCount == maxTries) {
      newGame();
    }
  }
  
  /**
   * Stop the applet.  Stop the danceThread.
   */
  public void stop() {
    danceThread = null;
  }
  
  /**
   * Run Duke's dancing animation. This method is called by class Thread.
   * @see java.lang.Thread
   */
  public void run() {
    try {
      tracker.waitForID(DANCECLASS);
    } catch (InterruptedException e) {
    }
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    
    // start the dancing music.
    danceMusic.loop();
    
    // increment the sequence count and invoke the paint method.
    while (getSize().width > 0 && getSize().height > 0 && danceThread != null) {
      repaint();
      try {Thread.sleep(100);} catch (InterruptedException e){}
    }
    
    // The dance is done so stop the music.
    danceMusic.stop();
  }
  
  /**
   * Starts Duke's dancing animation.
   */
  private void startDukeDancing () {
    if (danceThread == null) {
      danceThread = new Thread(this);
      danceThread.start();
    }
  }
  
  // Added by Kevin A. Smith 10/25/95
  public String getAppletInfo() {
    return "Author: Patrick Chan\nVersion 1.5";
  }
  
  
  /* This is the hangman's limited word list. */
  String wordlist[] = {
    "abstraction",
    "ambiguous",
    "arithmetic",
    "backslash",
    "bitmap",
    "circumstance",
    "combination",
    "consequently",
    "consortium",
    "decrementing",
    "dependency",
    "disambiguate",
    "dynamic",
    "encapsulation",
    "equivalent",
    "expression",
    "facilitate",
    "fragment",
    "hexadecimal",
    "implementation",
    "indistinguishable",
    "inheritance",
    "internet",
    "java",
    "localization",
    "microprocessor",
    "navigation",
    "optimization",
    "parameter",
    "patrick",
    "pickle",
    "polymorphic",
    "rigorously",
    "simultaneously",
    "specification",
    "structure",
    "lexical",
    "likewise",
    "management",
    "manipulate",
    "mathematics",
    "hotjava",
    "vertex",
    "unsigned",
    "traditional"};
  
  /* key tracking methods */
  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) { 
    int i;
    boolean found = false;
    char key = e.getKeyChar();
      
    // start new game if user has already won or lost.
    if (secretWordLen == wordLen || wrongLettersCount == maxTries) {
      newGame();
      e.consume();
      return;
    }
    
    // check if valid letter
    if (key < 'a' || key > 'z') {
      play(getDocumentBase(), "audio/beep.au");
      e.consume();
      return;    
    }
    // check if already in secret word
    for (i=0; i<secretWordLen; i++) {
      if (key == word[i]) {
	found = true;
	play(getDocumentBase(), "audio/ding.au");
	e.consume();
	return;
      }
    }
    // check if already in wrongLetters
    if (!found) {
      for (i=0; i<maxTries; i++) {
	if (key == wrongLetters[i]) {
	  found = true;
	  play(getDocumentBase(), "audio/ding.au");
	  e.consume();
	  return;
	}
      }
    }
    // is letter in secret word? If so, add it.
    if (!found) {
      for (i=0; i<secretWordLen; i++) {
	if (key == secretWord[i]) {
	  word[i] = (char)key;
	  wordLen++;
	  found = true;
	}
      }
      if (found) {
	if (wordLen == secretWordLen) {
	  play(getDocumentBase(), "audio/whoopy.au");
	  startDukeDancing();
	} else {
	  play(getDocumentBase(), "audio/ah.au");
	}
      }
    }
    // wrong letter; add to wrongLetters
    if (!found) {
      if (wrongLettersCount < wrongLetters.length) {
	wrongLetters[wrongLettersCount++] = (char)key;
	if (wrongLettersCount < maxTries) {
	  play(getDocumentBase(), "audio/ooh.au");
	} else {
	  // show the answer
	  for (i=0; i<secretWordLen; i++) {
	    word[i] = secretWord[i];
	  }
	  play(getDocumentBase(), "audio/scream.au");
	}
      }
    }
    if (wordLen == secretWordLen) {
      danceSequenceNum = -1;
    }
    repaint();
    e.consume();
    return;
  }

  public void keyTyped(KeyEvent e) {  
  }
  
  /* mouse tracking methods */
  public void mouseClicked(MouseEvent e) {
  }
  
  public void mouseReleased(MouseEvent e) {
  }
  
  /**
   * Grab the focus and restart the game.
   */  
  public void mousePressed(MouseEvent e) {
    int i;
    
    // grab focus to get keyDown events
    requestFocus();
    
    if (secretWordLen > 0 && 
	(secretWordLen == wordLen || wrongLettersCount == maxTries)) {
      newGame();
    } else {
      play(getDocumentBase(), "audio/beep.au");
    }
    e.consume();
  }
         
  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

}

