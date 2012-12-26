/*
 * wyil_set_uni.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This covers only the single primative wyil_set_union .
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
 * given a set and a list add each element of the list to the set
 */
static wycc_obj* wyil_set_add_list(wycc_obj* set, wycc_obj* lst){
    WY_OBJ_SANE(set, "wyil_set_add_list set");
    WY_OBJ_SANE(lst, "wyil_set_add_list lst");
    void** p = lst->ptr;
    wycc_obj *itm;
    long at, tmp;

    for (at= 0; at < (long) p[0]; at++) {
	itm = (wycc_obj *) p[3+at];
	wycc_set_add(set, itm);
    };
    return set;
}

/*
 * like wyil_set_union but where one or both operands are lists
 */
static wycc_obj* wyil_set_union_list(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_union_list lhs");
    WY_OBJ_SANE(rhs, "wyil_set_union_list rhs");
    wycc_obj* ans;
    struct chunk_ptr my_chunk_ptr;
    struct chunk_ptr *cptr = & my_chunk_ptr;

    ans = wycc_set_new(-1);
    if (lhs->typ == Wy_Set) {
	wycc_chunk_ptr_fill(cptr, lhs, 0);	/* 0 == this is a set */
	wycc_chunk_ptr_inc(cptr);
    } else if (rhs->typ == Wy_Set) {
	wycc_chunk_ptr_fill(cptr, rhs, 0);
	wycc_chunk_ptr_inc(cptr);
    } else {
	cptr->key = (wycc_obj *) NULL;
    };
    while (cptr->key != (wycc_obj *) NULL) {
	wycc_set_add(ans, cptr->key);
	wycc_chunk_ptr_inc(cptr);
    };
    /* **** Tuple? */
    if (lhs->typ == Wy_List) {
	wyil_set_add_list(ans, lhs);
    };
    if (rhs->typ == Wy_List) {
	wyil_set_add_list(ans, rhs);
    };
    return ans;
}

/*
 * return a set that is the union of lhs and rhs
 */
wycc_obj* wyil_set_union(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_union lhs");
    WY_OBJ_SANE(rhs, "wyil_set_union rhs");
    wycc_obj* ans;
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;


    /* **** Tuple? */
    if ((lhs->typ == Wy_List) || (rhs->typ == Wy_List)) {
	return wyil_set_union_list(lhs, rhs);
    }
    if (lhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_union for type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_union for type %d\n", rhs->typ)
    };
    ans = wycc_set_new(-1);
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
	    wycc_set_add(ans, litm);
	    wycc_chunk_ptr_inc(lptr);
	    wycc_chunk_ptr_inc(rptr);
	    continue;
	};
	if (end < 0) {
	    wycc_set_add(ans, litm);
	    wycc_chunk_ptr_inc(lptr);
	} else {
	    wycc_set_add(ans, ritm);
	    wycc_chunk_ptr_inc(rptr);
	};

    };
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
