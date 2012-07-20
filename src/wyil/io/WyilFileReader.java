package wyil.io;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wybs.lang.Path;
import wybs.util.Trie;
import wyil.lang.*;
import wyjc.attributes.WhileyDefine;
import wyjc.attributes.WhileyType;
import wyjc.runtime.BigRational;
import wyjvm.io.BinaryInputStream;
import wyjvm.lang.BytecodeAttribute;
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
		
		// FIXME: problem if more than one block.
		input.read_uv(); // block identifier
		// TODO: read block size
		return readModule();				
	}
	
	private void readStringPool(int size) throws IOException {
		System.out.println("=== TYPES ===");		
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
	
	private void readPathPool(int size) throws IOException {
		System.out.println("=== PATHS ===");
		pathPool.clear();
		for(int i=0;i!=size;++i) {
			int parent = input.read_uv();
			int stringIndex = input.read_uv();
			Path.ID id;
			if(parent == 0) {
				id = Trie.ROOT;
			} else {
				id = pathPool.get(parent-1);
			}
			id = id.append(stringPool.get(stringIndex));
			pathPool.add(id);
			System.out.println("#" + i + " = " + id);
		}
	}

	private void readNamePool(int size) throws IOException {
		System.out.println("=== NAMES ===");
		namePool.clear();
		for(int i=0;i!=size;++i) {
			int kind = input.read_uv();
			int pathIndex = input.read_uv();
			Path.ID id = pathPool.get(pathIndex);
			
			System.out.println("#" + i + " = " + kind + " " + id);
		}
	}

	private void readConstantPool(int size) throws IOException {
		System.out.println("=== CONSTANTS ===");
		constantPool.clear();
		ValueReader bin = new ValueReader(input);
		for(int i=0;i!=size;++i) {
			Value v = bin.read();
			System.out.println("#" + i + " = " + v);
			constantPool.add(v);
		}
	}

	private void readTypePool(int size) throws IOException {
		System.out.println("=== TYPES ===");
		typePool.clear();
		Type.BinaryReader bin = new Type.BinaryReader(input);
		for(int i=0;i!=size;++i) {
			Type t = bin.readType();
			typePool.add(t);
		}
	}
	
	private WyilFile readModule() throws IOException {
		int nameIdx = input.read_uv();
		int numBlocks = input.read_uv();
		List<WyilFile.Declaration> declarations = new ArrayList<WyilFile.Declaration>();
		for(int i=0;i!=numBlocks;++i) {
			declarations.add(readModuleBlock());			
		}
		return new WyilFile(pathPool.get(nameIdx),"unknown.whiley",declarations);
	}
	
	private WyilFile.Declaration readModuleBlock() throws IOException {
		int kind = input.read_uv();
		// TODO: read block size
		switch(kind) {
			case WyilFileWriter.BLOCK_constant:
				return readConstantBlock();
			case WyilFileWriter.BLOCK_type:
				return readTypeBlock();
			case WyilFileWriter.BLOCK_function:
				return readFunctionBlock();
			case WyilFileWriter.BLOCK_method:
				return readMethodBlock();				
			default:
				throw new RuntimeException("unknown module block encountered");
		}
	}
	
	private WyilFile.ConstantDeclaration readConstantBlock() throws IOException {
		int nameIdx = input.read_uv();
		int constantIdx = input.read_uv();
		int nBlocks = input.read_uv(); // unused
		return new WyilFile.ConstantDeclaration(Collections.EMPTY_LIST,
				stringPool.get(nameIdx), constantPool.get(constantIdx));
	}
	
	private WyilFile.TypeDeclaration readTypeBlock() throws IOException {		
		int nameIdx = input.read_uv();
		int typeIdx = input.read_uv();
		int nBlocks = input.read_uv();
		Block constraint = null;
		if(nBlocks != 0) {
			constraint = readCodeBlock();
		}
		return new WyilFile.TypeDeclaration(Collections.EMPTY_LIST,
				stringPool.get(nameIdx), typePool.get(typeIdx), constraint);
	}
	
	private WyilFile.MethodDeclaration readFunctionBlock() throws IOException {		
		int nameIdx = input.read_uv();
		int typeIdx = input.read_uv();
		int nBlocks = input.read_uv();
		Block constraint = null;
		if(nBlocks != 0) {
			constraint = readCodeBlock();
		}
		return new WyilFile.MethodDeclaration(Collections.EMPTY_LIST,
				stringPool.get(nameIdx), typePool.get(typeIdx), constraint);
	}
	
	private Block readCodeBlock() throws IOException {
		return new Block(0);		
	}
	
	private WyilFile.MethodDeclaration readFunctionBlock() throws IOException {
		return null;
	}
	
	private WyilFile.MethodDeclaration readMethodBlock() throws IOException {
		return null;
	}
	
	public final class ValueReader  {		
		private BinaryInputStream reader;
		
		public ValueReader(BinaryInputStream input) {
			this.reader = input;	
		}
		
		public Value read() throws IOException {		
			int code = reader.read_u1();				
			switch (code) {			
			case WyilFileWriter.CONSTANT_null:
				return Value.V_NULL;
			case WyilFileWriter.CONSTANT_false:
				return Value.V_BOOL(false);
			case WyilFileWriter.CONSTANT_true:
				return Value.V_BOOL(true);				
			case WyilFileWriter.CONSTANT_byte:			
			{
				byte val = (byte) reader.read_u1();				
				return Value.V_BYTE(val);
			}
			case WyilFileWriter.CONSTANT_char:			
			{
				char val = (char) reader.read_u2();				
				return Value.V_CHAR(val);
			}
			case WyilFileWriter.CONSTANT_int:			
			{
				int len = reader.read_u2();				
				byte[] bytes = new byte[len];
				reader.read(bytes);
				BigInteger bi = new BigInteger(bytes);
				return Value.V_INTEGER(bi);
			}
			case WyilFileWriter.CONSTANT_real:			
			{
				int len = reader.read_u2();
				byte[] bytes = new byte[len];
				reader.read(bytes);
				BigInteger num = new BigInteger(bytes);
				len = reader.read_u2();
				bytes = new byte[len];
				reader.read(bytes);
				BigInteger den = new BigInteger(bytes);
				BigRational br = new BigRational(num,den);
				return Value.V_RATIONAL(br);
			}
			case WyilFileWriter.CONSTANT_string:
			{
				int len = reader.read_u2();
				StringBuffer sb = new StringBuffer();
				for(int i=0;i!=len;++i) {
					char c = (char) reader.read_u2();
					sb.append(c);
				}
				return Value.V_STRING(sb.toString());
			}
			case WyilFileWriter.CONSTANT_list:
			{
				int len = reader.read_u2();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) read());
				}
				return Value.V_LIST(values);
			}
			case WyilFileWriter.CONSTANT_set:
			{
				int len = reader.read_u2();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) read());
				}
				return Value.V_SET(values);
			}
			case WyilFileWriter.CONSTANT_tuple:
			{
				int len = reader.read_u2();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) read());
				}
				return Value.V_TUPLE(values);
			}
			case WyilFileWriter.CONSTANT_record:
			{
				int len = reader.read_u2();
				HashMap<String,Value> tvs = new HashMap<String,Value>();
				for(int i=0;i!=len;++i) {
					int idx = reader.read_u2();
					String str = stringPool.get(idx);
					Value lhs = (Value) read();
					tvs.put(str, lhs);
				}
				return Value.V_RECORD(tvs);
			}			
			}
			throw new RuntimeException("Unknown Value encountered in WhileyDefine: " + code);
		}
	}	
	
}
