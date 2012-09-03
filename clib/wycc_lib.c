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

/*
 * to do:
 * * set up main method registry so that there can be multiples
 * * set up type registry routines.
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

#ifndef WY_OBJ_SAFE
int wycc__mile__stone = 0;
#endif

/*
 * storage for the type registry.
 * indexes were off by 1 - to reserve 0 for not assigned.
 * now Any is 0
 * and None is -1
 * simple objects of type any or none have no contents
 * complex objects with subtype none have no contents
 * complex objects with subtype any must accept all other types
 */

static char* wy_type_names[] = {
    /* None */
#define Wy_None	-1
    /* Any */
#define Wy_Any	0
    "any",
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
    /* a set */
#define Wy_Set		5
    "set",
    /* a map */
#define Wy_Map		6
    "map",
    /* a record */
#define Wy_Record	7
    "record",
    /*the field names of a record */
#define Wy_Fields	8
    "fields",
    /* a constant string (in code space, not heap */
#define Wy_CString	9
    "string",
    /* a single character */
#define Wy_Char		10
    "char",
    /* a boolean value */
#define Wy_Bool		11
    "bool",
    /* an iterator */
#define Wy_Iter		12
    "iterator",
    /* a NULL */
#define Wy_Null		13
    "null",
    /* a raw record, no names or field tpyes registered*/
#define Wy_RRecord		14
    "raw record",
    /* a reference to system resources */
#define Wy_Ref		15
    "reference",
    /* a record of the meta data for a variety of record. */
    /* a pointer to which changes a raw record into a record. */
#define Wy_Rcd_Rcd		16
    "record meta",
#define Wy_Type_Max	16
    (char *) NULL
};

/*
 * DETAILED OBJECT DESCRIPTIONS
 *
 * ie, what does obj->ptr really point to.
 *
 * Wy_None	
 * Wy_Any	ptr must be void
 * Wy_CString
 * Wy_String	ptr to null terminated char[].
 * Wy_Char	ptr is char
 * Wy_Bool
 * Wy_Int	ptr is long
 * wy_Ref	ptr is token
 * Wy_WInt	TBD
 * Wy_List	ptr to block:
 *		alloc size
 *		member count
 *		array of obj ptrs
 * Wy_Set	ptr to block:
 *		member type
 *		member count
 *		chunk:
 *			branch count
 *			* {
 *				branch link
 *				value
 *			}
 *			value ...
 *			level_chunk link
 * Wy_Map	ptr to block:
 *		member type
 *		member count
 *		chunk:
 *			branch count
 *			* {
 *				branch link
 *				value
 *				key
 *			}
 *			{
 *				value
 *				key
 *			} ...
 *			level_chunk link
 * Wy_Iter	ptr to block: w/ block == struct chunk_ptr
 * Wy_RRecord
 * Wy_Record	ptr to block:
 *		registry index (-1 for raw records)
 *		size (pad so as match list)
 *		array of obj ptrs *
 * Wy_Rcd_Rcd	ptr to block:
 *		ptr to Wy_List of names
 *		ptr to Wy_List of types
 */

/*
 * we need comparitor functions for all object types
 */
