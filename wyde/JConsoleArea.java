package wyde;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

public class JConsoleArea extends JTextArea implements DocumentListener {
	// A JConsoleArea is a text area which provides several 
	// pieces of functionality:
	//
	// 1) Input from the text area can be directed into 
	//    an input stream.
	// 2) Output from an output stream can be directed
	//    into the text area.
	// 3) the cursor (a.k.a. the caret) is fixed to be
	//    at the bottom of the text area after any text
	//    insertion.
	
    // needed for serialization
	private static final long serialVersionUID = 202L;
	
	// create the input and output streams 
	private PrintStream _output = new PrintStream(
    		new DocumentOutputStream(getDocument()));
	private InputStream _input = new ComponentInputStream(this);
	
	public JConsoleArea() {		
		super(); 
		getDocument().addDocumentListener(this);
	}
	public JConsoleArea(int width, int height) { 
		super(width,height); 
		getDocument().addDocumentListener(this);
	}
	
	public void setEditable(boolean flag) {
		super.setEditable(flag);
		if(flag && _input == null) {			
			_input = new ComponentInputStream(this);
		} else if(!flag && _input != null){
			try {
				_input.close();
			} catch(IOException e) {				
			}
			_input = null;
		}
	}
	public PrintStream getOutputStream() { return _output; }
	public InputStream getInputStream() { return _input; }
	
    // ------------------------
	// DocumentListener Methods
	// ------------------------

	public void changedUpdate(DocumentEvent e) {}

	public void insertUpdate(DocumentEvent e) {		
		// text inserted to automatically move
		// cursor to end
		setCaretPosition(getDocument().getLength());
	}

	public void removeUpdate(DocumentEvent e) {}
}
