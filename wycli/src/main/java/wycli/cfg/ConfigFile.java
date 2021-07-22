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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wycc.lang.*;
import wycc.util.AbstractCompilationUnit;
import wycc.util.AbstractSyntacticItem;

public class ConfigFile extends AbstractCompilationUnit<ConfigFile> implements Build.Artifact {
	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<ConfigFile> ContentType = new Content.Type<ConfigFile>() {
		@Override
		public ConfigFile read(Path id, InputStream input, Content.Registry registry) throws IOException {
			ConfigFileLexer lexer = new ConfigFileLexer(input);
			ConfigFileParser parser = new ConfigFileParser(id, lexer.scan());
			return parser.read();
		}

		@Override
		public void write(OutputStream output, ConfigFile value) {
			// for now
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "Content-Type: toml";
		}

		@Override
		public String getSuffix() {
			return "toml";
		}

		@Override
		public boolean includes(Class<?> kind) {
			return kind == ConfigFile.class;
		}
	};

	// =========================================================================
	// Node kinds
	// =========================================================================

	public static final int DECL_mask = 0b00010000;
	public static final int DECL_section = DECL_mask + 0;
	public static final int DECL_keyvalue = DECL_mask + 1;

	// =========================================================================
	// Constructors
	// =========================================================================
	private final Path path;
	/**
	 * The list of declarations which make up this configuration.
	 */
	private Tuple<Declaration> declarations;

	public ConfigFile(Path path) {
		this.declarations = new Tuple<>();
		this.path = path;
	}