static int wycc_comp_gen(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_str(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_int(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_wint(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_set(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_list(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_rmeta(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_record(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_obj(wycc_obj* lhs, wycc_obj* rhs);

typedef int (*Wycc_Comp_Ptr)(wycc_obj* lhs, wycc_obj* rhs);

wycc_initor* wycc_init_chain = NULL;

static void* wycc_fields_master = NULL;

void wycc_chunk_rebal(int payl, void** p, void** chunk, long at, long deep);

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
    wycc_obj* sys;
    wycc_obj* lst;
    wycc_obj* itm;

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
    //sys = wycc_box_int(1);	/* **** KLUDGE **** */

    itm = wycc_box_ref(1);
    sys = wycc_rrecord_new(2);
    wycc_record_fill(sys, 1, itm);
    wycc_deref_box(itm);
    lst = wycc_list_new(argc-1);
    for (idx=1; idx < argc ; idx++) {
	itm = wycc_box_cstr(argv[idx]);
	wycc_list_add(lst, itm);
    }
    wycc_record_fill(sys, 0, lst);
    wycc__main(sys);
    exit(0);
    return 0;
}

/* -------------------------------
 *  internal routines
 * -------------------------------
 */

/*
 * a disgusting little kludge routine to call from the middle of a routine
 * to use as a place for a breakpoint.
 */
static void bp(){
    static int i = 0;
    i++;
}

/*
 *
 */
static void wycc___bail(char *msg) {
    if (msg != (char *) NULL) {
	fprintf(stderr, "Notice: %s\n", msg);
    };
    if (wycc__mile__stone) {
	fprintf(stderr, "Notice: milestone is %d\n", wycc__mile__stone);
    };
    exit(-3);
}
/*
 *
 */
void wycc_obj_sane(wycc_obj *itm, char *msg){
    if (itm == (wycc_obj *) NULL) {
	return;
    }
    if (itm->cnt <=0) {
	fprintf(stderr, "FAILURE: ref count not positive (%d.%d@%p)\n"
		, itm->cnt, itm->typ, itm);
	wycc___bail(msg);
    }
    if (itm->typ <=0) {
	fprintf(stderr, "FAILURE: type not set (%d@%p)\n", itm->typ, itm);
	wycc___bail(msg);
    }
    if (itm->typ > Wy_Type_Max) {
	fprintf(stderr, "FAILURE: type bound exceeded (%d@%p)\n"
		, itm->typ, itm);
	wycc___bail(msg);
    }
    return;
}

void wycc_obj_bump(wycc_obj *itm) {
    WY_OBJ_SANE(itm, NULL);
    itm->cnt++;
    return;
}

/*
 *
 * given a long mask it down to its most significant bit
 */
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

/* -------------------------------
 *  Routines for basic support of typed objects
 * -------------------------------
 *
 * given a text string, box it in a wycc_obj
 */
wycc_obj* wycc_box_str(char* text) {
    wycc_obj* ans;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_String;
    ans->cnt = 1;
    ans->ptr = (void*) text;
    return ans;
}

/*
 * given a text string, box it in a wycc_obj
 */
wycc_obj* wycc_box_cstr(char* text) {
    wycc_obj* ans;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_CString;
    ans->cnt = 1;
    ans->ptr = (void*) text;
    return ans;
}

/*
 * given an char, box it in a wycc_obj
 */
wycc_obj* wycc_box_char(char x) {
    wycc_obj* ans;
    int tmp;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Char;
    ans->cnt = 1;
    tmp = (int) x;	/* **** kludge */
    ans->ptr = (void *) tmp;	/* **** kludge */
    return ans;
}

/*
 * given a boolean, box it in a wycc_obj
 */
wycc_obj* wycc_box_bool(int x) {
    wycc_obj* ans;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Bool;
    ans->cnt = 1;
    ans->ptr = (void *) x;	/* **** kludge */
    return ans;
}

/*
 * given an int (32 bits), box it in a wycc_obj
 */
wycc_obj* wycc_box_int(int x) {
    wycc_obj* ans;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Int;
    ans->cnt = 1;
    ans->ptr = (void*) x;	/* **** kludge */
    return ans;
}

/*
 * given an int (32 bits), box it in a wycc_obj as a reference token
 */
wycc_obj* wycc_box_ref(int x) {
    wycc_obj* ans;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Ref;
    ans->cnt = 1;
    ans->ptr = (void*) x;	/* **** kludge */
    return ans;
}

/*
 * given an long (64 bits), box it in a wycc_obj
 */
wycc_obj* wycc_box_long(long x) {
    wycc_obj* ans;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Int;
    ans->cnt = 1;
    ans->ptr = (void*) x;	/* **** kludge */
    return ans;
}

/*
 * box up a null
 */
wycc_obj* wycc_box_null() {
    wycc_obj* ans;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Null;
    ans->cnt = 1;
    ans->ptr = (void*) 0;	/* **** kludge */
    return ans;
}

/*
 * given an object and a string,
 * return 1 if the string describes the type of the object,
 * else return 0;
 */
int wycc_type_check(wycc_obj* itm, char* typ){
    WY_OBJ_SANE(itm, "wycc_type_check");

    // fprintf(stderr, "DEBUG wycc_type_check for type %s\n", typ);
    // fprintf(stderr, "DEBUG wycc_type_check given type %d\n", itm->typ);

    if (0 == strcmp(typ, "null")) {
	if (itm->typ == Wy_Null) {
	    return 1;
	};
	return 0;
    };
    if (0 == strcmp(typ, "char")) {
	if (itm->typ == Wy_Char) {
	    return 1;
	};
	return 0;
    };
    fprintf(stderr, "Help needed in wycc_type_check for type %s\n", typ);
    exit(-3);

}

/*
 * given a pait of lists, combine them into a record_record
 */
wycc_obj* wycc_record_record(wycc_obj* nam, wycc_obj* typ) {
    WY_OBJ_SANE(nam, "wycc_record_record nam");
    WY_OBJ_SANE(typ, "wycc_record_record typ");
    wycc_obj* ans;
    void** p;

    if (nam->typ != Wy_List) {
	fprintf(stderr, "Help needed in wycc_record_record for type %d\n"
		, nam->typ);
	exit(-3);
    };
    if (typ->typ != Wy_List) {
	fprintf(stderr, "Help needed in wycc_record_record for type %d\n"
		, typ->typ);
	exit(-3);
    };
    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Rcd_Rcd;
    p = (void**) calloc(2, sizeof(void *));
    p[0] = (void *) nam;
    p[1] = (void *) typ;
    ans->ptr = (void *) p;
    ans->cnt = 1;
    return ans;
}

/*
 * given a record and an offset, return the contents of that field.
 */
wycc_obj* wycc_record_get_dr(wycc_obj* rec, long osv) {
    WY_OBJ_SANE(rec, "wycc_record_get_dr");
    long at, tmp;
    wycc_obj* ans;
    void** p = rec->ptr;

    if ((rec->typ != Wy_RRecord) && (rec->typ != Wy_Record)) {
	fprintf(stderr, "Help needed in wycc_record_get_dr for type %d\n"
		, rec->typ);
	exit(-3);
    };
    if (osv < 0) {
	fprintf(stderr, "Help! wycc_record_get_dr offset %d\n"
		, osv);
	exit(-3);
    };
    at = (long) p[1];
    if (at < osv) {
	fprintf(stderr, "Help! wycc_record_get_dr offset:size %d:%d\n"
		, osv, at);
	exit(-3);
    };
    ans = (wycc_obj*) p[2+osv];
    if (ans == (wycc_obj *) NULL) {
	return ans;
    };
    ans->cnt++;
    return ans;
}

/*
 * given a count, setup a raw record:
 * just like list object big enough to accept them.
 */
void wycc_record_fill(wycc_obj* rec, int osv, wycc_obj* itm) {
    WY_OBJ_SANE(rec, "wycc_record_fill rec");
    WY_OBJ_SANE(itm, "wycc_record_fill itm");
    void** p = rec->ptr;
    long at, tmp;

    if ((rec->typ != Wy_RRecord) && (rec->typ != Wy_Record)) {
	fprintf(stderr, "Help needed in wycc_record_fill for type %d\n"
		, rec->typ);
	exit(-3);
    };
    if (osv < 0) {
	fprintf(stderr, "Help! wycc_record_fill offset %d\n"
		, osv);
	exit(-3);
    };
    at = (long) p[1];
    if (at < osv) {
	fprintf(stderr, "Help! wycc_record_fill offset:size %d:%d\n"
		, osv, at);
	exit(-3);
    };
    p[2 + osv] = (void *) itm;
    itm->cnt++;
    return;
}
//    wycc_record_fill(sys, 1, itm);

/*
 * given a count, setup a raw record:
 * just like list object big enough to accept them.
 */
wycc_obj* wycc_rrecord_new(long siz) {
    wycc_obj* ans;
    long tmp;
    void** p;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_RRecord;
    tmp = siz+2;
    p = (void**) calloc(tmp, sizeof(void *));
    p[1] = (void *) siz;
    p[0] = (void *) -1;		/* because it is raw */
    ans->ptr = (void *) p;
    ans->cnt = 1;
    return ans;
}

/*
 * given a record_record, setup a record:
 * just like list object big enough to accept them.
 */
wycc_obj* wycc_record_new(wycc_obj* meta) {
    WY_OBJ_SANE(meta, "wycc_record_new");
    wycc_obj* ans;
    long tmp, siz;
    void** p;

    if (meta->typ != Wy_Rcd_Rcd) {
	fprintf(stderr, "Help needed in wycc_record_new for type %d\n"
		, meta->typ);
	exit(-3);
    };
    p = (void **) meta->ptr;
    ans = (wycc_obj *) p[0];
    if (ans->typ != Wy_List) {
	fprintf(stderr, "Help needed in wycc_record_new, bad name list %d\n"
		, ans->typ);
	exit(-3);
    };
    siz = wycc_length_of_list(ans);
    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Record;
    
    tmp = siz+2;
    p = (void**) calloc(tmp, sizeof(void *));
    p[1] = (void *) siz;
    p[0] = (void *) meta;
    meta->cnt++;
    ans->ptr = (void *) p;
    ans->cnt = 1;
    return ans;
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

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_List;
    tmp = 2 * wycc_high_bit(siz+2);
    p = (void**) calloc(tmp, sizeof(void *));
    p[1] = (void *) 0;
    p[0] = (void *) tmp;
    ans->ptr = (void *) p;
    ans->cnt = 1;
    return ans;
}

wycc_obj* wycc_list_get(wycc_obj* lst, long at) {
    WY_OBJ_SANE(lst, "wycc_list_get");
    void** p = lst->ptr;

    if (lst->typ != Wy_List) {
	fprintf(stderr, "Help needed in wycc_list_get for type %d\n", lst->typ);
	exit(-3);
    };
    if (at >= (long) p[1]) {
	return NULL;
    };
    /* **** Does this need to inc the ref count */
    return (wycc_obj*) p[2+at];
}

void wycc_list_add(wycc_obj* lst, wycc_obj* itm) {
    WY_OBJ_SANE(lst, "wycc_list_add lst");
    WY_OBJ_SANE(itm, "wycc_list_add itm");
    void** p = lst->ptr;
    long at, tmp;
    size_t raw;

    if (lst->typ != Wy_List) {
	fprintf(stderr, "Help needed in wycc_list_add for type %d\n", lst->typ);
	exit(-3);
    };
    tmp = (long) p[0];
    at = ((long) p[1]) +1;
    p[1] = (void *) at;
    if ((at+2) >= tmp) {
	tmp *= 2;
        raw = tmp * sizeof(void *);
	p = (void **) realloc(p, raw);
	if (p == NULL) {
	    fprintf(stderr, "ERROR: realloc failed\n");
	    exit(-4);
	};
	p[0] = (void *) tmp;
	lst->ptr = p;
    };
    p[1 + at] = (void *) itm;
    itm->cnt++;
    return;
}

/*
 * given a simple type, setup an empty set object
 * 
 */
#define WYCC_SET_CHUNK 8
#define WYCC_MAP_CHUNK 8*3
wycc_obj* wycc_set_new(int typ) {
    wycc_obj* ans;
    long tmp;
    void** p;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Set;
    tmp = WYCC_SET_CHUNK + 2;
    p = (void**) calloc(tmp, sizeof(void *));
    p[0] = (void *) typ;
    p[1] = (void *) 0;
    ans->ptr = (void *) p;
    ans->cnt = 1;
    return ans;
}

struct chunk_ptr {
    void **p;		/* the top level set, map, or list */
    void **chk;		/* the current chunk */
    wycc_obj *key;	/* the current key object */
    wycc_obj *val;	/* the current value object if any*/
    long cnt;		/* the number of items remaining */
    long at;		/* position in the set */
    int idx;		/* position in the chunk */
    int flg;		/* 0==set, 1==map, 2==list */
};

wycc_obj* wycc_iter_new(wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc_iter_new");
    void** p = itm->ptr;
    void** chunk;
    wycc_obj* ans;
    struct chunk_ptr *ptr;
    int cnt;
    int flg;

    flg = -1;
    if (itm->typ == Wy_List) {
	flg = 2;
    };
    if (itm->typ == Wy_Set) {
	flg = 0;
    };
    if (flg == -1) {
	fprintf(stderr, "Help needed in wycc_iter_new for type %d\n", itm->typ);
	exit(-3);
    };
    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Iter;
    ans->cnt = 1;
    ptr = (struct chunk_ptr *) calloc(1, sizeof(struct chunk_ptr));
    cnt = (int) p[1];
    ptr->cnt = cnt;
    ptr->p = p;
    chunk = &(p[2]);
    ptr->chk = chunk;
    ptr->at = 0;
    ptr->flg = flg;
    ans->ptr = ptr;
    return ans;

}

static void wycc_chunk_ptr_fill(struct chunk_ptr *ptr, wycc_obj *itm, int typ) {
    WY_OBJ_SANE(itm, "wycc_chunk_ptr_fill");
    void** p = itm->ptr;
    void** chunk;
    int cnt;

    cnt = (int) p[1];
    ptr->cnt = cnt;
    ptr->p = p;
    chunk = &(p[2]);
    ptr->chk = chunk;
    ptr->at = 0;
    ptr->flg = typ;
    return;
}

static void wycc_chunk_ptr_inc(struct chunk_ptr *chunk) {
    int brnch;
    int step;
    int max;
    int tmp;
    wycc_obj *itm;
    void ** up;
    void ** was;
    void ** dn;

    chunk->key = NULL;
    chunk->val = NULL;
    if (chunk->cnt-- < 1) {
	chunk->cnt = 0;
	return;
    }
    if (chunk->flg == 0) {
	step = 1;	/* a key */
	max = WYCC_SET_CHUNK;
    } else if (chunk->flg == 1) {
	step = 2;	/* a key and a value */
	max = WYCC_MAP_CHUNK;
    } else if (chunk->flg == 2) {
	tmp = chunk->at++;
	chunk->idx = tmp;
	itm = (wycc_obj *) chunk->chk[tmp];
	chunk->key = (wycc_obj *) NULL;
	chunk->val = itm;
	return;
    } else {
	fprintf(stderr, "HELP: bad chunk->typ chunk_ptr_inc (%d)\n"
		, chunk->flg);
	exit(-3);
    };
    if (chunk->at == 0) {
	chunk->idx = 0;
    };
    tmp = chunk->idx;
    /*
     * pointing at a key, next is ?
     */
    while  (1) {
	brnch = (int) chunk->chk[0] * (step + 1);
	tmp++;
	if (tmp >=brnch) {
	    break;
	};
	dn = (void **) chunk->chk[tmp];
	if (dn == NULL) {
	    fprintf(stderr, "HHEELLPP: confused in chunk_ptr_inc; 2 deep\n");
	    exit(-3);
	};
	chunk->chk = dn;
	tmp = 0;
    }
    /*
     * we are now pointing beyond the branches.
     */
    while (1) {
	if (tmp < (max-1)) {
	    itm = (wycc_obj *) chunk->chk[tmp];
	    if (itm != NULL) {
		break;
	    };
	};
	bp();
	/*
	 * exhausted a chunk (maybe several)
	 */
	was = (void **) chunk->chk;
	up = (void **) chunk->chk[max-1];
	chunk->chk = up;
	brnch = (int) up[0] * (step + 1);
	for (tmp= 1; tmp < brnch ; tmp += (step + 1)) {
	    if (up[tmp] == was) {
		break;
	    };
	}
	if (up[tmp] != was) {
	    fprintf(stderr, "HHEELLPP: confused in chunk_ptr_inc\n");
	    exit(-3);
	};
	tmp++;
    }
    /*
     * we found a non-null item
     */
    if (step > 1) {
	chunk->val = itm;
	tmp++;
	itm = (wycc_obj *) chunk->chk[tmp];
    };
    chunk->idx = tmp;
    chunk->key = itm;
    chunk->at++;
    return;
}

wycc_obj* wycc_iter_next(wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc_iter_next");
    struct chunk_ptr *ptr;
    wycc_obj* ans;

    if (itm->typ != Wy_Iter) {
	fprintf(stderr, "Help needed in wycc_iter_next for type %d\n"
		, itm->typ);
	exit(-3);
    };
    ptr = (struct chunk_ptr *) itm->ptr;
    wycc_chunk_ptr_inc(ptr);
    if (ptr->flg == 2) {
	ans = ptr->val;
    } else if (ptr->flg == 0) {
	ans = ptr->key;
    } else {
	fprintf(stderr, "Help needed in wycc_iter_next for subtype %d\n"
		, ptr->flg);
	exit(-3);
    };
    if (ans != (wycc_obj *) NULL) {
	ans->cnt++;
    };
    return ans;
}

/*
 * given a chunk and an item, insert the item (increment ref cnt)
 */
static void wycc_chunk_add(wycc_obj* lst, wycc_obj* key, wycc_obj* val) {
}

/*
 * given a chunk_ptr and an item, find the spot for it
 */
static void wycc_chunk_ptr_find(struct chunk_ptr *chunk, wycc_obj* key) {
    return;
}

/*
 * given a set and an item, insert the item in the set (increment ref cnt)
 */
void wycc_set_add(wycc_obj* lst, wycc_obj* itm) {
    WY_OBJ_SANE(lst, "wycc_set_add lst");
    WY_OBJ_SANE(itm, "wycc_set_add itm");
    void** p = lst->ptr;
    void** chunk;
    void** new;
    void** chg;
    long at, typ, cnt, deep, idx, cp;
    size_t raw;
    wycc_obj* tst;
    int end;

    if (lst->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wycc_set_add for type %d\n", lst->typ);
	exit(-3);
    };
    /* Wy_None */
    typ = (long) p[0];
    if (typ == Wy_None) {
	typ = itm->typ;
	p[0] = (void *) typ;
    } else if (typ == Wy_Any) {
    } else if (typ != itm->typ) {
	//fprintf(stderr, "Help needed in wycc_set_add for multi-types \n");
	//exit(-3);
	p[0] = (void *) Wy_Any;
    };
    if ((typ < 0) || (typ > Wy_Type_Max)){
	fprintf(stderr, "Help needed in wycc_set_add for types %d\n", typ);
	exit(-3);
    };
    /* cnt = (long) p[1]; */
    at = 0;
    chunk = &(p[2]);
    if (((long) p[1]) <1) {
	/* add first member to empty set */
	chunk[0] = (void *) 0;
	chunk[1] = (void *) itm;
	itm->cnt++;
	p[1] = (void *) 1;
	return;
    }
    deep = 0;
    /*
     * sequencial search within the chunk
     */
    while (at < ((long) chunk[0])) {
	at++;
	tst = (wycc_obj *) chunk[2*at];
	end = wycc_comp_gen(itm, tst);
	if (end == 0) {
	    return;	/* key match == done */
	};
	if (end > 0) {
	    continue;
	};
	/* the item in question goes before here; ergo down a chunk */
	chunk = chunk[(2*at) - 1];
	at = 0;
	deep++;
    };
    at *= 2;		/* 2 ::= sizeof branch pair */
    /* at -= 1;		/* but not looking at pairs anymore */
    /*at += 1;		/* but not looking at pairs anymore */
    while (at < (WYCC_SET_CHUNK -2)) {
	at++;
	tst = (wycc_obj *) chunk[at];
	if (tst == (wycc_obj *) NULL) {
	    chunk[at] = (void *) itm;
	    itm->cnt++;
	    p[1]++;
	    wycc_chunk_rebal(0, p, chunk, at, deep);
	    return;
	}
	end = wycc_comp_gen(itm, tst);
	if (end == 0) {
	    return;	/* key match == done */
	};
	if (end < 0) {
	    break;
	};
    };
    if (end > 0) {
	at++;
    };
    //bp();
    /* this is a definite add */
    p[1]++;
    itm->cnt++;
    /*    if (at == (WYCC_SET_CHUNK -2)) {
	/* Need to insert just before or just after the end of a full chunk */
    tst = chunk[WYCC_SET_CHUNK -2];
    if (tst != (void *) NULL) {
	/* need to insert in a full chunk */
	/* split chunk into 2, expanding down deeper */
	new = (void**) calloc(WYCC_SET_CHUNK, sizeof(void *));
	if (new == (void **) NULL) {
	    fprintf(stderr, "ERROR: calloc failed new chunk\n");
	    exit(-4);
	};
	cnt = 2 * (long) chunk[0];
	/* push the bigger section down */
	if (cnt > (WYCC_SET_CHUNK /2)) {
	    /* the bigger is the branch section */
	    cnt += 1;	/* include the branch counter */
	    for (idx= 0; idx < cnt ;idx++) {
		new[idx] = chunk[idx];
		/* for each chunk, update its up links */
		if ((idx % 2) == 1) {
		    chg = chunk[idx];
		    chg[WYCC_SET_CHUNK -1] = new;
		};
		chunk[idx] = (void *) NULL;
	    };
	    /* we are forming a branch out of all the branch pairs */
	    /* but that branch pairs up with the first leaf */
	    chunk[0] = (void *) 1;
	    chunk[1] = (void *) new;
	    cp = 2;
	    for (; idx < (WYCC_SET_CHUNK - 1); idx++) {
		if (idx == at) {
		    chunk[cp++] = itm;
		};
		chunk[cp++] = chunk[idx];
	    };
	    /* we have to special special case an insert after the end */
	    if (idx == at) {
		chunk[cp++] = itm;
	    };
	    for (; cp < (WYCC_SET_CHUNK - 1); cp++) {
		chunk[cp++] = (void *) NULL;
	    };
	    new[WYCC_SET_CHUNK-1] = chunk;
	} else {
	    /* bigger is the leaf section */
	    bp();
	    cnt+= 1;	/* adjust for the branch counter */
	    new[0] = 0;
	    cp = 1;
	    for (idx= cnt; idx < (WYCC_SET_CHUNK - 1); idx++) {
		if (idx == at) {
		    new[cp++] = itm;
		};
		new[cp++] = chunk[idx];
		chunk[idx] = (void *) NULL;
	    };
	    /* we have to special special case an insert after the end */
	    if (idx == at) {
		new[cp++] = itm;
	    };
	    /* now steal back the last leaf to use in a new branch pair */
	    chunk[cnt++] = (void *) new;
	    chunk[cnt] = new[--cp];	/* last value becomes branch pair */
	    new[cp] = (void *) NULL;
	    chunk[0]++;
	    new[WYCC_SET_CHUNK-1] = chunk;
	};
	/**** rebalance tree */
	return;
    };
    /* need to insert item before at and we have room for it. */
    /* it is a leaf */
    for (idx= (WYCC_SET_CHUNK - 2); idx > at ; idx--) {
	chunk[idx] = chunk[idx - 1];
    }
    chunk[at] = itm;
    /**** rebalance tree */
    return;
}

/*
 * given a simple type, setup an empty map object
 * 
 */
wycc_obj* wycc_map_new(int typ) {
    wycc_obj* ans;
    long tmp;
    void** p;

    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_Map;
    tmp = WYCC_MAP_CHUNK + 2;
    p = (void**) calloc(tmp, sizeof(void *));
    p[0] = (void *) typ;
    p[1] = (void *) 0;
    ans->ptr = (void *) p;
    ans->cnt = 1;
    return ans;
}

void wycc_map_add(wycc_obj* lst, wycc_obj* key, wycc_obj* itm) {
    WY_OBJ_SANE(lst, "wycc_map_add lst");
    WY_OBJ_SANE(key, "wycc_map_add key");
    WY_OBJ_SANE(itm, "wycc_map_add itm");
    void** p = lst->ptr;
    void** chunk;
    void** new;
    void** chg;
    long at, typ, cnt, deep, idx, cp;
    size_t raw;
    wycc_obj* tst;
    int end;

    if (lst->typ != Wy_Map) {
	fprintf(stderr, "Help needed in wycc_map_add for type %d\n", lst->typ);
	exit(-3);
    };
    typ = (long) p[0];
    if (typ == Wy_None) {
	typ = key->typ;
	p[0] = (void *) typ;
    } else if (typ == Wy_Any) {
    } else if (typ != key->typ) {
	//fprintf(stderr, "Help needed in wycc_map_add for multi-types \n");
	//exit(-3);
	p[0] = (void *) Wy_Any;
    };
    if ((typ < 0) || (typ > Wy_Type_Max)){
	fprintf(stderr, "Help needed in wycc_map_add for types %d\n", typ);
	exit(-3);
    };
    at = 0;
    chunk = &(p[2]);
    if (((long) p[1]) <1) {
	/* add first member to empty map */
	chunk[0] = (void *) 0;
	chunk[1] = (void *) itm;
	chunk[2] = (void *) key;
	itm->cnt++;
	key->cnt++;
	p[1] = (void *) 1;
	return;
    }
    deep = 0;
    /*
     * sequencial search within the chunk
     */
    while (at < ((long) chunk[0])) {
	at++;
	tst = (wycc_obj *) chunk[3*at];
	end = wycc_comp_gen(key, tst);
	if (end == 0) {
	    /* key match == done ; swap the value stored */
	    tst = (wycc_obj *) chunk[(3*at) -1];
	    chunk[(3*at) -1] = (void *) itm;
	    wycc_deref_box(tst);
	    return;
	};
	if (end > 0) {
	    continue;
	};
	/* the item in question goes before here; ergo down a chunk */
	chunk = chunk[(3*at) - 2];
	at = 0;
	deep++;
    };
    at *= 3;		/* 3 ::= sizeof branch value key triplet */
    while (at < (WYCC_MAP_CHUNK -4)) {
	at += 2;
	tst = (wycc_obj *) chunk[at];
	if (tst == (wycc_obj *) NULL) {
	    chunk[at]    = (void *) key;
	    chunk[at -1] = (void *) itm;
	    itm->cnt++;
	    key->cnt++;
	    p[1]++;
	    wycc_chunk_rebal(0, p, chunk, at, deep);
	    return;
	}
	// end = compar(key, tst);
	end = wycc_comp_gen(key, tst);
	if (end == 0) {
	    /* key match == done ; swap the value stored */
	    tst = (wycc_obj *) chunk[at -1];
	    chunk[at -1] = (void *) itm;
	    wycc_deref_box(tst);
	    return;	/* key match == done */
	};
	if (end < 0) {
	    break;
	};
    };
    bp();
    if (end > 0) {
	at += 2;
    };
    /* at -= 1;	/* correct for to point at value */
    /* this is a definite add */
    p[1]++;
    itm->cnt++;
    key->cnt++;
	/* Need to insert just before or just after the end of a full chunk */
    tst = chunk[WYCC_MAP_CHUNK -4];
    if (tst != (void *) NULL) {
	/* need to insert in a full chunk (noo room for more) */
	/* split chunk into 2, expanding down deeper */
	new = (void**) calloc(WYCC_MAP_CHUNK, sizeof(void *));
	if (new == (void **) NULL) {
	    fprintf(stderr, "ERROR: calloc failed new chunk\n");
	    exit(-4);
	};
	cnt = 3 * (long) chunk[0];
	/* push the bigger section down */
	if (cnt > (WYCC_MAP_CHUNK /2)) {
	    /* the bigger is the branch section */
	    cnt += 1;	/* include the branch counter */
	    for (idx= 0; idx < cnt ;idx++) {
		new[idx] = chunk[idx];
		/* for each chunk, update its up links */
		if ((idx % 3) == 1) {
		    chg = chunk[idx];
		    chg[WYCC_MAP_CHUNK -1] = new;
		};
		chunk[idx] = (void *) NULL;
	    };
	    /* we are forming a branch out of all the branch triples */
	    /* but that branch pairs up with the first leaf */
	    chunk[0] = (void *) 1;
	    chunk[1] = (void *) new;
	    cp = 2;
	    for (; idx < (WYCC_MAP_CHUNK - 2); ) {
		if (idx == at-1) {
		    chunk[cp++] = itm;
		    chunk[cp++] = key;
		};
		chunk[cp++] = chunk[idx++];
		chunk[cp++] = chunk[idx++];
	    };
	    /* we have to special special case an insert after the end */
	    if (idx == at-1) {
		chunk[cp++] = itm;
		chunk[cp++] = key;
	    };
	    for (; cp < (WYCC_MAP_CHUNK - 1); cp++) {
		chunk[cp++] = (void *) NULL;
	    };
	} else {
	    /* bigger is the leaf section */
	    cnt+= 1;	/* adjust for the branch counter */
	    new[0] = 0;
	    cp = 1;
	    for (idx= cnt; idx < (WYCC_MAP_CHUNK - 3); ) {
		if (idx == at-1) {
		    new[cp++] = itm;
		    new[cp++] = key;
		};
		new[cp++] = chunk[idx];
		chunk[idx++] = (void *) NULL;
		new[cp++] = chunk[idx];
		chunk[idx++] = (void *) NULL;
	    };
	    /* we have to special special case an insert after the end */
	    if (idx == at-1) {
		new[cp++] = itm;
		new[cp++] = key;
	    };
	    /* now steal back the last leaf to use in a new branch pair */
	    chunk[cnt++] = (void *) new;
	    chunk[cnt++] = new[cp-2];	/* last value becomes branch pair */
	    chunk[cnt++] = new[cp-1];	/* last value becomes branch pair */
	    new[cp-2] = (void *) NULL;
	    new[cp-1] = (void *) NULL;
	    chunk[0]++;
	    new[WYCC_MAP_CHUNK-1] = chunk;
	};
	/**** rebalance tree */
	return;
    };
    /* need to insert item before at and we have room for it. */
    /* it is a leaf */
    for (idx= (WYCC_MAP_CHUNK - 1); idx > (at-1) ; idx--) {
	chunk[idx] = chunk[idx - 1];
    }
    chunk[at--] = key;
    chunk[at]   = itm;
    /**** rebalance tree */
    return;
}

/*
 * given a set (a chunked tree), rebalance it.
 * a chunk, an index into it, and a depth gage may be given as hints.
 */
void wycc_chunk_rebal(int payl, void** p, void** chunk, long at, long deep){
    return;
}

/*
 * given a wycc_obj, reduce the reference count.
 * when there are no more references reclaim the box.
 * if the flag is set (not zero), reclaim the underlying data.
 */

static void wycc_dealloc_typ(void* ptr, int typ);
wycc_obj* wycc_deref_box(wycc_obj* itm) {
    void* ptr;
    int typ;

    if (itm == NULL) {
	return NULL;
    };
    if (itm->cnt-- > 1) {
	return itm;
    };
    if (itm->cnt < 0) {
	fprintf(stderr, "FAILURE: ref count went negative (%d:%d@%p)\n"
		, itm->cnt, itm->typ, (void *) itm);
	exit(-3);
    }
    ptr = itm->ptr;
    typ = itm->typ;
    if (wycc_debug_flag) {
	fprintf(stderr, "note: deallocing box for typ %d\n", typ);
    };
    free(itm);
    if (typ == Wy_Int) {
	return (wycc_obj *) NULL;
    };
    if (typ == Wy_CString) {
	return (wycc_obj *) NULL;
    };
    if (typ == Wy_Char) {
	return (wycc_obj *) NULL;
    };
    if (typ == Wy_Bool) {
	return (wycc_obj *) NULL;
    };
    wycc_dealloc_typ(ptr, typ);
    return (wycc_obj *) NULL;
}

/*
 * given a chunk of a set, dereference/deallocate all keys, and
 * deallocate branch chunks, recursing as needed.
 */
static void wycc_dealloc_set_chunk(void** chunk) {
    int cnt;
    int idx;
    wycc_obj* nxt;

    cnt = ((long) chunk[0]) * 2;
    for (idx = 1; idx < (WYCC_SET_CHUNK-1) ; idx++) {
	nxt = (wycc_obj*) chunk[idx];
	chunk[idx] = NULL;
	if (nxt == NULL) {
	    break;
	};
	if ((idx < cnt) && ((idx % 2) != 0)) {
	    wycc_dealloc_set_chunk((void**) nxt);
	    free(nxt);
	    continue;
	};
	wycc_deref_box(nxt);
    }

    // fprintf(stderr, "deallocating chunk of set\n");
    return;
}

/*
 * given a chunk of a set, dereference/deallocate all keys, and
 * deallocate branch chunks, recursing as needed.
 */
static void wycc_dealloc_map_chunk(void** chunk) {
    int cnt;
    int idx;
    wycc_obj* nxt;

    cnt = ((long) chunk[0]) * 3;
    for (idx = 1; idx < WYCC_MAP_CHUNK ; idx++) {
	nxt = (wycc_obj*) chunk[idx];
	chunk[idx] = NULL;
	if (nxt == NULL) {
	    break;
	};
	if ((idx < cnt) && ((idx % 3) == 1)) {
	    wycc_dealloc_set_chunk((void**) nxt);
	    free(nxt);
	    continue;
	};
	wycc_deref_box(nxt);
    }

    // fprintf(stderr, "deallocating chunk of set\n");
    return;
}

static void wycc_dealloc_typ(void* ptr, int typ){
    long siz, idx;
    void** p = (void**) ptr;
    wycc_obj* itm;

    if (typ == Wy_String) {
        free(ptr);
	return;
    };
    if (typ == Wy_List) {
	siz = (long) p[1];
	for (idx= 0; idx < siz; idx++) {
	    itm = (wycc_obj*) p[2+ idx]; 
	    wycc_deref_box(itm);
	}
        free(ptr);
	return;
    };
    if (typ == Wy_Set) {
	void** chunk = &(p[2]);
	wycc_dealloc_set_chunk(chunk);
	free(ptr);
	return;
    }
    if (typ == Wy_Map) {
	void** chunk = &(p[2]);
	wycc_dealloc_map_chunk(chunk);
	free(ptr);
	return;
    }
    if (typ == Wy_Record) {
	fprintf(stderr, "Fail: no support for record in dealloc\n");
	exit(-3);
    }
    fprintf(stderr, "ERROR: unrecognized type (%d) in dealloc\n", typ);
    exit(-3);
}

/*
 * ------------------------------
 * wycc comparison routines for B things
 *
 * each returns      -1,  0, or 1
 * corresponding to  ab, aa,   ba
 * ------------------------------
 */

static int wycc_comp_gen(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_gen lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_gen rhs");
    int lt = lhs->typ;
    int rt = rhs->typ;

    if (lt == Wy_CString) {
	lt = Wy_String;
    };
    if (rt == Wy_CString) {
	rt = Wy_String;
    };
    if (lt < rt) {
	return -1;
    };
    if (lt > rt) {
	return 1;
    };
    if (lt == Wy_String) {
	return wycc_comp_str(lhs, rhs);
    };
    if (lt == Wy_Int) {
	return wycc_comp_int(lhs, rhs);
    };
    if (lt == Wy_Char) {
	return wycc_comp_int(lhs, rhs);
    };
    if (lt == Wy_Bool) {
	return wycc_comp_int(lhs, rhs);
    };
    if (lt == Wy_Set) {
	return wycc_comp_set(lhs, rhs);
    };
    if (lt == Wy_Record) {
	return wycc_comp_record(lhs, rhs);
    };
    if (lt == Wy_List) {
	return wycc_comp_list(lhs, rhs);
    };
    fprintf(stderr, "Help needed in wycc_comp_gen for type %d\n", lt);
    exit(-3);
}

/*
 * simple comarison of two c (null-terminated)strings
 */
static int wycc_comp_str(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_str lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_str rhs");
    char *lp, *rp;
    int ans;

    if ((lhs->typ != Wy_String) && (lhs->typ != Wy_CString)) {
	fprintf(stderr, "Help needed in wycc_comp_str for type %d\n", lhs->typ);
	exit(-3);
    };
    if ((rhs->typ != Wy_String) && (rhs->typ != Wy_CString)) {
	fprintf(stderr, "Help needed in wycc_comp_str for type %d\n", rhs->typ);
	exit(-3);
    };
    lp = lhs->ptr;
    rp = rhs->ptr;
    ans =  strcmp(lp, rp);
    if (ans < 0) {
	return -1;
    } else if (ans > 0) {
	return 1;
    };
    return 0;
}

/*
 * simple comarison of two small integers
 */
static int wycc_comp_int(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_int lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_int rhs");
    int lhv, rhv;

    lhv = (long) lhs->ptr;
    rhv = (long) rhs->ptr;
    if (lhv < rhv) {
	return -1;
    } else if (lhv > rhv) {
	return 1;
    };
    return 0;
}

static int wycc_comp_wint(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_wint lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_wint rhs");
	fprintf(stderr, "Help needed in wycc_comp_wint\n");
	exit(-3);
}

/*
 * a not quite simple comparison of two small sets
 * a set with fewer elements is always smaller
 * a 
 */
static int wycc_comp_set(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_set lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_set rhs");
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;

    wycc_chunk_ptr_fill(lptr, lhs, 0);	/* 0 == this is a set */
    wycc_chunk_ptr_fill(rptr, rhs, 0); 
   wycc_chunk_ptr_inc(lptr);
    wycc_chunk_ptr_inc(rptr);
    while (lptr->key) {
	litm = lptr->key;
	ritm = rptr->key;
	end = wycc_comp_gen(litm, ritm);
	if (end != 0) {
	    return end;
	};
	wycc_chunk_ptr_inc(lptr);
	wycc_chunk_ptr_inc(rptr);
    };
    return 0;
}

/*
 * a not quite simple comparison of two lists
 * a list with fewer elements is always smaller
 * a 
 */
static int wycc_comp_list(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_list lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_list rhs");
    void ** lblk = (void **) lhs->ptr;
    void ** rblk = (void **) rhs->ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;
    int idx;
    int cnt;

    idx = (int) lblk[1];
    cnt = (int) rblk[1];
    if (idx < cnt) {
	return -1;
    };
    if (cnt < idx) {
	return 1;
    };
    for (idx = 0; idx < cnt ; idx ++) {
	litm = (wycc_obj *) lblk[2+idx];
	ritm = (wycc_obj *) rblk[2+idx];
	end = wycc_comp_gen(litm, ritm);
	if (end != 0) {
	    return end;
	};
    }
    return 0;
}

/*
 * a not quite simple comparison of two records
 * a record with fewer fields is always smaller
 * a 
 */
static int wycc_comp_record(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_record lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_record rhs");
    void ** lblk = (void **) lhs->ptr;
    void ** rblk = (void **) rhs->ptr;
    int lcnt = (int) lblk[1];
    int rcnt = (int) rblk[1];
    int end;

    if (lcnt < rcnt) {
	return -1;
    };
    if (rcnt < lcnt) {
	return 1;
    };
    end = wycc_comp_rmeta((wycc_obj*) lblk[0], (wycc_obj*) rblk[0]);
    if (end != 0) {
	return end;
    };

    return wycc_comp_list(lhs, rhs);
}

/*
 * a not quite simple comparison of two records
 * a record with fewer fields is always smaller
 * a 
 */
static int wycc_comp_rmeta(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_record lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_record rhs");
    void ** lblk = (void **) lhs->ptr;
    void ** rblk = (void **) rhs->ptr;
    int end;

    if (lhs->typ != Wy_Rcd_Rcd) {
	fprintf(stderr, "FAIL wycc_comp_meta called for type %d\n", lhs->typ);
	exit(-3);
    }
    if (rhs->typ != Wy_Rcd_Rcd) {
	fprintf(stderr, "FAIL wycc_comp_meta called for type %d\n", rhs->typ);
	exit(-3);
    }
    end = wycc_comp_list((wycc_obj *) lblk[0], (wycc_obj *) rblk[0]);
    if (end != 0) {
	return end;
    };
    end = wycc_comp_list((wycc_obj *) lblk[1], (wycc_obj *) rblk[1]);
    if (end != 0) {
	return end;
    };
    return 0;
}

/*
 * here we assume that we cannot reasonably compare the value (contents)
 * of the object, so to force a comparison, we compare their locations
 * in memory.
 */
static int wycc_comp_obj(wycc_obj* lhs, wycc_obj* rhs){
    fprintf(stderr, "Warning: wycc_comp_obj is deprecated.\n");
    if (lhs < rhs) {
	return -1;
    } else if (lhs > rhs) {
	return 1;
    };
    return 0;
}

/*
 * given a numeric object return the size of the number
 * (how many longs it takes to represent it)
 */
static int wycc_wint_size(wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc_wint_size");
    if (itm->typ == Wy_Int) {
	return (size_t) 1;
    };
    fprintf(stderr, "Help needed in add for type %d\n", itm->typ);
    exit(-3);
}

int wycc_length_of_list(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_length_of_list");
    void** p = itm->ptr;

    if (itm->typ != Wy_List) {
	fprintf(stderr, "Help length_of_list called for type %d\n", itm->typ);
	exit(-3);
    }
    return (int) p[1];
}

int wycc_length_of_set(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_length_of_set");
    void** p = itm->ptr;
    int rslt;

    if (itm->typ != Wy_Set) {
	fprintf(stderr, "Help length_of_set called for type %d\n", itm->typ);
	exit(-3);
    };
    if (((long) p[0]) == Wy_None) {
	return 0;
    };
    return (int) p[1];
}

int wycc_length_of_map(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_length_of_map");
    void** p = itm->ptr;
    int rslt;

    if (itm->typ != Wy_Map) {
	fprintf(stderr, "Help length_of_map called for type %d\n", itm->typ);
	exit(-3);
    };
    if (((long) p[0]) == Wy_None) {
	return 0;
    };
    return (int) p[1];
}

int wycc_length_of_string(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_length_of_string");
    char* p = (char *) itm->ptr;
    int rslt;

    if ((itm->typ != Wy_String) && (itm->typ != Wy_CString)) {
	fprintf(stderr, "Help length_of_string called for type %d\n", itm->typ);
	exit(-3);
    };
    rslt = strlen(p);
    return rslt;
}

/*
 * some operation need to change an object that is currently being shared
 */
wycc_obj* wycc_cow_string(wycc_obj* str) {
    WY_OBJ_SANE(str, "wycc_cow_string");
    char* p = str->ptr;
    char *buf;
    size_t siz;

    siz = strlen(p) + 3;
    buf = (char *) malloc(siz);
    strcpy(buf, p);
    return wycc_box_str(buf);
    //fprintf(stderr, "Fail: wycc_cow_string not written yet.\n");
    //exit(-3);

}

/*
 * some operation need to change an object that is currently being shared
 */
wycc_obj* wycc_cow_list(wycc_obj* lst) {
    WY_OBJ_SANE(lst, "wycc_cow_list");
    wycc_obj* ans;
    wycc_obj* nxt;
    void** p = lst->ptr;
    long at, tmp;
    void** new;

    if (lst->typ != Wy_List) {
	fprintf(stderr, "Help needed in wycc_cow_list for type %d\n", lst->typ);
	exit(-3);
    };
    ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans->typ = Wy_List;
    tmp = (long) p[0];
    new = (void**) calloc(tmp, sizeof(void *));
    new[0] = (void *) tmp;
    tmp = (long) p[1];
    for (at= 0; at < tmp ; at++) {
	nxt = (wycc_obj*) p[2+at];
	nxt->cnt++;
	new[2+at] = (void *) nxt;
    }
    new[1] = (void *) tmp;
    ans->ptr = (void *) new;
    ans->cnt = 1;
    return ans;
}

/*
 * if rhs contains lhs return 1;
 * else return 0
 */
static wycc_compare_member_of(wycc_obj* lhs, wycc_obj* rhs) {
    WY_OBJ_SANE(lhs, "wycc_compare_member_of lhs");
    WY_OBJ_SANE(rhs, "wycc_compare_member_of rhs");
    void** rp = rhs->ptr;
    int rcnt;

    if (rhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wycc_compare_member_of for type %d\n"
		, rhs->typ);
	exit(-3);
    };
    rcnt = (int) rp[1];
    
    fprintf(stderr, "Failure: wycc_compare_member_of\n");
    exit(-3);
}

/*
 * if lhs is a subset of rhs, return 1;
 * if lhs == rhs and flg, return 1;
 * else return 0
 */
static wycc_compare_subset(wycc_obj* lhs, wycc_obj* rhs, int flg) {
    WY_OBJ_SANE(lhs, "wycc_compare_subset lhs");
    WY_OBJ_SANE(rhs, "wycc_compare_subset rhs");
    void** lp = lhs->ptr;
    void** rp = rhs->ptr;
    int lcnt;
    int rcnt;
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int dif;
    int end;

    if (lhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wycc_compare_subset for type %d\n"
		, lhs->typ);
	exit(-3);
    };
    if (rhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wycc_compare_subset for type %d\n"
		, rhs->typ);
	exit(-3);
    };
    lcnt = (int) lp[1];
    rcnt = (int) rp[1];
    if (lcnt > rcnt) {
	return 0;
    };
    if (lcnt <= 0) {
	if (rcnt <= 0) {
	    return flg;
	};
	return 1;
    };
    dif = 0;
    wycc_chunk_ptr_fill(lptr, lhs, 0);	/* 0 == this is a set */
    wycc_chunk_ptr_fill(rptr, rhs, 0);
    wycc_chunk_ptr_inc(lptr);
    litm = lptr->key;
    while (1) {
	wycc_chunk_ptr_inc(rptr);
	ritm = rptr->key;
	if (ritm == NULL) {
	    if (litm == NULL) {
		if (dif) {
		    return 1;
		};
		return flg;
	    };
	    return 0;
	};
	if (litm == NULL) {
	    return 1;
	};
	end = wycc_comp_gen(litm, ritm);
	if (end == 0) {
	    wycc_chunk_ptr_inc(lptr);
	    litm = lptr->key;
	    continue;
	};
	if (end > 0) {
	    return 0;
	};
	dif++;
    }
    fprintf(stderr, "Failure: wycc_compare_subset\n");
    exit(-3);
}

/*
 * if relationship rel holds between lhs & rhs, return 1
 * else return 0
 */
int wycc_compare(wycc_obj* lhs, wycc_obj* rhs, int rel){
    WY_OBJ_SANE(lhs, "wycc_compare lhs");
    WY_OBJ_SANE(rhs, "wycc_compare rhs");
    int end;

    if ((rel < 0) || (rel > Wyil_Relation_Se)) {
	fprintf(stderr, "Failure: wycc_compare with relation %d\n"
		, rel);
	exit(-3);
    };
    if (rel == Wyil_Relation_Mo) {
	return wycc_compare_member_of(lhs, rhs);
    };
    if (rel == Wyil_Relation_Ss) {
	return wycc_compare_subset(lhs, rhs, 0);
    };
    if (rel == Wyil_Relation_Se) {
	return wycc_compare_subset(lhs, rhs, 1);
    };

    end = wycc_comp_gen(lhs, rhs);
    if (end < 0) {
	if (rel == Wyil_Relation_Lt) {
	    return 1;
	};
	if (rel == Wyil_Relation_Le) {
	    return 1;
	};
	if (rel == Wyil_Relation_Ne) {
	    return 1;
	};
    } else if (end > 0) {
	if (rel == Wyil_Relation_Gt) {
	    return 1;
	};
	if (rel == Wyil_Relation_Ge) {
	    return 1;
	};
	if (rel == Wyil_Relation_Ne) {
	    return 1;
	};
    } else {
	if (rel == Wyil_Relation_Eq) {
	    return 1;
	};
	if (rel == Wyil_Relation_Le) {
	    return 1;
	};
	if (rel == Wyil_Relation_Ge) {
	    return 1;
	};
    };
    return 0;
}

static wycc_obj *wycc_set_from_list(wycc_obj *lst) {
    WY_OBJ_SANE(lst, "wycc_set_from_list");
    void** p = lst->ptr;
    wycc_obj *ans;
    wycc_obj *itm;
    long idx;

    ans = wycc_set_new(-1);
    for (idx= 0; idx < (long) p[1] ; idx++) {
	itm = (wycc_obj *) p[2 + idx];
	//itm->cnt++;
	wycc_set_add(ans, itm);
    }
    return ans;
}
/*
 * ******************************
 * wyil opcode implementations
 * ******************************
 */

/*
 * given two operands, a relationship, and a text line
 * returnn if the relationship holds, else print the text and exit.
 */
void wyil_assert(wycc_obj* lhs, wycc_obj* rhs, int rel, char *msg){
    WY_OBJ_SANE(lhs, "wyil_assert lhs");
    WY_OBJ_SANE(rhs, "wyil_assert rhs");

    if (wycc_compare(lhs, rhs, rel)) {
	return;
    };
    fprintf(stderr, msg);
    exit(-4);
}

/*
 * return a set that is the difference between lhs and rhs
 */
wycc_obj* wyil_set_diff(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_diff lhs");
    WY_OBJ_SANE(rhs, "wyil_set_diff rhs");
    wycc_obj* ans;
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;

    if (lhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wyil_set_diff for type %d\n"
		, lhs->typ);
	exit(-3);
    };
    if (rhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wyil_set_diff for type %d\n"
		, rhs->typ);
	exit(-3);
    };
    ans = wycc_set_new(-1);
    wycc_chunk_ptr_fill(lptr, lhs, 0);	/* 0 == this is a set */
    wycc_chunk_ptr_fill(rptr, rhs, 0);
    wycc_chunk_ptr_inc(lptr);
    wycc_chunk_ptr_inc(rptr);
    while (1) {
	litm = lptr->key;
	ritm = rptr->key;
	if ((litm == NULL) && (ritm == NULL)) {
	    return ans;
	};
	if (litm == NULL) {
	    end = 1;
	} else if (ritm == NULL) {
	    end = -1;
	} else {
	    end = wycc_comp_gen(litm, ritm);
	};
	if (end == 0) {
	    wycc_chunk_ptr_inc(lptr);
	    wycc_chunk_ptr_inc(rptr);
	    continue;
	};
	if (end < 0) {
	    wycc_set_add(ans, litm);
	    wycc_chunk_ptr_inc(lptr);
	} else {
	    wycc_chunk_ptr_inc(rptr);
	};

    };
    fprintf(stderr, "Failure: wyil_set_diff\n");
    exit(-3);
}

/*
 * return a set that is the intersection between lhs and rhs
 */
wycc_obj* wyil_set_insect(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_insect lhs");
    WY_OBJ_SANE(rhs, "wyil_set_insect rhs");
    wycc_obj* ans;
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;
    int flg = 0;

    if (lhs->typ == Wy_List) {
	flg |= 1;
	ans = wycc_set_from_list(lhs);
	lhs = ans;
    };
    if (rhs->typ == Wy_List) {
	flg |= 2;
	ans = wycc_set_from_list(rhs);
	rhs = ans;
    };
    if (lhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wyil_set_insect for type %d\n"
		, lhs->typ);
	exit(-3);
    };
    if (rhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wyil_set_insect for type %d\n"
		, rhs->typ);
	exit(-3);
    };
    ans = wycc_set_new(-1);
    wycc_chunk_ptr_fill(lptr, lhs, 0);	/* 0 == this is a set */
    wycc_chunk_ptr_fill(rptr, rhs, 0);
    wycc_chunk_ptr_inc(lptr);
    wycc_chunk_ptr_inc(rptr);
    while (1) {
	litm = lptr->key;
	ritm = rptr->key;
	if (litm == NULL) {
	    break;;
	};
	if (ritm == NULL) {
	    break;
	};
	end = wycc_comp_gen(litm, ritm);
	if (end == 0) {
	    wycc_set_add(ans, litm);
	    wycc_chunk_ptr_inc(lptr);
	    wycc_chunk_ptr_inc(rptr);
	    continue;
	};
	if (end < 0) {
	    wycc_chunk_ptr_inc(lptr);
	} else {
	    wycc_chunk_ptr_inc(rptr);
	};
    };
    if (flg & 1) {
	wycc_deref_box(lhs);
    };
    if (flg & 2) {
	wycc_deref_box(rhs);
    };
    return ans;
}

