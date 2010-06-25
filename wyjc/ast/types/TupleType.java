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

import wyjc.util.Pair;
import wyone.core.WType;
import wyone.theory.tuple.WTupleType;

public class TupleType implements NonUnionType {
	private HashMap<String,Type> types;
	
	public TupleType(Map<String,Type> types) {
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
			return types.equals(t.types);
		}
		return false;
	}
	
	public int hashCode() {		
		return types.hashCode();
	}	
	
	public boolean isSubtype(Type t) {
		if(t instanceof NamedType) {
			t = ((NamedType)t).type();
		}
		if(t == Types.T_VOID) {
			return true;
		} else if(t instanceof TupleType) {
			TupleType tt = (TupleType) t;			
						
			for (Map.Entry<String, Type> p : types.entrySet()) {
				String n = p.getKey();
				Type mt = tt.types().get(n);				
				Type ttt = p.getValue();
				if (mt == null || !ttt.isSubtype(mt)) {					
					return false;
				}
			}
			
			return true;			
		} else if(t instanceof ProcessType) {
			ProcessType pt = (ProcessType) t;
			return isSubtype(pt.element());
		} else if(t instanceof UnionType) {			
			UnionType ut = (UnionType) t;
			Type tt = Types.commonType(ut.types());			
			if(tt instanceof TupleType) {
				return isSubtype(tt);
			}			
		}
		return false;
	}
	
	public Type flattern() {
		HashMap<String, Type> ts = new HashMap<String, Type>();
		for (Map.Entry<String, Type> e : types.entrySet()) {
			ts.put(e.getKey(), e.getValue().flattern());
		}
		return new TupleType(ts);
	}
	
	public boolean isExistential() {
		for (Map.Entry<String, Type> e : types.entrySet()) {
			if (e.getValue().isExistential()) {
				return true;
			}
		}
		return false;
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
		return r + ")";
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
