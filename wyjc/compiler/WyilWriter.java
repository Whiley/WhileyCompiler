package wyjc.compiler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.util.Logger;
import wyil.io.*;
import wyjvm.io.*;
import wyjvm.lang.ClassFile;

public class WyilWriter implements Compiler.Stage {	
	
	public WyilWriter(Map<String, String> options) {

	}
	
	public Module process(Module m, Logger logout) {
		long start = System.currentTimeMillis();
				
		// calculate filename
		String filename = m.filename().replace(".whiley", ".wyil");
		try {
			FileOutputStream out = new FileOutputStream(filename);
			WyilFileWriter writer = new WyilFileWriter(out);
			writer.write(m);
			out.flush();
			logout.logTimedMessage("[" + m.filename() + "] wyil file written",
					System.currentTimeMillis() - start);			
		} catch(IOException ex) {
			logout.logTimedMessage("[" + m.filename()
					+ "] failed writing wyil file (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);
		}
		return m;
	}	
	
}
