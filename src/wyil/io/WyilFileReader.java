package wyil.io;

import java.io.*;

import wyil.lang.*;
import wyjvm.io.BinaryInputStream;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WyilFile object.
 * 
 * @author David J. Pearce
 * 
 */
public class WyilFileReader {
	private static final char[] magic = {'W','Y','I','L','F','I','L','E'};
	
	private final BinaryInputStream input;
	
	public WyilFileReader(String filename) throws IOException {
		input = new BinaryInputStream(new FileInputStream(filename));
	}
	
	
	public WyilFile read() throws IOException {
		
		
		for(int i=0;i!=8;++i) {
			char c = (char) input.read_u1();
			if(magic[i] != c) {
				throw new IllegalArgumentException("invalid magic number");
			}
		}
						
		int majorVersion = input.read_uv();
		int minorVersion = input.read_uv();
		
		System.out.println("VERSION: " + majorVersion + "." + minorVersion);
		
		return null;
	}
}
