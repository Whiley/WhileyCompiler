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

import static org.junit.Assert.*;
import static wyone.Main.*;

import org.junit.Test;

public class BoundedSetQuantifiers {
		
	@Test public void Unsat_1() { 
		assertTrue(checkUnsat("x <: int && xs <: {int} && all [ y : xs | y < 0 ] && {x}{=xs && x > 0"));
	}
	
	@Test public void Unsat_2() { 
		assertTrue(checkUnsat("x <: int && y <: int && xs <: {int} && all [ z : xs | z < 0 ]" + 
				" && {x}{=xs && {y}{=xs && x > 0 && y > x"));
	}
	
	@Test public void Unsat_3() { 
		assertTrue(checkUnsat("x <: int && y <: int && xs <: {int} && ys <: {int} && all [ a : xs, b : ys | a < b ]" + 
				" && {1} {= xs && xs {= ys"));
	}
	
	@Test public void Unsat_4() {
		// set union
		assertTrue(checkUnsat("ys <: {int} && xs <: {int} && $0 <: int && zs <: {int} && xs=={1.0,3.0,2.0} &&"
				+ "ys=={3.0,5.0,6.0} && xs{=zs && ys{=zs"
				+ "&& (all [$0 : zs | {$0}{=xs || {$0}{=ys ]) && ys{!=zs"));
	}
	
	@Test public void Unsat_5() {
		// set intersection
		assertTrue(checkUnsat("ys <: {int} && xs <: {int} && $0 <: int && zs <: {int} && xs=={1.0,3.0,2.0} &&"
				+ 
				"ys=={3.0,5.0,6.0} && zs{=xs && zs{=ys" + 
				"&& (all [$0 : zs | {$0}{=xs && {$0}{=ys ]) && zs{!=ys"));
	}
	
	@Test
	public void Unsat_6() {
		assertTrue(checkUnsat("zs <: {int} && y <: int && {1.0,2.0,y} {= zs && 0 > y && "
				+ "all [$0 : zs | {$0} == {1.0} || {$0} == {2.0} ]"));
	}
	
	@Test public void Unsat_7() {		
		assertTrue(checkUnsat("zs <: {int} && xs <: {int} && ys <: {int} && y <: int && xs=={1.0,3.0,2.0} && ys=={3.0,5.0,6.0}" + 
				"&& xs{=zs && ys{=zs && all [$0 : zs | {$0}{=xs || {$0}{=ys ] && {y} {= zs && 0 > y"));
	}
	
	@Test public void Unsat_8() {		
		assertTrue(checkUnsat("zs <: {int} && xs <: {int} && ys <: {int} && xs=={1.0,3.0,2.0} && ys=={3.0,5.0,6.0}" + 
				"&& xs{=zs && ys{=zs && all [$0 : zs | {$0}{=xs || {$0}{=ys ] && some [y: zs | 0 > y]"));
	}
	
	@Test
	public void Unsat_9() {
		assertTrue(checkUnsat("xs <: {int} && xs=={1.0,3.0,2.0} && (some [y : xs | 0 > y])"));
	}
	
	
	@Test
	public void Unsat_10() {
		// force skolemisation
		assertTrue(checkUnsat("xs <: {int} && all [y : xs | 0 < y ] && (some [y : xs | y < 0])"));
	}	
	
	@Test
	public void Unsat_11() {
		assertTrue(checkUnsat("ys <: {int} && xs <: {int} && ys{=xs && (all [$0 : xs | {$0}{=ys ]) && ys!=xs"));
	}	
	
	@Test
	public void Unsat_12() {
		assertTrue(checkUnsat("xs <: {int} && ys <: {int} && ys==xs && some [ z : ys | {z}{!=xs ]"));
	}
	
	@Test
	public void Unsat_13() {
		assertTrue(checkUnsat("xs <: {int} && ys <: {int} && ys==xs && " + 
				"(some [ z : ys | {z}{!=xs ] || some [ z : xs | {z}{!=ys ])"));
	}
	
	@Test
	public void Unsat_14() {
		// test set equality
		assertTrue(checkUnsat("xs <: {int} && ys <: {int} && xs{=ys && " +
				"all [y : ys | {y}{!=ys || {y}{=xs ] && ys!=xs"));
	}
	
	@Test
	public void Unsat_15() {
		// test set equality
		assertTrue(checkUnsat("xs <: {int} && ys <: {int} && zs <: {int} && xs{=ys && ys{=zs &&" + 
				"all [y : ys | {y}{!=zs || {y}{=xs ] && ys!=xs"));
	}
	
	@Test
	public void Unsat_16() {	
		// zs u ys != zs u ys {with some help}
		assertTrue(checkUnsat("xs <: {int} && ys <: {int} && ws <: {int} && xs <: {int} && ys{=ws && zs{=ws && zs{=xs && xs!=ws && ys{=xs && " + 
				"all [$2 : xs | {$2}{=ys || {$2}{=zs ] && " + 
				"all [$4 : ws | {$4}{=ys || {$4}{=zs ] &&" + 
				"(some [ z : xs | {z}{!=ws ] || some [ z : ws | {z}{!=xs ])"));
	}
	
	@Test
	public void Unsat_17() {	
		// zs u ys != zs u ys {without help}
		assertTrue(checkUnsat("xs <: {int} && ys <: {int} && ws <: {int} && xs <: {int} && ys{=ws && zs{=ws && zs{=xs && xs!=ws && ys{=xs && "
				+ 
				"all [$2 : xs | {$2}{=ys || {$2}{=zs ] && " + 
				"all [$4 : ws | {$4}{=ys || {$4}{=zs ]"));
	}
	
	@Test
	public void Unsat_18() {
		// zs n ys != zs n ys {with some help}
		assertTrue(checkUnsat("xs <: {int} && ys <: {int} && ws <: {int} && xs <: {int} && ws{=ys && ws{=zs && xs{=zs && xs{=ys && xs!=ws"
				+ "&& all [$2 : ys | {$2}{=xs || {$2}{!=zs ]"
				+ "&& all [$4 : ys | {$4}{!=zs || {$4}{=ws ]"
				+ "&& all [$2 : xs | {$2}{=ys && {$2}{=zs ]"
				+ "&& all [$4 : ws | {$4}{=ys && {$4}{=zs ]"
				+ "&& (some [ z : xs | {z}{!=ws ] || some [ z : ws | {z}{!=xs ])"));
	}
	
	@Test
	public void Sat_9() {
		assertTrue(checkSat("z <: int && ys <: {int} && xs <: {int} && ys{=xs && {6.0}{=xs" + 
				"&& all [x : xs | {x}{={6.0} || {x}{=ys ]" + 
				"&& {z} {= xs && {z}{!=ys"));
	}
	
	@Test
	public void Sat_10() {
		// slightly harder version of above --- requires skolemisation
		assertTrue(checkSat("ys <: {int} && xs <: {int} && ys{=xs && {6.0}{=xs" + 
				"&& all [x : xs | {x}{={6.0} || {x}{=ys ]" + 
				"&& some [ z : xs | {z}{!=ys ] || some [ z : ys | {z}{!=xs ]"));
	}
	
	@Test
	public void Sat_11() {
		// still harder version of above
		assertTrue(checkSat("ys <: {int} && xs <: {int} && ys{=xs && xs!=ys && {6.0}{=xs" + 
				"&& all [x : xs | {x}{={6.0} || {x}{=ys ]"));
	}
}


