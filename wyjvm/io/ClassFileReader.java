// This file is part of the Wyjvm bytecode manipulation library.
//
// Wyjvm is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyjvm is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyjvm. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjvm.io;

import java.io.*;
import java.util.*;

import wyil.util.*;
import wyjvm.attributes.*;
import wyjvm.lang.*;

public final class ClassFileReader {	
	private final byte[] bytes;      // byte array of class
	private final int[] items;       // start indices of constant pool items	
	private final HashMap<Integer,Constant.Info> constantPool;		
	private final HashMap<String,BytecodeAttributeReader> attributeReaders;
	
	/**
	 * Construct reader for classfile. This method looks in all the places
	 * specified by the VM's CLASSPATH.
	 * 
	 * @param fileName
	 *            The filename of the java classfile. Use dot notation for
	 *            specifying packages.
	 */
	
	public ClassFileReader(String fileName,
			BytecodeAttributeReader... readers) throws IOException {
		this(readClass(fileName), readers);
	}
	
	/**
	 * Construct reader for classfile from InputStream
	 * 
	 * @param fileName
	 *            The filename of the java classfile. Use dot notation for
	 *            specifying packages.
	 */
	
	public ClassFileReader(InputStream in,
			BytecodeAttributeReader... readers) throws IOException {
		this(readStream(in), readers);		
	}
			
	/**
	 * Construct reader from byte array representing classfile.
	 * 
	 * @param b the byte array!
	 * @throws ClassFormatError if the classfile is invalid.
	 */
	public ClassFileReader(byte[] b, BytecodeAttributeReader... readers) {						
		bytes = b;			
		int nitems = read_u2(8);
		items = new int[nitems];			
		constantPool = new HashMap<Integer,Constant.Info>();
		this.attributeReaders = new HashMap<String,BytecodeAttributeReader>();
		for(BytecodeAttributeReader c : readers) {
			this.attributeReaders.put(c.name(),c);
		}
	}
				
	/**
     * Parse classfile and construct ClassInfo object. Currently, this does the
     * same thing as readSkeletons.
     * 
     * @throws ClassFormatError
     *             if the classfile is invalid.
     */
	public ClassFile readClass() {		
		if(read_u2(0) != 0xCAFE || read_u2(2) != 0xBABE) {
			throw new ClassFormatError("bad magic number");
		}
				
		int version = read_i4(4); 	
		// parse constant pool
		int index = 4+2+2+2;
		int nitems = read_u2(8);
		
		// process each item		
		for(int i=1;i!=nitems;++i) {			
			int type = read_u1(index);
			items[i] = index + 1;
			switch(type) {
			case CONSTANT_Class:				
			case CONSTANT_String:
				index += 3;
				break;
			case CONSTANT_FieldRef:
			case CONSTANT_MethodRef:
			case CONSTANT_InterfaceMethodRef:
			case CONSTANT_Integer:
			case CONSTANT_Float:
			case CONSTANT_NameAndType:
				index += 5;
				break;
			case CONSTANT_Long:
			case CONSTANT_Double:
				index += 9;
				++i; // longs and doubles are two entries
				break;
			case CONSTANT_Utf8:				
				// could simply turn into string here?
				int length = read_u2(index+1);				
				index += length + 2 + 1;
				break;
			}			
		}		
		
		// Now, fully prime the cache
		for (int i = 1; i < nitems; ++i) {			
			Constant.Info c = getConstant(i);
			if (c instanceof Constant.Double || c instanceof Constant.Long) {
				++i; // longs and doubles are two entries.
			}
		}
		
		int modifiers = read_u2(index);		
		String name = getString(read_u2(items[read_u2(index+2)]));	
		String className = name.substring(name.lastIndexOf('/')+1);
		String superClass = read_u2(index + 4) == 0 ? null
				: getString(read_u2(items[read_u2(index + 4)]));
		
		index += 6;		
		List<JvmType.Clazz> interfaces = parseInterfaces(index);		
		int count = read_u2(index);
		index += 2 + (count * 2);				
		
		ArrayList<ClassFile.Field> fields = parseFields(index);
				
		count = read_u2(index);		
		index += 2;
		
		for(int i=0;i!=count;++i) {			
	    	int acount = read_u2(index+6);
	    	index += 8;
	    	for(int j=0;j!=acount;++j) {	    		
	    		int len = read_i4(index+2);	    		 
	    		index += len + 6;
	    	}	    		    		    		    	
		}
		
		ArrayList<ClassFile.Method> methods = parseMethods(index,className);
		count = read_u2(index);	
		index += 2;
		
		for(int i=0;i!=count;++i) {			
			int acount = read_u2(index+6);
			index += 8;
			for(int j=0;j!=acount;++j) {	    		
				int len = read_i4(index+2);	    		 
				index += len + 6;
			}	    		    		    		    	
		}
	
		JvmType.Clazz type = parseClassDescriptor("L" + name + ";");
		JvmType.Clazz superType = superClass == null ? null
				: parseClassDescriptor("L" + superClass + ";");
		
		ArrayList<BytecodeAttribute> attributes = parseAttributes(index, CLASS_CONTEXT, type);		
		
		// now, try and figure out the full type of this class
			List<Modifier> lmodifiers = parseClassModifiers(modifiers);
		
		for(BytecodeAttribute a : attributes) {
			if(a instanceof InnerClasses) {
				InnerClasses ic = (InnerClasses) a;								
												
				for(Triple<JvmType.Clazz,JvmType.Clazz,List<Modifier>> p : ic.inners()) {					
					if(matchedInnerTypes(type,p.second()))  {
						List<Modifier> tmp = p.third();
						for(Modifier m : tmp) {
							if(!lmodifiers.contains(m)) {
								lmodifiers.add(m);
							}
						}													
					}					
				}				
			}
		} 					
		
		ClassFile cfile = new ClassFile(version, type, superType, interfaces, lmodifiers);
		
		cfile.attributes().addAll(attributes);
		cfile.methods().addAll(methods);
		cfile.fields().addAll(fields);
						
		return 	cfile;			 		
	}
	
    // ============================================================
	// PARSING HELPERS
	// ============================================================	
	
	
	/**
	 * Get array of interfaces implemented by this class.
	 * 
	 * @return
	 */
	protected ArrayList<JvmType.Clazz> parseInterfaces(int interfaces) {
		int count = read_u2(interfaces);
		int index = interfaces + 2;
		ArrayList<JvmType.Clazz> r = new ArrayList<JvmType.Clazz>();		
		for(int i=0;i!=count;++i,index+=2) {
			JvmType.Clazz t = parseClassDescriptor("L"+getString(read_u2(items[read_u2(index)])));
			r.add(t); 
		}
		return r;
	}
	
	
	/**
	 * parse array of fields defined in this class
	 * 
	 * @return
	 */
	protected ArrayList<ClassFile.Field> parseFields(int fields) {
		int count = read_u2(fields);
		ArrayList<ClassFile.Field> r = new ArrayList<ClassFile.Field>();
		int index = fields + 2;		
		for(int i=0;i!=count;++i) {
			r.add(parseField(index));						
			int acount = read_u2(index+6);	 
			index += 8;
			for(int j=0;j!=acount;++j) {	    		
				int alen = read_i4(index+2);	    		 
				index += alen + 6;		    	    		    		    		    
			}
		}
		return r;
	}
	
	protected ClassFile.Field parseField(int offset) {
		int modifiers = read_u2(offset);		
		String name = getString(read_u2(offset+2));
		String desc = getString(read_u2(offset+4));
		
		// parse attributes
		int acount = read_u2(offset+6);
		ArrayList<BytecodeAttribute> attributes = new ArrayList<BytecodeAttribute>();
		int index = offset + 8;
		for(int j=0;j!=acount;++j) {
			int len = read_i4(index+2);
			attributes.add(parseAttribute(index, FIELD_CONTEXT, null));
			index += len + 6;
		}
		
		JvmType type = parseDescriptor(desc);
		
		List<Modifier> mods = parseFieldModifiers(modifiers);						
		
		ClassFile.Field f = new ClassFile.Field(name, type,
				mods);
		
		f.attributes().addAll(attributes);
		
		return f;		
	}
	
	/**
	 * parse array of methods defined in this class
	 * 
	 * @return
	 */
	protected ArrayList<ClassFile.Method> parseMethods(int methods, String owner) {
		int count = read_u2(methods);		
		ArrayList<ClassFile.Method> r = new ArrayList<ClassFile.Method>();
		int index = methods + 2;
		for(int i=0;i!=count;++i) {			
			r.add(parseMethod(index,owner));						
		    int acount = read_u2(index+6);
		    index += 8;
		    for(int j=0;j!=acount;++j) {	    		
		    	int alen = read_i4(index+2);	    		 
		    	index += alen + 6;		    	    		    		    		    
			}
		}
		return r;
	}
	
	protected ClassFile.Method parseMethod(int offset, String owner) {
		String name = getString(read_u2(offset+2));
		String desc = getString(read_u2(offset+4));						
		
		if(name.equals("<init>")) {
			// Need to strip off any enclosing class names here.
			// Otherwise, we end up with a constructor named e.g.
			// Attributes$Name(...)
			name = owner.substring(owner.lastIndexOf('$')+1);
		}
		
		int modifiers = read_u2(offset);
		
		// parse attributes
		int acount = read_u2(offset+6);
		ArrayList<BytecodeAttribute> attributes = new ArrayList<BytecodeAttribute>();
		int index = offset + 8;
		for(int j=0;j!=acount;++j) {
			int len = read_i4(index+2);
			attributes.add(parseAttribute(index, METHOD_CONTEXT, null));
			index += len + 6;
		}
				
		JvmType.Function type = parseMethodDescriptor(desc);			
		
		List<Modifier> mods = parseMethodModifiers(modifiers); 
		       
		ClassFile.Method cm = new ClassFile.Method(name, type,
				mods);
		cm.attributes().addAll(attributes);
		return cm;
	}
	
	public static final int CLASS_CONTEXT = 0;
	public static final int METHOD_CONTEXT = 1;
	public static final int FIELD_CONTEXT = 2;
	
	/**
	 * parse any attributes associated with this field.
	 * @return
	 */
	protected ArrayList<BytecodeAttribute> parseAttributes(int attributes, int context, JvmType.Clazz type) {
		int acount = read_u2(attributes);
		ArrayList<BytecodeAttribute> r = new ArrayList<BytecodeAttribute>();
		int index = attributes + 2;
		for(int j=0;j!=acount;++j) {
			int len = read_i4(index+2);
			r.add(parseAttribute(index, context, type));
			index += len + 6;
		}
		return r;
	}
	
