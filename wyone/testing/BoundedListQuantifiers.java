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
		assertTrue(checkUnsat("[int] xs; xs==[0,1,2] && all [ i : xs | xs[i] > 0 ]"));
	}
	
	@Test
	public void Unsat_5() {
		assertTrue(checkUnsat("[int] xs; int i; xs[i]==0 && all [ j : xs | xs[j] > 0 ]"));
	}
	
	@Test
	public void Unsat_6() {
		assertTrue(checkUnsat("[int] xs; some [ i : xs | xs[i] == 0 ] && all [ j : xs | xs[j] > 0 ]"));
	}
	
	@Test
	public void Unsat_7() {
		assertTrue(checkUnsat("int x,i; [int] xs; x == xs[i]" + 
				" && all [ j : xs | xs[j] > 0 ] && x >= 0 && x < 1"));
	}
	
	
	@Test
	public void Sat_1() {
		assertTrue(checkSat("[int] xs; xs==[1,2] && all [ i : xs | xs[i] > 0 ]"));
	}
}
