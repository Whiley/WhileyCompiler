// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.testing;

import static org.junit.Assert.assertTrue;
import static wyone.Main.checkUnsat;
import static wyone.Main.checkSat;

import org.junit.Test;

public class BoundedListQuantifiers {
	
	@Test
	public void Unsat_1() {
		assertTrue(checkUnsat("xs <: [int] && xs==[0,1,2] && all [ i : xs | xs[i] > 0 ]"));
	}
	
	@Test
	public void Unsat_5() {
		assertTrue(checkUnsat("xs <: [int] && y <: int && xs[i]==0 && all [ j : xs | xs[j] > 0 ]"));
	}
	
	@Test
	public void Unsat_6() {
		assertTrue(checkUnsat("xs <: [int] && some [ i : xs | xs[i] == 0 ] && all [ j : xs | xs[j] > 0 ]"));
	}
	
	@Test
	public void Unsat_7() {
		assertTrue(checkUnsat("x <: int && y <: int && xs <: [int] && x == xs[i]"
				+ " && all [ j : xs | xs[j] > 0 ] && x >= 0 && x < 1"));
	}
	

	@Test
	public void Unsat_8() {
		assertTrue(checkUnsat("result <: [int] && x <: int && 0 > result[x] && |result|==6 "
				+ "&& all [$3 : result | ([5.0,6.0,7.0][$3-3]==result[$3] || 3 > $3)"
				+ "&& (3 <= $3 || [1.0,2.0,3.0][$3]==result[$3])]"));
	}
	
	@Test
	public void Unsat_9() {
		assertTrue(checkUnsat("left <: [int] && right <: [int] && result <: [int] && " + 
				"some [x : result | 0 > result[x]] && left==[1.0,2.0,3.0]  && right==[5.0,6.0,7.0]" + 
				"&& |result|==(|left|+|right|) " + 
				"&& (all [$3 : result | (right[($3-|left|)]==result[$3] || 0 > ($3-|left|)) && (0 <= ($3-|left|) || left[$3]==result[$3])])"));
						
	}

	@Test
	public void Sat_1() {
		assertTrue(checkSat("xs <: [int] && xs==[1,2] && all [ i : xs | xs[i] > 0 ]"));
	}
	
	@Test
	public void Sat_2() {
		assertTrue(checkSat("all [x : xs | (0 <= (x-|r$4|) || r$4[x]==xs[x]) && (0 > (x-|r$4|) || [ls[i$5]][(x-|r$4|)]==xs[x])]"));
	}	
}
