package wycs.io;

import java.io.*;
import java.util.List;

import wycc.io.AbstractLexer;
import wycc.io.Token;
import wycc.lang.SyntaxError;
import wycs.syntax.WyalFile;

public class WyalFileReader {
	private final String filename;
	private final InputStream input;

	public WyalFileReader(String filename, InputStream input) {
		this.filename = filename;
		this.input = input;
	}

	public WyalFile read() throws IOException {
		WyalFileLexer lexer = new WyalFileLexer(input);
		List<Token> tokens;
		try {
			tokens = lexer.scan();
		} catch (AbstractLexer.Error error) {
			throw new SyntaxError(error.getMessage(), filename,
					error.getPosition(), error.getPosition(), error);
		}
		WyalFileStructuredParser parser = new WyalFileStructuredParser(filename, tokens);
		return parser.parse();
	}
}
