// This file is part of the Java Compiler Kit (JKit)
//
// The Java Compiler Kit is free software; you can 
// redistribute it and/or modify it under the terms of the 
// GNU General Public License as published by the Free Software 
// Foundation; either version 2 of the License, or (at your 
// option) any later version.
//
// The Java Compiler Kit is distributed in the hope
// that it will be useful, but WITHOUT ANY WARRANTY; without 
// even the implied warranty of MERCHANTABILITY or FITNESS FOR 
// A PARTICULAR PURPOSE.  See the GNU General Public License 
// for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Java Compiler Kit; if not, 
// write to the Free Software Foundation, Inc., 59 Temple Place, 
// Suite 330, Boston, MA  02111-1307  USA
//
// (C) David James Pearce, 2009. 

package wyjvm.io;

import java.io.*;
import java.util.*;

import wyjvm.lang.*;

public class BytecodeFileWriter {	
	protected final PrintWriter output;
	protected final ClassLoader loader;
	
	public BytecodeFileWriter(OutputStream o, ClassLoader loader) {
		output = new PrintWriter(o);
		this.loader = loader;
	}	

	public void write(ClassFile cfile) throws IOException {
		ArrayList<Constant.Info> constantPool = cfile.constantPool(loader);
		HashMap<Constant.Info,Integer> poolMap = new HashMap<Constant.Info,Integer>();
		
		int index = 0;
		for(Constant.Info ci : constantPool) {
			poolMap.put(ci, index++);
		}

		index = 0;
		for (Constant.Info c : constantPool) {
			if (c != null) { // item at index 0 is always null
				output.print("#" + ++index + "\t");
				output.println(c);
			}
		}
		output.println();

		writeModifiers(cfile.modifiers());
		output.print("class " + cfile.type() + " ");
		if(cfile.superClass() != null) {
			output.print(" extends " + cfile.superClass());
		}
		if(cfile.interfaces().size() > 0) {
			output.print(" implements ");
			boolean firstTime=true;
			for(JvmType.Clazz i : cfile.interfaces()) {
				if(!firstTime) {
					output.print(", ");
				}
				firstTime=false;
				output.print(i);
			}			
		}				
		
		output.println();
		
		for(BytecodeAttribute a : cfile.attributes()) {
			a.print(output,poolMap,loader);
		}
		
		output.println(" {");
		
		for(ClassFile.Field f : cfile.fields()) {
			writeField(f,poolMap);
		}
		
		if(!cfile.fields().isEmpty()) {
			output.println();
		}
		
		for(ClassFile.Method m : cfile.methods()) {
			writeMethod(cfile,m,poolMap);
			output.println();
		}
	
		output.println("}");
		
		output.flush();
	}
	
	protected void writeField(ClassFile.Field f,
			HashMap<Constant.Info, Integer> poolMap) throws IOException {
		output.print("  ");
		writeModifiers(f.modifiers());
		writeTypeWithoutBounds(f.type());		
		output.println(" " + f.name() + ";");
		for(BytecodeAttribute a : f.attributes()) {
			a.print(output,poolMap,loader);
		}
	}

	protected void writeMethod(ClassFile clazz, ClassFile.Method method,
			HashMap<Constant.Info, Integer> poolMap) throws IOException {
		output.print("  ");
		writeModifiers(method.modifiers());
		JvmType.Function type = method.type(); 
		
		List<JvmType.Variable> typeArgs = type.typeArguments();
		boolean firstTime=true;
		if(typeArgs.size() > 0) {
			output.print("<");
			for(JvmType.Variable tv : typeArgs) {
				if(!firstTime) {
					output.print(", ");
				}
				firstTime=false;
				output.print(tv);
			}
			output.print("> ");
		}
		
		writeTypeWithoutBounds(type.returnType());
		output.print(" " + method.name());
		output.print("(");		
		firstTime=true;
		
		List<JvmType> paramTypes = type.parameterTypes();				
		
		for(int i = 0; i != paramTypes.size();++i) {
			if(!firstTime) {
				output.print(", ");
			}
			firstTime=false;					
			writeTypeWithoutBounds(paramTypes.get(i));
		}
		
		output.println(");");
		
		for(BytecodeAttribute a : method.attributes()) {
			a.print(output,poolMap,loader);
		}					
	}	
	protected void writeModifiers(List<Modifier> modifiers) {	
		writeModifiers(modifiers,output);
	}
	
	protected void writeTypeWithoutBounds(JvmType t) {
		if(t instanceof JvmType.Variable) {
			JvmType.Variable v = (JvmType.Variable) t;
			output.write(v.variable());
		} else {
			output.write(t.toString());
		}
	}
	
	public static void writeModifiers(List<Modifier> modifiers, PrintWriter output) {
		for (Modifier x : modifiers) {			
			if (x instanceof Modifier.Private) {
				output.write("private ");
			} else if (x instanceof Modifier.Protected) {
				output.write("protected ");
			} else if (x instanceof Modifier.Public) {
				output.write("public ");
			} else if (x instanceof Modifier.Static) {
				output.write("static ");
			} else if (x instanceof Modifier.Abstract) {
				output.write("abstract ");
			} else if (x instanceof Modifier.Final) {
				output.write("final ");
			} else if (x instanceof Modifier.Super) {
				output.write("super ");
			} else if (x instanceof Modifier.Bridge) {
				output.write("bridge ");
			} else if (x instanceof Modifier.Enum) {
				output.write("enum ");
			} else if (x instanceof Modifier.Synthetic) {
				output.write("synthetic ");
			} else if (x instanceof Modifier.Native) {
				output.write("native ");
			} else if (x instanceof Modifier.StrictFP) {
				output.write("strictfp ");
			} else if (x instanceof Modifier.Synchronized) {
				output.write("synchronized ");
			} else if (x instanceof Modifier.Transient) {
				output.write("transient ");
			} else if (x instanceof Modifier.Volatile) {
				output.write("volatile ");
			} else {
				output.write("unknown ");
			}
		}
	}	
}
