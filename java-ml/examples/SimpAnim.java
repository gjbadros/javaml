import java.awt.*;

public class SimpAnim extends Applet implements Runnable, MouseListener {
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
}
