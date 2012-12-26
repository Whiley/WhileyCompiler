/*
 * wyil_range.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 *  This covers only the single primative wyil_range .
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
 * given two small integers (**** need to support wide ints)
 * construct a list of with members in that range.
 */
wycc_obj* wyil_range(wycc_obj* lhs, wycc_obj* rhs) {
    WY_OBJ_SANE(lhs, "wyil_range lhs");
    WY_OBJ_SANE(rhs, "wyil_range rhs");
    wycc_obj* ans;
    wycc_obj* itm;
    int lo, hi, sz, idx;

    if (lhs->typ != Wy_Int) {
	WY_PANIC("HELP needed in wyil_range for start of type %d\n"
		, lhs->typ)
    };
    if (rhs->typ != Wy_Int) {
	WY_PANIC("HELP needed in wyil_range for end of type %d\n"
		, rhs->typ)
    };
    lo = (int) lhs->ptr;
    hi = (int) rhs->ptr;
    sz = hi - lo;
    if (sz < 0) {
	lo = hi;
	hi = -1;
    } else {
	hi = 1;
    };
    ans = wycc_list_new(sz);
    for (idx=0 ; idx < sz; idx++) {
	itm = wycc_box_int((idx * hi) + lo);
	wycc_list_add(ans, itm);
    };
    return ans;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