	protected BytecodeAttribute parseAttribute(int offset, int context, JvmType.Clazz type) {
		String name = getString(read_u2(offset));
		
		if(name.equals("Code")) {
			// for now, do nothing.
			// return parseCode(offset,name);
		} else if(name.equals("Exceptions")) {			
			return parseExceptions(offset,name);
		} else if(name.equals("InnerClasses")) {
			return parseInnerClasses(offset,name,type);
		} else if(name.equals("ConstantValue")) {
			return parseConstantValue(offset, name);
		} 
		
		int len = read_i4(offset+2) + 6;
		byte[] bs = new byte[len];
		for(int i=0;i!=len;++i) {
			bs[i] = bytes[offset+i];
		}
						
		BytecodeAttributeReader reader = attributeReaders.get(name);

		if(reader != null) {			
			try {
				return reader.read(new BinaryInputStream(
						new ByteArrayInputStream(bs)), constantPool);
			} catch(IOException ioex) {
				throw new RuntimeException(ioex.getMessage(),ioex);
			}
		} else {		
			// unknown attribute		
			return new BytecodeAttribute.Unknown(name,bs);
		}
	}
	
	protected Exceptions parseExceptions(int offset, String name) {
		ArrayList<JvmType.Clazz> exceptions = new ArrayList<JvmType.Clazz>();
		int numExceptions = read_u2(offset + 6);
		offset += 8;
		for(int i=0;i!=numExceptions;++i) {
			exceptions.add(parseClassDescriptor("L" + getClassName(read_u2(offset)) + ";"));
			offset += 2;
		}
		return new Exceptions(exceptions);
	}
		
	protected ConstantValue parseConstantValue(int offset, String name) {
		Object obj = getConstant(read_u2(offset+6));
		return new ConstantValue(obj);
	}	
	
	protected InnerClasses parseInnerClasses(int offset, String name, JvmType.Clazz type) {			
		offset += 6;
		int numClasses = read_u2(offset);
		offset += 2;
		ArrayList<Triple<JvmType.Clazz,JvmType.Clazz,List<Modifier>>> inners = new ArrayList();		
		
		for(int i=0;i!=numClasses;++i,offset=offset+8) {
			int inner_class_idx = read_u2(offset);
			int outer_class_idx = read_u2(offset+2);			
			int inner_class_access_flags = read_u2(offset+6);
			List<Modifier> mods = parseInnerClassModifiers(inner_class_access_flags);
			
			// Note, either the inner or outer indices can be zero. This
			// indicates an anonymous class.
			
			if(inner_class_idx == 0) {
				String outer_class_name = getClassName(outer_class_idx);
				JvmType.Clazz outerT = parseClassDescriptor("L" + outer_class_name + ";");				
				inners.add(new Triple(outerT,null,mods));			
			} else if(outer_class_idx == 0) {
				String inner_class_name = getClassName(inner_class_idx);
				JvmType.Clazz innerT = parseClassDescriptor("L" + inner_class_name + ";");				
				inners.add(new Triple(null,innerT,mods));
			} else {
				String outer_class_name = getClassName(outer_class_idx);
				String inner_class_name = getClassName(inner_class_idx); 				
				JvmType.Clazz innerT = parseClassDescriptor("L" + inner_class_name + ";");
				JvmType.Clazz outerT = parseClassDescriptor("L" + outer_class_name + ";");				
				inners.add(new Triple(outerT,innerT,mods));			
			}
		}
		
		return new InnerClasses(type,inners);
	}
	
	/**
	 * This method parses a general type descriptor.  
	 * 
	 * @param descriptor
	 * @return
	 */
	public static JvmType parseDescriptor(String descriptor) {
		return parseInternalDescriptor(descriptor,0).first();
	}
	
	/**
	 * This method parses a descriptor of the form "Lxxx.yyy.zzz$aaa$bbb;"
	 * @param descriptor
	 * @return
	 */	
	public static JvmType.Clazz parseClassDescriptor(String descriptor) {
		return parseInternalClassDescriptor(descriptor,0).first();
	}
	
	/**
	 * The class signature provides information about the generic type
	 * parameters declared for a class.
	 * 
	 * @return
	 */
	protected Triple<List<JvmType.Reference>, JvmType.Clazz, List<JvmType.Clazz>> parseClassSigDesc(
			String descriptor) {
		int pos = 0;
		ArrayList<JvmType.Reference> targs = new ArrayList<JvmType.Reference>();
		if (descriptor.charAt(pos) == '<') {
			pos = pos + 1; // skip '<'
			while (descriptor.charAt(pos) != '>') {
				Pair<JvmType.Variable, Integer> rt = parseFormalType(descriptor,
						pos);
				targs.add(rt.first());
				pos = rt.second();
			}
			pos = pos + 1; // skip '>'
		}
		Pair<JvmType.Clazz, Integer> state = parseInternalClassDescriptor(
				descriptor, pos);
		JvmType.Clazz superT = state.first();
		pos = state.second();
		ArrayList<JvmType.Clazz> interfaces = new ArrayList<JvmType.Clazz>();
		while (pos < descriptor.length()) {
			state = parseInternalClassDescriptor(descriptor, pos);
			interfaces.add(state.first());
			pos = state.second();
		}
		return new Triple<List<JvmType.Reference>, JvmType.Clazz, List<JvmType.Clazz>>(targs,
				superT, interfaces);
	}
	
	public static Pair<JvmType, Integer> parseInternalDescriptor(
			String descriptor, int pos) {		
		char c = descriptor.charAt(pos);		
		if(c == 'L') {
			Pair<JvmType.Clazz,Integer> p = parseInternalClassDescriptor(descriptor,pos);
			return new Pair<JvmType,Integer>(p.first(),p.second());
		} else if(c == '[') {
			int num = 0;			
			while(pos < descriptor.length() && descriptor.charAt(pos) == '[') { 
				++num; ++pos;
			}
			Pair<JvmType,Integer> tmp = parseInternalDescriptor(descriptor,pos);
			JvmType type = tmp.first();
			for(int i=0;i!=num;++i) {
				type = new JvmType.Array(type);
			}			
			return new Pair<JvmType,Integer>(type,tmp.second());
		} else if(c == 'T') {
			// this is a type variable
			int start = ++pos;
			while(descriptor.charAt(pos) != ';') { ++pos; }			
			JvmType type = new JvmType.Variable(descriptor.substring(start,pos), JvmTypes.JAVA_LANG_OBJECT);
			return new Pair<JvmType,Integer>(type,pos+1);
		} else if(c == '+') {			
			Pair<JvmType,Integer> r = parseInternalDescriptor(descriptor,pos+1);
			return new Pair(new JvmType.Wildcard((JvmType.Reference)r.first(),null),r.second());
		} else if(c == '-') {
			Pair<JvmType,Integer> r = parseInternalDescriptor(descriptor,pos+1);
			return new Pair(new JvmType.Wildcard(null,(JvmType.Reference)r.first()),r.second());			
		} else {
			// is primitive type ...
			switch(c) {
			case 'B':
				return new Pair<JvmType,Integer>(JvmTypes.T_BYTE,pos+1);						        	
			case 'C':
				return new Pair<JvmType,Integer>(JvmTypes.T_CHAR,pos+1);			        	
			case 'D':
				return new Pair<JvmType,Integer>(JvmTypes.T_DOUBLE,pos+1);			        	
			case 'F':				
				return new Pair<JvmType,Integer>(JvmTypes.T_FLOAT,pos+1);			        	
			case 'I':
				return new Pair<JvmType,Integer>(JvmTypes.T_INT,pos+1);		        	
			case 'J':
				return new Pair<JvmType,Integer>(JvmTypes.T_LONG,pos+1);			        	
			case 'S':
				return new Pair<JvmType,Integer>(JvmTypes.T_SHORT,pos+1);	
			case 'Z':
				return new Pair<JvmType,Integer>(JvmTypes.T_BOOL,pos+1);
			case 'V':
				return new Pair<JvmType,Integer>(JvmTypes.T_VOID,pos+1);
			case '*':
	            // FIXME: wildcard bounds.				
				return new Pair<JvmType,Integer>(new JvmType.Wildcard(null,null),pos+1); 
			default:
				throw new RuntimeException("Unknown type qualifier: " + c);
			}
		}
	}
		
	public static Pair<JvmType.Clazz, Integer> parseInternalClassDescriptor(
			String descriptor, int pos) {		
		assert descriptor.charAt(pos) == 'L';
		
		int start = ++pos;
		int last = pos;
		while(pos < descriptor.length() && descriptor.charAt(pos) != ';' && descriptor.charAt(pos) != '<') {
			if(descriptor.charAt(pos) == '/') { last = pos; }
			++pos; 
		}
		String pkg = descriptor.substring(start,last).replace('/','.');
		
		ArrayList<Pair<String, List<JvmType.Reference>>> classes = new ArrayList<Pair<String, List<JvmType.Reference>>>();
		// back track to make my life easier
		pos = last;		
		while (pos < descriptor.length() && descriptor.charAt(pos) != ';') {
			if (descriptor.charAt(pos) == '.' || descriptor.charAt(pos) == '/'
					|| descriptor.charAt(pos) == '$') {
				pos++;
			}
			last = pos;
			while (pos < descriptor.length() && descriptor.charAt(pos) != '$'
					&& descriptor.charAt(pos) != ';'
					&& descriptor.charAt(pos) != '<') {
				pos++;
			}
			String name = descriptor.substring(last, pos);
			ArrayList<JvmType.Reference> targs;
			if (pos < descriptor.length() && descriptor.charAt(pos) == '<') {				
				ArrayList<JvmType.Reference> ts = new ArrayList<JvmType.Reference>();				
				pos = pos + 1; // skip '<'
				while(descriptor.charAt(pos) != '>') {					
					Pair<JvmType,Integer> ti = parseInternalDescriptor(descriptor,pos); 
					ts.add((JvmType.Reference) ti.first());
					pos=ti.second();					
				}
				pos=pos+1; // skip '>'
				targs = ts;				
			} else {
				targs = new ArrayList<JvmType.Reference>();
			}
			classes.add(new Pair<String,List<JvmType.Reference>>(name, targs));
		}
		
		JvmType.Clazz r = new JvmType.Clazz(pkg,classes);		
		return new Pair<JvmType.Clazz, Integer>(r,pos+1);
	}
	
