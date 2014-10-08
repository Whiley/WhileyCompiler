// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import wycc.lang.Attribute;
import wycc.lang.CompilationUnit;
import wycc.lang.SyntacticElement;
import wycc.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyil.io.*;

/**
 * <p>
 * Provides an in-memory representation of a binary WyIL file. This is an
 * Intermediate Representation of Whiley (and potentially other) files, where
 * all aspects of name resolution and type checking are already resolved.
 * Furthermore, the Whiley Intermediate Language (WyIL) is a low-level,
 * register-based bytecode format where control-flow constructs are flattened
 * into unstructured control flow using conditional and unconditional branching.
 * </p>
 * <p>
 * The purpose of the WyIL file format is to simply the construction of
 * back-ends for the Whiley compiler, as well as simplifying the process of name
 * and type resolution in libraries. The format achieves a similar goal to that
 * Java ClassFile format, except that it is geared towards Whiley rather than
 * Java.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class WyilFile implements CompilationUnit {

	// =========================================================================
	// Content Type
	// =========================================================================
	
	/**
	 * Responsible for identifying and reading/writing WyilFiles. The normal
	 * extension is ".wyil" for WyilFiles.
	 */
	public static final Content.Type<WyilFile> ContentType = new Content.Type<WyilFile>() {
		public Path.Entry<WyilFile> accept(Path.Entry<?> e) {			
			if (e.contentType() == this) {
				return (Path.Entry<WyilFile>) e;
			} 			
			return null;
		}

		public WyilFile read(Path.Entry<WyilFile> e, InputStream input) throws IOException {			
			WyilFileReader reader = new WyilFileReader(input);
			WyilFile mi = reader.read();
			return mi;				
		}
		
		public void write(OutputStream output, WyilFile module) throws IOException {
			WyilFileWriter writer = new WyilFileWriter(output);
			writer.write(module);
		}
		
		public String toString() {
			return "Content-Type: wyil";
		}
	};	

	// =========================================================================
	// State
	// =========================================================================
		
	/**
	 * The fully qualified name of this WyilFile, including both package and
	 * module name.
	 */
	private final Path.ID mid;
	
	/**
	 * The originating source filename of this WyilFile.
	 */
	private final String filename;
	
	/**
	 * The list of blocks in this WyiFile.
	 */
	private final ArrayList<Block> blocks;
	
	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Construct a WyilFile objects with a given identifier, originating
	 * filename and list of declarations.
	 * 
	 * @param mid
	 * @param filename
	 * @param declarations
	 */
	public WyilFile(Path.ID mid,
			String filename,
			List<Block> declarations) {		
		this.mid = mid;
		this.filename = filename;		
		this.blocks = new ArrayList<Block>(declarations);
		
		// second, validate methods and/or functions
		HashSet<Pair<String,Type.FunctionOrMethod>> methods = new HashSet<Pair<String,Type.FunctionOrMethod>>();
		HashSet<String> types = new HashSet<String>();
		HashSet<String> constants = new HashSet<String>();
		
		for (Block d : declarations) {
			if(d instanceof FunctionOrMethodDeclaration) {
				FunctionOrMethodDeclaration m = (FunctionOrMethodDeclaration) d;
				Pair<String,Type.FunctionOrMethod> p = new Pair<String,Type.FunctionOrMethod>(m.name(),m.type());				
				if (methods.contains(p)) {
					throw new IllegalArgumentException(
							"Multiple function or method definitions (" + p.first() + ") with the same name and type not permitted");
				}
				methods.add(p);	
			} else if(d instanceof TypeDeclaration) {
				TypeDeclaration t = (TypeDeclaration) d;
				if (types.contains(t.name())) {
					throw new IllegalArgumentException(
							"Multiple type definitions with the same name not permitted");
				}
				types.add(t.name());
			} else if (d instanceof ConstantDeclaration) {
				ConstantDeclaration c = (ConstantDeclaration) d;				
				if (constants.contains(c.name())) {
					throw new IllegalArgumentException(
							"Multiple constant definitions with the same name not permitted");
				}
				constants.add(c.name());
			}
		}
	}
	
	// =========================================================================
	// Accessors
	// =========================================================================
	
	/**
	 * Returns the fully qualified name of this WyilFile, including both the
	 * package and module name.
	 * 
	 * @return
	 */
	public Path.ID id() {
		return mid;
	}
	
	/**
	 * Returns the originating source file for this WyilFile.
	 * 
	 * @return
	 */
	public String filename() {
		return filename;
	}
	
	/**
	 * Determines whether a declaration exists with the given name.
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasName(String name) {
		for (Block d : blocks) {
			if(d instanceof Declaration) {
				Declaration nd = (Declaration) d;
				if(nd.name().equals(name)) {
					return true;
				}
			} 
		}		
		return false;
	}
	
	/**
	 * Returns all declarations declared in this WyilFile. This list is
	 * modifiable, and one can add new declarations to this WyilFile by adding
	 * them to the returned list.
	 * 
	 * @return
	 */
	public List<WyilFile.Block> blocks() {
		return blocks;
	}
		
	/**
	 * Looks up a type declaration in this WyilFile with the given name; if none
	 * exists, returns null.
	 * 
	 * @param name
	 * @return
	 */
	public TypeDeclaration type(String name) {
		for (Block d : blocks) {
			if(d instanceof TypeDeclaration) {
				TypeDeclaration td = (TypeDeclaration) d;
				if(td.name().equals(name)) {
					return td;
				}					
			}
		}
		return null;		
	}
	
	/**
	 * Returns all type declarations in this WyilFile. Note that the returned
	 * list is not modifiable.
	 * 
	 * @param name
	 * @return
	 */
	public Collection<WyilFile.TypeDeclaration> types() {
		ArrayList<TypeDeclaration> r = new ArrayList<TypeDeclaration>();
		for (Block d : blocks) {
			if(d instanceof TypeDeclaration) {
				r.add((TypeDeclaration)d);
			}
		}
		return Collections.unmodifiableList(r);
	}
	
	/**
	 * Looks up a constant declaration in this WyilFile with the given name; if none
	 * exists, returns null.
	 * 
	 * @param name
	 * @return
	 */
	public ConstantDeclaration constant(String name) {
		for (Block d : blocks) {
			if(d instanceof ConstantDeclaration) {
				ConstantDeclaration cd = (ConstantDeclaration) d;
				if(cd.name().equals(name)) {
					return cd;
				}					
			}
		}
		return null;
	}
	
	/**
	 * Returns all constant declarations in this WyilFile. Note that the
	 * returned list is not modifiable.
	 * 
	 * @param name
	 * @return
	 */
	public Collection<WyilFile.ConstantDeclaration> constants() {
		ArrayList<ConstantDeclaration> r = new ArrayList<ConstantDeclaration>();
		for (Block d : blocks) {
			if(d instanceof ConstantDeclaration) {
				r.add((ConstantDeclaration)d);
			}
		}
		return Collections.unmodifiableList(r);
	}
	
	
	/**
	 * Returns all function or method declarations in this WyilFile with the
	 * given name. Note that the returned list is not modifiable.
	 * 
	 * @param name
	 * @return
	 */
	public List<FunctionOrMethodDeclaration> functionOrMethod(String name) {
		ArrayList<FunctionOrMethodDeclaration> r = new ArrayList<FunctionOrMethodDeclaration>();
		for (Block d : blocks) {
			if (d instanceof FunctionOrMethodDeclaration) {
				FunctionOrMethodDeclaration m = (FunctionOrMethodDeclaration) d;
				if (m.name().equals(name)) {
					r.add(m);
				}
			}
		}
		return Collections.unmodifiableList(r);
	}
	
	/**
	 * Looks up a function or method declaration in this WyilFile with the given
	 * name and type; if none exists, returns null.
	 * 
	 * @param name
	 * @return
	 */
	public FunctionOrMethodDeclaration functionOrMethod(String name, Type.FunctionOrMethod ft) {
		for (Block d : blocks) {
			if (d instanceof FunctionOrMethodDeclaration) {
				FunctionOrMethodDeclaration md = (FunctionOrMethodDeclaration) d;
				if (md.name().equals(name) && md.type().equals(ft)) {
					return md;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns all function or method declarations in this WyilFile. Note that
	 * the returned list is not modifiable.
	 * 
	 * @param name
	 * @return
	 */
	public Collection<WyilFile.FunctionOrMethodDeclaration> functionOrMethods() {
		ArrayList<FunctionOrMethodDeclaration> r = new ArrayList<FunctionOrMethodDeclaration>();
		for (Block d : blocks) {
			if(d instanceof FunctionOrMethodDeclaration) {
				r.add((FunctionOrMethodDeclaration)d);
			}
		}
		return Collections.unmodifiableList(r);
	}
	
	// =========================================================================
	// Mutators
	// =========================================================================
	
	public void replace(WyilFile.Block old, WyilFile.Block nuw) {
		for(int i=0;i!=blocks.size();++i) {
			if(blocks.get(i) == old) {
				blocks.set(i,nuw);
				return;
			}			
		}
	}
		
	// =========================================================================
	// Types
	// =========================================================================		
	
	/**
	 * A block is an chunk of information within a WyIL file. For example, it
	 * might be a declaration for a type, constant, function or method. However,
	 * other kinds of block are possible, such as for storing documentation,
	 * debug information, etc.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Block extends SyntacticElement.Impl {
		public Block(Attribute... attributes) {
			super(attributes);
		}
		public Block(Collection<Attribute> attributes) {
			super(attributes);
		}
	}
	
	/**
	 * A declaration is a named entity within a WyIL file, and is either a type,
	 * constant, function or method declaration.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Declaration extends Block {
		private String name;
		private List<Modifier> modifiers;	
		
		public Declaration(String name, Collection<Modifier> modifiers,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.modifiers = new ArrayList<Modifier>(modifiers);
		}
		
		public Declaration(String name, Collection<Modifier> modifiers,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.modifiers = new ArrayList<Modifier>(modifiers);
		}
		
		public String name() {
			return name;
		}
				
		public List<Modifier> modifiers() {
			return modifiers;
		}				

		public boolean hasModifier(Modifier modifier) {
			return modifiers.contains(modifier);
		}		
	}
	
	/**
	 * A type declaration is a top-level block within a WyilFile that associates
	 * a name with a given type. These names can be used within types,
	 * constants, functions and methods in other WyIL files. Every type has an
	 * optional invariant as well, which must hold true for all values of that
	 * type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class TypeDeclaration extends Declaration {		
		private Type type;		
		private Code.Block invariant;

		public TypeDeclaration(Collection<Modifier> modifiers, String name, Type type,
				Code.Block invariant, Attribute... attributes) {
			super(name,modifiers,attributes);			
			this.type = type;
			this.invariant = invariant;
		}

		public TypeDeclaration(Collection<Modifier> modifiers, String name,
				Type type, Code.Block invariant,
				Collection<Attribute> attributes) {
			super(name, modifiers, attributes);
			this.type = type;
			this.invariant = invariant;
		}

		public Type type() {
			return type;
		}
		
		public Code.Block invariant() {
			return invariant;
		}				
	}
	
	/**
	 * A constant declaration is a top-level block within a WyilFile that
	 * associates a name with a given constant value. These names can be used
	 * within expressions found in constants and functions and methods in other
	 * WyIL files. Note that constants may not have cyclic dependencies (i.e.
	 * they're value may not depend on itself).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class ConstantDeclaration extends Declaration {
		private Constant constant;

		public ConstantDeclaration(Collection<Modifier> modifiers, String name,
				Constant constant, Attribute... attributes) {
			super(name, modifiers, attributes);
			this.constant = constant;
		}

		public ConstantDeclaration(Collection<Modifier> modifiers, String name,
				Constant constant, Collection<Attribute> attributes) {
			super(name, modifiers, attributes);
			this.constant = constant;
		}

		public Constant constant() {
			return constant;
		}
	}
		
	public static final class FunctionOrMethodDeclaration extends
			Declaration {
		private Type.FunctionOrMethod type;
		private List<Case> cases;

		public FunctionOrMethodDeclaration(Collection<Modifier> modifiers,
				String name, Type.FunctionOrMethod type,
				Collection<Case> cases, Attribute... attributes) {
			super(name, modifiers, attributes);
			this.type = type;
			this.cases = Collections
					.unmodifiableList(new ArrayList<Case>(cases));
		}

		public FunctionOrMethodDeclaration(Collection<Modifier> modifiers,
				String name, Type.FunctionOrMethod type,
				Collection<Case> cases, Collection<Attribute> attributes) {
			super(name, modifiers, attributes);
			this.type = type;
			this.cases = Collections
					.unmodifiableList(new ArrayList<Case>(cases));
		}

		public Type.FunctionOrMethod type() {
			return type;
		}

		public List<Case> cases() {
			return cases;
		}

		public boolean isFunction() {
			return type instanceof Type.Function;
		}

		public boolean isMethod() {
			return type instanceof Type.Method;
		}
	}
	
	public static final class Case extends SyntacticElement.Impl {				
		private final ArrayList<Code.Block> precondition;
		private final ArrayList<Code.Block> postcondition;
		private final Code.Block body;
		//private final ArrayList<String> locals;		
		
		public Case(Code.Block body,
				List<Code.Block> precondition,
				List<Code.Block> postcondition, 
				Attribute... attributes) {
			super(attributes);
			this.body = body;
			this.precondition = new ArrayList<Code.Block>(precondition);
			this.postcondition = new ArrayList<Code.Block>(postcondition);
		}

		public Case(Code.Block body,
				List<Code.Block> precondition,
				List<Code.Block> postcondition,   
				Collection<Attribute> attributes) {
			super(attributes);			
			this.body = body;
			this.precondition = new ArrayList<Code.Block>(precondition);
			this.postcondition = new ArrayList<Code.Block>(postcondition);
		}
		
		public Code.Block body() {
			return body;
		}
		
		public List<Code.Block> precondition() {
			return precondition;
		}
		
		public List<Code.Block> postcondition() {
			return postcondition;
		}		
	}
}
