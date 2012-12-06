/*
 * wycc_indirect_invoke.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc).
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
#include "FOM.h"
#include "box.h"

/*
 * given a FOM (reference) object, and list object,
 * invoke the FOM using the list members as arguments.
 */
wycc_obj * wycc_indirect_invoke(wycc_obj *who, wycc_obj *lst) {
    WY_OBJ_SANE(who, "wycc_indirect_invoke who");
    WY_OBJ_SANE(lst, "wycc_indirect_invoke lst");
    void **pw = who->ptr;
    void **pl = lst->ptr;
    int cnt;
    int tok;
    int cnt2;
    wycc_obj *tmp;
    wycc_obj *ans;
    char *txt;
    void *rtn;

    if (who->typ != Wy_FOM) {
	WY_PANIC("Help needed in wycc_indirect_invoke for who type %d\n"
		, who->typ)
    };
    if (lst->typ != Wy_List) {
	WY_PANIC("Help needed in wycc_indirect_invoke for lst type %d\n"
		, lst->typ)
    };
    tok = (int) pw[2];
    cnt = wycc_type_child_count(tok) - 2;
    cnt2 = (int) pl[0];
    if (wycc_experiment_flag) {
	tmp = (wycc_obj *) pw[0];
	txt = (char *) tmp->ptr;
	fprintf(stderr, "wycc_indirect_invoke to invoke %s\n", txt);
	fprintf(stderr, "\twith %d:%d arguments\n", cnt, cnt2);
    };
    if (cnt != cnt2) {
	WY_PANIC("Help needed wycc_indirect_invoke incomplete\n")
    };
    rtn = pw[3];
    switch (cnt) {
    case 1:
	ans = ((FOM_1a) rtn)(pl[3]);
	return ans;
    }
    WY_PANIC("Help needed wycc_indirect_invoke incomplete %d\n", cnt)
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
