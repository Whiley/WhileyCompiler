/*
 * wyil_substr.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This covers only the single primative wyil_substring .
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
 * return a string extracted from the given string 
 */
wycc_obj* wyil_substring(wycc_obj* str, wycc_obj* loo, wycc_obj* hio){
    WY_OBJ_SANE(str, "wyil_substrin str");
    WY_OBJ_SANE(loo, "wyil_substrin loo");
    WY_OBJ_SANE(hio, "wyil_substrin hio");
    wycc_obj* ans;
    size_t siz;
    long lo, hi, max;
    char *buf;
    char *sptr;

    if ((str->typ != Wy_String) && (str->typ != Wy_CString)) {
	WY_PANIC("Help needed in wyil_substring for type %d\n", str->typ)
    };
    if (loo->typ != Wy_Int) {
	WY_PANIC("Help needed in wyil_substring for type %d\n", loo->typ)
    };
    if (hio->typ != Wy_Int) {
	WY_PANIC("Help needed in wyil_substring for type %d\n", hio->typ)
    };
    lo = (long) loo->ptr;
    hi = (long) hio->ptr;
    if (lo > hi) {
	WY_PANIC("Help needed in wyil_substring for limits %d:%d\n", lo, hi)
    };
    siz = hi - lo;
    sptr = (char *) str->ptr;
    max = strlen(sptr);
    if (hi > max) {
	WY_PANIC("Help needed in wyil_substring for max %d:%d\n", hi, max)
    };
    if (lo < 0) {
	WY_PANIC("Help needed in wyil_substring for min %d\n", lo)
    };
    buf = (char *) malloc(siz + 3);
    if (siz > 0) {
	strncpy(buf, (sptr+lo), siz);
    }
    buf[siz] = 0;
    return wycc_box_str(buf);
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
