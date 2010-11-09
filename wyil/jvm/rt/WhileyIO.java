package wyil.jvm.rt;

import java.math.*;
import java.util.*;
import java.io.*;

public class WhileyIO {
	public static WhileyProcess openFile(WhileyList name) {
		WhileyRecord r = new WhileyRecord();
		try {
			String filename = WhileyList.toString(name);
			FileInputStream fin = new FileInputStream(filename);
			r.put("fileName", name);
			r.put("$fin", fin);
			WhileyProcess p = new WhileyProcess(r);
			return p;
		} catch(FileNotFoundException e) {
			r.put("msg", e.getMessage());			
		}
		return null;
	}
	
	public static void closeFile(WhileyProcess p) {
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");
		try {
			fin.close();
		} catch (IOException ioe) {
			// what to do here??
		}
	}
	
	public WhileyList readFile(WhileyProcess p, BigInteger max) {
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");
		
		WhileyList r = new WhileyList();
		byte[] bytes = new byte[max.intValue()];		
		try {
			int nbytes = fin.read(bytes);
			for(int i=0;i!=nbytes;++i) {
				r.add(BigInteger.valueOf(bytes[i]));
			}
			System.out.println("READ: " + nbytes);
		} catch (IOException ioe) {
			// what to do here??
		}
		
		return r;
	}
}
