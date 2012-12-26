/*
 * wyil_set_diff.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This covers only the single primative wyil_set_diff .
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
 * return a set that is the difference between lhs and rhs
 */
wycc_obj* wyil_set_diff(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_diff lhs");
    WY_OBJ_SANE(rhs, "wyil_set_diff rhs");
    wycc_obj* ans;
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;

    if (lhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_diff for type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_diff for type %d\n", rhs->typ)
    };
    ans = wycc_set_new(-1);
    /*
     * short circuit things a bit by checking the type of the members
     */
    wycc_chunk_ptr_fill(lptr, lhs, 0);	/* 0 == this is a set */
    wycc_chunk_ptr_fill(rptr, rhs, 0);
    wycc_chunk_ptr_inc(lptr);
    wycc_chunk_ptr_inc(rptr);
    while (1) {
	litm = lptr->key;
	ritm = rptr->key;
	if ((litm == NULL) && (ritm == NULL)) {
	    return ans;
	};
	if (litm == NULL) {
	    end = 1;
	} else if (ritm == NULL) {
	    end = -1;
	} else {
	    end = wycc_comp_gen(litm, ritm);
	};
	if (end == 0) {
	    wycc_chunk_ptr_inc(lptr);
	    wycc_chunk_ptr_inc(rptr);
	    continue;
	};
	if (end < 0) {
	    wycc_set_add(ans, litm);
	    wycc_chunk_ptr_inc(lptr);
	} else {
	    wycc_chunk_ptr_inc(rptr);
	};

    };
    WY_PANIC("Failure: wyil_set_diff\n")
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
