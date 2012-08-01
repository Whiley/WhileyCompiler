/*
 * wycc_lib.c
 *
 * This is a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
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

#include "../lib/wycc_lib.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
 * ******************************
 * wycc infrastructure
 * ******************************
 */

/*
 * just in case we need to refer to them from some routine other than main
 */
static int	orig_argc;
static char**	orig_argv;
static char**	orig_envp;

/*
 * storage for the type registry.
 * indexes are off by 1 - to reserve 0 for not assigned.
 */

static char* wy_type_names[] = {
#define Wy_String	1
    "string",
#define Wy_Int		2
    "int",
    (char *) NULL
};

wycc_initor* wycc_init_chain = NULL;

/*
 * wycc+wyil needs a unix/c standard starting routine.
 * **** to be changed ****
 * * whiley may have more than one main (with different signatures
 *    and may need to have them registered from each .o file linked in.
 * * these routines and all the other .o files need to agree about
 *    various type information.
 */
int main(int argc, char** argv, char** envp) {
    int idx;
    char* argp;
    wycc_initor* ini;

    orig_argc = argc;
    orig_argv = argv;
    orig_envp = envp;
    wycc_debug_flag = 0;
    for (idx=1; idx < argc ; idx++) {
	argp = argv[idx];
	if (strcmp(argp, "-D") == 0) {
	    wycc_debug_flag = 1;
	};
    };
    if (wycc_debug_flag) {
	if (wycc_init_chain == NULL) {
	    fprintf(stderr, "wycc_init_chain is null\n");
	};
    };
    for (ini= wycc_init_chain; ini != NULL; ) {
	ini->function();
	ini = (wycc_initor*)ini->nxt;
    };
    wycc_main();
    return 0;
}

/*
 * given a text string, box it in a wycc_obj
 */
wycc_obj* wycc_box_str(char* text) {
    wycc_obj* ans;

    ans = (wycc_obj*) malloc(sizeof(wycc_obj));
    /* ans->typ = 1; */
    ans->typ = Wy_String;
    ans->cnt = 0;
    ans->ptr = (void*) text;
    return ans;
}

/*
 * given an int (32 bits), box it in a wycc_obj
 */
wycc_obj* wycc_box_int(int x) {
    wycc_obj* ans;

    ans = (wycc_obj*) malloc(sizeof(wycc_obj));
    /* ans->typ = 2; */
    ans->typ = Wy_Int;
    ans->cnt = 0;
    ans->ptr = (void*) x;	/* **** kludge */
    return ans;
}

/*
 * given an long (64 bits), box it in a wycc_obj
 */
wycc_obj* wycc_box_long(long x) {
    wycc_obj* ans;

    ans = (wycc_obj*) malloc(sizeof(wycc_obj));
    /* ans->typ = 2; */
    ans->typ = Wy_Int;
    ans->cnt = 0;
    ans->ptr = (void*) x;	/* **** kludge */
    return ans;
}

/*
 * given a wycc_obj, reduce the reference count.
 * when there are no more references reclaim the box.
 * if the flag is set (not zero), reclaim the underlying data.
 */

static void wycc_dealloc_typ(void* ptr, int typ);
wycc_obj* wycc_deref_box(wycc_obj* itm, int flg) {
    void* ptr;
    int typ;

    if (--itm->cnt <= 0) {
	ptr = itm->ptr;
	typ = itm->typ;
	free(itm);
	if (flg != 0) {
	    wycc_dealloc_typ(ptr, typ);
	}
	return (wycc_obj *) NULL;
    }
    return itm;
}

static void wycc_dealloc_typ(void* ptr, int typ){
    /* if (typ == 1) { */
    if (typ == Wy_String) {
        free (ptr);
    } else {
	fprintf(stderr, "ERROR: unrecognized type (%d) in dealloc\n", typ);
	exit(-3);
    }
}


/*
 * ******************************
 * wyil opcode implementations
 * ******************************
 */

/*
 * debug using an unboxed string
 */
void wyil_debug_str(char* mesg) {
    ;
    fprintf(stderr, "%s\n", mesg);
    return;
}

/*
 * debug using a wycc_obj
 */
void wyil_debug_obj(wycc_obj* ptr1) {
    char* mesg;
    /* if (ptr1->typ == 1) { */
    if (ptr1->typ == Wy_String) {
	fprintf(stderr, "%s\n", ptr1->ptr);
    } else {
	fprintf(stderr, "Help needed in Debug for type %d\n", ptr1->typ);
    };
    return;
}

wycc_obj* wyil_strappend(wycc_obj* lhs, wycc_obj* rhs){
    size_t siz, sizl, sizr;
    char* rslt;
    wycc_obj* ans;

    /* if (lhs->typ != 1) { */
    if (lhs->typ != Wy_String) {
	fprintf(stderr, "Help needed in strappend for type %d\n", lhs->typ);
	exit(-3);
    };
    /* if (rhs->typ != 1) { */
    if (rhs->typ != Wy_String) {
	fprintf(stderr, "Help needed in strappend for type %d\n", rhs->typ);
	exit(-3);
    };
    sizr = strlen(rhs->ptr);
    sizl = strlen(lhs->ptr); 
    siz = sizl + sizr + 2;		/* pad for terminator and spare */
    rslt = (char*) malloc(siz);
    strncpy(rslt, lhs->ptr, sizl);
    strncpy(rslt+sizl, rhs->ptr, sizr+1);
    return wycc_box_str(rslt);
}

wycc_obj* wyil_add(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    wycc_obj* ans;

    if (lhs->typ != Wy_Int) {
	fprintf(stderr, "Help needed in add for type %d\n", lhs->typ);
	exit(-3);
    };
    if (rhs->typ != Wy_Int) {
	fprintf(stderr, "Help needed in add for type %d\n", rhs->typ);
	exit(-3);
    };
    rslt = ((long) rhs->ptr) + ((long)lhs->ptr); 
    return wycc_box_long(rslt);
}

/*
 * ******************************
 * whiley standard library ie., native methods
 * ******************************
 */
wycc_obj* toString(wycc_obj* itm) {
    size_t siz;
    int tmp;
    char *buf;

    if (itm->typ == 1) {
	itm->cnt++;
	return itm;
    }
    if (itm->typ != 2) {
	return wycc_box_str("Unknown");
    }
    tmp = (int) itm->ptr;
    buf = (char *) malloc(16);
    sprintf(buf, "%-14.1d", tmp);
    return wycc_box_str(buf);
}


/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
