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

package wycore.util;

import java.util.Arrays;
import java.util.Collection;

import wycore.lang.Content;
import wycore.lang.Path;
import wycore.lang.Path.Filter;

public final class RegexFilter<T> implements Filter<T> {
	private final Content.Type<T> contentType;		
	private final String[] components;		

	private RegexFilter(Content.Type<T> ct, String[] components) {
		this.components = components;
		this.contentType = ct;
	}
	
	private RegexFilter(Content.Type<T> ct, Path.ID prefix, String[] components) {
		this.contentType = ct;
		int prefixSize = prefix.size();
		this.components = new String[prefixSize + components.length];
		for (int i = 0; i != prefixSize; ++i) {
			this.components[i] = prefix.get(i);
		}
		System.arraycopy(components, 0, this.components, prefixSize,
				components.length);
	}
	
	public String last() {
		return components[components.length-1];
	}
	
	public Path.Entry<T> match(Path.Entry<?> e) {
		if ((contentType == null || e.contentType() == contentType)
				&& match(e.id(), 0, 0)) {
			return (Path.Entry<T>) e;
		}
		return null;
	}
	
	private boolean match(Path.ID id, int idIndex, int myIndex) {		
		if(myIndex == components.length && idIndex == id.size()) {			
			return true;
		} else if(myIndex == components.length || idIndex == id.size()) {			
			return false;
		}
		String myComponent = components[myIndex];
		if(myComponent.equals("*")) {
			return match(id,idIndex+1,myIndex+1);
		} else if(myComponent.equals("**")) {			
			myIndex++;
			for(int i=idIndex;i<=id.size();++i) {
				if(match(id,i,myIndex)) {
					return true;
				}
			}
			return false;
		} else {
			return myComponent.equals(id.get(idIndex)) && match(id,idIndex+1,myIndex+1);
		}				
	}		
	
	public int hashCode() {
		int r = 0;
		for(String c : components) {
			r = r + c.hashCode();
		}
		return r;
	}
	
	public boolean equals(Object o) {
		if (o instanceof RegexFilter) {
			RegexFilter r = (RegexFilter) o;
			return contentType == r.contentType
					&& Arrays.equals(components, r.components);
		}
		return false;
	}
	
	public String toString() {
		String r = "";
		
		for(int i=0;i!=components.length;++i) {
			if(i > 0) {
				r = r + "/";
			}
			r = r + components[i];
		}
		
		return r;
	}
	
	public static RegexFilter<?> create(String... components) {	
		return new RegexFilter(null,components);
	}
	
	public static RegexFilter<?> create(Collection<String> components) {		
		String[] cs = components.toArray(new String[components.size()]);
		return new RegexFilter(null,cs);
	}
	
	public static RegexFilter<?> create(Path.ID prefix, String... components) {	
		return new RegexFilter(null,prefix,components);
	}
	
	/**
	 * Create a Regex filter that matches all entries of a given content type
	 * and path regex.
	 * 
	 * @param contentType
	 * @param components
	 *            --- components of the path regex
	 * @return
	 */
	public static <T> RegexFilter<T> create(Content.Type<T> contentType, String... components) {	
		return new RegexFilter<T>(contentType,components);
	}	
}
