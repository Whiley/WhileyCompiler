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
package wycc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wycc.lang.SyntacticHeap;
import wycc.lang.SyntacticHeap.Schema;
import wycc.lang.SyntacticItem;

/**
 * Represents a schema which is divided up into named sections, where the
 * intention is that each section represents some grouping of opcodes which has
 * spare capacity that can be filled in subsequent versions.
 *
 * @author David J. Pearce
 *
 */
public class SectionedSchema implements SyntacticHeap.Schema {
	/**
	 * The base schema with version 0.0 which contains nothing. From this, all
	 * schemas derive.
	 */
	public static final SectionedSchema ROOT = new SectionedSchema(null, 0, 0, new Section[0]);

	private final Schema parent;
	private final int major;
	private final int minor;
	private final Section[] sections;
	private final Opcode[] opcodes;

	public SectionedSchema(Schema parent, int major, int minor, Section[] sections) {
		this.parent = parent;
		this.major = major;
		this.minor = minor;
		this.sections = sections;
		this.opcodes = flattern(sections);
	}

	@Override
	public int getMinorVersion() {
		return minor;
	}

	@Override
	public int getMajorVersion() {
		return major;
	}

	@Override
	public Schema getParent() {
		return parent;
	}

	/**
	 * Determine number of sections in this schema.
	 *
	 * @return
	 */
	public int size() {
		return sections.length;
	}

	/**
	 * Get the ith section of this schema.
	 *
	 * @param ith
	 * @return
	 */
	public Section get(int ith) {
		return sections[ith];
	}

	/**
	 * Begin an extension of this schema
	 *
	 * @return
	 */
	public Builder extend() {
		return new Builder(this);
	}

	@Override
	public wycc.lang.SyntacticItem.Descriptor getDescriptor(int opcode) {
		Opcode d = opcodes[opcode];
		return d == null ? null : d.schema;
	}

	public static class Section {
		private final String name;
		private final int size;
		private final Opcode[] opcodes;

		public Section(String name, int size, Opcode... opcodes) {
			this.name = name;
			this.size = size;
			this.opcodes = opcodes;
		}

		public int size() {
			return opcodes.length;
		}

		public int lookup(String name) {
			for(int i=0;i!=opcodes.length;++i) {
				Opcode opcode = opcodes[i];
				if(opcode != null && name.equals(opcode.name)) {
					return i;
				}
			}
			return -1;
		}

		public Section add(Opcode opcode) {
			if(opcode.name != null) {
				// NOTE: name can be null when adding blank
				int index = lookup(opcode.name);
				// Check opcode not already allocate
				if (index >= 0) {
					throw new IllegalArgumentException("duplicate opcode: " + name + ":" + opcode.name);
				} else if(size <= opcodes.length) {
					throw new IllegalArgumentException("section \"" + name + "\" full");
				}
			}
			Opcode[] nopcodes = Arrays.copyOf(opcodes,opcodes.length+1);
			nopcodes[opcodes.length] = opcode;
			return new Section(name,size,nopcodes);
		}

		public Section update(Opcode opcode) {
			int index = lookup(opcode.name);
			if(index < 0) {
				throw new IllegalArgumentException("invalid opcode \"" + opcode.name + "\"");
			}
			Opcode[] nopcodes = Arrays.copyOf(opcodes,opcodes.length);
			nopcodes[index] = opcode;
			return new Section(name,size,nopcodes);
		}
	}

	public static class Opcode {
		private final String name;
		private final SyntacticItem.Descriptor schema;

		public Opcode(String name, SyntacticItem.Descriptor schema) {
			this.name = name;
			this.schema = schema;
		}
	}

	/**
	 * Construct a base-level schema.
	 *
	 * @param major
	 * @param minor
	 * @param items
	 * @return
	 */
	public static class Builder {
		private final SectionedSchema schema;
		private final List<Action> delta;

		public Builder(SectionedSchema schema) {
			this.schema = schema;
			this.delta = new ArrayList<>();
		}

		/**
		 * Register a new section within this builder. This change is backwards
		 * compatible.
		 *
		 * @param name
		 * @param size
		 * @return
		 */
		public void register(String section, int size) {
			delta.add(new Action.Register(section, size));
		}

		/**
		 * Add a new opcode within a given section. This change is backwards compatible.
		 *
		 * @param Section
		 * @param name
		 * @param schema
		 * @return
		 */
		public void add(String section, String name, wycc.lang.SyntacticItem.Descriptor schema) {
			delta.add(new Action.Add(section, name, schema));
		}

