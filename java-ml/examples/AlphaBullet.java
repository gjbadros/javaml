/*
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
 */

import java.applet.Applet;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;
import java.util.Hashtable;

/**
 * An applet class that generates colored bullets dynamically.
 *
 * The source for the bullet is any GIF image, preferably grayscale.
 * The colormap of the image is modified to blend between the background
 * and foreground colors based on the color components in the pixels
 * of the image.  Separate blending is done on the R, G, and B components
 * of the colors so a grayscale image will produce the most predictable
 * blending between background and foreground colors.
 *
 * @author 	Jim Graham
 * @version 	1.1, 01/23/96
 */

public
class AlphaBullet extends Applet {
    /**
     * Cache of colors indexed by name or color-spec.
     */
    static Hashtable colorCache = new Hashtable();

    /**
     * Cache of colored bullets indexed by fg,bg,alphaurl.
     */
    static Hashtable bulletCache = new Hashtable();

    /**
     * The final bullet image with the appropriate colors.
     */
    Image bulletImage;

    /**
     * Initialize the color hash table.
     */
    static {
	colorCache.put("red", Color.red);
	colorCache.put("green", Color.green);
	colorCache.put("blue", Color.blue);
	colorCache.put("cyan", Color.cyan);
	colorCache.put("magenta", Color.magenta);
	colorCache.put("yellow", Color.yellow);
	colorCache.put("orange", Color.orange);
	colorCache.put("pink", Color.pink);
	colorCache.put("white", Color.white);
	colorCache.put("black", Color.black);
    }

    /**
     * Generate a color from a specification string.  The string
     * can either be a color name which is looked up in the colorCache
     * hash table, or it can be a 6 digit hex number which is converted
     * to a color as RRGGBB.
     */
    private synchronized Color lookupColor(String colorName) {
	String lowerName = colorName.toLowerCase();
	Color newcolor = (Color) colorCache.get(lowerName);
	if (newcolor != null)
	    return newcolor;
	int colorval;
	try {
	    colorval = java.lang.Integer.parseInt(lowerName, 16);
	} catch (NumberFormatException e) {
	    return Color.black;
	}
	int r = (colorval >> 16) & 0xff;
	int g = (colorval >> 8) & 0xff;
	int b = colorval & 0xff;
	newcolor = new Color(r, g, b);
	colorCache.put(lowerName, newcolor);
	return newcolor;
    }

    /**
     * Create a bullet bitmap from foreground, background colors and
     * an alpha image.
     */
    private static synchronized Image makeBullet(AlphaBullet app,
						 Color fg, Color bg, URL url) {
	String hashName = "" + fg + bg + url;
	Image bulletImage = (Image) bulletCache.get(hashName);
	if (bulletImage != null) {
	    return bulletImage;
	}

	Image alphaImage = app.getImage(url);
	ImageFilter imgf = new AlphaColorFilter(fg, bg);
	ImageProducer imgp = new FilteredImageSource(alphaImage.getSource(),
						     imgf);
	bulletImage = app.createImage(imgp);
	bulletCache.put(hashName, bulletImage);
	return bulletImage;
    }

    /**
     * Initialize the applet. Get parameters.
     */
    public void init() {
	String attr = getParameter("img");
	String dir = (attr != null) ? attr : "images/wave-alpha.gif";
	URL url;
	try {
	    url = new URL(getDocumentBase(), dir);
	} catch (MalformedURLException e) {
	    return;
	}
	attr = getParameter("fg");
	Color fg = (attr != null) ? lookupColor(attr) : getForeground();
	attr = getParameter("bg");
	Color bg = (attr != null) ? lookupColor(attr) : getBackground();
	bulletImage = makeBullet(this, fg, bg, url);
    }

    /**
     * Paint the bullet or error indicator.
     */
    public void paint(Graphics g) {
	if (bulletImage == null
	    || (checkImage(bulletImage, this) & ERROR) != 0)
	{
	    g.setColor(Color.red);
	    g.fillRect(0, 0, size().width, size().height);
	} else {
	    g.drawImage(bulletImage, 0, 0, this);
	}
    }
}

/**
 * The AlphaColorFilter class implements in ImageFilter which
 * interpolates the colors in an image between a background
 * color and a foreground color based on the intensity of the
 * color components in the source image at that location.
 */
class AlphaColorFilter extends RGBImageFilter {
    /**
     * The foreground color used to replace the colors in the
     * image wherever the color intensities are brightest.
     */
    Color fgColor;

    /**
     * The background color used to replace the colors in the
     * image wherever the color intensities are darkest.
     */
    Color bgColor;

    /**
     * Construct an AlphaColorFilter object which performs
     * interpolation filtering between the specified foreground
     * and background colors.
     */
    public AlphaColorFilter(Color fg, Color bg) {
	fgColor = fg;
	bgColor = bg;
	canFilterIndexColorModel = true;
    }

    /**
     * Interpolate a color component, given a foreground, background
     * and alpha value components.
     */
    private int interpolate(int fg, int bg, int alpha) {
	int newval = (((fg - bg) * alpha) / 255) + bg;
	return newval;
    }

    /**
     * Filter an individual pixel in the image by interpolating
     * between the foreground and background colors depending on
     * the brightness of the pixel in the source image.
     */
    public int filterRGB(int x, int y, int rgb) {
	int alpha = (rgb >> 24) & 0xff;
	int red   = (rgb >> 16) & 0xff;
	int green = (rgb >>  8) & 0xff;
	int blue  = (rgb      ) & 0xff;
	red   = interpolate(fgColor.getRed(),   bgColor.getRed(),   red);
	green = interpolate(fgColor.getGreen(), bgColor.getGreen(), green);
	blue  = interpolate(fgColor.getBlue(),  bgColor.getBlue(),  blue);
	return (alpha << 24) | (red << 16) | (green << 8) | (blue);
    }
}
