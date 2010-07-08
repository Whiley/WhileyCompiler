package wyone.testing;

import static wyone.Main.*;
import static org.junit.Assert.*;

import org.junit.*;

public class Types {
	@Test
	public void Unsat_1() {
		assertTrue(checkUnsat("? x;" + "x ~= int && x == [1,2,3]"));								
	}
	@Test
	public void Unsat_2() {
		assertTrue(checkUnsat("? x; int y;"
				+ "(x ~= int && x < y) || (x ~= [int] && |x| > 0) && "
				+ "x ~= int && x == 0"));								
	}
	@Test
	public void Unsat_3() {
		assertTrue(checkUnsat("? x; int y;"
				+ "(x ~= int && x < y) || (x ~= [int] && |x| > 0) && "
				+ "(x ~= int && x == 0) || (x ~= [int] && |x| == 0)"));								
	}
}
