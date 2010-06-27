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

package wyjc;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wyjc.ast.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.types.*;
import wyjc.jvm.attributes.WhileyCondition;
import wyjc.jvm.attributes.WhileyDefine;
import wyjc.util.*;
import wyjvm.attributes.*;
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
	private HashMap<ModuleID, ModuleInfo> moduletable = new HashMap<ModuleID, ModuleInfo>();  
	
	/**
     * A map from module names in the form "xxx.yyy" to skeleton objects. This
     * is required to permit preregistration of source files during compilation.
     */
	private HashMap<ModuleID, SkeletonInfo> skeletontable = new HashMap<ModuleID, SkeletonInfo>();  
			
	/**
	 * A PackageInfo object contains information about a particular package,
	 * including the following information:
	 * 
	 * 1) where it can be found on the file system (either a directory).
	 * 
	 * 2) what modules it contains.
	 * 
	 */
	private final class PackageInfo {
		
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
     * The packages map maps each package to its PackageInfo record. This means
     * we can quickly identify packages will have already been loaded.
     */
	private final HashMap<PkgID, PackageInfo> packages = new HashMap<PkgID, PackageInfo>();
	
	/**
     * The failed packages set is a collection of packages which have been
     * requested, but are known not to exist. The purpose of this cache is
     * simply to speek up package resolution.
     */
	private final HashSet<PkgID> failedPackages = new HashSet<PkgID>();
	
	/**
     * The compiler is needed to actually load and parse files from the file
     * system.
     */
	private Compiler compiler;
	
	public ModuleLoader(Collection<String> whileypath) {
		this.whileypath = new ArrayList<String>(whileypath);		
	}
	
	public void setCompiler(Compiler compiler) {
		this.compiler = compiler;
	}
	
	public void setClosedWorldAssumption(boolean flag) {
		closedWorldAssumption = flag;
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
	
	public void preregister(SkeletonInfo skeleton, String filename) {		
		skeletontable.put(skeleton.id(), skeleton);
		int index = filename.lastIndexOf(File.separatorChar);		
		if(index != -1) {			
			File parent = new File(filename).getParentFile();
			addPackageItem(skeleton.id().pkg(),skeleton.id().module(),parent);						
		}
	}
	
	public void register(ModuleInfo module) {			
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
				PackageInfo p = resolvePackage(pkg);
				
				for (String n : p.modules) {
					try {
						ModuleID mid = new ModuleID(pkg,n);									
						SkeletonInfo mi = loadSkeleton(mid);					
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
					SkeletonInfo mi = loadSkeleton(mid);					
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
		
	public static Condition dummyCondition = new BoolVal(true);
	public static Condition nullCondition = new BoolVal(true);
		
	public ModuleInfo loadModule(ModuleID module) throws ResolveError {		
		ModuleInfo m = moduletable.get(module);
		if(m != null) {
			return m; // module was previously loaded and cached
		}
			
		// module has not been previously loaded.
		PackageInfo pkg = resolvePackage(module.pkg());
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
	
	public SkeletonInfo loadSkeleton(ModuleID module) throws ResolveError {
		SkeletonInfo skeleton = skeletontable.get(module);
		if(skeleton != null) {
			return skeleton;
		} 		
		ModuleInfo m = moduletable.get(module);
		if(m != null) {
			return m; // module was previously loaded and cached
		}
		
		// module has not been previously loaded.
		PackageInfo pkg = resolvePackage(module.pkg());
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
	
	private SkeletonInfo loadSkeleton(ModuleID module, PackageInfo pkg) throws ResolveError, IOException {		
		String filename = module.fileName();	
		String jarname = filename.replace(File.separatorChar,'/') + ".class";

		for(File location : pkg.locations) {			
			if (location.getName().endsWith(".jar")) {
				// location is a jar file
				JarFile jf = new JarFile(location);				
				JarEntry je = jf.getJarEntry(jarname);
				if (je == null) { continue; }  								
				ModuleInfo mi = readWhileyClass(module, jarname, jf.getInputStream(je));
				if(mi != null) { return mi; }
			} else {
				File classFile = new File(location.getPath(),filename + ".class");					
				File srcFile = new File(location.getPath(),filename + ".whiley");

				if (!closedWorldAssumption
						&& srcFile.exists()
						&& (!classFile.exists() || classFile.lastModified() < srcFile
								.lastModified())) {
					// Here, there is a source file, and either there is no
					// class
					// file, or the class file is older than the source
					// file.
					// Therefore, we need to (re)compile the source file.
					UnresolvedWhileyFile wf = compiler.parse(srcFile);
					SkeletonInfo si = wf.skeletonInfo();
					// register fact that we've loaded a module
					skeletontable.put(module,si);
					return si;					
				} else if(classFile.exists()) {															
					// Here, there is no sourcefile, but there is a
					// classfile.
					// So, no need to compile --- just load the class file!
					ModuleInfo mi = readWhileyClass(module, classFile.getPath(), new FileInputStream(classFile));
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
	private ModuleInfo loadModule(ModuleID module, PackageInfo pkg)
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
				File srcFile = new File(location.getPath(),filename + ".whiley");
				
				if (!closedWorldAssumption && srcFile.exists()						
						&& (!classFile.exists() || classFile.lastModified() < srcFile
								.lastModified())) {
					// Here, there is a source file, and either there is no class
					// file, or the class file is older than the source file.
					// Therefore, we need to (re)compile the source file.
					UnresolvedWhileyFile wf = compiler.parse(srcFile);
					// register fact that we've loaded a module
					// moduletable.put(module,wf);
					// return wf;
					break;
				} else if(classFile.exists()) {															
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
	private PackageInfo resolvePackage(PkgID pkg) throws ResolveError {			
		// First, check if we have already resolved this package.						
		PackageInfo pkgInfo = packages.get(pkg);
		
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
	
	private ModuleInfo readWhileyClass(ModuleID module, String filename,
			InputStream input) throws IOException {
		long time = System.currentTimeMillis();

		ClassFileReader r = new ClassFileReader(
				input,
				new WhileyCondition.Reader("WhileyPreCondition"),
				new WhileyCondition.Reader("WhileyPostCondition"),
				new WhileyDefine.Reader()
		);					

		ClassFile cf = r.readClass();

		ModuleInfo mi = createModuleInfo(module,cf);
		if(mi != null) {
			compiler.logTimedMessage("Loaded " + filename, System
					.currentTimeMillis()
					- time);				

			// observe that createModuleInfo will return null if the
			// class file is *not* from whiley source file. In such
			// case, we simply ignore the class file altogether.
			moduletable.put(module,mi);
			return mi;
		} else {
			compiler.logTimedMessage("Ignored " + filename, System
					.currentTimeMillis()
					- time);	
			return null;
		}
	} 
	
	/**
     * This traverses the directory tree, starting from dir, looking for class
     * or java files. There's probably a bug if the directory tree is cyclic!
     */
	private PackageInfo lookForPackage(String root, PkgID pkg, String filepkg) {		
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
		PackageInfo items = packages.get(pkg);
		if (items == null) {						
			items = new PackageInfo();			
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
				packages.put(p, new PackageInfo());
			}
		}
	}
	
	protected ModuleInfo createModuleInfo(ModuleID mid, ClassFile cf) {
		HashMap<Pair<String,FunType>,List<ModuleInfo.Method>> methods = new HashMap();
		
		if(cf.attribute("WhileyVersion") == null) {
			// This indicates the class is not a WhileyFile. This means it was
			// generate from some other source (e.g. it was a .java file
			// compiled with javac). Hence, we simply want to ignore this file
			// since it obviously doesn't contain any information that we can
			// sensibly use.
			return null;
		}
		
		for(ClassFile.Method cm : cf.methods()) {
			if(!cm.isSynthetic()) {
				ModuleInfo.Method mi = createMethodInfo(mid,cm);
				Pair<String, FunType> key = new Pair(stripCase(mi.name()), mi
						.type());
				List<ModuleInfo.Method> cases = methods.get(key);
				if(cases == null) {
					cases = new ArrayList<ModuleInfo.Method>();
					methods.put(key,cases);
				}
				cases.add(mi);
			}
		}
		
		HashMap<String,List<ModuleInfo.Method>> nmethods = new HashMap();
		for(Pair<String,FunType> key : methods.keySet()) {
			List<ModuleInfo.Method> cases = methods.get(key);
			List<ModuleInfo.Method> ncases = nmethods.get(key.first());
			if(ncases == null) {
				ncases = new ArrayList<ModuleInfo.Method>();
				nmethods.put(key.first(),ncases);
			}
			for(ModuleInfo.Method c : cases) {								
				if(cases.size() == 1) {							
					ModuleInfo.Method sm = new ModuleInfo.Method(c.receiver(),
							stripCase(c.name()), c.type(), c.parameterNames(),
							c.preCondition(), c.postCondition());
					ncases.add(sm);
				}
			}
		}
						
		HashMap<String,ModuleInfo.TypeDef> types = new HashMap();
		HashMap<String,ModuleInfo.Const> constants = new HashMap();
		
		for(BytecodeAttribute ba : cf.attributes()) {
			if(ba instanceof WhileyDefine) {
				WhileyDefine wd = (WhileyDefine) ba;
				Type type = wd.type();
				Expr expr = wd.expr();
				if(type == null) {
					// constant definition
					// FIXME: split WhileyDefine into two attributes to avoid awkward cast
					ModuleInfo.Const ci = new ModuleInfo.Const(wd.defName(),(Value) expr);
					constants.put(wd.defName(),ci);
				} else {
					// type definition
					// FIXME: split WhileyDefine into two attributes to avoid awkward cast
					ModuleInfo.TypeDef ti = new ModuleInfo.TypeDef(wd.defName(),type,(Condition) expr);
					types.put(wd.defName(),ti);
				}
			}
		}
		
		return new ModuleInfo(mid,nmethods,types,constants);
	}
	
	public String stripCase(String name) {
		int idx = name.indexOf('$');
		if(idx != -1) {
			return name.substring(0,idx);
		} else {
			return name;
		}
	}
	
	protected ModuleInfo.Method createMethodInfo(ModuleID mid,
			ClassFile.Method cm) {
		Triple<String,Type,FunType> info = splitDescriptor(cm.name());				
		WhileyCondition pre = (WhileyCondition) cm
				.attribute("WhileyPreCondition");
		WhileyCondition post = (WhileyCondition) cm
				.attribute("WhileyPostCondition");
		Condition prec = pre == null ? null : pre.condition();
		Condition postc = post == null ? null : post.condition();
		ArrayList<String> parameterNames = new ArrayList<String>();
		FunType type = info.third();
		for (int i = 0; i != type.parameters().size(); ++i) {
			parameterNames.add("p" + i);
		}
		return new ModuleInfo.Method(info.second(), info.first(), type,
				parameterNames, prec, postc);
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
	
	protected Triple<String,Type,FunType> splitDescriptor(String desc) {
		String[] split = desc.split("\\$");
		Type receiver = null;
		String name = split[0];
		FunType ft = new TypeParser(split[split.length - 1]).parseFunType();
		if(split.length > 2) {
			receiver = new TypeParser(split[1]).parseType();
		}
		return new Triple<String,Type,FunType>(name,receiver,ft);
	}
	
	protected class TypeParser {
		private int index;
		private String desc;
		
		public TypeParser(String desc) {
			index = 0;
			this.desc = desc;
		}
		
		public Type parseType() {
			NonUnionType type = parseNonUnionType();

			if (index < desc.length() && desc.charAt(index) == '|') {
				ArrayList<NonUnionType> types = new ArrayList<NonUnionType>();
				types.add(type);
				while (index < desc.length() && desc.charAt(index) == '|') {
					index = index + 1;
					types.add(parseNonUnionType());
				}
				return new UnionType(types);
			}

			return type;
		}
		
		public NonUnionType parseNonUnionType() {
			char lookahead = desc.charAt(index++);
			switch (lookahead) {
			case '*':
				return Types.T_ANY;
			case '?':
				return Types.T_EXISTENTIAL;
			case 'V':
				return Types.T_VOID;
			case 'B':
				return Types.T_BOOL;
			case 'I':
				return Types.T_INT;
			case 'R':
				return Types.T_REAL;
			case 'P':
				return new ProcessType(parseType());
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
				return new NamedType(ModuleID.fromString(pkg), name,
							parseType());			
			case '[': 
				Type et = parseType();
				lookahead = desc.charAt(index);
				if (lookahead == ']') {
					index++;
					return new ListType(et);
				}				
				break;
			case '{': 
				et = parseType();
				lookahead = desc.charAt(index);
				if (lookahead == '}') {
					index++;
					return new SetType(et);	
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
				return new TupleType(types);	
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
		
		public FunType parseFunType() {
			Type rt = parseType();
			ArrayList<Type> ps = new ArrayList<Type>();
			while(index < desc.length()) {
				ps.add(parseType());				
			}
			return new FunType(rt,ps);
		}
	}
}