	public static Pair<JvmType.Variable, Integer> parseFormalType(String descriptor, int pos) {		
		int start = pos;	
		while(descriptor.charAt(pos) != ':') { pos++; }		
		String id = descriptor.substring(start,pos);
		pos = pos + 1; // skip ':'		
		ArrayList<JvmType.Reference> lowerBounds = new ArrayList<JvmType.Reference>();

		while(pos < descriptor.length() && descriptor.charAt(pos) == 'L') {
			Pair<JvmType.Clazz,Integer> rt = parseInternalClassDescriptor(descriptor,pos);
			lowerBounds.add(rt.first());
			pos = rt.second();
		}
		JvmType.Reference lb = null;
		if(lowerBounds.size() > 1) {			
			lb = new JvmType.Intersection(lowerBounds);
		} else if(lowerBounds.size() == 1) {
			lb = lowerBounds.get(0);
		} else {
			lb = JvmTypes.JAVA_LANG_OBJECT;
		}
		return new Pair<JvmType.Variable, Integer>(new JvmType.Variable(id,lb),pos);				
	}
	
	public static JvmType.Function parseMethodDescriptor(String descriptor) {		
		ArrayList<JvmType.Variable> targs = new ArrayList<JvmType.Variable>();
		int pos = 0;		
		
		// parse generic parameters (if there are any)
		if(descriptor.charAt(pos) == '<') { 
			pos = pos + 1; // skip '<'
			while(descriptor.charAt(pos) != '>') {
				Pair<JvmType.Variable,Integer> rt = parseFormalType(descriptor,pos);
				targs.add(rt.first());
				pos = rt.second();
			}
			pos = pos + 1; // skip '>'
		}		
		// now parse the methods parameters
		ArrayList<JvmType> params = new ArrayList<JvmType>();
		assert descriptor.charAt(pos) != '(';
		pos++;
		while (descriptor.charAt(pos) != ')') {
			Pair<JvmType, Integer> tmp = parseInternalDescriptor(descriptor, pos);
			params.add(tmp.first());
			pos = tmp.second();			
		}		
		// finally, parse the return type
		JvmType rtype = parseInternalDescriptor(descriptor, pos + 1).first();
		
		JvmType.Function rf = new JvmType.Function(rtype, params, targs);
				
		if(targs.size() > 0) {
			// First, build the binding.
			HashMap<String,JvmType.Reference> binding = new HashMap();
			for(JvmType.Variable v : targs) {
				binding.put(v.variable(), v);
			}
			
			rf = JvmTypes.substitute(rf, binding);
		}
		
		return rf;
	}
	
