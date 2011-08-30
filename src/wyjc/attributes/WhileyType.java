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
import wyil.lang.*;
import static wyts.lang.Node.*;
import wyil.util.Pair;
import wyjvm.io.*;
import wyjvm.lang.*;
import wyts.lang.*;
import wyts.util.AbstractTypeBuilder;
import wyts.util.TypeBuilder;

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
		
	public static void addPoolItems(Type type,
			Set<Constant.Info> constantPool) {
		Type.build(new ConstantBuilder(constantPool),type);
	}
		
	public static void write(Type type, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		Type.build(new JvmBuilder(writer,constantPool),type);
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
	
	public static class Reader implements BytecodeAttribute.Reader {
		public String name() {
			return "WhileyType";
		}

		public WhileyType read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			input.read_u2(); // attribute name index code
			input.read_u4(); // attribute length
			Type t = readType(input, constantPool);
			return new WhileyType(t);
		}

		public static Type readType(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			InternalTypeBuilder builder = new InternalTypeBuilder();
			int numNodes = input.read_u2();
			builder.initialise(numNodes);					
			for(int i=0;i!=numNodes;++i) {
				int tag = input.read_u1();			
				switch (tag) {
				case K_ANY:
					builder.buildPrimitive(i, Type.T_ANY);
					break;
				case K_VOID:
					builder.buildPrimitive(i, Type.T_VOID);
					break;
				case K_NULL:
					builder.buildPrimitive(i, Type.T_NULL);
					break;
				case K_BOOL:
					builder.buildPrimitive(i, Type.T_BOOL);
					break;
				case K_BYTE:
					builder.buildPrimitive(i, Type.T_BYTE);
					break;
				case K_CHAR:
					builder.buildPrimitive(i, Type.T_CHAR);
					break;
				case K_INT:
					builder.buildPrimitive(i, Type.T_INT);
					break;
				case K_RATIONAL:
					builder.buildPrimitive(i, Type.T_REAL);
					break;
				case K_STRING:
					builder.buildPrimitive(i, Type.T_STRING);
					break;
				case K_LIST:
					builder.buildList(i, input.read_u2());
					break;
				case K_SET:
					builder.buildSet(i, input.read_u2());
					break;
				case K_TUPLE:
				{
					int nents = input.read_u2();
					int[] elems = new int[nents];
					for (int j = 0; j != nents; ++j) {
						elems[j] = input.read_u2();
					}
					builder.buildTuple(i, elems);
					break;
				}
				case K_RECORD:
				{
					int nents = input.read_u2();
					Pair<String, Integer>[] types = new Pair[nents];
					for (int j = 0; j != nents; ++j) {
						String key = ((Constant.Utf8) constantPool.get(input
								.read_u2())).str;						
						types[j] = new Pair(key, input.read_u2());
					}
					builder.buildRecord(i, types);
					break;
				}
				case K_UNION:
				{
					int nents = input.read_u2();
					int[] elems = new int[nents];
					for (int j = 0; j != nents; ++j) {
						elems[j] = input.read_u2();
					}
					builder.buildUnion(i, elems);
					break;					
				}
				case K_PROCESS:					
					builder.buildProcess(i, input.read_u2());
					break;		
				case K_EXISTENTIAL:
					ModuleID mid = readModule(input,constantPool);
					String name = ((Constant.Utf8) constantPool
							.get(input.read_u2())).str;
					builder.buildExistential(i, new NameID(mid,name));
					break;
				case K_HEADLESS:
				case K_METHOD:{
					int rec = -1;
					if(tag == K_METHOD) {
						rec = input.read_u2();					
					}
					int ret = input.read_u2();
					int nents = input.read_u2();
					int[] params = new int[nents];
					for (int j = 0; j != nents; ++j) {
						params[j] = input.read_u2();
					}
					builder.buildMethod(i, rec, ret, params);
					break;
				}				
				case K_FUNCTION: {					
					int ret = input.read_u2();
					int nents = input.read_u2();
					int[] params = new int[nents];
					for (int j = 0; j != nents; ++j) {
						params[j] = input.read_u2();
					}
					builder.buildFunction(i, ret, params);
					break;
				}				
				default:
					throw new RuntimeException("invalid type");
				}
			}
			
			return builder.type();			
		}

		protected static ModuleID readModule(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			String modstr = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
			return ModuleID.fromString(modstr);
		}
	}
	
	private static class ConstantBuilder extends AbstractTypeBuilder {
		private final Set<Constant.Info> constantPool;
		
		public ConstantBuilder(Set<Constant.Info> pool) {
			constantPool = pool;
		}		

		public void buildExistential(int index, NameID name) { 
			Constant.Utf8 utf8 = new Constant.Utf8(name.module().toString());
			Constant.addPoolItem(utf8,constantPool);			
			utf8 = new Constant.Utf8(name.name());
			Constant.addPoolItem(utf8,constantPool);			
		}
		
		public void buildRecord(int index, Pair<String, Integer>... fields) { 
			for(Pair<String,Integer> f : fields) {
				Constant.Utf8 utf8 = new Constant.Utf8(f.first());
				Constant.addPoolItem(utf8,constantPool);
			}
		}
	}
	
	public static class JvmBuilder implements TypeBuilder {		
		private final BinaryOutputStream writer;
		private final Map<Constant.Info,Integer> constantPool;
		
		public JvmBuilder(BinaryOutputStream writer, Map<Constant.Info,Integer> pool) {
			this.writer = writer;
			this.constantPool = pool;
		}
		
		public void initialise(int numNodes) {
			try {
				writer.write_u2(numNodes);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildPrimitive(int index, Type.Leaf t) {
			try {
				if(t == Type.T_ANY) {
					writer.write_u1(K_ANY );
				} else if(t == Type.T_VOID) {
					writer.write_u1(K_VOID);
				} else if(t == Type.T_NULL) {
					writer.write_u1(K_NULL );
				} else if(t == Type.T_BOOL) {
					writer.write_u1(K_BOOL );			
				} else if(t == Type.T_BYTE) {			
					writer.write_u1(K_BYTE );		
				} else if(t == Type.T_CHAR) {			
					writer.write_u1(K_CHAR );		
				} else if(t == Type.T_INT) {			
					writer.write_u1(K_INT );		
				} else if(t == Type.T_REAL) {
					writer.write_u1(K_RATIONAL );			
				} else if(t == Type.T_STRING) {
					writer.write_u1(K_STRING );			
				} else {
					throw new RuntimeException("unknown type encountered: " + t);		
				}
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}			
		}

		public void buildExistential(int index, NameID name) {
			try {
				writer.write_u1(K_EXISTENTIAL);				
				Constant.Utf8 utf8 = new Constant.Utf8(name.module().toString());
				writer.write_u2(constantPool.get(utf8));
				utf8 = new Constant.Utf8(name.name());
				writer.write_u2(constantPool.get(utf8));
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildSet(int index, int element) {
			try {
				writer.write_u1(K_SET);			
				writer.write_u2(element);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildList(int index, int element) {
			try {
				writer.write_u1(K_LIST);
				writer.write_u2(element);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildProcess(int index, int element) {
			try {
				writer.write_u1(K_PROCESS);	
				writer.write_u2(element);				
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildDictionary(int index, int key, int value) {
			try {
				writer.write_u1(K_DICTIONARY);
				writer.write_u2(key);
				writer.write_u2(value);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildTuple(int index, int... elements) {
			try {
				writer.write_u1(K_TUPLE);
				// FIXME: bug here if number of entries > 64K
				writer.write_u2(elements.length);
				for(int e : elements) {					
					writer.write_u2(e);					
				}	
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildRecord(int index, Pair<String, Integer>... fields) {
			try {				
				writer.write_u1(K_RECORD );
				// FIXME: bug here if number of entries > 64K
				writer.write_u2(fields.length);
				for(Pair<String,Integer> p : fields) {
					Constant.Utf8 utf8 = new Constant.Utf8(p.first());
					writer.write_u2(constantPool.get(utf8));
					writer.write_u2(p.second());					
				}			
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildFunction(int index, int ret,
				int... parameters) {
			try {				
				writer.write_u1(K_FUNCTION);				
				writer.write_u2(ret);
				writer.write_u2(parameters.length);
				for (int p : parameters) {
					writer.write_u2(p);
				}
			} catch (IOException e) {
				throw new RuntimeException("internal failure", e);
			}
		}

		public void buildMethod(int index, int receiver, int ret,
				int... parameters) {
			try {			
				if(receiver == -1) {
					writer.write_u1(K_HEADLESS);					
				} else {
					writer.write_u1(K_METHOD);
					writer.write_u2(receiver);
				}
				writer.write_u2(ret);
				writer.write_u2(parameters.length);
				for (int p : parameters) {
					writer.write_u2(p);
				}
			} catch (IOException e) {
				throw new RuntimeException("internal failure", e);
			}
		}
		
		public void buildUnion(int index, int... bounds) {
			try {				
				writer.write_u1(K_UNION );			
				// FIXME: bug here if number of bounds > 64K
				writer.write_u2(bounds.length);
				for(int b : bounds) {
					writer.write_u2(b);
				}	
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}
		
		public void buildIntersection(int index, int... bounds) {
			try {				
				writer.write_u1(K_INTERSECTION);			
				// FIXME: bug here if number of bounds > 64K
				writer.write_u2(bounds.length);
				for(int b : bounds) {
					writer.write_u2(b);
				}	
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}
		
		public void buildDifference(int index, int left, int right) {
			try {
				writer.write_u1(K_DIFFERENCE);
				writer.write_u2(left);
				writer.write_u2(right);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}
	}		
}
