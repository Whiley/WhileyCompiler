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

package wyjvm.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import wyil.util.Pair;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.Bytecode;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile;
import wyjvm.lang.Constant;
import wyjvm.lang.JvmType;
import wyjvm.lang.JvmTypes;

/**
 * This represents the Code attribute from the JVM Spec.
 * 
 * @author David J. Pearce
 */
public class Code implements BytecodeAttribute {

	protected ArrayList<Bytecode> bytecodes;
	protected ArrayList<Handler> handlers;
	protected ArrayList<BytecodeAttribute> attributes;
	protected ClassFile.Method method; // enclosing method

	public Code(Collection<Bytecode> bytecodes,
			Collection<Handler> handlers, ClassFile.Method method) {			
		this.bytecodes = new ArrayList<Bytecode>(bytecodes);
		this.handlers = new ArrayList<Handler>(handlers);
		this.method = method;
		this.attributes = new ArrayList<BytecodeAttribute>();
	}

	public String name() { return "Code"; }

	public List<BytecodeAttribute> attributes() {
		return attributes;
	}
	
	public <T extends BytecodeAttribute> T attribute(Class<T> c) {
		for(BytecodeAttribute a : attributes) {
			if(c.isInstance(a)) {
				return (T) a;
			}
		}
		return null;
	}
	
	/**
	 * Determine the maximum number of local variable slots required for
	 * this method.
	 * 
	 * @return
	 */
	public int maxLocals() {
		int max = 0;
		for(Bytecode b :  bytecodes) {
			if(b instanceof Bytecode.Store) {
				Bytecode.Store s = (Bytecode.Store) b;
				max = Math.max(max, s.slot + ClassFile.slotSize(s.type));
			} else if(b instanceof Bytecode.Load) {
				Bytecode.Load l = (Bytecode.Load) b;
				max = Math.max(max, l.slot + ClassFile.slotSize(l.type));					
			} else if(b instanceof Bytecode.Iinc) {
				Bytecode.Iinc l = (Bytecode.Iinc) b;
				max = Math.max(max, l.slot+1);					
			}
		}		
		
		// The reason for the following, is that we must compute the
		// *minimal* number of slots required. Essentially, this is enough
		// to hold the "this" pointer (if appropriate) and the parameters
		// supplied. The issue is that the bytecodes might not actually
		// access all of the parameters supplied, so just looking at them
		// might produce an underestimate.		
		
		int thisp = method.isStatic() ? 0 : 1; 
		int min = thisp;

		for(JvmType p :  method.type().parameterTypes()) {			
			min += ClassFile.slotSize(p);
		}		
		
		return Math.max(max+thisp,min);
	}

	/**
	 * Determine the maximum number of stack slots required for this method.
	 * 
	 * @return
	 */
	public int maxStack() {
		// This algorithm computes a conservative over approximation. In
		// theory, we can do better, but there's little need to.
		int max = 0;
		int current = 0;
		int idx = 0;
		HashMap<Integer,Integer> starts = new HashMap<Integer,Integer>();
		for(Handler h : handlers) {
			starts.put(h.start,1);
		}
		
		HashMap<String,Integer> labels = new HashMap<String,Integer>();
		for(Bytecode b : bytecodes) {
			if(b instanceof Bytecode.Label) {
				Bytecode.Label lab = (Bytecode.Label) b;
				labels.put(lab.name, idx);
			}
			idx = idx + 1;
		}
		
		idx = 0;
		for(Bytecode b : bytecodes) {
			if(starts.containsKey(idx)) {
				// This bytecode is the first of an exception handler. Such
				// handlers begin with the thrown exception object on the stack,
				// hence we must account for this.
				current = Math.max(current,starts.get(idx));
			}			
									
			current = current + b.stackDiff();			
			max = Math.max(current,max);	
			
			if(b instanceof Bytecode.Goto) {
				Bytecode.Goto gto = (Bytecode.Goto) b;
				int offset = labels.get(gto.label);
				if(!starts.containsKey(offset)) {
					starts.put(offset, current);
				}
				current = 0;
			} else if(b instanceof Bytecode.If) {
				Bytecode.If gto = (Bytecode.If) b;
				int offset = labels.get(gto.label);
				if(!starts.containsKey(offset)) {
					starts.put(offset, current);
				}
			} else if(b instanceof Bytecode.Switch) {
				Bytecode.Switch gto = (Bytecode.Switch) b;
				for(Pair<Integer,String> c : gto.cases) {
					int offset = labels.get(c.second());
					if(!starts.containsKey(offset)) {
						starts.put(offset, current);
					}
				}
				int offset = labels.get(gto.defaultLabel);
				if(!starts.containsKey(offset)) {
					starts.put(offset, current);
				}
			}
			
			idx = idx + 1;
		}
		return max;
	}