	/* ===========================================================
	 * BEGIN PARSE CODE
	 * ===========================================================
	 * 
	 * I want to put this method back into play sometime.  For now,
	 * it's not strictly needed, but there's no reason why it can't 
	 * be used to generate a list of Bytecode objects.

	protected Attribute.Code parseCode(int offset, String name) {
		int clen = read_i4(offset + 10);
		int index = offset + 14 + clen;
		
		// parse exception table
		int exceptionTableOffset = index;
		int len = read_u2(index); // length of exception table
		index += 2 + (len * 8); // ignore for now

		// now parse attributes table
		len = read_u2(index); // length of attributes table
		index += 2;
		int lineMapOffset = -1;
		
		for (int k = 0; k != len; ++k) {
			String s = getString(read_u2(index));
			int alen = read_i4(index + 2);
			index += 6;
			if (s.equals("LineNumberTable")) {
				lineMapOffset = index;
			}
			// skip attribute for now
			index += alen;
		}
		
		// parse instruction sequence, including line numbers
		// if available
		Vector<Instruction> instructions = new Vector<Instruction>();		
		int start=offset+14;
		int line = -1;
		int ltp = lineMapOffset + 2;
		int ltlen = lineMapOffset > 0 ? read_u2(lineMapOffset) : -1;				
		for(int pc=start;pc<start+clen;){			
			if(ltlen > 0) {		
				if(read_u2(ltp) <= (pc-start)) {
					line = read_u2(ltp+2);
					ltp = ltp + 4;
					ltlen--;
				}
			}
			Instruction i = parseInsn(pc,start,line);			
			instructions.add(i);
			pc += insnLength(pc,start);
		}
				
		// setup other variables
		int maxStack = read_u2(offset + 6);
		int maxLocals = read_u2(offset + 8);
		
		return new Attribute.Code(name,maxStack,maxLocals,instructions);
	}
	
	protected Instruction parseInsn(int offset, int start, int line) {				
		int opcode = read_u1(offset);
		int insn = opmap[opcode] & INSN_MASK;
		
		switch(insn) {				
			case NOP:
			case SWAP:
			case POP:
			case DUP:
			case DUPX1:
			case DUPX2:
			case MONITORENTER:
			case MONITOREXIT:
			case ARRAYLENGTH:
				return new Instruction(insn,-1,line);
			case ADD:
			case SUB:
			case DIV:
			case MUL:
			case REM:
			case NEG:
			case SHL:
			case SHR:
			case USHR:
			case AND:
			case OR:
			case XOR:
			case NEW:
			case CHECKCAST:
			case RETURN:
			case ARRAYLOAD:
			case ARRAYSTORE:
				return parseTypeInsn(offset,start,line);			
			case LOADCONST:			
				return parseDataInsn(offset,line);
			case IINC:
				return new Instruction(insn,read_u1(offset),read_u1(offset+1),
						TypeInfo.intType(),line);				
			case LOADVAR:
			case STOREVAR:
				return parseVarTypeInsn(offset,line);
			case RET:
			case IF:
			case IFCMP:
			case GOTO:
			case JSR:		
				return parseDstInsn(offset,start,line);
			// === INDIRECT INSTRUCTIONS ===
			case INVOKE:
			case FIELDLOAD:
			case FIELDSTORE:
				return parseOwnerNameTypeInsn(offset,line);	
		}
				
		throw new RuntimeException("Internal failure parsing bytecode instruction (" + OpcodeMap.get()[opcode]);
	}
	
	protected Instruction parseTypeInsn(int offset, int start, int line) {
		int opcode = read_u1(offset);
		int data = opmap[opcode];
		int insn = data & INSN_MASK;
		int fmt = data & FMT_MASK;		
		int type = data & TYPE_MASK;
		
		switch(type) {				
		case T_BYTE:
			return new Instruction(insn,TypeInfo.byteType(),line);
		case T_CHAR:
			return new Instruction(insn,TypeInfo.charType(),line);			
		case T_SHORT:
			return new Instruction(insn,TypeInfo.shortType(),line);			
		case T_INT:
			return new Instruction(insn,TypeInfo.intType(),line);			
		case T_LONG:
			return new Instruction(insn,TypeInfo.longType(),line);				
		case T_FLOAT:
			return new Instruction(insn,TypeInfo.floatType(),line);
		case T_DOUBLE:
			return new Instruction(insn,TypeInfo.doubleType(),line);
		case T_REF:
			if(opcode == 1) {
				// special case for FMT_INTNULL
				return new Instruction(insn,TypeInfo.nullType(),line);
			} else {
				return new Instruction(insn,TypeInfo.referenceType("java.lang","Object"),line);					
			}
		case T_ARRAY:
			return new Instruction(insn,TypeInfo.arrayType(1,TypeInfo.voidType()),line);			
		}
		
		TypeInfo rtype = null;
						
		switch(fmt) {
		case FMT_TYPEINDEX16_U8:
			int dims = read_u1(offset+3);
			String desc = getString(read_u2(read_u2(offset+1),0));
			rtype = TypeParser.parseFieldType(desc);
			break;
		case FMT_TYPEINDEX16:
			dims = read_u1(offset+3);
			// it's fair to say that I don't really see
			// why this is necessary.
			String tmp = getString(read_u2(read_u2(offset+1),0)); 
			if(tmp.charAt(0) == '[') {
				rtype = TypeParser.parseFieldType(tmp);
			} else {
				StringBuffer buf = new StringBuffer("L");
				buf.append(tmp);
				buf.append(";");
				rtype = TypeParser.parseFieldType(buf.toString());
			}
			break;
		case FMT_TYPEAINDEX16:
			dims = read_u1(offset+3);
			// it's fair to say that I don't really see
			// why this is necessary.
			tmp = getString(read_u2(read_u2(offset+1),0)); 			
			StringBuffer buf = new StringBuffer("[");
			if(tmp.charAt(0) != '[') {
				buf.append('L');
				buf.append(tmp);
				buf.append(";");				
			} else {
				buf.append(tmp);
			}
			rtype = TypeParser.parseFieldType(buf.toString());
			break;		
		case FMT_ATYPE:
			// must be NEWARRAY
			int atype = read_u1(offset+1);
			rtype = buildAtype(atype);	
			break;
		case FMT_EMPTY:
			// do nothing.  special case for RETURN
			break;
		default:
			throw new RuntimeException("no type information available");
		}
		
		return new Instruction(insn,rtype,line);
	}
	
	protected Instruction parseDstInsn(int offset, int start, int line) {
		int opcode = read_u1(offset);
		int insn = opmap[opcode] & INSN_MASK;
		int fmt = opmap[opcode] & FMT_MASK;
		
		int vardst;
		switch(fmt) {						
			case FMT_TARGET16:
				vardst = read_i2(offset+1) + offset - start;
				break;
			case FMT_TARGET32:
				vardst = read_i4(offset+1) + offset - start;
				break;
			default:
				throw new RuntimeException("Operation not supported for instruction!");
		}
		return new Instruction(insn,vardst,line);
	}
	
	protected Instruction parseVarTypeInsn(int offset, int line) {
		int opcode = read_u1(offset);
		int insn = opmap[opcode] & INSN_MASK;
		int fmt = opmap[opcode] & FMT_MASK;
		
		int vardst;
		switch(fmt) {
			case FMT_INT0:
				vardst = 0;
				break;
			case FMT_INT1:
				vardst = 1;
				break;
			case FMT_INT2:
				vardst = 2;
				break;
			case FMT_INT3:
				vardst = 3;	
				break;
			case FMT_VARIDX:
			case FMT_VARIDX_I8:
				vardst = read_u1(offset+1);	
				break;							
			default:
				throw new RuntimeException("Operation not supported for instruction!");
		}
		TypeInfo type;
		switch(opmap[opcode] & TYPE_MASK) {				
			case T_BYTE:
				type = TypeInfo.byteType();
				break;
			case T_CHAR:
				type = TypeInfo.charType();
				break;
			case T_SHORT:
				type = TypeInfo.shortType();
				break;
			case T_INT:
				type = TypeInfo.intType();
				break;
			case T_LONG:
				type = TypeInfo.longType();
				break;
			case T_FLOAT:
				type = TypeInfo.floatType();
				break;
			case T_DOUBLE:
				type = TypeInfo.doubleType();
				break;
			case T_REF:
				if(opcode == 1) {
					// special case for FMT_INTNULL
					type = TypeInfo.nullType();
				} else {
					type = TypeInfo.referenceType("java.lang","Object");						
				}
				break;
			case T_ARRAY:
				type = TypeInfo.arrayType(1,TypeInfo.voidType());
				break;
			default:
				throw new RuntimeException("Internal Failure");
		}
		return new Instruction(insn,vardst,type,line);
	}
	
	
	protected Instruction parseDataInsn(int offset, int line) {
		int opcode = read_u1(offset);
		int insn = opmap[opcode] & INSN_MASK;
		int fmt = opmap[opcode] & FMT_MASK;		
		Object data;
	
		switch(fmt) {
			case FMT_INTNULL:
				data = null;
				break;
			case FMT_INTM1:
				data = new Integer(-1);
				break;
			case FMT_INT0:			
			case FMT_INT1:			
			case FMT_INT2:			
			case FMT_INT3:
				int n = (fmt - FMT_INT0) >> FMT_SHIFT;					
				int rtype = opmap[opcode] & TYPE_MASK;
				switch(rtype) {
				case T_INT:
					data = new Integer(n);
					break;
				case T_LONG:
					data = new Long(n);
					break;
				case T_FLOAT:
					data = new Float(n);
					break;
				case T_DOUBLE:
					data = new Double(n);
					break;
				default:
					throw new RuntimeException("Unreachable code reached!");
				}			
				break;
			case FMT_INT4:
				data = new Integer(4);
				break;
			case FMT_INT5:
				data = new Integer(5);
				break;
			case FMT_I8:
				data = new Integer(read_u1(offset+1));
				break;
			case FMT_I16:
				data = new Integer(read_i2(offset+1));
				break;
			case FMT_CONSTINDEX8:
				data = getConstant(read_u1(offset+1));				
				break;
			case FMT_CONSTINDEX16:
				data = getConstant(read_u2(offset+1));				
				break;
			case FMT_VARIDX_I8:
				data = read_i1(offset+2);				
				break;
			default:
				throw new RuntimeException("Operation not supported for instruction!");
			}	
		
		if(data instanceof Integer) {
			return new Instruction(insn,data,TypeInfo.intType(),line);
		} else if(data instanceof Long) {
			return new Instruction(insn,data,TypeInfo.longType(),line);
		} else if(data instanceof Float) {
			return new Instruction(insn,data,TypeInfo.floatType(),line);
		} else if(data instanceof Double) {
			return new Instruction(insn,data,TypeInfo.doubleType(),line);
		} else if(data instanceof String) {			
			return new Instruction(insn,data,TypeInfo.referenceType("java.lang","String"),line);			
		} 
		throw new RuntimeException("Internal failure");
	}
	
	protected Instruction parseOwnerNameTypeInsn(int offset, int line) {
		TypeInfo owner = null;
		TypeInfo type;
		String name;
		int opcode = read_u1(offset);
		int insn = opmap[opcode] & INSN_MASK;
		int fmt = opmap[opcode] & FMT_MASK;
		
		switch(fmt) {
		case FMT_FIELDINDEX16:					
		case FMT_METHODINDEX16:
		case FMT_METHODINDEX16_U8_0:
			int index = read_u2(offset+1);
			owner = TypeParser.parseFieldType("L" + getString(read_u2(read_u2(index, 0), 0)) + ";");
			name = getString(read_u2(read_u2(index, 2), 0));	
			if(fmt == FMT_FIELDINDEX16) {
				type = TypeParser.parseFieldType(getString(read_u2(read_u2(index,2),2)));
			} else {
				type = TypeParser.parseFunctionType(getString(read_u2(read_u2(index, 2), 2)));
			}
			break;
		default:
			throw new RuntimeException("Operation not supported for instruction!");
		}	
				
		return new Instruction(insn,owner,name,type,line);
	}
	
	protected int insnLength(int offset, int codeOffsetP14) { 
		int fmt = opmap[read_u1(offset)] & FMT_MASK;		
		
		switch(fmt) {
		case FMT_INTM1:
		case FMT_INT0:
		case FMT_INT1:
		case FMT_INT2:
		case FMT_INT3:
		case FMT_INT4:
		case FMT_INT5:
		case FMT_INTNULL:
		case FMT_EMPTY:	 return 1;
		case FMT_I8: return 2;
		case FMT_I16: return 3;
		case FMT_CONSTINDEX8: return 2;
		case FMT_FIELDINDEX16: return 3;
		case FMT_METHODINDEX16:	 return 3;
		case FMT_TYPEINDEX16: return 3;
		case FMT_TYPEAINDEX16: return 3;
		case FMT_CONSTINDEX16: return 3;
		case FMT_TYPEINDEX16_U8: return 4;
		case FMT_METHODINDEX16_U8_0: return 5;
		case FMT_VARIDX: return 2;
		case FMT_VARIDX_I8: return 3;
		case FMT_ATYPE: return 2;		
		case FMT_TARGET16: return 3;
		case FMT_TARGET32: return 5;
		case FMT_TABLESWITCH:
		{
			int cpos = offset+1;  
			int pos = 1 + offset - codeOffsetP14; 
			while((pos % 4) != 0) { pos++;cpos++; }
			// first comes default word, then low, then high bytes
			int low = read_i4(cpos+4);
			int high = read_i4(cpos+8);
			cpos += 12;
			int count = high-low+1;
			cpos += (count * 4);
			return cpos - offset;
		}
		case FMT_LOOKUPSWITCH:
		{
			int cpos = offset+1;  
			int pos = 1 + offset - codeOffsetP14;  
			while((pos % 4) != 0) { pos++;cpos++; }
			// first comes default word, then low, then high bytes
			int count = read_i4(cpos+4);			
			cpos += 8;
			cpos += (count * 8);
			return cpos - offset;
		}
		default:
			throw new RuntimeException("Should not get here");
		}
	}		
	
	protected TypeInfo insnType(int offset) {
		int opcode = read_u1(offset);
		int data = opmap[opcode];
	
		int type = data & TYPE_MASK;
		switch(type) {				
			case T_BYTE:
				return TypeInfo.byteType();
			case T_CHAR:
				return TypeInfo.charType();		
			case T_SHORT:
				return TypeInfo.shortType();		
			case T_INT:
				return TypeInfo.intType();				
			case T_LONG:
				return TypeInfo.longType();		
			case T_FLOAT:
				return TypeInfo.floatType();		
			case T_DOUBLE:
				return TypeInfo.doubleType();		
			case T_REF:
				if(opcode == 1) {
					// special case for FMT_INTNULL
					return TypeInfo.nullType();
				} else {
					return TypeInfo.referenceType("java.lang","Object");						
				}
			case T_ARRAY:
				return TypeInfo.arrayType(1,TypeInfo.voidType());
		}

		int fmt = data & FMT_MASK;				
		switch(fmt) {
			case FMT_TYPEINDEX16_U8:
				int dims = read_u1(offset+3);
				String desc = getString(read_u2(read_u2(offset+1),0));
				return TypeParser.parseFieldType(desc);
			case FMT_TYPEINDEX16:
				dims = read_u1(offset+3);
				// it's fair to say that I don't really see
				// why this is necessary.
				String tmp = getString(read_u2(read_u2(offset+1),0)); 
				if(tmp.charAt(0) == '[') {
					return TypeParser.parseFieldType(tmp);
				} else {
					StringBuffer buf = new StringBuffer("L");
					buf.append(tmp);
					buf.append(";");
					return TypeParser.parseFieldType(buf.toString());
				}			
			case FMT_TYPEAINDEX16:
				dims = read_u1(offset+3);
				// it's fair to say that I don't really see
				// why this is necessary.
				tmp = getString(read_u2(read_u2(offset+1),0)); 			
				StringBuffer buf = new StringBuffer("[");
				if(tmp.charAt(0) != '[') {
					buf.append('L');
					buf.append(tmp);
					buf.append(";");				
				} else {
					buf.append(tmp);
				}
				return TypeParser.parseFieldType(buf.toString());
			case FMT_CONSTINDEX8:
				// for LDC and LDCW
				return getConstantType(read_u1(offset+1) & 0xFF);
			case FMT_CONSTINDEX16:
				// for LDC and LDCW
				return getConstantType(read_u2(offset+1));			
			case FMT_FIELDINDEX16:
				int index = read_u2(offset+1);
				return TypeParser.parseFieldType(getString(read_u2(read_u2(index, 2), 2)));
			case FMT_METHODINDEX16:
			case FMT_METHODINDEX16_U8_0:
				index = read_u2(offset+1);
				return TypeParser.parseFunctionType(getString(read_u2(read_u2(index, 2), 2)));
			case FMT_ATYPE:
				// must be NEWARRAY
				int atype = read_u1(offset+1);
				return buildAtype(atype);				
			default:
				return null;
		}
	}
	
	protected static final TypeInfo buildAtype(int atype) {
		TypeInfo elemType;
		switch (atype) {
		case VM_BOOLEAN:
			elemType = TypeInfo.booleanType();
			break;
		case VM_CHAR:
			elemType = TypeInfo.charType();				
			break;
		case VM_FLOAT:
			elemType = TypeInfo.floatType();				
		break;
		case VM_DOUBLE:
			elemType = TypeInfo.doubleType();				
			break;
		case VM_BYTE:
			elemType = TypeInfo.byteType();
			break;
		case VM_SHORT:
			elemType = TypeInfo.shortType();
			break;
		case VM_INT:
			elemType = TypeInfo.intType();
			break;
		case VM_LONG:
			elemType = TypeInfo.longType();
			break;
		default:
			throw new RuntimeException("unrecognised NEWARRAY code");
		}
		return TypeInfo.arrayType(1, elemType);
	}
		
	// This method computes the set of possible exception
	// handlers for a given position in the bytecode.	 
	public Pair<Integer,TypeInfo>[] exceptionHandlers(int offset, int exceptionTableOffset) {
		Vector<Pair<Integer,TypeInfo>> handlers = new Vector<Pair<Integer,TypeInfo>>();
		int len = read_u2(exceptionTableOffset); 
		int idx = exceptionTableOffset+2;
		for(int i=0;i!=len;++i,idx+=8) {
			int start = read_u2(idx);
			int end = read_u2(idx+2);
			int dest = read_u2(idx+4);
			int ct = read_u2(idx+6);
			TypeInfo type;
			if(ct > 0) {
				String desc = getClassName(ct);
				type = TypeParser.parseFieldType("L" + desc + ";");				
			} else {
				// Not sure what type to use here.  Maybe Throwable would
				// be better.
				type = TypeInfo.referenceType("java.lang","Exception");
			}
			 
			if(offset >= start && offset < end) {
				handlers.add(new Pair.Impl<Integer,TypeInfo>(dest,type));
			}
		}
		return handlers.toArray(new Pair[handlers.size()]);		
	}
	
	* ===========================================================
	* END PARSE CODE 
	* ===========================================================
	*/

