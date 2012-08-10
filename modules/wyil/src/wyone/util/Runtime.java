package wyone.util;

public class Runtime {
	public static int[] append(int... xs) {
		return xs;
	}
	
	public static int[] append(int x, int[] y) {
		int[] r = new int[y.length + 1];
		System.arraycopy(y, 0, r, 1, y.length);
		r[0] = x;
		return r;
	}
	
	public static int[] append(int[] x, int[] y) {
		int[] r = new int[x.length + y.length];
		System.arraycopy(x, 0, r, 0, x.length);
		System.arraycopy(y, 0, r, x.length, y.length);
		return r;
	}
}
