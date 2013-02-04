package wycs.io;

import java.io.*;

import wycs.lang.WycsFile;

public class WycsFileReader {
	private final String filename;
	private final InputStream input;
	
	public WycsFileReader(String filename, InputStream input) {
		this.filename = filename;
		this.input = input;
	}
	
	public WycsFile read() throws IOException {
		Lexer lexer = new Lexer(input);
		Parser parser = new Parser(filename,lexer.scan());
		return parser.parse();
	}
}
