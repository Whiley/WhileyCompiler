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

package wyil.lang;

import java.io.File;
import java.util.*;

/**
 * A Module Identifier consists of a list of packages, and a
 * module name. The purpose of the UMI is to provide a uniform way of referring
 * to modules throughout the compiler.
 * 
 * @author David J. Pearce
 * 
 */
public class ModuleID {
	private PkgID pkg;
	private String module;
	
	/**
     * Construct a module identifier.
     * 
     * @param pkg
     *            --- A list of packages. For example, "whiley.lang" becomes
     *            ["whiley","lang"]
     * @param module
     *            --- A module name.
     */
	public ModuleID(Collection<String> pkg, String module) {
		this.pkg = new PkgID(pkg);
		this.module = module;
	}
	
	public ModuleID(PkgID pkg, String module) {
		this.pkg = pkg;
		this.module = module;
	}
	
	public static ModuleID fromString(String module) {
		String[] split = module.split("\\.");
		ArrayList<String> pkgs = new ArrayList<String>();
		for (int i = 0; i != split.length - 1; ++i) {
			pkgs.add(split[i]);
		}
		return new ModuleID(new PkgID(pkgs), split[split.length - 1]);
	}
	
	public String module() {
		return module;
	}
	
	public PkgID pkg() {
		return pkg;
	}
	
	public String fileName() {
		if (pkg.size() == 0) {
			return module;
		} else {
			return pkg.fileName() + File.separatorChar + module;
		}
	}
	
	public String toString() {
		String r = pkg.toString();
		
		if(pkg.size() == 0) {
			return module;
		} else {
			return r + "." + module;
		}
	}
	
	public int hashCode() {
		return pkg.hashCode() ^ module.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof ModuleID) {
			ModuleID u = (ModuleID) o;			
			return u.module.equals(module) && u.pkg.equals(pkg);						
		}
		return false;
	}
}
