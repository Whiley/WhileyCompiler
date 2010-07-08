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

public class Tuples {
	@Test public void Unsat_1() { 
		assertTrue(checkUnsat("t <: (int x, int y) && t.x > 0 && t.x <= 0"));
	}
	
	@Test public void Unsat_2() { 
		assertTrue(checkUnsat("t <: (int x, int y) && t.x > 0 && t.x+1 <= 1"));
	}
	
	@Test public void Unsat_3() { 
		assertTrue(checkUnsat("t <: (int x, int y) && t.x < t.y && t.y < 0 && 0 < t.x"));
	}
	
	@Test public void Unsat_4() { 
		assertTrue(checkUnsat("t <: (int x, int y) && t.x < t.y && t.y <= 0 && 0 <= t.x"));
	}

	@Test public void Unsat_5() {
		assertTrue(checkUnsat("x <: (int f1,int f2)&& x==(f1:1.0,f2:3.0) && x.f2 <= 2"));		
	}
	
	@Test public void Unsat_6() { 
		assertTrue(checkUnsat("t <: (int x, int y) && t.x != t.y && t.x >= 0 && t.y >= 0 && t.x + t.y <= 0"));
	}
	
	@Test public void Unsat_7() { 
		assertTrue(checkUnsat("t <: (int x, int y) && (t.y == 2 || t.y == -11) && (t.x == 3 || t.x == 10) && t.y >= t.x"));
	}
	
	@Test public void Unsat_8() { 
		assertTrue(checkUnsat("t1 <: (int x, int y) && t2 <: (int x, int y) &&t1.x < 0 && t2.x == t2.y && t2.y > 0 && t1 == t2"));
	}
	
	@Test public void Unsat_9() { 
		assertTrue(checkUnsat("x <: (int f1,int f2) && y <: (int f1,int f2) && " + 
				"y==(f1:1.0,f2:3.0) && x.f1==1 && x.f2==3 && x!=y"));
	}
	
	@Test public void Unsat_10() { 
		assertTrue(checkUnsat("x <: (int f1,int f2) && y <: (int f1,int f2) && z<:int &&" +
				"y==(f1:z,f2:3.0) && x.f1==z && x.f2==3 && x!=y"));
	}
	
	@Test public void Unsat_11() { 
		assertTrue(checkUnsat("x <: ((int f1,int f2) f) && y <: (int f1,int f2) && y==(f1:1.0,f2:3.0) && x.f.f1==1 && x.f.f2==3 && x.f!=y"));
	}
	
	@Test public void Unsat_12() { 
		assertTrue(checkUnsat("x <: (int f1,int f2) && y <: (int f1,int f2) && a <: int && y==(f1:a,f2:3.0) && x==(f1:1,f2:3.0) && x != y && a == 1"));
	}
	
	@Test public void Sat_1() { 
		assertTrue(checkSat("x <: (int f1,int f2) && y <: (int f1,int f2) && y==(f1:1.0,f2:3.0) && x.f1==2.0 && y.f2==x.f2 && x.f1!=x.f2"));
	}
}
