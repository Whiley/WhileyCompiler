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
