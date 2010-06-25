package wyde;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class ComponentInputStream extends InputStream implements KeyListener {				
	private ByteArrayOutputStream _in = new ByteArrayOutputStream();
	private PrintStream _out = new PrintStream(_in);
	private byte _packet[] = null;	
	private int _pos = 0;
	
	public ComponentInputStream(Component c) {		
		if(c == null) {
			throw new IllegalArgumentException("Component cannot be null");
		}
		c.addKeyListener(this);			
	}
	
	public synchronized int read() throws IOException { 
		try {
			while(_packet == null) { wait(); }
			int b = _packet[_pos++];
			if(_pos == _packet.length) { 
				_packet = null;				
			}			
			return b;
		} catch(InterruptedException e) {
			return -1;
		}
	}
	
	public synchronized int available() throws IOException {			
		if(_packet != null) {
			return (_packet.length - _pos) + _in.size();
		} else { return _in.size(); }
	}
	
    // -------------------
	// KeyListener Methods
	// -------------------
	
	public void keyPressed(KeyEvent e) {}	
	public void keyReleased(KeyEvent e) {}
	
	public synchronized void keyTyped(KeyEvent e) {      						
		char c = e.getKeyChar();
		_out.print(c);
			
		if(c == '\n' && _packet == null) {
			// I plain don't understand why
			// the -1 is needed here!?
			_out.write(-1);
			_packet = _in.toByteArray();
			_pos = 0;
			_in.reset();
			// tell reader data arrived
			notify();
		}
	}
}
