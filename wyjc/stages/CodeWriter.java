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

package wyjc.stages;

import java.io.PrintWriter;
import java.util.List;

import wyjc.ast.Modifier;
import wyjc.ast.ResolvedWhileyFile;
import wyjc.ast.ResolvedWhileyFile.*;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.Type;
import wyjc.util.Pair;

public class CodeWriter {
	private PrintWriter output;
	private boolean writeProofs = false;
	private boolean writeChecks = false;
	private boolean writeVCs = false;
	
	public CodeWriter(PrintWriter output) {
		this.output = output;
	}
	
	public void setWriteProofs() {
		writeProofs = true;
	}
	
	public void setWriteChecks() {
		writeChecks = true;
	}
	
	public void setWriteVerificationConditions() {
		writeChecks = true;
		writeVCs = true;
	}
	
	public void write(ResolvedWhileyFile wf) {
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				write((FunDecl)d);
			} else if(d instanceof TypeDecl) {
				write((TypeDecl)d);
			} else if(d instanceof ConstDecl) {
				write((ConstDecl)d);
			}
			output.println();
		}
	}
	
	public void write(TypeDecl td) {
		writeModifiers(td.modifiers());		
		output.println("define " + td.name() + " as " + td.type());		
	}
	
	public void write(ConstDecl td) {
		writeModifiers(td.modifiers());
		output.println("define " + td.name() + " as " + td.constant());
	}
	
	public void write(FunDecl f) {		
		writeModifiers(f.modifiers());
		output.print(f.returnType().type() + " " + f.name() + "(");
		boolean firstTime=true;
		for(FunDecl.Parameter p : f.parameters()) {
			if(!firstTime) {
				output.print(", ");
			}
			firstTime=false;
			output.print(p.type() + " " + p.name());
		}
		output.print(")");
		if(f.constraint() != null) {
			output.print(" where " + f.constraint());			
		}			
		output.println(":");
		for(Stmt s : f.statements()) {							
			write(s,1);
		}		
	}	
	
	protected void writeModifiers(List<Modifier> modifiers) {
		for(Modifier m : modifiers) {
			output.print(m + " ");
		}
	}
	
	protected void write(List<Stmt> trace, int level) {		
		for(Stmt s : trace) {							
			write(s,level);
		}						
	}	
	
	protected void write(Stmt s, int level) {		
		PreConditionAttr attr = (PreConditionAttr)s.attribute(PreConditionAttr.class);
					
		if (writeProofs
				&& (writeChecks || !(s instanceof Check) || s instanceof Assertion)
				&& !(s instanceof VarDecl) && attr != null) {
			Condition c = attr.preCondition();
			indent(level);
			output.println("// " + c);
		} 
		
		if (s instanceof Assign || s instanceof Return
				|| s instanceof Print
				|| s instanceof VarDecl || s instanceof Invoke) {
			// Simple statements.						
			indent(level);output.println(s);			
		} else if(s instanceof Assertion) {
			indent(level);output.print(s);
			if(writeVCs) {
				VerificationConditionAttr vc = s.attribute(VerificationConditionAttr.class);
				if(vc != null) {					
					output.println("\t// VC: " + vc);
				} else {
					output.println("\t// VC: ???");
				}
			} else {
				output.println();
			}
		} else if(s instanceof Check) {
			if(writeChecks) {				
				indent(level);output.print(s);
				if(writeVCs) {
					VerificationConditionAttr vc = s.attribute(VerificationConditionAttr.class);
					if(vc != null) {
						output.println("\t// VC: " + vc);
					} else {
						output.println("\t// VC: ???");
					}
				} else {
					output.println();
				}
			}
		} else if(s instanceof IfElse) {			
			IfElse is = (IfElse) s;
			indent(level);output.println("if(" + is.condition() + "):");
			write(is.trueBranch(),level+1);			
			if(is.falseBranch() != null) {
				indent(level);output.println("else:");
				write(is.falseBranch(),level+1);
			}						
		} else if(s instanceof While) {
			While is = (While) s;
			indent(level);output.print("while(" + is.condition() + ")");
			if(is.invariant() != null) {
				output.print(" where " + is.invariant());
			}
			output.println(":");
			write(is.body(),level);								
		}
	}

	protected void indent(int level) {
		for(int i=0;i!=level;++i) {
			output.print("  ");
		}
	}   


}
