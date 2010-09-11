package wyjc.compiler;

import java.io.FileOutputStream;
import java.io.IOException;

import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.util.Logger;
import wyil.io.*;
import wyjvm.util.Validation;

public class JvmBytecodeValidator implements Compiler.Stage {
	private Validation validator;

	public JvmBytecodeValidator() {
		validator = new Validation();
	}
		
	public Module process(Module m, Logger logout) {
		long start = System.currentTimeMillis();
		ClassFile file = classBuilder.build(m);		
		// calculate filename
		String filename = m.filename().replace(".whiley", ".jvm");
		try {
			FileOutputStream out = new FileOutputStream(filename);
			BytecodeFileWriter writer = new BytecodeFileWriter(out,null);
			writer.write(file);
			out.flush();
			logout.logTimedMessage("[" + m.filename() + "] jvm bytecode file written",
					System.currentTimeMillis() - start);
		} catch(IOException ex) {
			logout.logTimedMessage("[" + m.filename()
					+ "] failed writing jvm bytecode file (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);
		}
		return m;
	}	
	
}
