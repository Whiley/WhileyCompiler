// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wyil.jvm.attributes.*;
import wyil.lang.*;
import wyil.util.*;
import wyjvm.io.ClassFileReader;
import wyjvm.lang.*;

/**
 * The module loader is a critical component of the Whiley compiler. It is
 * responsible for finding whiley modules on the WHILEYPATH, and retaining
 * information about them which can be used to compile other whiley files.
 * 
 * @author djp
 * 
 */
public class ModuleLoader {
	/**
	 * The Closed World Assumption indicates whether or not we should attempt to
	 * compile source files that we encouter.
	 */
	private boolean closedWorldAssumption = true;
	
	/**
     * The whiley path is a list of directories which must be
     * searched in ascending order for whiley files.
     */
	private ArrayList<String> whileypath;
	
	/**
     * A map from module names in the form "xxx.yyy" to module objects. This is
     * the master cache of modules which have been loaded during the compilation
     * process. Once a module has been entered into the moduletable, it will not
     * be loaded again.
     */
	private HashMap<ModuleID, Module> moduletable = new HashMap<ModuleID, Module>();  
	
	/**
     * A map from module names in the form "xxx.yyy" to skeleton objects. This
     * is required to permit preregistration of source files during compilation.
     */
	private HashMap<ModuleID, Skeleton> skeletontable = new HashMap<ModuleID, Skeleton>();  
			
	/**
	 * A Package object contains information about a particular package,
	 * including the following information:
	 * 
	 * 1) where it can be found on the file system (either a directory).
	 * 
	 * 2) what modules it contains.
	 * 
	 */
	private static final class Package {
		
		/**
         * The modules field contains the names of those modules contained in
         * this package.
         */
		public final HashSet<String> modules = new HashSet<String>();
				
		/**
		 * The locations list contains the list of locations that have been
		 * identified for a particular package. Each location identifies either
		 * a jar file, or a directory. The order of locations found is
		 * important --- those which come first have higher priority.
		 */
		public final ArrayList<File> locations = new ArrayList<File>();
	}

	/**
	 * A module Skeleton provides basic information regarding what names are
	 * defined within a module. It represents the minimal knowledge regarding a
	 * module that we can have. Skeletons are used early on in the compilation
	 * process to help with name resolution.
	 * 
	 * @author djp
	 * 
	 */
	public abstract static class Skeleton {
		protected final ModuleID mid;

		public Skeleton(ModuleID mid) {
			this.mid = mid;
		}
		
		public ModuleID id() {
			return mid;
		}

		public abstract boolean hasName(String name);
	}
	
	/**
     * The packages map maps each package to its PackageInfo record. This means
     * we can quickly identify packages will have already been loaded.
     */
	private final HashMap<PkgID, Package> packages = new HashMap<PkgID, Package>();
	
	/**
     * The failed packages set is a collection of packages which have been
     * requested, but are known not to exist. The purpose of this cache is
     * simply to speek up package resolution.
     */
	private final HashSet<PkgID> failedPackages = new HashSet<PkgID>();
	
	/**
	 * The logger is used to log messages from the module loader.
	 */
	private Logger logger;
	
	public ModuleLoader(Collection<String> whileypath, Logger logger) {
		this.logger = logger;
		this.whileypath = new ArrayList<String>(whileypath);		
	}
	
	public ModuleLoader(Collection<String> whileypath) {
		this.logger = Logger.NULL;
		this.whileypath = new ArrayList<String>(whileypath);		
	}
	
	public void setClosedWorldAssumption(boolean flag) {
		closedWorldAssumption = flag;
	}
	
	/**
	 * Set the logger for this module loader.
	 * @param logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * This function checks whether the supplied package exists or not.
	 * 
	 * @param pkg
	 *            The package whose existence we want to check for.
	 * 
	 * @return true if the package exists, false otherwise.
	 */
	public boolean isPackage(PkgID pkg) {
		try {
			return resolvePackage(pkg) != null;
		} catch(ResolveError e) {
			return false;
		}
	}	
	
	public void preregister(Skeleton skeleton, String filename) {		
		skeletontable.put(skeleton.id(), skeleton);
		File parent = new File(filename).getParentFile();
		addPackageItem(skeleton.id().pkg(),skeleton.id().module(),parent);								 
	}
	
	public void register(Module module) {			
		moduletable.put(module.id(), module);	
	}
	
