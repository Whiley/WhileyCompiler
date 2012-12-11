/*
 * wycc__min.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc).  This portion of
 * the library provides support for the routine min.
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
 * given a pair of ints, return the smaller value.
 */
static wycc_obj* wycc__min_int(wycc_obj* lhs, wycc_obj* rhs) {
    long a, b;
    wycc_obj *ans;

    a = (long) lhs->ptr;
    b = (long) rhs->ptr;
    if (a > b) {
	ans = rhs;
    } else {
	ans = lhs;
    };
    ans->cnt++;
    return ans;
}

/*
 * given a pair of object, return the smaller value.
 */
wycc_obj* wycc__min(wycc_obj* lhs, wycc_obj* rhs) {
    WY_OBJ_SANE(lhs, "wycc__min lhs");
    WY_OBJ_SANE(rhs, "wycc__min rhs");

    if ((lhs->typ == Wy_Int) && (rhs->typ == Wy_Int)) {
	return wycc__min_int(lhs, rhs);
    };
    WY_PANIC("Help needed in wycc__min for types %d:%d\n"
	    , lhs->typ, rhs->typ);
    exit(-3);
}

static void __initor_b() {
// filling in type registry array goes here
// Here goes code to fill the FOM registry
    wycc_register_routine("min", "[^i,v,i,i]", wycc__min);
    wycc_register_routine("min", "[^r,v,r,r]", wycc__min);
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
