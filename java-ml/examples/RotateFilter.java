/*
 * @(#)RotateFilter.java	1.1 96/02/27
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

import java.awt.image.ColorModel;
import java.awt.image.ImageFilter;
import java.util.Hashtable;
import java.awt.Rectangle;

public class RotateFilter extends ImageFilter {

    private static ColorModel defaultRGB = ColorModel.getRGBdefault();

    private double angle;
    private double sin;
    private double cos;
    private double coord[] = new double[2];

    private int raster[];
    private int xoffset, yoffset;
    private int srcW, srcH;
    private int dstW, dstH;

    public RotateFilter(double angle) {
	this.angle = angle;
	sin = Math.sin(angle);
	cos = Math.cos(angle);
    }

    public void transform(double x, double y, double[] retcoord) {
	// Remember that the coordinate system is upside down so apply
	// the transform as if the angle were negated.
	// cos(-angle) =  cos(angle)
	// sin(-angle) = -sin(angle)
	retcoord[0] = cos * x + sin * y;
	retcoord[1] = cos * y - sin * x;
    }

    public void itransform(double x, double y, double[] retcoord) {
	// Remember that the coordinate system is upside down so apply
	// the transform as if the angle were negated.  Since inverting
	// the transform is also the same as negating the angle, itransform
	// is calculated the way you would expect to calculate transform.
	retcoord[0] = cos * x - sin * y;
	retcoord[1] = cos * y + sin * x;
    }

    public void transformBBox(Rectangle rect) {
	double minx = Double.POSITIVE_INFINITY;
	double miny = Double.POSITIVE_INFINITY;
	double maxx = Double.NEGATIVE_INFINITY;
	double maxy = Double.NEGATIVE_INFINITY;
	for (int y = 0; y <= 1; y++) {
	    for (int x = 0; x <= 1; x++) {
		transform(rect.x + x * rect.width,
			  rect.y + y * rect.height,
			  coord);
		minx = Math.min(minx, coord[0]);
		miny = Math.min(miny, coord[1]);
		maxx = Math.max(maxx, coord[0]);
		maxy = Math.max(maxy, coord[1]);
	    }
	}
	rect.x = (int) Math.floor(minx);
	rect.y = (int) Math.floor(miny);
	rect.width = (int) Math.ceil(maxx) - rect.x + 1;
	rect.height = (int) Math.ceil(maxy) - rect.y + 1;
    }

    public void setDimensions(int width, int height) {
	Rectangle rect = new Rectangle(0, 0, width, height);
	transformBBox(rect);
	xoffset = -rect.x;
	yoffset = -rect.y;
	srcW = width;
	srcH = height;
	dstW = rect.width;
	dstH = rect.height;
	raster = new int[srcW * srcH];
	consumer.setDimensions(dstW, dstH);
    }

    public void setProperties(Hashtable props) {
	props = (Hashtable) props.clone();
	Object o = props.get("filters");
	if (o == null) {
	    props.put("filters", toString());
	} else if (o instanceof String) {
	    props.put("filters", ((String) o)+toString());
	}
	consumer.setProperties(props);
    }

    public void setColorModel(ColorModel model) {
	consumer.setColorModel(defaultRGB);
    }

    public void setHints(int hintflags) {
	consumer.setHints(TOPDOWNLEFTRIGHT
			  | COMPLETESCANLINES
			  | SINGLEPASS
			  | (hintflags & SINGLEFRAME));
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model,
			  byte pixels[], int off, int scansize) {
	int srcoff = off;
	int dstoff = y * srcW + x;
	for (int yc = 0; yc < h; yc++) {
	    for (int xc = 0; xc < w; xc++) {
		raster[dstoff++] = model.getRGB(pixels[srcoff++] & 0xff);
	    }
	    srcoff += (scansize - w);
	    dstoff += (srcW - w);
	}
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model,
			  int pixels[], int off, int scansize) {
	int srcoff = off;
	int dstoff = y * srcW + x;
	if (model == defaultRGB) {
	    for (int yc = 0; yc < h; yc++) {
		System.arraycopy(pixels, srcoff, raster, dstoff, w);
		srcoff += scansize;
		dstoff += srcW;
	    }
	} else {
	    for (int yc = 0; yc < h; yc++) {
		for (int xc = 0; xc < w; xc++) {
		    raster[dstoff++] = model.getRGB(pixels[srcoff++]);
		}
		srcoff += (scansize - w);
		dstoff += (srcW - w);
	    }
	}
    }

    public void imageComplete(int status) {
	if (status == IMAGEERROR || status == IMAGEABORTED) {
	    consumer.imageComplete(status);
	    return;
	}
	int pixels[] = new int[dstW];
	for (int dy = 0; dy < dstH; dy++) {
	    itransform(0 - xoffset, dy - yoffset, coord);
	    double x1 = coord[0];
	    double y1 = coord[1];
	    itransform(dstW - xoffset, dy - yoffset, coord);
	    double x2 = coord[0];
	    double y2 = coord[1];
	    double xinc = (x2 - x1) / dstW;
	    double yinc = (y2 - y1) / dstW;
	    for (int dx = 0; dx < dstW; dx++) {
		int sx = (int) Math.round(x1);
		int sy = (int) Math.round(y1);
		if (sx < 0 || sy < 0 || sx >= srcW || sy >= srcH) {
		    pixels[dx] = 0;
		} else {
		    pixels[dx] = raster[sy * srcW + sx];
		}
		x1 += xinc;
		y1 += yinc;
	    }
	    consumer.setPixels(0, dy, dstW, 1, defaultRGB, pixels, 0, dstW);
	}
	consumer.imageComplete(status);
    }
}