	/**
	 * This methods attempts to resolve the correct package for a named item,
	 * given a list of imports. Resolving the correct package may require
	 * loading modules as necessary from the whileypath and/or compiling modules
	 * for which only source code is currently available.
	 * 
	 * @param module
	 *            A module name without package specifier.
	 * @param imports
	 *            A list of packages to search through. Packages are searched in
	 *            order of appearance.
	 * @return The resolved package.
	 * @throws ModuleNotFoundException
	 *             if it couldn't resolve the module
	 */
	public ModuleID resolve(String name, List<PkgID> imports)
			throws ResolveError {									
		for (PkgID pkg : imports) {				
			if(pkg.size() > 0 && pkg.last().equals("*")) {				
				pkg = pkg.subpkg(0, pkg.size()-1);
				if(!isPackage(pkg)) {					
					continue; // sanity check
				}								
				Package p = resolvePackage(pkg);
				
				for (String n : p.modules) {					
					try {
						ModuleID mid = new ModuleID(pkg,n);									
						Skeleton mi = loadSkeleton(mid);					
						if (mi.hasName(name)) {
							return mid;
						} 					
					} catch(ResolveError rex) {
						// ignore. This indicates we simply couldn't resolve
                        // this module. For example, if it wasn't a whiley class
                        // file.						
					}
				}
			} else if(pkg.size() > 0) {
				try {
					String pkgname = pkg.last();
					pkg = pkg.subpkg(0, pkg.size()-1);
					ModuleID mid = new ModuleID(pkg,pkgname);												
					Skeleton mi = loadSkeleton(mid);					
					if (mi.hasName(name)) {
						return mid;
					} 	
				} catch(ResolveError rex) {
					// ignore. This indicates we simply couldn't resolve
					// this module. For example, if it wasn't a whiley class
					// file.
				}
			}
		}
		
		throw new ResolveError("name not found: " + name);
	}
		
	public Module loadModule(ModuleID module) throws ResolveError {		
		Module m = moduletable.get(module);
		if(m != null) {
			return m; // module was previously loaded and cached
		}
			
		// module has not been previously loaded.
		Package pkg = resolvePackage(module.pkg());
		// check for error
		if(pkg == null) {
			throw new ResolveError("Unable to find package: " + module.pkg());
		} else {
			try {
				// ok, now look for module inside package.
				m = loadModule(module,pkg);
				if(m == null) {
					throw new ResolveError("Unable to find module: " + module);
				}
			} catch(IOException io) {
				throw new ResolveError("Unagle to find module: " + module,io);
			}
		}
		
		return m;		
	}
	
	public Skeleton loadSkeleton(ModuleID module) throws ResolveError {
		Skeleton skeleton = skeletontable.get(module);
		if(skeleton != null) {
			return skeleton;
		} 		
		Module m = moduletable.get(module);
		if(m != null) {
			return m; // module was previously loaded and cached
		}
		
		// module has not been previously loaded.
		Package pkg = resolvePackage(module.pkg());
		// check for error
		if(pkg == null) {
			throw new ResolveError("Unable to find package: " + module.pkg());
		} else {
			try {
				// ok, now look for module inside package.
				skeleton = loadSkeleton(module,pkg);
				if(skeleton == null) {
					throw new ResolveError("Unable to find module: " + module);
				}
			} catch(IOException io) {
				throw new ResolveError("Unagle to find module: " + module,io);
			}
		}
		
		return skeleton;		
	}
	
	private Skeleton loadSkeleton(ModuleID module, Package pkg) throws ResolveError, IOException {		
		String filename = module.fileName();	
		String jarname = filename.replace(File.separatorChar,'/') + ".class";

		for(File location : pkg.locations) {			
			if (location.getName().endsWith(".jar")) {
				// location is a jar file
				JarFile jf = new JarFile(location);				
				JarEntry je = jf.getJarEntry(jarname);
				if (je == null) { continue; }  								
				Module mi = readWhileyClass(module, jarname, jf.getInputStream(je));
				if(mi != null) { return mi; }
			} else {
				File classFile = new File(location.getPath(),filename + ".class");					
				File srcFile = new File(location.getPath(),filename + ".whiley");

				if(classFile.exists()) {															
					// Here, there is no sourcefile, but there is a
					// classfile.
					// So, no need to compile --- just load the class file!
					Module mi = readWhileyClass(module, classFile.getPath(), new FileInputStream(classFile));
					if(mi != null) {
						return mi;
					}
				}			
			}
		}

		throw new ResolveError("unable to find module: " + module);
	}

