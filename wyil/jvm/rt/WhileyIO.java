package wyil.jvm.rt;

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
}
