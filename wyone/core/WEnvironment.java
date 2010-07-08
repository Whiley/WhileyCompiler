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

public interface WEnvironment extends Map<String,WType> {
	
	/**
	 * The full type returns the true type of the variable in question. This may
	 * be a function type, if the variable accepts arguments.
	 * 
	 * @param var
	 * @return
	 */
	public WType fullType(String var);

	/**
	 * This returns type that this expression will evaluate to. So, in the case
	 * of a function, then this will be its return type.
	 * 
	 * @param var
	 * @return
	 */
	public WType evalType(String var);	
}
