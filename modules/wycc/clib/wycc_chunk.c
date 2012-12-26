/*
 * wycc_chunk.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This handles the support routines for chunked data structures.
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
#include <string.h>

/*
 * use the data in   an item to fill in a chunk pointer (structure).
 */
void wycc_chunk_ptr_fill_as(struct chunk_ptr *ptr, wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc_chunk_ptr_fill_as");
    int typ = itm->typ;

    if (typ == Wy_Set) {
	return wycc_chunk_ptr_fill(ptr, itm, 0);
    };
    if (typ == Wy_String) {
	return wycc_chunk_ptr_fill(ptr, itm, 4);
    };
    if (typ == Wy_CString) {
	return wycc_chunk_ptr_fill(ptr, itm, 4);
    };
    if (typ == Wy_List) {
	return wycc_chunk_ptr_fill(ptr, itm, 2);
    };
    if (typ == Wy_Tuple) {
	return wycc_chunk_ptr_fill(ptr, itm, 2);
    };
    if (typ == Wy_Map) {
	return wycc_chunk_ptr_fill(ptr, itm, 1);
    };
    WY_PANIC("Help needed in wycc_chunk_ptr_fill_as for type %d\n", typ)
}

void wycc_chunk_ptr_fill(struct chunk_ptr *ptr, wycc_obj *itm, int typ) {
    WY_OBJ_SANE(itm, "wycc_chunk_ptr_fill");
    void** p = itm->ptr;
    void** chunk;
    int cnt;

    cnt = (int) p[0];
    ptr->cnt = cnt;
    ptr->p = p;
    if (typ == 4) {	/* string*/
	chunk = p;
    } else if (typ == 0) {	/* set */
	chunk = &(p[2]);
    } else {	/* list, tuple, map */
	chunk = &(p[3]);
    }
    //    chunk = &(p[2]);
    ptr->chk = chunk;
    ptr->at = 0;
    ptr->flg = typ;
    return;
}

/*
 * advance a pointer structure (into the chunks of sets or maps or lists)
 */
void wycc_chunk_ptr_inc(struct chunk_ptr *chunk) {
    int brnch;
    int step;
    int max;
    int tmp;
    char cc;
    char *cp;
    wycc_obj *itm;
    void ** up;
    void ** was;
    void ** dn;

    chunk->key = NULL;
    chunk->val = NULL;
    if (chunk->cnt-- < 1) {
	chunk->cnt = 0;
	return;
    }
    if (chunk->flg == 0) {
	step = 1;	/* a key */
	max = WYCC_SET_CHUNK;
    } else if (chunk->flg == 1) {
	step = 2;	/* a key and a value */
	max = WYCC_MAP_CHUNK;
    } else if (chunk->flg == 2) {
	tmp = chunk->at++;
	chunk->idx = tmp;
	itm = (wycc_obj *) chunk->chk[tmp];
	//chunk->key = (wycc_obj *) NULL;
	chunk->val = itm;
	return;
    } else if (chunk->flg == 4) {
	cp = (char *) chunk->chk;
	tmp = chunk->at++;
	max = strlen(cp);
	if (tmp >= max) {
	    return;
	};
	cc = cp[tmp];
	chunk->val = wycc_box_char(cc);
	return;
    } else {
	WY_PANIC("HELP: bad chunk->typ chunk_ptr_inc (%d)\n", chunk->flg)
    };
    if (chunk->at == 0) {
	chunk->idx = 0;
    };
    tmp = chunk->idx;
    /*
     * pointing at a key, next is ?
     */
    while  (1) {
	brnch = (int) chunk->chk[0] * (step + 1);
	tmp++;
	if (tmp >=brnch) {
	    break;
	};
	dn = (void **) chunk->chk[tmp];
	if (dn == NULL) {
	    WY_PANIC("HHEELLPP: confused in chunk_ptr_inc; 2 deep\n")
	};
	chunk->chk = dn;
	tmp = 0;
    }
    /*
     * we are now pointing beyond the branches.
     */
    while (1) {
	//if (tmp < (max-1)) {
	if (tmp < (max-step)) {
	    itm = (wycc_obj *) chunk->chk[tmp];
	    if (itm != NULL) {
		break;
	    };
	};
	bp();
	/*
	 * exhausted a chunk (maybe several)
	 */
	was = (void **) chunk->chk;
	up = (void **) chunk->chk[max-1];
	chunk->chk = up;
	brnch = (int) up[0] * (step + 1);
	for (tmp= 1; tmp < brnch ; tmp += (step + 1)) {
	    if (up[tmp] == was) {
		break;
	    };
	}
	if (up[tmp] != was) {
	    WY_PANIC("HHEELLPP: confused in chunk_ptr_inc\n")
	};
	tmp++;
    }
    /*
     * we found a non-null item
     */
    if (step > 1) {
	chunk->val = itm;
	tmp++;
	itm = (wycc_obj *) chunk->chk[tmp];
    };
    chunk->idx = tmp;
    chunk->key = itm;
    chunk->at++;
    return;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
