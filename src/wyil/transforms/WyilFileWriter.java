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

package wyil.transforms;

import java.io.*;
import java.util.*;

import wyil.lang.*;
import wyil.lang.Module.*;
import wyil.ModuleLoader;
import wyil.Transform;

public class WyilFileWriter implements Transform {
	private PrintWriter out;
	private int codeFlags = Code.SHORT_TYPES;	
	private boolean writeLabels;
	private boolean writeAttributes;
		
	public WyilFileWriter(ModuleLoader loader) {

	}
	
	public String name() {
		return "wyil writer";
	}
	
	public void setWriteTypes(boolean flag) {
		if(flag) {
			codeFlags |= Code.INTERNAL_TYPES;
		}
	}
	
	public void setWriteLabels(boolean flag) {
		writeLabels = flag;
	}
	
	public void setWriteAttributes(boolean flag) {
		writeAttributes = flag;
	}
	
	public Module apply(Module module) throws IOException {
		String filename = module.filename().replace(".whiley", ".wyil");
		out = new PrintWriter(new FileOutputStream(filename));
		
		out.println("module: " + module.id());
		out.println("source-file: " + module.filename());
		out.println();
		for(ConstDef cd : module.constants()) {
			out.println("define " + cd.name() + " as " + cd.constant());
		}
		if(!module.constants().isEmpty()) {
			out.println();
		}
		for(TypeDef td : module.types()) {
			Type t = td.type();			
			String t_str;			
			t_str = t.toString();
			out.println("define " + td.name() + " as " + t_str);			
		}
		if(!module.types().isEmpty()) {
			out.println();
		}		
		for(Method md : module.methods()) {
			write(md,out);
			out.println();
		}
		out.flush();
		
		return module;
	}
	
	public void write(Method method, PrintWriter out) {
		for (Case c : method.cases()) {
			write(c, method, out);
		}
	}
	
	public void write(Case mcase, Method method, PrintWriter out) {
		Type.Fun ft = method.type(); 
		out.print(ft.ret() + " " + method.name() + "(");
		List<Type> pts = ft.params();
		List<String> names = mcase.parameterNames();
		for(int i=0;i!=names.size();++i) {
			String n = names.get(i);
			Type t = pts.get(i);
			if(i!=0) {
				out.print(", ");
			}			
			out.print(t + " " + n);			
		}
		out.println("):");				
		for(Attribute a : mcase.attributes()) {
			if(a instanceof wyjvm.lang.BytecodeAttribute) {
				wyjvm.lang.BytecodeAttribute ba = (wyjvm.lang.BytecodeAttribute) a;
				out.println("attribute: " + ba.name());
			}
		}
		out.println("body: ");
		write(0,mcase.body(),out);	
	}
	
	public void write(int indent, Block blk, PrintWriter out) {
		for(Stmt s : blk) {
			if(s.code instanceof Code.End) {
				indent--;
			}
			write(indent,s.code,s.attributes(),out);
			if(s.code instanceof Code.Loop) {
				Code.Loop loop = (Code.Loop) s.code; 
				indent++;				
				if(loop.invariant != null) {
					tabIndent(indent+1,out);
					out.println("invariant:");
					write(indent+1,loop.invariant,out);
				}
			} else if(s.code instanceof Code.Start) {
				indent++;
			}
		}
	}
	
	public void write(int indent, Code c, List<Attribute> attributes, PrintWriter out) {		
		String line = "null";		
		tabIndent(indent+1,out);
	
		// First, write out code	
		if(c instanceof Code.End) {
			Code.End cend = (Code.End)c;
			if(writeLabels) {
				line = "end " + cend.target;
			} else {
				line = "end";
			}
		} else if(c instanceof Code.Start) {
			Code.Start cstart = (Code.Start)c;
			String c_string = Code.toString(c,codeFlags);
			if(writeLabels) {
				line = "." + cstart.label + " " + c_string;
			} else {
				line = c_string;					
			}
		} else {
			line = Code.toString(c,codeFlags);			
		}
		
		// Second, write attributes				
		while(line.length() < 40) {
			line += " ";
		}
		out.print(line);
		if (writeAttributes && attributes.size() > 0) {
			out.print(" # ");
			boolean firstTime = true;
			for (Attribute a : attributes) {
				if (!firstTime) {
					out.print(", ");
				}
				firstTime = false;
				out.print(a);
			}
		}
		out.println();
	}
	
	public static void tabIndent(int indent, PrintWriter out) {
		indent = indent * 4;
		for(int i=0;i<indent;++i) {
			out.print(" ");
		}
	}
}