	/**
	 * This method attempts to read a whiley module from a given package.
	 * 
	 * @param module
	 *            The module to load
	 * @param pkgIngo
	 *            Information about the including package, in particular
	 *            where it can be located on the file system.
	 * @return
	 */
	private Module loadModule(ModuleID module, Package pkg)
			throws ResolveError, IOException {			
		String filename = module.fileName();	
		String jarname = filename.replace(File.separatorChar,'/') + ".class";
		
		for(File location : pkg.locations) {
			if (location.getName().endsWith(".jar")) {
				// location is a jar file				
				JarFile jf = new JarFile(location);				
				JarEntry je = jf.getJarEntry(jarname);
				if (je == null) { continue; }  								
				return readWhileyClass(module, jarname, jf.getInputStream(je));
			} else {
				File classFile = new File(location.getPath(),filename + ".class");					
				
				if(classFile.exists()) {															
					// Here, there is no sourcefile, but there is a classfile.
					// So, no need to compile --- just load the class file!
					return readWhileyClass(module, jarname, new FileInputStream(classFile));					
				}			
			}
		}
		
		throw new ResolveError("unable to find module: " + module);
	}
	
	/**
     * This method searches the WHILEYPATH looking for a matching package.
     * 
     * @param pkg --- the package to look for
     * @return
     */
	private Package resolvePackage(PkgID pkg) throws ResolveError {			
		// First, check if we have already resolved this package.						
		Package pkgInfo = packages.get(pkg);				
		
		if(pkgInfo != null) {			
			return pkgInfo;
		} else if(failedPackages.contains(pkg)) {
			// yes, it's already been resolved but it doesn't exist.
			throw new ResolveError("package not found: " + pkg);
		}

		// package has not been previously resolved.
		String filePkg = pkg.fileName();
		
		// Second, try whileypath
		for (String dir : whileypath) {			
			// check if whileypath entry is a jarfile or a directory
			if (!dir.endsWith(".jar")) {
				// dir is not a Jar file, so I assume it's a directory.				
				pkgInfo = lookForPackage(dir,pkg,filePkg);
				if(pkgInfo != null) {					
					packages.put(pkg,pkgInfo);
					return pkgInfo;
				}				
			} else {			
				// this is a jar file
				try {
					JarFile jf = new JarFile(dir);
					for (Enumeration<JarEntry> e = jf.entries(); e
							.hasMoreElements();) {
						JarEntry je = e.nextElement();
						String entryName = je.getName();
						if (entryName.endsWith(".class")) {
							// now strip off ".class"
							entryName = entryName.substring(0, entryName
									.length() - 6);
							entryName = entryName.replace("/", ".");
							// strip off package information
							String pkgName = pathParent(entryName);
							String moduleName = pathChild(entryName);

							// now add to package map
							addPackageItem(new PkgID(pkgName.split("\\.")),
									moduleName, new File(dir));
						}
					}
					
					pkgInfo = packages.get(pkg);
					if(pkgInfo != null) {						
						return pkgInfo;
					}
						
				} catch (IOException e) {
					// jarfile listed on classpath doesn't exist!
					// So, silently ignore it (this is what javac does).
				}
			}
		}
				
		failedPackages.add(pkg);
		throw new ResolveError("package not found: " + pkg);
	}
	
	private Module readWhileyClass(ModuleID module, String filename,
			InputStream input) throws IOException {
		long time = System.currentTimeMillis();		
		
		ClassFileReader r = new ClassFileReader(input,
				new WhileyDefine.Reader(), new WhileyBlock.Reader(
						"WhileyPreCondition"), new WhileyBlock.Reader(
						"WhileyPostCondition"));					

		ClassFile cf = r.readClass();

		Module mi = createModule(module,cf);
		if(mi != null) {
			logger.logTimedMessage("Loaded " + filename, System
					.currentTimeMillis()
					- time);				

			// observe that createModule will return null if the
			// class file is *not* from whiley source file. In such
			// case, we simply ignore the class file altogether.
			moduletable.put(module,mi);
			return mi;
		} else {
			logger.logTimedMessage("Ignored " + filename, System
					.currentTimeMillis()
					- time);	
			return null;
		}
	} 
	
