/*
 * wyil_set_int.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This covers only the single primative wyil_set_insect .
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
#include "chunk.h"

/*
 * given a list, produce a set.
 */
static wycc_obj *wycc_set_from_list(wycc_obj *lst) {
    WY_OBJ_SANE(lst, "wycc_set_from_list");
    void** p = lst->ptr;
    wycc_obj *ans;
    wycc_obj *itm;
    long idx;

    ans = wycc_set_new(-1);
    for (idx= 0; idx < (long) p[0] ; idx++) {
	itm = (wycc_obj *) p[3 + idx];
	//itm->cnt++;
	wycc_set_add(ans, itm);
    }
    return ans;
}

/*
 * return a set that is the intersection between lhs and rhs
 */
wycc_obj* wyil_set_insect(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_insect lhs");
    WY_OBJ_SANE(rhs, "wyil_set_insect rhs");
    wycc_obj* ans;
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;
    int flg = 0;

    if (lhs->typ == Wy_List) {
	flg |= 1;
	ans = wycc_set_from_list(lhs);
	lhs = ans;
    };
    if (rhs->typ == Wy_List) {
	flg |= 2;
	ans = wycc_set_from_list(rhs);
	rhs = ans;
    };
    if (lhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_insect for type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_insect for type %d\n", rhs->typ)
    };
    ans = wycc_set_new(-1);
    wycc_chunk_ptr_fill(lptr, lhs, 0);	/* 0 == this is a set */
    wycc_chunk_ptr_fill(rptr, rhs, 0);
    wycc_chunk_ptr_inc(lptr);
    wycc_chunk_ptr_inc(rptr);
    while (1) {
	litm = lptr->key;
	ritm = rptr->key;
	if (litm == NULL) {
	    break;;
	};
	if (ritm == NULL) {
	    break;
	};
	end = wycc_comp_gen(litm, ritm);
	if (end == 0) {
	    wycc_set_add(ans, litm);
	    wycc_chunk_ptr_inc(lptr);
	    wycc_chunk_ptr_inc(rptr);
	    continue;
	};
	if (end < 0) {
	    wycc_chunk_ptr_inc(lptr);
	} else {
	    wycc_chunk_ptr_inc(rptr);
	};
    };
    if (flg & 1) {
	wycc_deref_box(lhs, 0);
    };
    if (flg & 2) {
	wycc_deref_box(rhs, 0);
    };
    return ans;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