		/**
		 * Replace an existing opcode in a given section with a new schema. This change
		 * is not backwards compatible.
		 *
		 * @param Section
		 * @param name
		 * @param schema
		 * @return
		 */
		public void replace(String section, String name, wycc.lang.SyntacticItem.Descriptor schema) {
			delta.add(new Action.Replace(section, name, schema));
		}

		/**
		 * Finalise the changes made through this builder into a new schema with an
		 * appropriate version.
		 *
		 * @return
		 */
		public SectionedSchema done() {
			// Determine whether major or minor increment
			Section[] sections = schema.sections;
			sections = Arrays.copyOf(sections, sections.length);
			boolean isMinor = true;
			for (int i = 0; i != delta.size(); ++i) {
				Action ith = delta.get(i);
				isMinor &= ith.isBackwardsCompatible();
				sections = ith.apply(sections);
			}
			int major = schema.getMajorVersion();
			int minor = schema.getMinorVersion();
			if (isMinor) {
				minor = minor + 1;
			} else {
				major = major + 1;
				minor = 0;
			}
			//
			return new SectionedSchema(schema, major, minor, sections);
		}
	}

	/**
	 * Represents an arbitrary action on a schema
	 *
	 * @author David J. Pearce
	 *
	 */
	private static abstract class Action {

		public abstract boolean isBackwardsCompatible();

		public abstract Section[] apply(Section[] sections);

		private static class Register extends Action {
			protected final String section;
			private final int size;

			public Register(String section, int size) {
				this.section = section;
				this.size = size;
			}

			@Override
			public boolean isBackwardsCompatible() {
				return true;
			}

			@Override
			public Section[] apply(Section[] sections) {
				// Check the new section doesn't already exist!
				if (lookup(sections,section) >= 0) {
					throw new IllegalArgumentException("duplicate schema section: " + section);
				}
				// Add the new section to the end
				sections = Arrays.copyOf(sections, sections.length + 1);
				sections[sections.length - 1] = new Section(section, size);
				return sections;
			}
		}

		private static class Add extends Action {
			private final String section;
			private final String name;
			private final SyntacticItem.Descriptor schema;

			public Add(String section, String name, SyntacticItem.Descriptor schema) {
				this.section = section;
				this.name = name;
				this.schema = schema;
			}

			@Override
			public boolean isBackwardsCompatible() {
				return true;
			}

			@Override
			public Section[] apply(Section[] sections) {
				// Find section in question
				int i = lookup(sections, section);
				// Apply operation
				sections[i] = sections[i].add(new Opcode(name, schema));
				// Done
				return sections;
			}
		}

		private static class Replace extends Action {
			protected final String section;
			protected final String name;
			protected final SyntacticItem.Descriptor schema;

			public Replace(String section, String name, SyntacticItem.Descriptor schema) {
				this.section = section;
				this.name = name;
				this.schema = schema;
			}

			@Override
			public boolean isBackwardsCompatible() {
				return true;
			}

			@Override
			public Section[] apply(Section[] sections) {
				// Find section in question
				int i = lookup(sections, section);
				Section section = sections[i];
				// Check opcode exists
				if (section.lookup(name) < 0) {
					throw new IllegalArgumentException("missing opcode: " + section + ":" + name);
				}
				// Perform operation
				sections[i] = section.update(new Opcode(name, schema));
				return sections;
			}
		}

		private static int lookup(Section[] sections, String section) {
			for (int i = 0; i != sections.length; ++i) {
				Section s = sections[i];
				if (s.name.equals(section)) {
					return i;
				}
			}
			return -1;
		}

		private static int lookup(Section section, String name) {
			for(int i=0;i!=section.opcodes.length;++i) {
				Opcode opcode = section.opcodes[i];
				if(opcode != null && name.equals(opcode.name)) {
					return i;
				}
			}
			return -1;
		}
	}


	private static Opcode[] flattern(Section[] sections) {
		int length = 0;
		// Determine length
		for(int i=0;i!=sections.length;++i) {
			length += sections[i].size;
		}
		// Flattern map
		Opcode[] opcodes = new Opcode[length];
		int start = 0;
		for(int i=0;i!=sections.length;++i) {
			Section ith = sections[i];
			System.arraycopy(ith.opcodes, 0, opcodes, start, ith.opcodes.length);
			start += ith.size;
		}
		// Done
		return opcodes;
	}
}

