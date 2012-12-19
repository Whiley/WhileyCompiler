/*
 * wycc__isWhiteSpace.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc).  This portion of
 * the library provides support for the routine isWhiteSpace.
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

#include "../include/wycc_lib.h"
#include "common.h"
#include "box.h"

/*
 * given a char, return a bool
 * set to true iff the char is space, tab, newline, return
 * fails to check for formfeed (\f), vertical tab (\v)
 */
wycc_obj* wycc__isWhiteSpace(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc__isWhiteSpace");
    int ans;
    long val;

    val = -1;
    if (itm->typ == Wy_Int) {
	val = (long) itm->ptr;
    } else if (itm->typ == Wy_Char) {
	val = (long) itm->ptr;
    } else if (itm->typ == Wy_Byte) {
	val = (long) itm->ptr;
    } else {
	WY_PANIC("Help needed in wycc__isWhiteSpace for type %d\n"
		, itm->typ);
	exit(-3);
    };
    ans = 0;
    if (val == ' ') {
	ans = 1;
    } else if (val == '\t') {
	ans = 1;
    } else if (val == '\n') {
	ans = 1;
    } else if (val == '\r') {
	ans = 1;
    };
    return wycc_box_bool(ans);
}

static void __initor_b() {
// filling in type registry array goes here
// Here goes code to fill the FOM registry
    wycc_register_routine("isWhiteSpace", "[^b,v,c]", wycc__isWhiteSpace);
    return;
}

static void __initor_d() {
// Here goes code to query the FOM registry
    return;
}

static wycc_initor __initor_c;
__attribute__ ((constructor)) static void __initor_a() {
	__initor_c.nxt = wycc_init_chain;
	__initor_c.functionr = __initor_b;
	__initor_c.functionq = __initor_d;
	wycc_init_chain = &__initor_c;
	return;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