	/**
     * This traverses the directory tree, starting from dir, looking for class
     * or java files. There's probably a bug if the directory tree is cyclic!
     */
	private Package lookForPackage(String root, PkgID pkg, String filepkg) {				
		if(root.equals("")) {
			root = ".";
		}				
		
		File f = new File(root + File.separatorChar + filepkg);				
		if (f.isDirectory()) {			
			for (String file : f.list()) {				
				if (!closedWorldAssumption && file.endsWith(".whiley")) {
					// strip  off ".whiley" to get module name
					String name = file.substring(0,file.length()-7);
					addPackageItem(pkg, name, new File(root));					
				} else if (file.endsWith(".class")) {					
					// strip  off ".class" to get module name
					String name = file.substring(0,file.length()-6);
					addPackageItem(pkg, name, new File(root));					
				}
			}
		}

		return packages.get(pkg);
	}
	
	/**
	 * This adds a module to the module table. 
	 * 
	 * @param pkg
	 *            The name of the module package
	 * @param name
	 *            The name of the module to be added
	 * @param location
	 *            The location of the enclosing package. This is either a jar
	 *            file, or a directory.
	 */
	private void addPackageItem(PkgID pkg, String name, File pkgLocation) {
		Package items = packages.get(pkg);
		if (items == null) {						
			items = new Package();			
			packages.put(pkg, items);
		} 

		// add the class in question
		items.modules.add(name);
		
		// now, add the location (if it wasn't already added)  
		if(!items.locations.contains(pkgLocation)) {			
			items.locations.add(pkgLocation);
		}		

		// Finally, add all enclosing packages of this package as
		// well. Otherwise, isPackage("whiley") can fails even when we know about
		// a particular package.
		for(int i=0;i<pkg.size()-1;++i) {
			PkgID p = pkg.subpkg(0,i);
			if (packages.get(p) == null) {
				packages.put(p, new Package());
			}
		}
	}
	
	protected Module createModule(ModuleID mid, ClassFile cf) {
		if(cf.attribute("WhileyVersion") == null) {
			// This indicates the class is not a WhileyFile. This means it was
			// generate from some other source (e.g. it was a .java file
			// compiled with javac). Hence, we simply want to ignore this file
			// since it obviously doesn't contain any information that we can
			// sensibly use.
			return null;
		}
		
		HashMap<Pair<Type.Fun,String>,Module.Method> methods = new HashMap();
		
		for(ClassFile.Method cm : cf.methods()) {
			if(!cm.isSynthetic()) {
				Module.Method mi = createMethodInfo(mid,cm);
				Pair<Type.Fun,String> key = new Pair(mi.type(),mi.name());
				Module.Method method = methods.get(key);
				if(method != null) {					
					// coalesce cases
					ArrayList<Module.Case> ncases = new ArrayList<Module.Case>(method.cases());
					ncases.addAll(mi.cases());
					mi = new Module.Method(mi.name(), mi.type(), ncases);					
				}
				methods.put(key, mi);
			}
		}
		
		ArrayList<Module.TypeDef> types = new ArrayList();
		ArrayList<Module.ConstDef> constants = new ArrayList();
		
		for(BytecodeAttribute ba : cf.attributes()) {
			if(ba instanceof WhileyDefine) {
				WhileyDefine wd = (WhileyDefine) ba;
				Type type = wd.type();							
				if(type == null) {
					// constant definition
					Module.ConstDef ci = new Module.ConstDef(wd.defName(),wd.value());
					constants.add(ci);
				} else {
					// type definition					
					Module.TypeDef ti = new Module.TypeDef(wd.defName(),type,wd.block());					
					types.add(ti);
				}
			}
		}
				
		return new Module(mid, cf.name(), methods.values(), types, constants);
	}
	
	public String stripCase(String name) {
		int idx = name.indexOf('$');
		if(idx != -1) {
			return name.substring(0,idx);
		} else {
			return name;
		}
	}
	
	protected Module.Method createMethodInfo(ModuleID mid,
			ClassFile.Method cm) {
		Pair<String,Type.Fun> info = splitDescriptor(cm.name());							
		ArrayList<String> parameterNames = new ArrayList<String>();
		Type.Fun type = info.second();
		for (int i = 0; i != type.params.size(); ++i) {
			parameterNames.add("p" + i);
		}
		WhileyBlock precondition = (WhileyBlock) cm.attribute("WhileyPreCondition");
		WhileyBlock postcondition = (WhileyBlock) cm.attribute("WhileyPostCondition");
		Module.Case mcase;
		
		Block preblk = precondition == null ? null : precondition.block();
		Block postblk = postcondition == null ? null : postcondition.block();;
				
		mcase = new Module.Case(parameterNames, preblk,postblk, null);				
		return new Module.Method(stripCase(info.first()), type, mcase);
	}
	
