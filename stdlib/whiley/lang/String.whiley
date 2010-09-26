// This file is part of the Whiley Compiler
//
// The Whiley Compiler is free software; you can redistribute it 
// and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 2 of the License, or (at your option) any later version.
//
// The Whiley Compiler is distributed in the hope
// that it will be useful, but WITHOUT ANY WARRANTY; without 
// even the implied warranty of MERCHANTABILITY or FITNESS FOR 
// A PARTICULAR PURPOSE.  See the GNU General Public License 
// for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Java Compiler Kit; if not, 
// write to the Free Software Foundation, Inc., 59 Temple Place, 
// Suite 330, Boston, MA  02111-1307  USA
//
// (C) David James Pearce, 2009. 

package whiley.lang

define string as [char]

public string str(* item):
    if item ~= null:
        return "null"
    extern jvm:
        aload 0
        invokevirtual java/lang/Object.toString:()Ljava/lang/String;
        invokestatic wyil/jvm/rt/WhileyList.fromString:(Ljava/lang/String;)Lwyil/jvm/rt/WhileyList;
        areturn
    return "DUMMY" // dead code