	public ConfigFile(Path path, Tuple<Declaration> declarations) {
		this.declarations = declarations;
		//
		allocate(declarations);
		this.path = path;
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public Content.Type<ConfigFile> getContentType() {
		return ConfigFile.ContentType;
	}

	@Override
	public List<? extends Build.Artifact> getSourceArtifacts() {
		return Collections.EMPTY_LIST;
	}

	public static interface Declaration extends SyntacticItem {

	}

	public Tuple<Declaration> getDeclarations() {
		return declarations;
	}

	public void setDeclarations(Tuple<Declaration> declarations) {
		this.declarations = declarations;
	}

	/**
	 * Construct a configuration wrapper for this file. This ensures that the
	 * contents of the file meets a give configuration schema.
	 *
	 * @param schema The schema to use for the resulting configuration
	 * @param strict indicates whether or not to allow spurious entries in the
	 *               configuration file.
	 * @return
	 */
	public Configuration toConfiguration(Configuration.Schema schema, boolean strict) {
		return new Wrapper(schema, strict);
	}

	public static class Table extends AbstractSyntacticItem implements Declaration {

		public Table(Tuple<Identifier> name, Tuple<KeyValuePair> contents) {
			super(DECL_section, name, contents);
		}

		public Tuple<Identifier> getName() {
			return (Tuple<Identifier>) get(0);
		}

		public String getNameString() {
			Tuple<Identifier> ids = getName();
			String r = "";
			for (int i = 0; i != ids.size(); ++i) {
				if (i != 0) {
					r = r + "/";
				}
				r = r + ids.get(i);
			}
			return r;
		}

		public Tuple<KeyValuePair> getContents() {
			return (Tuple) get(1);
		}

		@Override
		public SyntacticItem clone(SyntacticItem[] operands) {
			return new Table((Tuple<Identifier>) operands[0], (Tuple<KeyValuePair>) operands[1]);
		}
	}

	/**
	 * Maps a given key to a given value.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class KeyValuePair extends AbstractSyntacticItem implements Declaration {

		public KeyValuePair(Identifier key, Value value) {
			super(DECL_keyvalue, key, value);
		}

		public Identifier getKey() {
			return (Identifier) get(0);
		}

		public Value getValue() {
			return (Value) get(1);
		}

		@Override
		public SyntacticItem clone(SyntacticItem[] operands) {
			return new KeyValuePair((Identifier) operands[0], (Value) operands[1]);
		}
	}

	private KeyValuePair getKeyValuePair(Path key, Tuple<? extends Declaration> decls) {
		String table = key.parent().toString();
		//
		for(int i=0;i!=decls.size();++i) {
			Declaration decl = decls.get(i);
			if(key.size() > 1 && decl instanceof Table) {
				Table s = (Table) decl;
				if (s.getNameString().equals(table)) {
					return getKeyValuePair(key.subpath(key.size() - 1, key.size()), s.getContents());
				}
			} else if(decl instanceof KeyValuePair && key.size() == 1){
				KeyValuePair p = (KeyValuePair) decl;
				if (p.getKey().toString().equals(key.get(0))) {
					return p;
				}
			}
		}
		return null;
	}

	private void insert(Path key, Object value, Tuple<Declaration> decls) {
		throw new UnsupportedOperationException();
		// FIXME: needs to be updated
//		for(int i=0;i!=decls.size();++i) {
//			Declaration decl = decls.get(i);
//			if(key.size() > 1 && decl instanceof Section) {
//				Section s = (Section) decl;
//				if(s.getName().equals(key.get(0))) {
//					insert(key.subpath(1, key.size()), value, s.getContents());
//				}
//			} else if(decl instanceof KeyValuePair && key.size() == 1){
//				KeyValuePair p = (KeyValuePair) decl;
//				if(p.getKey().equals(key.get(0))) {
//					p.value = value;
//					return;
//				}
//			}
//		}
//		if(key.size() == 1) {
//			declarations.add(new KeyValuePair(key.get(0),value));
//		} else {
//			throw new IllegalArgumentException("invalid key access \"" + key + "\"");
//		}
	}

	private class Wrapper implements Configuration {
		/**
		 * The schema to which this configuration file adheres.
		 */
		private final Configuration.Schema schema;

		/**
		 * Indicate whether or not to allow spurios entries (which are then hidden)
		 */
		private final boolean strict;

		public Wrapper(Configuration.Schema schema, boolean strict) {
			this.schema = schema;
			this.strict = strict;
			validate();
		}

		@Override
		public Schema getConfigurationSchema() {
			return schema;
		}

		@Override
		public boolean hasKey(Path key) {
			// Find the key-value pair
			KeyValuePair kvp = getKeyValuePair(key, declarations);
			// If didn't find a value, still might have default
			if(kvp == null && schema.isKey(key)) {
				// Get the descriptor for this key
				Configuration.KeyValueDescriptor<?> descriptor = schema.getDescriptor(key);
				// Check whether have a default
				return descriptor.hasDefault();
			} else {
				return kvp != null;
			}
		}

		@Override
		public <T> T get(Class<T> kind, Path key) {
			// Get the descriptor for this key
			Configuration.KeyValueDescriptor<?> descriptor = schema.getDescriptor(key);
			// Find the key-value pair
			KeyValuePair kvp = getKeyValuePair(key, declarations);
			if(kvp == null && descriptor.hasDefault()) {
				return (T) descriptor.getDefault();
			} else if(kvp != null) {
				// Extract the value
				Object value = kvp.getValue();
				// Sanity check the expected kind
				if (!kind.isInstance(value)) {
					throw new IllegalArgumentException("incompatible key access: expected " + kind.getSimpleName() + " got "
							+ descriptor.getType().getSimpleName());
				}
				//
				if(descriptor != null) {
					// 	Convert into value
					return (T) value;
				} else {
					throw new SyntacticException("hidden key access: " + key, ConfigFile.this, null);
				}
			} else {
				throw new SyntacticException("invalid key access: " + key, ConfigFile.this, null);
			}
		}

		@Override
		public <T> void write(Path key, T value) {
			// Get the descriptor for this key
			Configuration.KeyValueDescriptor descriptor = schema.getDescriptor(key);
			// Sanity check the expected kind
			Class<?> kind = descriptor.getType();
			//
			if (!kind.isInstance(value)) {
				throw new IllegalArgumentException("incompatible key access: expected " + kind.getSimpleName() + " got "
						+ descriptor.getType().getSimpleName());
			} else if(!descriptor.isValid(value)) {
				throw new IllegalArgumentException("incompatible key access: value does not match expected invariant");
			}
			// Update the relevant key-value pair
			insert(key, value, declarations);
		}

		@Override
		public List<Path> matchAll(Filter filter) {
			ArrayList<Path> matches = new ArrayList<>();
			match(Path.ROOT,filter,declarations,matches);
			return matches;
		}

		@Override
		public String toString() {
			List<Path> keys = matchAll(Filter.fromString("**/*"));
			String r = "{";
			for(int i=0;i!=keys.size();++i) {
				Path ith = keys.get(i);
				r = (i == 0) ? r : r + ",";
				r += ith + "=" + get(ith);
			}
			return r + "}";
		}

		private Object get(Path key) {
			// Get the descriptor for this key
			Configuration.KeyValueDescriptor<?> descriptor = schema.getDescriptor(key);
			// Find the key-value pair
			KeyValuePair kvp = getKeyValuePair(key, declarations);
			if(kvp == null && descriptor.hasDefault()) {
				return descriptor.getDefault();
			} else if(kvp != null) {
				// Extract the value
				return kvp.getValue();
			} else {
				throw new SyntacticException("invalid key access: " + key, ConfigFile.this, null);
			}
		}

		private void match(Path id, Filter filter, Tuple<? extends Declaration> declarations, ArrayList<Path> matches) {
			for (int i = 0; i != declarations.size(); ++i) {
				Declaration decl = declarations.get(i);
				if (decl instanceof Table) {
					Table table = (Table) decl;
					// FIXME: could be more efficient!
					Path tid = id;
					for (Identifier c : table.getName()) {
						tid = tid.append(c.toString());
					}
					match(tid, filter, table.getContents(), matches);
				} else if (decl instanceof KeyValuePair) {
					KeyValuePair kvp = (KeyValuePair) decl;
					Path match = id.append(kvp.getKey().toString());
					if (filter.matches(match)) {
						matches.add(match);
					}
				}
			}
		}

		private void validate() {
			List<KeyValueDescriptor<?>> descriptors = schema.getDescriptors();
			// Matched holds all concrete key-value pairs which are matched. This allows us
			// to identify any which were not matched and, hence, are invalid
			Set<Path> matched = new HashSet<>();
			// Validate all descriptors against given values.
			for (int i = 0; i != descriptors.size(); ++i) {
				KeyValueDescriptor descriptor = descriptors.get(i);
				// Sanity check the expected kind
				Class<?> kind = descriptor.getType();
				// Identify all matching keys
				List<Path> results = matchAll(descriptor.getFilter());
				// Sanity check whether required
				if(results.size() == 0 && descriptor.isRequired()) {
					throw new SyntacticException("missing key value: " + descriptor.getFilter(), ConfigFile.this, null);
				}
				// Check all matching keys
				for (Path id : results) {
					// Find corresponding key value pair.
					KeyValuePair kvp = getKeyValuePair(id, declarations);
					// NOTE: kvp != null
					if (!kind.isInstance(kvp.getValue())) {
						throw new SyntacticException(
								"invalid key value (expected " + kind.getSimpleName() + ")",
								ConfigFile.this, kvp);
					} else if (!descriptor.isValid(kvp.getValue())) {
						// Identified invalid key-value pair
						throw new SyntacticException("invalid key value", ConfigFile.this, kvp);
					}
				}
				// Remember every matched attribute
				matched.addAll(results);
			}
			if(strict) {
				// Check whether any unmatched key-valid pairs exist or not
				List<Path> all = matchAll(Filter.fromString("**/*"));
				for(int i=0;i!=all.size();++i) {
					Path id = all.get(i);
					if(!matched.contains(id)) {
						// Found unmatched attribute
						KeyValuePair kvp = getKeyValuePair(id, declarations);
						throw new SyntacticException("invalid key: " + id, ConfigFile.this, kvp.getKey());
					}
				}
			}
			// Done
		}
	}
}