	/**
	 * Given a path string of the form "xxx.yyy.zzz" this returns the parent
	 * component (i.e. "xxx.yyy"). If you supply "xxx", then the path parent is
	 * "". However, the path parent of "" is null.
	 * 
	 * @param pkg
	 * @return
	 */
	private String pathParent(String pkg) {
		if(pkg.equals("")) {
			return null;
		}
		int idx = pkg.lastIndexOf('.');
		if(idx == -1) {
			return "";
		} else {
			return pkg.substring(0,idx);
		}
	}		
	
	/**
	 * Given a path string of the form "xxx.yyy.zzz" this returns the child
	 * component (i.e. "zzz")
	 * 
	 * @param pkg
	 * @return
	 */
	private String pathChild(String pkg) {
		int idx = pkg.lastIndexOf('.');
		if(idx == -1) {
			return pkg;
		} else {
			return pkg.substring(idx+1);
		}
	}
	
	protected Pair<String,Type.Fun> splitDescriptor(String desc) {
		String[] split = desc.split("\\$");		
		String name = split[0];
		Type.Fun ft = new TypeParser(split[split.length - 1]).parseRestFunType();
		if(split.length > 2) {
			Type.Process rec = (Type.Process) new TypeParser(split[1]).parseType();
			ft = Type.T_FUN(rec,ft.ret,ft.params);
		} 
		return new Pair<String,Type.Fun>(name,ft);		
	}
	
	protected class TypeParser {
		private int index;
		private String desc;
		
		public TypeParser(String desc) {
			index = 0;
			this.desc = desc;
		}
		
		public Type parseType() {
			Type.NonUnion type = parseNonUnionType();

			if (index < desc.length() && desc.charAt(index) == '|') {
				ArrayList<Type.NonUnion> types = new ArrayList<Type.NonUnion>();
				types.add(type);
				while (index < desc.length() && desc.charAt(index) == '|') {
					index = index + 1;
					types.add(parseNonUnionType());
				}
				return Type.T_UNION(types);
			}

			return type;
		}
		
		public Type.NonUnion parseNonUnionType() {
			char lookahead = desc.charAt(index++);
			switch (lookahead) {
			case '*':
				return Type.T_ANY;
			case '?':
				return Type.T_EXISTENTIAL;
			case 'V':
				return Type.T_VOID;
			case 'B':
				return Type.T_BOOL;
			case 'I':
				return Type.T_INT;
			case 'R':
				return Type.T_REAL;
			case 'P':
				return Type.T_PROCESS(parseType());
			case 'N':				
				int start = index;
				while(desc.charAt(index) != ';') {
					index++;
				}
				String pkg = desc.substring(start,index);
				index++;
				start = index;
				while(desc.charAt(index) != ';') {
					index++;
				}
				String name = desc.substring(start,index);
				index++;				
				return Type.T_NAMED(ModuleID.fromString(pkg), name,
							parseType());			
			case '[': 
				Type et = parseType();
				lookahead = desc.charAt(index);
				if (lookahead == ']') {
					index++;
					return Type.T_LIST(et);
				}				
				break;
			case '{': 
				et = parseType();
				lookahead = desc.charAt(index);
				if (lookahead == '}') {
					index++;
					return Type.T_SET(et);	
				}	
				break;
			case '(':
				lookahead = desc.charAt(index);
				HashMap<String,Type> types = new HashMap<String,Type>();
				while(lookahead != ')') {
					String n = parseIdentifier();
					index++; // skip colon
					et = parseType();					
					types.put(n,et);
					lookahead = desc.charAt(index);					
				}
				index++; // skip right brace
				return Type.T_TUPLE(types);	
			}
			throw new RuntimeException("invalid type descriptor: "
					+ desc);	
		}
		
		public String parseIdentifier() {
			String r = "";
			char lookahead = desc.charAt(index);
			while(Character.isJavaIdentifierPart(lookahead)) {				
				r += lookahead;				
				index = index + 1;				
				lookahead = desc.charAt(index);				
			}
			return r;
		}
		
		public Type.Fun parseRestFunType() {			
			Type rt = parseType();
			ArrayList<Type> ps = new ArrayList<Type>();
			while(index < desc.length()) {
				ps.add(parseType());				
			}
			return Type.T_FUN(null,rt,ps);
		}
	}
}
