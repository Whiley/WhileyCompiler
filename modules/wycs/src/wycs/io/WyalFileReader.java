package wycs.io;

import java.io.*;
import java.util.List;

import wycs.syntax.WyalFile;

public class WyalFileReader {
	private final String filename;
	private final InputStream input;

	public WyalFileReader(String filename, InputStream input) {
		this.filename = filename;
		this.input = input;
	}

	public WyalFile read() throws IOException {
		WyalFileLexer lexer = new WyalFileLexer(filename,input);
		List<WyalFileLexer.Token> tokens;
		tokens = lexer.scan();
		WyalFileParser parser = new WyalFileParser(filename, tokens);
		return parser.read();
	}
}
