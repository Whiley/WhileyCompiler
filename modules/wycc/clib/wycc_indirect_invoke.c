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


static wycc_obj * wycc_lambda_invoke(wycc_obj *who, wycc_obj *lst);

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

    if (lst->typ != Wy_List) {
	WY_PANIC("Help needed in wycc_indirect_invoke for lst type %d\n"
		, lst->typ)
    };
    if (who->typ == Wy_Lambda) {
	return wycc_lambda_invoke(who, lst);
    };
    if (who->typ != Wy_FOM) {
	WY_PANIC("Help needed in wycc_indirect_invoke for who type %d\n"
		, who->typ)
    };
    tok = (int) pw[2];
	// 2 of the type children are for the return value and exceptions
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
    case 2:
	ans = ((FOM_1a) rtn)(pl[3], pl[4]);
	return ans;
    case 3:
	ans = ((FOM_1a) rtn)(pl[3], pl[4], pl[5]);
	return ans;
    case 4:
	ans = ((FOM_1a) rtn)(pl[3], pl[4], pl[5], pl[6]);
	return ans;
    }
    WY_PANIC("Help needed wycc_indirect_invoke incomplete %d\n", cnt)
}

/*
 * given a Lambda object, and a list of "missing" arguments,
 * invoke the FOM using the combined lists.
 *
 * done crudely.
 */
static wycc_obj * wycc_lambda_invoke(wycc_obj *who, wycc_obj *lst) {
    void **p = who->ptr;
    void **pw;
    void **pla = lst->ptr;
    void **plc;
    int cnt;
    int tok;
    int cnt2;
    int cnta;
    int ata, atf;
    wycc_obj *tmp;
    wycc_obj *ans;
    wycc_obj *fom;
    wycc_obj *cls;
    char *txt;
    void *rtn;

    fom = (wycc_obj *) p[0];
    cls = (wycc_obj *) p[1];
    pw = fom->ptr;
    tok = (int) pw[2];
	// 2 of the type children are for the return value and exceptions
    cnt = wycc_type_child_count(tok) - 2;
    plc = cls->ptr;
    cnt2 = (int) plc[0];
    if (wycc_experiment_flag) {
	tmp = (wycc_obj *) pw[0];
	txt = (char *) tmp->ptr;
	fprintf(stderr, "wycc_indirect_invoke to invoke %s\n", txt);
	fprintf(stderr, "\twith %d:%d:%d arguments\n", cnt, cnt2, (int)pla[0]);
    };

    tmp = wycc_list_new(cnt);
    atf = 0;
    ata = 0;
    ans = NULL;
    cnta = (int) pla[0];
    while (atf < cnt) {
	if (atf < cnt2) {
	    ans = plc[3+atf];
	};
	if ((ans == NULL) && (ata < cnta)) {
	    ans = pla[3+ata];
	    ata++;
	};
	if (ans == NULL) {
	    txt = (char *) tmp->ptr;
	    WY_PANIC("Help: insufficient arguments for wycc_lambda_invoke with %s\n", txt);
	};
	wycc_list_add(tmp, ans);
	atf++;
    }
    return wycc_indirect_invoke(fom, tmp);
    WY_PANIC("Help needed wycc_lambda_invoke incomplete\n")
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
