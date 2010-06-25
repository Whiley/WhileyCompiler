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

public class Sets {
	@Test public void Unsat_1() { 
		assertTrue(checkUnsat("{int} l; l=={1,2,3} && |l| == 2"));
	}
	
	@Test public void Unsat_2() { 
		assertTrue(checkUnsat("int y;  {y} == {1.0} && y < 0"));
	}
	
	@Test public void Unsat_3() { 
		assertTrue(checkUnsat("{int} l; int x,y; l=={x,y} && |l| == 3 && x > 0 && x < 2 && y > 0 && y < 2"));
	}
	
	@Test public void Unsat_4() { 
		assertTrue(checkUnsat("{int} l; int x,y; l=={x,y} && |l| == 1 && x > y"));
	}
	
	@Test public void Unsat_5() { 		
		assertTrue(checkUnsat("{int} l1,l2; l1=={2} && l2=={1} && l1 {= l2"));
	}
	
	@Test public void Unsat_6() { 		
		assertTrue(checkUnsat("{int} l1,l2; l1!=l2 && l1{=l2 && l2 {= l1"));
	}	
	
	@Test public void Unsat_7() { 		
		assertTrue(checkUnsat("{int} l1,l2; {1}{=l1 && {2}{=l1 && |l1| == 1"));
	}
	
	@Test public void Unsat_8() { 		
		assertTrue(checkUnsat("{int} l1,l2; int x,y; l1=={1} && l2=={x,y} && l1 {= l2 && x < y && (x == 2 || x == 3) && (y == 3 || y == 4)"));
	}
	
	@Test public void Unsat_9() { 		
		assertTrue(checkUnsat("{int} l1,l2; int x,y; l1=={1} && l2=={x,y} && l1 {= l2 && x < y && (x == 2 || x == 3)"));
	}
	
	@Test public void Unsat_10() { 		
		assertTrue(checkUnsat("{int} l1,l2; int x,y; {1} {= l1 && l1 {= l2 && l2=={2}"));
	}
	
	@Test public void Unsat_11() { 		
		assertTrue(checkUnsat("{int} l1,l2; int x,y; l1 {= l2 && |l2| < 2 && |l1| >= 2"));
	}
	
	@Test public void Unsat_12() { 		
		assertTrue(checkUnsat("{int} l1,l2; int x,y; l1 {= l2 && l2 {= {1,2,3,4} && |l1| > 4"));
	}
	
	@Test public void Unsat_13() { 		
		assertTrue(checkUnsat("int x,$1; (x==0.0 || x==255.0) && {$1}{={x} && ($1 < 0 || $1 > 255)"));
	}
	
	@Test public void Unsat_14() { 		
		assertTrue(checkUnsat("int x,$1; (x==0.0 || x==255.0) && ({$1}{!={x} || $1 < 0 || $1 > 255) && ({$1}{={x} || {x}{={$1})"));
	}

	@Test public void Unsat_15() { 		
		assertTrue(checkUnsat("{int} xs,ys; int y; xs{=ys && {y}{!=ys && {y}{=xs"));
	}	
}
