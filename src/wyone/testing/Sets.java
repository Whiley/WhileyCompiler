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
		assertTrue(checkUnsat("l <: {int} && l=={1,2,3} && |l| == 2"));
	}
	
	@Test public void Unsat_2() { 
		assertTrue(checkUnsat("y <: int &&  {y} == {1.0} && y < 0"));
	}
	
	@Test public void Unsat_3() { 
		assertTrue(checkUnsat("l <: {int} && x <: int && y <: int && l=={x,y} && |l| == 3 && x > 0 && x < 2 && y > 0 && y < 2"));
	}
	
	@Test public void Unsat_4() { 
		assertTrue(checkUnsat("l <: {int} && x <: int && y <: int && l=={x,y} && |l| == 1 && x > y"));
	}
	
	@Test public void Unsat_5() { 		
		assertTrue(checkUnsat("l1 <: {int} && l2 <: {int} && l1=={2} && l2=={1} && l1 {= l2"));
	}
	
	@Test public void Unsat_6() { 		
		assertTrue(checkUnsat("l1 <: {int} && l2 <: {int} && l1!=l2 && l1{=l2 && l2 {= l1"));
	}	
	
	@Test public void Unsat_7() { 		
		assertTrue(checkUnsat("l1 <: {int} && l2 <: {int} && {1}{=l1 && {2}{=l1 && |l1| == 1"));
	}
	
	@Test public void Unsat_8() { 		
		assertTrue(checkUnsat("l1 <: {int} && l2 <: {int} && x <: int && y <: int && l1=={1} && l2=={x,y} && l1 {= l2 && x < y && (x == 2 || x == 3) && (y == 3 || y == 4)"));
	}
	
	@Test public void Unsat_9() { 		
		assertTrue(checkUnsat("l1 <: {int} && l2 <: {int} && x <: int && y <: int && l1=={1} && l2=={x,y} && l1 {= l2 && x < y && (x == 2 || x == 3)"));
	}
	
	@Test public void Unsat_10() { 		
		assertTrue(checkUnsat("l1 <: {int} && l2 <: {int} && x <: int && y <: int && {1} {= l1 && l1 {= l2 && l2=={2}"));
	}
	
	@Test public void Unsat_11() { 		
		assertTrue(checkUnsat("l1 <: {int} && l2 <: {int} && x <: int && y <: int && l1 {= l2 && |l2| < 2 && |l1| >= 2"));
	}
	
	@Test public void Unsat_12() { 		
		assertTrue(checkUnsat("l1 <: {int} && l2 <: {int} && x <: int && y <: int && l1 {= l2 && l2 {= {1,2,3,4} && |l1| > 4"));
	}
	
	@Test public void Unsat_13() { 		
		assertTrue(checkUnsat("x <: int && y <: int && (x==0.0 || x==255.0) && {y}{={x} && (y < 0 || y > 255)"));
	}
	
	@Test public void Unsat_14() { 		
		assertTrue(checkUnsat("x <: int && y <: int && (x==0.0 || x==255.0) && ({y}{!={x} || y < 0 || y > 255) && ({y}{={x} || {x}{={y})"));
	}

	@Test public void Unsat_15() { 		
		assertTrue(checkUnsat("xs <: {int} && ys <: {int} && y <: int && xs{=ys && {y}{!=ys && {y}{=xs"));
	}	
	
	@Test public void Sat_1() {
		assertTrue(checkSat("xs <: {int} && xs == {1,2,3}"));
	}
	
	@Test public void Sat_2() {
		assertTrue(checkSat("xs <: {int} && xs {= {1,2,3}"));
	}
	
	@Test public void Sat_3() {
		assertTrue(checkSat("xs <: {int} && ys <: {int} && ys {= xs && xs {= {1,2,3}"));
	}
	
	@Test public void Sat_4() {
		assertTrue(checkSat("xs <: {int} && {1,2,3} {= xs"));
	}
	
	@Test public void Sat_5() {
		assertTrue(checkSat("xs <: {int} && ys <: {int} && xs == {1,2,3} && xs {= ys"));
	}
	
	@Test public void Sat_6() {
		assertTrue(checkSat("xs <: {int} && ys <: {int} && xs == {1,2,3} && xs {= ys && ys {= xs"));
	}
	
	@Test public void Sat_7() {
		assertTrue(checkSat("xs <: {int} && x <: int && x > 0 && xs == {x}"));
	}
	
	@Test public void Sat_8() {
		assertTrue(checkSat("xs <: {int} && x <: int && xs == {x}"));
	}
	
	@Test public void Sat_9() {
		assertTrue(checkSat("xs <: {int} && |xs| == 0"));
	}
	
	@Test public void Sat_10() {
		assertTrue(checkSat("xs <: {int} && |xs| == 1"));
	}
	
	@Test public void Sat_11() {
		assertTrue(checkSat("xs <: {int} && |xs| <= 2"));
	}
	
	@Test public void Sat_12() {
		assertTrue(checkSat("x <: int && xs <: {int} && |xs| == 2 && {x} {= xs && x > 4 && x < 6"));
	}
	
	@Test public void Sat_13() {
		assertTrue(checkSat("x <: int && xs <: {int} && |xs| == 2 && {x} {= xs && x > 4"));
	}
	
	@Test public void Sat_14() {
		assertTrue(checkSat("x <: int && y <: int && xs <: {int} && |xs| == 2 && {x,y} {= xs && y < 10 && x > y"));
	}
	
	@Test public void Sat_15() {
		assertTrue(checkSat("x <: int && y <: int && xs <: {int} && |xs| == 2 && {x,y} {= xs && x > y"));
	}
}
