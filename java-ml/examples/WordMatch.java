/*
 * @(#)WordMatch.java	1.6	07/17/97 11:01:45
 * @author Patrick Chan
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
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
 * modified for 1.1 compatibility on 7/15/97 by
 * @author Chris Bucchere
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;

public class WordMatch 
  extends java.applet.Applet
  implements MouseListener 
{
  /* Number of displayed cards and word. */
  int numCards = 4;
  
  /* Currently selected language. */
  int curLanguage;
  
  /* Width and size().height of language box. */
  int languageBoxW, languageBoxH;
  
  /* Size of border around language box. */
  int languageBoxB;
  
  /* Line spacing for wordFont. */
  int languageBoxSp;
  
  /* Font of languageBox. */
  Font languageFont;
  FontMetrics languageFontMt;
  
  /** 
   * Cards
   */ 
  
  /* card[i] refers to the i'th word. */
  int cards[];
  
  /* Currently selected card. */
  int curCard;
  
  /* Size of cards. */
  int cardW, cardH;
  
  /* Location and size of card box. */
  int cardBoxX, cardBoxY, cardBoxW, cardBoxH;
  
  /* Sp between cards. */
  int cardBoxSp;
  
  /** 
   * Sound box
   */ 
  
  /* Location of sound box. */
  int soundBoxX, soundBoxY;
  
  /* Sound image. */
  Image soundImage;
  int soundImageWidth = 32;
  int soundImageHeight = 32;
  
  /** 
   * Words
   */ 
  
  /* word[i] refers to the i'th card. */
  int words[];
  
  /* Location and size of word box. */
  int wordBoxX, wordBoxY, wordBoxW;
  
  /* Font used to paint words.. */
  Font wordFont;
  FontMetrics wordFontMt;
  
  /**
   * Scoring
   */
  
  /* Location and size of score box. */
  int scoreBoxX, scoreBoxY, scoreBoxW, scoreBoxH;
  
  /* Message to be displayed next to the score box. */
  String msg;
  
  /* If true, the user has successfully matched all the words. */
  boolean done;
  
  /** 
   * Links
   */ 
  
  /* links[i] == j -> cards[i] is linked to words[j]. */
  int links[];
  
  /** 
   * Misc
   */ 
  
  /* Images of pictures.  There is one image for each different word. 
     -1 => not loaded. */
  Image images[];
  int imageWidth[];
  int imageHeight[];
  
  /* Average size().width and size().height of an M character in the font. */
  int charW, charH;
  
  /**
   * Initialize the applet. Resize and load images.
   */
  public void init() {
    int maxWidth = 0;
    
    cards = new int[numCards];
    words = new int[numCards];
    links = new int[numCards];
    images = new Image[englishWords.length];
    imageWidth = new int[englishWords.length];
    imageHeight = new int[englishWords.length];
    curLanguage = 0;
    
    // Setup the language selection box.
    languageFont = new java.awt.Font("Helvetica", Font.PLAIN, 14);
    languageFontMt = getFontMetrics(languageFont);
    languageBoxSp = 3;
    
    for (int i=0; i<languages.length; i++) {
      int w = languageFontMt.stringWidth(languages[i]);
      if (w > maxWidth) {
	maxWidth = w;
      }
    }
    languageBoxB = 2;
    languageBoxW = maxWidth + 2 * languageFontMt.charWidth('M') + 
      2 * languageBoxB;
    languageBoxH = languages.length
      * (languageFontMt.getHeight() + languageBoxSp) +
      languageBoxSp + 2 * languageBoxB;
    
    cardW = cardH = 64;
    cardBoxSp = 5;
    cardBoxX = languageBoxW + cardW;
    cardBoxY = 0;
    cardBoxW = cardW;
    cardBoxH = (cardH + cardBoxSp) * (numCards - 1) + cardH;
    
    // Setup sound box.
    soundImage = getImage(getDocumentBase(), "images/sound.gif");
    soundBoxX = (languageBoxW - soundImageWidth) / 2;
    soundBoxY = languageBoxH + 10;
    soundBoxX = languageBoxW + 10;
    soundBoxY = (languageBoxH/2);
    
    // Setup word box.
    wordFont = new java.awt.Font("Helvetica", Font.PLAIN, 18);
    wordFontMt = getFontMetrics(wordFont);
    charW = wordFontMt.charWidth('M');
    charH = wordFontMt.getHeight();
    wordBoxX = cardBoxX + cardBoxW * 2;
    wordBoxY = 0;
    wordBoxW = 0;
    for (int i=0; i<languages.length; i++) {
      for (int j=0; j<englishWords.length; j++) {
	int w = wordFontMt.stringWidth(dictionary[i][j]);
	if (w > wordBoxW) {
	  wordBoxW = w;
	}
      }
    }
    wordBoxW += 2 * charW;
    
    // Setup score box.
    scoreBoxX = 0;
    scoreBoxY = languageBoxH + charH;
    scoreBoxW = wordFontMt.stringWidth("Score") + 2 * charW;
    scoreBoxH = charH + 4;
    newRound();
    
    resize(wordBoxX + wordBoxW + 1, scoreBoxY + scoreBoxH + 1);
    addMouseListener(this);
  }
  
  public boolean imageUpdate(Image img, int infoflags,
			     int x, int y, int width, int height) {
    if ((infoflags & (WIDTH | HEIGHT)) != 0) {
      // This is a waste of cycles.
      // Maybe I should use a hash table.
      for (int i=0; i<images.length; ++i) {
	if (img == images[i]) {
	  if ((infoflags & WIDTH) != 0) {
	    imageWidth[i] = width;
	  }
	  if ((infoflags & HEIGHT) != 0) {
	    imageHeight[i] = height;
	  }
	}
      }
    }
    return super.imageUpdate(img, infoflags, x, y, width, height);
  }
  
  /**
   * Paint the screen.
   */
  public void paint(Graphics g) {
    int x, y;
    
    // Paint language box.
    g.setFont(languageFont);
    y = languageFontMt.getHeight() + languageBoxB;
    for (int i=0; i<languages.length; i++) {
      if (i == curLanguage) {
	g.setColor(Color.red);
      } else {
	g.setColor(Color.black);
      } 
      x = languageFontMt.stringWidth(languages[i]);
      x = (languageBoxW - x) / 2 + languageBoxB;
      g.drawString(languages[i], x, y);
      y += languageFontMt.getHeight() + languageBoxSp;
    }
    g.setColor(Color.black);
    g.draw3DRect(0, 0, languageBoxW, languageBoxH, true);
    
    // Paint sound box.
    if (soundImage != null
	&& !languages[curLanguage].equals("Geekspeak")) {
      g.drawImage(soundImage, soundBoxX, soundBoxY, this);
    }
    
    // Paint words.
    g.setFont(wordFont);
    x = wordBoxX;
    y = (cardH - charH) / 2 + charH;
    for (int i=0; i<numCards; i++) {
      g.drawString(dictionary[curLanguage][words[i]], x, y);
      y += cardH + cardBoxSp;
    }
    
    // Paint links.
    g.setColor(Color.black);
    for (int i=0; i<numCards; i++) {
      if (links[i] >= 0) {
	int y2 = links[i] * (cardH + cardBoxSp) + cardH / 2;
	
	y = i * (cardH + cardBoxSp) + cardH / 2;
	g.drawLine(cardBoxX + cardBoxW + 2, y,
		   wordBoxX - 2, y2);
      }
    }
    
    // Paint score box.
    g.draw3DRect(scoreBoxX, scoreBoxY, scoreBoxW, scoreBoxH, true);
    if (done) {
      g.drawString("New", scoreBoxX + charW, scoreBoxY + charH);
    } else {
      g.drawString("Score", scoreBoxX + charW, scoreBoxY + charH);
    }
    if (msg != null) {
      g.setFont(new java.awt.Font("Helvetica", Font.PLAIN, 16));
      g.setColor(Color.blue);
      g.drawString(msg, scoreBoxW + charW, scoreBoxY + charH);
      g.setFont(wordFont);
    }
    
    // Paint cards.
    x = cardBoxX;
    y = 0;
    for (int i=0; i<numCards; i++) {
      if (images[cards[i]] == null) {
	// need to load the image.
	images[cards[i]] = getImage(getDocumentBase(), "images/" + 
				    englishWords[cards[i]] + ".gif");
	images[cards[i]].getWidth(this);
	images[cards[i]].getHeight(this);
      } 
      if (images[cards[i]] == null) {
	g.draw3DRect(x, y, cardW, cardH, true);
      } else if (imageWidth[cards[i]]>0 && imageHeight[cards[i]]>0) {
	int offsetX = (cardW - imageWidth[cards[i]]) / 2;
	int offsetY = (cardH - imageHeight[cards[i]]) / 2;
	
	g.drawImage(images[cards[i]], x + offsetX, y + offsetY, this);
      }
      
      if (i == curCard) {
	g.setColor(Color.red);
	g.drawRect(x, y, cardW, cardH);
	g.setColor(Color.black);
      }
      y += cardH + cardBoxSp;
    }
  }
  /**
   * Returns an integer in the interval [0..max).
   */
  int random(int max) {
    return (int)Math.floor(Math.random() * max);
  }
  
  /**
   * Starts a new round.
   */
  public void newRound() {
    msg = null;
    done = false;
    
    // Pick new cards.
    for (int i=0; i<numCards; i++) {
      int r;
      boolean found;
      
      do {
	r = random(englishWords.length);
	found = false;
	for (int j=0; j<i; j++) {
	  if (cards[j] == r) {
	    found = true;
	  }
	}
      } while (i > 0 && found);
      cards[i] = r;
      words[i] = r;
      links[i] = -1;
    }
    curCard = 0;
    
    // Now mix up the words.
    for (int i=0; i<50; i++) {
      int r1 = random(numCards);
      int r2 = random(numCards);
      int t = words[r1];
      words[r1] = words[r2];
      words[r2] = t;
    }
  }
  
  /**
   * Start the applet.
   */
  public void start() {
  }
  
  /**
   * Stop the applet.	 
   */
  public void stop() {
  }
  
  
  /* Added by K.A. Smith 10/25/95 */
  public String getAppletInfo() {
    return "Author: Patrick Chan\nVersion: 1.6";
  }
  
  
  /* List of words in various languages */
  String englishWords[] = {
    "house", "book", "java", "lips", "airplane", "UnitedStates",
    "apple", "clock", "phone", "star", "flower", "television",
    "car", "dog", "bug", "bird"};
  String cantoneseWords[] = {
    "fohng uk", "syu", "ga fe", "seuhn", "fei gei", "meih gwok",
    "pihng gwo", "jung", "dihn wa", "sing", "fa", "dihn sih",
    "che", "gau", "chuhng", "jeuk"};
  String dutchWords[] = {
    "huis", "boek", "koffie", "lippen", "vliegtuig", "Verenigde Staten",
    "appel", "klok", "telefoon", "ster", "bloem", "televisie",
    "auto", "hond", "insekt", "vogel"};
  String frenchWords[] = {
    "maison", "livre", "cafe", "levres", "avion", "Etats Unis",
    "pomme", "horloge", "telephone", "astre", "fleur", "television",
    "voiture", "chien", "insecte", "oiseaux"};
  String germanWords[] = {
    "Haus", "Buch", "Kaffee", "Lippen", "Flugzeug", "Vereinigte Staaten",
    "Apfel", "Uhr", "Telephon", "Stern", "Blume", "Fernsehgeraet",
    "Auto", "Hund", "Kaefer", "Vogel"};
  String geekspeakWords[] = {
    "lab", "FM (of RTFM)", "Peet's", "programming languages", "wings", "www.usa.com",
    "food", "MHz", "cellular", "splat (aka asterisk)", "inflorescence", "tube",
	"wheels", "386", "feature", "finger"};
  String hebrewWords[] = {
    "ba'it", "sefer", "java", "s'fata'im", "matos", "ar'tzot ha'brit",
    "tapu'ach", "sha'on", "tele'phon", "kochav", "pe'rach", "televizia",
    "m'chonit", "kelev", "cha'rak", "tsipor"};
  String hindiWords[] = {
    "ghar", "kitab", "coffee", "honth", "hawai jahaj", "America",
    "seb", "ghadi", "doorbhash", "sitara", "phool", "television",
    "gaadi", "kutta", "keeda", "chidiya"};
  String italianWords[] = {
    "house", "book", "coffee", "lips", "airplane", "UnitedStates",
    "apple", "clock", "phone", "star", "flower", "television",
    "car", "dog", "bug", "bird"};
  String japaneseWords[] = {
    "ie", "hon", "kouhii", "kuchibiru", "hikouki", "Amerika",
    "ringo", "tokei", "denwa", "hoshi", "hana", "terebi",
    "kuruma", "inu", "mushi", "tori"};
  String mandarinWords[] = {
    "fang wu", "shu", "ka fei", "chun", "fei ji", "mei guo",
    "ping guo", "zhong", "dian hua", "xing", "hua", "dian shi",
    "che", "gou", "chong", "niao"};
  String portugueseWords[] = {	
    "casa", "livro", "cafe", "lapios", "aviao", "Estados Unidos",
    "maca", "relogio", "telefone", "estrela", "flor", "televisao",
    "carro", "cachorro", "bicho", "passarinho"};
  String pigLatinWords[] = {
    "ouse-hay", "ook-bay", "ava-jay", "ips-lay", "airplane-gay", "United-gay Ates-Stay",
    "apple-gay", "ock-clay", "one-phay", "ar-stay", "ower-flay", "elevision-tay",
    "ar-cay", "og-day", "ug-bay", "ird-bay"};
  String spanishWords[] = {
    "casa", "libro", "cafe", "labios", "avion", "Estados Unidos",
    "manzana", "reloj", "telefono", "estrella", "flor", "television",
    "coche", "perro", "insecto", "pajaro"};
  String swissGermanWords[] = {
    "Huus", "Buech", "Cafi", "Lippe", "Flugzueg", "Vereinigti Staate",
    "Oepfel", "Uhr", "Telephon", "Stern", "Blueme", "Fernsehapparat",
    "Auto", "Hund", "Chaefer", "Vogel"};
  
  /* List of languages. */
  String languages[] = {
    "English",
    "Cantonese",
    "Dutch",
    "French",
    "Geekspeak",
    "German",
    "Hebrew",
    "Hindi",
    "Japanese",
    "Mandarin",
    "PigLatin",
    "Portuguese",
    "Spanish",
    "SwissGerman"};
  
  String dictionary[][] = {
    englishWords,
    cantoneseWords,
    dutchWords,
    frenchWords,
    geekspeakWords,
    germanWords,
    hebrewWords,
    hindiWords,
    japaneseWords,
    mandarinWords,
    pigLatinWords,
    portugueseWords,
    spanishWords,
    swissGermanWords};
  
  /* mouse tracking methods */
  public void mouseClicked(MouseEvent e) {
  }
  
  public void mouseReleased(MouseEvent e) {
    int sel;
    int x = e.getX();
    int y = e.getY();
    
    if (!done) {
      msg = null;
    }
    if (x < languageBoxW && y < languageBoxH) {
      // In language box.
      y -= languageBoxB;
      sel = y / (languageFontMt.getHeight() + languageBoxSp);
      sel = Math.min(sel, languages.length - 1);
      if (curLanguage == sel) {
	newRound();
      }
      curLanguage = sel;
      repaint();
    } else if (x > soundBoxX && y > soundBoxY 
	       && x < soundBoxX + soundImageWidth
	       && y < soundBoxY + soundImageHeight) {
      // In sound box.
      if (languages[curLanguage].equals("Geekspeak")) {
	// No voices for these languages yet.
	play(getDocumentBase(), "audio/ding.au");
      } else {
	play(getDocumentBase(), "audio/" + englishWords[cards[curCard]] 
	     + "." + languages[curLanguage] + ".au");
      }
    } else if (x > cardBoxX && y > cardBoxY 
	       && x < cardBoxX + cardBoxW
	       && y < cardBoxY + cardBoxH) {
      // In card box.
      y -= cardBoxY;
      sel = y / (cardH + cardBoxSp);
      sel = Math.min(sel, numCards - 1);
      if (sel != curCard) {
	curCard = sel;
	repaint();
      }
    } else if (!done && x > wordBoxX && y > wordBoxY 
	       && x < wordBoxX + wordBoxW
	       && y < wordBoxY + cardBoxH) {
      // In word box.
      sel = y / (cardH + cardBoxSp);
      sel = Math.min(sel, numCards - 1);
      if (links[curCard] != sel) {
	// Break an old link if necessary.
	for (int i=0; i<numCards; i++) {
	  if (links[i] == sel) {
	    links[i] = -1;
	  }
	}
	links[curCard] = sel;
	repaint();
      }
	} else if (x > scoreBoxX && y > scoreBoxY 
		   && x < scoreBoxX + scoreBoxW
		   && y < scoreBoxY + scoreBoxH) {
	  // In score box.
	  if (done) {
	    newRound();
	    repaint();
	    e.consume();
	    return;
	  }
	  int count = 0;
	  for (int i=0; i<numCards; i++) {
	    if (links[i] == -1) {
	      msg = "You have not yet matched all the words.";
	      repaint();
	      e.consume();
	      return;
	    }
	    if (cards[i] == words[links[i]]) {
	      count++;
	    }
	  }
	  if (count == numCards) {		
	    msg = "Congratulations, they're all right!";
	    done = true;
	  } else if (count == 1) {
	    msg = "There is only 1 correct match.";
	  } else if (count == 0) {
	    msg = "There are no correct matches.";
	  } else {
	    msg = "There are " + count + " correct matches.";
	  }
	  repaint();
	}
    e.consume();
    return;
  }
   
  public void mousePressed(MouseEvent e) {
  }
  
  public void mouseEntered(MouseEvent e) {
  }
  
  public void mouseExited(MouseEvent e) {
  }
}