	public List<Bytecode> bytecodes() { 
		return bytecodes;
	}

	public List<Handler> handlers() {
		return handlers;
	}

	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		Constant.addPoolItem(new Constant.Utf8("Code"), constantPool);

		for (Bytecode b : bytecodes()) {
			b.addPoolItems(constantPool);
		}

		for(Handler h : handlers) {
			if(!JvmTypes.isClass("java.lang","Throwable",h.exception)) {
				Constant.addPoolItem(Constant.buildClass(h.exception), constantPool);
			}
		}
		
		for(BytecodeAttribute a : attributes) {
			a.addPoolItems(constantPool, loader);			
		}		
	}
	
	/**
	 * The exception handler class is used to store the necessary information
	 * about where control-flow is directed when an exception is raised.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Handler {
		/**
		 * The start index of bytecodes covered by the handler.
		 */
		public int start;
		/**
		 * One past the last index covered by the handler.
		 */
		public int end;
		public String label; 
		public JvmType.Clazz exception;

		public Handler(int start, int end, String label,
				JvmType.Clazz exception) {
			this.start = start;
			this.end = end;
			this.label = label;
			this.exception = exception;
		}
	}	
	
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) throws IOException {

		// This method is a little tricky. The basic strategy is to first
		// translate each bytecode into it's binary representation. One
		// difficulty here, is that we must defer calculating the targets of
		// branch statements until after this is done, since we can't do the
		// calculation without exact values.

		// === DETERMINE LABEL OFFSETS ===

		HashMap<String, Integer> labelOffsets = new HashMap<String, Integer>();			

		// The insnOffsets is used to map the statement index to the
		// corresponding bytecodes. This is used in determining the start and
		// end offsets for the exception handlers

		int[] insnOffsets = new int[bytecodes.size()];
		
		boolean guestimate = true;
		
		while(guestimate) {
			guestimate = false;
			// With this loop, we have to iterate until we reach a fixed point
			// regarding the label offsets. The basic issue is that, increasing
			// the size of a branch may result in other branches we've already
			// passed requiring their sizes be increased. This can happen
			// because switch statements adjust their size depending on their
			// offset and include padding appropriately. To resolve this, I
			// simply ensure that once a branch looks like it needs to be long,
			// then it's fixed as being long. This may, in very unusual cases,
			// be sub-optimal, but at least it ensures termination!
			int offset = 0;							
			
			for (int i=0;i!=bytecodes.size();++i) {
				Bytecode b = bytecodes.get(i);
				insnOffsets[i] = offset;
				if (b instanceof Bytecode.Label) {
					Bytecode.Label l = (Bytecode.Label) b;
					if(labelOffsets.containsKey(l.name)) {						
						int old = labelOffsets.get(l.name);
						if(old != offset) {										
							guestimate=true;
						}
					} 
					
					labelOffsets.put(l.name, offset);					
				} else if (b instanceof Bytecode.Branch) {
					Bytecode.Branch br = (Bytecode.Branch) b;
					if(labelOffsets.containsKey(br.label))  {
						int len = br.toBytes(offset, labelOffsets, constantPool).length;
						offset += len;
						
						if(len > 3 && !br.islong) {
							// Now, this branch looks like it needs to be long,
							// so fix it so it's always long.
							bytecodes.set(i,br.fixLong());
						}
					} else {
						// In this case, we can't determine the offset of the
						// label, since we may not have passed it yet!
						// Therefore, for now, I assume that the bytecode requires 3
						// bytes (which is true, except for goto_w).
						offset += 3;											
						guestimate = true;
					}
				} else if (b instanceof Bytecode.Switch) {
					// calculate switch statement size					
					offset += ((Bytecode.Switch) b).getSize(offset);
				} else {
					offset += b.toBytes(offset, labelOffsets, constantPool).length;
				}				
			}						
		}

		// === CREATE BYTECODE BYTES ===

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		int offset = 0;
		for (Bytecode b : bytecodes) {
			byte[] bs = b.toBytes(offset, labelOffsets, constantPool);
			bout.write(bs);
			offset += bs.length;			
		}
		byte[] bytecodebytes = bout.toByteArray();
		
		// === CREATE ATTRIBUTE BYTES
		bout = new ByteArrayOutputStream();
		BinaryOutputStream attrbout = new BinaryOutputStream(bout); 
		for(BytecodeAttribute a : attributes) {
			if(a instanceof BytecodeMapAttribute) {
				BytecodeMapAttribute bap = (BytecodeMapAttribute) a;
				bap.write(insnOffsets, attrbout, constantPool, loader);
			} else {
				a.write(attrbout, constantPool, loader);
			}
		}
		byte[] attrbytes = bout.toByteArray();
		
		// === WRITE CODE ATTRIBUTE ===		

		writer.write_u2(constantPool.get(new Constant.Utf8("Code")));
		// need to figure out exception_table length
		int exception_table_length = handlers().size() * 8;
		// need to figure out attribute_table length
		int attribute_table_length = attrbytes.length;
		// write attribute length
		writer.write_u4(bytecodebytes.length + exception_table_length + attribute_table_length
				+ 12);
		// now write data
		writer.write_u2(maxStack());
		writer.write_u2(maxLocals());
		writer.write_u4(bytecodebytes.length);
		// write bytecode instructions
		for (int i = 0; i != bytecodebytes.length; ++i) {			
			writer.write_u1(bytecodebytes[i]);
		}

		// write exception handlers
		writer.write_u2(handlers().size());
		for (Handler h : handlers()) {
			writer.write_u2(insnOffsets[h.start]);
			writer.write_u2(insnOffsets[h.end]);
			writer.write_u2(labelOffsets.get(h.label));

			if (JvmTypes.isClass("java.lang", "Throwable", h.exception)) {
				writer.write_u2(0);
			} else {
				writer.write_u2(constantPool.get(Constant
						.buildClass(h.exception)));
			}
		}
				
		writer.write_u2(attributes.size()); 
		writer.write(attrbytes);		
	}
	
	/**
	 * The purpose of this method is to validate a candidate list of rewrites.
	 * More specifically, a rewrite is considered to be invalid if it crosses an
	 * exception handler boundary. Such rewrites are automatically removed from
	 * the list.
	 * 
	 * @param rewrites
	 */
	public void validate(List<Rewrite> rewrites) {		
		for(int i=0;i!=rewrites.size();++i) {
			Rewrite rw = rewrites.get(i);
			int start = rw.start;
			int end = start + rw.length;			
			for(Handler h : handlers) {								
				int hstart = h.start;
				int hend = h.end;
				if ((hstart < end && hend >= end)
						|| (hstart < start && hend >= start)) {					
					// Not OK
					rewrites.remove(i);
					i = i - 1;
					break;
				}
			}
		}
	}
	
	/**
	 * This method accepts a list of rewrites which should be applied. For
	 * efficiency reasons, several constraints are made on the list:
	 * <ol>
	 * <li>The rewrites are ordered by their start location, such that the
	 * first rewrite has the lowest start location</li>
	 * <li>The rewrites don't overlap. That is, we assume only one rewrite can
	 * be applied to any given region of bytecodes.</li>
	 * </ol>
	 * If the complete set of rewrites cannot be constructed according to these
	 * constraints, then it needs to be split up into several calls to this
	 * method.
	 * 
	 * @param rewrites
	 */
	public void apply(List<Rewrite> rewrites) {
		int offset = 0;
		
		// Ok, there's a bit of a hack here, since I assume that the rewrites
		// never increase the number of bytecodes!
		for(Rewrite rw : rewrites) {
			int start = rw.start + offset;
			int pos = start;
			Bytecode[] codes = rw.bytecodes;
			for(int i=0;i!=codes.length;++i,++pos) {
				bytecodes.set(pos,codes[i]);
			}
			
			// Now, remove any remaining slots that were erased.
			int diff = rw.length - codes.length;
			for(int i=0;i!=diff;++i) {
				bytecodes.remove(pos);				
			}
			offset -= diff;
			
			// Now, update the handlers appropriately					
			int end = start + rw.length;
			for (Handler h : handlers) {				
				int hstart = h.start;
				int hend = h.end;
				if (hstart <= start && hend > start) {
					hend -= diff;
				} else if (hstart >= end) {
					hstart -= diff;
					hend -= diff;
				} else if ((hstart < end && hend >= end)
						|| (hstart < start && hend >= start)) {					
					throw new RuntimeException(
							"Attempt to optimise an instruction that partially straddles an exception boundary!");
				}
				h.start = hstart;
				h.end = hend;
			}											
		}
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) {
		output.println("  Code:");
		output.println("   stack = " + maxStack() + ", locals = "
				+ maxLocals());

		for (Bytecode b : bytecodes) {
			if(b instanceof Bytecode.Label) {
				output.println("  " + b);
			} else {
				output.println("   " + b);
			}
		}
	}		
	
	/**
	 * A rewrite defines a sequence of bytecodes that are to be rewritten as a
	 * (potentially) smaller sequence.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Rewrite {
		public final int start;  // first bytecode in sequence to be replaced
		public final int length; // number of bytecodes to replace
		public final Bytecode[] bytecodes; // array of bytecodes to substitute
		
		public Rewrite(int start, int length, Bytecode... bytecodes) {
			this.start = start;
			this.length = length;
			this.bytecodes = bytecodes;
		}
	}

	/**
	 * <p>
	 * Maps bytecodes to some kind of attribute. For example, the Exceptions
	 * attribute maps bytecodes to exception handler regions; likewise, the
	 * LineNumbersTable attribute maps bytecodes to source code line numbers.
	 * </p>
	 * 
	 * <p>
	 * During bytecode optimisation, the relative position of bytecodes may
	 * change as a result of eliminating redundant bytecodes. In such a case we
	 * need to update those attributes which are affected. This interface
	 * captures those attributes which are affected, and provides a hook to tell
	 * them about rewrites as they happen.
	 * </p>
	 * 
	 * <p>
	 * Finally, the actual bytecode offsets in the code block (as opposed to
	 * their index in the block) are not known until the class file is actually
	 * written. Attributes which write bytecode offsets must convert between
	 * indices and actual code offsets.
	 * </p>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static interface BytecodeMapAttribute extends BytecodeAttribute {
		
		/**
		 * This method accepts a list of rewrites which should be applied. For
		 * efficiency reasons, several constraints are made on the list:
		 * <ol>
		 * <li>The rewrites are ordered by their start location, such that the
		 * first rewrite has the lowest start location</li>
		 * <li>The rewrites don't overlap. That is, we assume only one rewrite
		 * can be applied to any given region of bytecodes.</li>
		 * </ol>
		 * If the complete set of rewrites cannot be constructed according to
		 * these constraints, then it needs to be split up into several calls to
		 * this method.
		 * 
		 * @param rewrites
		 */
		public void apply(List<Rewrite> rewrites);

		/**
		 * This method requires the attribute to write itself to the binary
		 * stream.
		 * 
		 * @param bytecodeOffsets
		 *            --- maps each bytecode index to its actual offset in the
		 *            code block.
		 * @param writer
		 *            --- stream to write attribute to
		 * @param constantPool
		 *            --- map of constant pool items to their actual pool index
		 * @param load
		 *            --- class loader instance
		 * @returns the number of bytes written.
		 * @throws IOException
		 */
		public void write(int[] bytecodeOffsets, BinaryOutputStream writer,
				Map<Constant.Info, Integer> constantPool, ClassLoader loader)
				throws IOException;
	}
}	
