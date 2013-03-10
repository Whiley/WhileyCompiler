package wycs.io;

import java.io.*;
import java.util.List;

import wybs.lang.SyntaxError;
import wybs.util.AbstractLexer;
import wycs.lang.WycsFile;

public class WycsFileReader {
	private final String filename;
	private final InputStream input;

	public WycsFileReader(String filename, InputStream input) {
		this.filename = filename;
		this.input = input;
	}

	public WycsFile read() throws IOException {
		WycsFileLexer lexer = new WycsFileLexer(input);
		List<AbstractLexer.Token> tokens;
		try {
			tokens = lexer.scan();
		} catch (AbstractLexer.Error error) {
			throw new SyntaxError(error.getMessage(), filename,
					error.getPosition(), error.getPosition());
		}
		WycsFileParser parser = new WycsFileParser(filename, tokens);
		return parser.parse();
	}
}
