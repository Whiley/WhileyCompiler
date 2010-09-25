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

package wyil.util.dfa;

import java.util.*;

/**
 * A union flow set implements the flow set as a simple set of elements, using
 * set union to join them.
 * 
 * This implementation extends java.util.HashSet and, hence, element types must
 * provide appropriate hashcode() methods.
 * 
 * @author djp
 * 
 */
public class IntersectionFlowSet<T> implements FlowSet, Cloneable, Iterable<T> {
	private HashSet<T> data = new HashSet<T>();
	
	public IntersectionFlowSet() {}
	public IntersectionFlowSet(Collection<? extends T> src) { 
		data.addAll(src);
	}
	
	public IntersectionFlowSet<T> clone() {
		IntersectionFlowSet<T> r = new IntersectionFlowSet<T>();
		r.data.addAll(this.data);
		return r;
	}
	
	public Iterator<T> iterator() {
		return data.iterator();
	}
	
	public IntersectionFlowSet<T> join(FlowSet _fs) {		
		if(_fs instanceof IntersectionFlowSet) {
			IntersectionFlowSet<T> fs = (IntersectionFlowSet<T>) _fs;
			return intersect(fs);	
		}
		return null;
	}
	
	public IntersectionFlowSet<T> union(IntersectionFlowSet fs) {						
		IntersectionFlowSet<T> tmp = (IntersectionFlowSet<T>) clone();

		if(tmp.data.addAll(fs.data)) {				
			return fs;
		} else {				
			return this;
		}					
	}
	
	public IntersectionFlowSet<T> intersect(IntersectionFlowSet fs) {						
		IntersectionFlowSet<T> tmp = new IntersectionFlowSet<T>();		
		for(T i : data) {			
			if(fs.contains(i)) {
				tmp.data.add(i);
			} 
		}
		
		if(tmp.size() == size()) {
			return this;
		} else {		
			return tmp;
		}
	}
	
	public IntersectionFlowSet<T> add(T s) {
		if(!data.contains(s)) {
			IntersectionFlowSet r = (IntersectionFlowSet) this.clone();			
			if(r.data.add(s)) {
				return r;
			}
		}
		return this;		
	}
	
	public IntersectionFlowSet<T> addAll(Collection<T> s) {
		if(!data.contains(s)) {
			IntersectionFlowSet r = (IntersectionFlowSet) this.clone();			
			if(r.data.addAll(s)) {
				return r;	
			}			
		} 
		return this;		
	}	
	
	public IntersectionFlowSet<T> remove(T s) {
		if(data.contains(s)) {
			IntersectionFlowSet r = (IntersectionFlowSet) this.clone();			
			if(r.data.remove(s)) {
				return r;	
			}			
		} 
		
		return this;		
	}
	
	public boolean contains(T s) {
		return data.contains(s);
	}
	
	public int hashCode() {
		return data.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof IntersectionFlowSet) {
			IntersectionFlowSet ufs = (IntersectionFlowSet) o;
			return data.equals(ufs.data);
		}
		return false;
	}
	
	public int size() {
		return data.size();
	}
	
	public Set<T> toSet() {
		return new HashSet<T>(data);
	}
	
	public String toString() {
		String r = "{";
		boolean firstTime=true;
		for(T x : data) {
			if(!firstTime) {
				r = r + ", ";				
			}
			firstTime=false;
			r = r + x;
		}
		return r + "}";
	}
}
