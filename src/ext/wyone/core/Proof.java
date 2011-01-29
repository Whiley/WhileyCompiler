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

import static wyone.core.Constructor.*;
import java.util.*;

/**
 * <p>
 * An instance of Proof is returned by the solver after working on a constraint
 * program. There are three options for any proof:
 * </p>
 * <ul>
 * <li><b>Unsatisfiable.</b> This means the solver has shown no possible
 * assignment of variables to values can satisfy the constraint program.</li>
 * <li><b>Unknown.</b> This means the solver was unable to make a decision
 * regarding the given constraint program. Usually, this occurs because of a
 * time-out.</li>
 * <li><b>Satisfiable.</b> The solver found an assigned of variables to values
 * which satisfied the constraint program. The determined assignment is included
 * as evidence.</li>
 * </ul>
 * 
 * @author djp
 * 
 */
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
		private HashMap<Variable,Value> model;
		
		public Sat() {
			this.model = new HashMap();
		}
		
		public Sat(Map<Variable,Value> model) {
			this.model = new HashMap(model);
		}
		
		public Map<Variable,Value> model() {
			return model;
		}		
	}	
}
