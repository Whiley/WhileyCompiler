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

public class Quantifiers {
	@Test public void Unsat_1() { 
		assertTrue(checkUnsat("bool(int) pos; int x; pos(x) && x < 0 && forall [ int y; !pos(y) || y >= 0]"));
	}
	
	@Test public void Unsat_2() { 
		assertTrue(checkUnsat("bool(int) pos; bool(int,{int}) in; int x; int y; {int} xs; in(x,xs) && pos(x)" + 
				" && in(y,xs) && !pos(y) && forall [ int l; {int} ls; !in(l,ls) || pos(l)]"));
	}
	
	@Test public void Unsat_3() {
		assertTrue(checkUnsat("bool(int,{int},int) field; int f1,f2,i; {int} xs; i==1 && field(f1,xs,i) && field(f2,xs,i) && f1 != f2" 
				+ " && forall [ int v1,i2,v2,l,i1; (!field(v1,l,i1) || !field(v2,l,i2) || 0 != (i2-i1) || 0 == (v1-v2))]"));
	}
	
	@Test public void Unsat_4() { 
		assertTrue(checkUnsat("bool(int,{int}) in; {int} xs; forall [ int l; {int} ls; !in(l,ls) || l > 0 ] && exists [ int l; in(l,xs) && l < 0 ]"));
	}

	@Test public void Unsat_5() {
		// Test matching on expressions.
		assertTrue(checkUnsat("int(int) pos; int x; int y; pos(x+1) && x < 0 && forall [ int y; !pos(y) || y-1 >= 0]"));
	}
	
	/*
	 * this one need triggering on equality I think ...
	@Test public void Unsat_5() {
		assertTrue(checkUnsat("x != y && x == 0 && forall z [ z==y || z!=0 ]"));
	}
	 */
	@Test public void Unsat_6() {
		assertTrue(checkUnsat("bool(int,{int}) in; int a; int b; {int} xs, ys, zs; a == 1 && in(a,xs) && !in(b,xs) && b == 2 && in(b,ys) && in(b,zs) && forall [ int x; !in(x,xs) || in(x,ys)] && forall [ int y; !in(y,ys) || in(y,xs)] && forall [ int z; !in(z,zs) || !in(z,ys) || in(z,xs)]"));
	}
	
	@Test public void Unsat_7() {
		assertTrue(checkUnsat("bool(int,[int],int) get; int j1, j2, x, y; [int] xs, ys; j1 == 1 && j2 == 1 && get(x,xs,j1) && get(y,ys,j2) && xs == ys && x!=y && forall [ int v1,v2,i1,i2,l; !get(v1,l,i1) || !get(v2,l,i2) || i1!=i2 || v1==v2 ]"));
	}
		
	@Test public void Unsat_8() {
		assertTrue(checkUnsat("bool(int,{int},int) field; int $0, $1, $2, $3, x; ($0==2 || $1==4) && field($0,x,0) && field($1,x,0)" + 
				" && field($2,x,0) && field($3,x,0) && (0>$2 || $3>255) && forall [ int v1,i,v2,l; (!field(v1,l,i) || !field(v2,l,i) || v2==v1)]"));
	}
	
	@Test public void Unsat_9() {
		assertTrue(checkUnsat("bool(int,{int}) in;  {int} xs; forall [ int x; !in(x,xs) || x >= 0 ] && exists [ int y; in(y,xs) && y < 0 ]"));
	}		
	
	@Test
	public void Unsat_10() {
		assertTrue(checkUnsat("bool(int,[int],int) get; bool(int,{int}) length; int i, a, b, c, d; [int] arr1,arr2; i==a && length(1,arr1) &&"
				+ "length(a,arr1) && (!length(b,arr1) || length(b,arr2)) && get(i,arr2,0) && "
				+ "get(c,arr2,0) && length(d,arr1) && c!=d &&  get(1,arr1,0) &&"
				+ "forall [ int v1,i,v2,l; (!get(v1,l,i) || !get(v2,l,i) || v2==v1)] &&"
				+ "forall [ int v1,v2,l; (!length(v1,l) || !length(v2,l) || v2==v1)]) &&"
				+ "forall $8,$7,$9 [(!get($7,arr1,$9) || !get($8,arr2,$9) || $9==0 || $7==$8)]"));
	}
	
	@Test
	public void Unsat_11() {
		assertTrue(checkUnsat("bool(int,[int],int) get; bool(int,{int}) length; int i, b, c; [int] arr1,arr2,arr2$2;" +
				"((arr2==arr1 && i!=1) || (arr2$2==arr1 && i==1 && b==arr2)) && length(1,arr2) &&" +
				" length(1,arr2$2) && get(i,b,0) && get(c,arr2,0) && length(1,arr1) && get(1,arr1,0) && c!=1 &&" +
				" forall [ int v1,i,v2,l; (!get(v1,l,i) || !get(v2,l,i) || v2==v1)] &&" +
				" forall [ int $8,$7,$9; (!get($7,arr2$2,$9) || !get($8,arr2,$9) || $9==0 || $7==$8)]"));
	}

	@Test
	public void Unsat_12() {
		assertTrue(checkUnsat("bool(int,{int}) in; int y; {int} xs; in(y,xs) && 0<=y && y<1 &&" + 
				"forall [ int z; !in(z,xs) || z!=0]"));
	}
	
