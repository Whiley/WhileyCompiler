/*
 * wyil_index.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This covers only the single primative wyil_index_of .
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
#include <string.h>


static wycc_obj* wyil_index_of_list(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_index_of_list lhs");
    WY_OBJ_SANE(rhs, "wyil_index_of_list rhs");
    void** p = lhs->ptr;
    wycc_obj* ans;
    long idx;

    if (rhs->typ != Wy_Int) {
	WY_PANIC("Help needed in wyil_index_of_list for type %d\n", rhs->typ)
    };
    idx = (long) rhs->ptr;
    if (idx < 0) {
	fprintf(stderr, "ERROR: IndexOf under range for list (%d)\n", idx);
	exit(-4);
    };
    if (idx >= (long) p[0]) {
	fprintf(stderr, "ERROR: IndexOf over range for list (%d)\n", idx);
	exit(-4);
    };
    ans = (wycc_obj*) p[3+idx];
    ans->cnt++;
    return ans;
}

static wycc_obj* wyil_index_of_map(wycc_obj* map, wycc_obj* key){
    WY_OBJ_SANE(map, "wyil_index_of_map map");
    WY_OBJ_SANE(key, "wyil_index_of_map key");
    wycc_obj* ans;

    ans = wycc_index_of_map(map, key);
    if (ans != NULL) {
	return ans;
    }
    fprintf(stderr, "ERROR: key not found IndexOf map \n");
    exit(-4);

}
static wycc_obj* wyil_index_of_string(wycc_obj* str, wycc_obj* index){
    WY_OBJ_SANE(str, "wyil_index_of_string str");
    WY_OBJ_SANE(index, "wyil_index_of_string index");
    char *txt;
    long idx;
    char rslt;

    if (index->typ != Wy_Int) {
	WY_PANIC("Help needed in wyil_index_of_string for type %d\n"
		 , index->typ)
    };
    idx = (long) index->ptr;
    if (idx < 0) {
	fprintf(stderr, "ERROR: IndexOf under range for string (%d)", idx);
	exit(-4);
    };
    txt = str->ptr;
    if (idx >= strlen(txt)) {
	fprintf(stderr, "ERROR: IndexOf over range for string (%d)", idx);
	exit(-4);
    };
    rslt = txt[idx];
    return wycc_box_char(rslt);
}

wycc_obj* wyil_index_of(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_index_of lhs");
    WY_OBJ_SANE(rhs, "wyil_index_of rhs");
    wycc_obj* ans;

    if (lhs->typ == Wy_List) {
	return wyil_index_of_list(lhs, rhs);
    };
    if (lhs->typ == Wy_Tuple) {
	return wyil_index_of_list(lhs, rhs);
    };
    if (lhs->typ == Wy_Map) {
	return wyil_index_of_map(lhs, rhs);
    };
    if (lhs->typ == Wy_String) {
	return wyil_index_of_string(lhs, rhs);
    };
    if (lhs->typ == Wy_CString) {
	return wyil_index_of_string(lhs, rhs);
    };
    WY_PANIC("Help needed in wyil_index_of for type %d\n", lhs->typ)
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
