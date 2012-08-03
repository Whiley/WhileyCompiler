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
    /* primative string, ie. array of unboxed bytes*/
#define Wy_String	1
    "string",
    /* primative integer, it a long instead of pointer */
#define Wy_Int		2
    "int",
    /* a wide integer - unbounded */
#define Wy_WInt		3
    "wint",
    /* a primative list, ie. array of (boxed) objs */
#define Wy_List		4
    "list",
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
    ans->cnt = 1;
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
    ans->cnt = 1;
    ans->ptr = (void*) x;	/* **** kludge */
    return ans;
}

/*
 * given an long (64 bits), box it in a wycc_obj
 */
wycc_obj* wycc_box_long(long x) {
    wycc_obj* ans;

    ans = (wycc_obj*) malloc(sizeof(wycc_obj));
    ans->typ = Wy_Int;
    ans->cnt = 0;
    ans->ptr = (void*) x;	/* **** kludge */
    return ans;
}

static wycc_high_bit(long itm) {
    int tmpa, tmpb;

    tmpb = 0;
    for (tmpa= itm; tmpa > 0; tmpa &= (tmpa-1)) {
	tmpb = tmpa;
    }
    if (wycc_debug_flag) {
	fprintf(stderr, "wycc_high_bit(%d) => %d\n", itm, tmpb);
    }
    return tmpb;
}

/*
 * given a count, setup a list object big enough to accept them.
 * 
 */
wycc_obj* wycc_list_new(long siz) {
    wycc_obj* ans;
    long tmp;
    size_t raw;
    void** p;

    ans = (wycc_obj*) malloc(sizeof(wycc_obj));
    ans->typ = Wy_List;
    tmp = 2 * wycc_high_bit(siz+2);
    raw = tmp * sizeof(void *);
    p = (void**) malloc(raw);
    p[0] = (void *) 0;
    p[1] = (void *) raw;
    ans->ptr = (void *) p;
    ans->cnt = 1;
    return ans;
}

wycc_obj* wycc_list_get(wycc_obj* lst, long at) {
    void** p = lst->ptr;

    if (at >= (long) p[0]) {
	return NULL;
    };
    return (wycc_obj*) p[2+at];
}

void wycc_list_add(wycc_obj* lst, wycc_obj* itm) {
    void** p = lst->ptr;
    int at;
    size_t raw;

    raw = (size_t) p[1];
    at = ((long) p[0]) + 1;
    if ((at+2) >= raw) {
	raw *= 2;
	p = (void **) realloc(p, raw);
	if (p == NULL) {
	    fprintf(stderr, "ERROR: realloc failed\n");
	    exit(-4);
	};
	p[1] = (void *) raw;
	lst->ptr = p;
    };
    p[2 + at] = (void *) itm;
    itm->cnt++;
    return;
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
	if (wycc_debug_flag) {
	    fprintf(stderr, "note: deallocing box for typ %d\n", typ);
	};
	free(itm);
	if (flg != 0) {
	    wycc_dealloc_typ(ptr, typ);
	};
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

/*
 * given a numeric object return the size of the number
 * (how many longs it takes to represent it)
 */
static int wycc_wint_size(wycc_obj *itm) {
    if (itm->typ == Wy_Int) {
	return (size_t) 1;
    };
    fprintf(stderr, "Help needed in add for type %d\n", itm->typ);
    exit(-3);
}

wycc_obj* wyil_add(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc, ac;

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) + ((long)rhs->ptr); 
	return wycc_box_long(rslt);
    };
    fprintf(stderr, "Help needed in add for wide ints (%d)\n", ac);
    exit(-3);
}

wycc_obj* wyil_sub(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc, ac;

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) - ((long)rhs->ptr); 
	return wycc_box_long(rslt);
    };
    fprintf(stderr, "Help needed in add for wide ints (%d)\n", ac);
    exit(-3);
}

wycc_obj* wyil_bit_and(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc, ac;
    /* wycc_obj* ans;*/

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) & ((long)rhs->ptr); 
	return wycc_box_long(rslt);
    };
    fprintf(stderr, "Help needed in bit_and for wide ints (%d)\n", ac);
    exit(-3);
}

wycc_obj* wyil_bit_ior(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc, ac;
    /* wycc_obj* ans;*/

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) | ((long)rhs->ptr); 
	return wycc_box_long(rslt);
    };
    fprintf(stderr, "Help needed in bit_or for wide ints (%d)\n", ac);
    exit(-3);
}

wycc_obj* wyil_bit_xor(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc, ac;
    /* wycc_obj* ans;*/

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) ^ ((long)rhs->ptr); 
	return wycc_box_long(rslt);
    };
    fprintf(stderr, "Help needed in bit_xor for wide ints (%d)\n", ac);
    exit(-3);
}

wycc_obj* wyil_div(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc, ac;

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) / ((long)rhs->ptr); 
	return wycc_box_long(rslt);
    };
    fprintf(stderr, "Help needed in div for wide ints (%d)\n", ac);
    exit(-3);
}

wycc_obj* wyil_mod(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc, ac;

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) % ((long)rhs->ptr); 
	return wycc_box_long(rslt);
    };
    fprintf(stderr, "Help needed in div for wide ints (%d)\n", ac);
    exit(-3);
}

static int wycc_ilog2(long itm){
    int ans = 0;

    if (itm < 0) {
	itm *= -1;
    };
    while (itm > 0) {
	ans++;
	itm >>= 1;
    }
    return ans;
}

wycc_obj* wyil_mul(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc, ac;

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac > 1) {
	fprintf(stderr, "Help needed in mul for wide ints (%d)\n", ac);
	exit(-3);
    };
    rslt = (long) lhs->ptr;
    lc = wycc_ilog2(rslt);
    rc = wycc_ilog2((long) rhs->ptr);
    if ((lc + rc) > 62) {
	fprintf(stderr, "Help needed in mul for wide ints (2)\n");
	exit(-3);
    };
    rslt *= (long)rhs->ptr; 
    return wycc_box_long(rslt);
}

wycc_obj* wyil_shift_up(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc;

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    if (rc > 1) {
	fprintf(stderr, "ERROR shift to exceed memory\n");
	exit(-4);
    }
    if (lc > 1) {
	fprintf(stderr, "Help needed in shift_up for wide ints (%d)\n", lc);
	exit(-3);
    }
    rslt = (long) rhs->ptr;
    if (rslt > 60*8) {
	fprintf(stderr, "ERROR shift to exceed memory\n");
	exit(-4);
    }
    if (rslt > 60) {
	rslt += 63;
	rslt /= 64;
	fprintf(stderr, "Help needed in shift_up for wide ints (%d)\n", rslt);
	exit(-3);
    }
    rc = rslt;
    rslt = (long) lhs->ptr;
    rslt <<= rc;
    return wycc_box_long(rslt);
}

wycc_obj* wyil_shift_down(wycc_obj* lhs, wycc_obj* rhs){
    long rslt;
    int lc, rc;

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    if (rc > 1) {
	rslt = 0;
	return wycc_box_long(rslt);
    }
    if (lc > 1) {
	fprintf(stderr, "Help needed in shift_up for wide ints (%d)\n", lc);
	exit(-3);
    }
    rslt = (long) rhs->ptr;
    if (rslt > 64) {
	rslt = 0;
	return wycc_box_long(rslt);
    }
    rc = rslt;
    rslt = (long) lhs->ptr;
    rslt >>= rc;
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
