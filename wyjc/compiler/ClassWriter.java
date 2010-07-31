package wyjc.compiler;

import java.io.FileOutputStream;
import java.io.IOException;

import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.util.Logger;
import wyil.io.*;
import wyjvm.io.ClassFileWriter;
import wyjvm.lang.ClassFile;

public class ClassWriter implements Compiler.Writer {
	private ClassFileBuilder classBuilder;

	public ClassWriter(ModuleLoader loader, int whileyMajorVersion,
			int whileyMinorVersion) {
		classBuilder = new ClassFileBuilder(loader, whileyMajorVersion,
				whileyMinorVersion);
	}
		
	public void write(Module m, Logger logout) {
		long start = System.currentTimeMillis();
		ClassFile file = classBuilder.build(m);		
		// calculate filename
		String filename = m.filename().replace(".whiley", ".class");
		try {
			FileOutputStream out = new FileOutputStream(filename);
			ClassFileWriter writer = new ClassFileWriter(out,null);
			writer.write(file);
			logout.logTimedMessage("[" + m.filename() + "] class file written",
					System.currentTimeMillis() - start);
		} catch(IOException ex) {
			logout.logTimedMessage("[" + m.filename()
					+ "] failed writing class file (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);
		}
	}	
	
}