/*
 * given a set and a list add each element of the list to the set
 */
static wycc_obj* wyil_set_add_list(wycc_obj* set, wycc_obj* lst){
    WY_OBJ_SANE(set, "wyil_set_add_list set");
    WY_OBJ_SANE(lst, "wyil_set_add_list lst");
    void** p = lst->ptr;
    wycc_obj *itm;
    long at, tmp;

    for (at= 0; at < (long) p[1]; at++) {
	itm = (wycc_obj *) p[2+at];
	wycc_set_add(set, itm);
    };
    return set;
}

/*
 * like wyil_set_union but where one or both operands are lists
 */
static wycc_obj* wyil_set_union_list(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_union_list lhs");
    WY_OBJ_SANE(rhs, "wyil_set_union_list rhs");
    wycc_obj* ans;
    struct chunk_ptr my_chunk_ptr;
    struct chunk_ptr *cptr = & my_chunk_ptr;

    ans = wycc_set_new(-1);
    if (lhs->typ == Wy_Set) {
	wycc_chunk_ptr_fill(cptr, lhs, 0);	/* 0 == this is a set */
	wycc_chunk_ptr_inc(cptr);
    } else if (rhs->typ == Wy_Set) {
	wycc_chunk_ptr_fill(cptr, rhs, 0);
	wycc_chunk_ptr_inc(cptr);
    } else {
	cptr->key = (wycc_obj *) NULL;
    };
    while (cptr->key != (wycc_obj *) NULL) {
	wycc_set_add(ans, cptr->key);
	wycc_chunk_ptr_inc(cptr);
    };
    if (lhs->typ == Wy_List) {
	wyil_set_add_list(ans, lhs);
    };
    if (rhs->typ == Wy_List) {
	wyil_set_add_list(ans, rhs);
    };
    return ans;
}

