package wyil.util;

import java.io.*;

import wyil.lang.Type;
import wyjvm.io.BinaryOutputStream;
import wyautl.io.*;
import wyautl.lang.Automata;
import wyautl.util.*;
import wyautl.util.Generator.Config;
import wyautl.util.Generator.Kind;

public class TypeGenerator implements GenericWriter<Automata> {
	private PrintStream output;
	
	public TypeGenerator(PrintStream output) {
		this.output = output;
	}
	
	public void write(Automata automata) throws IOException {
		Type t = Type.construct(automata);
		if(t != Type.T_VOID) { 
			output.println(t);
			count++;
			if(verbose) {
				System.err.print("\rWrote " + count + " types.");
			}
		}
	}
	
	public void flush() throws IOException {
		output.flush();
	}
	
	public void close() throws IOException {
		output.close();
	}
	
	private static final Config config = new Config() {{		
		RECURSIVE = false;
		SIZE = 2;
		KINDS = new Kind[24];
		KINDS[Type.K_VOID] = new Kind(true,0,0);
		KINDS[Type.K_ANY] = new Kind(true,0,0);		
		KINDS[Type.K_NULL] = new Kind(true,0,0);
		//KINDS[Type.K_BOOL] = new Kind(true,0,0);
		//KINDS[Type.K_BYTE] = new Kind(true,0,0);
		//KINDS[Type.K_CHAR] = new Kind(true,0,0);
		KINDS[Type.K_INT] = new Kind(true,0,0);
		//KINDS[Type.K_RATIONAL] = new Kind(true,0,0);
		//KINDS[Type.K_STRING] = new Kind(true,0,0);
		//KINDS[Type.K_TUPLE] = new Kind(true,2,2);
		//KINDS[Type.K_SET] = new Kind(true,1,1);
		KINDS[Type.K_LIST] = new Kind(true,1,1);
	}};
	
	private static void kindUpdate(int k, Kind kind) {
		if(config.KINDS[k] != null) {
			config.KINDS[k] = kind;
		}
	}

	private static boolean verbose = false;
	private static int count = 0;
	
	public static void main(String[] args) {		
		boolean binary = false;
		GenericWriter<Automata> writer;
		PrintStream out = System.out;
		int minSize = 1;
		int maxSize = config.SIZE;
		
		try {
			int index = 0;
			while(index < args.length) {
				if(args[index].equals("-b")) {
					binary=true;
				} else if(args[index].equals("-o")) {
					String filename = args[++index];
					out = new PrintStream(new FileOutputStream(filename));
				} else if(args[index].equals("-s") || args[index].equals("-size")) {
					String arg = args[++index];
					if(arg.indexOf(':') >= 0) {
						String[] ss = arg.split(":");
						minSize = Integer.parseInt(ss[0]);
						maxSize = Integer.parseInt(ss[1]);
					} else {
						maxSize = Integer.parseInt(arg);						
					}
				} else if(args[index].equals("-v") || args[index].equals("-verbose")) {
					verbose = true;
				} else if(args[index].equals("-m") || args[index].equals("-model")) {
					config.RECURSIVE = false;
					kindUpdate(Type.K_UNION,null);
					kindUpdate(Type.K_INTERSECTION,null);
					kindUpdate(Type.K_NOT,null);
					kindUpdate(Type.K_SET,new Kind(true,0,2));
					kindUpdate(Type.K_LIST,new Kind(true,0,2));
					// could do more
				}
				index++;
			}
			
			if(binary) {
				BinaryOutputStream bos = new BinaryOutputStream(out);
				writer = new BinaryAutomataWriter(bos);
			} else {
				writer = new TypeGenerator(out);
			}				
					
			for(int i=minSize;i<=maxSize;++i) {
				config.SIZE = i;
				Generator.generate(writer,config);				
			}						
			System.err.print("\rWrote " + count + " types.");			
			writer.close();									
		} catch(IOException ex) {
			System.out.println("Exception: " + ex);
		}		
	}
}