	@Test
	public void Unsat_13() {
		assertTrue(checkUnsat("bool(int,{int}) in; bool(int,[int],int) get; bool(int,{int}) length; int a, b; [int] arr; length(1,arr) && 125>a && a>0 && get(a,arr,0) &&" + 
				"in(b,arr) && (0>b || b>255) && get(b,arr,0) &&" + 
				"forall [ int v1,i,v2,l; (!get(v1,l,i) || !get(v2,l,i) || v2==v1)]"));
	}
	
	@Test
	public void Unsat_14() {
		assertTrue(checkUnsat("bool(int,{int}) in; {int} ls; forall [ int x; (!in(x,ls) || 0<=x)] &&" + 
				"exists [ int x; (in(x,ls) && 0>x)]"));
	} 
	

	@Test public void Unsat_15() {
		assertTrue(checkUnsat("bool(int) f,g; int x; x < 0 && f(g(x)) && forall [ int y; !f(g(y)) || y > 0 ]"));
	}		
	
	@Test public void Unsat_16() {
		// The following is really a triggering problem.
		assertTrue(checkUnsat("int(int) f; int x; f(x-1,x) && forall [ int i; !f(i,i+1) || i > 0 ]"));
	}
		
	@Test
	public void Unsat_17() {
		assertTrue(checkUnsat(
				"bool(int,{int}) in; bool(int,[int],int) get; bool(int,{int}) length; int i,$28,$25,$27,$52,$21,$23,$22,$31,$46; [int] arr1,arr2,arr2$2,arr2$3,$30,$20,$36,$45;"+ 
				"((arr2$3==arr1 && arr2==$45 && arr1==$36 && i==$28) || (arr2==$30 && arr1==$20 && arr2$2==arr1 && i!=$28)) &&"+
				"get($25,$36,$23) && get($27,$36,$25) && $25==2 && length($28,arr1) && $52!=$28 &&"+
				"length($21,$20) && get($23,$36,$22) && $22==0 && $21==3 && get($23,$20,$22) &&"+
				"(length($31,$30) || $21!=3 || !length($31,arr2$2) || $25!=2) && get($21,$30,$25) &&"+
				"(length($46,$45) || $25!=2 || !length($46,arr2$3)) && $27==64 && $23==1 && get(i,$45,$25) &&"+
				"get($25,$20,$23) && get($27,$20,$25) && length($21,$36) && get($52,arr2,$25) &&"+
				"forall [ int i,v; [int] l; (get(v,l,i) || !in(v,l))] &&"+
				"forall [ int i,v; [int] l; (!get(v,l,i) || in(v,l))] &&"+
				"forall [ int v1,v2,l; (!length(v1,l) || !length(v2,l) || v2==v1)] &&"+
				"forall [ int i,s,v; [int] l; (!length(s,l) || !get(v,l,i) || (0<=i && 0<s && i<s))] &&"+
				"forall [ int $49,$48,$50; ($48==$49 || !get($48,arr2$3,$50) || !get($49,$45,$50) || $50==$25)] &&"+
				"forall [ int $34,$35,$33; (!get($33,arr2$2,$35) || $35==$25 || $33==$34 || !get($34,$30,$35))] &&"+
				"forall [ int v1,i2,v2,i1; [int] l; (i1!=i2 || !get(v2,l,i2) || v2==v1 || !get(v1,l,i1))]"));
	}
		
	@Test
	public void Sat_1() {
		assertTrue(checkSat("bool(int,{int}) in; int x; {int} $3; (x==0 || x==256) && forall [ int $5; (!in($5,$3) || $5==x)] && in(x,$3) && (0>x || x>255)"));
	}
	
	@Test
	public void Sat_2() {
		assertTrue(checkSat("bool(int,{int}) in; bool(int,[int],int) get; bool(int,{int}) length; " +
				"int $0,$3,$5,$6,$8,$9,$10,$11,$12,ys; " +
				"forall [ int v1,v2; {int} l; (!length(v1,l) || !length(v2,l) || v2==v1)] && $11<=$9 && " +				
				"forall [ int $2; (in($2,$0) || ($2!=$10 && $2!=$6 && $2!=$5 && $2!=$3))] && length($10,$0) && length($11,$8) && " +
				"forall [ int $2; ($2==$10 || $2==$6 || !in($2,$0) || $2==$5 || $2==$3)] && " +
				"forall [ {int} l,s; int v; (!length(l,s) || 0<l || !in(v,s))] && forall [ int $14; (($14!=$3 && $14!=$6 && $14!=$5) || in($14,$12))] && " +
				"length($9,ys) && forall [ int $7; ((in($7,ys) && in($7,$0)) || !in($7,$8))] && $3==2 && $11<=$10 && $5==1 && $10==4 && " +
				"exists [ int $18; (!in($18,$12) && in($18,$8))] && forall [ int $7; (!in($7,ys) || in($7,$8) || !in($7,$0))] && " +
				"forall [ int $14; ($14==$3 || $14==$6 || $14==$5 || !in($14,$12))] && $6==3 && length($6,$12)"));
	}
}