/*
 * return a set that is the union of lhs and rhs
 */
wycc_obj* wyil_set_union(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_union lhs");
    WY_OBJ_SANE(rhs, "wyil_set_union rhs");
    wycc_obj* ans;
    struct chunk_ptr lhs_chunk_ptr;
    struct chunk_ptr *lptr = & lhs_chunk_ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj *litm;
    wycc_obj *ritm;
    int end;

    if ((lhs->typ == Wy_List) || (rhs->typ == Wy_List)) {
	return wyil_set_union_list(lhs, rhs);
    }
    if (lhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wyil_set_union for type %d\n"
		, lhs->typ);
	exit(-3);
    };
    if (rhs->typ != Wy_Set) {
	fprintf(stderr, "Help needed in wyil_set_union for type %d\n"
		, rhs->typ);
	exit(-3);
    };
    ans = wycc_set_new(-1);
    wycc_chunk_ptr_fill(lptr, lhs, 0);	/* 0 == this is a set */
    wycc_chunk_ptr_fill(rptr, rhs, 0);
    wycc_chunk_ptr_inc(lptr);
    wycc_chunk_ptr_inc(rptr);
    while (1) {
	litm = lptr->key;
	ritm = rptr->key;
	if ((litm == NULL) && (ritm == NULL)) {
	    return ans;
	};
	if (litm == NULL) {
	    end = 1;
	} else if (ritm == NULL) {
	    end = -1;
	} else {
	    end = wycc_comp_gen(litm, ritm);
	};
	if (end == 0) {
	    wycc_set_add(ans, litm);
	    wycc_chunk_ptr_inc(lptr);
	    wycc_chunk_ptr_inc(rptr);
	    continue;
	};
	if (end < 0) {
	    wycc_set_add(ans, litm);
	    wycc_chunk_ptr_inc(lptr);
	} else {
	    wycc_set_add(ans, ritm);
	    wycc_chunk_ptr_inc(rptr);
	};

    };
}

