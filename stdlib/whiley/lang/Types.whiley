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

// The purpose of this module is to provide some standard types.
define int8 as int where $ >=-128 && $ <= 127
define int16 as int where $ >=-32768 && $ <= 32768
define int32 as int where $ >=-2147483648 && $ <= 2147483647
define int64 as int where $ >= -9223372036854775808 && $ <= 9223372036854775807

define byte as int where $ >=0 && $ <= 255
define uint8 as int where $ >=0 && $ <= 255
define uint16 as int where $ >= 0 && $ <= 65535
define uint32 as int where $ >= 0 && $ <= 4294967295
define uint64 as int where $ >= 0 && $ <= 18446744073709551615

define nat as int where $ >= 0

define error as ?
