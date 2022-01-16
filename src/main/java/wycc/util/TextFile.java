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

import java.nio.charset.Charset;

/**
 * A simple form of content representing a text file.
 *
 * @author David J. Pearce
 *
 */
public class TextFile  {
    private final String content;

    public TextFile(String content) {
        this.content = content;
    }

    public byte[] getBytes(Charset encoding) {
    	return content.getBytes(encoding);
    }

    public Line getEnclosingLine(int offset) {
        int line = 1;
        int start = 0;

        for(int i=0;i!=content.length();++i) {
            if(i == offset) {
                // advance to end of line
                while(i < content.length() && content.charAt(i) != '\n') {
                    i++;
                }
                // done
                return new Line(start, i - start, line);
            } else if(content.charAt(i) == '\n') {
                start = i+1;
                line = line + 1;
            }
        }
        return null;
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
            return content.substring(offset,offset+length);
        }
    }
}
