package wyjc.runtime;

import java.math.BigInteger;

public class WyByte implements Comparable<WyByte> {
	private byte value;

	private WyByte(byte value) {		
		this.value = value;
	}

	public byte value() {
		return value;
	}
	
	public WyByte and(WyByte b) {
		return WyByte.valueOf((byte) (value & b.value));
	}
	
	public WyByte or(WyByte b) {
		return WyByte.valueOf((byte) (value | b.value));
	}
	
	public WyByte xor(WyByte b) {
		return WyByte.valueOf((byte) (value ^ b.value));
	}

	public WyByte leftShift(BigInteger i) {
		byte b = (byte) ((value&0xFF) << i.intValue());
		return WyByte.valueOf(b);
	}
	
	public WyByte rightShift(BigInteger i) {
		byte b = (byte) ((value&0xFF) >>> i.intValue());
		return WyByte.valueOf(b);
	}
	
	
	
	public WyByte compliment() {
		return valueOf((byte) ~value);
	}

	
	@Override
	public int compareTo(WyByte o) {
		if(value < o.value) { 
			return -1;
		} else if(value > o.value) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof WyByte) {
			WyByte b = (WyByte) o;
			return value == b.value;
		}
		return false;
	}

	public int hashCode() {
		return value;
	}
	
	public String toString() {
		String r = "b";
		byte v = (byte) value;
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

	public static WyByte valueOf(byte val) {
		return new WyByte(val);
	}
}
