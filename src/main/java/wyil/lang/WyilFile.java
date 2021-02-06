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
package wyil.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import wybs.lang.Build;
import wybs.lang.CompilationUnit;
import wybs.lang.SyntacticHeap;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntacticItem.Descriptor;
import wybs.util.*;
import wybs.util.SectionedSchema.Section;
import wyc.util.ErrorMessages;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.lang.Content.Type;
import wyfs.lang.Path.ID;
import wyfs.util.ArrayUtils;
import wyfs.util.Trie;
import wyil.io.WyilFilePrinter;
import wyil.io.WyilFileReader;
import wyil.io.WyilFileWriter;
import wyil.util.AbstractConsumer;
import wyil.util.WyilUtils;

/**
 * <p>
 * Provides the in-memory representation of a Whiley source file (a.k.a. an
 * "Abstract Syntax Tree"). This is implemented as a "heap" of syntactic items.
 * For example, consider the following simple Whiley source file:
 * </p>
 *
 * <pre>
 * function id(int x) -> (int y):
 *     return x
 * </pre>
 *
 * <p>
 * This is represented internally using a heap of syntactic items which might
 * look something like this:
 * </p>
 *
 * <pre>
 * [00] DECL_function(#0,#2,#6,#8)
 * [01] ITEM_utf8("id")
 * [02] ITEM_tuple(#3)
 * [03] DECL_variable(#4,#5)
 * [04] ITEM_utf8("x")
 * [05] TYPE_int
 * [06] ITEM_tuple(#7)
 * [07] DECL_variable(#8,#9)
 * [08] ITEM_utf8("y")
 * [09] TYPE_int
 * [10] STMT_block(#11)
 * [11] STMT_return(#12)
 * [12] EXPR_variable(#03)
 * </pre>
 *
 * <p>
 * Each of these syntactic items will additionally be associated with one or
 * more attributes (e.g. encoding line number information, etc).
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class WyilFile extends AbstractCompilationUnit<WyilFile> implements Build.Entry {

	// =========================================================================
	// Binary Content Type
	// =========================================================================

	public static final Content.Type<WyilFile> ContentType = new Content.Printable<WyilFile>() {

		/**
		 * This method simply parses a whiley file into an abstract syntax tree. It
		 * makes little effort to check whether or not the file is syntactically
		 * correct. In particular, it does not determine the correct type of all
		 * declarations, expressions, etc.
		 *
		 * @param file
		 * @return
		 * @throws IOException
		 */
		@Override
		public WyilFile read(Path.Entry<WyilFile> e, InputStream input) throws IOException {
			WyilFile wf = new WyilFileReader(e).read();
			// new SyntacticHeapPrinter(new PrintWriter(System.out)).print(wf);
			return wf;
		}

		@Override
		public void write(OutputStream output, WyilFile value) throws IOException {
//			Class[] kinds = {
//				Decl.class,
//				Stmt.class,
//				Expr.class,
//				Type.class,
//				Value.class,
//				Tuple.class,
//				Identifier.class
//			};
//			int total = 0;
//			for(Class c : kinds) {
//				int size = value.getSyntacticItems(c).size();
//				System.out.println(c.getSimpleName() + ": " + size);
//				total = total + size;
//			}
//			System.out.println("Other: " + (value.getSyntacticItems(SyntacticItem.class).size()-total));
			new WyilFileWriter(output).write(value);
//			WyilFileWriter.printMetrics(WyilFile.SCHEMA);
		}

		@Override
		public void print(PrintStream output, WyilFile content) throws IOException {
			new WyilFilePrinter(output).apply(content);
		}

		@Override
		public String toString() {
			return "Content-Type: wyil";
		}

		@Override
		public String getSuffix() {
			return "wyil";
		}

		@Override
		public WyilFile read(wyfs.lang.Path.ID id, InputStream input) throws IOException {
			return new WyilFileReader(input).read(id);
		}
	};

	// =========================================================================
	// Schema
	// =========================================================================
	public static final int ITEM_null = 0; // <ZERO operands, ZERO>
	public static final int ITEM_bool = 1; // <ZERO operands, ONE>
	public static final int ITEM_int = 2; // <ZERO operands, MANY>
	public static final int ITEM_utf8 = 3; // <ZERO operands, MANY>
	public static final int ITEM_pair = 4; // <TWO operands, ZERO>
	public static final int ITEM_tuple = 5; // <MANY operands, ZERO>
	public static final int ITEM_array = 6; // <MANY operands, ZERO>
	public static final int ITEM_ident = 7; // <ZERO operands, MANY>
	public static final int ITEM_name = 8; // <MANY operands, ZERO>
	public static final int ITEM_decimal = 9; // <ZERO operands, MANY>
	public static final int ITEM_ref = 10; // <ONE operands, ZERO>
	public static final int ITEM_dictionary = 11; // <MANY operands, ZERO>
	public static final int ATTR_span = 14; // <THREE operands, ZERO>
	public static final int ITEM_byte = 15; // <ZERO operands, ONE>
	public static final int DECL_unknown = 16; // <ZERO operands, ZERO>
	public static final int DECL_module = 17; // <FOUR operands, ZERO>
	public static final int DECL_unit = 18; // <TWO operands, ZERO>
	public static final int DECL_import = 19; // <ONE operands, ZERO>
	public static final int DECL_importfrom = 20; // <TWO operands, ZERO>
	public static final int DECL_importwith = 21; // <TWO operands, ZERO>
	public static final int DECL_staticvar = 22; // <FOUR operands, ZERO>
	public static final int DECL_type = 23; // <FIVE operands, ZERO>
	public static final int DECL_rectype = 24; // <FIVE operands, ZERO>
	public static final int DECL_function = 25; // <EIGHT operands, ZERO>
	public static final int DECL_method = 26; // <EIGHT operands, ZERO>
	public static final int DECL_property = 27; // <SIX operands, ZERO>
	public static final int DECL_lambda = 28; // <SEVEN operands, ZERO>
	public static final int DECL_variable = 29; // <THREE operands, ZERO>
	public static final int DECL_link = 30; // <MANY operands, ZERO>
	public static final int DECL_binding = 31; // <TWO operands, ZERO>
	public static final int MOD_native = 48; // <ZERO operands, ZERO>
	public static final int MOD_export = 49; // <ZERO operands, ZERO>
	public static final int MOD_final = 50; // <ZERO operands, ZERO>
	public static final int MOD_private = 52; // <ZERO operands, ZERO>
	public static final int MOD_public = 53; // <ZERO operands, ZERO>
	public static final int TEMPLATE_type = 56; // <ONE operands, ONE>
	public static final int ATTR_error = 65; // <MANY operands, TWO>
	public static final int ATTR_stackframe = 68; // <TWO operands, ZERO>
	public static final int ATTR_counterexample = 69; // <ONE operands, ZERO>
	public static final int TYPE_unknown = 80; // <ZERO operands, ZERO>
	public static final int TYPE_void = 81; // <ZERO operands, ZERO>
	public static final int TYPE_any = 82; // <ZERO operands, ZERO>
	public static final int TYPE_null = 83; // <ZERO operands, ZERO>
	public static final int TYPE_bool = 84; // <ZERO operands, ZERO>
	public static final int TYPE_int = 85; // <ZERO operands, ZERO>
	public static final int TYPE_nominal = 86; // <TWO operands, ZERO>
	public static final int TYPE_reference = 87; // <ONE operands, ZERO>
	public static final int TYPE_array = 88; // <ONE operands, ZERO>
	public static final int TYPE_tuple = 89; // <MANY operands, ZERO>
	public static final int TYPE_record = 90; // <TWO operands, ZERO>
	public static final int TYPE_field = 91; // <TWO operands, ZERO>
	public static final int TYPE_function = 92; // <TWO operands, ZERO>
	public static final int TYPE_method = 93; // <TWO operands, ZERO>
	public static final int TYPE_property = 94; // <TWO operands, ZERO>
	public static final int TYPE_union = 95; // <MANY operands, ZERO>
	public static final int TYPE_byte = 96; // <ZERO operands, ZERO>
	public static final int TYPE_recursive = 105; // <ONE operands, ZERO>
	public static final int TYPE_universal = 106; // <ONE operands, ZERO>
	public static final int TYPE_existential = 107; // <ZERO operands, MANY>
	public static final int STMT_block = 144; // <MANY operands, ZERO>
	public static final int STMT_namedblock = 145; // <TWO operands, ZERO>
	public static final int STMT_caseblock = 146; // <TWO operands, ZERO>
	public static final int STMT_assert = 147; // <ONE operands, ZERO>
	public static final int STMT_assign = 148; // <TWO operands, ZERO>
	public static final int STMT_assume = 149; // <ONE operands, ZERO>
	public static final int STMT_debug = 150; // <ONE operands, ZERO>
	public static final int STMT_skip = 151; // <ZERO operands, ZERO>
	public static final int STMT_break = 152; // <ZERO operands, ZERO>
	public static final int STMT_continue = 153; // <ZERO operands, ZERO>
	public static final int STMT_dowhile = 154; // <FOUR operands, ZERO>
	public static final int STMT_fail = 155; // <ZERO operands, ZERO>
	public static final int STMT_for = 156; // <FOUR operands, ZERO>
	public static final int STMT_if = 158; // <TWO operands, ZERO>
	public static final int STMT_ifelse = 159; // <THREE operands, ZERO>
	public static final int STMT_initialiser = 160; // <TWO operands, ZERO>
	public static final int STMT_initialiservoid = 161; // <ONE operands, ZERO>
	public static final int STMT_return = 162; // <ONE operands, ZERO>
	public static final int STMT_returnvoid = 163; // <ZERO operands, ZERO>
	public static final int STMT_switch = 164; // <TWO operands, ZERO>
	public static final int STMT_while = 165; // <FOUR operands, ZERO>
	public static final int EXPR_variablecopy = 176; // <TWO operands, ZERO>
	public static final int EXPR_variablemove = 177; // <TWO operands, ZERO>
	public static final int EXPR_staticvariable = 179; // <TWO operands, ZERO>
	public static final int EXPR_constant = 180; // <TWO operands, ZERO>
	public static final int EXPR_cast = 181; // <TWO operands, ZERO>
	public static final int EXPR_invoke = 182; // <TWO operands, ZERO>
	public static final int EXPR_indirectinvoke = 183; // <THREE operands, ZERO>
	public static final int EXPR_logicalnot = 184; // <ONE operands, ZERO>
	public static final int EXPR_logicaland = 185; // <ONE operands, ZERO>
	public static final int EXPR_logicalor = 186; // <ONE operands, ZERO>
	public static final int EXPR_logicalimplication = 187; // <TWO operands, ZERO>
	public static final int EXPR_logicaliff = 188; // <TWO operands, ZERO>
	public static final int EXPR_logicalexistential = 189; // <TWO operands, ZERO>
	public static final int EXPR_logicaluniversal = 190; // <TWO operands, ZERO>
	public static final int EXPR_equal = 191; // <TWO operands, ZERO>
	public static final int EXPR_notequal = 192; // <TWO operands, ZERO>
	public static final int EXPR_integerlessthan = 193; // <TWO operands, ZERO>
	public static final int EXPR_integerlessequal = 194; // <TWO operands, ZERO>
	public static final int EXPR_integergreaterthan = 195; // <TWO operands, ZERO>
	public static final int EXPR_integergreaterequal = 196; // <TWO operands, ZERO>
	public static final int EXPR_is = 197; // <TWO operands, ZERO>
	public static final int EXPR_integernegation = 199; // <TWO operands, ZERO>
	public static final int EXPR_integeraddition = 200; // <THREE operands, ZERO>
	public static final int EXPR_integersubtraction = 201; // <THREE operands, ZERO>
	public static final int EXPR_integermultiplication = 202; // <THREE operands, ZERO>
	public static final int EXPR_integerdivision = 203; // <THREE operands, ZERO>
	public static final int EXPR_integerremainder = 204; // <THREE operands, ZERO>
	public static final int EXPR_bitwisenot = 207; // <TWO operands, ZERO>
	public static final int EXPR_bitwiseand = 208; // <TWO operands, ZERO>
	public static final int EXPR_bitwiseor = 209; // <TWO operands, ZERO>
	public static final int EXPR_bitwisexor = 210; // <TWO operands, ZERO>
	public static final int EXPR_bitwiseshl = 211; // <THREE operands, ZERO>
	public static final int EXPR_bitwiseshr = 212; // <THREE operands, ZERO>
	public static final int EXPR_dereference = 215; // <TWO operands, ZERO>
	public static final int EXPR_new = 216; // <TWO operands, ZERO>
	public static final int EXPR_lambdaaccess = 217; // <TWO operands, ZERO>
	public static final int EXPR_fielddereference = 218; // <THREE operands, ZERO>
	public static final int EXPR_recordaccess = 222; // <THREE operands, ZERO>
	public static final int EXPR_recordborrow = 223; // <THREE operands, ZERO>
	public static final int EXPR_recordupdate = 224; // <FOUR operands, ZERO>
	public static final int EXPR_recordinitialiser = 225; // <THREE operands, ZERO>
	public static final int EXPR_tupleinitialiser = 226; // <TWO operands, ZERO>
	public static final int EXPR_arrayaccess = 230; // <THREE operands, ZERO>
	public static final int EXPR_arrayborrow = 231; // <THREE operands, ZERO>
	public static final int EXPR_arrayupdate = 232; // <FOUR operands, ZERO>
	public static final int EXPR_arraylength = 233; // <TWO operands, ZERO>
	public static final int EXPR_arraygenerator = 234; // <THREE operands, ZERO>
	public static final int EXPR_arrayinitialiser = 235; // <TWO operands, ZERO>
	public static final int EXPR_arrayrange = 236; // <THREE operands, ZERO>
	/**
	 * Cached copy of the current schema
	 */
	private static Schema SCHEMA;

	public static Schema getSchema() {
		// FIXME: doing something?
		if(SCHEMA == null) {
			// Generate the latest schema
			SCHEMA = createSchema();
		}
		return SCHEMA;
	}

	// =========================================================================
	// Constructors
	// =========================================================================

	private final Path.ID ID;
	private final int majorVersion;
	private final int minorVersion;

	public WyilFile(Path.Entry<WyilFile> entry) {
		super(entry);
		Schema schema = WyilFile.getSchema();
		this.majorVersion = schema.getMajorVersion();
		this.minorVersion = schema.getMinorVersion();
		this.ID = (entry != null) ? entry.id() : null;
	}

	public WyilFile(Path.ID id) {
		super(null);
		this.ID = id;
		Schema schema = WyilFile.getSchema();
		this.majorVersion = schema.getMajorVersion();
		this.minorVersion = schema.getMinorVersion();
	}
	
	/**
	 * Copy constructor which creates an identical WyilFile.
	 *
	 * @param wf
	 */
	public WyilFile(Path.Entry<WyilFile> entry, WyilFile wf) {
		super(entry);
		this.majorVersion = wf.majorVersion;
		this.minorVersion = wf.minorVersion;
		// Create initial copies
		for (int i = 0; i != wf.size(); ++i) {
			SyntacticItem item = wf.getSyntacticItem(i);
			// Construct unlinked item
			item = SCHEMA.getDescriptor(item.getOpcode()).construct(item.getOpcode(), new SyntacticItem[item.size()],
					item.getData());
			syntacticItems.add(item);
			item.allocate(this, i);
		}
		// Link operands up
		for (int i = 0; i != wf.size(); ++i) {
			SyntacticItem item = wf.getSyntacticItem(i);
			SyntacticItem nItem = syntacticItems.get(i);
			for(int j=0;j!=item.size();++j) {
				int operand = item.get(j).getIndex();
				nItem.setOperand(j, syntacticItems.get(operand));
			}
		}
		// Set the distinguished root item
		setRootItem(getSyntacticItem(root));
		this.ID = (entry != null) ? entry.id() : null;
	}

	public WyilFile(Path.Entry<WyilFile> entry, int root, SyntacticItem[] items, int major, int minor) {
		super(entry);
		this.majorVersion = major;
		this.minorVersion = minor;
		// Allocate every item into this heap
		for (int i = 0; i != items.length; ++i) {
			syntacticItems.add(items[i]);
			items[i].allocate(this, i);
		}
		// Set the distinguished root item
		setRootItem(getSyntacticItem(root));
		this.ID = (entry != null) ? entry.id() : null;
	}

	public WyilFile(Path.ID ID, int root, SyntacticItem[] items, int major, int minor) {
		super(null);
		this.majorVersion = major;
		this.minorVersion = minor;
		// Allocate every item into this heap
		for (int i = 0; i != items.length; ++i) {
			syntacticItems.add(items[i]);
			items[i].allocate(this, i);
		}
		// Set the distinguished root item
		setRootItem(getSyntacticItem(root));
		this.ID = ID;
	}
	
	// =========================================================================
	// Accessors
	// =========================================================================

	@Override
	public ID getID() {
		return ID;
	}

	@Override
	public Content.Type<?> getContentType() {
		return ContentType;
	}
	
	public boolean isValid() {
		return findAll(SyntacticItem.Marker.class).size() == 0;
	}
	
	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public Decl.Module getModule() {
		return (Decl.Module) getRootItem();
	}

	public Decl.Unit getUnit() {
		// The first node is always the declaration root.
		List<Decl.Unit> modules = getSyntacticItems(Decl.Unit.class);
		if (modules.size() != 1) {
			throw new RuntimeException("expecting one module, found " + modules.size());
		}
		return modules.get(0);
	}

	public <S extends Decl.Named> S getDeclaration(Identifier name, Type signature, Class<S> kind) {
		List<S> matches = super.getSyntacticItems(kind);
		for (int i = 0; i != matches.size(); ++i) {
			S match = matches.get(i);
			if (match.getName().equals(name)) {
				if (signature != null && signature.equals(match.getType())) {
					return match;
				} else if (signature == null) {
					return match;
				}
			}
		}
		throw new IllegalArgumentException("unknown declarataion (" + name + "," + signature + ")");
	}

	@Override
	public <T extends SyntacticItem> void replace(T from, T to) {
		BitSet matches = findReachable(from, new BitSet());
		Decl.Module module = getModule();
		// Carefully remove all markers for items within that being replaced.
		Tuple<SyntacticItem.Marker> markers = module.getAttributes();
		ArrayList<SyntacticItem> items = new ArrayList<>();
		//
		for(int i=0;i!=markers.size();++i) {
			SyntacticItem marker = markers.get(i);
			for(int j=0;j!=marker.size();++j) {
				if(matches.get(marker.get(j).getIndex())) {
					// Found a match
					items.add(marker);
					break;
				}
			}
		}
		// Remove all existing markers
		module.setAttributes(markers.removeAll(items));
		// Done
		super.replace(from, to);
	}

	/**
	 * A qualified name represents a <i>fully-qualified</i> name within a
	 * compilation unit. That is, a full-qualified unit identifier and corresponding
	 * name.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class QualifiedName {
		private final Name unit;
		private final Identifier name;

		public QualifiedName(Tuple<Identifier> path, Identifier name) {
			this(path.toArray(Identifier.class), name);
		}

		public QualifiedName(Identifier[] path, Identifier name) {
			this(new Name(path), name);
		}

		public QualifiedName(Name unit, Identifier name) {
			this.unit = unit;
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof QualifiedName) {
				QualifiedName n = (QualifiedName) o;
				return unit.equals(n.unit) && name.equals(n.name);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return unit.hashCode() ^ name.hashCode();
		}

		public Name getUnit() {
			return unit;
		}

		public Identifier getName() {
			return name;
		}

		/**
		 * Provide a simple conversion from a qualified name to a generic name
		 *
		 * @return
		 */
		public Name toName() {
			return new Name(ArrayUtils.append(unit.getAll(), name));
		}

		@Override
		public String toString() {
			return unit + "::" + name;
		}
	}

	// ============================================================
	// Declarations
	// ============================================================
	/**
	 * <p>
	 * Represents a declaration within a Whiley source file. This includes <i>import
	 * declarations</i>, <i>function or method declarations</i>, <i>type
	 * declarations</i>, <i>variable declarations</i> and more.
	 * </p>
	 * <p>
	 * In general, a declaration is often a top-level entity within a module.
	 * However, this is not always the case. For example, variable declarations are
	 * used to represent local variables, function or method parameters, etc.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static interface Decl extends CompilationUnit.Declaration {

		public static class Unknown extends AbstractSyntacticItem implements Decl {
			public Unknown() {
				super(DECL_unknown);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Unknown();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "DECL_unknown") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Unknown();
				}
			};
		}

		/**
		 * A WyilFile contains exactly one active module which represents the root of
		 * all items in the module.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Module extends AbstractSyntacticItem {

			public Module(Name name, Tuple<Decl.Unit> modules, Tuple<Decl.Unit> externs,
					Tuple<SyntacticItem.Marker> attributes) {
				super(DECL_module, name, modules, externs, attributes);
			}

			public Name getName() {
				return (Name) get(0);
			}

			public Tuple<Decl.Unit> getUnits() {
				return (Tuple<Decl.Unit>) get(1);
			}

			public Tuple<Decl.Unit> getExterns() {
				return (Tuple<Decl.Unit>) get(2);
			}

			public Tuple<SyntacticItem.Marker> getAttributes() {
				return (Tuple<SyntacticItem.Marker>) get(3);
			}

			public Decl.Unit putUnit(Decl.Unit unit) {
				Tuple<Decl.Unit> units = getUnits();
				// Check whether replacing unit or adding new
				for (int i = 0; i != units.size(); ++i) {
					Decl.Unit ith = units.get(i);
					if (ith.getName().equals(unit.getName())) {
						// We're replacing an existing unit
						units.setOperand(i, unit);
						//
						return ith;
					}
				}
				// We're adding a new unit
				setOperand(1, getHeap().allocate(units.append(unit)));
				// Nothing was replaced
				return null;
			}

			public Decl.Unit putExtern(Decl.Unit unit) {
				Tuple<Decl.Unit> externs = getExterns();
				// Check whether replacing unit or adding new
				for (int i = 0; i != externs.size(); ++i) {
					Decl.Unit ith = externs.get(i);
					if (ith.getName().equals(unit.getName())) {
						// We're replacing an existing unit
						externs.setOperand(i, unit);
						return ith;
					}
				}
				// We're adding a new unit
				setOperand(2, getHeap().allocate(externs.append(unit)));
				// Nothing was replaced
				return null;
			}

			public void addAttribute(SyntacticItem.Marker attribute) {
				setOperand(3, getHeap().allocate(getAttributes().append(attribute)));
			}

			public void setAttributes(Tuple<SyntacticItem.Marker> attributes) {
				setOperand(3, getHeap().allocate(attributes));
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Module((Name) operands[0], (Tuple<Decl.Unit>) operands[1], (Tuple<Decl.Unit>) operands[2],
						(Tuple<SyntacticItem.Marker>) operands[3]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.FOUR, Data.ZERO, "DECL_module") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Module((Name) operands[0], (Tuple<Decl.Unit>) operands[1],
							(Tuple<Decl.Unit>) operands[2], (Tuple<SyntacticItem.Marker>) operands[3]);
				}
			};
		}

		/**
		 * Represents the top-level entity in a Whiley source file. All other
		 * declartions are contained within this.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Unit extends AbstractSyntacticItem implements Decl {

			public Unit(Name name, Tuple<Decl> declarations) {
				super(DECL_unit, name, declarations);
			}

			public Name getName() {
				return (Name) get(0);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Decl> getDeclarations() {
				return (Tuple<Decl>) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Unit((Name) operands[0], (Tuple<Decl>) operands[1]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "DECL_unit") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Unit((Name) operands[0], (Tuple<Decl>) operands[1]);
				}
			};
		}

		/**
		 * <p>
		 * Represents an import declaration in a Whiley source file. For example, the
		 * following illustrates a simple import statement:
		 * </p>
		 *
		 * <pre>
		 * import println from std::io
		 * </pre>
		 *
		 * <p>
		 * Here, the module is <code>std::io</code> and the symbol imported is
		 * <code>println</code>.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Import extends AbstractSyntacticItem implements Decl {
			public Import(Tuple<Identifier> path) {
				super(DECL_import, path);
			}

			public Import(Tuple<Identifier> path, boolean inclusive, Tuple<Identifier> names) {
				super(inclusive ? DECL_importwith : DECL_importfrom, path, names);
			}

			/**
			 * Get the filter path associated with this import declaration. This is
			 * <code>std::math</code> in <code>import max from std::math</code>.
			 *
			 * @return
			 */
			@SuppressWarnings("unchecked")
			public Tuple<Identifier> getPath() {
				return (Tuple<Identifier>) super.get(0);
			}

			/**
			 * Check whether from name is associated with this import declaration. This
			 * would <code>max</code> in <code>import max from std::math</code>, but is not
			 * present in <code>import std::math</code>.
			 *
			 * @return
			 */
			public boolean hasFrom() {
				return opcode == DECL_importfrom;
			}

			/**
			 * Check whether from name is associated with this import declaration. This
			 * would <code>max</code> in <code>import max from std::math</code>, but is not
			 * present in <code>import std::math</code>.
			 *
			 * @return
			 */
			public boolean hasWith() {
				return opcode == DECL_importwith;
			}

			/**
			 * Get the with name(s) associated with this import declaration. This is
			 * <code>max</code> in <code>import std::math with max</code>.
			 *
			 * @return
			 */
			public Tuple<Identifier> getNames() {
				return (Tuple<Identifier>) super.get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public Import clone(SyntacticItem[] operands) {
				switch(opcode) {
				case DECL_import:
					return new Import((Tuple<Identifier>) operands[0]);
				case DECL_importfrom:
					return new Import((Tuple<Identifier>) operands[0], false, (Tuple<Identifier>) operands[1]);
				default:
				case DECL_importwith:
					return new Import((Tuple<Identifier>) operands[0], true, (Tuple<Identifier>) operands[1]);
				}
			}

			@Override
			public String toString() {
				String r = "import ";
				if (hasFrom()) {
					r += WyilFile.toString(getNames());
					r += " from ";
				}
				Tuple<Identifier> path = getPath();
				for (int i = 0; i != path.size(); ++i) {
					if (i != 0) {
						r += ".";
					}
					Identifier component = path.get(i);
					if (component == null) {
						r += "*";
					} else {
						r += component.get();
					}
				}
				if (hasWith()) {
					r += " with ";
					r += WyilFile.toString(getNames());
				}

				return r;
			}

			public static final Descriptor DESCRIPTOR_0a = new Descriptor(Operands.ONE, Data.ZERO, "DECL_import") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Import((Tuple<Identifier>) operands[0]);
				}
			};

			public static final Descriptor DESCRIPTOR_0b = new Descriptor(Operands.TWO, Data.ZERO, "DECL_importfrom") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Import((Tuple<Identifier>) operands[0], false, (Tuple<Identifier>) operands[1]);
				}
			};
			public static final Descriptor DESCRIPTOR_0c = new Descriptor(Operands.TWO, Data.ZERO, "DECL_importwith") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Import((Tuple<Identifier>) operands[0], true, (Tuple<Identifier>) operands[1]);
				}
			};
		}

		/**
		 * A named declaration has an additional symbol name associated with it
		 *
		 * @author David J. Pearce
		 *
		 */
		public static abstract class Named<T extends WyilFile.Type> extends AbstractSyntacticItem implements Decl {

			public Named(int opcode, Tuple<Modifier> modifiers, Identifier name, SyntacticItem... rest) {
				super(opcode, ArrayUtils.append(new SyntacticItem[] { modifiers, name }, rest));
			}

			@SuppressWarnings("unchecked")
			public Tuple<Modifier> getModifiers() {
				return (Tuple<Modifier>) super.get(0);
			}

			public Identifier getName() {
				return (Identifier) super.get(1);
			}

			public QualifiedName getQualifiedName() {
				// FIXME: this is completely broken.
				Unit module = getAncestor(Decl.Unit.class);
				return new QualifiedName(module.getName(), getName());
			}

			public Tuple<Template.Variable> getTemplate() {
				throw new UnsupportedOperationException();
			}

			public abstract T getType();
		}

		/**
		 * Represents a <i>function</i>, <i>method</i> or <i>property</i> declaration in
		 * a Whiley source file.
		 */
		public static abstract class Callable extends Named<WyilFile.Type.Callable> {

			public Callable(int opcode, Tuple<Modifier> modifiers, Identifier name, Tuple<Template.Variable> template,
					Tuple<Decl.Variable> parameters, Tuple<Decl.Variable> returns, SyntacticItem... rest) {
				super(opcode, modifiers, name,
						ArrayUtils.append(new SyntacticItem[] { template, parameters, returns }, rest));
			}

			@Override
			@SuppressWarnings("unchecked")
			public Tuple<Template.Variable> getTemplate() {
				return (Tuple<Template.Variable>) get(2);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Decl.Variable> getParameters() {
				return (Tuple<Decl.Variable>) get(3);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Decl.Variable> getReturns() {
				return (Tuple<Decl.Variable>) get(4);
			}

			public static WyilFile.Type project(Tuple<Decl.Variable> decls) {
				switch(decls.size()) {
				case 0:
					return WyilFile.Type.Void;
				case 1:
					return decls.get(0).getType();
				default:
					WyilFile.Type[] types = new WyilFile.Type[decls.size()];
					for(int i=0;i!=types.length;++i) {
						types[i] = decls.get(i).getType();
					}
					return new WyilFile.Type.Tuple(types);
				}
			}

			/**
			 * Get the body associated with this callable declaration which is either a
			 * statement block (for functions and methods) or an expression (for properties
			 * and lambdas).
			 *
			 * @return
			 */
			public abstract Stmt getBody();

			@Override
			public abstract WyilFile.Type.Callable getType();
		}

		/**
		 * <p>
		 * Represents a <i>function</i>, <i>method</i> or <i>property</i> declaration in
		 * a Whiley source file. The following function declaration provides a small
		 * example to illustrate:
		 * </p>
		 *
		 * <pre>
		 * function max(int[] xs) -> (int z)
		 * // array xs cannot be empty
		 * requires |xs| > 0
		 * // return must be greater than all elements in xs
		 * ensures all { i in 0..|xs| | xs[i] <= z }
		 * // return must equal one of the elements in xs
		 * ensures some { i in 0..|xs| | xs[i] == z }
		 *     ...
		 * </pre>
		 *
		 * <p>
		 * Here, we see the specification for the well-known <code>max()</code> function
		 * which returns the largest value of an array. This employs both
		 * <i>requires</i> and <i>ensures</i> clauses:
		 * <ul>
		 * <li><b>Requires clause</b>. This defines a constraint on the permissible
		 * values of the parameters on entry to the function or method, and is often
		 * referred to as the "precondition". This expression may refer to any variables
		 * declared within the parameter type pattern. Multiple clauses may be given,
		 * and these are taken together as a conjunction. Furthermore, the convention is
		 * to specify the requires clause(s) before any ensure(s) clauses.</li>
		 * <li><b>Ensures clause</b>. This defines a constraint on the permissible
		 * values of the the function or method's return value, and is often referred to
		 * as the "postcondition". This expression may refer to any variables declared
		 * within either the parameter or return type pattern. Multiple clauses may be
		 * given, and these are taken together as a conjunction. Furthermore, the
		 * convention is to specify the requires clause(s) after the others.</li>
		 * </ul>
		 * </p>
		 *
		 * @see Callable
		 */
		public static abstract class FunctionOrMethod extends Callable {

			public FunctionOrMethod(int opcode, Tuple<Modifier> modifiers, Identifier name,
					Tuple<Template.Variable> template, Tuple<Decl.Variable> parameters, Tuple<Decl.Variable> returns,
					Tuple<Expr> requires, Tuple<Expr> ensures, Stmt.Block body, SyntacticItem... rest) {
				super(opcode, modifiers, name, template, parameters, returns,
						ArrayUtils.append(new SyntacticItem[] { requires, ensures, body }, rest));
			}

			@SuppressWarnings("unchecked")
			public Tuple<Expr> getRequires() {
				return (Tuple<Expr>) get(5);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Expr> getEnsures() {
				return (Tuple<Expr>) get(6);
			}

			@Override
			public Stmt.Block getBody() {
				return (Stmt.Block) get(7);
			}
		}

		/**
		 * <p>
		 * Represents a function declaration in a Whiley source file. For example:
		 * </p>
		 *
		 * <pre>
		 * function f(int x) -> (int y)
		 * // Parameter must be positive
		 * requires x > 0
		 * // Return must be negative
		 * ensures y < 0:
		 *    // body
		 *    return -x
		 * </pre>
		 *
		 * <p>
		 * Here, a function <code>f</code> is defined which accepts only positive
		 * integers and returns only negative integers. The variable <code>y</code> is
		 * used to refer to the return value. Functions in Whiley may not have
		 * side-effects (i.e. they are <code>pure functions</code>).
		 * </p>
		 *
		 * <p>
		 * Function declarations may also have modifiers, such as <code>public</code>
		 * and <code>private</code>.
		 * </p>
		 *
		 * @see FunctionOrMethod
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Function extends FunctionOrMethod {

			public Function(Tuple<Modifier> modifiers, Identifier name, Tuple<Template.Variable> template,
					Tuple<Decl.Variable> parameters, Tuple<Decl.Variable> returns, Tuple<Expr> requires,
					Tuple<Expr> ensures, Stmt.Block body) {
				super(DECL_function, modifiers, name, template, parameters, returns, requires, ensures, body);
			}

			@Override
			public WyilFile.Type.Function getType() {
				return new WyilFile.Type.Function(project(getParameters()),project(getReturns()));
			}

			@Override
			@SuppressWarnings("unchecked")
			public Function clone(SyntacticItem[] operands) {
				return new Function((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Template.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
						(Tuple<Decl.Variable>) operands[4], (Tuple<Expr>) operands[5], (Tuple<Expr>) operands[6],
						(Stmt.Block) operands[7]);
			}

			@Override
			public String toString() {
				return "function " + getName() + " : " + getType();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.EIGHT, Data.ZERO, "DECL_function") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Function((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(Tuple<Template.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
							(Tuple<Decl.Variable>) operands[4], (Tuple<Expr>) operands[5], (Tuple<Expr>) operands[6],
							(Stmt.Block) operands[7]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a method declaration in a Whiley source file. For example:
		 * </p>
		 *
		 * <pre>
		 * method m(int x) -> (int y)
		 * // Parameter must be positive
		 * requires x > 0
		 * // Return must be negative
		 * ensures $ < 0:
		 *    // body
		 *    return -x
		 * </pre>
		 *
		 * <p>
		 * Here, a method <code>m</code> is defined which accepts only positive integers
		 * and returns only negative integers. The variable <code>y</code> is used to
		 * refer to the return value. Unlike functions, methods in Whiley may have
		 * side-effects.
		 * </p>
		 *
		 * <p>
		 * Method declarations may also have modifiers, such as <code>public</code> and
		 * <code>private</code>.
		 * </p>
		 *
		 * @see FunctionOrMethod
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Method extends FunctionOrMethod {

			public Method(Tuple<Modifier> modifiers, Identifier name, Tuple<Template.Variable> template,
					Tuple<Decl.Variable> parameters, Tuple<Decl.Variable> returns, Tuple<Expr> requires,
					Tuple<Expr> ensures, Stmt.Block body) {
				super(DECL_method, modifiers, name, template, parameters, returns, requires, ensures, body);
			}

			@Override
			public WyilFile.Type.Method getType() {
				// FIXME: This just feels wrong as we are throwing away other template
				// variables. The issue is that callable types do not declare template variables
				// as they are compiled away.
				return new WyilFile.Type.Method(project(getParameters()), project(getReturns()));
			}

			@SuppressWarnings("unchecked")
			@Override
			public Method clone(SyntacticItem[] operands) {
				return new Method((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Template.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
						(Tuple<Decl.Variable>) operands[4], (Tuple<Expr>) operands[5], (Tuple<Expr>) operands[6],
						(Stmt.Block) operands[7]);
			}

			@Override
			public String toString() {
				return "method " + getName() + " : " + getType();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.EIGHT, Data.ZERO, "DECL_method") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Method((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(Tuple<Template.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
							(Tuple<Decl.Variable>) operands[4], (Tuple<Expr>) operands[5], (Tuple<Expr>) operands[6],
							(Stmt.Block) operands[7]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a property declaration in a Whiley source file. For example:
		 * </p>
		 *
		 * <pre>
		 * property contains(int[] xs, int x)
		 * where some { i in 0..|xs| | xs[i] == x}
		 * </pre>
		 *
		 * <p>
		 * Here, a property <code>contains</code> is defined which captures the concept
		 * of an element being contained in an array.
		 * </p>
		 *
		 * <p>
		 * Property declarations may also have modifiers, such as <code>public</code>
		 * and <code>private</code>.
		 * </p>
		 *
		 * @See FunctionOrMethod
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Property extends Callable {

			public Property(Tuple<Modifier> modifiers, Identifier name, Tuple<Template.Variable> template,
					Tuple<Decl.Variable> parameters, Tuple<Expr> invariant) {
				super(DECL_property, modifiers, name, template, parameters, new Tuple<Decl.Variable>(), invariant);
			}

			public Property(Tuple<Modifier> modifiers, Identifier name, Tuple<Template.Variable> template,
					Tuple<Decl.Variable> parameters, Tuple<Decl.Variable> returns, Tuple<Expr> invariant) {
				super(DECL_property, modifiers, name, template, parameters, returns, invariant);
			}

			@Override
			public WyilFile.Type.Property getType() {
				return new WyilFile.Type.Property(project(getParameters()));
			}

			@SuppressWarnings("unchecked")
			public Tuple<Expr> getInvariant() {
				return (Tuple<Expr>) get(5);
			}

			@Override
			public Stmt getBody() {
				// FIXME: this doesn't make sense for properties. Realistically, this should be
				// resolved when properties are changed from their current form into something
				// more useful.
				throw new UnsupportedOperationException();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Property clone(SyntacticItem[] operands) {
				return new Property((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Template.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
						(Tuple<Decl.Variable>) operands[4], (Tuple<Expr>) operands[5]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.SIX, Data.ZERO, "DECL_property") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Property((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(Tuple<Template.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
							(Tuple<Decl.Variable>) operands[4], (Tuple<Expr>) operands[5]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a lambda declaration within a Whiley source file. Sometimes also
		 * known as closures, these are anonymous function or method declarations
		 * declared within an expression. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * type func is function(int)->int
		*
		* function g() -> func:
		*    return &(int x -> x + 1)
		 * </pre>
		 * <p>
		 * This defines a lambda which accepts one parameter <code>x</code> and returns
		 * its increment.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Lambda extends Callable implements Expr {

			public Lambda(Tuple<Modifier> modifiers, Identifier name, Tuple<Decl.Variable> parameters, Expr body,
					WyilFile.Type.Callable signature) {
				this(modifiers, name, new Tuple<>(), parameters, new Tuple<Decl.Variable>(), body, signature);
			}


			public Lambda(Tuple<Modifier> modifiers, Identifier name, Tuple<Template.Variable> template,
					Tuple<Decl.Variable> parameters, Tuple<Decl.Variable> returns, Expr body, WyilFile.Type.Callable signature) {
				super(DECL_lambda, modifiers, name, template, parameters, returns, body, signature);
			}

			public Set<Decl.Variable> getCapturedVariables(Build.Meter meter) {
				UsedVariableExtractor usedVariableExtractor = new UsedVariableExtractor(meter);
				HashSet<Decl.Variable> captured = new HashSet<>();
				usedVariableExtractor.visitExpression(getBody(), captured);
				Tuple<Decl.Variable> parameters = getParameters();
				for (int i = 0; i != parameters.size(); ++i) {
					captured.remove(parameters.get(i));
				}
				return captured;
			}

			@Override
			public Expr getBody() {
				return (Expr) get(5);
			}

			@Override
			public WyilFile.Type.Callable getType() {
				return (WyilFile.Type.Callable) super.get(6);
			}

			@Override
			public void setType(WyilFile.Type type) {
				// FIXME: this is a hack which should be removed.
				type = type.as(WyilFile.Type.Callable.class);
				SyntacticHeap heap = getHeap();
				//
				if (type instanceof WyilFile.Type.Callable) {
					// Set the type
					operands[6] = heap.allocate(type);
				} else {
					throw new IllegalArgumentException();
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Lambda((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Template.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
						(Tuple<Decl.Variable>) operands[4], (Expr) operands[5], (WyilFile.Type.Callable) operands[6]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.SEVEN, Data.ZERO, "DECL_lambda") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Lambda((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(Tuple<Template.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
							(Tuple<Decl.Variable>) operands[4], (Expr) operands[5],
							(WyilFile.Type.Callable) operands[6]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a type declaration in a Whiley source file. A simple example to
		 * illustrate is:
		 * </p>
		 *
		 * <pre>
		 * type nat is (int x) where x >= 0
		 * </pre>
		 *
		 * <p>
		 * This defines a <i>constrained type</i> called <code>nat</code> which
		 * represents the set of natural numbers (i.e the non-negative integers). The
		 * "where" clause is optional and is often referred to as the type's
		 * "constraint".
		 * </p>
		 *
		 * <p>
		 * Type declarations may also have modifiers, such as <code>public</code> and
		 * <code>private</code>.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Type extends Named<WyilFile.Type> {

			public Type(Tuple<Modifier> modifiers, Identifier name, Tuple<Template.Variable> template,
					Decl.Variable vardecl, Tuple<Expr> invariant) {
				super(DECL_type, modifiers, name, template, vardecl, invariant);
			}

			@Override
			public Tuple<Template.Variable> getTemplate() {
				return (Tuple<Template.Variable>) get(2);
			}

			public Decl.Variable getVariableDeclaration() {
				return (Decl.Variable) get(3);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Expr> getInvariant() {
				return (Tuple<Expr>) get(4);
			}

			@Override
			public WyilFile.Type getType() {
				return getVariableDeclaration().getType();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Decl.Type clone(SyntacticItem[] operands) {
				return new Type((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Template.Variable>) operands[2], (Decl.Variable) operands[3], (Tuple<Expr>) operands[4]);
			}

			public static final Descriptor DESCRIPTOR_0a = new Descriptor(Operands.FIVE, Data.ZERO, "DECL_type") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Type((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(Tuple<Template.Variable>) operands[2], (Decl.Variable) operands[3], (Tuple<Expr>) operands[4]);
				}
			};

			public static final Descriptor DESCRIPTOR_0b = new Descriptor(Operands.FIVE, Data.ZERO, "DECL_rectype") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					Decl.Type r = new Type((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(Tuple<Template.Variable>) operands[2], (Decl.Variable) operands[3],
							(Tuple<Expr>) operands[4]);
					//r.setRecursive();
					return r;
				}
			};
		}

		// ============================================================
		// Variable Declaration
		// ============================================================

		/**
		 * <p>
		 * Represents a variable declaration which has an optional expression assignment
		 * referred to as an <i>initialiser</i>. If an initialiser is given, then this
		 * will be evaluated and assigned to the variable when the declaration is
		 * executed. Some example declarations:
		 * </p>
		 *
		 * <pre>
		 * int x
		 * int y = 1
		 * int z = x + y
		 * </pre>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Variable extends Named<WyilFile.Type> {
			public Variable(Tuple<Modifier> modifiers, Identifier name, WyilFile.Type type) {
				super(DECL_variable, modifiers, name, type);
			}

			protected Variable(int opcode, Tuple<Modifier> modifiers, Identifier name, WyilFile.Type type, WyilFile.Expr initialiser) {
				super(opcode, modifiers, name, type, initialiser);
			}

			@Override
			public WyilFile.Type getType() {
				return (WyilFile.Type) get(2);
			}

			@SuppressWarnings("unchecked")
			@Override
			public Decl.Variable clone(SyntacticItem[] operands) {
				return new Variable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(WyilFile.Type) operands[2]);
			}

			@Override
			public String toString() {
				return getType().toString() + " " + getName();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "DECL_variable") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Variable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(WyilFile.Type) operands[2]);
				}
			};
		}

		/**
		 * Represents a constant declaration in a Whiley source file, which has the
		 * form:
		 *
		 * <pre>
		 * ConstantDeclaration ::= "constant" Identifier "is" Expression
		 * </pre>
		 *
		 * A simple example to illustrate is:
		 *
		 * <pre>
		 * constant PI is 3.141592654
		 * </pre>
		 *
		 * Here, we are defining a constant called <code>PI</code> which represents the
		 * decimal value "3.141592654". Constant declarations may also have modifiers,
		 * such as <code>public</code> and <code>private</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public class StaticVariable extends Variable {
			public StaticVariable(Tuple<Modifier> modifiers, Identifier name, WyilFile.Type type, Expr initialiser) {
				super(DECL_staticvar, modifiers, name, type, initialiser);
			}

			@Override
			public WyilFile.Type getType() {
				return (WyilFile.Type) get(2);
			}

			public Expr getInitialiser() {
				return (Expr) get(3);
			}

			@SuppressWarnings("unchecked")
			@Override
			public StaticVariable clone(SyntacticItem[] operands) {
				return new StaticVariable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(WyilFile.Type) operands[2], (Expr) operands[3]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.FOUR, Data.ZERO, "DECL_staticvar") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new StaticVariable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(WyilFile.Type) operands[2], (Expr) operands[3]);
				}
			};
		}

		/**
		 * Represents a link to a given syntactic item which is "resolvable". That is,
		 * it is given as a name which is subsequently resolved during some compilation
		 * stage.
		 *
		 * @author David J. Pearce
		 *
		 * @param <T>
		 */
		public static class Link<T extends Decl.Named<?>> extends AbstractSyntacticItem {
			public Link(Name name) {
				super(DECL_link,name);
			}

			private Link(int opcode, SyntacticItem... operands) {
				super(opcode, operands);
			}

			public boolean isResolved() {
				return operands.length == 2;
			}

			public boolean isPartiallyResolved() {
				return operands.length >= 2;
			}

			public Name getName() {
				return (Name) operands[0];
			}

			public T getTarget() {
				if(isResolved()) {
					return ((Ref<T>) operands[1]).get();
				} else {
					throw new IllegalArgumentException("link unresolved");
				}
			}

			public List<T> getCandidates() {
				ArrayList<T> candidates = new ArrayList<>();
				for (int i = 1; i != operands.length; ++i) {
					Ref<T> candidate = (Ref<T>) operands[i];
					candidates.add(candidate.get());
				}
				return candidates;
			}

			public T lookup(WyilFile.Type type) {
				for (int i = 1; i != operands.length; ++i) {
					Ref<T> candidate = (Ref<T>) operands[i];
					Decl.Named<?> n = candidate.get();
					if(n.getType().equals(type)) {
						return (T) n;
					}
				}
				throw new IllegalArgumentException("unable to find candidate declaration");
			}

			@SuppressWarnings("unchecked")
			public void resolve(T... items) {
				SyntacticHeap heap = getHeap();
				SyntacticItem first = operands[0];
				this.operands = Arrays.copyOf(operands, items.length + 1);
				this.operands[0] = first;
				for(int i=1;i!=operands.length;++i) {
					operands[i] = heap.allocate(new Ref<>(items[i - 1]));
				}
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Link<T>(DECL_link, operands);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.MANY, Data.ZERO, "DECL_link") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Link(DECL_link, operands);
				}
			};
		}

		/**
		 * Represents a binding between a Linkable item and the corresponding
		 * declaration.
		 *
		 * @author David J. Pearce
		 *
		 * @param <T>
		 */
		public static class Binding<S extends WyilFile.Type, T extends Decl.Named<S>> extends AbstractSyntacticItem {
			private S concreteType;

			public Binding(Link<T> link, Tuple<WyilFile.Type> arguments) {
				super(DECL_binding, link, arguments);
			}

			@SuppressWarnings("unchecked")
			public Link<T> getLink() {
				return (Link<T>) get(0);
			}

			public S getConcreteType() {
				if(concreteType == null) {
					T decl = getLink().getTarget();
					S type = decl.getType();
					//
					// Substitute type parameters
					if(type instanceof WyilFile.Type.Callable) {
						concreteType = (S) WyilFile.substituteTypeCallable((WyilFile.Type.Callable) type, decl.getTemplate(),
								getArguments());
					} else {
						concreteType = (S) type.substitute(bindingFunction(decl.getTemplate(), getArguments()));
					}
				}
				return concreteType;
			}

			public T getDeclaration() {
				return getLink().getTarget();
			}

			/**
			 * Get the provided template arguments.
			 *
			 * @return
			 */
			public Tuple<WyilFile.Type> getArguments() {
				return (Tuple<WyilFile.Type>) get(1);
			}

			public void setArguments(Tuple<WyilFile.Type> arguments) {
				operands[1] = getHeap().allocate(arguments);
				concreteType = null;
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Binding((Link) operands[0], (Tuple<Type>) operands[1]);
			}

			@Override
			public String toString() {
				Identifier name = getDeclaration().getName();
				String arguments = getArguments().toBareString();
				return name + "<" + arguments + ">";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "DECL_binding") {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Binding((Decl.Link) operands[0], (Tuple<Type>) operands[1]);
				}
			};
		}
	}
	// ============================================================
	// Template
	// ============================================================
	public interface Template {
		public enum Variance {
			UNKNOWN, INVARIANT, COVARIANT, CONTRAVARIANT
		}
		public static abstract class Variable extends AbstractSyntacticItem {
			public Variable(int opcode, Identifier name, Variance variance) {
				super(opcode, new byte[] { (byte) variance.ordinal() }, name);
			}

			public Identifier getName() {
				return (Identifier) get(0);
			}

			public Variance getVariance() {
				return getVariance(data[0]);
			}

			public void setVariance(Variance v) {
				data[0] = (byte) v.ordinal();
			}

			protected static Variance getVariance(byte b) {
				switch (b) {
				case 0:
					return Variance.UNKNOWN;
				case 1:
					return Variance.INVARIANT;
				case 2:
					return Variance.COVARIANT;
				case 3:
					return Variance.CONTRAVARIANT;
				default:
					throw new IllegalArgumentException("Invalid variance modifier");
				}
			}
		}
		public static class Type extends Variable {
			public Type(Identifier name, Variance variance) {
				super(TEMPLATE_type, name, variance);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Type((Identifier) operands[0], getVariance());
			}

			@Override
			public String toString() {
				return getName().get();
			}
			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ONE, "TEMPLATE_type") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Template.Type((Identifier) operands[0], getVariance(data[0]));
				}
			};
		}
	}
	// ============================================================
	// Stmt
	// ============================================================

	/**
	 * Provides classes for representing statements in Whiley's source language.
	 * Examples include <i>assignments</i>, <i>for-loops</i>, <i>conditions</i>,
	 * etc. Each class is an instance of <code>SyntacticItem</code> and, hence, can
	 * be adorned with certain information (such as source location, etc).
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Stmt extends SyntacticItem {

		public interface Loop extends Stmt {
			//Expr getCondition();

			Tuple<Expr> getInvariant();

			Tuple<Decl.Variable> getModified();

			Stmt getBody();
		}

		/**
		 * <p>
		 * A statement block represents a sequence of zero or more consecutive
		 * statements at the same indentation level. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function abs(int x) -> (int r):
		 * // ---------------------------+
		 *    if x > 0:               // |
		 *       // ----------------+    |
		 *       return x        // |    |
		 *       // ----------------+    |
		 *    else:                   // |
		 *       // ----------------+    |
		 *       return -x       // |    |
		 *       // ----------------+    |
		 * // ---------------------------+
		 * </pre>
		 * <p>
		 * This example contains three statement blocks. The outermost block defines the
		 * body of the function and contains exactly one statement (i.e. the
		 * <code>if</code> statement). Two inner blocks are used to represent the true
		 * and false branches of the conditional.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Block extends AbstractSyntacticItem implements Stmt {
			public Block(Stmt... stmts) {
				super(STMT_block, stmts);
			}

			@Override
			public Stmt get(int i) {
				return (Stmt) super.get(i);
			}

			@Override
			public Block clone(SyntacticItem[] operands) {
				return new Block(ArrayUtils.toArray(Stmt.class, operands));
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.MANY, Data.ZERO, "STMT_block") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Block(ArrayUtils.toArray(Stmt.class, operands));
				}
			};
		}

		/**
		 * Represents a named block, which has the form:
		 *
		 * <pre>
		 * NamedBlcok ::= LifetimeIdentifier ':' NewLine Block
		 * </pre>
		 *
		 * As an example:
		 *
		 * <pre>
		 * function sum():
		 *   &this:int x = new:this x
		 *   myblock:
		 *     &myblock:int y = new:myblock y
		 * </pre>
		 */
		public static class NamedBlock extends AbstractSyntacticItem implements Stmt {
			public NamedBlock(Identifier name, Stmt.Block block) {
				super(STMT_namedblock, name, block);
			}

			public Identifier getName() {
				return (Identifier) super.get(0);
			}

			public Block getBlock() {
				return (Block) super.get(1);
			}

			@Override
			public NamedBlock clone(SyntacticItem[] operands) {
				return new NamedBlock((Identifier) operands[0], (Block) operands[1]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "STMT_namedblock") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new NamedBlock((Identifier) operands[0], (Stmt.Block) operands[1]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a assert statement of the form "<code>assert e</code>", where
		 * <code>e</code> is a boolean expression. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function abs(int x) -> int:
		 *     if x < 0:
		 *         x = -x
		 *     assert x >= 0
		 *     return x
		 * </pre>
		 *
		 * <p>
		 * Assertions are either statically checked by the verifier, or turned into
		 * runtime checks.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Assert extends AbstractSyntacticItem implements Stmt {
			public Assert(Expr condition) {
				super(STMT_assert, condition);
			}

			public Expr getCondition() {
				return (Expr) super.get(0);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Assert((Expr) operands[0]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "STMT_assert") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Assert((Expr) operands[0]);
				}
			};
		}

		/**
		 * <p>
		 * Represents an assignment statement of the form "<code>lhs = rhs</code>".
		 * Here, the <code>rhs</code> is any expression, whilst the <code>lhs</code>
		 * must be an <code>LVal</code> --- that is, an expression permitted on the
		 * left-side of an assignment. The following illustrates different possible
		 * assignment statements:
		 * </p>
		 *
		 * <pre>
		 * x = y       // variable assignment
		 * x.f = y     // field assignment
		 * x[i] = y    // list assignment
		 * x[i].f = y  // compound assignment
		 * </pre>
		 *
		 * <p>
		 * The last assignment here illustrates that the left-hand side of an assignment
		 * can be arbitrarily complex, involving nested assignments into lists and
		 * records.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Assign extends AbstractSyntacticItem implements Stmt {
			public Assign(Tuple<LVal> lvals, Tuple<Expr> rvals) {
				super(STMT_assign, lvals, rvals);
			}

			@SuppressWarnings("unchecked")
			public Tuple<LVal> getLeftHandSide() {
				return (Tuple<LVal>) super.get(0);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Expr> getRightHandSide() {
				return (Tuple<Expr>) super.get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Assign((Tuple<LVal>) operands[0], (Tuple<Expr>) operands[1]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "STMT_assign") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Assign((Tuple<LVal>) operands[0], (Tuple<Expr>) operands[1]);
				}
			};
		}

		/**
		 * <p>
		 * Represents an assume statement of the form "<code>assume e</code>", where
		 * <code>e</code> is a boolean expression. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function abs(int x) -> int:
		 *     if x < 0:
		 *         x = -x
		 *     assume x >= 0
		 *     return x
		 * </pre>
		 *
		 * <p>
		 * Assumptions are assumed by the verifier and, since this may be unsound,
		 * always turned into runtime checks.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Assume extends AbstractSyntacticItem implements Stmt {
			public Assume(Expr condition) {
				super(STMT_assume, condition);
			}

			public Expr getCondition() {
				return (Expr) super.get(0);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Assume((Expr) operands[0]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "STMT_assume") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Assume((Expr) operands[0]);
				}
			};
		}

		/**
		 * Represents a debug statement of the form "<code>debug e</code>" where
		 * <code>e</code> is a string expression. Debug statements are effectively print
		 * statements in debug mode, and no-operations otherwise.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Debug extends AbstractSyntacticItem implements Stmt {
			public Debug(Expr condition) {
				super(STMT_debug, condition);
			}

			public Expr getOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Debug((Expr) operands[0]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "STMT_debug") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Debug((Expr) operands[0]);
				}
			};
		}

		/**
		 * Represents a classical skip statement of the form "<code>skip</code>". A skip
		 * statement is simply a no-operation.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Skip extends AbstractSyntacticItem implements Stmt {
			public Skip() {
				super(STMT_skip);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Skip();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "STMT_skip") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Skip();
				}
			};
		}

		/**
		 * Represents a classical break statement of the form "<code>break</code>" which
		 * can be used to force the termination of a loop or switch statement.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Break extends AbstractSyntacticItem implements Stmt {
			public Break() {
				super(STMT_break);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Break();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "STMT_break") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Break();
				}
			};
		}

		/**
		 * Represents a classical continue statement of the form "<code>continue</code>"
		 * which can be used to proceed to the next iteration of a loop or the next case
		 * of a switch statement.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Continue extends AbstractSyntacticItem implements Stmt {
			public Continue() {
				super(STMT_continue);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Continue();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "STMT_continue") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Continue();
				}
			};
		}

		/**
		 * <p>
		 * Represents a do-while statement whose body is made up from a block of
		 * statements separated by indentation. As an example:
		 * </p>
		 *
		 * <pre>
		 * function sum([int] xs) -> int
		 * requires |xs| > 0:
		 *   int r = 0
		 *   int i = 0
		 *   do:
		 *     r = r + xs[i]
		 *     i = i + 1
		 *   while i < |xs| where i >= 0
		 *   return r
		 * </pre>
		 *
		 * <p>
		 * The <code>where</code> clause is optional, and commonly referred to as the
		 * <i>loop invariant</i>. When multiple clauses are given, these are combined
		 * using a conjunction. The combined invariant defines a condition which must be
		 * true on every iteration of the loop.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class DoWhile extends AbstractSyntacticItem implements Loop {
			public DoWhile(Expr condition, Tuple<Expr> invariant, Tuple<Decl.Variable> modified, Stmt.Block body) {
				super(STMT_dowhile, condition, invariant, modified, body);
			}

			public Expr getCondition() {
				return (Expr) super.get(0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Tuple<Expr> getInvariant() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Tuple<Decl.Variable> getModified() {
				return (Tuple<Decl.Variable>) super.get(2);
			}

			public void setModified(Tuple<Decl.Variable> modified) {
				operands[2] = modified;
			}

			@Override
			public Stmt.Block getBody() {
				return (Stmt.Block) super.get(3);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new DoWhile((Expr) operands[0], (Tuple<Expr>) operands[1], (Tuple<Decl.Variable>) operands[2],
						(Stmt.Block) operands[3]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.FOUR, Data.ZERO, "STMT_dowhile") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new DoWhile((Expr) operands[0], (Tuple<Expr>) operands[1],
							(Tuple<Decl.Variable>) operands[2], (Stmt.Block) operands[3]);
				}
			};
		}

		/**
		 * Represents a fail statement for the form "<code>fail</code>". This causes an
		 * abrupt termination of the program and should represent dead-code if present.
		 */
		public static class Fail extends AbstractSyntacticItem implements Stmt {
			public Fail() {
				super(STMT_fail);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Fail();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "STMT_fail") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Fail();
				}
			};
		}

		/**
		 * <p>
		 * Represents a for statement made up of variable initialisers and a block of statements
		 * referred to as the <i>body</i>. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function sum(int[] xs) -> int:
		 *   int r = 0
		 *   for i in 0..|xs|:
		 *     r = r + xs[i]
		 *   return r
		 * </pre>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class For extends AbstractSyntacticItem implements Loop {
			public For(Decl.StaticVariable var, Tuple<Expr> invariant, Tuple<Decl.Variable> modified, Stmt.Block trueBranch) {
				super(STMT_for, var, invariant, modified, trueBranch);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new For((Decl.StaticVariable) operands[0], (Tuple<Expr>) operands[1], (Tuple<Decl.Variable>) operands[2], (Stmt.Block) operands[2]);
			}

			public Decl.StaticVariable getVariable() {
				return (Decl.StaticVariable) operands[0];
			}

			public Tuple<Expr> getInvariant() {
				return (Tuple<Expr>) operands[1];
			}

			public Tuple<Decl.Variable> getModified() {
				return (Tuple<Decl.Variable>)operands[2];
			}

			public void setModified(Tuple<Decl.Variable> modified) {
				operands[2] = modified;
			}

			public Stmt.Block getBody() {
				return (Stmt.Block) operands[3];
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.FOUR, Data.ZERO, "STMT_for") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new For((Decl.StaticVariable) operands[0], (Tuple<Expr>) operands[1], (Tuple<Decl.Variable>) operands[2], (Stmt.Block) operands[2]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a classical if-else statement consisting of a <i>condition</i>, a
		 * <i>true branch</i> and an optional <i>false branch</i>. The following
		 * illustrates:
		 * </p>
		 *
		 * <pre>
		 * function max(int x, int y) -> int:
		 *   if(x > y):
		 *     return x
		 *   else if(x == y):
		 *   	return 0
		 *   else:
		 *     return y
		 * </pre>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IfElse extends AbstractSyntacticItem implements Stmt {
			public IfElse(Expr condition, Stmt.Block trueBranch) {
				super(STMT_if, condition, trueBranch);
			}

			public IfElse(Expr condition, Stmt.Block trueBranch, Stmt.Block falseBranch) {
				super(STMT_ifelse, condition, trueBranch, falseBranch);
			}

			public boolean hasFalseBranch() {
				return getOpcode() == STMT_ifelse;
			}

			public Expr getCondition() {
				return (Expr) super.get(0);
			}

			public Stmt.Block getTrueBranch() {
				return (Stmt.Block) super.get(1);
			}

			public Stmt.Block getFalseBranch() {
				return (Stmt.Block) super.get(2);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				if (operands.length == 2) {
					return new IfElse((Expr) operands[0], (Stmt.Block) operands[1]);
				} else {
					return new IfElse((Expr) operands[0], (Stmt.Block) operands[1], (Stmt.Block) operands[2]);
				}
			}

			public static final Descriptor DESCRIPTOR_0a = new Descriptor(Operands.TWO, Data.ZERO, "STMT_if") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IfElse((Expr) operands[0], (Stmt.Block) operands[1]);
				}
			};

			public static final Descriptor DESCRIPTOR_0b = new Descriptor(Operands.THREE, Data.ZERO, "STMT_ifelse") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IfElse((Expr) operands[0], (Stmt.Block) operands[1], (Stmt.Block) operands[2]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a variable initialiser statement which declares one or more
		 * variables with an optional initialiser.  The following illustrates:
		 * </p>
		 * <pre>
		 * int x, int y = swap(1,2)
		 * </pre>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Initialiser extends AbstractSyntacticItem implements Stmt {
			public Initialiser(Tuple<Decl.Variable> variables) {
				super(STMT_initialiservoid, variables);
			}
			public Initialiser(Tuple<Decl.Variable> variables, Expr initialiser) {
				super(STMT_initialiser, variables, initialiser);
			}

			public boolean hasInitialiser() {
				return opcode == STMT_initialiser;
			}

			public Tuple<Decl.Variable> getVariables() {
				return (Tuple<Decl.Variable>) get(0);
			}

			public Expr getInitialiser() {
				return (Expr) get(1);
			}

			public Type getType() {
				return Type.Tuple.create(getVariables().map(v -> v.getType()));
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				if(hasInitialiser()) {
					return new Initialiser((Tuple<Decl.Variable>) operands[0], (Expr) operands[1]);
				} else {
					return new Initialiser((Tuple<Decl.Variable>) operands[0]);
				}
			}

			public static final Descriptor DESCRIPTOR_0a = new Descriptor(Operands.TWO, Data.ZERO, "STMT_initialiser") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Initialiser((Tuple<Decl.Variable>) operands[0], (Expr) operands[1]);
				}
			};

			public static final Descriptor DESCRIPTOR_0b = new Descriptor(Operands.ONE, Data.ZERO, "STMT_initialiservoid") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Initialiser((Tuple<Decl.Variable>) operands[0]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a return statement which has one or more optional return
		 * expressions referred to simply as the "returns". Note that, the returned
		 * expression (if there is one) must begin on the same line as the return
		 * statement itself. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function f(int x) -> int:
		 * 	  return x + 1
		 * </pre>
		 *
		 * <p>
		 * Here, we see a simple <code>return</code> statement which returns an
		 * <code>int</code> value.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Return extends AbstractSyntacticItem implements Stmt {
			public Return() {
				super(STMT_returnvoid);
			}
			public Return(Expr operand) {
				super(STMT_return, operand);
			}

			public boolean hasReturn() {
				return opcode == STMT_return;
			}

			@SuppressWarnings("unchecked")
			public Expr getReturn() {
				return (Expr) super.get(0);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				if(hasReturn()) {
					return new Return((Expr) operands[0]);
				} else {
					return new Return();
				}
			}
			public static final Descriptor DESCRIPTOR_0a = new Descriptor(Operands.ONE, Data.ZERO, "STMT_return") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Return((Expr) operands[0]);
				}
			};

			public static final Descriptor DESCRIPTOR_0b = new Descriptor(Operands.ZERO, Data.ZERO, "STMT_returnvoid") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Return();
				}
			};
		}

		/**
		 * <p>
		 * Represents a classical switch statement made of up a condition and one or
		 * more case blocks. Each case consists of zero or more constant expressions.
		 * The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * switch x:
		 *   case 1:
		 *     y = -1
		 *   case 2:
		 *     y = -2
		 *   default:
		 *     y = 0
		 * </pre>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Switch extends AbstractSyntacticItem implements Stmt {
			public Switch(Expr condition, Tuple<Case> cases) {
				super(STMT_switch, condition, cases);
			}

			public Expr getCondition() {
				return (Expr) get(0);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Case> getCases() {
				return (Tuple<Case>) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Switch((Expr) operands[0], (Tuple<Case>) operands[1]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "STMT_switch") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Switch((Expr) operands[0], (Tuple<Stmt.Case>) operands[1]);
				}
			};
		}

		public static class Case extends AbstractSyntacticItem {

			public Case(Tuple<Expr> conditions, Stmt.Block block) {
				super(STMT_caseblock, conditions, block);
			}

			public boolean isDefault() {
				return getConditions().size() == 0;
			}

			@SuppressWarnings("unchecked")
			public Tuple<Expr> getConditions() {
				return (Tuple<Expr>) get(0);
			}

			public Stmt.Block getBlock() {
				return (Stmt.Block) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Case((Tuple<Expr>) operands[0], (Stmt.Block) operands[1]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "STMT_caseblock") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Case((Tuple<Expr>) operands[0], (Stmt.Block) operands[1]);
				}
			};
		}

		/**
		 * <p>
		 * Represents a while statement made up a condition and a block of statements
		 * referred to as the <i>body</i>. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function sum([int] xs) -> int:
		 *   int r = 0
		 *   int i = 0
		 *   while i < |xs| where i >= 0:
		 *     r = r + xs[i]
		 *     i = i + 1
		 *   return r
		 * </pre>
		 *
		 * <p>
		 * The optional <code>where</code> clause(s) are commonly referred to as the
		 * "loop invariant". When multiple clauses are given, these are combined using a
		 * conjunction. The combined invariant defines a condition which must be true on
		 * every iteration of the loop.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class While extends AbstractSyntacticItem implements Loop {
			public While(Expr condition, Tuple<Expr> invariant, Tuple<Decl.Variable> modified, Stmt.Block body) {
				super(STMT_while, condition, invariant, modified, body);
			}

			public Expr getCondition() {
				return (Expr) super.get(0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Tuple<Expr> getInvariant() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Tuple<Decl.Variable> getModified() {
				return (Tuple<Decl.Variable>) super.get(2);
			}

			public void setModified(Tuple<Decl.Variable> modified) {
				operands[2] = modified;
			}

			@Override
			public Stmt.Block getBody() {
				return (Stmt.Block) super.get(3);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new While((Expr) operands[0], (Tuple<Expr>) operands[1], (Tuple<Decl.Variable>) operands[2],
						(Stmt.Block) operands[3]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.FOUR, Data.ZERO, "STMT_while") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new While((Expr) operands[0], (Tuple<Expr>) operands[1],
							(Tuple<Decl.Variable>) operands[2], (Stmt.Block) operands[3]);
				}
			};
		}
	}

	/**
	 * <p>
	 * Represents an arbitrary expression permissible on the left-hand side of an
	 * assignment statement. For example, consider the following method:
	 * </p>
	 *
	 * <pre>
	 * method f(int[] xs, int x, int y):
	 *   x = y + 1
	 *   xs[i] = x
	 * </pre>
	 * <p>
	 * This contains two assignment statements with the lval's <code>x</code> and
	 * <code>xs[i]</code> respectively. The set of lvals is a subset of the set of
	 * all expressions, since not every expression can be assigned. For example, an
	 * assignment "<code>f() = x</code>" does not make sense.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface LVal extends Expr {

	}

	/**
	 * Represents an arbitrary expression within a Whiley source file. Every
	 * expression has a known type and zero or more expression operands alongside
	 * other syntactic information.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Expr extends Stmt {

		/**
		 * Get the type to which this expression is guaranteed to evaluate. That is, the
		 * result type of this expression.
		 *
		 * @return
		 */
		public Type getType();

		/**
		 * Set the inferred return type for this expression. Observe that some
		 * expressions do not support this operation.
		 *
		 * @param type
		 */
		public void setType(Type type);

		// =========================================================================
		// General Expressions
		// =========================================================================

		/**
		 * Represents an abstract operator expression over exactly one <i>operand
		 * expression</i>. For example, <code>!x</code> is a unary operator expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface UnaryOperator extends Expr {
			public Expr getOperand();
		}

		/**
		 * Represents an abstract operator expression over exactly two <i>operand
		 * expressions</i>. For example, <code>x &lt;&lt; 1</code> is a binary operator
		 * expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface BinaryOperator extends Expr {
			public Expr getFirstOperand();

			public Expr getSecondOperand();
		}

		/**
		 * Represents an abstract operator expression over exactly three <i>operand
		 * expressions</i>. For example, <code>xs[i:=1]</code> is a ternary operator
		 * expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface TernaryOperator extends Expr {
			public Expr getFirstOperand();

			public Expr getSecondOperand();

			public Expr getThirdOperand();
		}

		/**
		 * Represents an abstract operator expression over one or more <i>operand
		 * expressions</i>. For example. in <code>arr[i+1]</code> the expression
		 * <code>i+1</code> would be an nary operator expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface NaryOperator extends Expr {
			public Tuple<Expr> getOperands();
		}

		public abstract static class AbstractExpr extends AbstractSyntacticItem implements Expr {
			public AbstractExpr(int opcode, Type type, SyntacticItem... items) {
				super(opcode, ArrayUtils.append(type, items));
			}

			@Override
			public Type getType() {
				return (Type) super.get(0);
			}

			@Override
			public void setType(Type type) {
				operands[0] = type;
			}
		}

		/**
		 * Represents a cast expression of the form "<code>(T) e</code>" where
		 * <code>T</code> is the <i>cast type</i> and <code>e</code> the <i>casted
		 * expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Cast extends AbstractExpr implements Expr, UnaryOperator {
			public Cast(Type type, Expr rhs) {
				super(EXPR_cast, type, rhs);
			}

			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Cast clone(SyntacticItem[] operands) {
				return new Cast((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "(" + getType() + ") " + getOperand();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_cast") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Cast((Type) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents the use of a constant within some expression. For example, in
		 * <code>x + 1</code> the expression <code>1</code> is a constant expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Constant extends AbstractExpr implements Expr {
			public Constant(Type type, Value value) {
				super(EXPR_constant, type, value);
			}

			public Value getValue() {
				return (Value) get(1);
			}

			@Override
			public Constant clone(SyntacticItem[] operands) {
				return new Constant((Type) operands[0], (Value) operands[1]);
			}

			@Override
			public String toString() {
				return getValue().toString();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_constant") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Constant((Type) operands[0], (Value) operands[1]);
				}
			};
		}

		/**
		 * Represents the use of a static variable within an expression. A static
		 * variable is effectively a global variable which may or may not be defined
		 * within the enclosing module.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class StaticVariableAccess extends AbstractExpr implements LVal, Expr, Linkable {
			public StaticVariableAccess(Type type, Decl.Link<Decl.StaticVariable> name) {
				super(EXPR_staticvariable, type, name);
			}

			@Override
			public Decl.Link<Decl.StaticVariable> getLink() {
				return (Decl.Link<Decl.StaticVariable>) get(1);
			}

			@Override
			public StaticVariableAccess clone(SyntacticItem[] operands) {
				return new StaticVariableAccess((Type) operands[0], (Decl.Link<Decl.StaticVariable>) operands[1]);
			}

			@Override
			public String toString() {
				return getLink().toString();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_staticvariable") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new StaticVariableAccess((Type) operands[0],
							(Decl.Link<Decl.StaticVariable>) operands[1]);
				}
			};
		}

		/**
		 * Represents a <i>type test expression</i> of the form "<code>e is T</code>"
		 * where <code>e</code> is the <i>test expression</i> and <code>T</code> is the
		 * <i>test type</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Is extends AbstractSyntacticItem implements Expr, UnaryOperator {
			public Is(Expr lhs, Type rhs) {
				super(EXPR_is, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getOperand() {
				return (Expr) get(0);
			}

			public Type getTestType() {
				return (Type) get(1);
			}

			@Override
			public Is clone(SyntacticItem[] operands) {
				return new Is((Expr) operands[0], (Type) operands[1]);
			}

			@Override
			public String toString() {
				return getOperand() + " is " + getTestType();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_is") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Is((Expr) operands[0], (Type) operands[1]);
				}
			};
		}

		/**
		 * Represents an invocation of the form "<code>x.y.f(e1,..en)</code>". Here,
		 * <code>x.y.f</code> constitute a <i>partially-</i> or <i>fully-qualified
		 * name</i> and <code>e1</code> ... <code>en</code> are the <i>argument
		 * expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Invoke extends AbstractSyntacticItem implements Expr, NaryOperator, Bindable {

			public Invoke(Decl.Binding<Type.Callable, Decl.Callable> binding, Tuple<Expr> arguments) {
				super(EXPR_invoke, binding, arguments);
			}

			@Override
			public Type getType() {
				return getBinding().getConcreteType().getReturn();
			}

			@Override
			public void setType(Type type) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Decl.Link<Decl.Callable> getLink() {
				return getBinding().getLink();
			}

			@Override
			public Decl.Binding<Type.Callable,Decl.Callable> getBinding() {
				return (Decl.Binding<Type.Callable,Decl.Callable>) get(0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public Invoke clone(SyntacticItem[] operands) {
				return new Invoke((Decl.Binding<Type.Callable, Decl.Callable>) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return getBinding().toString() + getOperands();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_invoke") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Invoke((Decl.Binding<Type.Callable, Decl.Callable>) operands[0],
							(Tuple<Expr>) operands[1]);
				}
			};
		}

		/**
		 * Represents an indirect invocation of the form "<code>x.y(e1,..en)</code>".
		 * Here, <code>x.y</code> returns a function value and <code>e1</code> ...
		 * <code>en</code> are the <i>argument expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IndirectInvoke extends AbstractSyntacticItem implements Expr {

			public IndirectInvoke(Type type, Expr source, Tuple<Expr> arguments) {
				super(EXPR_indirectinvoke, type, source, arguments);
			}

			@Override
			public Type getType() {
				return (Type) operands[0];
			}

			@Override
			public void setType(Type type) {
				operands[0] = type;
			}

			public Expr getSource() {
				return (Expr) get(1);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Expr> getArguments() {
				return (Tuple<Expr>) get(2);
			}

			@SuppressWarnings("unchecked")
			@Override
			public IndirectInvoke clone(SyntacticItem[] operands) {
				return new IndirectInvoke((Type) operands[0], (Expr) operands[1],
						(Tuple<Expr>) operands[2]);
			}

			@Override
			public String toString() {
				String r = getSource().toString();
				r += getArguments();
				return r;
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_indirectinvoke") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IndirectInvoke((Type) operands[0], (Expr) operands[1],
							(Tuple<Expr>) operands[2]);
				}
			};
		}

		/**
		 * Represents an abstract quantified expression of the form
		 * "<code>forall(T v1, ... T vn).e</code>" or
		 * "<code>exists(T v1, ... T vn).e</code>" where <code>T1 v1</code> ...
		 * <code>Tn vn</code> are the <i>quantified variable declarations</i> and
		 * <code>e</code> is the body.
		 *
		 * @author David J. Pearce
		 *
		 */
		public abstract static class Quantifier extends AbstractSyntacticItem implements Expr, UnaryOperator {
			public Quantifier(int opcode, Decl.StaticVariable[] parameters, Expr body) {
				super(opcode, new Tuple<>(parameters), body);
			}

			public Quantifier(int opcode, Tuple<Decl.StaticVariable> parameters, Expr body) {
				super(opcode, parameters, body);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@SuppressWarnings("unchecked")
			public Tuple<Decl.StaticVariable> getParameters() {
				return (Tuple<Decl.StaticVariable>) get(0);
			}

			@Override
			public Expr getOperand() {
				return (Expr) get(1);
			}

			@Override
			public abstract Expr clone(SyntacticItem[] operands);
		}

		/**
		 * Represents an unbounded universally quantified expression of the form
		 * "<code>forall(T v1, ... T vn).e</code>" where <code>T1 v1</code> ...
		 * <code>Tn vn</code> are the <i>quantified variable declarations</i> and
		 * <code>e</code> is the body.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class UniversalQuantifier extends Quantifier {
			public UniversalQuantifier(Decl.StaticVariable[] parameters, Expr body) {
				super(EXPR_logicaluniversal, new Tuple<>(parameters), body);
			}

			public UniversalQuantifier(Tuple<Decl.StaticVariable> parameters, Expr body) {
				super(EXPR_logicaluniversal, parameters, body);
			}

			@SuppressWarnings("unchecked")
			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new UniversalQuantifier((Tuple<Decl.StaticVariable>) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				String r = "forall";
				r += getParameters();
				r += ".";
				r += getOperand();
				return r;
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_logicaluniversal") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new UniversalQuantifier((Tuple<Decl.StaticVariable>) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents an unbounded existentially quantified expression of the form
		 * "<code>some(T v1, ... T vn).e</code>" where <code>T1 v1</code> ...
		 * <code>Tn vn</code> are the <i>quantified variable declarations</i> and
		 * <code>e</code> is the body.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ExistentialQuantifier extends Quantifier {
			public ExistentialQuantifier(Decl.StaticVariable[] parameters, Expr body) {
				super(EXPR_logicalexistential, new Tuple<>(parameters), body);
			}

			public ExistentialQuantifier(Tuple<Decl.StaticVariable> parameters, Expr body) {
				super(EXPR_logicalexistential, parameters, body);
			}

			@SuppressWarnings("unchecked")
			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new ExistentialQuantifier((Tuple<Decl.StaticVariable>) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				String r = "exists";
				r += getParameters();
				r += ".";
				r += getOperand();
				return r;
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_logicalexistential") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new ExistentialQuantifier((Tuple<Decl.StaticVariable>) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents the use of some variable within an expression. For example, in
		 * <code>x + 1</code> the expression <code>x</code> is a variable access
		 * expression. Every variable access is associated with a <i>variable
		 * declaration</i> that unique identifies which variable is being accessed.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class VariableAccess extends AbstractExpr implements LVal {
			public VariableAccess(Type type, Decl.Variable decl) {
				super(EXPR_variablecopy, type, decl);
			}

			public Decl.Variable getVariableDeclaration() {
				return (Decl.Variable) get(1);
			}

			/**
			 * Mark this variable access as a move or borrow
			 */
			public void setMove() {
				this.opcode = EXPR_variablemove;
			}

			public boolean isMove() {
				return this.opcode == EXPR_variablemove;
			}

			@Override
			public VariableAccess clone(SyntacticItem[] operands) {
				return new VariableAccess((Type) operands[0], (Decl.Variable) operands[1]);
			}

			@Override
			public String toString() {
				return getVariableDeclaration().getName().toString();
			}

			public static final Descriptor DESCRIPTOR_0a = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_variablecopy") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new VariableAccess((Type) operands[0], (Decl.Variable) operands[1]);
				}
			};

			public static final Descriptor DESCRIPTOR_0b = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_variablemove") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					Expr.VariableAccess v = new VariableAccess((Type) operands[0], (Decl.Variable) operands[1]);
					v.setMove();
					return v;
				}
			};
		}

		// =========================================================================
		// Logical Expressions
		// =========================================================================
		/**
		 * Represents a <i>logical conjunction</i> of the form
		 * "<code>e1 && .. && en</code>" where <code>e1</code> ... <code>en</code> are
		 * the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class LogicalAnd extends AbstractSyntacticItem implements NaryOperator {
			public LogicalAnd(Tuple<Expr> operands) {
				super(EXPR_logicaland, operands);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(0);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new LogicalAnd((Tuple<Expr>) operands[0]);
			}

			@Override
			public String toString() {
				return " && ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "EXPR_logicaland") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new LogicalAnd((Tuple<Expr>) operands[0]);
				}
			};
		}

		/**
		 * Represents a <i>logical disjunction</i> of the form
		 * "<code>e1 || .. || en</code>" where <code>e1</code> ... <code>en</code> are
		 * the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class LogicalOr extends AbstractSyntacticItem implements NaryOperator {
			public LogicalOr(Tuple<Expr> operands) {
				super(EXPR_logicalor, operands);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(0);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new LogicalOr((Tuple<Expr>) operands[0]);
			}

			@Override
			public String toString() {
				return " || ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "EXPR_logicalor") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new LogicalOr((Tuple<Expr>) operands[0]);
				}
			};
		}

		/**
		 * Represents a <i>logical implication</i> of the form "<code>e1 ==> e2</code>"
		 * where <code>e1</code> and <code>e2</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class LogicalImplication extends AbstractSyntacticItem implements BinaryOperator {
			public LogicalImplication(Expr lhs, Expr rhs) {
				super(EXPR_logicalimplication, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new LogicalImplication((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " ==> ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_logicalimplication") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new LogicalImplication((Expr) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents a <i>logical biconditional</i> of the form
		 * "<code>e1 <==> e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class LogicalIff extends AbstractSyntacticItem implements BinaryOperator {
			public LogicalIff(Expr lhs, Expr rhs) {
				super(EXPR_logicaliff, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new LogicalIff((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " <==> ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_logicaliff") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new LogicalIff((Expr) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents a <i>logical negation</i> of the form "<code>!e</code>" where
		 * <code>e</code> is the <i>operand expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class LogicalNot extends AbstractSyntacticItem implements UnaryOperator {
			public LogicalNot(Expr operand) {
				super(EXPR_logicalnot, operand);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getOperand() {
				return (Expr) get(0);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new LogicalNot((Expr) operands[0]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "EXPR_logicalnot") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new LogicalNot((Expr) operands[0]);
				}
			};
		}

		// =========================================================================
		// Comparator Expressions
		// =========================================================================

		/**
		 * Represents an equality expression of the form "<code>e1 == e2</code>" where
		 * <code>e1</code> and <code>e2</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Equal extends AbstractSyntacticItem implements BinaryOperator {
			public Equal(Expr lhs, Expr rhs) {
				super(EXPR_equal, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new Equal((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " == ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_equal") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Equal((Expr) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents an unequality expression of the form "<code>e1 != e2</code>" where
		 * <code>e1</code> and <code>e2</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class NotEqual extends AbstractSyntacticItem implements BinaryOperator {
			public NotEqual(Expr lhs, Expr rhs) {
				super(EXPR_notequal, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new NotEqual((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " != ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_notequal") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new NotEqual((Expr) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents a strict <i>inequality expression</i> of the form
		 * "<code>e1 < e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerLessThan extends AbstractSyntacticItem implements BinaryOperator {
			public IntegerLessThan(Expr lhs, Expr rhs) {
				super(EXPR_integerlessthan, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerLessThan((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " < ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_integerlessthan") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerLessThan((Expr) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents a non-strict <i>inequality expression</i> of the form
		 * "<code>e1 <= e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerLessThanOrEqual extends AbstractSyntacticItem implements BinaryOperator {
			public IntegerLessThanOrEqual(Expr lhs, Expr rhs) {
				super(EXPR_integerlessequal, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerLessThanOrEqual((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " <= ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_integerlessequal") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerLessThanOrEqual((Expr) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents a strict <i>inequality expression</i> of the form
		 * "<code>e1 > e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerGreaterThan extends AbstractSyntacticItem implements BinaryOperator {
			public IntegerGreaterThan(Expr lhs, Expr rhs) {
				super(EXPR_integergreaterthan, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerGreaterThan((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " > ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_integergreaterthan") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerGreaterThan((Expr) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents a non-strict <i>inequality expression</i> of the form
		 * "<code>e1 >= e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerGreaterThanOrEqual extends AbstractSyntacticItem implements BinaryOperator {
			public IntegerGreaterThanOrEqual(Expr lhs, Expr rhs) {
				super(EXPR_integergreaterequal, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerGreaterThanOrEqual((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " >= ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_integergreaterequal") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerGreaterThanOrEqual((Expr) operands[0], (Expr) operands[1]);
				}
			};
		}

		// =========================================================================
		// Integer Expressions
		// =========================================================================

		/**
		 * Represents an integer <i>addition expression</i> of the form
		 * "<code>e1 + e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerAddition extends AbstractExpr implements BinaryOperator {
			public IntegerAddition(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integeraddition, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerAddition((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " + ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_integeraddition") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerAddition((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents an integer <i>subtraction expression</i> of the form
		 * "<code>e1 - e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerSubtraction extends AbstractExpr implements BinaryOperator {
			public IntegerSubtraction(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integersubtraction, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerSubtraction((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " - ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_integersubtraction") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerSubtraction((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents an integer <i>multiplication expression</i> of the form
		 * "<code>e1 * e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerMultiplication extends AbstractExpr implements BinaryOperator {
			public IntegerMultiplication(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integermultiplication, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerMultiplication((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " * ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_integermultiplication") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerMultiplication((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents an integer <i>division expression</i> of the form
		 * "<code>e1 / e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerDivision extends AbstractExpr implements BinaryOperator {
			public IntegerDivision(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integerdivision, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerDivision((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " / ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_integerdivision") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerDivision((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents an integer <i>remainder expression</i> of the form
		 * "<code>e1 % e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerRemainder extends AbstractExpr implements BinaryOperator {
			public IntegerRemainder(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integerremainder, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerRemainder((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " % ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_integerremainder") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerRemainder((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents an integer <i>negation expression</i> of the form
		 * "<code>-e</code>" where <code>e</code> is the <i>operand expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerNegation extends AbstractExpr implements UnaryOperator {
			public IntegerNegation(Type type, Expr operand) {
				super(EXPR_integernegation, type, operand);
			}

			@Override
			public Expr getOperand() {
				return (Expr) get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerNegation((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "-" + getOperand();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_integernegation") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new IntegerNegation((Type) operands[0], (Expr) operands[1]);
				}
			};
		}

		// =========================================================================
		// Bitwise Expressions
		// =========================================================================

		/**
		 * Represents a <i>bitwise shift left</i> of the form "<code>e << i</code>"
		 * where <code>e</code> is the expression being shifted and <code>i</code> the
		 * amount it is shifted by.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseShiftLeft extends AbstractExpr implements BinaryOperator {
			public BitwiseShiftLeft(Type type, Expr lhs, Expr rhs) {
				super(EXPR_bitwiseshl, type, lhs, rhs);
			}

			/**
			 * Get the value operand to be shifted. That is <code>x</code> in
			 * <code>x << i</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			/**
			 * Get the operand indicating the amount to shift. That is <code>i</code> in
			 * <code>x << i</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseShiftLeft((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " << ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_bitwiseshl") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new BitwiseShiftLeft((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents a <i>bitwise shift right</i> of the form "<code>e >> i</code>"
		 * where <code>e</code> is the expression being shifted and <code>i</code> the
		 * amount it is shifted by.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseShiftRight extends AbstractExpr implements BinaryOperator {
			public BitwiseShiftRight(Type type, Expr lhs, Expr rhs) {
				super(EXPR_bitwiseshr, type, lhs, rhs);
			}

			/**
			 * Get the value operand to be shifted. That is <code>x</code> in
			 * <code>x >> i</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			/**
			 * Get the operand indicating the amount to shift. That is <code>i</code> in
			 * <code>x >> i</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseShiftRight((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " >> ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_bitwiseshr") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new BitwiseShiftRight((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents a <i>bitwise and</i> of the form "<code>e1 & .. & en</code>" where
		 * <code>e1</code> ... <code>en</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseAnd extends AbstractExpr implements NaryOperator {
			public BitwiseAnd(Type type, Tuple<Expr> operands) {
				super(EXPR_bitwiseand, type, operands);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseAnd((Type) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return " & ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_bitwiseand") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new BitwiseAnd((Type) operands[0], (Tuple<Expr>) operands[1]);
				}
			};
		}

		/**
		 * Represents a <i>bitwise or</i> of the form "<code>e1 | .. | en</code>" where
		 * <code>e1</code> ... <code>en</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseOr extends AbstractExpr implements NaryOperator {
			public BitwiseOr(Type type, Tuple<Expr> operands) {
				super(EXPR_bitwiseor, type, operands);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseOr((Type) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return " | ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_bitwiseor") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new BitwiseOr((Type) operands[0], (Tuple<Expr>) operands[1]);
				}
			};
		}

		/**
		 * Represents a <i>bitwise xor</i> of the form "<code>e1 ^ .. ^ en</code>" where
		 * <code>e1</code> ... <code>en</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseXor extends AbstractExpr implements NaryOperator {
			public BitwiseXor(Type type, Tuple<Expr> operands) {
				super(EXPR_bitwisexor, type, operands);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseXor((Type) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return " ^ ";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_bitwisexor") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new BitwiseXor((Type) operands[0], (Tuple<Expr>) operands[1]);
				}
			};
		}

		/**
		 * Represents a <i>bitwise complement</i> of the form "<code>~e</code>" where
		 * <code>e</code> is the <i>operand expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseComplement extends AbstractExpr implements UnaryOperator {
			public BitwiseComplement(Type type, Expr operand) {
				super(EXPR_bitwisenot, type, operand);
			}

			/**
			 * Get the operand to be complimented. That is,
			 * <code>e<code> in </code>!e</code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseComplement((Type) operands[0], (Expr) operands[1]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_bitwisenot") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new BitwiseComplement((Type) operands[0], (Expr) operands[1]);
				}
			};
		}

		// =========================================================================
		// Reference Expressions
		// =========================================================================

		/**
		 * Represents an object dereference expression of the form "<code>*e</code>"
		 * where <code>e</code> is the <i>operand expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Dereference extends AbstractExpr implements LVal, UnaryOperator {
			public Dereference(Type type, Expr operand) {
				super(EXPR_dereference, type, operand);
			}

			/**
			 * Get the operand to be dereferenced. That is,
			 * <code>e<code> in </code>*e</code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new Dereference((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "*" + getOperand();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_dereference") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Dereference((Type) operands[0], (Expr) operands[1]);
				}
			};
		}

		/**
		 * Represents an object dereference expression of the form "<code>e->f</code>"
		 * where <code>e</code> is the <i>operand expression</i> and <code>f</code> the
		 * <i>target field</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class FieldDereference extends AbstractExpr implements LVal, UnaryOperator {
			public FieldDereference(Type type, Expr operand, Identifier field) {
				super(EXPR_fielddereference, type, operand, field);
			}

			/**
			 * Get the operand to be dereferenced. That is,
			 * <code>e<code> in </code>*e</code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			public Identifier getField() {
				return (Identifier) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new FieldDereference((Type) operands[0], (Expr) operands[1], (Identifier) operands[2]);
			}

			@Override
			public String toString() {
				return getOperand() + "->" + getField();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_fielddereference") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new FieldDereference((Type) operands[0], (Expr) operands[1], (Identifier) operands[2]);
				}
			};
		}

		/**
		 * Represents an <i>object allocation</i> expression of the form
		 * <code>new e</code> or <code>new e</code> where <code>e</code> is the
		 * operand expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class New extends AbstractExpr implements LVal, UnaryOperator {

			public New(Type type, Expr operand) {
				super(EXPR_new, type, operand);
			}

			/**
			 * Get the operand to be evaluated and stored in the heap. That is,
			 * <code>e<code> in </code>new e</code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new New((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "new " + getOperand();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_new") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new New((Type) operands[0], (Expr) operands[1]);
				}
			};
		}

		public static class LambdaAccess extends AbstractSyntacticItem implements Expr, Bindable {

			public LambdaAccess(Decl.Binding<Type.Callable,Decl.Callable> name, Type parameters) {
				super(EXPR_lambdaaccess, name, parameters);
			}

			@Override
			public Type getType() {
				return getLink().getTarget().getType();
			}

			@Override
			public void setType(Type type) {
				if(!type.equals(Type.Bool)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public Decl.Link<Decl.Callable> getLink() {
				return getBinding().getLink();
			}

			@Override
			public Decl.Binding<Type.Callable, Decl.Callable> getBinding() {
				return (Decl.Binding<Type.Callable, Decl.Callable>) get(0);
			}

			@SuppressWarnings("unchecked")
			public Type getParameterTypes() {
				return (Type) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new LambdaAccess((Decl.Binding<Type.Callable, Decl.Callable>) operands[0],
						(Type.Tuple) operands[1]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_lambdaaccess") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new LambdaAccess((Decl.Binding<Type.Callable, Decl.Callable>) operands[0],
							(Type) operands[1]);
				}
			};

		}

		// =========================================================================
		// Array Expressions
		// =========================================================================

		/**
		 * Represents an <i>array access expression</i> of the form
		 * "<code>arr[e]</code>" where <code>arr</code> is the <i>source array</i> and
		 * <code>e</code> the <i>subscript expression</i>. This returns the value held
		 * in the element determined by <code>e</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayAccess extends AbstractExpr implements LVal, BinaryOperator {
			public ArrayAccess(Type type, Expr src, Expr index) {
				super(EXPR_arrayaccess, type, src, index);
			}

			/**
			 * Get the source array operand for this access. That is <code>xs</code> in
			 * <code>xs[i]</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) get(1);
			}

			/**
			 * Get the index operand for this access. That is <code>i</code> in
			 * <code>xs[i]</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) get(2);
			}

			public void setMove() {
				this.opcode = EXPR_arrayborrow;
			}

			public boolean isMove() {
				return opcode == EXPR_arrayborrow;
			}

			@Override
			public ArrayAccess clone(SyntacticItem[] operands) {
				return new ArrayAccess((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return getFirstOperand() + "[" + getSecondOperand() + "]";
			}

			public static final Descriptor DESCRIPTOR_0a = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_arrayaccess") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new ArrayAccess((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};

			public static final Descriptor DESCRIPTOR_0b = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_arrayborrow") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					Expr.ArrayAccess r = new ArrayAccess((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
					r.setMove();
					return r;
				}
			};
		}

		/**
		 * Represents an <i>array update expression</i> of the form
		 * "<code>arr[e1:=e2]</code>" where <code>arr</code> is the <i>source array</i>,
		 * <code>e1</code> the <i>subscript expression</i> and <code>e2</code> is the
		 * value expression. This returns a new array which is equivalent to
		 * <code>arr</code> but where the element determined by <code>e1</code> has the
		 * value resulting from <code>e2</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayUpdate extends AbstractExpr implements Expr, TernaryOperator {
			public ArrayUpdate(Type type, Expr src, Expr index, Expr value) {
				super(EXPR_arrayupdate, type, src, index, value);
			}

			/**
			 * Get the source array operand for this update. That is <code>xs</code> in
			 * <code>xs[i:=v]</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) get(1);
			}

			/**
			 * Get the index operand for this update. That is <code>i</code> in
			 * <code>xs[i:=v]</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) get(2);
			}

			/**
			 * Get the value operand of this update. That is <code>v</code> in
			 * <code>xs[i:=v]</code>.
			 */
			@Override
			public Expr getThirdOperand() {
				return (Expr) get(3);
			}

			@Override
			public ArrayUpdate clone(SyntacticItem[] operands) {
				return new ArrayUpdate((Type) operands[0], (Expr) operands[1], (Expr) operands[2], (Expr) operands[3]);
			}

			@Override
			public String toString() {
				return getFirstOperand() + "[" + getSecondOperand() + ":=" + getThirdOperand() + "]";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.FOUR, Data.ZERO, "EXPR_arrayupdate") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new ArrayUpdate((Type) operands[0], (Expr) operands[1], (Expr) operands[2],
							(Expr) operands[3]);
				}
			};
		}

		/**
		 * Represents an <i>array initialiser expression</i> of the form
		 * "<code>[e1,...,en]</code>" where <code>e1</code> ... <code>en</code> are the
		 * <i>initialiser expressions</i>. Thus returns a new array made up from those
		 * values resulting from the initialiser expressions.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayInitialiser extends AbstractExpr implements NaryOperator {
			public ArrayInitialiser(Type type, Tuple<Expr> elements) {
				super(EXPR_arrayinitialiser, type, elements);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			public ArrayInitialiser clone(SyntacticItem[] operands) {
				return new ArrayInitialiser((Type) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return Arrays.toString(toArray(SyntacticItem.class));
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_arrayinitialiser") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new ArrayInitialiser((Type) operands[0], (Tuple<Expr>) operands[1]);
				}
			};
		}

		/**
		 * Represents an <i>array generator expression</i> of the form
		 * "<code>[e1;e2]</code>" where <code>e1</code> is the <i>element expression</i>
		 * and <code>e2</code> is the <i>length expression</i>. This returns a new array
		 * whose length is determined by <code>e2</code> and where every element has
		 * contains the value determined by <code>e1</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayGenerator extends AbstractExpr implements BinaryOperator {
			public ArrayGenerator(Type type, Expr value, Expr length) {
				super(EXPR_arraygenerator, type, value, length);
			}

			/**
			 * Get the value operand for this generator. That is <code>e</code> in
			 * <code>[e; n]</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) get(1);
			}

			/**
			 * Get the length operand for this generator. That is <code>n</code> in
			 * <code>[e; n]</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) get(2);
			}

			@Override
			public ArrayGenerator clone(SyntacticItem[] operands) {
				return new ArrayGenerator((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_arraygenerator") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new ArrayGenerator((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents an <i>array range expression</i> of the form
		 * "<code>e1 .. e2</code>" where <code>e1</code> is the start of the range and
		 * <code>e2</code> the end. Thus returns a new array made up from those values
		 * between start and end (but not including the end).
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayRange extends AbstractExpr implements BinaryOperator {
			public ArrayRange(Type type, Expr start, Expr end) {
				super(EXPR_arrayrange, type, start, end);
			}

			/**
			 * Get the starting operand for this range. That is <code>s</code> in
			 * <code>s..e</code>. This determines the first element of the resulting range.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			/**
			 * Get the ending operand for this range. That is <code>e</code> in
			 * <code>s..e</code>. The range extends up to (but not including) this value.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public ArrayRange clone(SyntacticItem[] operands) {
				return new ArrayRange((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_arrayrange") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new ArrayRange((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
				}
			};
		}

		/**
		 * Represents an <i>array length expression</i> of the form "<code>|arr|</code>"
		 * where <code>arr</code> is the <i>source array</i>. This simply returns the
		 * length of array <code>arr</code>. <code>e</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayLength extends AbstractExpr implements Expr.UnaryOperator {
			public ArrayLength(Type type, Expr src) {
				super(EXPR_arraylength, type, src);
			}

			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public ArrayLength clone(SyntacticItem[] operands) {
				return new ArrayLength((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "|" + getOperand() + "|";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_arraylength") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new ArrayLength((Type) operands[0], (Expr) operands[1]);
				}
			};
		}

		// =========================================================================
		// Record Expressions
		// =========================================================================

		/**
		 * Represents a <i>record access expression</i> of the form "<code>rec.f</code>"
		 * where <code>rec</code> is the <i>source record</i> and <code>f</code> is the
		 * <i>field</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class RecordAccess extends AbstractExpr implements LVal, UnaryOperator {
			public RecordAccess(Type type, Expr lhs, Identifier rhs) {
				super(EXPR_recordaccess, type, lhs, rhs);
			}

			/**
			 * Get the source operand for this access. That is <code>e</code> in
			 * <code>e.f/code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) get(1);
			}

			/**
			 * Get the field name for this access. That is <code>f</code> in
			 * <code>e.f/code>.
			 */
			public Identifier getField() {
				return (Identifier) get(2);
			}

			public void setMove() {
				this.opcode = EXPR_recordborrow;
			}

			public boolean isMove() {
				return opcode == EXPR_recordborrow;
			}

			@Override
			public RecordAccess clone(SyntacticItem[] operands) {
				return new RecordAccess((Type) operands[0], (Expr) operands[1], (Identifier) operands[2]);
			}

			@Override
			public String toString() {
				return getOperand() + "." + getField();
			}

			public static final Descriptor DESCRIPTOR_0a = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_recordaccess") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new RecordAccess((Type) operands[0], (Expr) operands[1], (Identifier) operands[2]);
				}
			};

			public static final Descriptor DESCRIPTOR_0b = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_recordborrow") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					Expr.RecordAccess r = new RecordAccess((Type) operands[0], (Expr) operands[1],
							(Identifier) operands[2]);
					r.setMove();
					return r;
				}
			};
		}

		/**
		 * Represents a <i>tuple initialiser</i> expression of the form
		 * "<code>(e1,..,en)</code>". This returns a new tuple where each element holds
		 * the value resulting from its corresponding expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class TupleInitialiser extends AbstractExpr implements Expr, LVal, NaryOperator {
			public TupleInitialiser(Type type, Tuple<Expr> operands) {
				super(EXPR_tupleinitialiser, type, operands);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new TupleInitialiser((Type) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) operands[1];
			}

			@Override
			public String toString() {
				String r = "";
				//
				for (int i = 0; i != size(); ++i) {
					if (i != 0) {
						r += ",";
					}
					r += get(i).toString();
				}
				//
				return "(" + r + ")";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "EXPR_tupleinitialiser") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new TupleInitialiser((Type) operands[0], (Tuple<Expr>) operands[1]);
				}
			};
		}

		/**
		 * Represents a <i>record initialiser</i> expression of the form
		 * "<code>{ f1:e1, ..., fn:en }</code>" where <code>f1:e1</code> ...
		 * <code>fn:en</code> are <i>field initialisers</i>. This returns a new record
		 * where each field holds the value resulting from its corresponding expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class RecordInitialiser extends AbstractExpr implements Expr, NaryOperator {

			public RecordInitialiser(Type type, Tuple<Identifier> fields, Tuple<Expr> operands) {
				// FIXME: it would be nice for the constructor to require a record type;
				// however, the parser constructs multiple initialisers during parsing (even
				// when only one is present). This causes subsequent problems down the track.
				super(EXPR_recordinitialiser, type, fields, operands);
			}

			public Tuple<Identifier> getFields() {
				return (Tuple<Identifier>) super.get(1);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(2);
			}

			@SuppressWarnings("unchecked")
			@Override
			public RecordInitialiser clone(SyntacticItem[] operands) {
				return new RecordInitialiser((Type) operands[0], (Tuple<Identifier>) operands[1],
						(Tuple<Expr>) operands[2]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.THREE, Data.ZERO, "EXPR_recordinitialiser") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new RecordInitialiser((Type) operands[0], (Tuple<Identifier>) operands[1],
							(Tuple<Expr>) operands[2]);
				}
			};
		}

		/**
		 * Represents a <i>record update expression</i> of the form
		 * "<code>e[f:=v]</code>" where <code>e</code> is the <i>source operand</i>,
		 * <code>f</code> is the <i>field</i> and <code>v</code> is the <i>value
		 * operand</i>. This returns a new record which is equivalent to <code>e</code>
		 * but where the element in field <code>f</code> has the value resulting from
		 * <code>v</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class RecordUpdate extends AbstractExpr implements Expr, BinaryOperator {
			public RecordUpdate(Type type, Expr lhs, Identifier mhs, Expr rhs) {
				super(EXPR_recordupdate, type, lhs, mhs, rhs);
			}

			/**
			 * Get the source operand for this update. That is <code>e</code> in
			 * <code>e[f:=v]/code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) get(1);
			}

			/**
			 * Get the field name for this update. That is <code>f</code> in
			 * <code>e[f:=v]/code>.
			 */
			public Identifier getField() {
				return (Identifier) get(2);
			}

			/**
			 * Get the value operand for this update. That is <code>v</code> in
			 * <code>e[f:=v]/code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) get(3);
			}

			@Override
			public RecordUpdate clone(SyntacticItem[] operands) {
				return new RecordUpdate((Type) operands[0], (Expr) operands[1], (Identifier) operands[2],
						(Expr) operands[3]);
			}

			@Override
			public String toString() {
				return getFirstOperand() + "{" + getField() + ":=" + getSecondOperand() + "}";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.FOUR, Data.ZERO, "EXPR_recordupdate") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new RecordUpdate((Type) operands[0], (Expr) operands[1], (Identifier) operands[2],
							(Expr) operands[3]);
				}
			};
		}
	}

	// =========================================================================
	// Syntactic Type
	// =========================================================================

	public static interface Type extends SyntacticItem {

		public static final Any Any = new Any();
		public static final Void Void = new Void();
		public static final Bool Bool = new Bool();
		public static final Byte Byte = new Byte();
		public static final Int Int = new Int();
		public static final Array VoidArray = new Array(Void);
		public static final Array IntArray = new Array(Int);
		public static final Array AnyArray = new Array(Any);
		public static final Null Null = new Null();

		/**
		 * Get the "shape" of this type. That is, the number of dimensions it has.
		 *
		 * @return
		 */
		public int shape();

		/**
		 * Get the nth dimension of this type.
		 *
		 * @param nth
		 * @return
		 */
		public Type dimension(int nth);

		/**
		 * Determine whether or not this type can be written. This is equivalent to
		 * asking whether or not it has a known static size.
		 *
		 * @return
		 */
		public boolean isWriteable();

		/**
		 * Determine whether or not this type can be read. Types with an "unknown"
		 * component cannot be read.
		 *
		 * @return
		 */
		public boolean isReadable();

		/**
		 * Substitute for type parameters
		 *
		 * @param binding A function which returns
		 * @return
		 */
		public Type substitute(java.util.function.Function<Object, SyntacticItem> binding);

		/**
		 * Expose the underlying type of this type. For example, consider this:
		 *
		 * <pre>
		 *  type
		 * Point is {int x, int y}
		 *
		 * function getX(Point p) -> (int r): return p.x
		 * </pre>
		 *
		 * At the point of the record access `p.x` we may want to know the underlying
		 * record type for `p` (e.g. perhaps to calculate the offset of field `x`).
		 * However, the `getType()` method only returns an instance of `Type` which, in
		 * fact, is an instance of `Type.Nominal`. We need to further expand this to get
		 * at the underlying record type. That is what this function does.
		 *
		 * @param kind
		 * @return
		 */
		public <T extends Type> T as(Class<T> kind);

		/**
		 * Filter a type according to a given kind whilst descending through unions. For
		 * example, consider the following:
		 *
		 * <pre>
		 * {int f}|null xs = {f:0}
		 * </pre>
		 *
		 * In this case, at the point of the record initialiser, we need to extract the
		 * target type <code>{int f}</code> from the type of <code>xs</code>. This is
		 * what the filter method does. The following illustrates another example:
		 *
		 * <pre>
		 * {int f}|{bool f}|null xs = {f:0}
		 * </pre>
		 *
		 * In this case, the filter will return two instances of
		 * <code>Type.Record</code> one for <code>{int f}</code> and one for
		 * <code>{bool f}</code>.
		 *
		 * @param      <T>
		 * @param kind
		 * @param type
		 * @return
		 */

		public <T extends Type> List<T> filter(Class<T> kind);

		/**
		 * Return a canonical string which embodies this type.
		 *
		 * @return
		 */
		public String toCanonicalString();

		public interface Atom extends Type {
		}

		public interface Primitive extends Atom {

		}

		static abstract class AbstractType extends AbstractSyntacticItem implements Type {
			AbstractType(int opcode) {
				super(opcode);
			}

			AbstractType(int opcode, SyntacticItem operand) {
				super(opcode, operand);
			}

			AbstractType(int opcode, SyntacticItem... operands) {
				super(opcode, operands);
			}

			AbstractType(int opcode, byte[] bytes) {
				super(opcode, bytes);
			}

			@Override
			public int shape() {
				return 1;
			}

			@Override
			public Type dimension(int nth) {
				if(nth != 0) {
					throw new IllegalArgumentException("invalid dimension");
				} else {
					return this;
				}
			}

			@Override
			public boolean isWriteable() {
				return true;
			}

			@Override
			public boolean isReadable() {
				return true;
			}


			@Override
			public <T extends Type> T as(Class<T> kind) {
				if (kind.isInstance(this)) {
					return (T) this;
				} else {
					return null;
				}
			}

			@Override
			public <T extends Type> List<T> filter(Class<T> kind) {
				if (kind.isInstance(this)) {
					ArrayList<T> results = new ArrayList<>();
					results.add((T) this);
					return results;
				} else {
					return Collections.EMPTY_LIST;
				}
			}
		}

		/**
		 * Represents the set of all functions, methods and properties. These are values
		 * which can be called using an indirect invoke expression. Each function or
		 * method accepts zero or more parameters and will produce zero or more returns.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static interface Callable extends Atom {

			public Type getParameter();

			public Type getReturn();

			@Override
			public Type.Callable substitute(java.util.function.Function<Object, SyntacticItem> binding);
		}

		/**
		 * A void type represents the type whose variables cannot exist! That is, they
		 * cannot hold any possible value. Void is used to represent the return type of
		 * a function which does not return anything. However, it is also used to
		 * represent the element type of an empty list of set. <b>NOTE:</b> the void
		 * type is a subtype of everything; that is, it is bottom in the type lattice.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Void extends AbstractType implements Primitive {
			public Void() {
				super(TYPE_void);
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				return this;
			}

			@Override
			public Void clone(SyntacticItem[] operands) {
				return new Void();
			}

			@Override
			public int shape() {
				return 0;
			}

			@Override
			public Type dimension(int nth) {
				throw new IllegalArgumentException();
			}

			@Override
			public String toString() {
				return "void";
			}

			@Override
			public String toCanonicalString() {
				return "void";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "TYPE_void") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Void();
				}
			};
		}

		/**
		 * An any type represents the type of all possible values. This type is not
		 * currently permited in the syntax of Whiley, but is used internally within the
		 * subtyping algorithm. <b>NOTE:</b> the any type is a supertype of everything;
		 * that is, it is top in the type lattice.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Any extends AbstractType implements Primitive {
			public Any() {
				super(TYPE_any);
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				return this;
			}

			@Override
			public Any clone(SyntacticItem[] operands) {
				return new Any();
			}

			@Override
			public int shape() {
				return 1;
			}

			@Override
			public Type dimension(int nth) {
				return this;
			}

			@Override
			public String toString() {
				return "any";
			}

			@Override
			public String toCanonicalString() {
				return "any";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "TYPE_any") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Any();
				}
			};
		}

		/**
		 * The null type is a special type which should be used to show the absence of
		 * something. It is distinct from void, since variables can hold the special
		 * <code>null</code> value (where as there is no special "void" value). With all
		 * of the problems surrounding <code>null</code> and
		 * <code>NullPointerException</code>s in languages like Java and C, it may seem
		 * that this type should be avoided. However, it remains a very useful
		 * abstraction to have around and, in Whiley, it is treated in a completely safe
		 * manner (unlike e.g. Java).
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Null extends AbstractType implements Primitive {
			public Null() {
				super(TYPE_null);
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				return this;
			}

			@Override
			public Null clone(SyntacticItem[] operands) {
				return new Null();
			}

			@Override
			public String toString() {
				return "null";
			}

			@Override
			public String toCanonicalString() {
				return "null";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "TYPE_null") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Null();
				}
			};
		}

		/**
		 * Represents the set of boolean values (i.e. true and false)
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Bool extends AbstractType implements Primitive {
			public Bool() {
				super(TYPE_bool);
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				return this;
			}

			@Override
			public Bool clone(SyntacticItem[] operands) {
				return new Bool();
			}

			@Override
			public String toString() {
				return "bool";
			}

			@Override
			public String toCanonicalString() {
				return "bool";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "TYPE_bool") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Bool();
				}
			};
		}

		/**
		 * Represents a sequence of 8 bits. Note that, unlike many languages, there is
		 * no representation associated with a byte. For example, to extract an integer
		 * value from a byte, it must be explicitly decoded according to some
		 * representation (e.g. two's compliment) using an auxillary function (e.g.
		 * <code>Byte.toInt()</code>).
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Byte extends AbstractType implements Primitive {
			public Byte() {
				super(TYPE_byte);
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				return this;
			}

			@Override
			public Byte clone(SyntacticItem[] operands) {
				return new Byte();
			}

			@Override
			public String toString() {
				return "byte";
			}

			@Override
			public String toCanonicalString() {
				return "byte";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "TYPE_byte") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Byte();
				}
			};
		}

		/**
		 * Represents the set of (unbound) integer values. Since integer types in Whiley
		 * are unbounded, there is no equivalent to Java's <code>MIN_VALUE</code> and
		 * <code>MAX_VALUE</code> for <code>int</code> types.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Int extends AbstractType implements Primitive {
			public Int() {
				super(TYPE_int);
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				return this;
			}

			@Override
			public Int clone(SyntacticItem[] operands) {
				return new Int();
			}

			@Override
			public String toString() {
				return "int";
			}

			@Override
			public String toCanonicalString() {
				return "int";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "TYPE_int") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Int();
				}
			};
		}

		/**
		 * Represents an array type, which is of the form:
		 *
		 * <pre>
		 * ArrayType ::= Type '[' ']'
		 * </pre>
		 *
		 * An array type describes array values whose elements are subtypes of the
		 * element type. For example, <code>[1,2,3]</code> is an instance of array type
		 * <code>int[]</code>; however, <code>[false]</code> is not.
		 *
		 * @return
		 */
		public static class Array extends AbstractType implements Atom {
			public Array(Type element) {
				super(TYPE_array, element);
			}

			public Type getElement() {
				return (Type) get(0);
			}

			@Override
			public boolean isWriteable() {
				return false;
			}

			@Override
			public Type.Array substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				Type before = getElement();
				Type after = before.substitute(binding);
				if (before == after) {
					return this;
				} else if (after == null) {
					return null;
				} else {
					return new Array(after);
				}
			}

			@Override
			public Type.Array clone(SyntacticItem[] operands) {
				return new Array((Type) operands[0]);
			}

			@Override
			public String toString() {
				return getElement().toString() + "[]";
			}

			@Override
			public String toCanonicalString() {
				return canonicalBraceAsNecessary(getElement()) + "[]";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "TYPE_array") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Array((Type) operands[0]);
				}
			};
		}

		/**
		 * Parse a reference type, which is of the form:
		 *
		 * <pre>
		 * ReferenceType ::= '&' Type
		 * </pre>
		 *
		 * Represents a reference to an object in Whiley. For example,
		 * <code>&this:int</code> is the type of a reference to a location allocated in
		 * the enclosing scope which holds an integer value.
		 *
		 * @return
		 */
		public static class Reference extends AbstractType implements Atom {
			public Reference(Type element) {
				super(TYPE_reference, element);
			}

			public Type getElement() {
				return (Type) get(0);
			}

			public Identifier getLifetime() {
				return (Identifier) get(1);
			}

			@Override
			public Type.Reference substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				Type elementBefore = getElement();
				Type elementAfter = elementBefore.substitute(binding);
				if(elementBefore != elementAfter) {
					if(elementAfter == null) {
						return null;
					} else {
						return new Reference(elementAfter);
					}
				}
				return this;
			}

			@Override
			public Type.Reference clone(SyntacticItem[] operands) {
					return new Reference((Type) operands[0]);
			}

			@Override
			public String toString() {
				return "&" + braceAsNecessary(getElement());
			}

			@Override
			public String toCanonicalString() {
				return "&" + canonicalBraceAsNecessary(getElement());
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "TYPE_reference") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Reference((Type) operands[0]);
				}
			};
		}

		/**
		 * Represents record type, which is of the form:
		 *
		 * <pre>
		 * RecordType ::= '{' Type Identifier (',' Type Identifier)* [ ',' "..." ] '}'
		 * </pre>
		 *
		 * A record is made up of a number of fields, each of which has a unique name.
		 * Each field has a corresponding type. One can think of a record as a special
		 * kind of "fixed" map (i.e. where we know exactly which entries we have).
		 *
		 * @return
		 */
		public static class Record extends AbstractType implements Atom {
			public Record(boolean isOpen, WyilFile.Tuple<Type.Field> fields) {
				this(new Value.Bool(isOpen), fields);
			}

			public Record(boolean isOpen, WyilFile.Tuple<Identifier> fields, WyilFile.Tuple<Type> types) {
				this(new Value.Bool(isOpen), zip(fields,types));
			}

			public Record(boolean isOpen, Identifier field, Type type) {
				this(new Value.Bool(isOpen), new WyilFile.Tuple<>(new Type.Field(field, type)));
			}

			public Record(Value.Bool isOpen, WyilFile.Tuple<Type.Field> fields) {
				super(TYPE_record, isOpen, fields);
			}

			@Override
			public boolean isWriteable() {
				if(isOpen()) {
					return false;
				} else {
					WyilFile.Tuple<Field> fields = getFields();
					for (int i = 0; i != fields.size(); ++i) {
						Field vd = fields.get(i);
						if(!vd.getType().isWriteable()) {
							return false;
						}
					}
					return true;
				}
			}

			@Override
			public boolean isReadable() {
				if(isOpen()) {
					return false;
				} else {
					WyilFile.Tuple<Field> fields = getFields();
					for (int i = 0; i != fields.size(); ++i) {
						Field vd = fields.get(i);
						if(!vd.getType().isReadable()) {
							return false;
						}
					}
					return true;
				}
			}

			public boolean isOpen() {
				Value.Bool flag = (Value.Bool) get(0);
				return flag.get();
			}

			public Type getField(Identifier fieldName) {
				WyilFile.Tuple<Field> fields = getFields();
				for (int i = 0; i != fields.size(); ++i) {
					Field vd = fields.get(i);
					Identifier declaredFieldName = vd.getName();
					if (declaredFieldName.equals(fieldName)) {
						return vd.getType();
					}
				}
				return null;
			}

			@SuppressWarnings("unchecked")
			public WyilFile.Tuple<Type.Field> getFields() {
				return (WyilFile.Tuple<Type.Field>) get(1);
			}

			@Override
			public Type.Record substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				WyilFile.Tuple<Type.Field> before = getFields();
				WyilFile.Tuple<Type.Field> after = substitute(before, binding);
				if (before == after) {
					return this;
				} else if(after == null) {
					return null;
				} else {
					return new Record(isOpen(), after);
				}
			}

			@Override
			public Type.Record clone(SyntacticItem[] operands) {
				return new Record((Value.Bool) operands[0], (WyilFile.Tuple<Type.Field>) operands[1]);
			}

			/**
			 * Substitute through fields whilst minimising memory allocations
			 *
			 * @param fields
			 * @param binding
			 * @return
			 */
			private static WyilFile.Tuple<Type.Field> substitute(WyilFile.Tuple<Type.Field> fields,
					java.util.function.Function<Object, SyntacticItem> binding) {
				//
				for (int i = 0; i != fields.size(); ++i) {
					Type.Field field = fields.get(i);
					Type before = field.getType();
					Type after = before.substitute(binding);
					if(after == null) {
						return null;
					} else if (before != after) {
						// Now committed to a change
						Type.Field[] nFields = fields.toArray(Type.Field.class);
						nFields[i] = new Field(field.getName(), after);
						for (int j = i + 1; j < fields.size(); ++j) {
							field = fields.get(j);
							before = field.getType();
							after = before.substitute(binding);
							if(after == null) {
								return null;
							} else if (before != after) {
								nFields[j] = new Field(field.getName(), after);
							}
						}
						return new WyilFile.Tuple<>(nFields);
					}
				}
				//
				return fields;
			}

			@Override
			public String toString() {
				WyilFile.Tuple<Type.Field> fields = getFields();
				String r = "";
				//
				for (int i = 0; i != fields.size(); ++i) {
					Type.Field field = fields.get(i);
					if (i != 0) {
						r += ",";
					}
					r += field.toString();
				}
				//
				if(isOpen()) {
					if(fields.size() > 0) {
						r += ", ...";
					} else {
						r += "...";
					}
				}
				//
				return "{" + r + "}";
			}

			@Override
			public String toCanonicalString() {
				WyilFile.Tuple<Type.Field> fields = getFields();
				String r = "";
				//
				for (int i = 0; i != fields.size(); ++i) {
					Type.Field field = fields.get(i);
					if (i != 0) {
						r += ",";
					}
					r += field.toCanonicalString();
				}
				//
				if(isOpen()) {
					if(fields.size() > 0) {
						r += ", ...";
					} else {
						r += "...";
					}
				}
				//
				return "{" + r + "}";
			}

			private static WyilFile.Tuple<Type.Field> zip(WyilFile.Tuple<Identifier> fields, WyilFile.Tuple<Type> types) {
				Type.Field[] fs = new Type.Field[fields.size()];
				for(int i=0;i!=fs.length;++i) {
					fs[i] = new Type.Field(fields.get(i),types.get(i));
				}
				return new WyilFile.Tuple<>(fs);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "TYPE_record") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Record((Value.Bool) operands[0], (WyilFile.Tuple<Type.Field>) operands[1]);
				}
			};
		}

		public static class Field extends AbstractSyntacticItem {

			public Field(Identifier name, Type type) {
				super(TYPE_field, name, type);
			}

			public Identifier getName() {
				return (Identifier) get(0);
			}

			public Type getType() {
				return (Type) get(1);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Field((Identifier) operands[0], (Type) operands[1]);
			}

			@Override
			public String toString() {
				Type type = getType();
				if(type != null) {
					return type.toString() + " " + getName();
				} else {
					return "??? " + getName();
				}
			}

			public String toCanonicalString() {
				return getType().toString() + " " + getName();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "TYPE_field") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Field((Identifier) operands[0], (Type) operands[1]);
				}
			};
		}

		/**
		 * Represents tuple type, which is of the form:
		 *
		 * <pre>
		 * RecordType ::= '(' Type (',' Type )* ')'
		 * </pre>
		 *
		 * A tuple is simply a finite sequence of types.
		 *
		 * @return
		 */
		public static class Tuple extends AbstractType implements Atom {
			/**
			 * Create a sequence of zero or more types.
			 *
			 * @param types
			 * @return
			 */
			public static Type create(List<Type> types) {
				switch(types.size()) {
				case 0:
					return Type.Void;
				case 1:
					return types.get(0);
				default:
					return new Type.Tuple(types);
				}
			}

			/**
			 * Create a sequence of zero or more types.
			 *
			 * @param types
			 * @return
			 */
			public static Type create(Type... types) {
				switch(types.length) {
				case 0:
					return Type.Void;
				case 1:
					return types[0];
				default:
					// Sanity check
					return new Type.Tuple(types);
				}
			}

			/**
			 * Create a sequence of zero or more types.
			 *
			 * @param types
			 * @return
			 */
			public static Type create(WyilFile.Tuple<Type> types) {
				switch(types.size()) {
				case 0:
					return Type.Void;
				case 1:
					return types.get(0);
				default:
					// Sanity check
					for(int i=0;i!=types.size();++i) {
						if(types.get(i) instanceof Type.Void) {
							return Type.Void;
						}
					}
					return new Type.Tuple(types.toArray(Type.class));
				}
			}


			private Tuple(Type... types) {
				super(TYPE_tuple, types);
				if(types.length <= 1) {
					throw new IllegalArgumentException("invalid tuple created");
				}
			}

			private Tuple(List<Type> types) {
				super(TYPE_tuple, types.toArray(new Type[types.size()]));
				if(types.size() <= 1) {
					throw new IllegalArgumentException("invalid tuple created");
				}
			}

			@Override
			public int shape() {
				return size();
			}

			@Override
			public Type dimension(int nth) {
				return get(nth);
			}

			@Override
			public boolean isWriteable() {
				for(int i=0;i!=size();++i) {
					if(!get(i).isWriteable()) {
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean isReadable() {
				for(int i=0;i!=size();++i) {
					if(!get(i).isReadable()) {
						return false;
					}
				}
				return true;
			}

			@Override
			public Type get(int ith) {
				return (Type) super.get(ith);
			}

			@Override
			public Type[] getAll() {
				return (Type[]) super.getAll();
			}

			@Override
			public Type.Tuple substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				for(int i=0;i!=size();++i) {
					Type before = get(i);
					Type after = before.substitute(binding);
					if(after == null) {
						return null;
					} else if(before != after) {
						// Committed to change
						Type[] types = new Type[size()];
						for(int j=0;j!=types.length;++j) {
							types[j] = get(j).substitute(binding);
						}
						return new Tuple(types);
					}
				}
				return this;
			}

			@Override
			public Type.Tuple clone(SyntacticItem[] operands) {
				return new Tuple(ArrayUtils.toArray(Type.class, operands));
			}

			@Override
			public String toString() {
				String r = "";
				//
				for (int i = 0; i != size(); ++i) {
					if (i != 0) {
						r += ",";
					}
					r += get(i).toString();
				}
				//
				return "(" + r + ")";
			}

			@Override
			public String toCanonicalString() {
				String r = "";
				//
				for (int i = 0; i != size(); ++i) {
					if (i != 0) {
						r += ",";
					}
					r += get(i).toCanonicalString();
				}
				//
				return "(" + r + ")";
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.MANY, Data.ZERO, "TYPE_tuple") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Tuple(ArrayUtils.toArray(Type.class, operands));
				}
			};
		}


		/**
		 * Represents a nominal type, which is of the form:
		 *
		 * <pre>
		 * NominalType ::= Identifier ('.' Identifier)*
		 * </pre>
		 *
		 * A nominal type specifies the name of a type defined elsewhere. In some cases,
		 * this type can be expanded (or "inlined"). However, visibility modifiers can
		 * prevent this and, thus, give rise to true nominal types.
		 *
		 * @return
		 */
		public static class Nominal extends AbstractSyntacticItem implements Type, Linkable {

			public Nominal(Decl.Link<Decl.Type> name, WyilFile.Tuple<Type> parameters) {
				super(TYPE_nominal, name, parameters);
				if(parameters != null) {
					for(Type t : parameters) {
						if(t == null) {
							throw new IllegalArgumentException("invalid parameter types");
						}
					}
				}
			}

			@Override
			public int shape() {
				// NOTE: Don't believe we need to use the concrete type here as shape is not
				// affected by substitution.
				return getLink().getTarget().getType().shape();
			}

			@Override
			public Type dimension(int nth) {
				if(shape() == 1) {
					// In the special case we have unit shape, then return ourself. This gives
					// better error messages by retaining the nominal name.
					return this;
				} else {
					// NOTE: Don't believe we need to use the concrete type here as shape is not
					// affected by substitution.
					return getLink().getTarget().getType().dimension(nth);
				}
			}

			public boolean isAlias() {
				return getLink().getTarget().getInvariant().size() == 0;
			}

			@Override
			public boolean isWriteable() {
				return getConcreteType().isWriteable();
			}

			@Override
			public boolean isReadable() {
				return getConcreteType().isReadable();
			}

			@Override
			public Decl.Link<Decl.Type> getLink() {
				return (Decl.Link<Decl.Type>) get(0);
			}

			public WyilFile.Tuple<Type> getParameters() {
				return (WyilFile.Tuple<Type>) get(1);
			}

			@Override
			public <T extends Type> T as(Class<T> kind) {
				if(kind.isInstance(this)) {
					return (T) this;
				} else {
					return getConcreteType().as(kind);
				}
			}

			@Override
			public <T extends Type> List<T> filter(Class<T> kind) {
				if(kind.isInstance(this)) {
					ArrayList<T> result = new ArrayList<>();
					result.add((T) this);
					return result;
				} else {
					return getConcreteType().filter(kind);
				}
			}

			public Type getConcreteType() {
				Decl.Type decl = getLink().getTarget();
				WyilFile.Tuple<Template.Variable> template = decl.getTemplate();
				WyilFile.Tuple<Type> arguments = getParameters();
				Type type = decl.getType();
				//
				if (template.size() > 0) {
					type = type.substitute(bindingFunction(template,arguments));
				}
				//
				return type;
			}

			@Override
			public Type.Nominal substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				WyilFile.Tuple<Type> o_parameters = getParameters();
				WyilFile.Tuple<Type> n_parameters = WyilFile.substitute(o_parameters, binding);
				if(n_parameters == null) {
					return null;
				} else if (o_parameters == n_parameters) {
					return this;
				} else {
					return new Nominal(getLink(), n_parameters);
				}
			}

			@Override
			public Nominal clone(SyntacticItem[] operands) {
				return new Nominal((Decl.Link<Decl.Type>) operands[0], ((WyilFile.Tuple<Type>) operands[1]));
			}

			@Override
			public String toString() {
				String s = getLink().getName().toString();
				WyilFile.Tuple<Type> parameters = getParameters();
				if (parameters.size() == 0) {
					return s;
				} else {
					return s + "<" + WyilFile.toString(parameters) + ">";
				}
			}

			@Override
			public String toCanonicalString() {
				return getLink().getTarget().getQualifiedName().toString();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "TYPE_nominal") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Nominal((Decl.Link<Decl.Type>) operands[0], (WyilFile.Tuple<Type>) operands[1]);
				}
			};
		}

		/**
		 * Represents a union type, which is of the form:
		 *
		 * <pre>
		 * UnionType ::= IntersectionType ('|' IntersectionType)*
		 * </pre>
		 *
		 * Union types are used to compose types together. For example, the type
		 * <code>int|null</code> represents the type which is either an <code>int</code>
		 * or <code>null</code>.
		 *
		 * Represents the union of one or more types together. For example, the union of
		 * <code>int</code> and <code>null</code> is <code>int|null</code>. Any variable
		 * of this type may hold any integer or the null value. Furthermore, the types
		 * <code>int</code> and <code>null</code> are collectively referred to as the
		 * "bounds" of this type.
		 *
		 * @return
		 */
		public static class Union extends AbstractType implements Type {
			public static Type create(WyilFile.Tuple<Type> types) {
				return create(types.toArray(Type.class));
			}
			/**
			 * Create a sequence of zero or more types.
			 *
			 * @param types
			 * @return
			 */
			public static Type create(Type... _types) {
				Type[] types = _types;
				for (int i = 0; i != types.length; ++i) {
					Type ith = types[i];
					if (ith instanceof Type.Union) {
						Type.Union u = (Type.Union) ith;
						Type[] nTypes = new Type[types.length + u.size() - 1];
						System.arraycopy(types, 0, nTypes, 0, i);
						System.arraycopy(u.getAll(), 0, nTypes, i, u.size());
						System.arraycopy(types, i + 1, nTypes, i + u.size(), (types.length - i - 1));
						return create(nTypes);
					} else if(ith instanceof Type.Void) {
						if(types == _types) {
							types = Arrays.copyOf(_types, _types.length);
						}
						types[i] = null;
					}
				}
				//
				types = ArrayUtils.removeAll(types, null);
				types = ArrayUtils.removeDuplicates(types);
				//
				switch (types.length) {
				case 0:
					return Type.Void;
				case 1:
					return types[0];
				default:
					return new Type.Union(types);
				}
			}

			public Union(Type... types) {
				super(TYPE_union, types);
			}

			@Override
			public boolean isWriteable() {
				for(int i=0;i!=size();++i) {
					if(!get(i).isWriteable()) {
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean isReadable() {
				for(int i=0;i!=size();++i) {
					if(!get(i).isReadable()) {
						return false;
					}
				}
				return true;
			}


			@Override
			public Type get(int i) {
				return (Type) super.get(i);
			}

			@Override
			public Type[] getAll() {
				return (Type[]) super.getAll();
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				Type[] before = getAll();
				Type[] after = WyilFile.substitute(before, binding);
				if(after == null) {
					return null;
				} else if (before == after) {
					return this;
				} else {
					return create(after);
				}
			}

			@Override
			public Type.Union clone(SyntacticItem[] operands) {
				return new Union(ArrayUtils.toArray(Type.class, operands));
			}

			@Override
			public <T extends Type> List<T> filter(Class<T> kind) {
				ArrayList<T> result = new ArrayList<>();
				if (kind.isInstance(this)) {
					result.add((T) this);
				} else {
					for (int i = 0; i != size(); ++i) {
						result.addAll(get(i).filter(kind));
					}
				}
				return result;
			}

			@Override
			public String toString() {
				String r = "";
				for (int i = 0; i != size(); ++i) {
					if (i != 0) {
						r += "|";
					}
					r += braceAsNecessary(get(i));
				}
				return r;
			}

			@Override
			public String toCanonicalString() {
				String r = "";
				for (int i = 0; i != size(); ++i) {
					if (i != 0) {
						r += "|";
					}
					r += "(" + get(i).toCanonicalString() + ")";
				}
				return r;
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.MANY, Data.ZERO, "TYPE_union") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Union(ArrayUtils.toArray(Type.class, operands));
				}
			};
		}

		/**
		 * Represents the set of all function values. These are pure functions,
		 * sometimes also called "mathematical" functions. A function cannot have any
		 * side-effects and must always return the same values given the same inputs. A
		 * function cannot have zero returns, since this would make it a no-operation.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Function extends AbstractType implements Type.Callable {
			public Function(Type parameters, Type returns) {
				super(TYPE_function, parameters, returns);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Type getParameter() {
				return (Type) get(0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Type getReturn() {
				return (Type) get(1);
			}

			@Override
			public Type.Function substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				Type parameterBefore = getParameter();
				Type parameterAfter = parameterBefore.substitute(binding);
				Type returnBefore = getReturn();
				Type returnAfter = returnBefore.substitute(binding);
				if(parameterAfter == null || returnAfter == null) {
					return null;
				} else if (parameterBefore == parameterAfter && returnBefore == returnAfter) {
					return this;
				} else {
					return new Function(parameterAfter, returnAfter);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public Function clone(SyntacticItem[] operands) {
				return new Function((Type.Tuple) operands[0], (Type.Tuple) operands[1]);
			}

			@Override
			public String toString() {
				return "function" + brace(getParameter()) + "->" + brace(getReturn());
			}

			@Override
			public String toCanonicalString() {
				Type ret = getReturn();
				return "function" + getParameter().toCanonicalString() + "->" + getReturn().toCanonicalString();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "TYPE_function") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Function((Type) operands[0], (Type) operands[1]);
				}
			};
		}

		/**
		 * Represents the set of all method values. These are impure and may have
		 * side-effects (e.g. performing I/O, updating non-local state, etc). A method
		 * may have zero returns and, in such case, the effect of a method comes through
		 * other side-effects. Methods may also have captured lifetime arguments, and
		 * may themselves declare lifetime arguments.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Method extends AbstractType implements Type.Callable {

			public Method(Type parameters, Type returns) {
				super(TYPE_method, new SyntacticItem[] { parameters, returns });
			}

			@Override
			@SuppressWarnings("unchecked")
			public Type getParameter() {
				return (Type) get(0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Type getReturn() {
				return (Type) get(1);
			}

			@Override
			public Type.Method substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				// Proceed with the potentially updated binding
				Type parametersBefore = getParameter();
				Type parametersAfter = parametersBefore.substitute(binding);
				Type returnBefore = getReturn();
				Type returnAfter = returnBefore.substitute(binding);
				if (parametersBefore == parametersAfter && returnBefore == returnAfter) {
					return this;
				} else {
					return new Method(parametersAfter, returnAfter);
				}
			}

			@Override
			public String toString() {
				return "method" + brace(getParameter()) + "->" + brace(getReturn());
			}

			@Override
			public String toCanonicalString() {
				return "method" + getParameter().toCanonicalString() + "->" + getReturn().toCanonicalString();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Method clone(SyntacticItem[] operands) {
				return new Method((Type.Tuple) operands[0], (Type.Tuple) operands[1]);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "TYPE_method") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Method((Type) operands[0], (Type) operands[1]);
				}
			};
		}

		/**
		 * Represents the set of all proeprty values. These are pure predicates,
		 * sometimes also called "mathematical" functions. A property cannot have any
		 * side-effects and always returns the boolean true.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Property extends AbstractType implements Type.Callable {
			public Property(Type parameter) {
				super(TYPE_property, parameter, Type.Bool);
			}

			public Property(Type parameter, Type ret) {
				super(TYPE_property, parameter, ret);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Type getParameter() {
				return (Type) get(0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Type getReturn() {
				return (Type) get(1);
			}

			@Override
			public Type.Property substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				Type parametersBefore = getParameter();
				Type parametersAfter = parametersBefore.substitute(binding);
				Type returnBefore = getReturn();
				Type returnAfter = returnBefore.substitute(binding);
				if (parametersBefore == parametersAfter && returnBefore == returnAfter) {
					return this;
				} else {
					return new Property(parametersAfter, returnAfter);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public Property clone(SyntacticItem[] operands) {
				return new Property((Type.Tuple) operands[0], (Type.Tuple) operands[1]);
			}

			@Override
			public String toString() {
				return "property" + brace(getParameter()) + "->" + brace(getReturn());
			}

			@Override
			public String toCanonicalString() {
				return "property" + getParameter().toCanonicalString() + "->" + getReturn().toCanonicalString();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "TYPE_property") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Property((Type.Tuple) operands[0], (Type.Tuple) operands[1]);
				}
			};

		}

		public static class Unknown extends AbstractType implements Callable {
			public Unknown() {
				super(TYPE_unknown);
			}

			@Override
			public Type.Tuple getParameter() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Type.Tuple getReturn() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Type.Unknown substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				throw new UnsupportedOperationException();
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Unknown();
			}

			@Override
			public String toString() {
				return "(???)->(???)";
			}

			@Override
			public String toCanonicalString() {
				throw new UnsupportedOperationException();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "TYPE_unknown") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Unknown();
				}
			};
		}

		public static class Existential extends AbstractType implements Atom {
			public Existential(int index) {
				super(TYPE_existential, BigInteger.valueOf(index).toByteArray());
				if(index < 0) {
					throw new IllegalArgumentException("invalid existential type index");
				}
			}

			@Override
			public boolean isWriteable() {
				// It never makes sense to ask this question of a type variable, since we cannot
				// possible known the answer.
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isReadable() {
				// It never makes sense to ask this question of a type variable, since we cannot
				// possible known the answer.
				throw new UnsupportedOperationException();
			}

			public int get() {
				return new BigInteger(data).intValue();
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Existential(new BigInteger(data).intValue());
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				SyntacticItem i = binding.apply(get());
				if(i instanceof Type) {
					return (Type) i;
				} else {
					return this;
				}
			}

			@Override
			public String toCanonicalString() {
				return toString();
			}

			@Override
			public String toString() {
				return "?" + Integer.toString(get());
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.MANY, "TYPE_existential") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Existential(new BigInteger(data).intValue());
				}
			};
		}

		public static class Universal extends AbstractType implements Atom {
			public Universal(Identifier name) {
				super(TYPE_universal, name);
			}

			@Override
			public boolean isWriteable() {
				// It never makes sense to ask this question of a type variable, since we cannot
				// possible known the answer.
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isReadable() {
				// It never makes sense to ask this question of a type variable, since we cannot
				// possible known the answer.
				throw new UnsupportedOperationException();
			}

			public Identifier getOperand() {
				return (Identifier) get(0);
			}


			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Universal((Identifier) operands[0]);
			}

			@Override
			public Type substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				SyntacticItem expanded = binding.apply(getOperand());
				if (expanded instanceof Type) {
					return (Type) expanded;
				} else {
					return this;
				}
			}

			@Override
			public String toCanonicalString() {
				return toString();
			}

			@Override
			public String toString() {
				return getOperand().toString();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "TYPE_universal") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Universal((Identifier) operands[0]);
				}
			};
		}

		/**
		 * Represents a recursive link. That is a backlink into the type itself.
		 *
		 * @return
		 */
		public static class Recursive extends AbstractType implements Type {

			public Recursive(Ref<Type> reference) {
				super(TYPE_recursive, reference);
			}

			@Override
			public boolean isWriteable() {
				return false;
			}

			@Override
			public boolean isReadable() {
				return false;
			}

			public Type getHead() {
				Ref<Type> r = (Ref<Type>) get(0);
				return r.get();
			}

			public void setHead(Ref<Type> ref) {
				operands[0] = ref;
			}

			@Override
			public Recursive substitute(java.util.function.Function<Object, SyntacticItem> binding) {
				return this;
			}

			@Override
			public Recursive clone(SyntacticItem[] operands) {
				return new Recursive((Ref<Type>) operands[0]);
			}

			@Override
			public String toString() {
				Type head = getHead();
				if (head instanceof Type.Atom || head instanceof Type.Nominal) {
					return "?" + head;
				} else if (head.getHeap() != null) {
					return "?" + head.getIndex();
				} else {
					return "?";
				}
			}

			@Override
			public String toCanonicalString() {
				throw new UnsupportedOperationException();
			}
			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "TYPE_recursive") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Recursive((Ref<Type>) operands[0]);
				}
			};
		}

		/**
		 * A type selector provides a form of refinement for a give type. More
		 * specifically, it determines which type tags are active for a given type.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Selector {
			/**
			 * Indicates that the given element is not selected.
			 */
			public static final Selector BOTTOM = new Selector() {
				@Override
				public String toString(Type t) {
					return "...";
				}
				@Override
				public String toString() {
					return "_";
				}
				@Override
				public Type apply(Type source) {
					return Type.Void;
				}
			};
			/**
			 * Indicates that the given element is selected entirely.
			 */
			public static final Selector TOP = new Selector() {
				@Override
				public String toString(Type t) {
					return t.toString();
				}
				@Override
				public Type apply(Type source) {
					return source;
				}
				@Override
				public String toString() {
					return "*";
				}
			};

			private final Selector[] children;

			public Selector(Selector... nodes) {
				this.children = nodes;
			}

			public int size() {
				return children.length;
			}

			public Selector get(int ith) {
				return children[ith];
			}

			@Override
			public String toString() {
				String r = "";
				for(int i=0;i!=children.length;++i) {
					if(i != 0) { r += ","; }
					r += children[i].toString();
				}
				return "(" + r + ")";
			}

			/**
			 * Generate a "partial" view of a given type according to this selector. For
			 * example, <code>null|{int f}</code> with selector <code>(_|*)</code> would
			 * produce the string <code>...|{int f}</code>.
			 *
			 * @param t
			 * @return
			 */
			public String toString(Type type) {
				switch (type.getOpcode()) {
				case TYPE_any:
				case TYPE_void:
				case TYPE_null:
				case TYPE_bool:
				case TYPE_byte:
				case TYPE_int:
				case TYPE_reference:
				case TYPE_method:
				case TYPE_function:
				case TYPE_property:
					return type.toString();
				case TYPE_array: {
					Type.Array t = (Type.Array) type;
					return children[0].toString(t.getElement()) + "[]";
				}
				case TYPE_nominal: {
					Type.Nominal t = (Type.Nominal) type;
					return toString(t.getConcreteType());
				}
				case TYPE_record: {
					Type.Record t = (Type.Record) type;
					WyilFile.Tuple<Type.Field> fields = t.getFields();
					String r = "";
					boolean ignored = false;
					for (int i = 0; i != fields.size(); ++i) {
						Type.Field f = fields.get(i);
						Selector s = children[i];
						if(!ignored || s != BOTTOM) {
							if(i != 0) { r = r + ", "; }
							if(s == BOTTOM) {
								r = r + s.toString(f.getType());
							} else {
								r = r + s.toString(f.getType()) + " " + f.getName();
							}
						}
						ignored = (s == BOTTOM);
					}
					return "{" + r + "}";
				}
				default: {
					Type.Union t = (Type.Union) type;
					String r = "";
					boolean ignored = false;
					for(int i=0;i!=t.size();++i) {
						Selector s = children[i];
						if(!ignored || s != BOTTOM) {
							if(i != 0) { r = r + "|"; }
							r += s.toString(t.get(i));
						}
						ignored = (s == BOTTOM);
					}
					return r;
				}
				}
			}

			/**
			 * Apply this selector to a given type, producing a potentially updated type.
			 * For example, applying <code>int|null</code> to the selector <code>_|*</code>
			 * yields <code>null</code>.
			 *
			 * @param source
			 * @return
			 */
			public Type apply(Type type) {
				// FIXME: need to support infinite selectors
				switch (type.getOpcode()) {
				case TYPE_any:
				case TYPE_void:
				case TYPE_null:
				case TYPE_bool:
				case TYPE_byte:
				case TYPE_int:
				case TYPE_reference:
				case TYPE_method:
				case TYPE_function:
				case TYPE_property:
					return type;
				case TYPE_array: {
					Type.Array t = (Type.Array) type;
					return new Type.Array(children[0].apply(t.getElement()));
				}
				case TYPE_nominal: {
					Type.Nominal t = (Type.Nominal) type;
					return apply(t.getConcreteType());
				}
				case TYPE_record: {
					Type.Record t = (Type.Record) type;
					WyilFile.Tuple<Type.Field> fields = t.getFields();
					Type.Field[] items = new Type.Field[fields.size()];
					for (int i = 0; i != fields.size(); ++i) {
						Type.Field f = fields.get(i);
						Type ft = children[i].apply(f.getType());
						items[i] = new Type.Field(f.getName(), ft);
					}
					return new Type.Record(t.isOpen(), new WyilFile.Tuple<>(items));
				}
				default:
					return apply((Type.Union) type);
				}
			}

			private Type apply(Type.Union type) {
				Type[] items = new Type[type.size()];
				for(int i=0;i!=type.size();++i) {
					items[i] = children[i].apply(type.get(i));
				}
				// Strip out unselected items
				items = ArrayUtils.removeAll(items, Type.Void);
				//
				switch(items.length) {
				case 0:
					return Type.Void;
				case 1:
					return items[0];
				default:
					return new Type.Union(items);
				}
			}
		}
	}

	private static Tuple<Type> substitute(Tuple<Type> types,
			java.util.function.Function<Object, SyntacticItem> binding) {
		for (int i = 0; i != types.size(); ++i) {
			Type before = types.get(i);
			Type after = before.substitute(binding);
			if (before != after) {
				// Committed to change
				Type[] nTypes = new Type[types.size()];
				for (int j = 0; j != nTypes.length; ++j) {
					after = types.get(j).substitute(binding);
					if(after == null) {
						return null;
					}
					nTypes[j] = after;
				}
				return new Tuple<>(nTypes);
			}
		}
		//
		return types;
	}

	private static Type[] substitute(Type[] types, java.util.function.Function<Object, SyntacticItem> binding) {
		Type[] nTypes = types;
		for (int i = 0; i != nTypes.length; ++i) {
			Type before = types[i];
			Type after = before.substitute(binding);
			if(after == null) {
				return null;
			} else if (before != after) {
				if (nTypes == types) {
					nTypes = Arrays.copyOf(types, types.length);
				}
				nTypes[i] = after;
			}
		}
		//
		return nTypes;
	}

	/**
	 * Apply an explicit binding to a given function, method or property declaration
	 * via substitution. Observe we cannot just use the existing Type.substitute
	 * method as this accounts for lifetime captures. Therefore, we first build the
	 * binding and then apply it to each of the parameters and returns.
	 *
	 * @param fmp
	 * @param templateArguments
	 * @return
	 */
	public static Type.Callable substituteTypeCallable(Type.Callable fmp, Tuple<Template.Variable> templateParameters,
			Tuple<WyilFile.Type> templateArguments) {
		Function<Object,SyntacticItem> binding = WyilFile.bindingFunction(templateParameters,templateArguments);
		// Proceed with the potentially updated binding
		Type parameters = fmp.getParameter().substitute(binding);
		Type ret = fmp.getReturn().substitute(binding);
		//
		if(fmp instanceof Type.Method) {
			return new Type.Method(parameters, ret);
		} else if(fmp instanceof Type.Function) {
			return new Type.Function(parameters, ret);
		} else {
			return new Type.Property(parameters, ret);
		}
	}

	private static String brace(Type t) {
		if(t.shape() < 2) {
			return "(" + t + ")";
		} else {
			return t.toString();
		}
	}

	public static String toString(Tuple<? extends SyntacticItem> items) {
		String r = "";
		for (int i = 0; i != items.size(); ++i) {
			SyntacticItem ith = items.get(i);
			if (i != 0) {
				r += ",";
			}
			r += ith == null ? "?" : ith;
		}
		return r;
	}

	// ============================================================
	// Modifiers
	// ============================================================

	/**
	 * <p>
	 * Represents an arbitrary modifier on a declaration. For example, all
	 * declarations (e.g. functions, types, etc) can be marked as
	 * <code>public</code> or <code>private</code>. The following illustrates:
	 * </p>
	 *
	 * <pre>
	 * public function square(int x, int y) -> int:
	 *    return x * y
	 * </pre>
	 * <p>
	 * The <code>public</code> modifier used above indicates that the function
	 * <code>square</code> can be accessed from outside its enclosing module.
	 * </p>
	 * <p>
	 * The modifiers <code>native</code> and <code>export</code> are used to enable
	 * inter-operation with other languages. By declaring a function or method as
	 * <code>native</code> you are signaling that its implementation is provided
	 * elsewhere (e.g. it's implemented in Java code directly). By marking a
	 * function or method with <code>export</code>, you are declaring that external
	 * code may call it. For example, you have some Java code that needs to call it.
	 * The modifier is required because, by default, all the names of all methods
	 * and functions are <i>mangled</i> to include type information and enable
	 * overloading. Therefore, a method/function marked with <code>export</code>
	 * will generate a function without name mangling.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Modifier extends SyntacticItem {

		public static final class Public extends AbstractSyntacticItem implements Modifier {
			public Public() {
				super(MOD_public);
			}

			@Override
			public String toString() {
				return "public";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Public();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "MOD_public") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Modifier.Public();
				}
			};
		}

		public static final class Private extends AbstractSyntacticItem implements Modifier {
			public Private() {
				super(MOD_private);
			}

			@Override
			public String toString() {
				return "private";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Private();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "MOD_private") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Modifier.Private();
				}
			};
		}

		public static final class Native extends AbstractSyntacticItem implements Modifier {
			public Native() {
				super(MOD_native);
			}

			@Override
			public String toString() {
				return "native";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Native();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "MOD_native") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Modifier.Native();
				}
			};
		}

		public static final class Export extends AbstractSyntacticItem implements Modifier {
			public Export() {
				super(MOD_export);
			}

			@Override
			public String toString() {
				return "export";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Export();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "MOD_export") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Modifier.Export();
				}
			};
		}

		public static final class Final extends AbstractSyntacticItem implements Modifier {
			public Final() {
				super(MOD_final);
			}

			@Override
			public String toString() {
				return "final";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Final();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ZERO, Data.ZERO, "MOD_final") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Modifier.Final();
				}
			};
		}
	}

	public interface Linkable {
		/**
		 * Get the link associated with this linkable expression.
		 * @return
		 */
		Decl.Link<? extends Decl.Named> getLink();
	}

	public interface Bindable extends Linkable, SyntacticItem {
		/**
		 * Get the binding associated with this bindable expression.
		 *
		 * @return
		 */
		Decl.Binding<? extends Type, ? extends Decl.Callable> getBinding();
	}

	/**
	 * Create a simple binding function from two tuples representing the key set and
	 * value set respectively.
	 *
	 * @param variables
	 * @param arguments
	 * @return
	 */
	public static <T extends SyntacticItem> java.util.function.Function<Object, SyntacticItem> bindingFunction(
			Tuple<Template.Variable> variables, Tuple<T> arguments) {
		//
		return (Object var) -> {
			int n = Math.min(variables.size(),arguments.size());
			for (int i = 0; i != n; ++i) {
				if (var.equals(variables.get(i).getName())) {
					return arguments.get(i);
				}
			}
			return null;
		};
	}

	/**
	 * Create a simple binding function from two tuples representing the key set and
	 * value set respectively.
	 *
	 * @param variables
	 * @param arguments
	 * @return
	 */
	public static java.util.function.Function<Object, SyntacticItem> bindingFunction(
			Tuple<Template.Variable> variables, Type.Tuple arguments) {
		//
		return (Object var) -> {
			int n = Math.min(variables.size(),arguments.size());
			for (int i = 0; i != n; ++i) {
				if (var.equals(variables.get(i).getName())) {
					return arguments.get(i);
				}
			}
			return null;
		};
	}

	/**
	 * Construct a binding function from another binding where a given set of
	 * variables are removed. This is necessary in situations where the given
	 * variables are captured.
	 *
	 * @param binding
	 * @param variables
	 * @return
	 */
	public static java.util.function.Function<Object, SyntacticItem> removeFromBinding(
			java.util.function.Function<Object, SyntacticItem> binding, Tuple<Identifier> variables) {
		return (Object var) -> {
			// Sanity check whether this is a variable which is being removed
			for (int i = 0; i != variables.size(); ++i) {
				if (var.equals(variables.get(i))) {
					return null;
				}
			}
			// Not being removed, reuse existing binding
			return binding.apply(var);
		};
	}

	// ============================================================
	// Attributes
	// ============================================================
	public interface Attr {
		public static class SyntaxError extends AbstractSyntacticItem implements SyntacticItem.Marker {

			public SyntaxError(int errcode, SyntacticItem target) {
				super(ATTR_error, BigInteger.valueOf(errcode).toByteArray(), target, new Tuple<>());
			}

			public SyntaxError(int errcode, SyntacticItem target, Tuple<SyntacticItem> context) {
				super(ATTR_error, BigInteger.valueOf(errcode).toByteArray(), target, context);
			}

			@Override
			public SyntacticItem getTarget() {
				return operands[0];
			}

			public Tuple<SyntacticItem> getContext() {
				return (Tuple<SyntacticItem>) operands[1];
			}

			/**
			 * Get the error code associated with this message
			 *
			 * @return
			 */
			public int getErrorCode() {
				return new BigInteger(getData()).intValue();
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new SyntaxError(getErrorCode(), operands[0], (Tuple<SyntacticItem>) operands[1]);
			}

			@Override
			public String getMessage() {
				// Done
				return ErrorMessages.getErrorMessage(getErrorCode(), getContext());
			}

			@Override
			public Path.ID getSource() {
				Decl.Unit unit = getTarget().getAncestor(Decl.Unit.class);
				// FIXME: this is realy a temporary hack
				String nameStr = unit.getName().toString().replace("::", "/");
				return Trie.fromString(nameStr);
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.MANY, Data.TWO, "ATTR_error") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					int errcode = new BigInteger(data).intValue();
					return new SyntaxError(errcode, operands[0], (Tuple<SyntacticItem>) operands[1]);
				}
			};
		}

		public static class StackFrame extends AbstractSyntacticItem {
			public StackFrame(Decl.Named<?> context, Tuple<Value> arguments) {
				super(ATTR_stackframe,context,arguments);
			}

			public Decl.Named<?> getContext() {
				return (Decl.Named) operands[0];
			}

			public Tuple<Value> getArguments() {
				return (Tuple<Value>) operands[1];
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new StackFrame((Decl.Callable) operands[0], (Tuple) operands[1]);
			}

			@Override
			public String toString() {
				return getContext().getQualifiedName().toString() + getArguments();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.TWO, Data.ZERO, "ATTR_stackframe") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new StackFrame((Decl.Named) operands[0], (Tuple<Value>) operands[1]);
				}
			};
		}

		public static class CounterExample extends AbstractSyntacticItem {
			public CounterExample(Value.Dictionary mapping) {
				super(ATTR_counterexample,mapping);
			}

			public Value.Dictionary getMapping() {
				return (Value.Dictionary) operands[0];
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new CounterExample((Value.Dictionary) operands[0]);
			}

			@Override
			public String toString() {
				return getMapping().toString();
			}

			public static final Descriptor DESCRIPTOR_0 = new Descriptor(Operands.ONE, Data.ZERO, "ATTR_counterexample") {
				@SuppressWarnings("unchecked")
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new CounterExample((Value.Dictionary) operands[0]);
				}
			};
		}
	}

	// Parsing
	public static final int EXPECTING_TOKEN = 300;	// "expecting \"" + kind + "\" here"
	public static final int EXPECTED_LIFETIME = 301;// "expecting lifetime identifier"
	public static final int UNEXPECTED_EOF = 302; // "unexpected end-of-file"
	public static final int UNEXPECTED_BLOCK_END = 303; // "unexpected end-of-block"
	public static final int UNKNOWN_LIFETIME = 304; // "use of undeclared lifetime"
	public static final int UNKNOWN_TYPE = 305; // "unknown type encountered"
	public static final int UNKNOWN_LVAL = 306; // "unexpected lval"
	public static final int UNKNOWN_TERM = 307; // "unrecognised term"
	public static final int INVALID_UNICODE_LITERAL = 308; // "invalid unicode string"
	public static final int INVALID_BINARY_LITERAL = 309; // "invalid binary literal"
	public static final int INVALID_HEX_LITERAL = 310; // "invalid hex literal (invalid characters)"
	public static final int DUPLICATE_VISIBILITY_MODIFIER = 311; // "visibility modifier already given"
	public static final int DUPLICATE_TEMPLATE_VARIABLE = 312; // "duplicate template variable"
	public static final int DUPLICATE_CASE_LABEL = 313; // "duplicate case label"
	public static final int DUPLICATE_DEFAULT_LABEL = 314; // "duplicate default label"
	public static final int DUPLICATE_FIELD = 315; // "duplicate record key"
	public static final int DUPLICATE_DECLARATION = 316; // "name already declared"
	public static final int MISSING_TYPE_VARIABLE = 317; // "missing type variable(s)"
	public static final int BREAK_OUTSIDE_SWITCH_OR_LOOP = 318; // "break outside switch or loop"
	public static final int CONTINUE_OUTSIDE_LOOP = 319; // "continue outside loop"
	// Types
	public static final int SUBTYPE_ERROR = 400;
	public static final int EMPTY_TYPE = 401;
	public static final int EXPECTED_ARRAY = 402;
	public static final int EXPECTED_RECORD = 403;
	public static final int EXPECTED_REFERENCE = 404;
	public static final int EXPECTED_LAMBDA = 405;
	public static final int INVALID_FIELD = 406;
	public static final int RESOLUTION_ERROR = 407;
	public static final int AMBIGUOUS_COERCION = 408;
	public static final int MISSING_TEMPLATE_PARAMETERS = 409;
	public static final int TOOMANY_TEMPLATE_PARAMETERS = 410;
	public static final int EXPOSING_HIDDEN_DECLARATION = 411;
	// Statements
	public static final int MISSING_RETURN_STATEMENT = 500;
	public static final int UNREACHABLE_CODE = 504;
	public static final int BRANCH_ALWAYS_TAKEN = 506;
	public static final int TOO_MANY_RETURNS = 507;
	public static final int INSUFFICIENT_RETURNS = 508;
	public static final int CYCLIC_STATIC_INITIALISER = 509;
	// Expressions
	public static final int VARIABLE_POSSIBLY_UNITIALISED = 601;
	public static final int INCOMPARABLE_OPERANDS = 602;
	public static final int INSUFFICIENT_ARGUMENTS = 603;
	public static final int AMBIGUOUS_CALLABLE = 604;
	public static final int PARAMETER_REASSIGNED = 605;
	public static final int FINAL_VARIABLE_REASSIGNED = 606;
	public static final int ALLOCATION_NOT_PERMITTED = 607;
	public static final int METHODCALL_NOT_PERMITTED = 608;
	public static final int REFERENCE_ACCESS_NOT_PERMITTED = 609;
	public static final int INVALID_LVAL_EXPRESSION = 610;
	public static final int DEREFERENCED_DYNAMICALLY_SIZED = 611;
	public static final int DEREFERENCED_UNKNOWN_TYPE = 612;
	// Runtime Failure Subset
	public static final int RUNTIME_PRECONDITION_FAILURE = 700;
	public static final int RUNTIME_POSTCONDITION_FAILURE = 701;
	public static final int RUNTIME_TYPEINVARIANT_FAILURE = 702;
	public static final int RUNTIME_LOOPINVARIANT_ESTABLISH_FAILURE = 703;
	public static final int RUNTIME_LOOPINVARIANT_RESTORED_FAILURE = 704;
	public static final int RUNTIME_ASSERTION_FAILURE = 705;
	public static final int RUNTIME_ASSUMPTION_FAILURE = 706;
	public static final int RUNTIME_BELOWBOUNDS_INDEX_FAILURE = 707;
	public static final int RUNTIME_ABOVEBOUNDS_INDEX_FAILURE = 708;
	public static final int RUNTIME_NEGATIVE_LENGTH_FAILURE = 709;
	public static final int RUNTIME_NEGATIVE_RANGE_FAILURE = 710;
	public static final int RUNTIME_DIVIDEBYZERO_FAILURE = 711;
	public static final int RUNTIME_FAULT = 712;
	// Verification Subset
	public static final int STATIC_PRECONDITION_FAILURE = 716;
	public static final int STATIC_POSTCONDITION_FAILURE = 717;
	public static final int STATIC_TYPEINVARIANT_FAILURE = 718;
	public static final int STATIC_ESTABLISH_LOOPINVARIANT_FAILURE = 719;
	public static final int STATIC_ENTER_LOOPINVARIANT_FAILURE = 720;
	public static final int STATIC_RESTORE_LOOPINVARIANT_FAILURE = 721;
	public static final int STATIC_ASSERTION_FAILURE = 722;
	public static final int STATIC_ASSUMPTION_FAILURE = 723;
	public static final int STATIC_BELOWBOUNDS_INDEX_FAILURE = 724;
	public static final int STATIC_ABOVEBOUNDS_INDEX_FAILURE = 725;
	public static final int STATIC_NEGATIVE_LENGTH_FAILURE = 726;
	public static final int STATIC_NEGATIVE_RANGE_FAILURE = 727;
	public static final int STATIC_DIVIDEBYZERO_FAILURE = 728;
	public static final int STATIC_FAULT = 729;

	// ==============================================================================
	//
	// ==============================================================================

	private static String canonicalBraceAsNecessary(Type type) {
		String str = type.toCanonicalString();
		if (needsBraces(type)) {
			return "(" + str + ")";
		} else {
			return str;
		}
	}

	private static String braceAsNecessary(Type type) {
		String str = type.toString();
		if (needsBraces(type)) {
			return "(" + str + ")";
		} else {
			return str;
		}
	}

	private static boolean needsBraces(Type type) {
		if (type instanceof Type.Atom || type instanceof Type.Nominal) {
			return false;
		} else {
			return true;
		}
	}

	private static class UsedVariableExtractor extends AbstractConsumer<HashSet<Decl.Variable>> {
		public UsedVariableExtractor(Build.Meter meter) {
			super(meter);
		}

		@Override
		public void visitVariableAccess(WyilFile.Expr.VariableAccess expr, HashSet<Decl.Variable> used) {
			used.add(expr.getVariableDeclaration());
		}

		@Override
		public void visitUniversalQuantifier(WyilFile.Expr.UniversalQuantifier expr, HashSet<Decl.Variable> used) {
			for(Decl.StaticVariable v : expr.getParameters()) {
				visitVariable(v,used);
			}
			visitExpression(expr.getOperand(), used);
			removeAllDeclared(expr.getParameters(), used);
		}

		@Override
		public void visitExistentialQuantifier(WyilFile.Expr.ExistentialQuantifier expr, HashSet<Decl.Variable> used) {
			for(Decl.StaticVariable v : expr.getParameters()) {
				visitVariable(v,used);
			}
			visitExpression(expr.getOperand(), used);
			removeAllDeclared(expr.getParameters(), used);
		}

		@Override
		public void visitType(WyilFile.Type type, HashSet<Decl.Variable> used) {
			// No need to visit types
		}

		private void removeAllDeclared(Tuple<? extends Decl.Variable> parameters, HashSet<Decl.Variable> used) {
			for (int i = 0; i != parameters.size(); ++i) {
				used.remove(parameters.get(i));
			}
		}
	};


	// =========================================================================
	// Schema Generator
	// =========================================================================

	/**
	 * This method constructs a chain of versioned schemas which should represent an
	 * immutable record of the various file formats used for representing WyilFiles.
	 *
	 * @return
	 */
	private static Schema createSchema() {
		return createSchema_2_0();
	}

	/**
	 * The first recorded schema for WyIL files. There were many many versions prior
	 * to this, but their differences are now lost in time.
	 *
	 * @return
	 */
	private static SectionedSchema createSchema_2_0() {
		SectionedSchema ROOT = new SectionedSchema(null, 2, 0, new Section[0]);
		SectionedSchema.Builder builder = ROOT.extend();
		// Register the necessary sections
		builder.register("ITEM", 16);
		builder.register("DECL", 32);
		builder.register("MOD", 8);
		builder.register("TEMPLATE", 8);
		builder.register("ATTR", 16);
		builder.register("TYPE", 64);
		builder.register("STMT", 32);
		builder.register("EXPR", 64);
		// Items from AbstractCompilationUnit
		builder.add("ITEM", "null", AbstractCompilationUnit.Value.Null.DESCRIPTOR_0);
		builder.add("ITEM", "bool", AbstractCompilationUnit.Value.Bool.DESCRIPTOR_0);
		builder.add("ITEM", "int", AbstractCompilationUnit.Value.Int.DESCRIPTOR_0);
		builder.add("ITEM", "utf8", AbstractCompilationUnit.Value.UTF8.DESCRIPTOR_0);
		builder.add("ITEM", "pair", AbstractCompilationUnit.Pair.DESCRIPTOR_0);
		builder.add("ITEM", "tuple", AbstractCompilationUnit.Tuple.DESCRIPTOR_0);
		builder.add("ITEM", "array", AbstractCompilationUnit.Value.Array.DESCRIPTOR_0);
		builder.add("ITEM", "ident", AbstractCompilationUnit.Identifier.DESCRIPTOR_0);
		builder.add("ITEM", "name", AbstractCompilationUnit.Name.DESCRIPTOR_0);
		builder.add("ITEM", "decimal", AbstractCompilationUnit.Value.Decimal.DESCRIPTOR_0);
		builder.add("ITEM", "ref", AbstractCompilationUnit.Ref.DESCRIPTOR_0);
		builder.add("ITEM", "dictionary", AbstractCompilationUnit.Value.Dictionary.DESCRIPTOR_0);
		builder.add("ITEM", null, null);
		builder.add("ITEM", null, null);
		builder.add("ITEM", "span", AbstractCompilationUnit.Attribute.Span.DESCRIPTOR_0);
		builder.add("ITEM", "byte", AbstractCompilationUnit.Value.Byte.DESCRIPTOR_0);
		// Declarations
		builder.add("DECL", "unknown", Decl.Unknown.DESCRIPTOR_0);
		builder.add("DECL", "module", Decl.Module.DESCRIPTOR_0);
		builder.add("DECL", "unit", Decl.Unit.DESCRIPTOR_0);
		builder.add("DECL", "import", Decl.Import.DESCRIPTOR_0a);
		builder.add("DECL", "importfrom", Decl.Import.DESCRIPTOR_0b);
		builder.add("DECL", "importwith", Decl.Import.DESCRIPTOR_0c);
		builder.add("DECL", "staticvar", Decl.StaticVariable.DESCRIPTOR_0);
		builder.add("DECL", "type", Decl.Type.DESCRIPTOR_0a);
		builder.add("DECL", "rectype", Decl.Type.DESCRIPTOR_0b);
		builder.add("DECL", "function", Decl.Function.DESCRIPTOR_0);
		builder.add("DECL", "method", Decl.Method.DESCRIPTOR_0);
		builder.add("DECL", "property", Decl.Property.DESCRIPTOR_0);
		builder.add("DECL", "lambda", Decl.Lambda.DESCRIPTOR_0);
		builder.add("DECL", "variable", Decl.Variable.DESCRIPTOR_0);
		builder.add("DECL", "link", Decl.Link.DESCRIPTOR_0);
		builder.add("DECL", "binding", Decl.Binding.DESCRIPTOR_0);
		// Modifiers
		builder.add("MOD", "native", Modifier.Native.DESCRIPTOR_0);
		builder.add("MOD", "export", Modifier.Export.DESCRIPTOR_0);
		builder.add("MOD", "final", Modifier.Final.DESCRIPTOR_0);
		builder.add("MOD", "protected", null);
		builder.add("MOD", "private", Modifier.Private.DESCRIPTOR_0);
		builder.add("MOD", "public", Modifier.Public.DESCRIPTOR_0);
		// Templates
		builder.add("TEMPLATE", "type", Template.Type.DESCRIPTOR_0);
		// Attributes
		builder.add("ATTR", "warning", null);
		builder.add("ATTR", "error", Attr.SyntaxError.DESCRIPTOR_0);
		builder.add("ATTR", "verificationcondition", null);
		builder.add("ATTR", null, null);
		builder.add("ATTR", "stackframe", Attr.StackFrame.DESCRIPTOR_0);
		builder.add("ATTR", "counterexample", Attr.CounterExample.DESCRIPTOR_0);
		// Types
		builder.add("TYPE", "unknown", Type.Unknown.DESCRIPTOR_0);
		builder.add("TYPE", "void", Type.Void.DESCRIPTOR_0);
		builder.add("TYPE", "any", Type.Any.DESCRIPTOR_0);
		builder.add("TYPE", "null", Type.Null.DESCRIPTOR_0);
		builder.add("TYPE", "bool", Type.Bool.DESCRIPTOR_0);
		builder.add("TYPE", "int", Type.Int.DESCRIPTOR_0);
		builder.add("TYPE", "nominal", Type.Nominal.DESCRIPTOR_0);
		builder.add("TYPE", "reference", Type.Reference.DESCRIPTOR_0);
		builder.add("TYPE", "array", Type.Array.DESCRIPTOR_0);
		builder.add("TYPE", "tuple", Type.Tuple.DESCRIPTOR_0);
		builder.add("TYPE", "record", Type.Record.DESCRIPTOR_0);
		builder.add("TYPE", "field", Type.Field.DESCRIPTOR_0);
		builder.add("TYPE", "function", Type.Function.DESCRIPTOR_0);
		builder.add("TYPE", "method", Type.Method.DESCRIPTOR_0);
		builder.add("TYPE", "property", Type.Property.DESCRIPTOR_0);
		builder.add("TYPE", "union", Type.Union.DESCRIPTOR_0);
		builder.add("TYPE", "byte", Type.Byte.DESCRIPTOR_0);
		builder.add("TYPE", null, null);
		builder.add("TYPE", null, null);
		builder.add("TYPE", null, null);
		builder.add("TYPE", null, null);
		builder.add("TYPE", null, null);
		builder.add("TYPE", null, null);
		builder.add("TYPE", null, null);
		builder.add("TYPE", null, null);
		builder.add("TYPE", "recursive", Type.Recursive.DESCRIPTOR_0);
		builder.add("TYPE", "universal", Type.Universal.DESCRIPTOR_0);
		builder.add("TYPE", "existential", Type.Existential.DESCRIPTOR_0);
		// Statements
		builder.add("STMT", "block", Stmt.Block.DESCRIPTOR_0);
		builder.add("STMT", "namedblock", Stmt.NamedBlock.DESCRIPTOR_0);
		builder.add("STMT", "caseblock", Stmt.Case.DESCRIPTOR_0);
		builder.add("STMT", "assert", Stmt.Assert.DESCRIPTOR_0);
		builder.add("STMT", "assign", Stmt.Assign.DESCRIPTOR_0);
		builder.add("STMT", "assume", Stmt.Assume.DESCRIPTOR_0);
		builder.add("STMT", "debug", Stmt.Debug.DESCRIPTOR_0);
		builder.add("STMT", "skip", Stmt.Skip.DESCRIPTOR_0);
		builder.add("STMT", "break", Stmt.Break.DESCRIPTOR_0);
		builder.add("STMT", "continue", Stmt.Continue.DESCRIPTOR_0);
		builder.add("STMT", "dowhile", Stmt.DoWhile.DESCRIPTOR_0);
		builder.add("STMT", "fail", Stmt.Fail.DESCRIPTOR_0);
		builder.add("STMT", "for", Stmt.For.DESCRIPTOR_0);
		builder.add("STMT", "foreach", null);
		builder.add("STMT", "if", Stmt.IfElse.DESCRIPTOR_0a);
		builder.add("STMT", "ifelse", Stmt.IfElse.DESCRIPTOR_0b);
		builder.add("STMT", "initialiser", Stmt.Initialiser.DESCRIPTOR_0a);
		builder.add("STMT", "initialiservoid", Stmt.Initialiser.DESCRIPTOR_0b);
		builder.add("STMT", "return", Stmt.Return.DESCRIPTOR_0a);
		builder.add("STMT", "returnvoid", Stmt.Return.DESCRIPTOR_0b);
		builder.add("STMT", "switch", Stmt.Switch.DESCRIPTOR_0);
		builder.add("STMT", "while", Stmt.While.DESCRIPTOR_0);
		// General Expressions
		builder.add("EXPR", "variablecopy", Expr.VariableAccess.DESCRIPTOR_0a);
		builder.add("EXPR", "variablemove", Expr.VariableAccess.DESCRIPTOR_0b);
		builder.add("EXPR", null, null);
		builder.add("EXPR", "staticvariable", Expr.StaticVariableAccess.DESCRIPTOR_0);
		//
		builder.add("EXPR", "constant", Expr.Constant.DESCRIPTOR_0);
		builder.add("EXPR", "cast", Expr.Cast.DESCRIPTOR_0);
		builder.add("EXPR", "invoke", Expr.Invoke.DESCRIPTOR_0);
		builder.add("EXPR", "indirectinvoke", Expr.IndirectInvoke.DESCRIPTOR_0);
		// Logical Expressions
		builder.add("EXPR", "logicalnot", Expr.LogicalNot.DESCRIPTOR_0);
		builder.add("EXPR", "logicaland", Expr.LogicalAnd.DESCRIPTOR_0);
		builder.add("EXPR", "logicalor", Expr.LogicalOr.DESCRIPTOR_0);
		builder.add("EXPR", "logicalimplication", Expr.LogicalImplication.DESCRIPTOR_0);
		//
		builder.add("EXPR", "logicaliff", Expr.LogicalIff.DESCRIPTOR_0);
		builder.add("EXPR", "logicalexistential", Expr.ExistentialQuantifier.DESCRIPTOR_0);
		builder.add("EXPR", "logicaluniversal", Expr.UniversalQuantifier.DESCRIPTOR_0);
		// Comparator Expressions
		builder.add("EXPR", "equal", Expr.Equal.DESCRIPTOR_0);
		builder.add("EXPR", "notequal", Expr.NotEqual.DESCRIPTOR_0);
		builder.add("EXPR", "integerlessthan", Expr.IntegerLessThan.DESCRIPTOR_0);
		builder.add("EXPR", "integerlessequal", Expr.IntegerLessThanOrEqual.DESCRIPTOR_0);

		builder.add("EXPR", "integergreaterthan", Expr.IntegerGreaterThan.DESCRIPTOR_0);
		builder.add("EXPR", "integergreaterequal", Expr.IntegerGreaterThanOrEqual.DESCRIPTOR_0);
		builder.add("EXPR", "is", Expr.Is.DESCRIPTOR_0);
		builder.add("EXPR", null, null);
		// Arithmetic Expressions
		builder.add("EXPR", "integernegation", Expr.IntegerNegation.DESCRIPTOR_0);
		builder.add("EXPR", "integeraddition", Expr.IntegerAddition.DESCRIPTOR_0);
		builder.add("EXPR", "integersubtraction", Expr.IntegerSubtraction.DESCRIPTOR_0);
		builder.add("EXPR", "integermultiplication", Expr.IntegerMultiplication.DESCRIPTOR_0);
		//
		builder.add("EXPR", "integerdivision", Expr.IntegerDivision.DESCRIPTOR_0);
		builder.add("EXPR", "integerremainder", Expr.IntegerRemainder.DESCRIPTOR_0);
		builder.add("EXPR", null, null);
		builder.add("EXPR", null, null);
		// Bitwise Expressions
		builder.add("EXPR", "bitwisenot", Expr.BitwiseComplement.DESCRIPTOR_0);
		builder.add("EXPR", "bitwiseand", Expr.BitwiseAnd.DESCRIPTOR_0);
		builder.add("EXPR", "bitwiseor", Expr.BitwiseOr.DESCRIPTOR_0);
		builder.add("EXPR", "bitwisexor", Expr.BitwiseXor.DESCRIPTOR_0);
		//
		builder.add("EXPR", "bitwiseshl", Expr.BitwiseShiftLeft.DESCRIPTOR_0);
		builder.add("EXPR", "bitwiseshr", Expr.BitwiseShiftRight.DESCRIPTOR_0);
		builder.add("EXPR", null, null);
		builder.add("EXPR", null, null);
		// Reference Expressions
		builder.add("EXPR", "dereference", Expr.Dereference.DESCRIPTOR_0);
		builder.add("EXPR", "new", Expr.New.DESCRIPTOR_0);
		builder.add("EXPR", "lambdaaccess", Expr.LambdaAccess.DESCRIPTOR_0);
		//
		builder.add("EXPR", "fielddereference", Expr.FieldDereference.DESCRIPTOR_0);
		builder.add("EXPR", null, null);
		builder.add("EXPR", null, null);
		builder.add("EXPR", null, null);
		// Record Expressions
		builder.add("EXPR", "recordaccess", Expr.RecordAccess.DESCRIPTOR_0a);
		builder.add("EXPR", "recordborrow", Expr.RecordAccess.DESCRIPTOR_0b);
		builder.add("EXPR", "recordupdate", Expr.RecordUpdate.DESCRIPTOR_0);
		builder.add("EXPR", "recordinitialiser", Expr.RecordInitialiser.DESCRIPTOR_0);
		// Tuple Expressions
		builder.add("EXPR", "tupleinitialiser", Expr.TupleInitialiser.DESCRIPTOR_0);
		builder.add("EXPR", null, null);
		builder.add("EXPR", null, null);
		builder.add("EXPR", null, null);
		// Array Expressions
		builder.add("EXPR", "arrayaccess", Expr.ArrayAccess.DESCRIPTOR_0a);
		builder.add("EXPR", "arrayborrow", Expr.ArrayAccess.DESCRIPTOR_0b);
		builder.add("EXPR", "arrayupdate", Expr.ArrayUpdate.DESCRIPTOR_0);
		builder.add("EXPR", "arraylength", Expr.ArrayLength.DESCRIPTOR_0);
		//
		builder.add("EXPR", "arraygenerator", Expr.ArrayGenerator.DESCRIPTOR_0);
		builder.add("EXPR", "arrayinitialiser", Expr.ArrayInitialiser.DESCRIPTOR_0);
		builder.add("EXPR", "arrayrange", Expr.ArrayRange.DESCRIPTOR_0);
		builder.add("EXPR", null, null);
		// Done
		SectionedSchema v0_1 = builder.done();
		return v0_1;
	}

	public static void main(String[] args) {
		Schema current = createSchema();
		for (int i = 0; i <= 255; ++i) {
			Descriptor desc = current.getDescriptor(i);
			if (desc != null) {
				System.out.println("public static final int " + desc.getMnemonic() + " = " + i + "; // " + desc);
			}
		}
		System.out.println("VERSION: " + current.getMajorVersion() + "." + current.getMinorVersion());
	}
}
