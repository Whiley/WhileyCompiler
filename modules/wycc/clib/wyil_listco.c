/*
 * wyil_listco.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This covers only the single primative wyil_list_comb .
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
 * create a list that combines two lists
 */
wycc_obj* wyil_list_comb(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_list_comb lhs");
    WY_OBJ_SANE(rhs, "wyil_list_comb rhs");
    wycc_obj* ans;
    wycc_obj* itm;
    long lsiz, rsiz, idx, siz;

    if (lhs->typ != Wy_List) {
	WY_PANIC("ERROR: lhs in wyil_list_comb is type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_List) {
	WY_PANIC("ERROR: rhs in wyil_list_comb is type %d\n", rhs->typ)
    };
    lsiz = wycc_length_of_list(lhs);
    rsiz = wycc_length_of_list(rhs);
    siz = lsiz + rsiz;
    siz += 2;	/* pad it a lttle */
    ans = wycc_list_new(siz);
    for (idx=0 ; idx < lsiz; idx++) {
	itm = wycc_list_get(lhs, idx);
	itm->cnt++;
	wycc_list_add(ans, itm);
    }
    for (idx=0 ; idx < rsiz; idx++) {
	itm = wycc_list_get(rhs, idx);
	itm->cnt++;
	wycc_list_add(ans, itm);
    }
    return ans;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
