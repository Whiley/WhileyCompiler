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

package wyil.io;

import java.io.*;
import java.util.*;

import wyil.lang.*;
import wyil.lang.Module.*;

public class WyilFileWriter {
	private PrintWriter out;
	private int codeFlags = Code.SHORT_TYPES;	
	private boolean writeLabels;
	private boolean writeAttributes;
	
	public WyilFileWriter(Writer os) {
		out = new PrintWriter(os);
	}
	
	public WyilFileWriter(OutputStream os) {
		out = new PrintWriter(os);
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
	
	public void write(Module module) {
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
			out.print("define " + td.name() + " as " + td.type());
			if(td.constraint() != null) {
				out.println(":");
				write(0,td.constraint(),out);				
			} else {
				out.println();
			}
		}
		if(!module.types().isEmpty()) {
			out.println();
		}		
		for(Method md : module.methods()) {
			write(md,out);
			out.println();
		}
		out.flush();
	}
	
	public void write(Method method, PrintWriter out) {
		for (Case c : method.cases()) {
			write(c, method, out);
		}
	}
	
	public void write(Case mcase, Method method, PrintWriter out) {
		Type.Fun ft = method.type(); 
		out.print(ft.ret + " " + method.name() + "(");
		List<Type> pts = ft.params;
		List<String> names = mcase.parameterNames();
		for(int i=0;i!=names.size();++i) {
			String n = names.get(i);
			Type t = pts.get(i);
			if(i!=0) {
				out.print(", ");
			}
			if((codeFlags & Code.SHORT_TYPES) != 0) {
				out.print(Type.toShortString(t) + " " + n);
			} else {
				out.print(t + " " + n);
			}
		}
		out.println("):");
		
		if(mcase.precondition() != null) {
			out.println("precondition: ");
			write(0,mcase.precondition(),out);			
		}
		if(mcase.postcondition() != null) {
			out.println("postcondition: ");
			write(0,mcase.postcondition(),out);			
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
