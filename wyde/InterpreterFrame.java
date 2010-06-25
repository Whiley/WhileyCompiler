package wyde;

import wyjc.util.*;
import wyjc.stages.*;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class InterpreterFrame extends JFrame implements ActionListener,
		CaretListener, DocumentListener, WindowListener {

	private JConsoleArea consoleView;
	private JConsoleArea problemsView;
	private JTextPane textView;
	private DefaultStyledDocument document;
	private JLabel statusView;
	private JSplitPane splitPane;
	private JPanel outerpanel;
	private JPanel statusbar;
	private JToolBar toolbar;
	private JMenuBar menubar;
	private JTabbedPane tabbedPane;
	private JLabel lineNumberView;
	private JButton runButton;
	private DisplayThread highlighter;
	
	private int topProportion = 60;
	private int bottomProportion = 40;

	// Actions for copy, cut and paste
	private Action cutAction = new AbstractAction("Cut",
			makeImageIcon("stock_cut.png")) {
		public void actionPerformed(ActionEvent e) {			
			textView.cut();
		}
	};

	private Action copyAction = new AbstractAction("Copy",
			makeImageIcon("stock_copy.png")) {
		public void actionPerformed(ActionEvent e) {
			textView.copy();
		}
	};

	private Action pasteAction = new AbstractAction("Paste",
			makeImageIcon("stock_paste.png")) {
		public void actionPerformed(ActionEvent e) {
			 textView.paste();
		}
	};

	// The run thread is used for executing
	// the simple lisp program
	private RunThread runThread = null;

	private ImageIcon runImage = makeImageIcon("Play24.gif");

	private ImageIcon stopImage = makeImageIcon("stock_stop.png");	

	// The dirty bit is used to signal when the source file
	// has been modified. This is useful as it allows us
	// to ask the user if they want to save the file before
	// doing an operation such as "file new" or "file open".
	private boolean dirty = false;

	// File is used to represent the filename of the program
	// currently being edited.
	private File file = null;

	// The file chooser is used to open a file browser dialog
	// when opening or saving files.
	private final JFileChooser fileChooser = new JFileChooser(new File("."));

	// The serial version is needed for serialization
	private static final long serialVersionUID = 201L;

	public InterpreterFrame() {
		super("Wyde: A Simple Whiley IDE");

		// Create the menu
		menubar = buildMenuBar();
		setJMenuBar(menubar);
		// Create the toolbar
		toolbar = buildToolBar();
		// disable cut and copy actions
		cutAction.setEnabled(false);
		copyAction.setEnabled(false);
		// Setup text area for editing source code
		// and setup document listener so interpreter
		// is notified when current file modified and
		// when the cursor is moved.
		textView = buildEditor();
		textView.getDocument().addDocumentListener(this);
		textView.addCaretListener(this);

		// Give text view scrolling capability
		Border border = BorderFactory.createCompoundBorder(BorderFactory
				.createEmptyBorder(3, 3, 3, 3), BorderFactory
				.createLineBorder(Color.gray));
		JScrollPane topSplit = addScrollers(textView);
		topSplit.setBorder(border);

		// Create tabbed pane for console/problems
		consoleView = makeConsoleArea(10, 50, true);
		problemsView = makeConsoleArea(10, 50, false);
		tabbedPane = buildProblemsConsole();

		// Plug the editor and problems/console together
		// using a split pane. This allows one to change
		// their relative size using the split-bar in
		// between them.
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					   topSplit, tabbedPane);

		// Create status bar
		statusView = new JLabel(" Status");
		lineNumberView = new JLabel("0:0");
		statusbar = buildStatusBar();

		// Now, create the outer panel which holds
		// everything together
		outerpanel = new JPanel();
		outerpanel.setLayout(new BorderLayout());
		outerpanel.add(toolbar, BorderLayout.PAGE_START);
		outerpanel.add(splitPane, BorderLayout.CENTER);
		outerpanel.add(statusbar, BorderLayout.SOUTH);
		getContentPane().add(outerpanel);

		// tell frame to fire a WindowsListener event
		// but not to close when "x" button clicked.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		// set minimised icon to use
		setIconImage(makeImageIcon("spi.png").getImage());

		// set default window size
		Component top = splitPane.getTopComponent();
		Component bottom = splitPane.getBottomComponent();
		top.setPreferredSize(new Dimension(400,400));
		bottom.setPreferredSize(new Dimension(400,200));
		pack();					      

		textView.grabFocus();
		setVisible(true);		

		// redirect all I/O to problems/console
		redirectIO();		
		
		// start highlighter thread
		highlighter = new DisplayThread(250);
		highlighter.setDaemon(true);
		highlighter.start();
	}

	public void newFile() {
		if (!dirty || checkForSave()) {
			textView.setText("");
			consoleView.setText("");
			problemsView.setText("");
			statusView.setText(" Created new file.");
			file = null;
			// reset dirty bit
			dirty = false;
		}
	}

	public void openFile() {
		if (!dirty || checkForSave()) {
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
			} else {
				// user cancelled open after all
				return;
			}
			// load file into text view
			textView.setText(physReadTextFile(file));
			// update status
			statusView.setText(" Loaded file \"" + file.getName() + "\".");
			// reset dirty bit
			dirty = false;
		}
	}

	public void saveFileAs() {
		// Force user to enter new file name
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
		} else {
			// user cancelled save after all
			return;
		}
		// file selected, so write it.
		physWriteTextFile(file, textView.getText());
		// update status
		statusView.setText(" Saved file \"" + file.getName() + "\".");
		// reset dirty bit
		dirty = false;
	}

	public void saveFile() {
		if (file == null) {
			// first save file, so prompt for name.
			saveFileAs();
		} else {
			// file already named so just write it.
			physWriteTextFile(file, textView.getText());
			// update status
			statusView.setText(" Saved file \"" + file.getName() + "\".");
			// reset dirty bit
			dirty = false;
		}
	}

	public void evaluate() {
		/*
		try {
			// clear problems and console messages
			problemsView.setText("");
			consoleView.setText("");
			// update status view
			statusView.setText(" Parsing ...");
			tabbedPane.setSelectedIndex(0);
			LispExpr root = Parser.parse(textView.getText());
			statusView.setText(" Running ...");
			tabbedPane.setSelectedIndex(1);
			// update run button
			runButton.setIcon(stopImage);
			runButton.setActionCommand("Stop");
			// start run thread
			runThread = new RunThread(root);
			runThread.start();
		} catch(SyntaxError e) {
	    	tabbedPane.setSelectedIndex(0);
	    	System.err.println("Syntax Error at " + e.getLine() + ", " + 
	    			e.getColumn() + " : " + e.getMessage());
	    } catch (Error e) {
			// parsing error
		    System.err.println(e.getMessage());
		    statusView.setText(" Errors.");
		}
		*/
	}

	public void stopEvaluate() {
		// user requested run be stopped so tell
		// run thread to stop
		Thread tmp = runThread;
		runThread = null;
		tmp.interrupt();
	}

	public void runFinished() {
		// program execution finished so update
		// status and run button accordingly
		if (runThread == null) {
			// _runThread = null only if
			// execution stopped by user via
			// run button
			statusView.setText(" Stopped.");
		} else {
			statusView.setText(" Done.");
		}
		runButton.setActionCommand("Run");
		runButton.setIcon(runImage);
	}

    // Many of the following methods have been added purely
    // so InternalFunctions can work.  Originally, the code in 
    // that class was inline here, so its functions had direct
    // access.  I removed it so that students do not need to wade
    // through all the functions!  But, that leaves the question as 
    // to what to do with the following methods, but I don't think
    // there's much you can do ...
    public void changeSize(int width, int height) {
    	setSize(width,height);
    	Component top = splitPane.getTopComponent();
    	Component bottom = splitPane.getBottomComponent();
    	int totalHeight = top.getHeight() + bottom.getHeight();
    	int topHeight = (totalHeight * topProportion) / 100;
    	int bottomHeight = (totalHeight * bottomProportion) / 100;
    	top.setPreferredSize(new Dimension(width-10,topHeight));
    	bottom.setPreferredSize(new Dimension(width-10,bottomHeight));
    	splitPane.resetToPreferredSizes();
    	pack();
    }

    public void setTopProportion(int top) {
    	topProportion = top;
    	bottomProportion = 100-top;
    }
    
    public void setToolBarMode(boolean enable) {
    	if(enable) {
    		if(!outerpanel.isAncestorOf(toolbar)) {
    			outerpanel.add(toolbar, BorderLayout.PAGE_START);
    		}
    	} else {
    		outerpanel.remove(toolbar);
    	}
    }
    
    public void setMenuBarMode(boolean enable) {
    	if(enable) {    
    		setJMenuBar(menubar);
    	} else {
    		setJMenuBar(null);
    	}    	    
    }
    
    public void setStatusBarMode(boolean enable) {
    	if(enable) {
    		if(!outerpanel.isAncestorOf(statusbar)) {
    			outerpanel.add(statusbar, BorderLayout.SOUTH);
    		}
    	} else {
    		outerpanel.remove(statusbar);
    		pack();
    	}
    }
    
    public void copy() { textView.copy(); }
    public void paste() { textView.paste(); }
    public void cut() { textView.cut(); }
    
    public int getCaretPosition() {
    	Caret c = textView.getCaret();
		return c.getDot();
    }
    
    public void setCaretPosition(int position) {
    	Caret c = textView.getCaret();
		// move the caret
		c.setDot(position);
    }
    
    public Document getDocument() { return document; }
    
    public void setSelectedTab(int pos) {
    	tabbedPane.setSelectedIndex(pos);
    }
    
    public int getSelectedTab() {
    	return tabbedPane.getSelectedIndex();
    }
    
    // convert color name in Java Color object
    public Color getColour(String name) {    	
    	if(name.equals("red")) {
    		return Color.red;
    	} else if(name.equals("blue")) {
    		return Color.blue;
    	} else if(name.equals("black")) {
    		return BACKGROUND_COLOR;
    	} else if(name.equals("cyan")) {
    		return Color.cyan;
    	} else if(name.equals("dark gray")) {
    		return Color.darkGray;
    	} else if(name.equals("gray")) {
    		return Color.gray;
    	} else if(name.equals("light gray")) {
    		return Color.lightGray;
    	} else if(name.equals("green")) {
    		return Color.gray;
    	} else if(name.equals("magenta")) {
    		return Color.magenta;
    	} else if(name.equals("orange")) {
    		return Color.orange;
    	} else if(name.equals("pink")) {
    		return Color.pink;
    	} else if(name.equals("white")) {
    		return Color.white;
    	} else if(name.equals("yellow")) {
    		return TEXT_COLOR;
    	}
    	try {
    		// see if the colour is expressed in
    		// 0xAABBCC format for RGB...
    		return Color.decode(name);
    	} catch(NumberFormatException e) {}
    	// no, ok bail then ... but this will certainly
    	// through an exception
    	return null;    	
    }
    
    public void exit() {
		// user is attempting to exit the interpreter.
		// make sure this is what they want to do
		// and check if changes need to be saved.
		int r = JOptionPane.showConfirmDialog(this, new JLabel(
				"Are you Sure?"), "Confirm Exit",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (r == JOptionPane.YES_OPTION) {
			// user still wants to go ahead.
			// if file not saved then prompt to check
			// whether it should be.
			if (!dirty || checkForSave()) {
				System.exit(0);
			}
		}
	}

	// ----------------
	// Helper Functions
	// ----------------

	private JMenuBar buildMenuBar() {
		// This function builds the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File"); 
		fileMenu.add(makeMenuItem("New"));
		fileMenu.add(makeMenuItem("Open"));
		fileMenu.addSeparator();
		fileMenu.add(makeMenuItem("Save"));
		fileMenu.add(makeMenuItem("Save As"));
		fileMenu.addSeparator();
		fileMenu.add(makeMenuItem("Exit"));
		menuBar.add(fileMenu);
		// edit menu
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(makeMenuItem(cutAction));
		editMenu.add(makeMenuItem(copyAction));
		editMenu.add(makeMenuItem(pasteAction));
		menuBar.add(editMenu);
		return menuBar;
	}

	private JToolBar buildToolBar() {
		// build tool bar
		JToolBar toolBar = new JToolBar("Toolbar");
		toolBar.add(makeToolbarButton("stock_new.png", "New file", "New"));
		toolBar.add(makeToolbarButton("stock_open.png", "Open file", "Open"));
		toolBar.add(makeToolbarButton("stock_save.png", "Save file", "Save"));
		// run button is special
		runButton = makeToolbarButton("Play24.gif", "Run Program", "Run");
		toolBar.add(runButton);		
		toolBar.addSeparator();
		toolBar.add(new JButton(copyAction));
		toolBar.add(new JButton(cutAction));
		toolBar.add(new JButton(pasteAction));
		return toolBar;
	}

	private JTextPane buildEditor() {
	    // build the editor pane
	    JTextPane ta = makeTextPane(true);
	    ta.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
	    return ta;
	}

	private JTabbedPane buildProblemsConsole() {
		// build the problems/console editor
		JTabbedPane tp = new JTabbedPane();
		ImageIcon consoleIcon = makeImageIcon("stock_print-layout-16.png");
		tp.addTab("Problems", addScrollers(problemsView));
		tp.addTab("Console", consoleIcon, addScrollers(consoleView));
		// empty border to give padding around tab pane
		tp.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		return tp;
	}

	private JPanel buildStatusBar() {
		// build the status bar.  this sits at
		// the bottom of the window and indicates
		// the status of the last operation and
		// the current line number.
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(statusView, BorderLayout.WEST);
		panel.add(lineNumberView, BorderLayout.EAST);
		return panel;
	}

		private void redirectIO() {
		// redirect std I/O to console and problems:
		// > System.out => console
		// > System.err => problems
		// > console => System.in
		System.setOut(consoleView.getOutputStream());
		System.setErr(problemsView.getOutputStream());
		// redirect input from console to System.in
		System.setIn(consoleView.getInputStream());
	}

	private boolean checkForSave() {
		// build warning message
		String message;
		if (file == null) {
			message = "File has been modified.  Save changes?";
		} else {
			message = "File \"" + file.getName()
					+ "\" has been modified.  Save changes?";
		}

		// show confirm dialog
		int r = JOptionPane.showConfirmDialog(this, new JLabel(message),
				"Warning!", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (r == JOptionPane.YES_OPTION) {
			// Save File
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				// write the file
				physWriteTextFile(fileChooser.getSelectedFile(), textView
						.getText());
			} else {
				// user cancelled save after all
				return false;
			}
		}
		return r != JOptionPane.CANCEL_OPTION;
	}

	private String physReadTextFile(File file) {
		// physically read text file
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			StringBuffer tmp = new StringBuffer();
			while (input.ready()) {
				tmp.append(input.readLine());
				tmp.append("\n");
			}
			return tmp.toString();
		} catch (FileNotFoundException e) {
			// not sure how this can happen
			showErrorDialog("Unable to load \"" + file.getName() + "\" (file not found)");
		} catch (IOException e) {
			// This happens if e.g. file already exists and
			// we do not have write permissions
			showErrorDialog("Unable to load \"" + file.getName() + "\" (I/O error)");
		}
		return new String("");
	}

	private void physWriteTextFile(File file, String text) {
		// physically write file
		try {
			FileOutputStream fo = new FileOutputStream(file);
			fo.write(text.getBytes());
			fo.close();
		} catch (FileNotFoundException e) {
			// not sure how this can happen
			showErrorDialog("Saving failed due to file not found error?");
		} catch (IOException e) {
			// This happens if e.g. file already exists and
			// we do not have write permissions
			showErrorDialog("Saving failed due I/O error.");
		}
	}

	private void showErrorDialog(String msg) {
		// show a dialog window containing the error message
		JOptionPane.showMessageDialog(this, new JLabel(msg), "Error!",
				JOptionPane.ERROR_MESSAGE);
	}

	private JTextPane makeTextPane(boolean editable) {
	    document = new DefaultStyledDocument();	    
		JTextPane ta = new JTextPane(document);
	    ta.setEditable(editable);
	    return ta;
	}

	private JConsoleArea makeConsoleArea(int width, int height, boolean editable) {
		JConsoleArea ta = new JConsoleArea();
		ta.setEditable(editable);
		return ta;
	}

	private JScrollPane addScrollers(JComponent c) {
		return new JScrollPane(c,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	private JMenuItem makeMenuItem(String s) {
	    JMenuItem item = new JMenuItem(s);
	    item.setActionCommand(s);
	    item.addActionListener(this);
	    return item;
	}

	private JMenuItem makeMenuItem(Action a) {
		JMenuItem item = new JMenuItem(a);
		item.addActionListener(this);
		return item;
	}

	private JButton makeToolbarButton(String name, String toolTipText,
			String action) {
		// Create and initialize the button.
		JButton button = new JButton(makeImageIcon(name));
		button.setToolTipText(toolTipText);
		button.setActionCommand(action);
		button.addActionListener(this);

		return button;
	}

	private ImageIcon makeImageIcon(String name) {
		String fileName = "icons/" + name;
		// using the URL means the image loads when stored
		// in a jar or expanded into individual files.
		java.net.URL imageURL = InterpreterFrame.class.getResource(fileName);

		ImageIcon icon = null;
		if (imageURL != null) {
			icon = new ImageIcon(imageURL);
		}
		return icon;
	}
	
	private static final Color BACKGROUND_COLOR = Color.BLACK;
	private static final Color TEXT_COLOR = Color.YELLOW;
	private static final Color KEYWORD_COLOR = Color.BLUE;
	private static final Color BRACES_COLOR = Color.CYAN;
	private static final Color TYPES_COLOR = Color.GREEN;
	private static final Color VALUE_COLOR = Color.RED;
	private static final Color COMMENT_COLOR = Color.GRAY;
	
	private void updateDisplay() {
		// first, set block colours
		textView.setBackground(BACKGROUND_COLOR);
		textView.setForeground(TEXT_COLOR);
		textView.setCaretColor(TEXT_COLOR);
		problemsView.setBackground(BACKGROUND_COLOR);
		problemsView.setForeground(TEXT_COLOR);
		consoleView.setBackground(BACKGROUND_COLOR);
		consoleView.setForeground(TEXT_COLOR);
		StringReader ss = new StringReader(textView.getText());
		try {
			WhileyLexer wl = new WhileyLexer(ss);
			
			java.util.List<WhileyLexer.Token> tokens = wl.scan();	
			for(WhileyLexer.Token t : tokens) {					
				int len = t.text.length();
				if(t instanceof WhileyLexer.RightBrace 
						|| t instanceof WhileyLexer.LeftBrace) {
					highlightArea(t.start,len,BRACES_COLOR);			    				 
				} else if(t instanceof WhileyLexer.Strung) {
					highlightArea(t.start,len,VALUE_COLOR);				
				} else if(t instanceof WhileyLexer.Comment) {
					highlightArea(t.start,len,COMMENT_COLOR);
				} else if(t instanceof WhileyLexer.Comma) {
					highlightArea(t.start,len,BRACES_COLOR);
				} else if(t instanceof WhileyLexer.Int) {
					highlightArea(t.start,len,VALUE_COLOR);
				} 				
			}	
		} catch(IOException e) {}
	}
	
	private void highlightArea(int pos, int len, Color col) {
		SimpleAttributeSet attrs = new SimpleAttributeSet();
	    StyleConstants.setForeground(attrs, col);			    
	    document.setCharacterAttributes(pos,len,attrs, true);
	}
	
	// ----------------------
	// ActionListener Methods
	// ----------------------

	public void actionPerformed(ActionEvent e) {
		// When a toolbar button or menu item is
		// clicked on this function will be called
		String cmd = e.getActionCommand();
		if (cmd.equals("New")) {
			newFile();
		} else if (cmd.equals("Open")) {
			openFile();
		} else if (cmd.equals("Save")) {
			saveFile();
		} else if (cmd.equals("Save As")) {
			saveFileAs();
		} else if (cmd.equals("Exit")) {
			exit();
		} else if (cmd.equals("Run")) {
			evaluate();
		} else if (cmd.equals("Stop")) {
			stopEvaluate();
		} 	
	}

	// ---------------------
	// CaretListener Methods
	// ---------------------

	public void caretUpdate(CaretEvent e) {
		// when the cursor moves on _textView
		// this method will be called. Then, we
		// must determine what the line number is
		// and update the line number view
		Element root = textView.getDocument().getDefaultRootElement();
		int line = root.getElementIndex(e.getDot());
		root=root.getElement(line);
		int col = root.getElementIndex(e.getDot());
		lineNumberView.setText(line + ":" + col);
		// if text is selected then enable copy and cut
		boolean isSelection = e.getDot() != e.getMark();
		copyAction.setEnabled(isSelection);
		cutAction.setEnabled(isSelection);
		
	}

	// ------------------------
	// DocumentListener Methods
	// ------------------------

	public void changedUpdate(DocumentEvent e) {		
	}

	public void insertUpdate(DocumentEvent e) {
		// when text is typed into _textView
		// this will be called
		dirty = true;
	}

	public void removeUpdate(DocumentEvent e) {
		// when text is deleted from _textView
		// this will be called
		dirty = true;			
	}

	// ----------------------
	// WindowListener Methods
	// ----------------------

	public void windowClosing(WindowEvent e) {
		// when the user clicks on 'x' button
		// in top-right-hand corner of frame,
		// this method is called.
		exit();
	}

    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}

	// ---------------
	// private classes
	// ---------------

	private class RunThread extends Thread {
		// the runthread is responsible for
		// executing the users simpular program.
		// Using a separate thread here means 
		// that the user interface will still
		// respond to events whilst the program
		// is running.
	        Class program;

		RunThread(Class p) {
			program = p;
		}

		public void run() {
		    try {			
		    	while (runThread == this) {
		    		program.getMethod("main").invoke(null);
		    		break;
		    	}
		    } catch(NoSuchMethodException e) {
		    	tabbedPane.setSelectedIndex(0);
		    	System.err.println("Program requires main method to run!");
		    } catch(Throwable e) {
		    	tabbedPane.setSelectedIndex(0);
		    	System.err.println("Runtime Error: " + e.getMessage());
		    } 
		    
		    // notify GUI that program execution finished
		    runFinished();
		}
	}

	private class DisplayThread extends Thread {
		// the dislpay thread is responsible for
		// syntax highlighting the users code and
		// updating various colours
		private int _period; // in ms
		
		public DisplayThread(int period) { _period = period; }
		
		public void run() { // how to let this terminate gracefully?
		    while(1!=2) {
		    	try { 
		    		Thread.sleep(_period);
		    		updateDisplay(); 
		    	} catch(InterruptedException e) {}	
		    }	
		}			

	}

	
	public static void main(String argv[]) {		
		if(argv.length == 0) {
		    // no command-line parameters, so use GUI.
		    InterpreterFrame f = new InterpreterFrame();	
		} else {
		    // run interpreter with GUI, but
		    // load requested file.

		    InterpreterFrame f = new InterpreterFrame();	
		    // load file into text view		    
		    f.textView.setText(f.physReadTextFile(new File(argv[0])));
		    // update status
		    f.statusView.setText(" Loaded file \"" + argv[0] + "\".");
		    // reset dirty bit
		    f.dirty = false;
		}
	}
}
