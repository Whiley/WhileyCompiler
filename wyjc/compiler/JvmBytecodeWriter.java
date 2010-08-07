package wyjc.compiler;

import java.io.FileOutputStream;
import java.io.IOException;

import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.util.Logger;
import wyil.io.*;
import wyjvm.io.*;
import wyjvm.lang.ClassFile;

public class JvmBytecodeWriter implements Compiler.Writer {
	private ClassFileBuilder classBuilder;

	public JvmBytecodeWriter(ModuleLoader loader, int whileyMajorVersion,
			int whileyMinorVersion) {
		classBuilder = new ClassFileBuilder(loader, whileyMajorVersion,
				whileyMinorVersion);
	}
		
	public void write(Module m, Logger logout) {
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
	}	
	
}
