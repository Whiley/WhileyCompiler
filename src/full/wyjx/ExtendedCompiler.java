package wyjx;

import java.util.List;

import wyil.ModuleLoader;
import wyil.lang.Module;
import wyjc.compiler.Compiler.Stage;
import wyjc.lang.WhileyFile;
import wyjx.stages.*;

public class ExtendedCompiler extends wyjc.compiler.Compiler {
	public ExtendedCompiler(List<Stage> stages, ModuleLoader loader) {
		super(loader,stages);		
	}
	
	protected List<Module> buildModules(List<WhileyFile> files) {
		long start = System.currentTimeMillis();		
		List<Module> modules = new ModuleBuilder(loader).resolve(files);
		logTimedMessage("built modules",
				System.currentTimeMillis() - start);
		return modules;		
	}	
}
