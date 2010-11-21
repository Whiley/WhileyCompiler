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

public class Types {
	@Test
	public void Unsat_1() {
		assertTrue(checkUnsat("x <: int && x == [1,2,3]"));								
	}
	@Test
	public void Unsat_2() {
		assertTrue(checkUnsat("y <: int && (y <: [int] || y <: bool)"));								
	}
	@Test
	public void Unsat_3() {
		assertTrue(checkUnsat("y <: int &&"
				+ "((x <: int && x < y) || (x <: [int] && |x| > 0)) && "
				+ "x <: int && x == y"));								
	}
	@Test
	public void Unsat_4() {
		assertTrue(checkUnsat("y <: int &&"
				+ "((x <: int && x < y) || (x <: [int] && |x| > 0)) && "
				+ "x <: int && x == 1 && y == 0"));								
	}
	@Test
	public void Unsat_5() {
		assertTrue(checkUnsat("y <: int &&"
				+ "((x <: int && x < y) || (x <: [int] && |x| > 0)) && "
				+ "((x <: int && x == y) || (x <: [int] && |x| == 0))"));								
	}
}
