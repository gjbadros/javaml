
import java.applet.*;
import java.awt.*;

class LabelField extends Panel {
    int labelWidth;
    Label lbl;
    TextField field;

    public LabelField(int labelWidth, String lbl, String val) {
	this.labelWidth = labelWidth;
	add(this.lbl = new Label(lbl));
	add(this.field = new TextField(val));
	field.setEditable(false);
    }
    public void layout() {
	Dimension d = size();

	Dimension p1 = lbl.preferredSize();
	Dimension p2 = field.preferredSize();
	lbl.reshape(0, 0, labelWidth, p1.height);
	field.reshape(labelWidth + 5, 0, d.width - (labelWidth + 5), p2.height);
    }
}

public class SystemInfo extends Applet {
    CardLayout c;
    Panel p;

    public void init() {
	Font lbl = new Font("Helvetica", Font.BOLD, 14);
	setLayout(new BorderLayout());
	add("South", p = new Panel());
	p.add(new Button("Next"));
	p.add(new Button("Previous"));

	add("Center", p = new Panel());
	p.setLayout(c = new CardLayout());

	Panel p2 = new Panel();
	p2.setLayout(new GridLayout(0, 1));
	p2.add(new Label("System Properties")).setFont(lbl);
	p2.add(new LabelField(100, "version:",    System.getProperty("java.version")));
	p2.add(new LabelField(100, "vendor:",     System.getProperty("java.vendor")));
	p2.add(new LabelField(100, "vendor.url:", System.getProperty("java.vendor.url")));
	p.add("system", p2);

	p2 = new Panel();
	p2.setLayout(new GridLayout(0, 1));
	p2.add(new Label("User Properties")).setFont(lbl);
	p2.add(new LabelField(100, "User:",    System.getProperty("user.name")));
	p2.add(new LabelField(100, "Home:",     System.getProperty("user.home")));
	p2.add(new LabelField(100, "Current:", System.getProperty("user.dir")));
	p.add("user", p2);

	p2 = new Panel();
	p2.add(new Label("Java Properties")).setFont(lbl);
	p2.setLayout(new GridLayout(0, 1));
	p2.add(new LabelField(100, "java home:",    System.getProperty("java.home")));
	p2.add(new LabelField(100, "class version:",     System.getProperty("java.class.version")));
	p2.add(new LabelField(100, "class path:", System.getProperty("java.class.path")));
	p.add("java", p2);

	p2 = new Panel();
	p2.setLayout(new GridLayout(0, 1));
	p2.add(new Label("OS Properties")).setFont(lbl);
	p2.add(new LabelField(100, "OS:",    System.getProperty("os.name")));
	p2.add(new LabelField(100, "OS Arch:",     System.getProperty("os.arch")));
	p2.add(new LabelField(100, "OS Version:", System.getProperty("os.version")));
	p.add("os", p2);

	p2 = new Panel();
	p2.setLayout(new GridLayout(0, 1));
	p2.add(new Label("Misc Properties")).setFont(lbl);
	p2.add(new LabelField(100, "File Separator:",    System.getProperty("file.separator")));
	p2.add(new LabelField(100, "Path Separator:",     System.getProperty("path.separator")));
	p2.add(new LabelField(100, "Line Separator:", System.getProperty("line.separator")));
	p.add("sep", p2);
    }

    public boolean action(Event evt, Object obj) {
	if ("Next".equals(obj)) {
	    c.next(p);
	    return true;
	}
	if ("Previous".equals(obj)) {
	    c.previous(p);
	    return true;
	}
	return false;
    }
}
