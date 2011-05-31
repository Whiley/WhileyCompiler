// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjc.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wyil.lang.Attribute;
import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.Type;
import wyil.util.Pair;
import wyil.util.Types;
import wyjc.attributes.WhileyDefine;
import wyjc.attributes.WhileyType;
import wyjvm.io.BinaryInputStream;
import wyjvm.io.ClassFileReader;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile;

/**
 * The ClassFileLoader is responsible for reading class files and converting
 * them into wyil modules. The value of this is that we can extract necessary
 * module information from classfiles on the CLASSPATH.
 * 
 * @author djp
 * 
 */
public class ClassFileLoader {

	/**
	 * A map from attribute names to attribute readers. The readers are used to
	 * decode unknown attributes. 
	 */
	private ArrayList<BytecodeAttribute.Reader> readers;
	private ClassFileReader reader;
		
	public ClassFileLoader() {
		readers = new ArrayList();
		readers.add(new WhileyType.Reader());	
		readers.add(new WhileyDefine.Reader(readers));		
		// probably want to add more readers here.  E.g. for pre and post-conditions.
	}
		
	public Module read(ModuleID module, String filename, InputStream input)
			throws IOException {
		ArrayList<BytecodeAttribute.Reader> readers = new ArrayList<BytecodeAttribute.Reader>(
				this.readers);

		ClassFileReader r = new ClassFileReader(input, readers);
		return createModule(module, r.readClass());
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
		
		for (ClassFile.Method cm : cf.methods()) {
			if (!cm.isSynthetic()) {
				Module.Method mi = createMethodInfo(mid, cm);
				Pair<Type.Fun, String> key = new Pair(mi.type(), mi.name());
				Module.Method method = methods.get(key);
				if (method != null) {
					// coalesce cases
					ArrayList<Module.Case> ncases = new ArrayList<Module.Case>(
							method.cases());
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
					List<Attribute> attrs = new ArrayList<Attribute>();		
					for(BytecodeAttribute bba : wd.attributes()) {			
						// Ooh, this is such a hack ...						
						if(bba instanceof Attribute) {							
							attrs.add((Attribute)bba);
						}
					}
					Module.ConstDef ci = new Module.ConstDef(wd.defName(),wd.value(),attrs);
					constants.add(ci);
				} else {
					// type definition
					List<Attribute> attrs = new ArrayList<Attribute>();		
					for(BytecodeAttribute bba : wd.attributes()) {			
						// Ooh, this is such a hack ...						
						if(bba instanceof Attribute) {								
							attrs.add((Attribute)bba);
						}
					}
					Module.TypeDef ti = new Module.TypeDef(wd.defName(),type,attrs);					
					types.add(ti);
				}
			}
		}
				
		return new Module(mid, cf.name(), methods.values(), types, constants);
	}
	
	protected Module.Method createMethodInfo(ModuleID mid, ClassFile.Method cm) {
		// string any mangling off.
		try {
			int split = cm.name().indexOf('$');
			String name = cm.name().substring(0, split);
			String mangle = cm.name().substring(split + 1, cm.name().length());
			// then find the type
			Type.Fun type = (Type.Fun) new Types.BinaryReader(
					new BinaryInputStream(new JavaIdentifierInputStream(mangle)))
					.read();
			// now build the parameter names
			List<Attribute> attrs = new ArrayList<Attribute>();
			for (BytecodeAttribute ba : cm.attributes()) {
				// Ooh, this is such a hack ...
				if (ba instanceof Attribute) {
					attrs.add((Attribute) ba);
				}
			}

			ArrayList<String> parameterNames = new ArrayList<String>();

			for (int i = 0; i != type.params().size(); ++i) {
				parameterNames.add("$" + i);
			}

			List<Module.Case> mcases = new ArrayList<Module.Case>();
			mcases.add(new Module.Case(null, attrs));

			return new Module.Method(name, type, mcases);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
