/*
 * wycc__abs.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc).  This portion of
 * the library provides support for the routine abs.
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

#include "../lib/wycc_lib.h"
#include "common.h"
#include "box.h"

/*
 * given an int, return one with absolute value.
 */
static wycc_obj* wycc__abs_int(wycc_obj* itm) {
    long val = (long) itm->ptr;

    if (val < 0) {
	val *= -1;
	return wycc_box_long(val);
    };
    itm->cnt++;
    return itm;
}

/*
 * given an object, return one with absolute value.
 */
wycc_obj* wycc__abs(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc__abs");

    if (itm->typ == Wy_Int) {
	return wycc__abs_int(itm);
    };
    WY_PANIC("Help needed in wycc__abs for type %d\n", itm->typ);
    exit(-3);
}

static void __initor_b() {
// filling in type registry array goes here
// Here goes code to fill the FOM registry
    wycc_register_routine("abs", "[^i,v,i]", wycc__abs);
    wycc_register_routine("abs", "[^r,v,r]", wycc__abs);
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
