package wyde;

import java.io.*;
import javax.swing.text.*;

public class DocumentOutputStream extends OutputStream {	
	private Document _doc;
	private StringBuffer _buf = new StringBuffer(" ");
	
	DocumentOutputStream(Document d) {
		if(d == null) {
			throw new IllegalArgumentException("Document cannot be null");
		}
		_doc = d; 
	}
	
	public void write(int b) {
		try {
			_buf.setCharAt(0,(char) b);
			_doc.insertString(_doc.getLength (),_buf.toString(),null);
		} catch(BadLocationException e) {
			// do nothing
		}		
	}
}
