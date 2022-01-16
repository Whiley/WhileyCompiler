// Copyright 2011 The Whiley Project Developers
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
package wyc.lang;

import java.util.Collections;
import java.util.List;

import wycc.util.TextFile;
import wycc.util.Trie;
import wyc.io.WhileyFileLexer;

public class WhileyFile extends TextFile {
	private final List<WhileyFileLexer.Token> tokens;
	private final Trie path;

	public WhileyFile(Trie ID, byte[] bytes) {
		this(ID, new String(bytes));
	}

	public WhileyFile(Trie ID, String content) {
		super(content);
		this.path = ID;
		this.tokens = new WhileyFileLexer(content).scan();
	}

	public Trie getPath() {
		return path;
	}

	public List<WhileyFileLexer.Token> getTokens() {
		return Collections.unmodifiableList(tokens);
	}
}
