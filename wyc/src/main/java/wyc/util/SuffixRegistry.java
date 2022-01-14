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
package wyc.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jbuildgraph.util.Trie;
import jbuildstore.core.Content;
import jbuildstore.core.Content.Type;
import jbuildstore.core.Key;

public class SuffixRegistry implements Key.Map<Trie, String> {
	private final HashMap<String, Content.Type<?>> registry = new HashMap<>();

	/**
	 * Register a new content type with this registry.
	 *
	 * @param suffix
	 * @param ct
	 */
	public void add(Content.Type<?> ct) {
		this.registry.put(ct.suffix(), ct);
	}

	/**
	 * Register a list of zero or more content types with this registry.
	 * @param cts
	 */
	public void addAll(List<Content.Type<?>> cts) {
		for(Content.Type<?> ct : cts) {
			add(ct);
		}
	}

	@Override
	public String encode(Key<Trie,?> key) {
		for (Map.Entry<String, ?> e : registry.entrySet()) {
			if (e.getValue() == key.contentType()) {
				return key.id().toString().replace('/', File.separatorChar) + "." + e.getKey();
			}
		}
		return null;
	}

	@Override
	public Key<Trie,?> decode(String t) {
		Trie id = decodeKey(t);
		Type<?> ct = decodeType(t);
		if(id == null || ct == null) {
			return null;
		} else {
			return new Key.Pair<>(id, ct);
		}
	}

	private Trie decodeKey(String t) {
		int i = t.lastIndexOf('.');
		if (i >= 0) {
			t = t.substring(0,i);
		}
		return Trie.fromString(t.replace(File.separatorChar, '/'));
	}


	private Type<?> decodeType(String t) {
		int i = t.lastIndexOf('.');
		if (i >= 0) {
			String suffix = t.substring(i + 1);
			return registry.get(suffix);
		} else {
			return null;
		}
	}
}