	protected static final char BYTE = 'B';
	protected static final char CHAR = 'C';
	protected static final char DOUBLE = 'D';
	protected static final char FLOAT = 'F';
	protected static final char INT = 'I';
	protected static final char LONG = 'J';
	protected static final char SHORT = 'S';
	protected static final char BOOLEAN = 'Z';
	protected static final char STRING = 's';
	protected static final char ENUM = 'e';
	protected static final char CLASS = 'c';
	protected static final char ANNOTATION = '@';
	protected static final char ARRAY = '[';

	class Enumeration {
		protected final int type_name;
		protected final int const_name;

		public Enumeration(int tname, int cname) {
			type_name = tname;
			const_name = cname;
		}

		public String getTypeName() {
			return getString(type_name);
		}

		public String getConstName() {
			return getString(const_name);
		}
	};
	
	protected int annotationLength(int offset) {
		int length=0;
		int type = read_u2(offset);						
		int npairs = read_u2(offset+2);
		length += 4;
		for (int j = 0; j < npairs; j++) {
			switch (type) {
				case BOOLEAN:
				case BYTE:
				case SHORT:
				case CHAR:
				case INT:
				case FLOAT:
				case LONG:
				case DOUBLE:
					length = 3;
					break;
				case STRING:					
					length = 5;
					break;				
				case ENUM:
					length = 7;
					break;					
				case CLASS:					
					length = 5;
					break;															
				default:				
			}
		}
		return length;
	}	
		 
    // ============================================================
	// OTHER HELPER METHODS
	// ============================================================		
	
    // access flags for fields / methods / classes

	public static final int ACC_PUBLIC = 0x0001; 
	public static final int ACC_PRIVATE = 0x0002; 
	public static final int ACC_PROTECTED = 0x0004; 
	public static final int ACC_STATIC = 0x0008; 
	public static final int ACC_FINAL = 0x0010;         
	public static final int ACC_VOLATILE = 0x0040;
	public static final int ACC_TRANSIENT = 0x0080;
	public static final int ACC_SYNTHETIC = 0x1000;	
	public static final int ACC_BRIDGE = 0x0040;
	public static final int ACC_VARARGS = 0x0080;
	public static final int ACC_ANNOTATION = 0x2000;
	public static final int ACC_ENUM = 0x4000;
	    
    // access flags for methods / classes 
    
	public static final int ACC_SYNCHRONIZED = 0x0020;
	public static final int ACC_NATIVE = 0x0100;	
	public static final int ACC_ABSTRACT = 0x0400;
	public static final int ACC_STRICT = 0x0800; 
    
    // access flags for classes
    
    public static final int ACC_INTERFACE = 0x0200; 
    public static final int ACC_SUPER = 0x0020; 
	
	private final List<Modifier> parseClassModifiers(int modifiers) {
		ArrayList<Modifier> mods = new ArrayList<Modifier>();
		if((modifiers & ACC_PUBLIC) != 0) {
			mods.add(Modifier.ACC_PUBLIC);
		}
		if((modifiers & ACC_FINAL) != 0) {
			mods.add(Modifier.ACC_FINAL);
		}
		if((modifiers & ACC_SUPER) != 0) {
			mods.add(Modifier.ACC_SUPER);
		}
		if((modifiers & ACC_INTERFACE) != 0) {
			mods.add(Modifier.ACC_INTERFACE);
		}
		if((modifiers & ACC_ABSTRACT) != 0) {
			mods.add(Modifier.ACC_ABSTRACT);
		}
		if((modifiers & ACC_SYNTHETIC) != 0) {
			mods.add(Modifier.ACC_SYNTHETIC);
		}
		if((modifiers & ACC_ANNOTATION) != 0) {
			mods.add(Modifier.ACC_ANNOTATION);
		}
		if((modifiers & ACC_ENUM) != 0) {
			mods.add(Modifier.ACC_ENUM);
		}				
		
		return mods;
	}	

	private final List<Modifier> parseFieldModifiers(int modifiers) {
		ArrayList<Modifier> mods = new ArrayList<Modifier>();
		if((modifiers & ACC_PUBLIC) != 0) {
			mods.add(Modifier.ACC_PUBLIC);
		}
		if((modifiers & ACC_PRIVATE) != 0) {
			mods.add(Modifier.ACC_PRIVATE);
		}
		if((modifiers & ACC_PROTECTED) != 0) {
			mods.add(Modifier.ACC_PROTECTED);
		}
		if((modifiers & ACC_STATIC) != 0) {
			mods.add(Modifier.ACC_STATIC);
		}
		if((modifiers & ACC_FINAL) != 0) {
			mods.add(Modifier.ACC_FINAL);
		}
		if((modifiers & ACC_VOLATILE) != 0) {
			mods.add(Modifier.ACC_VOLATILE);
		}
		if((modifiers & ACC_TRANSIENT) != 0) {
			mods.add(Modifier.ACC_TRANSIENT);
		}
		if((modifiers & ACC_SYNTHETIC) != 0) {
			mods.add(Modifier.ACC_SYNTHETIC);
		}
		if((modifiers & ACC_ENUM) != 0) {
			mods.add(Modifier.ACC_ENUM);
		}
		return mods;
	}	

	private final List<Modifier> parseMethodModifiers(int modifiers) {			
		ArrayList<Modifier> mods = new ArrayList<Modifier>();
		if((modifiers & ACC_PUBLIC) != 0) {
			mods.add(Modifier.ACC_PUBLIC);
		}
		if((modifiers & ACC_PRIVATE) != 0) {
			mods.add(Modifier.ACC_PRIVATE);
		}
		if((modifiers & ACC_PROTECTED) != 0) {
			mods.add(Modifier.ACC_PROTECTED);
		}
		if((modifiers & ACC_STATIC) != 0) {
			mods.add(Modifier.ACC_STATIC);
		}
		if((modifiers & ACC_FINAL) != 0) {
			mods.add(Modifier.ACC_FINAL);
		}
		if((modifiers & ACC_VOLATILE) != 0) {
			mods.add(Modifier.ACC_VOLATILE);
		}
		if((modifiers & ACC_SYNCHRONIZED) != 0) {
			mods.add(Modifier.ACC_SYNCHRONIZED);
		}
		if((modifiers & ACC_BRIDGE) != 0) {
			mods.add(Modifier.ACC_BRIDGE);
		}
		if((modifiers & ACC_VARARGS) != 0) {
			mods.add(Modifier.ACC_VARARGS);
		}
		if((modifiers & ACC_NATIVE) != 0) {
			mods.add(Modifier.ACC_NATIVE);
		}
		if((modifiers & ACC_ABSTRACT) != 0) {
			mods.add(Modifier.ACC_ABSTRACT);
		}
		if((modifiers & ACC_STRICT) != 0) {
			mods.add(Modifier.ACC_STRICT);
		}
		if((modifiers & ACC_SYNTHETIC) != 0) {
			mods.add(Modifier.ACC_SYNTHETIC);
		}
		return mods;
	}	
	
	private static List<Modifier> parseInnerClassModifiers(int modifiers) {			
		ArrayList<Modifier> mods = new ArrayList<Modifier>();
		if((modifiers & ACC_PUBLIC) != 0) {
			mods.add(Modifier.ACC_PUBLIC);
		}
		if((modifiers & ACC_PRIVATE) != 0) {
			mods.add(Modifier.ACC_PRIVATE);
		}
		if((modifiers & ACC_PROTECTED) != 0) {
			mods.add(Modifier.ACC_PROTECTED);
		}
		if((modifiers & ACC_STATIC) != 0) {
			mods.add(Modifier.ACC_STATIC);
		}
		if((modifiers & ACC_FINAL) != 0) {
			mods.add(Modifier.ACC_FINAL);
		}
		if((modifiers & ACC_INTERFACE) != 0) {
			mods.add(Modifier.ACC_INTERFACE);
		}
		if((modifiers & ACC_ABSTRACT) != 0) {
			mods.add(Modifier.ACC_ABSTRACT);
		}
		if((modifiers & ACC_SYNTHETIC) != 0) {
			mods.add(Modifier.ACC_SYNTHETIC);
		}
		if((modifiers & ACC_ANNOTATION) != 0) {
			mods.add(Modifier.ACC_ANNOTATION);
		}
		if((modifiers & ACC_ENUM) != 0) {
			mods.add(Modifier.ACC_ENUM);
		}
		return mods;
	}
	
	/** 
	 * Read string from this classfile's constant pool.
	 * 
	 * @param index index into constant pool
	 */
	public final String getString(int index) {
		Constant.Utf8 cu = (Constant.Utf8) getConstant(index);
		return cu.str;
	}
	
	/**
	 * Read constant value from this classfile's constant pool.
	 * 
	 * @param index
	 * @return Object representing value (e.g. Integer for int etc). 
	 */
	public final Constant.Info getConstant(int index) {
		// index points to constant pool entry
		Constant.Info ci = constantPool.get(index);
		if(ci != null) { return ci; }
				
		int type = read_u1(items[index]-1);
		int p = items[index];
		switch (type) {
		case CONSTANT_NameAndType:
			Constant.Utf8 name = (Constant.Utf8) getConstant(read_u2(p));
			Constant.Utf8 t = (Constant.Utf8) getConstant(read_u2(p+2));
			ci = new Constant.NameType(name,t);
			break;
		case CONSTANT_FieldRef:
			Constant.Class owner = (Constant.Class) getConstant(read_u2(p));
			Constant.NameType nt = (Constant.NameType) getConstant(read_u2(p+2));
			ci = new Constant.FieldRef(owner,nt);
			break;
		case CONSTANT_MethodRef:
			owner = (Constant.Class) getConstant(read_u2(p));
			nt = (Constant.NameType) getConstant(read_u2(p+2));
			ci = new Constant.MethodRef(owner,nt);			
			break;
		case CONSTANT_InterfaceMethodRef:
			owner = (Constant.Class) getConstant(read_u2(p));
			nt = (Constant.NameType) getConstant(read_u2(p+2));
			ci = new Constant.InterfaceMethodRef(owner,nt);			
			break;
		case CONSTANT_Utf8:			
			int length = read_u2(p);
			try {
				ci = new Constant.Utf8(new String(bytes,p+2,length,"UTF-8"));
			} catch(UnsupportedEncodingException e) {
				throw new RuntimeException("UTF-8 Charset not supported?");
			}
			break;
		case CONSTANT_String:
			ci = new Constant.String((Constant.Utf8) getConstant(read_u2(p)));
			break;
		case CONSTANT_Double:
			ci = new Constant.Double(readDouble(p));
			break;
		case CONSTANT_Float:
			ci = new Constant.Float(readFloat(p));
			break;
		case CONSTANT_Integer:
			ci = new Constant.Integer(read_i4(p));
			break;
		case CONSTANT_Long:
			ci = new Constant.Long(read_i8(p));
			break;
			// in Java 1.5, LDC_W can read 
			// "class constants"
		case CONSTANT_Class:
			ci = new Constant.Class((Constant.Utf8) getConstant(read_u2(p)));
			break;
		default:
			throw new RuntimeException("unreachable code reached");
		}
		
		// cache the pool item
		constantPool.put(index,ci);		
		
		return ci;
	}
	
