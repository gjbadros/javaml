/*
 * @(#)ImageLoop.java	1.22f 95/03/28 James Gosling
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
import java.awt.*;
import java.net.*;

/**
 * ImageLoop class. This is a container for a list
 * of images that can be animated.
 *
 * @author 	James Gosling
 * @version 	1.22f, 28 Mar 1995
 */
class ImageLoop {
    /**
     * The images.
     */
    Image imgs[];

    /**
     * The number of images actually loaded.
     */
    int nimgs = 0;

    /**
     * Load the images, from dir. The images are assumed to be
     * named T1.gif, T2.gif...
     * Once all images are loaded the applet is resized to the
     * maximum size().width and size().height.
     */
    ImageLoop(URL context, String dir, ImageLoopItem parent, int nimgs) {
	this.nimgs = nimgs;
	imgs = new Image[nimgs];
	for (int i = 0; i < nimgs; i++) {
	    imgs[i] = parent.getImage(parent.getDocumentBase(), dir + "/T" + (i + 1) + ".gif");
	}
    }
  
  public String getAppletInfo() {
    return "Title: ImageLoop\nAuthor: James Gosling\nThis is a container for a list of images that can be animated.";
  }
}


