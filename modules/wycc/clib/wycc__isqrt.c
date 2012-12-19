/*
 * wycc__isqrt.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc).  This portion of
 * the library provides support for the routine isqrt (integer square root).
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
 * given an int, return one with absolute value.
 */
static wycc_obj* wycc__isqrt_int(wycc_obj* itm) {
    long val = (long) itm->ptr;
    char *msg =  "precondition not satisfied";
    long ans;
    long del;
    long thr;
    
    if (val < 0) {
	fprintf(stderr, msg);
	exit(-4);
    };
    ans = 0;
    thr = 0;
    del = 3;
    while (val > thr) {
	ans++;
	thr += del;
	del += 2;
    }
    return wycc_box_long(ans);
}

/*
 * given an object, return one with absolute value.
 */
wycc_obj* wycc__isqrt(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc__isqrt");

    if (itm->typ == Wy_Int) {
	return wycc__isqrt_int(itm);
    };
    WY_PANIC("Help needed in wycc__isqrt for type %d\n", itm->typ);
    exit(-3);
}

static void __initor_b() {
// filling in type registry array goes here
// Here goes code to fill the FOM registry
    wycc_register_routine("isqrt", "[^i,v,i]", wycc__isqrt);
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
