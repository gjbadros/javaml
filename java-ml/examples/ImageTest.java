/*
 * @(#)ImageTest.java	1.8 96/02/27
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
 */
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.applet.Applet;

public class ImageTest extends Applet {
    public void init() {
	setLayout(new BorderLayout());
	add("Center", new ImagePanel(this));
	add("North", new ImageHelp());
	setVisible(true);
    }
    public String getAppletInfo() {
        return "A simple image manipulation tool.";
    }
}

class ImageHelp extends Panel {
    public ImageHelp() {
	setLayout(new GridLayout(5, 1));
	add(new Label("Move the images with the mouse, with < > ^ v, or with [L]eft/[R]ight/[U]p/[D]own",
		      Label.CENTER));
	add(new Label("Resize the images with +/-",
		      Label.CENTER));
	add(new Label("[T]oggle a color filter with the T key",
		      Label.CENTER));
	add(new Label("Change the alpha with (Shift)+/-",
		      Label.CENTER));
	add(new Label("Rotate the image with (Shift)<> or (Shift)[L]eft/[R]ight",
		      Label.CENTER));
    }
}

class ImagePanel extends Panel {
    Applet applet;

    public ImagePanel(Applet app) {
	applet = app;
	setLayout(new BorderLayout());
	Panel grid = new Panel();
	grid.setLayout(new GridLayout(0, 2));
	add("Center", grid);
	grid.add(new ImageCanvas(applet, makeDitherImage(), 0.5));
	Image joe = applet.getImage(applet.getDocumentBase(),
				    "images/joe.surf.yellow.small.gif");
	grid.add(new ImageCanvas(applet, joe, 1.0));
	setBounds(0, 0, 20, 20);
    }

    Image makeDitherImage() {
	int w = 100;
	int h = 100;
	int pix[] = new int[w * h];
	int index = 0;
	for (int y = 0; y < h; y++) {
	    int red = (y * 255) / (h - 1);
	    for (int x = 0; x < w; x++) {
		int blue = (x * 255) / (w - 1);
		pix[index++] = (255 << 24) | (red << 16) | blue;
	    }
	}
	return applet.createImage(new MemoryImageSource(w, h, pix, 0, w));
    }
}

