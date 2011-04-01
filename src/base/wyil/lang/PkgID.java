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

import java.util.*;
import java.io.File;

public class PkgID implements Iterable<String> {
	private final ArrayList<String> components;
	
	public PkgID(String... cs) {
		components = new ArrayList<String>();
		for(String s : cs) {
			components.add(s);
		}
	}
	
	public static PkgID fromString(String pkg) {
		String[] split = pkg.split("\\.");
		ArrayList<String> pkgs = new ArrayList<String>();
		for(int i=0;i!=split.length-1;++i) {
			pkgs.add(split[i]);
		}
		return new PkgID(pkgs);				
	}
	
	public PkgID(Collection<String> cs) {
		components = new ArrayList<String>(cs);		
	}
	
	public String get(int index) {
		return components.get(index);
	}
	
	public int size() {
		return components.size();
	}
	
	public String last() {
		return components.get(components.size()-1);
	}
	
	public PkgID subpkg(int start, int end) {
		PkgID r = new PkgID();		
		for(int i=start;i!=end;++i) {
			r.components.add(components.get(i));
		}
		return r;
	}
	
	public PkgID append(String pkg) {
		PkgID r = new PkgID(components);
		r.components.add(pkg);
		return r;
	}
	
	public Iterator<String> iterator() {
		return components.iterator();
	}
	
	public String fileName() {		
		boolean firstTime=true;
		String r = "";
		for(String s : components) {
			if(!firstTime) {
				r += File.separatorChar;
			}
			firstTime=false;
			r += s;			
		}
		return r;	
	}
	
	public boolean equals(Object o) {
		if(o instanceof PkgID) {
			PkgID p = (PkgID) o;
			return p.components.equals(components);
		}
		return false;
	}
	
	public int hashCode() {
		return components.hashCode();
	}
	
	public String toString() {
		boolean firstTime=true;
		String r = "";
		for(String s : components) {
			if(!firstTime) {
				r += ".";
			}
			firstTime=false;
			r += s;			
		}
		return r;
	}
}