/*
 * return a set that is the union of lhs and rhs
 */
wycc_obj* wyil_set_union_left(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_union_left lhs");
    WY_OBJ_SANE(rhs, "wyil_set_union_left rhs");
    fprintf(stderr, "Failure: wyil_set_union_left\n");
    exit(-3);
}

/*
 * return a set that is the union of lhs and rhs
 */
wycc_obj* wyil_set_union_right(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_union_right lhs");
    WY_OBJ_SANE(rhs, "wyil_set_union_right rhs");
    fprintf(stderr, "Failure: wyil_set_union_right\n");
    exit(-3);
}

/*
 * return a set that is the intersection between lhs and rhs
 */
wycc_obj* wyil_set_insect_left(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_insect_left lhs");
    WY_OBJ_SANE(rhs, "wyil_set_insect_left rhs");
    fprintf(stderr, "Failure: wyil_set_insect_left\n");
    exit(-3);
}

/*
 * return a set that is the intersection between lhs and rhs
 */
wycc_obj* wyil_set_insect_right(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_insect_right lhs");
    WY_OBJ_SANE(rhs, "wyil_set_insect_right rhs");
    fprintf(stderr, "Failure: wyil_set_insect_right\n");
    exit(-3);
}

/*
 * return a set that is the difference between the lhs and rhs
 */
