package wyjc.util;

import java.io.*;
import java.util.List;

import wybs.util.StdBuildRule;
import wybs.util.StdProject;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.VirtualRoot;
import wyil.lang.WyilFile;
import wyjc.Wyil2JavaBuilder;
import jasm.io.ClassFileReader;
import jasm.io.ClassFileWriter;
import jasm.io.JasmFileWriter;
import jasm.lang.ClassFile;
import jasm.io.ClassFileWriter;

public class WyjcBuildTask extends wyc.util.WycBuildTask {

	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<ClassFile> ContentType = new Content.Type<ClassFile>() {
		public Path.Entry<ClassFile> accept(Path.Entry<?> e) {
			if (e.contentType() == this) {
				return (Path.Entry<ClassFile>) e;
			}
			return null;
		}

		public ClassFile read(Path.Entry<ClassFile> e, InputStream input)
				throws IOException {
			ClassFileReader reader = new ClassFileReader(input);
			return reader.readClass();
		}

		public void write(OutputStream output, ClassFile module)
				throws IOException {
			ClassFileWriter writer = new ClassFileWriter(output);	
			writer.write(module);
		}

		public String toString() {
			return "Content-Type: class";
		}
	};

	// =========================================================================
	// Registry
	// =========================================================================


	public static class Registry extends wyc.util.WycBuildTask.Registry {
		public void associate(Path.Entry e) {
			String suffix = e.suffix();

			if(suffix.equals("class")) {
				e.associate(ContentType, null);
			} else {
				super.associate(e);
			}
		}

		public String suffix(Content.Type<?> t) {
			if(t == ContentType) {
				return "class";
			} else {
				return super.suffix(t);
			}
		}
	}

	/**
	 * The purpose of the class file filter is simply to ensure only binary
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter classFileFilter = new FileFilter() {
		public boolean accept(File f) {
			String name = f.getName();
			return name.endsWith(".class") || f.isDirectory();
		}
	};

	/**
	 * The class directory is the filesystem directory where all generated jvm
	 * class files are stored.
	 */
	protected DirectoryRoot classDir;

	public WyjcBuildTask() {
		super(new Registry());
	}

	@Override
	public void setWhileyDir(File dir) throws IOException {
		// Note, we don't call super.setWhileyDir here as might be expected.
		// This is because that would set the wyilDir to a matching directory
		// root. However, for this builder, we don't want to write wyil files by
		// default.
		this.whileyDir = new DirectoryRoot(dir, whileyFileFilter, registry);
		if (classDir == null) {
			this.classDir = new DirectoryRoot(dir, classFileFilter,
					registry);
		}
	}

	@Override
	public void setWyilDir(File dir) throws IOException {
		super.setWyilDir(dir);
		if (classDir == null) {
			this.classDir = new DirectoryRoot(dir, classFileFilter,
					registry);
		}
	}

	public void setClassDir(File classdir) throws IOException {
		this.classDir = new DirectoryRoot(classdir, classFileFilter,
				registry);
	}

	@Override
	protected void addBuildRules(StdProject project) {
		// Add default build rule for converting whiley files into wyil files.
		super.addBuildRules(project);

		// Now, add build rule for converting wyil files into class files using
		// the Wyil2JavaBuilder.

		Wyil2JavaBuilder jbuilder = new Wyil2JavaBuilder(project);

		if (verbose) {
			jbuilder.setLogger(new Logger.Default(System.err));
		}

		project.add(new StdBuildRule(jbuilder, wyilDir, wyilIncludes,
				wyilExcludes, classDir));
	}

	@Override
	protected List<Path.Entry<?>> getModifiedSourceFiles() throws IOException {
		// First, determine all whiley source files which are out-of-date with
		// respect to their wyil files.
		List<Path.Entry<?>> sources = super.getModifiedSourceFiles();

		// Second, determine all wyil source files which are out-of-date with
		// respect to their class files.
		sources.addAll(super.getModifiedSourceFiles(wyilDir, wyilIncludes,
				classDir, ContentType));

		return sources;
	}

	@Override
	protected void flush() throws IOException {
		super.flush();
		classDir.flush();
	}
}

