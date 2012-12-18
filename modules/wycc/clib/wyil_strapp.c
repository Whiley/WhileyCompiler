/*
 * wyil_strapp.c
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
 * given two strings, return their concatination
 */
wycc_obj* wyil_strappend(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_strappend lhs");
    WY_OBJ_SANE(rhs, "wyil_strappend rhs");
    size_t siz, sizl, sizr;
    char* rslt;
    wycc_obj* ans;
    char lbuf[4], rbuf[4];
    char *lp, *rp;
    int x;

    /* **** do we need to support Wy_Char ? */
    if (lhs->typ == Wy_Char) {
	lp = lbuf;
	x = (int) lhs->ptr;
	*lp = (char) x;
	lp[1] = '\0';
	sizl = 1;
    } else if ((lhs->typ != Wy_String) && (lhs->typ != Wy_CString)) {
	WY_PANIC("Help needed in strappend for type %d\n", lhs->typ)
    } else {
	lp = lhs->ptr;
	sizl = strlen(lp); 
    };
    if (rhs->typ == Wy_Char) {
	rp = rbuf;
	x = (int) rhs->ptr;
	*rp = (char) x;
	rp[1] = '\0';
	sizr = 1;
    } else if ((rhs->typ != Wy_String) && (rhs->typ != Wy_CString)) {
	WY_PANIC("Help needed in strappend for type %d\n", rhs->typ)
    } else {
	rp = rhs->ptr;
	sizr = strlen(rp);
    };
    siz = sizl + sizr + 2;		/* pad for terminator and spare */
    rslt = (char*) malloc(siz);
    strncpy(rslt, lp, sizl);
    strncpy(rslt+sizl, rp, sizr+1);
    return wycc_box_str(rslt);
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
