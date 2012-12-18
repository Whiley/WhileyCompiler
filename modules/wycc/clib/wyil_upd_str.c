/*
 * wyil_upd_str.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This covers only the single primative wyil_strappend .

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


/*
 * update an element of a string
 */
wycc_obj* wyil_update_string(wycc_obj* str, wycc_obj* osv, wycc_obj* rhs){
    WY_OBJ_SANE(str, "wyil_update_string str");
    WY_OBJ_SANE(osv, "wyil_update_string osv");
    WY_OBJ_SANE(rhs, "wyil_update_string rhs");
    char *txt;
    long lsiz, idx;
    int tmp;
    wycc_obj *swp;
    wycc_obj *was = str;

    if ((str->typ != Wy_String) && (str->typ != Wy_CString)) {
	WY_PANIC("ERROR: string in wyil_update_string is type %d\n", str->typ)
    };
    if (osv->typ != Wy_Int) {
	WY_PANIC("ERROR: offset  value in wyil_update_string is type %d\n"
		, osv->typ)
    };
    if (rhs->typ != Wy_Char) {
	fprintf(stderr
		, "ERROR: replacement value in wyil_update_string is type %d\n"
		, rhs->typ);
	exit(-3);
    };
    if (str->typ == Wy_CString) {
	swp = str;
	str = wycc_cow_string(swp);
	wycc_deref_box(swp, 0);
	//} else if (str->cnt > 1) {
	//swp = str;
	//str = wycc_cow_string(swp);
	//wycc_deref_box(swp);
    };
    txt = str->ptr;
    lsiz = strlen(txt);
    idx = (long) osv->ptr;
    if ((idx < 0) || (idx >= lsiz)) {
	fprintf(stderr
		, "ERROR: out of bounds offset value in wyil_update_string\n");
	exit(-4);
    };
    if (str->cnt > 1) {
	str = wycc_cow_string(str);
    };
    if (str == was) {
	str->cnt++;
    };
    tmp = (int) rhs->ptr;
    txt = str->ptr;
    txt[idx] = (char) tmp;
    return str;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
