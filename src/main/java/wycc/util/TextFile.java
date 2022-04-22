// Copyright 2021 David James Pearce
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wycc.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A simple form of content representing a text file.
 *
 * @author David J. Pearce
 *
 */
public class TextFile implements Iterable<String> {
    private final String[] lines;

    public TextFile(String content) {
        this.lines = content.split("\n");
    }

    public TextFile(String[] lines) {
        this.lines = Arrays.copyOf(lines, lines.length);
    }

	public byte[] getBytes(Charset encoding) throws IOException {
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); PrintStream pout = new PrintStream(bout);) {
			for (String line : lines) {
				pout.println(line);
			}
			pout.flush();
			return bout.toByteArray();
		}
	}

	@Override
	public Iterator<String> iterator() {
		return Arrays.asList(lines).iterator();
	}

	/**
	 * Replace a number of lines in this text file with some other number of lines.
	 * The number being replaced could be smaller, equal or greater than those being
	 * replaced with giving different effects.
	 *
	 * @param start First line to replace (starting from one)
	 * @param end Last line to replace (exclusive).
	 * @param lines
	 * @return
	 */
	public TextFile replace(int start, int end, String... newLines) {
		start = start - 1;
		end = end - 1;
		int delta = (end - start) - newLines.length;
		System.out.println("delta = " + delta);
		String[] nlines = Arrays.copyOf(lines, lines.length - delta);
		System.out.println("|nlines| = " + nlines.length);
		// Copy over new lines
		for (int i = 0; i < newLines.length; ++i) {
			nlines[i + start] = newLines[i];
		}
		// Copy over what comes after
		for(int i = end;i < lines.length; ++i) {
			nlines[i - delta] = lines[i];
		}
		// Done
		return new TextFile(nlines);
	}

    public Line getEnclosingLine(int offset) {
        int index = 0;
        for(int i=0;i!=lines.length;++i) {
        	String ith = lines[i];
        	if(offset < ith.length()) {
        		return new Line(index,ith.length(),i+1);
        	}
        	index += ith.length() + 1;
			// Strip line
			offset = offset - ith.length();
			// Strip newline
        	offset = offset - 1;
        }
        return null;
    }

    @Override
	public String toString() {
    	StringBuffer buf = new StringBuffer();
    	for(int i=0;i!=lines.length;++i) {
    		buf.append(lines[i]);
    		buf.append("\\n");
    	}
    	return "\"" + buf.toString() + "\"";
    }

    public class Line {
        private final int offset;
        private final int length;
        private final int number;

        public Line(int offset, int length, int number) {
            this.offset = offset;
            this.length = length;
            this.number = number;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

        public int getNumber() {
            return number;
        }

        public String getText() {
            return lines[number-1];
        }
    }
}
