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

package wyone.core;

import java.util.*;

public abstract class Proof {	
	
	public final static Proof UNSAT = new Unsat();
	public final static Proof UNKNOWN = new Unknown();
	
	public final static class Unsat extends Proof {
		private Unsat() {}
	}	

	public final static class Unknown extends Proof {
		private Unknown() {}		
	}
	
	public final static class Sat extends Proof {
		private HashMap<WVariable,WValue> model;
		
		public Sat() {
			this.model = new HashMap();
		}
		
		public Sat(Map<WVariable,WValue> model) {
			this.model = new HashMap(model);
		}
		
		public Map<WVariable,WValue> model() {
			return model;
		}		
	}	
}
