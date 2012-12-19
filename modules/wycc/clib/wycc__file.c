/*
 * wycc_file.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc).  This includes
 * all the native routines from File.whiley 
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
 * given a string open a file of that name, to use for writing
 */
wycc_obj *wycc__Writer(wycc_obj *fnam) {
    WY_OBJ_SANE(fnam, "wycc__Writer");
    wycc_obj* ans = NULL;
    FILE *fil;

    if ((fnam->typ != Wy_String) && (fnam->typ != Wy_CString)) {
	WY_PANIC("Help needed in wycc__Writer for type %d\n", fnam->typ)
    };
    fil = fopen(fnam->ptr, "w");
    if (fil == NULL) {
	WY_PANIC("Failed to open '%s' for writing\n", fnam->ptr)
    };
    ans = wycc_box_new(Wy_Addr, fil);
    return ans;
}

/*
 * given a string open a file of that name, to use for reading
 */
wycc_obj *wycc__Reader(wycc_obj *fnam) {
    WY_OBJ_SANE(fnam, "wycc__Reader");
    wycc_obj* ans = NULL;
    FILE *fil;

    if ((fnam->typ != Wy_String) && (fnam->typ != Wy_CString)) {
	WY_PANIC("Help needed in wycc__Reader for type %d\n", fnam->typ)
    };
    fil = fopen(fnam->ptr, "r");
    if (fil == NULL) {
	WY_PANIC("Failed to open '%s' for reading\n", fnam->ptr)
    };
    ans = wycc_box_new(Wy_Addr, fil);
    return ans;
}

/*
 * given an object that addresses a FILE, close the file.
 */
void wycc__close(wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc__close");
    FILE *fil;
    int sts;

    if (itm->typ != Wy_Addr) {
	WY_PANIC("Help needed in wycc__close for type %d\n", itm->typ)
    };
    fil = (FILE *) itm->ptr;
    sts = fclose(fil);
    if (sts != 0) {
	WY_PANIC("Failed to close file\n")
    };
}

/*
 * given an object that addresses a FILE, close the file.
 */
wycc_obj* wycc__read(wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc__read");
    FILE *fil;
    size_t siz;
    char chr;
    long tmp;
    wycc_obj *mbr;
    wycc_obj *lst;

    if (itm->typ != Wy_Addr) {
	WY_PANIC("Help needed in wycc__close for type %d\n", itm->typ)
    };
    fil = (FILE *) itm->ptr;
    lst = wycc_list_new(10);
    while (feof(fil) == 0) {	/* could be skipped */
	siz = fread(&chr, 1, 1, fil);
	if (siz != 1) {
	    if (feof(fil)) {
		break;
	    };
	    WY_PANIC("Failed to read byte from file\n")
	};
	tmp = (long) chr;
	mbr = wycc_box_new(Wy_Char, (void *) tmp);
	wycc_list_add(lst, mbr);
    }
    return lst;
}

static void __initor_b() {
// filling in type registry array goes here
// Here goes code to fill the FOM registry
    wycc_register_routine("Writer", "[^[.a],v,s]", wycc__Writer);
    wycc_register_routine("Writer", "[:[.[{fileName,writer}s,i]],v,s]", wycc__Writer);
    wycc_register_routine("Reader", "[^[.a],v,s]", wycc__Reader);
    wycc_register_routine("Reader", "[:[.[{fileName}s]],v,s]", wycc__Reader);
    wycc_register_routine("close", "[^v,v,[.a]]", wycc__close);
    wycc_register_routine("close", "[:v,v,[.[{fileName,writer}s,i]]]", wycc__close);
    wycc_register_routine("close", "[:v,v,[.[{fileName}s]]]", wycc__close);
    wycc_register_routine("read", "[:[#d],v,[.a]]", wycc__read);
    wycc_register_routine("read", "[:[#d],v,[.[{fileName}s]]]", wycc__read);
    return;
}

static void __initor_d() {
// Here goes code to query the FOM registry
    return;
}

static wycc_initor __initor_c;
__attribute__ ((constructor)) static void __initor_a() {
	__initor_c.nxt = wycc_init_chain;
	__initor_c.functionr = __initor_b;
	__initor_c.functionq = __initor_d;
	wycc_init_chain = &__initor_c;
	return;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
