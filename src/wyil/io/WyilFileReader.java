package wyil.io;

import java.io.*;
import java.util.ArrayList;

import wybs.lang.Path;
import wyil.lang.*;
import wyjvm.io.BinaryInputStream;
import wyjvm.lang.Constant;

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
	private final ArrayList<String> stringPool = new ArrayList<String>();
	private final ArrayList<Path.ID> pathPool = new ArrayList<Path.ID>();
	private final ArrayList<NameID> namePool = new ArrayList<NameID>();
	private final ArrayList<Value> constantPool = new ArrayList<Value>();
	private final ArrayList<Type> typePool = new ArrayList<Type>();
	
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
		
		int stringPoolSize = input.read_uv();
		int pathPoolSize = input.read_uv();
		int namePoolSize = input.read_uv();
		int constantPoolSize = input.read_uv();
		int typePoolSize = input.read_uv();
				
		int numBlocks = input.read_uv();
		
		readStringPool(stringPoolSize);
		readPathPool(pathPoolSize);
		readNamePool(namePoolSize);
		readConstantPool(constantPoolSize);
		readTypePool(typePoolSize);			
		
		return null;
	}
	
	private void readStringPool(int size) throws IOException {
		System.out.println("STRING POOL");
		System.out.println("=====================");
		stringPool.clear();
		for(int i=0;i!=size;++i) {
			int length = input.read_uv();
			try {
				byte[] data = new byte[length];
				input.read(data);
				String str = new String(data,0,length,"UTF-8");
				System.out.println("#" + i + " = " + str);
				stringPool.add(str);
			} catch(UnsupportedEncodingException e) {
				throw new RuntimeException("UTF-8 Charset not supported?");
			}
		}
	}
	
	private void readPathPool(int size) {
		pathPool.clear();
	}

	private void readNamePool(int size) {
		namePool.clear();
	}

	private void readConstantPool(int size) {
		constantPool.clear();
	}

	private void readTypePool(int size) {
		typePool.clear();
	}
}
