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
		NewWyalFileLexer lexer = new NewWyalFileLexer(filename,input);
		List<NewWyalFileLexer.Token> tokens;		
		tokens = lexer.scan();		
		NewWyalFileParser parser = new NewWyalFileParser(filename, tokens);
		return parser.read();
	}
}
