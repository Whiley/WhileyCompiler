package whiley.lang;

public class String$native {    
	public static java.lang.String toString(Object o) {
		if(o == null) {
			return "null";
		} else if(o instanceof java.lang.String) {
			return "\"" + o + "\"";
		} else if(o instanceof java.lang.Character) {
			return "\'" + o + "\'";
		} else if(o instanceof Byte) {
			Byte b = (Byte) o;
			return toString((byte)b);
		} else {
			return o.toString();
		}
	}
	
	private static java.lang.String toString(byte b) {
		java.lang.String r = "b";
		byte v = b;
		for(int i=0;i!=8;++i) {
			if((v&0x1) == 1) {
				r = "1" + r;
			} else {
				r = "0" + r;
			}
			v = (byte) (v >>> 1);
		}
		return r;
	} 	  
}
