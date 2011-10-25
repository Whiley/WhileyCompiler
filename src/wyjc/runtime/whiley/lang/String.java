package wyjc.runtime.whiley.lang;

public class String {
	public static java.lang.String str(Object o) {
		if(o == null) {
			return "null";
		} else if(o instanceof java.lang.String) {
			return "\"" + o + "\"";
		} else if(o instanceof java.lang.Character) {
			return "\'" + o + "\'";
		} else if(o instanceof Byte) {
			Byte b = (Byte) o;
			return str((byte)b);
		} else {
			return o.toString();
		}
	}
	
	private static java.lang.String str(byte b) {
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
	
//    if item is null:
//        return "null"
//    else if item is string:
//        return "\"" + item + "\""
//    else if item is char:
//        return "\'" + item + "\'"
//    else if item is byte:
//        extern jvm:
//            iload 0
//            invokestatic wyjc/runtime/Util.str:(B)Ljava/lang/String;
//            areturn
//    extern jvm:
//        aload 0
//        invokevirtual java/lang/Object.toString:()Ljava/lang/String;
//        areturn
//    return "DUMMY" // dead code

}
