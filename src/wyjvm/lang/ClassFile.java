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

package wyjvm.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import wybs.lang.Content;
import wybs.lang.Path;
import wyil.util.Pair;
import wyjvm.attributes.*;
import wyjvm.io.*;

public class ClassFile {
	
	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<ClassFile> ContentType = new Content.Type<ClassFile>() {
		public Path.Entry<ClassFile> accept(Path.Entry<?> e) {
			if (e.contentType() == this) {
				return (Path.Entry<ClassFile>) e;
			}
			return null;
		}

		public ClassFile read(Path.Entry<ClassFile> e, InputStream input)
				throws IOException {
			ClassFileReader reader = new ClassFileReader(input);
			return reader.readClass();	
		}

		public void write(OutputStream output, ClassFile module)
				throws IOException {
			ClassFileWriter writer = new ClassFileWriter(output,null);
			writer.write(module);	
			output.close();
		}

		public String toString() {
			return "Content-Type: wyil";
		}
	};

	// =========================================================================
	// State
	// =========================================================================

	protected int version;
	protected JvmType.Clazz type;
	protected JvmType.Clazz superClazz;
	protected List<JvmType.Clazz> interfaces;	
	protected List<Modifier> modifiers;
	protected ArrayList<BytecodeAttribute> attributes;
	protected ArrayList<Field> fields;
	protected ArrayList<Method> methods;	

	// =========================================================================
	// Constructors
	// =========================================================================
	
	public ClassFile(int version, JvmType.Clazz type, JvmType.Clazz superClazz,
			List<JvmType.Clazz> interfaces, List<Modifier> modifiers, BytecodeAttribute... attributes) {
		this.version = version;
		this.type = type;
		this.superClazz = superClazz;
		this.interfaces = interfaces;		
		this.modifiers = modifiers;
		this.fields = new ArrayList<Field>();
		this.methods = new ArrayList<Method>();
		this.attributes = new ArrayList<BytecodeAttribute>();
		for(BytecodeAttribute a : attributes) {
			this.attributes.add(a);
		}		
	}
	
	public ClassFile(int version, JvmType.Clazz type, JvmType.Clazz superClazz,
			List<JvmType.Clazz> interfaces, List<Modifier> modifiers, Collection<BytecodeAttribute> attributes) {
		this.version = version;
		this.type = type;
		this.superClazz = superClazz;
		this.interfaces = interfaces;		
		this.modifiers = modifiers;
		this.fields = new ArrayList<Field>();
		this.methods = new ArrayList<Method>();
		this.attributes = new ArrayList<BytecodeAttribute>(attributes);
	}
	
	// =========================================================================
	// Accessors
	// =========================================================================
			
	public JvmType.Clazz type() {
		return type;
	}
		
	public String name() {
		return type.lastComponent().first();
	}	
	
	public JvmType.Clazz superClass() {
		return superClazz;
	}
	
	public List<JvmType.Clazz> interfaces() {
		return interfaces;
	}
	
	public List<JvmType.Clazz> inners() {
		// this needs to be fixed. Essentially, by looking for an InnerClasses
		// attribute and then digging stuff out of it.
		return new ArrayList<JvmType.Clazz>();
	}
	
	public List<BytecodeAttribute> attributes() {
		return attributes;
	}
	
	public BytecodeAttribute attribute(String name) {
		for(BytecodeAttribute a : attributes) {
			if(a.name().equals(name)) {
				return a;
			}
		}
		return null;
	}
	
	public List<Modifier> modifiers() {
		return modifiers;
	}
	
	public List<Field> fields() {
		return fields;
	}

	public Field field(String name) {
		for(Field f : fields) {
			if(f.name().equals(name)) {
				return f;
			}
		}
		return null;
	}
	
	public List<Method> methods() {
		return methods;
	}
	
	public List<Method> methods(String name) {
		ArrayList<Method> r = new ArrayList<Method>();
		for(Method m : methods) {
			if(m.name().equals(name)) {
				r.add(m);
			}
		}
		return r;
	}
	
	public int version() {
		return version;
	}
	
	/**
     * Check whether this method has one of the "base" modifiers (e.g. static,
     * public, private, etc). These are found in java.lang.reflect.Modifier.
     * 
     * @param modifier
     * @return true if it does!
     */
	public boolean hasModifier(Class modClass) {
		for(Modifier m : modifiers) {
			if(m.getClass().equals(modClass)) {
				return true;
			}			
		}
		return false;
	}
	
