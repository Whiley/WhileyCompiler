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

public class IntegerArithmetic {

	/*
	 * ============= UNSAT TESTS ==============
	 */
	
	@Test public void Unsat_1() { 
		assertTrue(checkUnsat("x <: int && x > 0 && x <= 0"));
	}
	
	@Test public void Unsat_2() { 
		assertTrue(checkUnsat("x <: int && x > 0 && x+1 <= 1"));
	}
	
	@Test public void Unsat_3() { 
		assertTrue(checkUnsat("x <: int && y <: int && x < y && y < 0 && 0 < x"));
	}
	
	@Test public void Unsat_4() { 
		assertTrue(checkUnsat("x <: int && y <: int && x < y && y <= 0 && 0 <= x"));
	}
	
	@Test public void Unsat_5() { 
		assertTrue(checkUnsat("x <: int && y <: int && x != y && x >= 0 && y >= 0 && x + y <= 0"));
	}
	
	@Test public void Unsat_6() { 
		assertTrue(checkUnsat("x <: int && y <: int && y == 2 * x && x == 5 && y < 10"));
	}
	
	@Test public void Unsat_7() { 
		assertTrue(checkUnsat("x <: int && y <: int && (y == 2 || y == -11) && (x == 3 || x == 10) && y >= x"));
	}
	
	@Test public void Unsat_8() { 
		assertTrue(checkUnsat("x <: int && y <: int && y == 2*x && (x == 3 || x == 10) && y < x"));
	}
	
	@Test public void Unsat_9() { 
		assertTrue(checkUnsat("x <: int && 1 < 2*x && x < 1"));
	}
	
	@Test public void Unsat_9a() { 
		assertTrue(checkUnsat("x <: int && x > 1 && x < 2"));
	}
		
	@Test public void Unsat_10() { 
		assertTrue(checkUnsat("x <: int && 1 < -2*x && x >= 0"));
	}
		
	@Test public void Unsat_11() { 
		assertTrue(checkUnsat("x <: int && 1 < 2*x && x < 2 && x != 1"));
	}
	
	@Test public void Unsat_11b() { 
		assertTrue(checkUnsat("x <: int && y <: int && x+y == 1 && x > 0 && y > 0"));
	}
	
	@Test public void Unsat_11c() { 
		assertTrue(checkUnsat("x <: int && y <: int && x+y == 2 && 2*x == y && x > 0 && y > 0"));
	}
	
	@Test public void Unsat_12() { 
		// this should fail because both x and y are assumed to be integer
		// variables... 
		assertTrue(checkUnsat("x <: int && y <: int && y == 2*x && y == 5"));
	}
	
	@Ignore("Known Bug") @Test public void Unsat_13() { 				
		assertTrue(checkUnsat("x <: int && y <: int && 1 == 3*x + 6*y"));
	}
	
	@Ignore("Known Bug") @Test public void Unsat_14() { 
		assertTrue(checkUnsat("x <: int && y <: int && z <: int && 1 == 3*x + 6*y + z && z == 2"));
	}
	
	@Ignore("Known Bug") @Test public void Unsat_15() {
		assertTrue(checkUnsat("x <: int && y <: int && 1 == 3*x + 3*y  || 2 == 3*x + 3*y"));
	}
	
	@Ignore("Known Bug") @Test public void Unsat_16() {
		assertTrue(checkUnsat("x <: int && y <: int && 1 <= 3*x + 3*y  || 2 >= 3*x + 3*y"));
	}
	
	@Test public void Unsat_17() {
		assertTrue(checkUnsat("a <: int && q <: int && r <: int && a == 5*q + r && r >= 0 && r < 5 && a == 10002 && r <= 1"));
	}
	
	@Ignore("Known Bug") @Test public void Unsat_18() {
		assertTrue(checkUnsat("x <: int && y <: int && (3*x + 3*y == 1) || (3*x + 6*y + 2*z == 1 && z == 2)"));
	}
	
