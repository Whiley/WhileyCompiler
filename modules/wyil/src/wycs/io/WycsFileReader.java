package wycs.io;

import java.io.*;

import wycs.lang.WycsFile;

public class WycsFileReader {
	private InputStream input;
	
	public WycsFileReader(InputStream input) {
		this.input = input;
	}
	
	public WycsFile read() throws IOException {
		Lexer lexer = new Lexer(input);
		Parser parser = new Parser(lexer.scan());
		return parser.parse();
	}
}
