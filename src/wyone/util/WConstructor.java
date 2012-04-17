// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.util;

import java.util.*;

import wyone.core.*;
import wyone.theory.numeric.*;

/**
 * <p>
 * A constructor represents an uninterpreted value constructor.  
 * </p>
 * 
 * @author djp
 * 
 */
public class WConstructor<T extends WExpr> implements Iterable<T> {
	protected final String name; // constructor name
	protected final ArrayList<T> subterms; 	
	
	public WConstructor(String name, T... subterms) {
		assert name != null;
		this.name = name;		
		this.subterms = new ArrayList<T>();
		for(T p : subterms) {
			this.subterms.add(p);
		}		
	}		
	
	public WConstructor(String name, Collection<T> subterms) {
		assert name != null;
		this.name = name;		
		this.subterms = new ArrayList<T>(subterms);
	}
	
	// =================================================================
	// ACCESSORS
	// =================================================================

	public String name() { 
		return name;
	}
		
	public Iterator<T> iterator() {
		return subterms.iterator();
	}
	
	// =================================================================
	// REQUIRED METHODS
	// =================================================================

	public List<T> subterms() {
		return subterms;
	}
	
	// =================================================================
	// OBJECT METHODS
	// =================================================================
	
	public String toString() {
		String r = name + "(";
		boolean firstTime=true;
		
		for(WExpr p : subterms) {
			if(!firstTime) {
				r = r + ",";
			}
			firstTime=false;
			r = r + p;
		}
		return r + ")";		
	}
		
	public boolean equals(Object o) {
		if (o instanceof WConstructor) {
			WConstructor f = (WConstructor) o;
			return name.equals(f.name) && subterms.equals(f.subterms);
		}
		return false;
	}				

	public int compareTo(WExpr e) {				
		if(e instanceof WConstructor) {
			WConstructor<WExpr> c = (WConstructor<WExpr>) e;
			if(subterms.size() < c.subterms().size()) {
				return -1;
			} else if(subterms.size() > c.subterms().size()) {
				return 1;
			} 

			int nc = name.compareTo(c.name());
			if(nc != 0) {
				return nc;
			}

			for(int i=0;i!=subterms.size();++i) {
				WExpr p1 = subterms.get(i);
				WExpr p2 = c.subterms().get(i);
				nc = p1.compareTo(p2);
				if(nc != 0) { return nc; }
			}

			return 0;
		} else if(cid() < e.cid()){
			return -1;
		} else {
			return 1;
		}
	}	
	
	public int hashCode() {
		return name.hashCode() + subterms.hashCode();
	}
	
	private final static int CID = WExprs.registerCID();
	public int cid() { return CID; }
}
