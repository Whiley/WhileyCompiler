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
package wycc.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import jbfs.util.ArrayUtils;
import wycc.lang.SyntacticHeap;
import wycc.lang.SyntacticItem;

public abstract class AbstractSyntacticItem implements Comparable<SyntacticItem>, SyntacticItem, Cloneable {
	// Constants;
	private SyntacticHeap parent;
	private int index; // index in the parent
	protected int opcode;
	protected SyntacticItem[] operands;
	protected byte[] data;

	public AbstractSyntacticItem(int opcode) {
		super();
		this.opcode = opcode;
		this.operands = null;
		this.data = null;
	}

	public AbstractSyntacticItem(int opcode, SyntacticItem... operands) {
		this.opcode = opcode;
		this.operands = operands;
		this.data = null;
	}

	protected AbstractSyntacticItem(int opcode, byte[] data, SyntacticItem... operands) {
		this.opcode = opcode;
		this.operands = operands;
		this.data = data;
	}

	@Override
	public SyntacticHeap getHeap() {
		return parent;
	}

	@Override
	public void allocate(SyntacticHeap heap, int index) {
		if(parent != null && parent != heap) {
			throw new IllegalArgumentException(
					"item already allocated to different heap (" + getClass().getName() + ";" + parent + ", " + heap
							+ ")");
		}

		this.parent = heap;
		this.index = index;
	}

	/**
	 * Get the first syntactic item of a given kind which refers to this item.
	 *
	 * @param kind
	 * @return
	 */
	@Override
	public <T extends SyntacticItem> T getParent(Class<T> kind) {
		return parent.getParent(this, kind);
	}

	/**
	 * Get all syntactic items of a given kind which refer to this item.
	 *
	 * @param kind
	 * @return
	 */
	@Override
	public <T extends SyntacticItem> List<T> getParents(Class<T> kind) {
		return parent.getParents(this, kind);
	}
	/**
	 * Get the first syntactic item of a given kind which refers to this item either
	 * indirectly or directly.
	 *
	 * @param kind
	 * @return
	 */
	@Override
	public <T extends SyntacticItem> T getAncestor(Class<T> kind) {
		return parent.getAncestor(this, kind);
	}

	@Override
	public int getOpcode() {
		return opcode;
	}

	@Override
	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}


	@Override
	public int size() {
		if(operands != null) {
			return operands.length;
		} else {
			return 0;
		}
	}

	@Override
	public SyntacticItem get(int i) {
		return operands[i];
	}

	@Override
	public void setOperand(int ith, SyntacticItem child) {
		operands[ith] = child;
	}

	public <T> T[] toArray(Class<T> elementKind) {
		return ArrayUtils.toArray(elementKind, operands);
	}

	@Override
	public int getIndex() {
		if (parent != null) {
			return index;
		} else {
			throw new IllegalArgumentException("SyntacticItem not allocated to heap (" + getClass().getName() + ")");
		}
	}

	@Override
	public SyntacticItem[] getAll() {
		return operands;
	}

	@Override
	public byte[] getData() {
		return data;
	}

	public <S extends SyntacticItem> S match(Class<S> kind) {
		for (int i = 0; i != size(); ++i) {
			SyntacticItem operand = operands[i];
			if (kind.isInstance(operand)) {
				return (S) operand;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		int hash = getOpcode() ^ Arrays.hashCode(operands);
		if (data != null) {
			hash ^= Arrays.hashCode(data);
		}
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractSyntacticItem) {
			AbstractSyntacticItem bo = (AbstractSyntacticItem) o;
			return getOpcode() == bo.getOpcode() && Arrays.equals(operands, bo.operands)
					&& Arrays.equals(data, bo.data);
		}
		return false;
	}

	@Override
	public String toString() {
		String r = Integer.toString(opcode);
		if (operands != null) {
			r += "(";
			for (int i = 0; i != operands.length; ++i) {
				if (i != 0) {
					r += ", ";
				}
				SyntacticItem item = operands[i];
				if(item != null && item.getHeap() != null) {
					r += item.getIndex();
				} else {
					r += "?";
				}
			}
			r += ")";
		}
		if (data != null) {
			r += ":" + data;
		}
		return r;
	}

	@Override
	public int compareTo(SyntacticItem other) {
		int diff = opcode - other.getOpcode();
		if (diff != 0) {
			return diff;
		}
		// We have two items with the same opcode. Need to investigate their
		// structure.
		diff = size() - other.size();
		if (diff != 0) {
			return diff;
		}
		for (int i = 0; i != size(); ++i) {
			SyntacticItem my_ith = get(i);
			SyntacticItem other_ith = other.get(i);
			if (my_ith == null && other_ith == null) {
				// skip
			} else if(my_ith == null) {
				// null is below everything
				return -1;
			} else if(other_ith == null) {
				return 1;
			} else {
				diff = my_ith.compareTo(other_ith);
				if (diff != 0) {
					return diff;
				}
			}
		}
		return compareData(data, other.getData());
	}

	/**
	 * Compare the data object associated with a given syntactic item. An
	 * important question here is what kinds of data are actually permitted. At
	 * this stage, it's not completely clear. However, at least:
	 * <code>Boolean</code>, <code>BigInteger</code>, <code>String</code> and
	 * <code>byte[]</code>.  For simplicity, we'll order them according to this sequence.
	 *
	 * @param leftData
	 * @param rightData
	 * @return
	 */
	private int compareData(Object leftData, Object rightData) {
		if(leftData == null || rightData == null) {
			if(leftData == rightData) {
				return 0;
			} else if(leftData == null) {
				return -1;
			} else {
				return 1;
			}
		}
		// At this point, we have two non-null data items. Therefore, we need to
		// determine whether they refer to the same kind of item or to different
		// kinds. In the latter case, we need to determine the relative ordering
		// of kinds.
		int leftKind = getDataKind(leftData);
		int rightKind = getDataKind(rightData);
		if(leftKind != rightKind) {
			return leftKind - rightKind;
		} else {
			switch(leftKind) {
			case 0: // Boolean
				return ((Boolean)leftData).compareTo((Boolean)rightData);
			case 1: // BigInteger
				return ((BigInteger)leftData).compareTo((BigInteger)rightData);
			case 2: // String
				return ((String)leftData).compareTo((String)rightData);
			default:
				// byte[]
				byte[] leftBytes = (byte[]) leftData;
				byte[] rightBytes = (byte[]) rightData;
				if(leftBytes.length != rightBytes.length) {
					return leftBytes.length - rightBytes.length;
				} else {
					for(int i=0;i!=leftBytes.length;++i) {
						int c = Byte.compare(leftBytes[i], rightBytes[i]);
						if(c != 0) {
							return c;
						}
					}
					//
					return 0;
				}
			}
		}
	}

	private int getDataKind(Object o) {
		if (o instanceof Boolean) {
			return 0;
		} else if (o instanceof BigInteger) {
			return 1;
		} else if (o instanceof String) {
			return 2;
		} else if (o instanceof byte[]) {
			return 3;
		} else {
			throw new IllegalArgumentException("unknown datakind encountered");
		}
	}
}
