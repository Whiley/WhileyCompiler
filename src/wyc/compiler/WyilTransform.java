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

package wyc.compiler;

import wyil.lang.Module;
import wyil.stages.ModuleTransform;
import wyil.util.Logger;

public class WyilTransform implements WyCompiler.Stage {
	private String name;
	private ModuleTransform transform;
	
	public WyilTransform(String name, ModuleTransform transform) {
		this.name = name;
		this.transform = transform;
	}
	
	public String name() {
		return name;
	}
	
	public Module process(Module module, Logger logout) {
		long start = System.currentTimeMillis();
			
		try {
			module = transform.apply(module);
			logout.logTimedMessage("[" + module.filename() + "] applied " + name,
					System.currentTimeMillis() - start);
			return module;
		} catch(RuntimeException ex) {
			logout.logTimedMessage("[" + module.filename()
					+ "] failed on " + name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);			
			throw ex;			
		}
	}
}
