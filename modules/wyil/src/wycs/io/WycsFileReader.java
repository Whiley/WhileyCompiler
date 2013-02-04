package wycs.io;

import java.io.IOException;
import java.io.Reader;

import wycs.lang.WycsFile;

public class WycsFileReader {
	private Reader reader;
	
	public WycsFile read() throws IOException {
		Lexer lexer = new Lexer(reader);
		Parser parser = new Parser(lexer.scan());
		return parser.parse();
	}
}