wycc_obj* wyil_set_diff_left(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_set_diff_left lhs");
    WY_OBJ_SANE(rhs, "wyil_set_diff_left rhs");
    fprintf(stderr, "Failure: wyil_set_diff_left\n");
    exit(-3);
}

/*
 * update an element of a string
 */
wycc_obj* wyil_update_string(wycc_obj* str, wycc_obj* osv, wycc_obj* rhs){
    WY_OBJ_SANE(str, "wyil_update_string str");
    WY_OBJ_SANE(osv, "wyil_update_string osv");
    WY_OBJ_SANE(rhs, "wyil_update_string rhs");
    char *txt;
    long lsiz, idx;
    int tmp;
    wycc_obj *swp;

    if ((str->typ != Wy_String) && (str->typ != Wy_CString)) {
	fprintf(stderr, "ERROR: string in wyil_update_string is type %d\n"
		, str->typ);
	exit(-3);
    };
    if (osv->typ != Wy_Int) {
	fprintf(stderr
		, "ERROR: offset  value in wyil_update_string is type %d\n"
		, osv->typ);
	exit(-3);
    };
    if (rhs->typ != Wy_Char) {
	fprintf(stderr
		, "ERROR: replacement value in wyil_update_string is type %d\n"
		, rhs->typ);
	exit(-3);
    };
    if (str->typ == Wy_CString) {
	swp = str;
	str = wycc_cow_string(swp);
	wycc_deref_box(swp);
    } else if (str->cnt > 1) {
	swp = str;
	str = wycc_cow_string(swp);
	wycc_deref_box(swp);
    };
    txt = str->ptr;
    lsiz = strlen(txt);
    idx = (long) osv->ptr;
    if ((idx < 0) || (idx >= lsiz)) {
	fprintf(stderr
		, "ERROR: out of bounds offset value in wyil_update_string\n");
	exit(-4);
    };
    if (str->cnt > 1) {
	str = wycc_cow_string(str);
    };
    tmp = (int) rhs->ptr;
    txt[idx] = (char) tmp;
    return str;
}

/*
 * update an element of a list
 */
wycc_obj* wyil_update_list(wycc_obj* lst, wycc_obj* osv, wycc_obj* rhs){
    WY_OBJ_SANE(lst, "wyil_update_list lst");
    WY_OBJ_SANE(osv, "wyil_update_list osv");
    WY_OBJ_SANE(rhs, "wyil_update_list rhs");
    void** p = lst->ptr;
    wycc_obj* itm;
    long lsiz, idx;

    if (lst->typ != Wy_List) {
	fprintf(stderr, "ERROR: list in wyil_update_list is type %d\n"
		, lst->typ);
	exit(-3);
    };
    if (osv->typ != Wy_Int) {
	fprintf(stderr, "ERROR: offset  value in wyil_update_list is type %d\n"
		, osv->typ);
	exit(-3);
    };
    lsiz = wycc_length_of_list(lst);
    idx = (long) osv->ptr;
    if ((idx < 0) || (idx >= lsiz)) {
	fprintf(stderr
		, "ERROR: out of bounds offset value in wyil_update_list\n");
	exit(-3);
    };
    if (lst->cnt > 1) {
	lst = wycc_cow_list(lst);
	p = lst->ptr;
    };
    itm = p[2+idx];
    p[2+idx] = rhs;
    rhs->cnt++;
    wycc_deref_box(itm);
    return lst;
}

