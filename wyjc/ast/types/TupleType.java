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

package wyjc.ast.types;

import java.util.*;

import wyjc.ast.exprs.Condition;
import wyjc.util.Pair;
import wyone.core.WType;
import wyone.theory.tuple.WTupleType;

public class TupleType extends ConstrainedType implements NonUnionType {
	private HashMap<String,Type> types;
	
	public TupleType(Map<String,Type> types) {
		this.types = new HashMap<String,Type>(types);			
	}
	
	public TupleType(Map<String,Type> types, Condition constraint) {
		super(constraint);
		this.types = new HashMap<String,Type>(types);			
	}
	
	public Iterator<Map.Entry<String,Type>> iterator() {
		return types.entrySet().iterator();
	}
	
	public Map<String,Type> types() {
		return types;
	}
	
	public Type get(String key) {
		return types.get(key);
	}
	
	public boolean equals(Object o) {
		if(o instanceof TupleType) {
			TupleType t = (TupleType) o;
			return types.equals(t.types)
					&& (constraint == t.constraint || (constraint != null && constraint
							.equals(t.constraint)));
		}
		return false;
	}
	
	public int hashCode() {		
		int hc = constraint == null ? 0 : constraint.hashCode();
		return types.hashCode() + hc;
	}	
	
	public Type flattern() {
		HashMap<String, Type> ts = new HashMap<String, Type>();
		for (Map.Entry<String, Type> e : types.entrySet()) {
			ts.put(e.getKey(), e.getValue().flattern());
		}
		return new TupleType(ts,constraint);
	}
	
	public boolean isExistential() {
		for (Map.Entry<String, Type> e : types.entrySet()) {
			if (e.getValue().isExistential()) {
				return true;
			}
		}
		return false;
	}
	
	public Type substitute(Map<String, Type> binding) {
		HashMap<String,Type> ts = new HashMap<String,Type>();
		for (Map.Entry<String, Type> e : types.entrySet()) {
			ts.put(e.getKey(), e.getValue().substitute(binding));
		}
		return new TupleType(ts,constraint);
	}
	
	public <T> Set<T> match(Class<T> type) {
		HashSet<T> r = new HashSet<T>();
		
		for(Map.Entry<String, Type> e : types.entrySet()) {
			r.addAll(e.getValue().match(type));
		}
		
		if(UnionType.class == type) {			
			r.add((T)this);			
		} 
		
		return r;		
	}
	
	public String toString() {
		String r = "(";
		boolean firstTime = true;

		ArrayList<String> ss = new ArrayList<String>(types.keySet());
		Collections.sort(ss);

		for (String s : ss) {
			if (!firstTime) {
				r = r + ",";
			}
			firstTime = false;
			r = r + types.get(s).toString() + " " + s;
		}
		return r + ")" + super.toString();
	}
	
	public WType convert() {
		ArrayList<wyone.util.Pair<String, WType>> ntypes = new ArrayList();
		ArrayList<String> fields = new ArrayList(types.keySet());
		Collections.sort(fields);
		for (String field : fields) {
			WType type = types.get(field).convert();
			ntypes.add(new wyone.util.Pair(field,type));
		}
		return new WTupleType(ntypes);
	}
}
