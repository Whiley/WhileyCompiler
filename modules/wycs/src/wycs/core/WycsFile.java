package wycs.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import wybs.lang.Content;
import wybs.lang.Path;
import wycs.io.WyalFileReader;
import wycs.io.WyalFileStructuredPrinter;
import wycs.syntax.WyalFile;

public class WycsFile {

	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<WyalFile> ContentType = new Content.Type<WyalFile>() {
		public Path.Entry<WyalFile> accept(Path.Entry<?> e) {
			if (e.contentType() == this) {
				return (Path.Entry<WyalFile>) e;
			}
			return null;
		}

		public WyalFile read(Path.Entry<WyalFile> e, InputStream input)
				throws IOException {
			// System.out.println("SCANNING: " + e.id());
			WyalFileReader reader = new WyalFileReader(e.location().toString(),
					input);
			return reader.read();
		}

		public void write(OutputStream output, WyalFile module)
				throws IOException {
			// WycsFileClassicalPrinter writer = new
			// WycsFileClassicalPrinter(output);
			WyalFileStructuredPrinter writer = new WyalFileStructuredPrinter(
					output);
			writer.write(module);
		}

		public String toString() {
			return "Content-Type: wycs";
		}
	};

	// =========================================================================
	// State
	// =========================================================================

}
