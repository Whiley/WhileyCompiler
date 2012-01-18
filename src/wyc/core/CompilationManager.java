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

package wyc.core;

import static wyil.util.ErrorMessages.*;
import static wyc.core.Context.internalFailure;
import static wyc.core.Context.syntaxError;

import java.util.*;

import wyautl.lang.*;
import wyc.lang.Expr;
import wyc.lang.UnresolvedType;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Declaration;
import wyc.util.*;
import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.util.*;

/**
 * <p>
 * Responsible for overseeing the compilation of a group Whiley source files
 * into wyil modules.  
 * </p>
 * <p>
 * A Name Resolver is responsible for searching the WHILEYPATH to resolve given
 * names and packages. For example, we may wish to resolve a name "Reader"
 * within a given import context of "import whiley.io.*". In such case, the name
 * resolver will first locate the package "whiley.io", and then decide whether
 * there is a module named "Reader".
 * </p>
 * 
 * 
 * @author David J. Pearce
 * 
 */
public final class CompilationManager {
	private final ModuleLoader loader;
	
	/**
	 * The set of source files being compiled in this run.
	 */
	private final CompilationGroup files;
	
	public CompilationManager(ModuleLoader loader, CompilationGroup files) {
		this.loader = loader;
		this.files = files;
	}	
}