	/**
	 * Get the type of the constant value from this classfile's constant pool.
	 * 
	 * @param index
	 * @return TypeInfo object representing type of value  
	 */	
	public final JvmType getConstantType(int index) {
		// index points to constant pool entry		
		int type = read_u1(items[index]-1);
		//int p = items[index];
		switch (type) {
		case CONSTANT_String:
			if(getConstant(index) != null) {
				return JvmTypes.JAVA_LANG_STRING;
			} else {
				return JvmTypes.T_NULL;
			}
		case CONSTANT_Double:
			return JvmTypes.T_DOUBLE;						
		case CONSTANT_Float:
			return JvmTypes.T_FLOAT;						
		case CONSTANT_Integer:
			return JvmTypes.T_INT;			
		case CONSTANT_Long:
			return JvmTypes.T_LONG;			
		  // in Java 1.5, LDC_W can read 
		  // "class constants"
		case CONSTANT_Class:
			// FIXME: this is broken, since Class takes a generic param.
			return new JvmType.Clazz("java.lang","Class");
		default:
			throw new RuntimeException("unreachable code reached!");
		}				
	}
	
	private static final boolean matchedInnerTypes(JvmType.Clazz t1, JvmType.Clazz t2) {
		List<Pair<String, List<JvmType.Reference>>> t1comps = t1.components();
		List<Pair<String, List<JvmType.Reference>>> t2comps = t2.components();
		int t1comps_size = t1comps.size();

		if (!t1.pkg().equals(t2.pkg()) || t1comps_size != t2comps.size()) {
			return false;
		}

		for (int i = 0; i != t1comps_size; ++i) {
			String t1s = t1comps.get(i).first();
			String t2s = t2comps.get(i).first();
			if (!t1s.equals(t2s)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method is slightly ugly and it would be nice to get rid of it.
	 * It's used for getting the name of an exception.
	 * 
	 * @param index
	 * @return
	 */
	public final String getClassName(int index) {
		return getString(read_u2(items[index]));
	}
	
	
	final int read_i1(int index) { return bytes[index]; }	
	final int read_u1(int index) { return bytes[index] & 0xFF; }
	
	// I think this method should be renamed!
	final int read_u2(int index, int offset) {
		return read_u2(items[index]+offset);
	}
	
	final int read_u2(int index) {
		return ((bytes[index] & 0xFF) << 8) | (bytes[index + 1] & 0xFF);
	}	
		
	final short read_i2(int index) {
		return (short) (((bytes[index] & 0xFF) << 8) | (bytes[index + 1] & 0xFF));
	}
	
	final int read_i4(int index) {    
		return ((bytes[index] & 0xFF) << 24) | ((bytes[index + 1] & 0xFF) << 16)
	           | ((bytes[index + 2] & 0xFF) << 8) | (bytes[index + 3] & 0xFF);
	}			
		
	final long read_i8(int index) {
		long upper = read_i4(index);
		long lower = read_i4(index + 4) & 0xFFFFFFFFL;
		return (upper << 32) | lower;
	}
	
	final float readFloat(int index) {
		return Float.intBitsToFloat(read_i4(index));
	}
	
	final double readDouble(int index) {
		return Double.longBitsToDouble(read_i8(index)); 
	}
	
	final String readUTF8(int index) {
		// index points to constant pool entry
		// which is a CONSTANT_Utf8_info construct		
		int p = items[index];
		int length = read_u2(p);
		try {
			return new String(bytes,p+2,length,"UTF-8");
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 Charset not supported?");
		}				
	}
	
	protected static byte[] readClass(String s) throws IOException {
		InputStream input = java.lang.ClassLoader.getSystemResourceAsStream(
				s.replace('.', '/') + ".class");		
		if(input == null) {
			// Couldn't find class via class load.  Try load from current 
			// location instead.
			File file = new File(s.replace('.','/') + ".class");
			if(!file.exists()) {
				throw new RuntimeException("unable to read class " + s);
			}
			input = new FileInputStream(s.replace('.','/') + ".class");
		}
		
		return readStream(input);
	}

	protected static byte[] readStream(final InputStream is) throws IOException {
		// read in class
        byte[] b = new byte[is.available()];
        int length = 0;
        while(true) {
        	// read as much as possible in one chunk!
            int n = is.read(b, length, b.length - length);
            if(n == -1) {
            	// end of stream!
                if(length < b.length) {
                	// strip out unused bytes at end of array
                    byte[] c = new byte[length];
                    System.arraycopy(b, 0, c, 0, length);
                    b = c;
                }
                return b;
            }
            length += n;
            if(length == b.length) {
            	// deal with overflow!
                byte[] c = new byte[b.length + 1000];
                System.arraycopy(b, 0, c, 0, length);
                b = c;
            }
        }
    }

	/**
	 * This class represents a class constant. It is needed to distinguish between a
	 * class constant and a general String.
	 * 
	 * @author djp
	 * 
	 */
	protected class ClassConstant {
		private String name;
		
		public ClassConstant(String n) {
			name = n;
		}
		
		public String name() { return name; }
	}	
	
	// tags for constant pool entries	
	protected static final int CONSTANT_Class = 7;
	protected static final int CONSTANT_FieldRef = 9;
	protected static final int CONSTANT_MethodRef = 10;
	protected static final int CONSTANT_InterfaceMethodRef = 11;
	protected static final int CONSTANT_String = 8;
	protected static final int CONSTANT_Integer = 3;
	protected static final int CONSTANT_Float = 4;
	protected static final int CONSTANT_Long = 5;
	protected static final int CONSTANT_Double = 6;
	protected static final int CONSTANT_NameAndType = 12;
	protected static final int CONSTANT_Utf8 = 1;
	    
    // from the VM SPEC
	public static final byte VM_BOOLEAN = 4;
	public static final byte VM_CHAR = 5;
	public static final byte VM_FLOAT = 6;
	public static final byte VM_DOUBLE = 7;
	public static final byte VM_BYTE = 8;
	public static final byte VM_SHORT = 9;
	public static final byte VM_INT = 10;
	public static final byte VM_LONG = 11;

	//  RESULT TYPES.
	static final int TYPE_SHIFT = 6;
	static final int TYPE_MASK = 15 << TYPE_SHIFT;     
	static final int T_VOID = 0 << TYPE_SHIFT;     // no result type	
	static final int T_BYTE = 1 << TYPE_SHIFT;     
	static final int T_CHAR = 2 << TYPE_SHIFT;     
	static final int T_SHORT = 3 << TYPE_SHIFT;    
	static final int T_INT = 4 << TYPE_SHIFT;
	static final int T_LONG = 5 << TYPE_SHIFT;
	static final int T_FLOAT = 6 << TYPE_SHIFT;
	static final int T_DOUBLE = 7 << TYPE_SHIFT;
	static final int T_REF = 8 << TYPE_SHIFT;	
	static final int T_ARRAY = 9 << TYPE_SHIFT;	

	// SRC TYPES.  Used for the CONVERT instructions
	static final int SRCTYPE_SHIFT = 10;
	static final int SRCTYPE_MASK = 3 << SRCTYPE_SHIFT;     
	static final int S_INT = 0 << SRCTYPE_SHIFT;
	static final int S_LONG = 1 << SRCTYPE_SHIFT;
	static final int S_FLOAT = 2 << SRCTYPE_SHIFT;
	static final int S_DOUBLE = 3 << SRCTYPE_SHIFT;	
	
	// INSTRUCTION FORMATS.  These determine the different instruction formats.
	static final int FMT_SHIFT = 12;
	static final int FMT_MASK = 31 << FMT_SHIFT;
	static final int FMT_EMPTY = 0 << FMT_SHIFT;
	static final int FMT_I8 = 1 << FMT_SHIFT;
	static final int FMT_I16 = 2 << FMT_SHIFT;	
	static final int FMT_TYPEINDEX16 = 4 << FMT_SHIFT;      // INDEX into runtime pool for Type Descriptor
	static final int FMT_TYPEAINDEX16 = 5 << FMT_SHIFT;      // INDEX into runtime pool for Type Descriptor
	static final int FMT_TYPEINDEX16_U8 = 6 << FMT_SHIFT;   // INDEX into runtime pool for Type Descriptor
	static final int FMT_CONSTINDEX8 = 7 << FMT_SHIFT;      // INDEX into runtime pool for constant
	static final int FMT_CONSTINDEX16 = 8 << FMT_SHIFT;     // INDEX into runtime pool for constant
	static final int FMT_FIELDINDEX16 = 9 << FMT_SHIFT;     // INDEX into runtime pool for field
	static final int FMT_METHODINDEX16 = 10 << FMT_SHIFT;    // INDEX into runtime pool for method
	static final int FMT_METHODINDEX16_U8_0 = 11 << FMT_SHIFT; // INDEX into runtime pool
	static final int FMT_VARIDX = 12 << FMT_SHIFT;          // INDEX into local var array (1 byte)
	static final int FMT_VARIDX_I8 = 13 << FMT_SHIFT;
	static final int FMT_ATYPE = 14 << FMT_SHIFT;           // USED ONLY FOR NEWARRAY
	static final int FMT_TABLESWITCH = 15 << FMT_SHIFT;
	static final int FMT_LOOKUPSWITCH = 16 << FMT_SHIFT;
	static final int FMT_TARGET16 = 17 << FMT_SHIFT;
	static final int FMT_TARGET32 = 18 << FMT_SHIFT;
	static final int FMT_INTM1 = 19 << FMT_SHIFT;
	static final int FMT_INT0 = 20 << FMT_SHIFT;
	static final int FMT_INT1 = 21 << FMT_SHIFT;
	static final int FMT_INT2 = 22 << FMT_SHIFT;
	static final int FMT_INT3 = 23 << FMT_SHIFT;
	static final int FMT_INT4 = 24 << FMT_SHIFT;
	static final int FMT_INT5 = 25 << FMT_SHIFT;
	static final int FMT_INTNULL = 26 << FMT_SHIFT;
	
	static final int MOD_SHIFT = 17;
	static final int MOD_MASK = 3 << MOD_SHIFT;
	static final int MOD_VIRTUAL = 0 << MOD_SHIFT;
	static final int MOD_STATIC = 1 << MOD_SHIFT;
	static final int MOD_SPECIAL = 2 << MOD_SHIFT;
	static final int MOD_INTERFACE = 3 << MOD_SHIFT;
	
	public static final int INSN_MASK = 63;
	public static final int WIDE_INSN = 18; 
	
    // This table contains all the important info!
	// It was constructed by hand, which was a bit of a pain ...
	
	public static final int NOP = 0;
	public static final int LOADVAR = 1;
	public static final int STOREVAR = 2;	
	public static final int LOADCONST = 3;
	public static final int STORECONST = 4;
	public static final int ARRAYLOAD = 5;
	public static final int ARRAYSTORE = 6;
	public static final int ARRAYLENGTH = 7;
	public static final int IINC = 8;
	public static final int NEW = 9;
	public static final int THROW = 10;
	public static final int CHECKCAST = 11;
	public static final int INSTANCEOF = 12;
	public static final int MONITORENTER = 13;
	public static final int MONITOREXIT = 14;
	public static final int SWITCH = 15;
	public static final int CONVERT = 16;	
	
	// STACK INSTRUCTIONS
	public static final int POP = 19;
	public static final int DUP = 20;
	public static final int DUPX1 = 21;
	public static final int DUPX2 = 22;
	public static final int SWAP = 23;
	
	// ARITHMETIC INSTRUCTIONS
	public static final int ADD = 24;
	public static final int SUB = 25;
	public static final int MUL = 26;
	public static final int DIV = 27;
	public static final int REM = 28;
	public static final int NEG = 29;
	public static final int SHL = 30;
	public static final int SHR = 31;
	public static final int USHR = 32;
	public static final int AND = 33;
	public static final int OR = 34;
	public static final int XOR = 35;
	public static final int CMP = 36;
	public static final int CMPL = 37;
	public static final int CMPG = 38;
	
	// BRANCHING INSTRUCTIONS
	public static final int IF = 39;
	public static final int IFCMP = 40;
	public static final int GOTO = 41;
	public static final int JSR = 42;
	public static final int RET = 43;	
	public static final int RETURN = 44;
	
	// INDIRECT INSTRUCTIONS
	public static final int FIELDLOAD = 45;
	public static final int FIELDSTORE = 46;
	public static final int INVOKE = 47;

	public static final int EQUALS = 0;
	public static final int NOTEQUALS = 1;
	public static final int LESSTHAN = 2;		
	public static final int GREATERTHANEQUALS = 3;
	public static final int GREATERTHAN = 4;
	public static final int LESSTHANEQUALS = 5;
	public static final int NULL = 6;
	public static final int NONNULL = 7;
	
	public static final int[] opmap = new int[] { 
		NOP | FMT_EMPTY, // NOP = 0;

		LOADCONST | FMT_INTNULL | T_REF,          // ACONST_NULL = 1;
		LOADCONST | FMT_INTM1 | T_INT,            // ICONST_M1 = 2;
		LOADCONST | FMT_INT0  | T_INT,            // ICONST_0 = 3;
		LOADCONST | FMT_INT1  | T_INT,            // ICONST_1 = 4;
		LOADCONST | FMT_INT2  | T_INT,            // ICONST_2 = 5;
		LOADCONST | FMT_INT3  | T_INT,            // ICONST_3 = 6;
		LOADCONST | FMT_INT4  | T_INT,            // ICONST_4 = 7;
		LOADCONST | FMT_INT5  | T_INT,            // ICONST_5 = 8;
		LOADCONST | FMT_INT0  | T_LONG,           // LCONST_0 = 9;
		LOADCONST | FMT_INT1  | T_LONG,           // LCONST_1 = 10;
		LOADCONST | FMT_INT0  | T_FLOAT,          // FCONST_0 = 11;
		LOADCONST | FMT_INT1  | T_FLOAT,          // FCONST_1 = 12;
		LOADCONST | FMT_INT2  | T_FLOAT,          // FCONST_2 = 13;
		LOADCONST | FMT_INT0  | T_DOUBLE,         // DCONST_0 = 14;
		LOADCONST | FMT_INT1  | T_DOUBLE,         // DCONST_1 = 15;
		LOADCONST | FMT_I8 | T_INT,               // BIPUSH = 16;
		LOADCONST | FMT_I16 | T_INT,              // SIPUSH = 17;
		LOADCONST | FMT_CONSTINDEX8,              // LDC = 18
		LOADCONST | FMT_CONSTINDEX16,             // LDC_W = 19
		LOADCONST | FMT_CONSTINDEX16,             // LDC2_W = 20
		
		LOADVAR | FMT_VARIDX | T_INT,             // ILOAD = 21
		LOADVAR | FMT_VARIDX | T_LONG,            // LLOAD = 22
		LOADVAR | FMT_VARIDX | T_FLOAT,           // FLOAD = 23
		LOADVAR | FMT_VARIDX | T_DOUBLE,          // DLOAD = 24
		LOADVAR | FMT_VARIDX | T_REF,             // ALOAD = 25
		LOADVAR | FMT_INT0 | T_INT,               // ILOAD_0 = 26
		LOADVAR | FMT_INT1 | T_INT,               // ILOAD_1 = 27
		LOADVAR | FMT_INT2 | T_INT,               // ILOAD_2 = 28
		LOADVAR | FMT_INT3 | T_INT,               // ILOAD_3 = 29
		LOADVAR | FMT_INT0 | T_LONG,              // LLOAD_0 = 30
		LOADVAR | FMT_INT1 | T_LONG,              // LLOAD_1 = 31
		LOADVAR | FMT_INT2 | T_LONG,              // LLOAD_2 = 32
		LOADVAR | FMT_INT3 | T_LONG,              // LLOAD_3 = 33
		LOADVAR | FMT_INT0 | T_FLOAT,             // FLOAD_0 = 34
		LOADVAR | FMT_INT1 | T_FLOAT,             // FLOAD_1 = 35
		LOADVAR | FMT_INT2 | T_FLOAT,             // FLOAD_2 = 36
		LOADVAR | FMT_INT3 | T_FLOAT,             // FLOAD_3 = 37
		LOADVAR | FMT_INT0 | T_DOUBLE,            // DLOAD_0 = 38
		LOADVAR | FMT_INT1 | T_DOUBLE,            // DLOAD_1 = 39
		LOADVAR | FMT_INT2 | T_DOUBLE,            // DLOAD_2 = 40
		LOADVAR | FMT_INT3 | T_DOUBLE,            // DLOAD_3 = 41
		LOADVAR | FMT_INT0 | T_REF,               // ALOAD_0 = 42
		LOADVAR | FMT_INT1 | T_REF,               // ALOAD_1 = 43
		LOADVAR | FMT_INT2 | T_REF,               // ALOAD_2 = 44
		LOADVAR | FMT_INT3 | T_REF,               // ALOAD_3 = 45
		
		ARRAYLOAD | FMT_EMPTY | T_INT,           // IALOAD = 46
		ARRAYLOAD | FMT_EMPTY | T_LONG,          // LALOAD = 47
		ARRAYLOAD | FMT_EMPTY | T_FLOAT,      // FALOAD = 48
		ARRAYLOAD | FMT_EMPTY | T_DOUBLE,     // DALOAD = 49
		ARRAYLOAD | FMT_EMPTY | T_REF,        // AALOAD = 50
		ARRAYLOAD | FMT_EMPTY | T_BYTE,   // BALOAD = 51
		ARRAYLOAD | FMT_EMPTY | T_CHAR,       // AALOAD = 52
		ARRAYLOAD | FMT_EMPTY | T_SHORT,      // SALOAD = 53
		
		STOREVAR | FMT_VARIDX | T_INT,                    // ISTORE = 54;
		STOREVAR | FMT_VARIDX | T_LONG,                   // LSTORE = 55;
		STOREVAR | FMT_VARIDX | T_FLOAT,                  // FSTORE = 56;
		STOREVAR | FMT_VARIDX | T_DOUBLE,                 // DSTORE = 57;
		STOREVAR | FMT_VARIDX | T_REF,                    // ASTORE = 58;
		
		STOREVAR | FMT_INT0 | T_INT,                           // ISTORE_0 = 59;
		STOREVAR | FMT_INT1 | T_INT,                           // ISTORE_1 = 60;
		STOREVAR | FMT_INT2 | T_INT,                           // ISTORE_2 = 61;
		STOREVAR | FMT_INT3 | T_INT,                           // ISTORE_3 = 62;
		STOREVAR | FMT_INT0 | T_LONG,                          // LSTORE_0 = 63;
		STOREVAR | FMT_INT1 | T_LONG,                          // LSTORE_1 = 64;
		STOREVAR | FMT_INT2 | T_LONG,                          // LSTORE_2 = 65;
		STOREVAR | FMT_INT3 | T_LONG,                          // LSTORE_3 = 66;
		STOREVAR | FMT_INT0 | T_FLOAT,                         // FSTORE_0 = 67;
		STOREVAR | FMT_INT1 | T_FLOAT,                         // FSTORE_1 = 68;
		STOREVAR | FMT_INT2 | T_FLOAT,                         // FSTORE_2 = 69;
		STOREVAR | FMT_INT3 | T_FLOAT,                         // FSTORE_3 = 70;
		STOREVAR | FMT_INT0 | T_DOUBLE,                        // DSTORE_0 = 71;
		STOREVAR | FMT_INT1 | T_DOUBLE,                        // DSTORE_1 = 72;
		STOREVAR | FMT_INT2 | T_DOUBLE,                        // DSTORE_2 = 73;
		STOREVAR | FMT_INT3 | T_DOUBLE,                        // DSTORE_3 = 74;
		STOREVAR | FMT_INT0 | T_REF,                           // ASTORE_0 = 75;
		STOREVAR | FMT_INT1 | T_REF,                           // ASTORE_1 = 76;
		STOREVAR | FMT_INT2 | T_REF,                           // ASTORE_2 = 77;
		STOREVAR | FMT_INT3 | T_REF,                           // ASTORE_3 = 78;
		 
		ARRAYSTORE | FMT_EMPTY | T_INT,                // IASTORE = 79;
		ARRAYSTORE | FMT_EMPTY | T_LONG,               // LASTORE = 80;
		ARRAYSTORE | FMT_EMPTY | T_FLOAT,              // FASTORE = 81;
		ARRAYSTORE | FMT_EMPTY | T_DOUBLE,             // DASTORE = 82;
		ARRAYSTORE | FMT_EMPTY | T_REF,                // AASTORE = 83;
		ARRAYSTORE | FMT_EMPTY | T_BYTE,           // BASTORE = 84
		ARRAYSTORE | FMT_EMPTY | T_CHAR,                // CASTORE = 85;
		ARRAYSTORE | FMT_EMPTY | T_SHORT,              // SASTORE = 86;
				
		POP | FMT_EMPTY, // POP = 87;
		POP | FMT_EMPTY,  // POP2 = 88;
		
				DUP | FMT_EMPTY,                       // DUP = 89		
		// these ones are a real pain to deal with :(
		DUPX1 | FMT_EMPTY,                     // DUP_X1 = 90
		DUPX2 | FMT_EMPTY,                     // DUP_X2 = 91		
		DUP | FMT_EMPTY,                        // DUP2 = 92;
		// again, these ones are a real pain to deal with :(
		DUPX1 | FMT_EMPTY,                      // DUP2_X1 = 93
		DUPX2 | FMT_EMPTY,                      // DUP2_X2 = 94
		
		// PROBLEM HERE: how to specify no change to stack?
		SWAP | FMT_EMPTY,                          // SWAP = 95;
		
		ADD | FMT_EMPTY | T_INT,               // IADD = 96
		ADD | FMT_EMPTY | T_LONG,            // LADD = 97
		ADD | FMT_EMPTY | T_FLOAT,         // FADD = 98
		ADD | FMT_EMPTY | T_DOUBLE,      // DADD = 99
				
		SUB | FMT_EMPTY | T_INT,               // ISUB = 100
		SUB | FMT_EMPTY | T_LONG,            // LSUB = 101
		SUB | FMT_EMPTY | T_FLOAT,         // FSUB = 102
		SUB | FMT_EMPTY | T_DOUBLE,      // DSUB = 103
				
		MUL | FMT_EMPTY | T_INT,               // IMUL = 104
		MUL | FMT_EMPTY | T_LONG,            // LMUL = 105
		MUL | FMT_EMPTY | T_FLOAT,         // FMUL = 106
		MUL | FMT_EMPTY | T_DOUBLE,      // DMUL = 107
		
		DIV | FMT_EMPTY | T_INT,               // IDIV = 108
		DIV | FMT_EMPTY | T_LONG,            // LDIV = 109
		DIV | FMT_EMPTY | T_FLOAT,         // FDIV = 110
		DIV | FMT_EMPTY | T_DOUBLE,      // DDIV = 111
		
		REM | FMT_EMPTY | T_INT,               // IREM = 112
		REM | FMT_EMPTY | T_LONG,            // LREM = 113
		REM | FMT_EMPTY | T_FLOAT,         // FREM = 114
		REM | FMT_EMPTY | T_DOUBLE,      // DREM = 115
		
		NEG | FMT_EMPTY | T_INT,                        // INEG = 116
		NEG | FMT_EMPTY | T_LONG,                      // LNEG = 117
		NEG | FMT_EMPTY | T_FLOAT,                    // FNEG = 118
		NEG | FMT_EMPTY | T_DOUBLE,                  // DNEG = 119
		
		SHL | FMT_EMPTY | T_INT,                        // ISHL = 120
		SHL | FMT_EMPTY | T_LONG,                      // LSHL = 121
				
		SHR | FMT_EMPTY | T_INT,                        // ISHR = 122
		SHR | FMT_EMPTY | T_LONG,                      // LSHR = 123
		
		USHR | FMT_EMPTY | T_INT,                       // IUSHR = 124
		USHR | FMT_EMPTY | T_LONG,                     // LUSHR = 125
		
		AND | FMT_EMPTY | T_INT,                        // IAND = 126
		AND | FMT_EMPTY | T_LONG,                      // LAND = 127
		
		OR | FMT_EMPTY | T_INT,                         // IXOR = 128
		OR | FMT_EMPTY | T_LONG,                       // LXOR = 129
		
		XOR | FMT_EMPTY | T_INT,                        // IXOR = 130
		XOR | FMT_EMPTY | T_LONG,                      // LXOR = 131
				
		IINC | FMT_VARIDX_I8,                                   // IINC = 132
		
		CONVERT | FMT_EMPTY | S_INT | T_LONG,                   // I2L = 133
		CONVERT | FMT_EMPTY | S_INT | T_FLOAT,                  // I2F = 134
		CONVERT | FMT_EMPTY | S_INT | T_DOUBLE,                 // I2D = 135		
		CONVERT | FMT_EMPTY | S_LONG | T_INT,                   // L2I = 136
		CONVERT | FMT_EMPTY | S_LONG | T_FLOAT,                 // L2F = 137
		CONVERT | FMT_EMPTY | S_LONG | T_DOUBLE,                // L2D = 138		
		CONVERT | FMT_EMPTY | S_FLOAT | T_INT,                  // F2I = 139
		CONVERT | FMT_EMPTY | S_FLOAT | T_LONG,                 // F2L = 140
		CONVERT | FMT_EMPTY | S_FLOAT | T_DOUBLE,               // F2D = 141		
		CONVERT | FMT_EMPTY | S_DOUBLE | T_INT,                 // D2I = 142
		CONVERT | FMT_EMPTY | S_DOUBLE | T_LONG,                // D2L = 143
		CONVERT | FMT_EMPTY | S_DOUBLE | T_FLOAT,               // D2F = 144

		CONVERT | FMT_EMPTY | S_INT | T_BYTE,                   // I2B = 145
		CONVERT | FMT_EMPTY | S_INT | T_CHAR,                   // I2C = 146
		CONVERT | FMT_EMPTY | S_INT | T_SHORT,                  // I2S = 147

		CMP | FMT_EMPTY | T_LONG,                           // LCMP = 148
		
		// why oh why are these done this way?
		CMPL | FMT_EMPTY | T_FLOAT,                         // FCMPL = 149
		CMPG | FMT_EMPTY | T_FLOAT,                         // FCMPG = 150
		CMPL | FMT_EMPTY | T_DOUBLE,                        // DCMPL = 151
		CMPG | FMT_EMPTY | T_DOUBLE,                        // DCMPG = 152
		
		IF | FMT_TARGET16 | T_INT,                          // IFEQ = 153;
		IF | FMT_TARGET16 | T_INT,                          // IFNE = 154;
		IF | FMT_TARGET16 | T_INT,                          // IFLT = 155;
		IF | FMT_TARGET16 | T_INT,                          // IFGE = 156;
		IF | FMT_TARGET16 | T_INT,                          // IFGT = 157;
		IF | FMT_TARGET16 | T_INT,                          // IFLE = 158;
				
		IFCMP | FMT_TARGET16 | T_INT,                       // IF_ICMPEQ = 159;
		IFCMP | FMT_TARGET16 | T_INT,                       // IF_ICMPNE = 160;
		IFCMP | FMT_TARGET16 | T_INT,                       // IF_ICMPLT = 161;
		IFCMP | FMT_TARGET16 | T_INT,                       // IF_ICMPGE = 162;
		IFCMP | FMT_TARGET16 | T_INT,                       // IF_ICMPGT = 163;
		IFCMP | FMT_TARGET16 | T_INT,                       // IF_ICMPLE = 164;
		IFCMP | FMT_TARGET16 | T_REF,                       // IF_ACMPEQ = 165;
		IFCMP | FMT_TARGET16 | T_REF,                       // IF_ACMPNE = 166;
		
		GOTO | FMT_TARGET16,                                    // GOTO = 167;
		
		JSR | FMT_TARGET16,                                     // JSR = 168;
		
		RET,                                              // RET = 169;
		
		SWITCH | FMT_TABLESWITCH,                     // TABLESWITCH = 170;
		SWITCH | FMT_LOOKUPSWITCH,                   // LOOKUPSWITCH = 171;
		
		RETURN | FMT_EMPTY | T_INT,                             // IRETURN = 172;
		RETURN | FMT_EMPTY | T_LONG,                            // LRETURN = 173;
		RETURN | FMT_EMPTY | T_FLOAT,                           // FRETURN = 174;
		RETURN | FMT_EMPTY | T_DOUBLE,                          // DRETURN = 175;
		RETURN | FMT_EMPTY | T_REF,                             // ARETURN = 176;
		RETURN | FMT_EMPTY,                                     // RETURN = 177;
			
		FIELDLOAD | MOD_STATIC | FMT_FIELDINDEX16,              // GETSTATIC = 178;
		FIELDSTORE | MOD_STATIC | FMT_FIELDINDEX16,             // PUTSTATIC = 179;
		FIELDLOAD | FMT_FIELDINDEX16,                           // GETFIELD = 180;
		FIELDSTORE | FMT_FIELDINDEX16,                          // PUTFIELD = 181;
 
		INVOKE | FMT_METHODINDEX16,                             // INVOKEVIRTUAL = 182;		
		INVOKE | MOD_SPECIAL | FMT_METHODINDEX16,               // INVOKESPECIAL = 183;
		INVOKE | MOD_STATIC | FMT_METHODINDEX16,                // INVOKESTATIC = 184;
		INVOKE | MOD_INTERFACE | FMT_METHODINDEX16_U8_0,        // INVOKEINTERFACE = 185;		
		0, // UNUSED = 186;		
		NEW | FMT_TYPEINDEX16,                           // NEW = 187
		NEW | FMT_ATYPE,                                 // NEWARRAY = 188
		NEW | FMT_TYPEAINDEX16,                           // ANEWARRAY = 189			
		ARRAYLENGTH | FMT_EMPTY,                                // ARRAYLENGTH = 190;				
		THROW | FMT_EMPTY,                                      // ATHROW = 191		
		CHECKCAST | FMT_TYPEINDEX16,                    // CHECKCAST = 192;		
		INSTANCEOF | FMT_TYPEINDEX16,                   // INSTANCEOF = 193;		
		MONITORENTER | FMT_EMPTY,                               // MONITORENTER = 194;
		MONITOREXIT | FMT_EMPTY,                                // MONITOREXIT = 195;		
		WIDE_INSN,                                              // WIDE = 196;		
		NEW | FMT_TYPEINDEX16_U8,                       // MULTIANEWARRAY = 197;		
		IF | FMT_TARGET16 | T_REF,                              // IFNULL = 198;
		IF | FMT_TARGET16 | T_REF,                              // IFNONNULL = 199;
		GOTO | FMT_TARGET32,                                    // GOTO_W = 200;
		JSR | FMT_TARGET32,                                     // JSR_W = 201;
	};
}
