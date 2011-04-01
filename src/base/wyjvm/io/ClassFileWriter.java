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

package wyjvm.io;

import java.io.*;
import java.util.*;

import wyil.util.Pair;
import wyjvm.lang.*;

public class ClassFileWriter {
	protected final BinaryOutputStream output;
	protected final ClassLoader loader;

	/**
	 * Construct a ClassFileWriter Object that the given output stream to write
	 * ClassFiles.
	 * 
	 * @param o
	 *            Output stream for class bytes
	 */
	public ClassFileWriter(OutputStream o, ClassLoader loader) {
		output = new BinaryOutputStream(o);		
		this.loader = loader;
	}
	
	public void write(ClassFile cfile) throws IOException {
		ArrayList<Constant.Info> constantPool = cfile.constantPool(loader);
		HashMap<Constant.Info,Integer> poolMap = new HashMap<Constant.Info,Integer>();
		
		int index = 0;
		for(Constant.Info ci : constantPool) {
			poolMap.put(ci, index++);
		}
		
		output.write_u1(0xCA);
		output.write_u1(0xFE);
		output.write_u1(0xBA);
		output.write_u1(0xBE);
		output.write_u4(cfile.version());
		output.write_u2(constantPool.size());
		// now, write the constant pool
		for (Constant.Info c : constantPool) {
			if (c != null) { // item at index 0 is always null
				output.write(c.toBytes(poolMap));
			}
		}
		
		// ok, done that now write more stuff
		writeClassModifiers(cfile.modifiers());
		output.write_u2(poolMap.get(Constant.buildClass(cfile.type())));
		if (cfile.superClass() != null) {
			output.write_u2(poolMap.get(Constant.buildClass(cfile.superClass())));
		}
		output.write_u2(cfile.interfaces().size());
		for (JvmType.Reference i : cfile.interfaces()) {
			output.write_u2(poolMap.get(Constant.buildClass(i)));
		}

		output.write_u2(cfile.fields().size());
		for (ClassFile.Field f : cfile.fields()) {
			writeField(f, poolMap);
		}

		output.write_u2(cfile.methods().size());
		for (ClassFile.Method m : cfile.methods()) {
			writeMethod(m, poolMap);
		}

		output.write_u2(cfile.attributes().size());
		for(BytecodeAttribute a : cfile.attributes()) {
			a.write(output, poolMap, loader);
		}
		
		output.flush();
	}
	
	protected void writeField(ClassFile.Field f,
			HashMap<Constant.Info, Integer> constantPool) throws IOException {
		writeFieldModifiers(f.modifiers());
		output.write_u2(constantPool.get(new Constant.Utf8(f.name())));
		output.write_u2(constantPool.get(new Constant.Utf8(ClassFile
				.descriptor(f.type(), false))));

		// Write number of attributes
		output.write_u2(f.attributes().size());

		for (BytecodeAttribute a : f.attributes()) {
			a.write(output, constantPool, loader);
		}
	}

	protected void writeMethod(ClassFile.Method m,
			HashMap<Constant.Info, Integer> constantPool) throws IOException {

		writeMethodModifiers(m.modifiers());
		output.write_u2(constantPool.get(new Constant.Utf8(m.name())));
		output.write_u2(constantPool.get(new Constant.Utf8(ClassFile
				.descriptor(m.type(), false))));
		
		output.write_u2(m.attributes().size());

		for (BytecodeAttribute a : m.attributes()) {
			a.write(output, constantPool, loader);
		}
	}
	
