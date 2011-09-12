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

package wyjc.attributes;

import java.io.*;

import java.util.*;

import wyautl.io.*;
import wyautl.lang.*;
import wyil.lang.*;
import wyil.util.Pair;
import wyjvm.io.*;
import wyjvm.lang.*;

/**
 * A WhileyCondition attribute corresponds to a whiley.ast.exprs.Condition
 * expression. The purpose of the attribute is to allow the conditions to be
 * stored into a class file, such that they can be retrieved and checked against
 * during compilation.
 * 
 * @author djp
 * 
 */
public class WhileyType implements BytecodeAttribute {	
	private Type type;
	
	public WhileyType(Type type) {		
		this.type = type;
	}
	
	public Type type() {
		return type;
	}
	
	public String name() {
		return "WhileyType";
	}
	
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		Constant.addPoolItem(new Constant.Utf8(name()), constantPool);						
		addPoolItems(type, constantPool);
	}
		
	public static void addPoolItems(Type type, Set<Constant.Info> constantPool) {
		if (type instanceof Type.Compound) {
			Automata automata = Type.destruct(type);
			for (int i = 0; i != automata.size(); ++i) {
				Automata.State s = automata.states[i];
				if (s.kind == Type.K_EXISTENTIAL) {
					NameID name = (NameID) s.data;
					Constant.Utf8 utf8 = new Constant.Utf8(name.module()
							.toString());
					Constant.addPoolItem(utf8, constantPool);
					utf8 = new Constant.Utf8(name.name());
					Constant.addPoolItem(utf8, constantPool);
				} else if (s.kind == Type.K_RECORD) {
					ArrayList<String> fields = (ArrayList<String>) s.data;
					for (String f : fields) {
						Constant.Utf8 utf8 = new Constant.Utf8(f);
						Constant.addPoolItem(utf8, constantPool);
					}
				}
			}
		}
	}
		
	public static void write(Type type, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		TypeWriter typeWriter = new TypeWriter(writer,constantPool);
		typeWriter.write(Type.destruct(type));
		typeWriter.close();
	}
			
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryOutputStream iw = new BinaryOutputStream(out);
		write(type,iw,constantPool);
		writer.write_u2(constantPool.get(new Constant.Utf8(name())));
		writer.write_u4(out.size());		
		writer.write(out.toByteArray());						
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		output.print(name() + ":");
	}	
	
	public static class Reader implements BytecodeAttribute.Reader{
		public String name() {
			return "WhileyType";
		}

		public WhileyType read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			input.read_u2(); // attribute name index code
			input.read_u4(); // attribute length
			TypeReader reader = new TypeReader(input,constantPool);
			Type t = Type.construct(reader.read());
			return new WhileyType(t);
		}		
	}
	

	public static final class TypeReader extends BinaryAutomataReader {				
		private final Map<Integer,Constant.Info> constantPool;
		
		public TypeReader(BinaryInputStream reader, Map<Integer,Constant.Info> pool) {
			super(reader);
			this.constantPool = pool;
		}
				
		public Automata.State readState() throws IOException {
			Automata.State state = super.readState();
			if (state.kind == Type.K_EXISTENTIAL) {
				String modstr = ((Constant.Utf8) constantPool.get(reader
						.read_uv())).str;
				ModuleID mid = ModuleID.fromString(modstr);
				String name = ((Constant.Utf8) constantPool.get(reader
						.read_uv())).str;
				NameID data = new NameID(mid, name);
				return new Automata.State(state.kind, state.children,
						state.deterministic, data);
			} else if (state.kind == Type.K_RECORD) {
				int nfields = reader.read_uv();
				ArrayList<String> fields = new ArrayList<String>();
				for (int i = 0; i != nfields; ++i) {
					String f = ((Constant.Utf8) constantPool.get(reader
							.read_uv())).str;
					fields.add(f);
				}
				return new Automata.State(state.kind, state.children,
						state.deterministic, fields);
			}
			return state;
		}
	}	
	
	public static final class TypeWriter extends BinaryAutomataWriter {				
		private final Map<Constant.Info,Integer> constantPool;
		
		public TypeWriter(BinaryOutputStream writer, Map<Constant.Info,Integer> pool) {
			super(writer);
			this.constantPool = pool;
		}
		
		public void writerState(Automata.State state) throws IOException {
			super.write(state);
			if(state.kind == Type.K_EXISTENTIAL) {
				NameID name = (NameID) state.data;
				Constant.Utf8 utf8 = new Constant.Utf8(name.module().toString());
				writer.write_uv(constantPool.get(utf8));
				utf8 = new Constant.Utf8(name.name());
				writer.write_uv(constantPool.get(utf8));
			} else if(state.kind == Type.K_RECORD) {
				ArrayList<String> fields = (ArrayList<String>) state.data;
				writer.write_uv(fields.size());
				for (String f : fields) {
					Constant.Utf8 utf8 = new Constant.Utf8(f);
					writer.write_uv(constantPool.get(utf8));
				}
			}			
		}
	}		
}
