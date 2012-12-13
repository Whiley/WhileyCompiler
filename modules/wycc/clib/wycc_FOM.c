/*
 * wycc_FOM.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc).  This portion of
 * the library provides support for FOM (Function Or Method) instances.
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
#include "ytype.h"
#include "box.h"

int FOM_max_arg_count = 0;
wycc_obj **list_FOM = NULL;
wycc_obj *map_FOM = NULL;

/*
 * given an address, box it in a wycc_obj
 */
static wycc_obj* wycc_box_fom(wycc_obj *nam, wycc_obj *sig, int tok, void* ptr) {
    void **p;

    p = (void **) calloc(4, sizeof(void *));
    p[0] = (void *) nam;
    p[1] = (void *) sig;
    p[2] = (void *) tok;
    p[3] = (void *) ptr;
    return wycc_box_new(Wy_FOM, (void *) p);
}


static wycc_obj * wycc_register_argmap_get(int cnt) {
    wycc_obj* ans;

    if (list_FOM == NULL) {
	FOM_max_arg_count = 16;
	list_FOM = (wycc_obj **) calloc(16, sizeof(wycc_obj *));
    };
    if (cnt >= FOM_max_arg_count) {
	// **** need to expand the list or doing something really special
	// don't forget to zero out the expansion.
	WY_PANIC("Help needed in wycc_register_map_get (%d)\n", cnt)
    };
    ans = list_FOM[cnt];
    if (ans == NULL) {
	ans = wycc_map_new(-1);
	list_FOM[cnt] = ans;
    };
    return ans;
}

static wycc_obj * wycc_register_nammap_get(int cnt) {
    wycc_obj* ans;

    if (list_FOM == NULL) {
	FOM_max_arg_count = 16;
	list_FOM = (wycc_obj **) calloc(16, sizeof(wycc_obj *));
    };
    if (cnt >= FOM_max_arg_count) {
	// **** need to expand the list or doing something really special
	// don't forget to zero out the expansion.
	WY_PANIC("Help needed in wycc_register_map_get (%d)\n", cnt)
    };
    ans = list_FOM[cnt];
    if (ans == NULL) {
	ans = wycc_map_new(-1);
	list_FOM[cnt] = ans;
    };
    return ans;
}
//void wycc_register_routine(const char *nam, int args, const char *rtyp
//			   , const char *sig
//			   , void* ptr) {
void wycc_register_routine(const char *nam, const char *sig, void* ptr) {
    int tok;
    size_t siz;
    int tmp;
    int idx;
    int args;
    void **p;
    wycc_obj *key;
    wycc_obj *sigMap;
    wycc_obj *txt;
    wycc_obj *fom;
    wycc_obj *adr;

    if (wycc_debug_flag || wycc_experiment_flag) {
	fprintf(stderr, "wycc_register(%s) => %s\n", nam, sig);
    };
    tok = wycc_type_internal(sig);
    tmp = wycc_type_child_count(tok);
    if (wycc_debug_flag || wycc_experiment_flag) {
	fprintf(stderr, "wycc_register(%d) => %d\n", tok, tmp);
    };
    //adr = wycc_box_addr(ptr);
    key = wycc_box_cstr(nam);
    txt = wycc_box_cstr(sig);
    adr = wycc_box_fom(key, txt, tok, ptr);
    sigMap = wycc_index_of_map(map_FOM, key);
    if (sigMap == NULL) {
	sigMap = wycc_map_new(-1);
	wycc_map_add(map_FOM, key, sigMap);
    };
    wycc_map_add(sigMap, txt, adr);
    wycc_deref_box(key, 0);
    wycc_deref_box(txt, 0);

    return;
}

wycc_obj* wycc_fom_handle(const char *nam, const char *sig){
    wycc_obj *txt;
    wycc_obj *sigMap;
    wycc_obj *adr;

    if (wycc_debug_flag || wycc_experiment_flag) {
	fprintf(stderr, "wycc_fom_handle(%s) => %s\n", nam, sig);
    };
    txt = wycc_box_cstr(nam);
    sigMap = wycc_index_of_map(map_FOM, txt);
    if (sigMap == NULL) {
	WY_PANIC("Help needed: wycc_fom_handle for unknown'%s''\n", nam)
    };
    if (wycc_debug_flag || wycc_experiment_flag) {
	fprintf(stderr, "wycc_fom_handle '%s' found\n", nam);
    };
    wycc_deref_box(txt, 0);
    txt = wycc_box_cstr(sig);
    adr = wycc_index_of_map(sigMap, txt);
    if (adr == NULL) {
	WY_PANIC("Help needed: wycc_fom_handle for unknown'%s:%s''\n", nam, sig)
    };

    //fprintf(stderr, "Help needed in wycc_fom_handle for '%s':'%s'\n"
    //	    , nam, sig);
    //exit(-3);
    adr->cnt++;
    return adr;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
