/*
 * wyil_listsub.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * This covers only the single primative wyil_list_sub .
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
 * create a list that combines two lists
 */
wycc_obj* wyil_list_sub(wycc_obj* lst, wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lst, "wyil_list_sub lst");
    WY_OBJ_SANE(lhs, "wyil_list_sub lhs");
    WY_OBJ_SANE(rhs, "wyil_list_sub rhs");

    if (lst->typ != Wy_List) {
	WY_PANIC("ERROR: list in wyil_list_sub is type %d\n", lst->typ)
    };
    if (lhs->typ != Wy_Int) {
	WY_PANIC("ERROR: low in wyil_list_sub is type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_Int) {
	WY_PANIC("ERROR: hi in wyil_list_sub is type %d\n", rhs->typ)
    };
    return wycc_list_slice(lst, (int) lhs->ptr, (int) rhs->ptr);
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
