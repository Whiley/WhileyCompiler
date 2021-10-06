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
package wycli.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jbfs.util.Trie;

/**
 * Provides a dynamic configuration (i.e. one which is not backed by a file on
 * disk, etc).
 *
 * @author David J. Pearce
 *
 */
public class HashMapConfiguration implements Configuration {
	private final Configuration.Schema schema;
	private final HashMap<Trie,Object> entries;

	public HashMapConfiguration(Configuration.Schema schema) {
		this.schema = schema;
		this.entries = new HashMap<>();
	}

	@Override
	public Schema getConfigurationSchema() {
		return schema;
	}

	@Override
	public boolean hasKey(Trie key) {
		return entries.get(key) != null;
	}

	@Override
	public <T> T get(Class<T> kind, Trie key) {
		// Get descriptor (i.e. check it exists)
		KeyValueDescriptor<?> d = schema.getDescriptor(key);
		//
		Object o = entries.get(key);
		if(o == null && d.hasDefault()) {
			return (T) d.getDefault();
		} else if(kind.isInstance(o)) {
			return (T) o;
		} else {
			throw new IllegalArgumentException("invalid key accesss: " + key);
		}
	}

	@Override
	public <T> void write(Trie key, T value) {
		KeyValueDescriptor d = schema.getDescriptor(key);
		if (d.isValid(value)) {
			entries.put(key, value);
		} else {
			throw new IllegalArgumentException("invalid key accesss: " + key);
		}
	}

	@Override
	public List<Trie> matchAll(Trie filter) {
		ArrayList<Trie> ids = new ArrayList<>();
		for (Trie id : entries.keySet()) {
			if (filter.matches(id)) {
				ids.add(id);
			}
		}
		return ids;
	}

}
