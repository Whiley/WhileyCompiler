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

package wyjx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wyil.ModuleLoader;
import wyil.stages.*;
import wyjc.compiler.*;
import wyjc.compiler.Compiler;
import wyjx.attributes.*;
import wyjx.stages.*;

public class Main extends wyjc.Main {
	public static void main(String[] args) {
		new Main().run(args);
	}
	
	public void usage() {
		String[][] info = {
				{ "version", "Print version information" },
				{ "verbose",
						"Print detailed information on what the compiler is doing" },								
				{"whileypath <path>", "Specify where to find whiley (class) files"},
				{"wp <path>", "Specify where to find whiley (class) files"},
				{"bootpath <path>",
				"Specify where to find whiley standard library (class) files"},
				{"bp <path>", "Specify where to find whiley standard library (class) files"},				};
		
		System.out.println("usage: wyjx <options> <source-files>");
		System.out.println("Options:");

		// first, work out gap information
		int gap = 0;

		for (String[] p : info) {
			gap = Math.max(gap, p[0].length() + 5);
		}

		// now, print the information
		for (String[] p : info) {
			System.out.print("  -" + p[0]);
			int rest = gap - p[0].length();
			for (int i = 0; i != rest; ++i) {
				System.out.print(" ");
			}
			System.out.println(p[1]);
		}
	}	

	protected Compiler createCompiler(ModuleLoader loader,List<Compiler.Stage> stages) {	
		// You know what? this is an ugly thing to do :|
		loader.attributeReaders().add(new Constraint.Reader());
		loader.attributeReaders().add(new Precondition.Reader());
		loader.attributeReaders().add(new Postcondition.Reader());
		// Ok, can now create the extended compiler
		return new ExtendedCompiler(stages,loader);
	}
	
	protected List<Compiler.Stage> defaultPipeline(ModuleLoader loader) {
		ArrayList<Compiler.Stage> stages = new ArrayList<Compiler.Stage>();
		
		// First, construct the default pipeline
		stages.add(new WyilWriter(loader,Collections.EMPTY_MAP));	
		
		// FIXME: this is a bug as it must run before all other stages
		stages.add(new WyilTransform("type propagation", new TypePropagation(
				loader)));
		
		stages.add(new WyilTransform("dispatch inline", new PreconditionInline(
				loader)));
		
		stages.add(new WyilTransform("type propagation", new TypePropagation(
				loader)));
		
		stages.add(new WyilTransform("definite assignment",
				new DefiniteAssignment(loader)));
		
		stages.add(new WyilTransform("constant propagation",
				new ConstantPropagation(loader)));

		stages.add(new WyilTransform("branch prediction",
				new ExpectedInference(loader)));
		
		stages.add(new WyilTransform("verification check",
				new ConstraintPropagation(loader, 250)));
		
		stages.add(new WyilTransform("failure check",
				new FailureCheck(loader)));
		
		stages.add(new WyilTransform("function check",
				new FunctionCheck(loader)));
						
		stages.add(new ClassWriter(loader));
		
		return stages;
	}
	
}
