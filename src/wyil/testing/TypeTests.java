package wyil.testing;

import wyil.lang.Type;
import org.junit.*;
import static org.junit.Assert.*;


public class TypeTests {	
	
	public void checkIsSubtype(String from, String to) {
		Type ft = Type.fromString(from);
		Type tt = Type.fromString(to);
		assertTrue(Type.isSubtype(ft,tt));
	}
}
