/*
 * box.h
 *
 * This is a a header file that describes the
 * library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 *
 * This file is part of the Whiley Development Kit (WDK).
 *
 * The Whiley Development Kit is free software; you can redistribute 
 * it and/or modify it under the terms of the GNU General Public 
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The Whiley Development Kit is distributed in the hope that it 
 * will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public 
 * License along with the Whiley Development Kit. If not, see 
 * <http://www.gnu.org/licenses/>
 */

/*
 * These are the defines and declarations needed for the 
 * boxing of data by this library.
 */

/*
 * It was initially assumed that this array of names would be needed
 * storage for the type registry.
 * indexes were off by 1 - to reserve 0 for not assigned.
 * now Any is 0
 * and None is -1
 * simple objects of type any or none have no contents
 * complex objects with subtype none have no contents
 * complex objects with subtype any must accept all other types
 */

//static char* wy_type_names[] = {
    /* None */
#define Wy_None	-1
    /* Any */
#define Wy_Any	0
//    "any",
    /* primative string, ie. array of unboxed bytes*/
#define Wy_String	1
//    "string",
    /* primative integer, it a long instead of pointer */
#define Wy_Int		2
//    "int",
    /* a wide integer - unbounded */
#define Wy_WInt		3
//    "wint",
    /* a primative list, ie. array of (boxed) objs */
#define Wy_List		4
//    "list",
    /* a set */
#define Wy_Set		5
//    "set",
    /* a map */
#define Wy_Map		6
//    "map",
    /* a record */
#define Wy_Record	7
//    "record",
    /*the field names of a record */
#define Wy_Fields	8
//    "fields",
    /* a constant string (in code space, not heap */
#define Wy_CString	9
//    "string",
    /* a single character */
#define Wy_Char		10
//    "char",
    /* a boolean value */
#define Wy_Bool		11
//    "bool",
    /* an iterator */
#define Wy_Iter		12
//    "iterator",
    /* a NULL */
#define Wy_Null		13
//    "null",
    /* a raw record, no names or field tpyes registered*/
#define Wy_RRecord		14
//    "raw record",
    /* a token to reference system resources */
#define Wy_Token		15
//    "token",
    /* a record of the meta data for a variety of record. */
    /* a pointer to which changes a raw record into a record. */
#define Wy_Rcd_Rcd		16
//    "record meta",
    /* a tuple == a fixed size list */
#define Wy_Tuple		17
//    "tuple",
    /* a floating point number (long double) */
#define Wy_Float		18
//    "float",
    /* a reference == an indirect pointer (upper or outer part) */
#define Wy_Ref1		19
//    "reference",
    /* a unicode string as wchar array */
#define Wy_WString	20
//    "wide string",
    /* a single byte (8 bits) */
#define Wy_Byte		21
//    "byte",
    /* a reference to some address in memory (say for a routine) */
#define Wy_Addr		22
//    "addr",
    /* a reference to a FOM */
#define Wy_FOM		23
//    "FOM",
    /* a reference == an indirect pointer (lower or inner part) */
#define Wy_Ref2		24
//    "reference",
#define Wy_Type_Max	24
//    (char *) NULL
//};

/*
 * DETAILED OBJECT DESCRIPTIONS
 *
 * ie, what does obj->ptr really point to.
 *
 * Wy_None	
 * Wy_Null
 * Wy_Any	ptr must be void
 * Wy_Char	ptr is char (will become wchar)
 * Wy_Byte	ptr is char (8 bits)
 * Wy_Bool
 * Wy_Int	ptr is long
 * Wy_Token	ptr is token
 * Wy_Ref1	ptr is pointer to another wycc_obj of type Wy_Ref2.
 * Wy_Ref2	ptr is pointer to another wycc_obj.
 * Wy_Addr	ptr is pointer to arbitrary memory
 * Wy_CString
 * Wy_String	ptr to null terminated char[].
 * Wy_WString	ptr to null terminated wchar[].
 * Wy_WInt	TBD
 * Wy_Float	ptr to data:
 *		long double
 * Wy_Tuple	just like Wy_List except no size changing.
 * Wy_List	ptr to block:
 *		member count
 *		member type
 *		alloc size
 *		array of obj ptrs [3]
 * Wy_Set	ptr to block:
 *		member count
 *		member type
 *		chunk: [2]
 *			branch count
 *			* {
 *				branch link
 *				value
 *			}
 *			value ...
 *			level_chunk link
 * Wy_Map	ptr to block:
 *		member count
 *		member key type
 *		member val type
 *		chunk: [3]
 *			branch count
 *			* {
 *				branch link
 *				value
 *				key
 *			}
 *			{
 *				value
 *				key
 *			} ...
 *			level_chunk link
 * Wy_Iter	ptr to block: w/ block == struct chunk_ptr
 * Wy_RRecord
 * Wy_Record	ptr to block:
 *		size
 *		meta obj (was pad so to match list)
 *		zero (was registry index (-1 for raw records; else ptr to recrec)
 *		array of obj ptrs * [3]
 * Wy_Rcd_Rcd	ptr to block:
 *		ptr to Wy_List of names
 *		ptr to Wy_List of types
 * Wy_FOM	ptr to block:
 *		ptr to name
 *		ptr to signature
 *		ptr to type token (index)
 *		ptr to code
 *
 */


wycc_obj* wycc_box_new(int typ, void* ptr);

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