	@Test public void Unsat_19() {
		assertTrue(checkUnsat("x <: int && y <: int && z <: int && z<=0 && 0<=y && 0<=x && x<y && z==(y+x)"));
	}
	
	@Test public void Unsat_20() { 
		assertTrue(checkUnsat("x <: int && y <: int && z <: int && x==y && 0<=x && z<=0 && 0<=y && z==1"));
	}
	
	@Test public void Unsat_21() { 
		assertTrue(checkUnsat("x <: int && y <: int && z <: int && z==(x+y) && x!=y && 0<=x && z<=0 && 0<=y"));
	}
	
	@Test
	public void Unsat_22() {
		assertTrue(checkUnsat("x <: int && y <: int && a <: bool && b <: bool && c <: bool &&" +
				"(0<=x && 0<=y && (!a || b) && (a || !b) && (c || a) && (!c || !a) && (!b || x==y) && (x!=y || b) && c && x+y<=0)"));
	}
	
	@Test public void Unsat_24() { 
		assertTrue(checkUnsat("f(y) <: int && f(x) <: int && x <: int && y <: int && f(y) == 2*f(x) && f(y) == 5"));
	}
	
	@Test public void Unsat_25() {
		// this one is an interesting challenge, because there is no integer
		// value for y. 
		assertTrue(checkUnsat("x <: int && y <: int && x < y && y < x+1"));
	}
	
	@Test public void Unsat_26() { 		
		assertTrue(checkUnsat("x <: [int] && i <: int && j <: int && " + 
				"0 > i && ((0 <= i && 0 > -|x| && 0 > (-|x|+i)) || ((0 > j || 0 <= (-|x|+j)) && i==0.0 && 0 > -|x|))"));
	}			
	
	@Test public void Unsat_27() {
		assertTrue(checkUnsat("2*x <: int && z==2*x && z>0 && z<1"));
	}
	
	@Test public void Unsat_28() {
		// This one is a real challenge I think
		assertTrue(checkUnsat("2*x <: int && z==4*x && z>0 && z<1"));
	}
	
	/*
	 * ============= SAT TESTS ==============
	 */
	
	@Test public void Sat_1() { 
		assertTrue(checkSat("x <: int && x >= 0 && x <= 0"));
	}
	
	@Test public void Sat_2() { 
		assertTrue(checkSat("x <: int && x > 0 && x-1 <= 1"));
	}
	
	@Test public void Sat_3() { 
		assertTrue(checkSat("x <: int && y <: int && y < x && y < 0 && 0 < x"));
	}
	
	@Test public void Sat_4() { 
		assertTrue(checkSat("x <: int && y <: int && y < x && y <= 0 && 0 <= x"));
	}
	
	@Test public void Sat_5() { 
		assertTrue(checkSat("x <: int && y <: int && x != y && x >= 0 && y >= 0 && x + y <= 1"));
	}
	
	@Test public void Sat_6a() { 
		assertTrue(checkSat("x <: int && y <: int && x == 2 * y && x == 6 && y < 10"));
	}
	
	@Test public void Sat_6b() { 
		assertTrue(checkSat("x <: int && y <: int && x == 2 * y && x == 6 && y == 3"));
	}
	
	@Test public void Sat_7() { 
		assertTrue(checkSat("x <: int && y <: int && (y == 2 || y == -11) && (x == 3 || x == 10) && x > y"));
	}
	
	@Test public void Sat_8() { 
		assertTrue(checkSat("x <: int && y <: int && y == 2*x && (x == 3 || x == 10) && x <= y"));
	}
	
	// Need better model generation for this.
	@Ignore("Known Bug") @Test public void Sat_9() { 
		assertTrue(checkSat("x <: int && y <: int && z <: int && 1 == 3*x + 6*y + z && z == 1"));
	}
	
	@Test public void Sat_10() {
		assertTrue(checkSat("x <: int && x<=5 && 10<=2*x"));
	}
	
	@Test public void Sat_11() {
		assertTrue(checkSat("10<=x && y==z && x<:int && y<:int && x==2*y && z<:int && z<=5"));
	}
	
}