/*
 * create a list that combines two lists
 */
wycc_obj* wyil_list_comb(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_list_comb lhs");
    WY_OBJ_SANE(rhs, "wyil_list_comb rhs");
    wycc_obj* ans;
    wycc_obj* itm;
    long lsiz, rsiz, idx, siz;

    if (lhs->typ != Wy_List) {
	fprintf(stderr, "ERROR: lhs in wyil_list_comb is type %d\n", lhs->typ);
	exit(-3);
    };
    if (rhs->typ != Wy_List) {
	fprintf(stderr, "ERROR: rhs in wyil_list_comb is type %d\n", rhs->typ);
	exit(-3);
    };
    lsiz = wycc_length_of_list(lhs);
    rsiz = wycc_length_of_list(rhs);
    siz = lsiz + rsiz;
    siz += 2;	/* pad it a lttle */
    ans = wycc_list_new(siz);
    for (idx=0 ; idx < lsiz; idx++) {
	itm = wycc_list_get(lhs, idx);
	itm->cnt++;
	wycc_list_add(ans, itm);
    }
    for (idx=0 ; idx < rsiz; idx++) {
	itm = wycc_list_get(rhs, idx);
	itm->cnt++;
	wycc_list_add(ans, itm);
    }
    return ans;
}

/*
 * given a wycc obj return an object that represents the length
 */
wycc_obj* wyil_length_of(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wyil_length_of");
    long typ = itm->typ;
    int rslt = 0;

    if (typ == Wy_List) {
	rslt = wycc_length_of_list(itm);
    } else if (typ == Wy_Set) {
	rslt = wycc_length_of_set(itm);
    } else if (typ == Wy_Map) {
	rslt = wycc_length_of_map(itm);
    } else if (typ == Wy_String) {
	rslt = wycc_length_of_string(itm);
    } else if (typ == Wy_CString) {
	rslt = wycc_length_of_string(itm);
    } else {
	fprintf(stderr, "Help needed in lengthOf for type %d\n", itm->typ);
	exit(-3);
    };
    return wycc_box_int(rslt);
}

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
    WY_OBJ_SANE(ptr1, "wyil_debug_obj");
    char* mesg;
    /* if (ptr1->typ == 1) { */
    if (ptr1->typ == Wy_String) {
	fprintf(stderr, "%s\n", ptr1->ptr);
    } else if (ptr1->typ == Wy_CString) {
	fprintf(stderr, "%s\n", ptr1->ptr);
    } else {
	fprintf(stderr, "Help needed in Debug for type %d\n", ptr1->typ);
    };
    return;
}

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
	fprintf(stderr, "Help needed in strappend for type %d\n", lhs->typ);
	exit(-3);
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
	fprintf(stderr, "Help needed in strappend for type %d\n", rhs->typ);
	exit(-3);
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
 * rudimentary negation operation
 */
wycc_obj* wyil_negate(wycc_obj* itm){
    WY_OBJ_SANE(itm, "wyil_negate");

    if (itm->typ == Wy_Int) {
	return wycc_box_long(-((long) itm->ptr));
    }
    fprintf(stderr, "Help needed in wyil_negate for wide ints (%d\n", itm->typ);
    exit(-3);
}

/*
 * rudimentary add operation
 */
