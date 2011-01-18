import java.awt.*;
import java.awt.event.*;

public class Helper {
    public static void doStuff() {
	Frame f = new Frame("Hello World");       
	Panel buttons = new Panel();
	Button redButton = new Button("Red");
	Button blueButton = new Button("Blue");
	Button greenButton = new Button("Green");
	buttons.add(redButton);
	buttons.add(blueButton);
	buttons.add(greenButton);
	f.add("South", buttons);
	f.setSize(300,300);
	f.setVisible(true);
	f.addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){ 
		    System.exit(0);
		}
	    });
    }
}