	protected void writeClassModifiers(List<Modifier> modifiers)
			throws IOException {

		int mods = 0;
		for(Modifier m : modifiers) {
			if(m instanceof Modifier.Public) {
				mods |= ClassFileReader.ACC_PUBLIC;
			} else if(m instanceof Modifier.Final) {
				mods |= ClassFileReader.ACC_FINAL;
			} else if(m instanceof Modifier.Super) {
				mods |= ClassFileReader.ACC_SUPER;
			} else if(m instanceof Modifier.Interface) {
				mods |= ClassFileReader.ACC_INTERFACE;
			} else if(m instanceof Modifier.Abstract) {
				mods |= ClassFileReader.ACC_ABSTRACT;
			} else if(m instanceof Modifier.Enum) {
				mods |= ClassFileReader.ACC_ENUM;
			}
		}
		
		output.write_u2(mods);
	}

	protected void writeFieldModifiers(List<Modifier> modifiers)
			throws IOException {
		
		int mods = 0;
		for(Modifier m : modifiers) {
			if(m instanceof Modifier.Public) {
				mods |= ClassFileReader.ACC_PUBLIC;
			} else if(m instanceof Modifier.Private) {
				mods |= ClassFileReader.ACC_PRIVATE;
			} else if(m instanceof Modifier.Protected) {
				mods |= ClassFileReader.ACC_PROTECTED;
			} else if(m instanceof Modifier.Static) {
				mods |= ClassFileReader.ACC_STATIC;
			} else if(m instanceof Modifier.Final) {
				mods |= ClassFileReader.ACC_FINAL;
			} else if(m instanceof Modifier.Volatile) {
				mods |= ClassFileReader.ACC_VOLATILE;
			} else if(m instanceof Modifier.Transient) {
				mods |= ClassFileReader.ACC_TRANSIENT;
			} else if(m instanceof Modifier.Synthetic) {
				mods |= ClassFileReader.ACC_SYNTHETIC;
			} else if(m instanceof Modifier.Enum) {
				mods |= ClassFileReader.ACC_ENUM;
			}
		}
		
		output.write_u2(mods);
	}

	protected void writeMethodModifiers(List<Modifier> modifiers)
			throws IOException {		
		int mods = 0;
		for(Modifier m : modifiers) {
			if(m instanceof Modifier.Public) {
				mods |= ClassFileReader.ACC_PUBLIC;
			} else if(m instanceof Modifier.Private) {
				mods |= ClassFileReader.ACC_PRIVATE;
			} else if(m instanceof Modifier.Protected) {
				mods |= ClassFileReader.ACC_PROTECTED;
			} else if(m instanceof Modifier.Static) {
				mods |= ClassFileReader.ACC_STATIC;
			} else if(m instanceof Modifier.Final) {
				mods |= ClassFileReader.ACC_FINAL;
			} else if(m instanceof Modifier.Volatile) {
				mods |= ClassFileReader.ACC_VOLATILE;
			} else if(m instanceof Modifier.Synchronized) {
				mods |= ClassFileReader.ACC_SYNCHRONIZED;
			} else if(m instanceof Modifier.Bridge) {
				mods |= ClassFileReader.ACC_BRIDGE;
			} else if(m instanceof Modifier.VarArgs) {
				mods |= ClassFileReader.ACC_VARARGS;
			} else if(m instanceof Modifier.Native) {
				mods |= ClassFileReader.ACC_NATIVE;
			} else if(m instanceof Modifier.Abstract) {
				mods |= ClassFileReader.ACC_ABSTRACT;
			} else if(m instanceof Modifier.Synthetic) {
				mods |= ClassFileReader.ACC_SYNTHETIC;
			} else if(m instanceof Modifier.StrictFP) {
				mods |= ClassFileReader.ACC_STRICT;
			}
		}
		
		output.write_u2(mods);
	}
		
	protected static void writeModifiers(List<Modifier> modifiers, int[] masks,
			Modifier[] mods, BinaryOutputStream output) throws IOException {
		ArrayList<Modifier> r = new ArrayList<Modifier>();

		int mask = 0;

		for (Modifier m : modifiers) {
			for (int i = 0; i != mods.length; ++i) {
				if (mods[i].getClass().equals(m.getClass())) {
					mask |= masks[i];
				}
			}
		}

		output.write_u2(mask);
	}
}