wycc_obj* wyil_add(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_add lhs");
    WY_OBJ_SANE(rhs, "wyil_add rhs");
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
    WY_OBJ_SANE(lhs, "wyil_sub lhs");
    WY_OBJ_SANE(rhs, "wyil_sub rhs");
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
    WY_OBJ_SANE(lhs, "wyil_bit_and lhs");
    WY_OBJ_SANE(rhs, "wyil_bit_and rhs");
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
    WY_OBJ_SANE(lhs, "wyil_bit_ior lhs");
    WY_OBJ_SANE(rhs, "wyil_bit_ior rhs");
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
    WY_OBJ_SANE(lhs, "wyil_bit_xor lhs");
    WY_OBJ_SANE(rhs, "wyil_bit_xor rhs");
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
    WY_OBJ_SANE(lhs, "wyil_div lhs");
    WY_OBJ_SANE(rhs, "wyil_div rhs");
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
    WY_OBJ_SANE(lhs, "wyil_mod lhs");
    WY_OBJ_SANE(rhs, "wyil_mod rhs");
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
    WY_OBJ_SANE(lhs, "wyil_mul lhs");
    WY_OBJ_SANE(rhs, "wyil_mul rhs");
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
    WY_OBJ_SANE(lhs, "wyil_shift_up lhs");
    WY_OBJ_SANE(rhs, "wyil_shift_up rhs");
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
    WY_OBJ_SANE(lhs, "wyil_shift_down lhs");
    WY_OBJ_SANE(rhs, "wyil_shift_down rhs");
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

static wycc_obj* wyil_index_of_list(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_index_of_list lhs");
    WY_OBJ_SANE(rhs, "wyil_index_of_list rhs");
    void** p = lhs->ptr;
    wycc_obj* ans;
    long idx;

    if (rhs->typ != Wy_Int) {
	fprintf(stderr, "Help needed in wyil_index_of_list for type %d\n"
		, rhs->typ);
	exit(-3);
    };
    idx = (long) rhs->ptr;
    if (idx < 0) {
	fprintf(stderr, "ERROR: IndexOf under range for list (%d)\n", idx);
	exit(-4);
    };
    if (idx >= (long) p[1]) {
	fprintf(stderr, "ERROR: IndexOf over range for list (%d)\n", idx);
	exit(-4);
    };
    ans = (wycc_obj*) p[2+idx];
    ans->cnt++;
    return ans;
}

static wycc_obj* wyil_index_of_map(wycc_obj* map, wycc_obj* key){
    WY_OBJ_SANE(map, "wyil_index_of_map map");
    WY_OBJ_SANE(key, "wyil_index_of_map key");
    void** p = map->ptr;
    void** chunk;
    wycc_obj* ans;
    wycc_obj* tst;
    long at, typ, cnt;
    // int (*compar)(wycc_obj* lhs, wycc_obj* rhs);
    int end;

    typ = (long) p[0];
    if (typ == Wy_None) {
	fprintf(stderr, "ERROR: IndexOf for empty map \n");
	exit(-4);
    };
    at = 0;
    chunk = &(p[2]);
    if (((long) p[1]) <1) {
	fprintf(stderr, "ERROR: IndexOf for empty map \n");
	exit(-4);
    }
    // compar = wycc_get_comparator(typ);
    /*
     * sequencial search within the chunk
     */
    while (at < ((long) chunk[0])) {
	at++;
	tst = (wycc_obj *) chunk[3*at];
	// end = compar(key, tst);
	end = wycc_comp_gen(key, tst);
	if (end == 0) {
	    /* key match == done ; swap the value stored */
	    ans = (wycc_obj *) chunk[(3*at) -1];
	    ans->cnt++;
	    return ans;
	};
	if (end < 0) {
	    continue;
	};
	/* the item in question goes before here; ergo down a chunk */
	chunk = chunk[(3*at) - 2];
	at = 0;
    };
    at *= 3;		/* 3 ::= sizeof branch value key triplet */
    while (at < (WYCC_MAP_CHUNK -2)) {
	at += 2;
	tst = (wycc_obj *) chunk[at];
	if (tst == (wycc_obj *) NULL) {
	    fprintf(stderr, "ERROR: key not found IndexOf map \n");
	    exit(-4);
	}
	// end = compar(key, tst);
	end = wycc_comp_gen(key, tst);
	if (end == 0) {
	    /* key match == done ; swap the value stored */
	    ans = (wycc_obj *) chunk[at -1];
	    ans->cnt++;
	    return ans;
	};
	if (end < 0) {
	    break;
	};
    };
    fprintf(stderr, "ERROR: key not found IndexOf map \n");
    exit(-4);

}

static wycc_obj* wyil_index_of_string(wycc_obj* str, wycc_obj* index){
    WY_OBJ_SANE(str, "wyil_index_of_string str");
    WY_OBJ_SANE(index, "wyil_index_of_string index");
    char *txt;
    long idx;
    char rslt;

    if (index->typ != Wy_Int) {
	fprintf(stderr, "Help needed in wyil_index_of_string for type %d\n"
		, index->typ);
	exit(-3);
    };
    idx = (long) index->ptr;
    if (idx < 0) {
	fprintf(stderr, "ERROR: IndexOf under range for string (%d)", idx);
	exit(-4);
    };
    txt = str->ptr;
    if (idx >= strlen(txt)) {
	fprintf(stderr, "ERROR: IndexOf over range for string (%d)", idx);
	exit(-4);
    };
    rslt = txt[idx];
    return wycc_box_char(rslt);
}

/*
 * given two small integers (**** need to support wide ints)
 * construct a list of with members in that range.
 */
wycc_obj* wyil_range(wycc_obj* lhs, wycc_obj* rhs) {
    WY_OBJ_SANE(lhs, "wyil_range lhs");
    WY_OBJ_SANE(rhs, "wyil_range rhs");
    wycc_obj* ans;
    wycc_obj* itm;
    int lo, hi, sz, idx;

    if (lhs->typ != Wy_Int) {
	fprintf(stderr, "HELP needed in wyil_range for start of type %d\n"
		, lhs->typ);
	exit(-3);
    };
    if (rhs->typ != Wy_Int) {
	fprintf(stderr, "HELP needed in wyil_range for end of type %d\n"
		, rhs->typ);
	exit(-3);
    };
    lo = (int) lhs->ptr;
    hi = (int) rhs->ptr;
    sz = hi - lo;
    if (sz < 0) {
	lo = hi;
	hi = -1;
    } else {
	hi = 1;
    };
    ans = wycc_list_new(sz);
    for (idx=0 ; idx < sz; idx++) {
	itm = wycc_box_int((idx * hi) + lo);
	wycc_list_add(ans, itm);
    };
    return ans;
}

wycc_obj* wyil_index_of(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_index_of lhs");
    WY_OBJ_SANE(rhs, "wyil_index_of rhs");
    wycc_obj* ans;

    if (lhs->typ == Wy_List) {
	return wyil_index_of_list(lhs, rhs);
    };
    if (lhs->typ == Wy_Map) {
	return wyil_index_of_map(lhs, rhs);
    };
    if (lhs->typ == Wy_String) {
	return wyil_index_of_string(lhs, rhs);
    };
    if (lhs->typ == Wy_CString) {
	return wyil_index_of_string(lhs, rhs);
    };
	fprintf(stderr, "Help needed in wyil_index_of for type %d\n", lhs->typ);
	exit(-3);
}

/*
 * ******************************
 * whiley standard library ie., native methods
 * ******************************
 *
 * Note: there is a double underscore (__) following the "wycc"
 * in these names.
 */

/*
 * given a System object, write a line to the file referred to by out
 */
void wycc__println(wycc_obj* sys, wycc_obj* itm) {
    /* WY_OBJ_SANE(sys); */
    WY_OBJ_SANE(itm, "wycc__println itm");
    wycc_obj* alt;
    int tmp;

    if (sys->typ != Wy_Ref) {
	fprintf(stderr, "Help needed in wycc__println for type %d\n", sys->typ);
    };
    if (itm->typ == Wy_String) {
	printf("%s\n", (char *) itm->ptr);
	return;
    };
    if (itm->typ == Wy_CString) {
	printf("%s\n", (char *) itm->ptr);
	return;
    };
    if (itm->typ == Wy_Char) {
	tmp = (int) itm->ptr;
	printf("'%c'\n", (char) tmp);
	return;
    };
    if (itm->typ == Wy_Int) {
	printf("%-.1d\n", (long) itm->ptr);
	return;
    };
    if (itm->typ == Wy_Bool) {
	if (itm->ptr == NULL) {
	    printf("false\n");
	} else {
	    printf("true\n");
	}
	return;
    };
    alt = wycc__toString(itm);
    printf("%s\n", alt->ptr);
    wycc_deref_box(alt);
}


/*
 * given a record, step thru every slot pairing with its meta data
 */
static wycc_obj *wycc__toString_record(wycc_obj *itm){
    WY_OBJ_SANE(itm, "wycc__toString_record");
    wycc_obj *meta;
    wycc_obj *nams;
    wycc_obj *nxt;
    void** pa;
    void** p = (void **) itm->ptr;
    int cnt;
    size_t siz;
    long at;
    int idx;
    int tmp;
    char *str;
    char *buf;

    if (((int)p[0]) == -1) {
	return wycc_box_cstr("Unknown");
    };
    meta = (wycc_obj *) p[0];
    cnt = (int) p[1];
    if (meta->typ != Wy_Rcd_Rcd) {
	fprintf(stderr, "Help needed in wycc__toString_record, bad meta %d\n"
		, meta->typ);
	exit(-3);
    };
    pa = (void **) meta->ptr;
    nams = pa[0];
    if (nams->typ != Wy_List) {
	fprintf(stderr, "Help needed in wycc__toString_record, bad names %d\n"
		, meta->typ);
	exit(-3);
    };
    siz = 1 + (4 * cnt);
    buf = (char *) malloc(siz);
    buf[0] = '\0';
    strncat(buf, "{", siz);
    at = 1;
    pa = (void **) nams->ptr;
    for (idx= 0; idx < cnt; idx++) {
	nxt = (wycc_obj *) pa[2+idx];
	if ((nxt->typ != Wy_String) && (nxt->typ != Wy_CString)) {
	    fprintf(stderr, "Help needed in wycc__toString_record for name %d\n"
		    , nxt->typ);
	    exit(-3);
	};
	str = (char *) nxt->ptr;
	tmp = strlen(str);
	if (siz <= (at+tmp+4)) {
	    siz += tmp + ((cnt - idx) * 4);
	    buf = (char*) realloc((void*)buf, siz);
	};
	if (at > 1) {
	    strcpy((buf+at), ",");
	    at += 1;
	};
	strcpy((buf+at), str);
	at += tmp;
	nxt = (wycc_obj *) p[2+idx];
	nxt = wycc__toString(nxt);
	str = (char *) nxt->ptr;
	tmp = strlen(str);
	if (siz <= (at+tmp+4)) {
	    siz += tmp + ((cnt - idx) * 4);
	    buf = (char*) realloc((void*)buf, siz);
	};
	strcpy((buf+at), ":");
	at += 1;
	strcpy((buf+at), str);
	at += tmp;
    }
    if (siz <= (at+4)) {
	siz += 4;
	buf = (char*) realloc((void*)buf, siz);
    };
    strcpy((buf+at), "}");
    at += 1;
    return wycc_box_str(buf);
    
}


/*
 * given a set, step thru every slot
 */
static wycc_obj *wycc__toString_set_alt(wycc_obj *itm){
    WY_OBJ_SANE(itm, "wycc__toString_set_alt");
    /* (void **chunk, char* buf, size_t *isiz) { */
    struct chunk_ptr my_chunk_ptr;
    struct chunk_ptr *chptr = & my_chunk_ptr;
    long tmp;
    wycc_obj* nxt;
    size_t siz;
    long cnt, idx, at;
    char *buf;
    char *part;
    char *ptr;

    cnt = wycc_length_of_set(itm);
    chptr->cnt = cnt;
    siz = 3 + (cnt * 4);	/* minimalist approx. */
    buf = (char *) malloc(siz);
    buf[0] = '\0';
    strncat(buf, "{", siz);
    at = 1;
    void **p = itm->ptr;
    chptr->p = p;
    chptr->chk = &(p[2]);
    chptr->at = 0;
    chptr->flg = 0;	/* this is a set */
    while (1) {
	wycc_chunk_ptr_inc(chptr);
	nxt = chptr->key;
	if (nxt == NULL) {
	    break;
	};
	nxt = wycc__toString(nxt);
	tmp = strlen(nxt->ptr);
	if (siz <= (at+tmp+3)) {
	    siz += tmp + (chptr->cnt * 4);
	    buf = (char*) realloc((void*)buf, siz);
	};
	if (at > 1) {
	    strcpy((buf+at), ", ");
	    at += 2;
	};
	strcpy((buf+at), nxt->ptr);
	at += tmp;
	wycc_deref_box(nxt);
    };
    at = strlen(buf);
    strcpy((buf+at), "}");
    return wycc_box_str(buf);
}

/*
 * given a chunk of a map, step thru every slot, digressing as needed
 */
static wycc_obj *wycc__toString_map_alt(wycc_obj *itm){
    WY_OBJ_SANE(itm, "wycc__toString_map_alt");
    /* (void **chunk, char* buf, size_t *isiz) { */
    struct chunk_ptr my_chunk_ptr;
    struct chunk_ptr *chptr = & my_chunk_ptr;
    long tmp;
    wycc_obj* nxt;
    wycc_obj* val;
    size_t siz;
    long cnt, idx, at;
    char *buf;
    char *part;
    char *ptr;

    cnt = wycc_length_of_map(itm);
    chptr->cnt = cnt;
    siz = 3 + (cnt * 10);	/* minimalist approx. */
    buf = (char *) malloc(siz);
    buf[0] = '\0';
    strncat(buf, "{", siz);
    at = 1;
    void **p = itm->ptr;
    chptr->p = p;
    chptr->chk = &(p[2]);
    chptr->at = 0;
    chptr->flg = 1;	/* this is a set */
    while (1) {
	wycc_chunk_ptr_inc(chptr);
	nxt = chptr->key;
	val = chptr->val;
	if (nxt == NULL) {
	    break;
	};
	if (val == NULL) {
	    break;
	};
	nxt = wycc__toString(nxt);
	tmp = strlen(nxt->ptr);
	if (siz <= (at+tmp+5)) {
	    siz += tmp + (chptr->cnt * 10);
	    buf = (char*) realloc((void*)buf, siz);
	};
	if (at > 1) {
	    strcpy((buf+at), ", ");
	    at += 2;
	};
	strcpy((buf+at), nxt->ptr);
	at += tmp;
	wycc_deref_box(nxt);
	strcpy((buf+at), "=>");
	at += 2;
	nxt = wycc__toString(val);
	tmp = strlen(nxt->ptr);
	if (siz <= (at+tmp+3)) {
	    siz += tmp +(chptr->cnt * 10);
	    buf = (char*) realloc((void*)buf, siz);
	};
	strcpy((buf+at), nxt->ptr);
	at += tmp;
	wycc_deref_box(nxt);
    };
    at = strlen(buf);
    strcpy((buf+at), "}");
    return wycc_box_str(buf);
}

wycc_obj* wycc__toString(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc__toString");
    size_t siz;
    long tmp, tmpa, tmpb;
    long cnt, idx, at;
    char *buf;
    char *part;
    char *ptr;
    wycc_obj* nxt;

    if (itm->typ == Wy_String) {
	itm->cnt++;
	return itm;
    };
    if (itm->typ == Wy_CString) {
	itm->cnt++;
	return itm;
    };
    if (itm->typ == Wy_Int) {
	tmp = (int) itm->ptr;
	buf = (char *) malloc(16);
	sprintf(buf, "%-.1d", tmp);
	return wycc_box_str(buf);
    };
    if (itm->typ == Wy_Char) {
	tmp = (int) itm->ptr;
	buf = (char *) malloc(6);
	sprintf(buf, "'%-.1c'", (char) tmp);
	return wycc_box_str(buf);
    };
    if (itm->typ == Wy_Bool) {
	if (itm->ptr == NULL) {
	    return wycc_box_cstr("false");
	}
	return wycc_box_cstr("true");
    };
    if (itm->typ == Wy_Null) {
	return wycc_box_cstr("null");
    };
    if (itm->typ == Wy_List) {
	cnt = wycc_length_of_list(itm);
	siz = 3 + (cnt * 4);	/* minimalist approx. */
	buf = (char *) malloc(siz);
	buf[0] = '\0';
	strncat(buf, "[", siz);
	at = 1;
	for (idx=0 ; idx < cnt; idx++) {
	    nxt = wycc_list_get(itm, idx);
	    nxt = wycc__toString(nxt);
	    tmp = strlen(nxt->ptr);
	    tmpa = at + tmp + 3;
	    if (siz <= tmpa) {
		tmpb = siz + ((cnt - idx) * (tmp + 2));
		if (tmpa < tmpb) {
		    siz = tmpb;
		}else {
		    siz = tmpa;
		};
		buf = (char*) realloc((void*)buf, siz);
	    };
	    if (idx > 0) {
		strcpy((buf+at), ", ");
		at += 2;
	    };
	    strcpy((buf+at), nxt->ptr);
	    at += tmp;
	    wycc_deref_box(nxt);
	}
	strcpy((buf+at), "]");
	return wycc_box_str(buf);
    };
    if (itm->typ == Wy_Set) {
	return wycc__toString_set_alt(itm);
    };
    if (itm->typ == Wy_Map) {
	return wycc__toString_map_alt(itm);
    };
    if (itm->typ == Wy_Record) {
	return wycc__toString_record(itm);
    };
    return wycc_box_cstr("Unknown");
}


/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
