/*
 * wyil_deref.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc).  This
 * covers only the single primative wyil_dereference .
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
 * given a reference object, return the thing referred to.
 */
wycc_obj* wyil_dereference(wycc_obj* itm){
    WY_OBJ_SANE(itm, "wyil_dereference");
    wycc_obj* ans;

    if (itm->typ != Wy_Ref1) {
	WY_PANIC("Help needed in wyil_dereference 1 for type %d\n", itm->typ)
    };
    ans = (wycc_obj*) itm->ptr;
    if (ans->typ != Wy_Ref2) {
	WY_PANIC("Help needed in wyil_dereference 2 for type %d\n", ans->typ)
    };
    ans = (wycc_obj*) ans->ptr;
    ans->cnt++;
    return ans;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
