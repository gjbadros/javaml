import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleAnimator extends Applet implements Runnable, MouseListener {
    URL hrefURL = null;              // URL link for information if any
    static final int STARTUP_ID    = 0;
    static final int BACKGROUND_ID = 1;
    static final int ANIMATION_ID  = 2;

    public String getAppletInfo() {
	return "Animator v1.10 (02/05/97), by Herb Jellinek";
    }

    public String[][] getParameterInfo() {
	String[][] info = {
	    {"imagesource", 	"URL", 	       "a directory"},
	    {"sounds",		"URLs",	       "list of audio samples"},
	};
	return info;
    }

    public String getParam(String key) {
	String result = getParameter(key);
	return result;
    }

    public void handleParams() {
      String param = getParam("IMAGESOURCE");
      animation.imageSource = (param == null) ? getDocumentBase() :
        new URL(getDocumentBase(), param + "/");
      
      animation.repeat = (param == null) ? true :
        (param.equalsIgnoreCase("yes") ||
         param.equalsIgnoreCase("true"));
    }

     public void init() {
	appWidth = getSize().width;
	appHeight = getSize().height;
    }

    public void run() {
	try {
            while (engine == me) {
                repaint();
            }
        } finally {
          int i = 1;
            synchronized(this) {
              if (engine == me)
                animation.stopPlaying();
            }
        }
    }

    public synchronized void stop() {
	engine = null;
        animation.stopPlaying();
    }
}

class Animation extends Object {
    static final int STARTUP_ID    = 0;
    static final int BACKGROUND_ID = 1;
    static final int ANIMATION_ID  = 2;
    static final String imageLabel = "image";
    static final String soundLabel = "sound";

    int globalPause = 1300;        // global pause in milleseconds
    List frames = null;            // List holding frames of animation
    int currentFrame;              // Index into images for current position
    Image startUpImage = null;     // The startup image if any
    Image backgroundImage = null;  // The background image if any
    AudioClip soundTrack = null;   // The soundtrack for this animation
    Color backgroundColor = null;  // Background color if any
    URL backgroundImageURL = null; // URL of background image if any
    URL startUpImageURL = null;    // URL of startup image if any 
    URL soundTrackURL = null;      // URL of soundtrack
    URL imageSource = null;        // Directory or URL for images
    URL soundSource = null;        // Directory or URL for sounds
    boolean repeat;                // Repeat the animation if true
    Image offScrImage;             // Offscreen image
    Graphics offScrGC;             // Offscreen graphics context
    MediaTracker tracker;          // MediaTracker used to load images
    Animator owner;                // Applet that contains this animation

    Animation(Animator container) {
        super();
        owner = container;
    }
}
