/*
 * wycc_comp.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This handles comparisons between boxed items.
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
 * we need comparitor functions for all object types
 */
static int wycc_comp_str(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_set(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_rmeta(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_record(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_obj(wycc_obj* lhs, wycc_obj* rhs);


/*
 * ------------------------------
 * wycc comparison routines for B things
 *
 * each returns      -1,  0, or 1
 * corresponding to  ab, aa,   ba
 * ------------------------------
 */
int wycc_comp_gen(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_gen lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_gen rhs");
    int lt = lhs->typ;
    int rt = rhs->typ;

    lt = wycc_type_dealias(lt);
    rt = wycc_type_dealias(rt);
    if (lt < rt) {
	return -1;
    };
    if (lt > rt) {
	return 1;
    };
    if (lt == Wy_String) {
	return wycc_comp_str(lhs, rhs);
    };
    if (lt == Wy_Int) {
	return wycc_comp_int(lhs, rhs);
    };
    if (lt == Wy_Set) {
	return wycc_comp_set(lhs, rhs);
    };
    if (lt == Wy_Record) {
	return wycc_comp_record(lhs, rhs);
    };
    if (lt == Wy_List) {
	return wycc_comp_list(lhs, rhs);
    };
    if (lt == Wy_Tuple) {
	return wycc_comp_list(lhs, rhs);
    };
    if (lt == Wy_Float) {
	return wycc_comp_float(lhs, rhs);
    };
    if (lt == Wy_Null) {
	return 0;
    };
    WY_PANIC("Help needed in wycc_comp_gen for type %d\n", lt)
}

/*
 * simple comarison of two c (null-terminated)strings
 */
static int wycc_comp_str(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_str lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_str rhs");
    char *lp, *rp;
    int ans;

    if ((lhs->typ != Wy_String) && (lhs->typ != Wy_CString)) {
	WY_PANIC("Help needed in wycc_comp_str for type %d\n", lhs->typ)
    };
    if ((rhs->typ != Wy_String) && (rhs->typ != Wy_CString)) {
	WY_PANIC("Help needed in wycc_comp_str for type %d\n", rhs->typ)
    };
    lp = lhs->ptr;
    rp = rhs->ptr;
    ans =  strcmp(lp, rp);
    if (ans < 0) {
	return -1;
    } else if (ans > 0) {
	return 1;
    };
    return 0;
}

/*
 * a not quite simple comparison of two small sets
 * a set with fewer elements is always smaller
 * a 
 */
static int wycc_comp_set(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_set lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_set rhs");
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;

    wycc_chunk_ptr_fill(lptr, lhs, 0);	/* 0 == this is a set */
    wycc_chunk_ptr_fill(rptr, rhs, 0); 
    wycc_chunk_ptr_inc(lptr);
    wycc_chunk_ptr_inc(rptr);
    while (lptr->key) {
	litm = lptr->key;
	ritm = rptr->key;
	end = wycc_comp_gen(litm, ritm);
	if (end != 0) {
	    return end;
	};
	wycc_chunk_ptr_inc(lptr);
	wycc_chunk_ptr_inc(rptr);
    };
    return 0;
}

static int wycc_comp_array(wycc_obj** lhs, wycc_obj** rhs, int cnt){
    wycc_obj *litm;
    wycc_obj *ritm;
    int idx;
    int end;

    for (idx = 0; idx < cnt ; idx ++) {
	litm = lhs[idx];
	ritm = rhs[idx];
	end = wycc_comp_gen(litm, ritm);
	if (end != 0) {
	    return end;
	};
    }
    return 0;
}

/*
 * a not quite simple comparison of two lists
 * a list with fewer elements is always "smaller"
 * list of equal size are compared element by element.
 */
int wycc_comp_list(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_list lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_list rhs");
    void ** lblk = (void **) lhs->ptr;
    void ** rblk = (void **) rhs->ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;
    int idx;
    int cnt;

    idx = (int) lblk[0];
    cnt = (int) rblk[0];
    if (idx < cnt) {
	return -1;
    };
    if (cnt < idx) {
	return 1;
    };
    return wycc_comp_array((wycc_obj**)&(lblk[3]), (wycc_obj**)&(rblk[3]), cnt);
}

/*
 * a not quite simple comparison of two records
 * a record with fewer fields is always smaller
 * a 
 */
static int wycc_comp_record(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_record lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_record rhs");
    void ** lblk = (void **) lhs->ptr;
    void ** rblk = (void **) rhs->ptr;
    int lcnt = (int) lblk[0];
    int rcnt = (int) rblk[0];
    int end;
    int cnt;

    if (lcnt < rcnt) {
	return -1;
    };
    if (rcnt < lcnt) {
	return 1;
    };
    end = wycc_comp_rmeta((wycc_obj*) lblk[1], (wycc_obj*) rblk[1]);
    if (end != 0) {
	return end;
    };
    cnt = lcnt;
    return wycc_comp_array((wycc_obj**)&(lblk[3]), (wycc_obj**)&(rblk[3]), cnt);
    //return wycc_comp_array(&(lblk[3]), &(rblk[3]), lcnt);
}

/*
 * a not quite simple comparison of two records
 * a record with fewer fields is always smaller
 * a 
 */
static int wycc_comp_rmeta(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_record lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_record rhs");
    int ltok = (int) lhs->ptr;
    int rtok = (int) rhs->ptr;
    int end;

    if (lhs->typ != Wy_Rcd_Rcd) {
	WY_PANIC("FAIL wycc_comp_meta called for type %d\n", lhs->typ)
    }
    if (rhs->typ != Wy_Rcd_Rcd) {
	WY_PANIC("FAIL wycc_comp_meta called for type %d\n", rhs->typ)
    }
    if (ltok == rtok) {
	return 0;
    };
    if (ltok < rtok) {
	return -1;
    }
    return 1;
}

#if 0
/*
 * here we assume that we cannot reasonably compare the value (contents)
 * of the object, so to force a comparison, we compare their locations
 * in memory.
 */
static int wycc_comp_obj(wycc_obj* lhs, wycc_obj* rhs){
    fprintf(stderr, "Warning: wycc_comp_obj is deprecated.\n");
    if (lhs < rhs) {
	return -1;
    } else if (lhs > rhs) {
	return 1;
    };
    return 0;
}
#endif




/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
