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

package wyjvm.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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
 * @author djp
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
		HashSet<Integer> handlerStarts = new HashSet<Integer>();
		for(Handler h : handlers) {
			handlerStarts.add(h.start);
		}
		for(Bytecode b : bytecodes) {
			if(handlerStarts.contains(idx)) {
				// This bytecode is the first of an exception handler. Such
				// handlers begin with the thrown exception object on the stack,
				// hence we must account for this.
				current = current + 1;
			}			
									
			current = current + b.stackDiff();			
			max = Math.max(current,max);			
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
	 * @author djp
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
			a.write(attrbout, constantPool, loader);
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
				
		writer.write_u2(attributes.size()); // no attributes for now
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
	 * @author djp
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
	 * A Rewriteable attribute is one maps bytecodes to something. For example,
	 * the Exceptions attribute maps bytecodes to exception handler regions;
	 * likewise, the LineNumbers attribute maps bytecodes to source code line
	 * numbers. During bytecode optimisation, the relative position of bytecodes
	 * may change as a result of eliminating redundant bytecodes. In such a case
	 * we need to update those attributes which are affected. This interface
	 * captures those attributes which are affected, and provides a hook to tell
	 * them about rewrites as they happen.
	 * 
	 * @author djp
	 * 
	 */
	public static interface Rewriteable {
		
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
	}
}	
