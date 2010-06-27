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

import static wyone.Main.*;
import static org.junit.Assert.*;

import org.junit.*;

public class Lists {
	@Test public void Unsat_1() { 
		assertTrue(checkUnsat("[int] l; |l| < 0"));
	}
	
	@Test public void Unsat_2() { 
		assertTrue(checkUnsat("[int] l; int i; l[i] == 0 && l[i] != 0"));
	}
	
	@Test public void Unsat_3() { 
		assertTrue(checkUnsat("[int] l; int i; l[i] > 0 && l[i] <= 0"));
	}
	
	@Test public void Unsat_4() { 
		assertTrue(checkUnsat("[int] l; l==[1.0,2.0,3.0] && |l| < 0"));
	}	 
	
	@Test public void Unsat_5() { 
		assertTrue(checkUnsat("[int] arr; arr==[1.0,2.0,3.0] && 0 <= (-|arr|+arr[0.0])="));
	}	 
			
	@Test public void Unsat_6() { 
		assertTrue(checkUnsat("[int] l; int x,y; l==[x,y] && l==[y,x] && x != y"));
	}

	@Test public void Unsat_7() { 
		assertTrue(checkUnsat("[int] l; int x,y; l==[x,y] && |l| > 2"));
	}	
	
	@Test public void Unsat_8() { 
		assertTrue(checkUnsat("[int] l; int x,y,z; l==[y,z] && |l| > x && x == 3"));
	}
	
	@Test public void Unsat_9() { 
		assertTrue(checkUnsat("[int] l; int i; |l| <= 2 && l[i] == 2 && (|l| > 2 || i > 2)"));
	}
	
	@Test public void Unsat_10() {
		assertTrue(checkUnsat("[int] ls; int i; i < |ls| && 0 <= i && (0 > i+1 || i+1 > |ls|)"));
	}
	
	@Test
	public void Unsat_11() {
		assertTrue(checkUnsat("[int] result; int x; 0 > result[x] && " + 
				"|result|==6 && ([5.0,6.0,7.0][x-3]==result[x] || 3 > x) " + 
				"&& (3 <= x || [1.0,2.0,3.0][x]==result[x])"));
	}
	
	@Test public void Sat_1() { 
		assertTrue(checkSat("[int] l; int i; |l| == 2 && l[0] == 1 && l[1] == 2"));
	}
	
	@Test public void Sat_2() { 
		assertTrue(checkSat("[int] l; int i; |l| == 2 && l[0] == 1 && l[1] > 1 && l[1] < 3"));
	}
	
	@Test public void Sat_3() { 
		assertTrue(checkSat("[int] arr2; [int] arr1; int $1; arr1==[1.0,2.0,3.0] && arr2[2.0]==2.0 && arr2[2.0]!=|arr1|"));
	}	
	
}
