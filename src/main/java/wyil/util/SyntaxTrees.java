// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.util;

import java.util.Arrays;
import java.util.BitSet;

import wyil.lang.Bytecode;
import wyil.lang.SyntaxTree.Location;

/**
 * Helper functions for working with SyntaxTrees.
 *
 * @author David J. Pearce
 *
 */
public class SyntaxTrees {
	/**
	 * Traverse a syntax tree from one or more roots looking for variable
	 * accesses (uses).
	 *
	 * @param roots
	 * @return
	 */
	public static int[] determineUsedVariables(Location<?>... roots) {
		BitSet usedVariables = new BitSet();
		for(int i=0;i!=roots.length;++i) {
			determineUsedVariables(roots[i],usedVariables);
		}
		int[] vars = new int[usedVariables.cardinality()];
		int index = 0;
		for (int i = usedVariables.nextSetBit(0); i >= 0; i = usedVariables.nextSetBit(i+1)) {
			vars[index++] = i;
		}
		return vars;
	}

	private static void determineUsedVariables(Location<?> root, BitSet usedVariables) {
		switch(root.getOpcode()) {
		case Bytecode.OPCODE_vardecl:
		case Bytecode.OPCODE_vardeclinit:
		case Bytecode.OPCODE_aliasdecl:
			usedVariables.set(root.getIndex());
			return;
		}
		for(int i=0;i!=root.numberOfOperands();++i) {
			determineUsedVariables(root.getOperand(i),usedVariables);
		}
		for(int i=0;i!=root.numberOfOperandGroups();++i) {
			Location<?>[] group = root.getOperandGroup(i);
			for(int j=0;j!=group.length;++j) {
				determineUsedVariables(group[j],usedVariables);
			}
		}
		for(int i=0;i!=root.numberOfBlocks();++i) {
			determineUsedVariables(root.getBlock(i),usedVariables);
		}
		switch(root.getOpcode()) {
		case Bytecode.OPCODE_some:
		case Bytecode.OPCODE_all: {
			Bytecode.Quantifier qf = (Bytecode.Quantifier) root.getBytecode();
			for(Bytecode.Range r : qf.ranges()) {
				// Make sure that no bound variables are captured
				usedVariables.set(r.variable(),false);
			}
		}
		}
	}
}