	/**
	 * Check whether this method is abstract
	 */
	public boolean isInterface() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Interface) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check whether this method is abstract
	 */
	public boolean isAbstract() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Abstract) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether this method is final
	 */
	public boolean isFinal() {
		for(Modifier m : modifiers) { if (m instanceof Modifier.Final) { return true; }}
		return false;
	}

	/**
	 * Check whether this method is static
	 */
	public boolean isStatic() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Static) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether this method is public
	 */
	public boolean isPublic() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Public) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether this method is protected
	 */
	public boolean isProtected() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Protected) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether this method is private
	 */
	public boolean isPrivate() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Private) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether this method is native
	 */
	public boolean isNative() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Native) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether this method is synchronized
	 */
	public boolean isSynchronized() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Synchronized) {
				return true;
			}
		}
		return false;
	}		
	
	/**
	 * Check whether this method is synthetic
	 */
	public boolean isSynthetic() {
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Synthetic) {
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * Check whether or not this is an inner class.
	 * @return
	 */
	public boolean isInnerClass() {
		return type.components().size() > 1;
	}

	// =========================================================================
	// Mutators
	// =========================================================================

	public void setType(JvmType.Clazz t) {
		type = t;
	}	
	
	// =========================================================================
	// Types
	// =========================================================================
		
	public static class Field {
		protected String name;
		protected JvmType type;
		protected List<Modifier> modifiers;
		protected ArrayList<BytecodeAttribute> attributes;
		
		public Field(String name, JvmType type, List<Modifier> modifiers) {
			this.name = name;
			this.type = type;
			this.modifiers = modifiers;
			this.attributes = new ArrayList<BytecodeAttribute>();
		}
		
		public String name() {
			return name;
		}

		public void setName(String n) {
			name = n;
		}
		
		public JvmType type() {
			return type;
		}

		public void setType(JvmType t) {
			type = t;
		}
		
		public List<Modifier> modifiers() {
			return modifiers;
		}
		
		public List<BytecodeAttribute> attributes() {
			return attributes;
		}
		
		public BytecodeAttribute attribute(String name) {
			for(BytecodeAttribute a : attributes) {
				if(a.name().equals(name)) {
					return a;
				}
			}
			return null;
		}
		
		/**
	     * Check whether this field has one of the "base" modifiers (e.g. static,
	     * public, private, etc). These are found in Modifier.ACC_
	     * 
	     * @param modifier
	     * @return true if it does!
	     */
		public boolean hasModifier(Class modClass) {
			for(Modifier m : modifiers) {
				if(m.getClass().equals(modClass)) {
					return true;
				}			
			}
			return false;
		}
		

		/**
		 * Check whether this field is abstract
		 */
		public boolean isAbstract() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Abstract) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether this field is final
		 */
		public boolean isFinal() {		
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Final) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether this field is static
		 */
		public boolean isStatic() {		
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Static) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether this field is public
		 */
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether this field is protected
		 */
		public boolean isProtected() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Protected) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether this field is private
		 */
		public boolean isPrivate() {		
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Private) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Check whether this method is synthetic
		 */
		public boolean isSynthetic() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Synthetic) {
					return true;
				}
			}
			return false;
		}	
		
		public boolean isConstant() {
			for(BytecodeAttribute a : attributes) {
				if(a instanceof ConstantValue) {
					return true;
				}
			}
			return false;
		}
		
		public Object constant() {
			for(BytecodeAttribute a : attributes) {
				if(a instanceof ConstantValue) {
					ConstantValue x = (ConstantValue) a;
					return x.constant();
				}
			}
			return null;
		}
	}
	
	public static class Method {
		protected String name;
		protected JvmType.Function type;
		protected List<Modifier> modifiers;
		protected ArrayList<BytecodeAttribute> attributes;		

		public Method(String name, JvmType.Function type,
				List<Modifier> modifiers) {
			this.name = name;
			this.type = type;
			this.modifiers = modifiers;			
			attributes = new ArrayList<BytecodeAttribute>();
		}

		public String name() {
			return name;
		}

		public JvmType.Function type() {
			return type;
		}

		public void setType(JvmType.Function t) {
			type = t;
		}
		
		public List<Parameter> parameters() {
			ArrayList<Parameter> r = new ArrayList<Parameter>();
			
			for(JvmType t : type.parameterTypes()) {
				r.add(new Parameter());
			}
			
			return r;
		}
		
		public List<Modifier> modifiers() {
			return modifiers;
		}		
		
		public List<JvmType.Clazz> exceptions() {
			for(BytecodeAttribute a : attributes) {
				if(a instanceof Exceptions) {
					return ((Exceptions)a).exceptions();
				}
			}
			return new ArrayList();
		}
		
		public <T extends BytecodeAttribute> T attribute(Class<T> c) {
			for(BytecodeAttribute a : attributes) {
				if(c.isInstance(a)) {
					return (T) a;
				}
			}
			return null;
		}

		public List<BytecodeAttribute> attributes() {
			return attributes;
		}
		
		public BytecodeAttribute attribute(String name) {
			for(BytecodeAttribute a : attributes) {
				if(a.name().equals(name)) {
					return a;
				}
			}
			return null;
		}
		
		/**
	     * Check whether this method has one of the "base" modifiers (e.g. static,
	     * public, private, etc). These are found in Modifier.ACC_
	     * 
	     * @param modifier
	     * @return true if it does!
	     */
		public boolean hasModifier(Class modClass) {
			for(Modifier m : modifiers) {
				if(m.getClass().equals(modClass)) {
					return true;
				}			
			}
			return false;
		}
		
		/**
		 * Check whether this method is abstract
		 */
		public boolean isAbstract() {
			for(Modifier m : modifiers) { 
				if(m instanceof Modifier.Abstract) {
					return true;
				}
			}
			return false;		
		}

		/**
		 * Check whether this method is final
		 */
		public boolean isFinal() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Final) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether this method is static
		 */
		public boolean isStatic() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Static) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether this method is public
		 */
		public boolean isPublic() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Public) {
					return true;
				}
			}		
			return false;
		}

		/**
		 * Check whether this method is protected
		 */
		public boolean isProtected() {
			for(Modifier m : modifiers) { if(m instanceof Modifier.Protected) { return true; }}
			return false;
		}

		/**
		 * Check whether this method is private
		 */
		public boolean isPrivate() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Private) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Check whether this method is native
		 */
		public boolean isNative() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Native) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether this method is synchronized
		 */
		public boolean isSynchronized() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Synchronized) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Check whether this method is synthetic
		 */
		public boolean isSynthetic() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Synthetic) {
					return true;
				}
			}
			return false;
		}	
		
		/**
		 * Check whether this method has varargs
		 */
		public boolean isVariableArity() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.VarArgs) {
					return true;
				}
			}
			return false;
		}		
	}
	
	public static class Parameter {

		public List<Modifier> modifiers() {
			return new ArrayList();
		}
		
		public boolean isSynthetic() {
			return false;
		}
	}
	
	/**
	 * This method builds a constant pool for this class file.
	 * 
	 * @return
	 */
	public ArrayList<Constant.Info> constantPool(ClassLoader loader) {
		HashSet<Constant.Info> constantPool = new HashSet<Constant.Info>();
		// Now, add constant pool items
		Constant.addPoolItem(Constant.buildClass(type),constantPool);
		Constant.addPoolItem(new Constant.Utf8("Signature"),constantPool);
		Constant.addPoolItem(new Constant.Utf8("ConstantValue"),constantPool);
		
		if (superClazz != null) {
			Constant.addPoolItem(Constant.buildClass(superClazz), constantPool);
		}

		for (JvmType.Reference i : interfaces) {
			Constant.addPoolItem(Constant.buildClass(i), constantPool);
		}			
		
		// Now, add all constant pool information for fields
		for (Field f : fields) {
			// Now, add pool items
			Constant.addPoolItem(new Constant.Utf8(f.name()), constantPool);
			Constant.addPoolItem(
					new Constant.Utf8(descriptor(f.type(), false)),
					constantPool);
			for(BytecodeAttribute a : f.attributes()) {
				a.addPoolItems(constantPool,loader);
			}
		}
		
		for(Method m : methods) {
			// Now, add all constant pool information for methods
			Constant.addPoolItem(new Constant.Utf8(m.name()), constantPool);						
			Constant.addPoolItem(new Constant.Utf8(descriptor(m.type(),
					false)), constantPool);

			for(BytecodeAttribute a : m.attributes()) {
				a.addPoolItems(constantPool,loader);
			}			
		}
		
		for(BytecodeAttribute a : attributes) {
			a.addPoolItems(constantPool,loader);
		}
		
		// Finally, we need to flatten the constant pool
		ArrayList<Constant.Info> pool = new ArrayList<Constant.Info>();
		pool.add(null); // first entry is not used
		for (Constant.Info ci : constantPool) {
			pool.add(ci);
			// Doubles and Longs require (for some reason) double slots.
			if(ci instanceof Constant.Double || ci instanceof Constant.Long) {
				pool.add(null);
			}
		}
		
		return pool;
	}
	
	/**
	 * This method returns a JVM descriptor string for the type in question. The
	 * format of the string is defined in "The JavaTM Virtual Machine
	 * Specification, 2nd ed", Section 4.3. Example descriptor strings include:
	 * 
	 * <table>
	 * <tr>
	 * <td><b>Type</b></td>
	 * <td><b>Descriptor</b></td>
	 * </tr>
	 * <tr>
	 * <td>int
	 * <tr>
	 * <td>I</td>
	 * <tr>
	 * <tr>
	 * <td>boolean
	 * <tr>
	 * <td>Z</td>
	 * <tr>
	 * <tr>
	 * <td>float[]
	 * <tr>
	 * <td>F[</td>
	 * <tr>
	 * <tr>
	 * <td>java.lang.Integer
	 * <tr>
	 * <td>Ljava/lang/Integer;</td>
	 * <tr>
	 * <tr>
	 * <td>int(Double,Float)
	 * <tr>
	 * <td>(DF)I</td>
	 * <tr> </table>
	 * <p>
	 * The descriptor string is used, amongst other things, to uniquely identify
	 * a class in the ClassTable.
	 * </p>
	 * 
	 * See the <a
	 * href="http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#1169">JVM
	 * Specification</a> for more information.
	 * 
	 * @param t
	 *            The type to generate the descriptor for
	 * @param generic
	 *            True indicates generic information should be included.
	 * @return
	 */
	public static String descriptor(JvmType t, boolean generic) {
		if(t instanceof JvmType.Bool) {
			return "Z";
		} if(t instanceof JvmType.Byte) {
			return "B";
		} else if(t instanceof JvmType.Char) {
			return "C";
		} else if(t instanceof JvmType.Short) {
			return "S";
		} else if(t instanceof JvmType.Int) {
			return "I";
		} else if(t instanceof JvmType.Long) {
			return "J";
		} else if(t instanceof JvmType.Float) {
			return "F";
		} else if(t instanceof JvmType.Double) {
			return "D";
		} else if(t instanceof JvmType.Void) {
			return "V";
		} else if(t instanceof JvmType.Array) {
			JvmType.Array at = (JvmType.Array) t;
			return "[" + descriptor(at.element(),generic);
		} else if(t instanceof JvmType.Clazz) {
			JvmType.Clazz ref = (JvmType.Clazz) t;
			String r = "L" + ref.pkg().replace(".","/");
			List<Pair<String, List<JvmType.Reference>>> classes = ref.components();
			for (int i = 0; i != classes.size(); ++i) {
				if (i == 0 && r.length() > 1) {
					r += "/";
				} else if(i > 0) {
					r += "$";
				}
				r += classes.get(i).first();
				if(generic) {
					List<JvmType.Reference> gparams = classes.get(i).second();
					if(gparams != null && gparams.size() > 0) {
						r += "<";
						for(JvmType gt : gparams) {
							r += descriptor(gt,generic);
						}
						r += ">";
					}
				}
			}
			return r + ";";
		} else if(t instanceof JvmType.Function) {
			// For simplicity, this code does not support generic function
            // types. The reason for this is that, to do so, requires access to
            // the ClassLoader. Instead, generic method signatures are supported
            // only by the MethodSignature class.
			
			JvmType.Function ft = (JvmType.Function) t;
			String r = "(";

			for (JvmType pt : ft.parameterTypes()) {				
				r += ClassFile.descriptor(pt,generic);
			}

			r = r + ")" + ClassFile.descriptor(ft.returnType(),generic);
			return r;			
		} if(t instanceof JvmType.Variable) {		
			if(generic) {
				JvmType.Variable tv = (JvmType.Variable) t;
				return "T" + tv.variable() + ";";
			} else {
				JvmType.Variable tv = (JvmType.Variable) t;
				JvmType.Reference lb = tv.lowerBound();
				if(lb != null) {
					return descriptor(lb,generic);
				} else {
					return "Ljava/lang/Object;";
				}
			}
		}  else if(t instanceof JvmType.Wildcard) {
			if(generic) {
				 JvmType.Wildcard tw = (JvmType.Wildcard) t;
				 if(tw.lowerBound() == null) {
					 return "*";
				 } else {
					 return "+" + descriptor(tw.lowerBound(),generic);
				 }
			} else {
				return "Ljava/lang/Object;";
			}
		} 
		 
		throw new RuntimeException("Invalid type passed to descriptor(): " + t);
	}
	
	/**
	 * Determine the slot size for the corresponding Java type.
	 * 
	 * @param type
	 *            The type to determine the slot size for.
	 * @return the slot size in slots.
	 */
	public static int slotSize(JvmType type) {
		if (type instanceof JvmType.Double || type instanceof JvmType.Long) {
			return 2;
		} else {
			return 1;
		}
	}
}
