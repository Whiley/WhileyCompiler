package whiley.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import wyjc.runtime.Actor;
import wyjc.runtime.List;
import wyjc.runtime.Record;

public class File$native {
	public static Actor Reader(String filename) {
		Record r = new Record();
		try {			
			FileInputStream fin = new FileInputStream(filename);
			r.put("fileName", filename);
			r.put("$fin", fin);
			Actor p = new Actor(r);
			p.start();
			return p;
		} catch(FileNotFoundException e) {
			r.put("msg", e.getMessage());			
		}
		return null;
	}
	
	public static Actor Writer(String filename) {
		Record r = new Record();
		try {			
			FileOutputStream fout = new FileOutputStream(filename);
			r.put("fileName", filename);
			r.put("$fout", fout);			
			Actor p = new Actor(r);
			p.start();
			return p;
		} catch(FileNotFoundException e) {
			r.put("msg", e.getMessage());	
		}
		return null;
	}
	
	public static void close(Actor p) {
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");		
		try {
			if(fin != null) {
				fin.close();
			} else {
				FileOutputStream fout = (FileOutputStream) ((HashMap) p.state())
				.get("$fout");		
				fout.close();
			}
		} catch (IOException ioe) {
			// what to do here??
		}
	}
	
	public static List read(Actor p, BigInteger max) {		
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");
		
		List r = new List();
		byte[] bytes = new byte[max.intValue()];		
		try {
			int nbytes = fin.read(bytes);
			for(int i=0;i!=nbytes;++i) {				
				r.add(bytes[i]);
			}
		} catch (IOException ioe) {
			// what to do here??
		}
		
		return r;		
	}
	
	private static final int CHUNK_SIZE = 1024;
	public static List read(Actor p) {		
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");
		
		List r = new List();				
		try {
			int nbytes = 0;
			do {
				byte[] bytes = new byte[CHUNK_SIZE];
				nbytes = fin.read(bytes);
				for(int i=0;i!=nbytes;++i) {
					r.add(bytes[i]);
				}
			} while(nbytes == CHUNK_SIZE);			
		} catch (IOException ioe) {
			// what to do here??
		}
		
		return r;		
	}
	
	public static void write(Actor p, List bytes) {
		FileOutputStream fout = (FileOutputStream) ((HashMap) p.state())
				.get("$fout");
				
		try {			
			byte[] bs = new byte[bytes.size()];
			for(int i=0;i!=bs.length;++i) {
				Byte r = (Byte) bytes.get(i); 
				bs[i] = r.byteValue();
			}			
			fout.write(bs);			
		} catch (IOException ioe) {
			// what to do here??
		}		
	}
	
	public static void flush() {
		System.out.flush();
	}
}