class ImageCanvas 
extends Canvas 
implements ImageObserver, KeyListener, MouseListener, 
  MouseMotionListener, FocusListener {
    double 	hmult = 0;
    int		xadd = 0;
    int		yadd = 0;
    int		xprev = 0;
    int		yprev = 0;
    int		imgw = -1;
    int		imgh = -1;
    int		xoff = 0;
    int		yoff = 0;
    int		scalew = -1;
    int		scaleh = -1;
    boolean	focus = false;
    boolean	usefilter = false;
    static final int numalphas = 9;
    int		alpha = numalphas - 1;
    static final int numrotations = 8;
    int		rotation = 0;
    ImageFilter colorfilter;
    ImageFilter alphafilters[] = new ImageFilter[numalphas];
    RotateFilter rotfilters[] = new RotateFilter[numrotations];
    Image	origimage;
    Image	curimage;
    Applet	applet;

    public ImageCanvas(Applet app, Image img, double mult) {
        applet = app;
	origimage = img;
	hmult = mult;
	pickImage();
	setBounds(0, 0, 100, 100);
	addMouseListener(this);
	addMouseMotionListener(this);
	addKeyListener(this);
	addFocusListener(this);
    }
    //1.1 event handling
  public void focusGained(FocusEvent e) {
    focus = true;
    repaint();
  }
    
  public void focusLost(FocusEvent e) {
    focus = false;
    repaint();
  }

  public void keyPressed(KeyEvent e) {
  }
    
  public void keyTyped(KeyEvent e) {
    char key = e.getKeyChar();
    switch(key) 
      {
      case 't':
      case 'T':
	usefilter = !usefilter;
	pickImage();
	repaint();
	e.consume();
	break;
      case '^':
      case '6':
      case 'u':
      case 'U':
	yadd -= 5;
	repaint();
	e.consume();
	break;
      case 'v':
      case 'V':
      case 'd':
      case 'D':
	yadd += 5;
	repaint();
	e.consume();
	break;
      case '>':
      case 'R':
	rotation--;
	if (rotation < 0) {
	  rotation = numrotations - 1;
	}
	pickImage();
	scalew = scaleh = -1;
	repaint();
	e.consume();
	break;
      case '.':
      case 'r':
	xadd += 5;
	repaint();
	e.consume();
	break;
      case '<':
      case 'L':      
        rotation++;
	if (rotation >= numrotations) {
	  rotation = 0;
	}
	pickImage();
	scalew = scaleh = -1;
	repaint();
	e.consume();
	break;
      case ',':
      case 'l':
	xadd -= 5;
	repaint();
	e.consume();
	break;
      case '+':
	if (++alpha > numalphas - 1) {
	  alpha = numalphas - 1;
	}
	pickImage();
	repaint();
	e.consume();
	break;	      
      case '=':
	hmult *= 1.2;
	scalew = scaleh = -1;
	repaint();
	e.consume();
	break;	      
      case '-':
	hmult /= 1.2;
	scalew = scaleh = -1;
	repaint();
	e.consume();
	break;
      case '_':
	if (--alpha < 0) {
	  alpha = 0;
	}
	pickImage();
	repaint();
	e.consume();
	break;
      }
  }
    
  public void keyReleased(KeyEvent e) {

  }
    
  public void mouseClicked(MouseEvent e) {
  }
    
  public void mousePressed(MouseEvent e) {
    xprev = e.getX();
    yprev = e.getY();
    e.consume();
  }
    
  public void mouseReleased(MouseEvent e) {
    e.consume();
  }
    
  public void mouseEntered(MouseEvent e) {
    requestFocus();
    e.consume();
  }
    
  public void mouseExited(MouseEvent e) {
  }
    
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    
    xadd += x - xprev;
    yadd += y - yprev;
    xprev = x;
    yprev = y;
    repaint();
    e.consume();
  }
    
  public void mouseMoved(MouseEvent e) {
  }
 
    public void paint(Graphics g) {
	Rectangle r = getBounds();
	int hlines = r.height / 10;
	int vlines = r.width / 10;

	if (focus) {
	    g.setColor(Color.red);
	} else {
	    g.setColor(Color.darkGray);
	}
	g.drawRect(0, 0, r.width-1, r.height-1);
	g.drawLine(0, 0, r.width, r.height);
	g.drawLine(r.width, 0, 0, r.height);
	g.drawLine(0, r.height / 2, r.width, r.height / 2);
	g.drawLine(r.width / 2, 0, r.width / 2, r.height);
	if (imgw < 0) {
	    imgw = curimage.getWidth(this);
	    imgh = curimage.getHeight(this);
	    if (imgw < 0 || imgh < 0) {
		return;
	    }
	}
	if (scalew < 0) {
	    if (rotation == 0) {
		scalew = imgw;
		scaleh = imgh;
	    } else {
		Rectangle rect = new Rectangle(0, 0, imgw, imgh);
		rotfilters[rotation].transformBBox(rect);
		xoff = rect.x;
		yoff = rect.y;
		scalew = rect.width;
		scaleh = rect.height;
	    }
	    scalew = (int) (scalew * hmult);
	    scaleh = (int) (scaleh * hmult);
	    xoff = (imgw - scalew) / 2;
	    yoff = (imgh - scaleh) / 2;
	}
	if (imgw != scalew || imgh != scaleh) {
	    g.drawImage(curimage, xadd + xoff, yadd + yoff,
			scalew, scaleh, this);
	} else {
	    g.drawImage(curimage, xadd + xoff, yadd + yoff, this);
	}
    }

    static final long updateRate = 100;

    public synchronized boolean imageUpdate(Image img, int infoflags,
					    int x, int y, int w, int h) {
	if (img != curimage) {
	    return false;
	}
	boolean ret = true;
	boolean dopaint = false;
	long updatetime = 0;
	if ((infoflags & WIDTH) != 0) {
	    imgw = w;
	    dopaint = true;
	}
	if ((infoflags & HEIGHT) != 0) {
	    imgh = h;
	    dopaint = true;
	}
	if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
	    dopaint = true;
	    ret = false;
	} else if ((infoflags & SOMEBITS) != 0) {
	    dopaint = true;
	    updatetime = updateRate;
	}
	if ((infoflags & ERROR) != 0) {
	    ret = false;
	}
	if (dopaint) {
	    repaint(updatetime);
	}
	return ret;
    }

    public synchronized Image pickImage() {
	ImageProducer src = origimage.getSource();
	if (alpha != numalphas - 1) {
	    ImageFilter imgf = alphafilters[alpha];
	    if (imgf == null) {
		int alphaval = (alpha * 255) / (numalphas - 1);
		imgf = new AlphaFilter(alphaval);
		alphafilters[alpha] = imgf;
	    }
	    src = new FilteredImageSource(src, imgf);
	}
	if (rotation != 0) {
	    RotateFilter imgf = rotfilters[rotation];
	    if (imgf == null) {
		double angle = (2 * Math.PI * rotation) / numrotations;
		imgf = new RotateFilter(angle);
		rotfilters[rotation] = imgf;
	    }
	    src = new FilteredImageSource(src, imgf);
	}
	if (usefilter) {
	    if (colorfilter == null) {
		colorfilter = new RedBlueSwapFilter();
	    }
	    src = new FilteredImageSource(src, colorfilter);
	}
	Image choice;
	if (src == origimage.getSource()) {
	    choice = origimage;
	} else {
	    choice = applet.createImage(src);
	}
	if (curimage != choice) {
	    if (curimage != null && curimage != origimage) {
		curimage.flush();
	    }
	    curimage = choice;
	}
	return choice;
    }
}

class RedBlueSwapFilter extends RGBImageFilter {
    public RedBlueSwapFilter() {
	canFilterIndexColorModel = true;
    }

    public void setColorModel(ColorModel model) {
	if (model instanceof DirectColorModel) {
	    DirectColorModel dcm = (DirectColorModel) model;
	    int rm = dcm.getRedMask();
	    int gm = dcm.getGreenMask();
	    int bm = dcm.getBlueMask();
	    int am = dcm.getAlphaMask();
	    int bits = dcm.getPixelSize();
	    dcm = new DirectColorModel(bits, bm, gm, rm, am);
	    substituteColorModel(model, dcm);
	    consumer.setColorModel(dcm);
	} else {
	    super.setColorModel(model);
	}
    }

    public int filterRGB(int x, int y, int rgb) {
	return ((rgb & 0xff00ff00)
		| ((rgb & 0xff0000) >> 16)
		| ((rgb & 0xff) << 16));
    }
}

class AlphaFilter extends RGBImageFilter {
    ColorModel origmodel;
    ColorModel newmodel;

    int alphaval;

    public AlphaFilter(int alpha) {
	alphaval = alpha;
	canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
	int alpha = (rgb >> 24) & 0xff;
	alpha = alpha * alphaval / 255;
	return ((rgb & 0x00ffffff) | (alpha << 24));
    }
}
