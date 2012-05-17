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
		assertTrue(checkUnsat("l <: [int] && |l| < 0"));
	}
	
	@Test public void Unsat_2() { 
		assertTrue(checkUnsat("l <: [int] && i <: int && l[i] == 0 && l[i] != 0"));
	}
	
	@Test public void Unsat_3() { 
		assertTrue(checkUnsat("l <: [int] && i <: int && l[i] > 0 && l[i] <= 0"));
	}
	
	@Test public void Unsat_4() { 
		assertTrue(checkUnsat("l <: [int] && l==[1.0,2.0,3.0] && |l| < 0"));
	}	 
	
	@Test public void Unsat_5() { 
		assertTrue(checkUnsat("l <: [int] && l==[1.0,2.0,3.0] && 0 <= (-|l|+l[0.0])"));
	}	 
			
	@Test public void Unsat_6() { 
		assertTrue(checkUnsat("l <: [int] && x <: int && y <: int && l==[x,y] && l==[y,x] && x != y"));
	}

	@Test public void Unsat_7() { 
		assertTrue(checkUnsat("l <: [int] && x <: int && y <: int && l==[x,y] && |l| > 2"));
	}	
	
	@Test public void Unsat_8() { 
		assertTrue(checkUnsat("l <: [int] && x <: int && y <: int && z <: int && l==[y,z] && |l| > x && x == 3"));
	}
	
	@Test public void Unsat_9() { 
		assertTrue(checkUnsat("l <: [int] && i <: int && |l| <= 2 && l[i] == 2 && (|l| > 2 || i > 2)"));
	}
	
	@Test public void Unsat_10() {
		assertTrue(checkUnsat("ls <: [int] && i <: int && i < |ls| && 0 <= i && (0 > i+1 || i+1 > |ls|)"));
	}
	
	@Test
	public void Unsat_11() {
		assertTrue(checkUnsat("l <: [int] && x <: int && x && 0 > l[x] && "
				+ "|l|==6 && ([5.0,6.0,7.0][x-3]==l[x] || 3 > x) "
				+ "&& (3 <= x || [1.0,2.0,3.0][x]==l[x])"));
	}
	
	@Test public void Unsat_12() {
		assertTrue(checkUnsat("|r|==|r|+1 && r<:[int]"));
	}
	
	@Test public void Sat_1() { 
		assertTrue(checkSat("l <: [int] && i <: int && |l| == 2 && l[0] == 1 && l[1] == 2"));
	}
	
	@Test public void Sat_2() { 
		assertTrue(checkSat("l <: [int] && i <: int && |l| == 2 && l[0] == 1 && l[1] > 1 && l[1] < 3"));
	}
	
	@Test public void Sat_3() { 
		assertTrue(checkSat("l2 <: [int] && l1 <: [int] && l1==[1.0,2.0,3.0] && l2[2.0]==2.0 && l2[2.0]!=|l1|"));
	}	
	
	@Test public void Sat_4() { 
		assertTrue(checkSat("(arr==[1.0,2.0] || arr==[1.0,2.0,3.0]) && |arr|!=4.0 && arr<:[int]"));
	}			
	
}
