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
#include <string.h>
#include "common.h"
#include "FOM.h"
#include "ytype.h"
#include "box.h"
#include "math.h"

/*
 * ******************************
 * wycc infrastructure
 * ******************************
 */

// #define Save_Name 1

#ifndef WY_OBJ_SAFE
int wycc__mile__stone = 0;
#endif

static wycc_obj *obj_free_list = (wycc_obj *) NULL;
static void wycc_obj_free(wycc_obj* itm);
static void wycc_obj_free_bump();

/*
 * we need comparitor functions for all object types
 */
static int wycc_comp_gen(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_str(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_set(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_list(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_rmeta(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_record(wycc_obj* lhs, wycc_obj* rhs);
static int wycc_comp_obj(wycc_obj* lhs, wycc_obj* rhs);

#define WYCC_SET_CHUNK 8
#define WYCC_MAP_CHUNK 8*3

struct chunk_ptr {
    void **p;		/* the top level set, map, or list */
    void **chk;		/* the current chunk */
    wycc_obj *key;	/* the current key object */
    wycc_obj *val;	/* the current value object if any*/
    long cnt;		/* the number of items remaining */
    long at;		/* position in the set */
    int idx;		/* position in the chunk */
    int flg;		/* 0==set, 1==map, 2==list, 4==string */
};


// typedef int (*Wycc_Comp_Ptr)(wycc_obj* lhs, wycc_obj* rhs);
typedef wycc_obj *(*Wycc_Convert)(wycc_obj* itm);

/*
 * the root of a link list of initialiser records
 */
wycc_initor* wycc_init_chain = NULL;

/*
 * a registry of record meta data (Rcd_Rcd)
 */
static void* wycc_fields_master = NULL;

/*
 * a very ugly kludge to handle converting lists to maps.
 */
static wycc_obj *map_add_odd = NULL;

static wycc_obj *type_dict = NULL;
static int type_count = 0;
static int type_alloc = 0;

static void *type_parsings = NULL;
//	  gdb	 p ((struct type_desc *)type_parsings)[103]
//	  gdb	 p *((wycc_obj *) $3)

struct type_desc {
#if Save_Name
    char * nam;
#endif
    int flgs;
    int down;
    int next;
};
#define Type_Odd	  1
#define Type_Leaf	  2
#define Type_Negate	  4
#define Type_Union	  8
#define Type_List	 16
#define Type_Set	 32
#define Type_Map	 64
#define Type_Record	128
#define Type_Tuple	256       
#define Type_Reference	512       
#define Type_Method    1024       
#define Type_Funct     2048       
#define Type_Chain     4096       
#define Type_Frac      8192       
#define Type_Recurs   16384      

static char *Recursive_Type_Letters = "XYZUVWLMNOPQRST";
static int *Recursive_Type_Tokens = NULL;
static int Recursive_Type_Token_Max = 0;

/*
 * Odd:		no down, next
 * Leaf:	no down, next
 * List, Set:	no next; down is member type
 * Map:		down is typeof key, next is typeof val
 * Union:	next is chain (continued), down is subtype 
 * Tuple:	next is chain (continued), down is subtype of field
 * Record:	next is tuple (started), down is Record_Record
 * Recurs:	no next; down is subtype
 * Leaf:	no down, next
 * Leaf:	no down, next
 * Referene:	no next; down is type referred to
 */
#define Type_Parsings_Alloc	40
static int my_type_any  = -1;
static int my_type_int  = -1;
static int my_type_real = -1;
static int my_type_bool = -1;
static int my_type_byte  = -1;
static int my_type_char  = -1;
static int my_type_null  = -1;
static int my_type_void  = -1;
static int my_type_string = -1;
static int my_type_x    = -1;

typedef void ((funct_fill)(wycc_obj *col, wycc_obj *key, wycc_obj *val, int cnt));

static void wycc_list_add_odd(wycc_obj* lst, wycc_obj* key, wycc_obj* val, int cnt);
static void wycc_set_add_odd(wycc_obj* col, wycc_obj* key, wycc_obj* val, int cnt);
static void wycc_map_add_odd(wycc_obj* lst, wycc_obj* key, wycc_obj* itm, int cnt);
static void wycc_tuple_add_odd(wycc_obj* lst, wycc_obj* key, wycc_obj* val, int cnt);

static int wycc_type_check_tok(wycc_obj* itm, int tok, int force);
static int wycc_type_parse(const char *nam, int *lo, int hi);
static int wycc_type_flags(int id);
static int wycc_type_tok_alloc();

void wycc_chunk_rebal(int payl, void** p, void** chunk, long at, long deep);
static int wycc_type_down(int id);
static int wycc_type_next(int id);
static int wycc_type_is_odd(int id);
static int wycc_type_is_leaf(int id);
static int wycc_type_is_list(int id);
static int wycc_type_is_map(int id);
static int wycc_type_is_iter(int id);
static int wycc_type_is_tuple(int id);
static int wycc_type_is_record(int id);
static int wycc_type_is_union(int id);
static int wycc_type_is_negate(int id);
static int wycc_type_is_frac(int id);
static int wycc_type_is_recurs(int id);
static int wycc_type_is_chain(int id);
static int wycc_type_is_ref(int id);
static void wycc_chunk_ptr_fill(struct chunk_ptr *ptr, wycc_obj *itm, int typ);
static void wycc_chunk_ptr_inc(struct chunk_ptr *chunk);
static void wycc_chunk_ptr_fill_as(struct chunk_ptr *ptr, wycc_obj *itm);

static wycc_obj* wyil_convert_tok(wycc_obj* itm, int tok, int force);
static wycc_obj* wyil_set_add_list(wycc_obj* set, wycc_obj* lst);
static wycc_obj* wyil_set_union_list(wycc_obj* lhs, wycc_obj* rhs);
static wycc_obj* wyil_add_ld(wycc_obj* lhs, wycc_obj* rhs);
static wycc_obj* wyil_sub_ld(wycc_obj* lhs, wycc_obj* rhs);
static wycc_obj* wyil_div_ld(wycc_obj* lhs, wycc_obj* rhs);
static wycc_obj* wyil_mul_ld(wycc_obj* lhs, wycc_obj* rhs);
static wycc_obj* wyil_index_of_list(wycc_obj* lhs, wycc_obj* rhs);
static wycc_obj* wyil_index_of_map(wycc_obj* map, wycc_obj* key);
static wycc_obj* wyil_index_of_string(wycc_obj* str, wycc_obj* index);
static wycc_obj *wycc__toString_record(wycc_obj *itm);
static wycc_obj *wycc__toString_set_alt(wycc_obj *itm);
static wycc_obj *wycc__toString_map_alt(wycc_obj *itm);
static wycc_obj *wycc__toString_array(wycc_obj *itm);
static wycc_obj * wycc_type_record_names(int tok);
static int wycc_type_isleaf(wycc_obj *itm);
static int wycc_type_ishomo(wycc_obj *itm);
static int wycc_type_subsume_records(int itok, int atok);
static int wycc_type_subsume_tokens(int itok, int atok);
static int wycc_type_subsume_tuples(int itok, int atok);
static int wycc_type_subsume_lists(int itok, int atok);
static int wycc_type_subsume_leaves(int itok, int atok);


/* -------------------------------
 *  internal routines
 * -------------------------------
 */


/*
 * register the fundamental types first (before anyone else can grab
 * those numbers).  The system should work even if the numbers are scrambled
 * but having them in this order makes debugging easier.
 */
// int wycc_type_internal(const char *nam);
void wycc_type_init() {
    int tmp;

    type_dict = wycc_map_new(-1);
    my_type_any  = wycc_type_internal("a");		/* 0 */
    my_type_null   = wycc_type_internal("n");		/* 1 */
    my_type_void   = wycc_type_internal("v");		/* 2 */
    my_type_bool   = wycc_type_internal("b");	/* 3 */
    my_type_byte   = wycc_type_internal("d");	/* 4 */
    my_type_char   = wycc_type_internal("c");	/* 5 */
    my_type_int    = wycc_type_internal("i");		/* 6 */
    my_type_real   = wycc_type_internal("r");	/* 7 */
    my_type_string = wycc_type_internal("s");	/* 8 */
    //    my_type_x      = wycc_type_internal("X");		/* 9 */
    return;
}

/*
 * register the routines that make up Whiley's standard lib
 * (its stdlib equivalent as verses its libc).
 *
 * **** need to change things in lookup to be able to do impedence matching
 * like for toString - any should cover all the other cases
 */
void wycc_register_lib(){
    wycc_register_routine("print", "[:v,v,[.a],s]", wycc__print);
    wycc_register_routine("print", "[:v,v,[.a],a]", wycc__print);
    wycc_register_routine("println", "[:v,v,[.a],s]", wycc__println);
    wycc_register_routine("println", "[:v,v,[.a],a]", wycc__println);
    wycc_register_routine("toString", "[^s,v,a]", wycc__toString);
    wycc_register_routine("toString", "[^s,v,b]", wycc__toString);
    wycc_register_routine("toString", "[^s,v,c]", wycc__toString);
    wycc_register_routine("toString", "[^s,v,i]", wycc__toString);
    wycc_register_routine("toString", "[^s,v,r]", wycc__toString);
    //wycc_register_routine("abs", "[^i,v,i]", wycc__abs);
    //wycc_register_routine("abs", "[^r,v,r]", wycc__abs);
    //wycc_register_routine("max", "[^i,v,i,i]", wycc__max);
    //wycc_register_routine("max", "[^r,v,r,r]", wycc__max);
    //wycc_register_routine("min", "[^i,v,i,i]", wycc__min);
    //wycc_register_routine("min", "[^r,v,r,r]", wycc__min);
    //wycc_register_routine("isLetter", "[^b,v,c]", wycc__isLetter);
    //wycc_register_routine("toUnsignedByte", "[^d,v,i]", wycc__toUnsignedByte);
    //wycc_register_routine("toUnsignedInt", "[^i,v,d]", wycc__toUnsignedInt);
    //wycc_register_routine("isDigit", "[^b,v,c]", wycc__isDigit);
    //wycc_register_routine("toChar", "[^c,v,d]", wycc__toChar);


}

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
 * given a long, mask it down to its most significant bit
 */
static wycc_high_bit(long itm) {
    int tmpa, tmpb;

    tmpb = 0;
    for (tmpa= itm; tmpa > 0; tmpa &= (tmpa-1)) {
	tmpb = tmpa;
    }
    //if (wycc_debug_flag) {
    //	fprintf(stderr, "wycc_high_bit(%d) => %d\n", itm, tmpb);
    //}
    return tmpb;
}

static void wycc_obj_free(wycc_obj* itm) {
    itm->ptr = obj_free_list;
    itm->typ = Wy_None;
    obj_free_list = itm;
}

static void wycc_obj_free_bump() {
    wycc_obj *array;
    wycc_obj *itm;
    int idx;

    array = (wycc_obj *) calloc(1024, sizeof(wycc_obj));
    for (idx= 0; idx <1024 ; idx++) {
	itm = &(array[idx]);
	wycc_obj_free(itm);
    }
}

/* -------------------------------
 *  Routines for basic support general infrastructure
 * -------------------------------
 */

/* -------------------------------
 *  Routines for basic support of typed objects
 * -------------------------------
 */
wycc_obj* wycc_box_type_match(wycc_obj *lhs, wycc_obj *rhs, long rslt) {

    if (lhs->typ == Wy_Char) {
	return wycc_box_char((char) rslt);
    };
    if (rhs->typ == Wy_Char) {
	return wycc_box_char((char) rslt);
    };
    if (lhs->typ == Wy_Byte) {
	return wycc_box_byte((char) rslt);
    };
    if (rhs->typ == Wy_Byte) {
	return wycc_box_byte((char) rslt);
    };
    return wycc_box_long(rslt);
}

/*
 * given a wycc_obj, report if is a leaf (primative) or a composite.
 */
static int wycc_type_isleaf(wycc_obj *itm) {
    int typ = itm->typ;

    if (typ == Wy_String) {
	return 1;
    };
    if (typ == Wy_Int) {
	return 1;
    };
    if (typ == Wy_WInt) {
	return 1;
    };
    if (typ == Wy_CString) {
	return 1;
    };
    if (typ == Wy_Char) {
	return 1;
    };
    if (typ == Wy_Bool) {
	return 1;
    };
    if (typ == Wy_Null) {
	return 1;
    };
    if (typ == Wy_Byte) {
	return 1;
    };
    if (typ == Wy_Float) {
	return 1;
    };
   if (typ == Wy_WString) {
	return 1;
    };
    return 0;
}

/*
 * given a wycc_obj, report if is a simple composite
 */
static int wycc_type_ishomo(wycc_obj *itm) {
    int typ = itm->typ;

    if (typ == Wy_List) {
	return 1;
    };
    if (typ == Wy_Set) {
	return 1;
    };
    if (typ == Wy_Map) {
	return 1;
    };
    if (typ == Wy_Fields) {
	return 1;
    };
    if (typ == Wy_String) {
	return 1;
    };
    if (typ == Wy_CString) {
	return 1;
    };
   if (typ == Wy_WString) {
	return 1;
    };
    return 0;
}

/*
 * here we add the extra overhead of yet another routine,
 * this one to allocate a wycc_obj.
 * **** future performance improvement would be to 
 * ** allocate a bunch at a time.
 * ** stop freeing them and make a free list so we don't have to call malloc.
 * ** switch to C++ and inline this function.
 */
wycc_obj* wycc_box_new(int typ, void* ptr) {
    wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    ans = obj_free_list;
    if (ans == NULL) {
	wycc_obj_free_bump();
	ans = obj_free_list;
    }
    obj_free_list = ans->ptr;
    ans->typ = typ;
    ans->cnt = 1;
    ans->ptr = ptr;
    return ans;
}

/*
 * given an address, box it in a wycc_obj
 */
wycc_obj* wycc_box_addr(void* ptr) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Addr;
    //ans->cnt = 1;
    //ans->ptr = (void*) ptr;
    //return ans;
    return wycc_box_new(Wy_Addr, ptr);
}

/*
 * given a text string, box it in a wycc_obj
 */
wycc_obj* wycc_box_str(char* text) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_String;
    //ans->cnt = 1;
    //ans->ptr = (void*) text;
    //return ans;
    return wycc_box_new(Wy_String, (void*) text);
}

/*
 * given a text string, box it in a wycc_obj
 */
wycc_obj* wycc_box_cstr(const char* text) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_CString;
    //ans->cnt = 1;
    //ans->ptr = (void*) text;
    //return ans;
    return wycc_box_new(Wy_CString, (void*) text);
}

/*
 * given a char, box it in a wycc_obj
 */
wycc_obj* wycc_box_char(char x) {
    //wycc_obj* ans;
    int tmp;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Char;
    //ans->cnt = 1;
    tmp = (int) x;	/* **** kludge */
    //ans->ptr = (void *) tmp;	/* **** kludge */
    //return ans;
    return wycc_box_new(Wy_Char, (void*) tmp);
}

/*
 * given a boolean, box it in a wycc_obj
 */
wycc_obj* wycc_box_bool(int x) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Bool;
    //ans->cnt = 1;
    //ans->ptr = (void *) x;	/* **** kludge */
    //return ans;
    return wycc_box_new(Wy_Bool, (void*) x);
}

/*
 * given an int (32 bits), box it in a wycc_obj
 */
wycc_obj* wycc_box_int(int x) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Int;
    //ans->cnt = 1;
    //ans->ptr = (void*) x;	/* **** kludge */
    //return ans;
    return wycc_box_new(Wy_Int, (void*) x);
}

/*
 * given an int (32 bits), box it in a wycc_obj as a reference token
 */
wycc_obj* wycc_box_token(int x) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Token;
    //ans->cnt = 1;
    //ans->ptr = (void*) x;	/* **** kludge */
    //return ans;
    return wycc_box_new(Wy_Token, (void*) x);
}

/*
 * given an int (only 8 bits), box it in a wycc_obj as a byte
 */
wycc_obj* wycc_box_byte(int x) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Byte;
    //ans->cnt = 1;
    //ans->ptr = (void*) x;
    //return ans;
    return wycc_box_new(Wy_Byte, (void*) x);
}

/*
 * given a pointer to wycc_obj, box it in a wycc_obj as a reference
 */
wycc_obj* wycc_box_ref(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_box_ref");
    wycc_obj* alt;

    itm->cnt++;
    //alt = wycc_box_new(Wy_Ref2, (void*) alt);
    alt = wycc_box_new(Wy_Ref2, (void*) itm);
    return wycc_box_new(Wy_Ref1, (void*) alt);
}

/*
 * a pair of objects, box them up as a size 2 tuple
 */
wycc_obj* wycc_box_pair(wycc_obj* key, wycc_obj* val) {
    WY_OBJ_SANE(key, "wycc_box_pair key");
    WY_OBJ_SANE(val, "wycc_box_pair val");
    wycc_obj* ans;

    ans = wycc_tuple_new(2);
    wycc_update_list(ans, key, 0);
    wycc_update_list(ans, val, 1);
    return ans;
}

/*
 * given an long (64 bits), box it in a wycc_obj
 */
wycc_obj* wycc_box_long(long x) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Int;
    //ans->cnt = 1;
    //ans->ptr = (void*) x;	/* **** kludge */
    //return ans;
    return wycc_box_new(Wy_Int, (void*) x);
}

/*
 * given an long double, box it in a wycc_obj
 * **** performance feature:
 *  ** build up a cache of floats somewhat like the wycc_obj cache. 
 */
wycc_obj* wycc_box_float(long double x) {
    //wycc_obj* ans;
    long double *buf;

    if (wycc_debug_flag) {
	fprintf(stderr, "wycc_box_float(%Lg)\n", x);
    }

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    buf = (long double*)  calloc(1, sizeof(long double));
    //ans->typ = Wy_Float;
    //ans->cnt = 1;
    *buf = x;
    //ans->ptr = (void*) buf;
    //return ans;
    return wycc_box_new(Wy_Float, (void*) buf);
}

/*
 * box up a null
 */
wycc_obj* wycc_box_null() {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Null;
    //ans->cnt = 1;
    //ans->ptr = (void*) 0;
    //return ans;
    return wycc_box_new(Wy_Null, (void*) 0);
}

/*
 * box up the meta information for a record type
 */
static wycc_obj* wycc_box_meta(int tok) {
    //wycc_obj* ans;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Rcd_Rcd;
    //ans->ptr = (void *) tok;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_Rcd_Rcd, (void*) tok);
}


static int wycc_type_not_map(char* typ) {
    char *at;

    at = strchr(typ, '{');
    if (at != NULL) {
	return 0;
    };
    at = strchr(typ, '}');
    if (at != NULL) {
	return 0;
    };
    return 1;
}

/*
 * return 1 iff our type (typ) is consistant with the wyil type token
 */
static int wycc_type_comp_loss(int typ, int tok, int force){
    int flgs = wycc_type_flags(tok);
    int alt;

    if (tok == my_type_any) {
	return 1;
    };
    if (wycc_type_is_leaf(tok)) {
	if (tok == my_type_int) {
	    if ((typ == Wy_Int) || (typ == Wy_WInt)){
		return 1;
	    };
	    if ((typ == Wy_Char) || (typ == Wy_Bool)){
		return 1;
	    };
	    if (typ == Wy_Byte) {
		return 1;
	    };
	    return 0;
	};
	if (tok == my_type_real) {
	    if (typ == Wy_Float){
		return 1;
	    };
	    if ((typ == Wy_Int) || (typ == Wy_WInt)){
		return 1;
	    };
	    if ((typ == Wy_Char) || (typ == Wy_Bool)){
		return 1;
	    };
	    if (typ == Wy_Byte) {
		return 1;
	    };
	    return 0;
	};
	if (tok == my_type_bool) {
	    return (typ == Wy_Bool);
	};
	if (tok == my_type_byte) {
	    return (typ == Wy_Byte);
	};
	if (tok == my_type_char) {
	    if ((typ == Wy_Char) || (typ == Wy_Byte)){
		return 1;
	    };
	    return 0;
	};
	if (tok == my_type_string) {
	    return ((typ == Wy_String) || (typ == Wy_CString));
	};
	WY_PANIC("Help needed in wycc_type_comp leaf (%d)\n", tok)
    };
    if (flgs & Type_List) {
	return (typ == Wy_List);
    };
    if (flgs & Type_Set) {
	return (typ == Wy_Set);
    };
    if (flgs & Type_Map) {
	return (typ == Wy_Map);
    };
    if (flgs & Type_Record) {
	return (typ == Wy_Record);
    };
    if (flgs & Type_Frac) {
	return (typ == Wy_Record);
    };
    if (flgs & Type_Tuple) {
	return (typ == Wy_Tuple);
    };
    if (tok == my_type_null) {
	return (typ == Wy_Null);
    }
    if (flgs & Type_Recurs) {
	alt = wycc_type_down(tok);
	return wycc_type_comp_loss(typ, alt, force) ;
    }; 
    if (flgs & Type_Union) {
	while (1) {
	    alt = wycc_type_down(tok);
	    if (wycc_type_comp_loss(typ, alt, force)) {
		return 1;
	    };
	    tok = wycc_type_next(tok);
	    if (tok == 0) {
		return 0;
	    };
	}
    };
    //WY_SEG_FAULT
    WY_PANIC("Help needed in wycc_type_comp other (%d)\n", tok)
    fprintf(stderr, "Help needed in wycc_type_comp other (%d)\n", tok);
    struct type_desc *desc;
    desc = &((struct type_desc *)type_parsings)[tok];
    fprintf(stderr, "\t tok type (%d)\n", desc->flgs);
    exit(-3);

}

/*
 * return 1 iff our type (typ) is consistant with the wyil type token
 */
static int wycc_type_comp(int typ, int tok){
    int flgs = wycc_type_flags(tok);
    int alt;

    if (tok == my_type_any) {
	return 1;
    };
    if (wycc_type_is_leaf(tok)) {
	if (tok == my_type_int) {
	    return ((typ == Wy_Int) || (typ == Wy_WInt));
	};
	if (tok == my_type_real) {
	    return (typ == Wy_Float);
	};
	if (tok == my_type_bool) {
	    return (typ == Wy_Bool);
	};
	if (tok == my_type_byte) {
	    return (typ == Wy_Byte);
	};
	if (tok == my_type_char) {
	    return (typ == Wy_Char);
	};
	if (tok == my_type_string) {
	    return ((typ == Wy_String) || (typ == Wy_CString));
	};
	WY_PANIC("Help needed in wycc_type_comp leaf (%d)\n", tok)
    };
    if (flgs & Type_List) {
	return (typ == Wy_List);
    };
    if (flgs & Type_Set) {
	return (typ == Wy_Set);
    };
    if (flgs & Type_Map) {
	return (typ == Wy_Map);
    };
    if (flgs & Type_Record) {
	return (typ == Wy_Record);
    };
    if (flgs & Type_Frac) {
	return (typ == Wy_Record);
    };
    if (flgs & Type_Tuple) {
	return (typ == Wy_Tuple);
    };
    if (tok == my_type_null) {
	return (typ == Wy_Null);
    }
    if (flgs & Type_Recurs) {
	alt = wycc_type_down(tok);
	return wycc_type_comp(typ, alt) ;
    }; 
    if (flgs & Type_Union) {
	while (1) {
	    alt = wycc_type_down(tok);
	    if (wycc_type_comp(typ, alt)) {
		return 1;
	    };
	    tok = wycc_type_next(tok);
	    if (tok == 0) {
		return 0;
	    };
	}
    };
    //WY_SEG_FAULT
    WY_PANIC("Help needed in wycc_type_comp other (%d)\n", tok)
    fprintf(stderr, "Help needed in wycc_type_comp other (%d)\n", tok);
    struct type_desc *desc;
    desc = &((struct type_desc *)type_parsings)[tok];
    fprintf(stderr, "\t tok type (%d)\n", desc->flgs);
    exit(-3);

}

/*
 * Checking if an item matches a union type is
 * checking if the item matches any subtype.
 */
static wycc_type_check_union(wycc_obj* itm, int tok, int force){
    int alt;

    while (1) {
	alt = wycc_type_down(tok);
	if (wycc_type_check_tok(itm, alt, force)) {
	    return 1;
	};
	tok = wycc_type_next(tok);
	if (tok == 0) {
	    return 0;
	};
    }
}

/*
 * Checking if an item matches a negate type is
 * checking that the item does not match the subtype.
 */
static wycc_type_check_negate(wycc_obj* itm, int tok, int force){
    int alt;
    int end;

    alt = wycc_type_down(tok);
    end = wycc_type_check_tok(itm, alt, force);
    if (end == 0) {
	return 1;
    };
    return 0;
}

/*
 * given a pair of type tokens, return a pointer to the history record.
 * record contains:
 * 0 for not seen yet.
 * 1 seen but not decided
 * 2 known success.
 * 3 known failure.
 */
#define WY_TSTP_INC 1024
struct wy_tstp_obj {
    struct wy_tstp_obj *nxt;
    int itok;
    int atok;
    int past;
};
static struct wy_tstp_obj * wy_tstp_pool = NULL;
static struct wy_tstp_obj * wy_tstp_list = (struct wy_tstp_obj *) NULL;
static struct wy_tstp_obj* wycc_tstp_next() {
    struct wy_tstp_obj * ans;
    size_t inc = sizeof(struct wy_tstp_obj);
    int idx;

    while (1) {
	ans = wy_tstp_pool;
	if (ans != (struct wy_tstp_obj *) NULL) {
	    wy_tstp_pool = (void *) ans->nxt;
	    return ans;
	};
	ans = calloc(WY_TSTP_INC, inc);
	if (ans == NULL) {
	    WY_PANIC("ERROR: calloc failed\n");
	};
	wy_tstp_pool = ans;
	//for (idx =0, sz =0; idx < (WY_TSTP_INC - 1) ; idx++, sz += inc) {
	//    *(ans + sz) = (void *) (ans + sz + inc);
	for (idx =0; idx < (WY_TSTP_INC - 1) ; idx++) {
	    ans[idx].nxt = &(ans[idx+1]);
        }
    }
}

static int *wycc_type_subsume_tokens_past(int itok, int atok) {
    struct wy_tstp_obj* obj;

    obj = (struct wy_tstp_obj *) &(wy_tstp_list);
    while (obj->nxt != (struct wy_tstp_obj *) NULL) {
	obj = obj->nxt;
	if (obj->itok != itok) {
	    continue;
	}
	if (obj->atok != atok) {
	    continue;
	}
	return &(obj->past);
    }
    obj->nxt = wycc_tstp_next();
    obj = obj->nxt;
    obj->nxt = (struct wy_tstp_obj *) NULL;
    obj->itok = itok;
    obj->atok = atok;
    obj->past = 0;
    return &(obj->past);
}

/*
 * given a type token from an instance and another type token (maybe abstract)
 * return 1 iff the instance is subsumed by the other.
 * check for unions and ...
 */
static int wycc_type_subsume_tokens(int itok, int atok) {
    int iflg, aflg;
    int nxt;
    int alt;
    int end;
    int * past;

    //    if (wycc_type_is_union(itok)) {
	//WY_PANIC("Help needed in wycc_type_subsume instance union (%d)\n", itok)
    //	fprintf(stderr, ".... type_subsume has union\n");
    //};
    if (itok == atok) {
	return 1;
    };
    past = wycc_type_subsume_tokens_past(itok, atok);
    if (*past == 3) {
	return 0;
    } else if (*past == 2) {
	return 1;
    } else if (*past == 1) {
	*past = 2;
	return 1;
    } else if (*past == 0) {
	*past = 1;
    } else {
	WY_PANIC("Help: bad value for past in subsume_tokens (%d)\n", *past)
    }
    iflg = wycc_type_flags(itok);
    aflg = wycc_type_flags(atok);
    if (iflg == aflg) {
	if (wycc_type_is_tuple(itok)) {
	    end = wycc_type_subsume_tuples(itok, atok);
	    if (end) {
		*past = 2;
	    } else {
		*past = 3;
	    };
	    return end;
	};
	if (wycc_type_is_record(itok)) {
	    end = wycc_type_subsume_records(itok, atok);
	    if (end) {
		*past = 2;
	    } else {
		*past = 3;
	    };
	    return end;
	};
	if (wycc_type_is_list(itok)) {
	    end = wycc_type_subsume_lists(itok, atok);
	    if (end) {
		*past = 2;
	    } else {
		*past = 3;
	    };
	    return end;
	};
	if (wycc_type_is_chain(itok)) {
	    nxt = wycc_type_down(itok);
	    alt = wycc_type_down(atok);
	    end = wycc_type_subsume_tokens(nxt, alt);
	    if (end == 0) {
		*past = 3;
		return 0;
	    };
	    nxt = wycc_type_next(itok);
	    alt = wycc_type_next(atok);
	    end = wycc_type_subsume_tokens(nxt, alt);
	    if (end) {
		*past = 2;
	    } else {
		*past = 3;
	    };
	    return end;
	};
	if (wycc_type_is_leaf(itok)) {
	    end = wycc_type_subsume_leaves(itok, atok);
	    if (end) {
		*past = 2;
	    } else {
		*past = 3;
	    };
	    return end;
	};
	if (wycc_type_is_recurs(itok)) {
	    nxt = wycc_type_next(itok);
	    alt = wycc_type_next(atok);
	    end = wycc_type_subsume_tokens(nxt, alt);
	    if (end) {
		*past = 2;
	    } else {
		*past = 3;
	    };
	    return end;
	};
	if (wycc_type_is_union(itok)) {
	    for (nxt= itok; nxt > 0; nxt= wycc_type_next(nxt)) {
		alt = wycc_type_down(nxt);
		if (wycc_type_subsume_tokens(alt, atok)) {
		    *past = 2;
		    return 1;
		};
	    }
	};
	WY_PANIC("Help needed in wycc_type_subsume descent (%d)\n", iflg);
    };
    if (wycc_type_is_union(atok)) {
	for (nxt= atok; nxt > 0; nxt= wycc_type_next(nxt)) {
	    alt = wycc_type_down(nxt);
	    if (wycc_type_subsume_tokens(itok, alt)) {
		*past = 2;
		return 1;
	    };
	}
	*past = 3;
	return 0;
    };
    if (wycc_type_is_recurs(atok)) {
	nxt = wycc_type_down(atok);
	end =  wycc_type_subsume_tokens(itok, nxt);
	if (end) {
	    *past = 2;
	} else {
	    *past = 3;
	};
	return end;
    };
    if (wycc_type_is_leaf(itok)) {
	*past = 3;
	return 0;
    };
    if (itok == my_type_void) {
	*past = 2;
	return 1;
    };
    if (wycc_type_is_recurs(itok)) {
	alt = wycc_type_down(itok);
	end = wycc_type_subsume_tokens(alt, atok);
	if (end) {
	    *past = 2;
	} else {
	    *past = 3;
	};
	return end;
    };
    if (wycc_type_is_record(itok)) {
	*past = 3;
	return 0;
    };
    if (wycc_type_is_list(itok)) {
	*past = 3;
	return 0;
    };
    if (wycc_type_is_map(itok)) {
	*past = 3;
	return 0;
    };
    if (wycc_type_is_union(itok)) {
	for (nxt= itok; nxt > 0; nxt= wycc_type_next(nxt)) {
	    alt = wycc_type_down(nxt);
	    if (wycc_type_subsume_tokens(alt, atok)) {
		*past = 2;
		return 1;
	    };
	}
	*past = 3;
	return 0;
    };
     //WY_SEG_FAULT
    WY_PANIC("Help needed in wycc_type_subsume instance unk (%d, %d)\n", itok, iflg)

}

/*
 * 8:6 6:7
 * 3b, 4d, 5c, 6i, 7r,  8s
 */
static int wycc_type_subsume_leaves(int itok, int atok) {
    int istp;
    int astp;

    if (itok == my_type_int) {
	if (atok == my_type_real) {
	    return 1;
	};
    };
    if (itok == my_type_string) {
	if (atok == my_type_int) {
	    return 0;
	};
	return 1;
    };

    WY_PANIC("Help needed in wycc_type_subsume_leaves (%d:%d)\n", itok, atok);

}

/*
 * 
 */
static int wycc_type_subsume_lists(int itok, int atok) {
    int istp;
    int astp;

    istp = wycc_type_down(itok);
    astp = wycc_type_down(atok);
    return wycc_type_subsume_tokens(istp, astp);
}

/*
 * 
 */
static int wycc_type_subsume_tuples(int itok, int atok) {
    int istp;
    int astp;
    int end;

    istp = wycc_type_down(itok);
    astp = wycc_type_down(atok);
    end = wycc_type_subsume_tokens(istp, astp);
    if (end == 0) {
	return 0;
    };
    istp = wycc_type_next(itok);
    astp = wycc_type_next(atok);
    return wycc_type_subsume_tokens(istp, astp);
    
}

/*
 * given two type tokens (record types), compute equivalence
 * This would be a simple comparison of the tokens except that
 * the second one can be an abstract (recursive) type.
 */
static int wycc_type_subsume_records(int itok, int atok) {
    wycc_obj *inam;
    wycc_obj *anam;
    int end;
    int itup;
    int atup;

    /* ***** lots of work needed here */
    if (itok == atok) {
	return 1;
    };
    inam = wycc_type_record_names(itok);
    anam = wycc_type_record_names(atok);
    end = wycc_comp_list(inam, anam);
    if (end != 0) {
	return 0;
    };
    itup = wycc_type_next(itok);
    atup = wycc_type_next(atok);
    if (itup == atup) {
	return 1;
    };
    return wycc_type_subsume_tokens(itup, atup);
    //WY_SEG_FAULT
    //	return ;
}

/*
 * return 1 iff the given item (wycc_obj) matches the given type token
 */
static int wycc_type_check_tok(wycc_obj* itm, int tok, int force){
    void **p = (void **) itm->ptr;
    void **pn;
    int ty = itm->typ;
    int flgs = wycc_type_flags(tok);
    int alt;
    int alta;
    int tmp;
    long max;
    int at;
    struct chunk_ptr col_chunk_ptr;
    struct chunk_ptr *cptr = &col_chunk_ptr;
    wycc_obj* key;
    wycc_obj* val;
    struct type_desc *desc;
    char * vu;

    if (tok == my_type_any) {
	return 1;
    };
    if (wycc_type_is_recurs(tok)) {
	alt = wycc_type_down(tok);
	return wycc_type_check_tok(itm, alt, force);
    };
    if (wycc_type_is_union(tok)) {
	return wycc_type_check_union(itm, tok, force);
    };
    if (wycc_type_is_negate(tok)) {
	return wycc_type_check_negate(itm, tok, force);
    };
    if (wycc_type_is_recurs(tok)) {
	tmp = wycc_type_down(tok);
	return wycc_type_check_tok(itm, tmp, force);
    };
    tmp = wycc_type_comp(ty, tok);
    if (! tmp) {
	if (force) {
	    tmp = wycc_type_comp_loss(ty, tok, force);
	    if (! tmp) {
		return 0;
	    };
	} else {
	    return 0;
	};
    };
    if (wycc_type_is_leaf(tok)) {
	return 1;
    };
    if (wycc_type_is_odd(tok)) {
	return 1;
    };
    alt = wycc_type_down(tok);
    if (wycc_debug_flag) {
	fprintf(stderr, "Debug in wycc_type_check_tok w/ tok %d, %d\n"
		, tok, flgs);
	if ((flgs & Type_Record) == 0) {
	    tmp = wycc_type_flags(alt);
	    fprintf(stderr, "Debug in wycc_type_check_tok w/ alt %d, %d\n"
		, alt, tmp);
	};
    };
    if (wycc_type_is_tuple(tok)){
	for (alt= 0; alt < (int)(p[0]) ; alt++) {
	    val = (wycc_obj *)p[3+alt];
	    desc = &((struct type_desc *)type_parsings)[tok];
	    tmp = wycc_type_check_tok(val, desc->down, force);
	    if (!tmp) {
		return 0;
	    };
	    tok = desc->next;
	    if (tok == 0) {
		return 1;
	    };
	}
	return 1;
    };
    if (wycc_type_is_record(tok)) {
	val = (wycc_obj *) p[1];
	tmp = (int) val->ptr;
	//return (tmp == tok);
	return wycc_type_subsume_records(tmp, tok);
	WY_PANIC("Help needed in wycc_type_check_tok w/ record\n")
    };
    if (wycc_type_is_frac(tok)) {
	val = (wycc_obj *) wycc_type_down(tok);
	pn = (void **) val->ptr;
	max = (long) pn[0];

	alt = wycc_type_next(tok);
	for (at= 0; at < max ; at++) {
	    if (alt <= 0) {
		break;
	    };
	    key = (wycc_obj *) pn[3+at];
	    vu = (char *) key->ptr;
	    alta = wycc_type_down(alt);
	    if (wycc_debug_flag) {
		fprintf(stderr, "\tneed to match %d (%s)", at, vu);
		fprintf(stderr, "\tagainst %d\n", alta);
	    };
	    val = wycc_record_get_nam(itm, vu);
	    tmp = wycc_type_check_tok(val, alta, force);
	    if (!tmp) {
		return 0;
	    };
	    alt = wycc_type_next(alt);
	}
	return 1;

    };
    if (wycc_type_is_list(tok)){
	if ((int) p[0] <= 0) {
	    return 1;	/* an empty list matches any type of list */
	};
	ty = (int) p[1];
	if (ty != Wy_Any) {
	    tmp = wycc_type_comp(ty, alt);
	    if (! tmp) {
		return  0;
	    };
	};
	wycc_chunk_ptr_fill_as(cptr, itm);
	while (1) {
	    wycc_chunk_ptr_inc(cptr);
	    key = cptr->key;
	    val = cptr->val;
	    if ((key == NULL) && (val == NULL)) {
		return 1;
	    };
	    //if (key != NULL) {
	    //	tmp = wycc_type_check_tok(key, alt);
	    //	if (! tmp) {
	    //	    return 0;
	    //	};
	    //};
	    if (val != NULL) {
		tmp = wycc_type_check_tok(val, alt, force);
		if (! tmp) {
		    return 0;
		};
	    };
	}
	/*
	 * need to check individual list members.
	 */
	//fprintf(stderr, "Help needed in wycc_type_check_tok w/ list\n");
	//exit(-3);
	
    };
    if (wycc_type_is_iter(tok)){
	if (itm->typ == Wy_Map) {
	    alta = wycc_type_next(tok);
	} else {
	    alta  = alt;
	}
	wycc_chunk_ptr_fill_as(cptr, itm);
	while (1) {
	    wycc_chunk_ptr_inc(cptr);
	    key = cptr->key;
	    val = cptr->val;
	    if ((key == NULL) && (val == NULL)) {
		return 1;
	    };
	    if (key != NULL) {
		tmp = wycc_type_check_tok(key, alt, force);
		if (! tmp) {
		    return 0;
		};
	    };
	    if (val != NULL) {
		tmp = wycc_type_check_tok(val, alta, force);
		if (! tmp) {
		    return 0;
		};
	    };
	}
	//return wycc_type_check_tok_iter(
	WY_PANIC("Help needed in wycc_type_check_tok w/ iter %d\n", tok)
    };
	fprintf(stderr, "Help needed in wycc_type_check_tok w/ unk %d\n", tok);
    desc = &((struct type_desc *)type_parsings)[tok];
    fprintf(stderr, "\t tok type (%d)\n", desc->flgs);
}

/*
 * given an object and a string,
 * return 1 if the string describes the type of the object,
 * else return 0;
 */
int wycc_type_check(wycc_obj* itm, char* typ){
    WY_OBJ_SANE(itm, "wycc_type_check");
    int tok;

    // fprintf(stderr, "DEBUG wycc_type_check for type %s\n", typ);
    // fprintf(stderr, "DEBUG wycc_type_check given type %d\n", itm->typ);

    tok = wycc_type_internal(typ);
    return wycc_type_check_tok(itm, tok, 0);
}

/*
 *
 */
int wycc_exception_check() {
    return (exception_thrown != NULL);
}

/*
 *
 */
int wycc_exception_try(char *str) {
    return wycc_type_check(exception_thrown, str);
}

/*
 *
 */
wycc_obj* wycc_exception_get() {
    return exception_thrown;
}

void  wycc_exception_clear() {
    exception_thrown = NULL;
}

/*
 * given a a comma (with or without space) separated list of names
 * return a list (obj) of string objects.
 */
wycc_obj* wycc_record_list_names(const char *nams) {
    wycc_obj *ans;
    wycc_obj *nam;
    char * buf;
    size_t siz;
    int sav;
    int len;
    int cnt;		/* count of names == 1 + separators */
    int brk;
    char chr;

    cnt = 1;
    for (brk= 0; ; brk++) {
	chr = nams[brk];
	if (chr == '\0') {
	    break;
	};
	if (chr == ',') {
	    cnt++;
	}
    }
    len = brk;
    ans = wycc_list_new(cnt);
    for (sav= 0, brk= 0; sav < len; brk++) {
	chr = nams[brk];
	if ((chr >= 'a') && (chr <= 'z')) {
	    continue;
	};
	if ((chr >= 'A') && (chr <= 'Z')) {
	    continue;
	};
	if ((chr >= '0') && (chr <= '9')) {
	    continue;
	};
	if (sav < brk) {
	    siz = brk - sav;
	    buf = (char *) malloc(siz + 2);
	    strncpy(buf, &(nams[sav]), siz);
	    buf[siz] = '\0';
	    if (wycc_debug_flag) {
		fprintf(stderr, "named = '%s'\n", buf);
	    };
	    nam = wycc_box_str(buf);
	    wycc_list_add(ans, nam);
	};
	sav = brk+1;
    }
    return ans;
}

/*
 * given a dense type string, return a rcd_rcd wycc_obj
 */
wycc_obj* wycc_record_type(const char *txt) {
    int tok;

    tok = wycc_type_internal(txt);
    if (wycc_type_is_recurs(tok)) {
	tok = wycc_type_down(tok);
    };
    if (! wycc_type_is_record(tok)) {
	WY_PANIC("Help needed in wycc_record_type for '%s'\n", txt)
    };
    return wycc_box_meta(tok);
}

#if 0
/*
 * given a pair of lists, combine them into a record_record
 */
wycc_obj* wycc_record_record(wycc_obj* nam, wycc_obj* typ) {
    WY_OBJ_SANE(nam, "wycc_record_record nam");
    WY_OBJ_SANE(typ, "wycc_record_record typ");
    //wycc_obj* ans;
    void** p;

    if (nam->typ != Wy_List) {
	WY_PANIC("Help needed in wycc_record_record for type %d\n", nam->typ)
    };
    if (typ->typ != Wy_List) {
	WY_PANIC("Help needed in wycc_record_record for type %d\n", typ->typ)
    };
    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Rcd_Rcd;
    p = (void**) calloc(2, sizeof(void *));
    p[0] = (void *) nam;
    p[1] = (void *) typ;
    //ans->ptr = (void *) p;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_Rcd_Rcd, (void*) p);
}
#endif

/*
 * given a record and an offset, return the contents of that field.
 */
wycc_obj* wycc_record_get_dr(wycc_obj* rec, long osv) {
    WY_OBJ_SANE(rec, "wycc_record_get_dr");
    long at, tmp;
    wycc_obj* ans;
    void** p = rec->ptr;

    if ((rec->typ != Wy_RRecord) && (rec->typ != Wy_Record)) {
	WY_PANIC("Help needed in wycc_record_get_dr for type %d\n", rec->typ)
    };
    if (osv < 0) {
	WY_PANIC("Help! wycc_record_get_dr offset %d\n", osv)
    };
    at = (long) p[0];
    if (at < osv) {
	WY_PANIC("Help! wycc_record_get_dr offset:size %d:%d\n", osv, at)
    };
    ans = (wycc_obj*) p[3+osv];
    if (ans == (wycc_obj *) NULL) {
	return ans;
    };
    ans->cnt++;
    return ans;
}

static wycc_obj * wycc_type_record_names(int tok){
    struct type_desc *desc;

    return (wycc_obj *) wycc_type_down(tok);
}

/*
 * given a record_record and a field name, return the field number.
 */
int wycc_recrec_nam(wycc_obj* rec, char *nam) {
    WY_OBJ_SANE(rec, "wycc_recrec_nam");
    //void** p = (void **) rec->ptr;
    void** p;
    wycc_obj *nxt;
    wycc_obj *nams;
    char *str;
    int cnt;
    int ans = 0;

    if (rec->typ != Wy_Rcd_Rcd) {
	WY_PANIC("Help needed in wycc_recrec_nam for type %d\n", rec->typ)
    };
    nams = wycc_type_record_names((int) rec->ptr);
    if (nams->typ != Wy_List) {
	WY_PANIC("Help needed in wycc_recrec_nam, bad names %d\n", nams->typ)
    };
    cnt = wycc_length_of_list(nams);
    p = (void **) nams->ptr;
    for ( ; ans < cnt ; ans++) {
	nxt = (wycc_obj *) p[3+ans];
	if ((nxt->typ != Wy_String) && (nxt->typ != Wy_CString)) {
	    WY_PANIC("Help needed in wycc_recrec_nam for names %d @%d\n"
		    , nxt->typ, ans)
	};

	str = (char *) nxt->ptr;
	if (strcmp(str, nam) == 0) {
	    return ans;
	};
    }
    WY_PANIC("Help needed in wycc_recrec_nam, name '%s' not found.\n", nam)
    fprintf(stderr, "Help needed in wycc_recrec_nam, name '%s' not found.\n"
	    , nam);
    WY_SEG_FAULT
    exit(-3);
    return 0;
}

/*
 * given a record and field name, return the contents of that field.
 */
wycc_obj* wycc_record_get_nam(wycc_obj* rec, char *nam) {
    WY_OBJ_SANE(rec, "wycc_record_get_nam");
    long at;
    wycc_obj* meta;
    void** p = rec->ptr;

    if (rec->typ != Wy_Record) {
	WY_PANIC("Help needed in wycc_record_get_nam for type %d\n", rec->typ)
    };
    meta = (wycc_obj *) p[1];
    at = wycc_recrec_nam(meta, nam);
    return wycc_record_get_dr(rec, at);
}

/*
 * given a record, a field name, and an object,
 * fill in the named slot with the object.
 */
wycc_obj* wycc_record_put_nam(wycc_obj* rec, char *nam, wycc_obj *itm) {
    WY_OBJ_SANE(rec, "wycc_record_put_nam rec");
    WY_OBJ_SANE(itm, "wycc_record_put_nam itm");
    long at;
    wycc_obj* meta;
    void** p = rec->ptr;

    if (rec->typ != Wy_Record) {
	WY_PANIC("Help needed in wycc_record_put_nam for type %d\n", rec->typ)
    };
    meta = (wycc_obj *) p[1];
    at = wycc_recrec_nam(meta, nam);
    wycc_record_fill(rec, at, itm) ;
    return;
}

/*
 * given a record, an offset value and an object,
 * fill the slot of the record with the object.
 */
void wycc_record_fill(wycc_obj* rec, int osv, wycc_obj* itm) {
    WY_OBJ_SANE(rec, "wycc_record_fill rec");
    WY_OBJ_SANE(itm, "wycc_record_fill itm");
    void** p = rec->ptr;
    long at, tmp;

    if ((rec->typ != Wy_RRecord) && (rec->typ != Wy_Record)) {
	WY_PANIC("Help needed in wycc_record_fill for type %d\n", rec->typ)
    };
    if (osv < 0) {
	WY_PANIC("Help! wycc_record_fill offset %d\n", osv)
    };
    at = (long) p[0];
    if (at < osv) {
	WY_PANIC("Help! wycc_record_fill offset:size %d:%d\n", osv, at)
    };
    p[3 + osv] = (void *) itm;
    itm->cnt++;
    return;
}
//    wycc_record_fill(sys, 1, itm);

/*
 * given a count, setup a raw record:
 * just like list object big enough to accept them.
 */
wycc_obj* wycc_rrecord_new(long siz) {
    //wycc_obj* ans;
    long tmp;
    void** p;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_RRecord;
    tmp = siz+3;
    p = (void**) calloc(tmp, sizeof(void *));
    p[0] = (void *) siz;
    p[1] = (void *) -1;		/* because it is raw */
    //ans->ptr = (void *) p;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_RRecord, (void*) p);
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
	WY_PANIC("Help needed in wycc_record_new for type %d\n", meta->typ)
    };
    ans = wycc_type_record_names((int) meta->ptr);
    if (ans->typ != Wy_List) {
	WY_PANIC("Help needed in wycc_record_new, bad name list %d\n", ans->typ)
    };
    siz = wycc_length_of_list(ans);
    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Record;
    
    tmp = siz+3;
    p = (void**) calloc(tmp, sizeof(void *));
    p[0] = (void *) siz;
    p[1] = (void *) meta;
    meta->cnt++;
    //ans->ptr = (void *) p;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_Record, (void*) p);
}

static void wycc_tuple_add_odd(wycc_obj* lst, wycc_obj* key, wycc_obj* val, int cnt) {
    wycc_update_list(lst, val, cnt);
}


/*
 * given a count, setup a tuple object big enough to accept them.
 * cheat, use list code.
 */
wycc_obj* wycc_tuple_new(long siz) {
    wycc_obj* ans;
    void** p;

    ans = wycc_list_new(siz);
    ans->typ = Wy_Tuple;
    p = (void **) ans->ptr;
    p[0] = (void *) siz; 
    return ans;
}

/*
 * given a count, setup a list object big enough to accept them.
 * 
 */
wycc_obj* wycc_list_new(long siz) {
    //wycc_obj* ans;
    long tmp;
    //size_t raw;
    void** p;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_List;
    tmp = 2 * wycc_high_bit(siz+3);
    p = (void**) calloc(tmp, sizeof(void *));
    p[0] = (void *) 0;
    p[1] = (void *) Wy_None;
    p[2] = (void *) tmp;

    //ans->ptr = (void *) p;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_List, (void*) p);
}

wycc_obj* wycc_list_get(wycc_obj* lst, long at) {
    WY_OBJ_SANE(lst, "wycc_list_get");
    void** p = lst->ptr;
    wycc_obj *ans;

    if ((lst->typ != Wy_List) && (lst->typ != Wy_Tuple)) {
	WY_PANIC("Help needed in wycc_list_get for type %d\n", lst->typ)
    };
    if (at >= (long) p[0]) {
	return NULL;
    };
    /* **** Does this need to inc the ref count */
    //return (wycc_obj*) p[3+at];
    ans = (wycc_obj*) p[3+at];
    ans->cnt++;
    return ans;
}

/*
 * provide a common interface for maps, sets, and lists.
 */
static void wycc_list_add_odd(wycc_obj* lst, wycc_obj* key, wycc_obj* val, int cnt) {
    if (val != NULL) {
	return wycc_list_add(lst, val);
    }
    return wycc_list_add(lst, key);
}

/*
 * add an item to a list
 */
void wycc_list_add(wycc_obj* lst, wycc_obj* itm) {
    WY_OBJ_SANE(lst, "wycc_list_add lst");
    WY_OBJ_SANE(itm, "wycc_list_add itm");
    void** p = lst->ptr;
    long at, tmp, typ;
    size_t raw;

    if (lst->typ != Wy_List) {
	WY_PANIC("Help needed in wycc_list_add for type %d\n", lst->typ)
    };
    if (itm == NULL) {
	return;
    }
    typ = (long) p[1];
    if (typ == Wy_None) {
	typ = itm->typ;
	p[1] = (void *) typ;
    } else if (typ == Wy_Any) {
    } else if (typ != itm->typ) {
	//WY_PANIC("Help needed in wycc_list_add for multi-types \n")
	p[1] = (void *) Wy_Any;
    };
    tmp = (long) p[2];
    at = ((long) p[0]) +1;
    p[0] = (void *) at;
    if ((at+2) >= tmp) {
	tmp *= 2;
        raw = tmp * sizeof(void *);
	p = (void **) realloc(p, raw);
	if (p == NULL) {
	    fprintf(stderr, "ERROR: realloc failed\n");
	    exit(-4);
	};
	p[2] = (void *) tmp;
	lst->ptr = p;
    };
    p[2 + at] = (void *) itm;
    itm->cnt++;
    return;
}

/*
 * given a simple type, setup an empty set object
 * 
 */
wycc_obj* wycc_set_new(int typ) {
    //wycc_obj* ans;
    long tmp;
    void** p;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Set;
    tmp = WYCC_SET_CHUNK + 2;
    p = (void**) calloc(tmp, sizeof(void *));
    p[1] = (void *) typ;
    p[0] = (void *) 0;
    //ans->ptr = (void *) p;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_Set, (void*) p);
}

wycc_obj* wycc_iter_new(wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc_iter_new");
    void** p = itm->ptr;
    void** chunk;
    //wycc_obj* ans;
    struct chunk_ptr *ptr;
    int cnt;
    int flg;

    flg = -1;
    if (itm->typ == Wy_List) {
	flg = 2;
    };
    if (itm->typ == Wy_Tuple) {
	flg = 2;
    };
    if (itm->typ == Wy_Set) {
	flg = 0;
    };
    if (itm->typ == Wy_Map) {
	flg = 1;
    };
    if ((itm->typ == Wy_String) || (itm->typ == Wy_CString)) {
	flg = 4;
    };
    if (flg == -1) {
	WY_PANIC("Help needed in wycc_iter_new for type %d\n", itm->typ)
    };
    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Iter;
    //ans->cnt = 1;
    ptr = (struct chunk_ptr *) calloc(1, sizeof(struct chunk_ptr));
    cnt = (int) p[0];
    ptr->cnt = cnt;
    ptr->p = p;
    if (flg == 4) {
	chunk = p;
    } else if (flg == 0) {
	chunk = &(p[2]);
    } else {
	chunk = &(p[3]);
    }
    ptr->chk = chunk;
    ptr->at = 0;
    ptr->flg = flg;
    //ans->ptr = ptr;
    //return ans;
    return wycc_box_new(Wy_Iter, ptr);

}

/*
 * use the data in   an item to fill in a chunk pointer (structure).
 */
static void wycc_chunk_ptr_fill_as(struct chunk_ptr *ptr, wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc_chunk_ptr_fill_as");
    int typ = itm->typ;

    if (typ == Wy_Set) {
	return wycc_chunk_ptr_fill(ptr, itm, 0);
    };
    if (typ == Wy_String) {
	return wycc_chunk_ptr_fill(ptr, itm, 4);
    };
    if (typ == Wy_CString) {
	return wycc_chunk_ptr_fill(ptr, itm, 4);
    };
    if (typ == Wy_List) {
	return wycc_chunk_ptr_fill(ptr, itm, 2);
    };
    if (typ == Wy_Tuple) {
	return wycc_chunk_ptr_fill(ptr, itm, 2);
    };
    if (typ == Wy_Map) {
	return wycc_chunk_ptr_fill(ptr, itm, 1);
    };
    WY_PANIC("Help needed in wycc_chunk_ptr_fill_as for type %d\n", typ)
}

static void wycc_chunk_ptr_fill(struct chunk_ptr *ptr, wycc_obj *itm, int typ) {
    WY_OBJ_SANE(itm, "wycc_chunk_ptr_fill");
    void** p = itm->ptr;
    void** chunk;
    int cnt;

    cnt = (int) p[0];
    ptr->cnt = cnt;
    ptr->p = p;
    if (typ == 4) {	/* string*/
	chunk = p;
    } else if (typ == 0) {	/* set */
	chunk = &(p[2]);
    } else {	/* list, tuple, map */
	chunk = &(p[3]);
    }
    //    chunk = &(p[2]);
    ptr->chk = chunk;
    ptr->at = 0;
    ptr->flg = typ;
    return;
}

/*
 * advance a pointer structure (into the chunks of sets or maps or lists)
 */
static void wycc_chunk_ptr_inc(struct chunk_ptr *chunk) {
    int brnch;
    int step;
    int max;
    int tmp;
    char cc;
    char *cp;
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
	//chunk->key = (wycc_obj *) NULL;
	chunk->val = itm;
	return;
    } else if (chunk->flg == 4) {
	cp = (char *) chunk->chk;
	tmp = chunk->at++;
	max = strlen(cp);
	if (tmp >= max) {
	    return;
	};
	cc = cp[tmp];
	chunk->val = wycc_box_char(cc);
	return;
    } else {
	WY_PANIC("HELP: bad chunk->typ chunk_ptr_inc (%d)\n", chunk->flg)
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
	    WY_PANIC("HHEELLPP: confused in chunk_ptr_inc; 2 deep\n")
	};
	chunk->chk = dn;
	tmp = 0;
    }
    /*
     * we are now pointing beyond the branches.
     */
    while (1) {
	//if (tmp < (max-1)) {
	if (tmp < (max-step)) {
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
	    WY_PANIC("HHEELLPP: confused in chunk_ptr_inc\n")
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

    ans = (wycc_obj *) NULL;
    if (itm->typ != Wy_Iter) {
	WY_PANIC("Help needed in wycc_iter_next for type %d\n", itm->typ)
    };
    ptr = (struct chunk_ptr *) itm->ptr;
    wycc_chunk_ptr_inc(ptr);
    if ((ptr->flg == 2) || (ptr->flg == 4)) {
	ans = ptr->val;
    } else if (ptr->flg == 0) {
	ans = ptr->key;
    } else if (ptr->flg == 1) {
	if ((ptr->key) && (ptr->val)) {
	    return wycc_box_pair(ptr->key, ptr->val);
	};
    } else {
	WY_PANIC("Help needed in wycc_iter_next for subtype %d\n", ptr->flg)
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
    /* **** need a bunch of code here */
    return;
}

/*
 * provide a common interface for maps, sets, and lists.
 */
static void wycc_set_add_odd(wycc_obj* col, wycc_obj* key, wycc_obj* val, int cnt) {
    if (key != NULL) {
	return wycc_set_add(col, key);
    }
    return wycc_set_add(col, val);
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
	WY_PANIC("Help needed in wycc_set_add for type %d\n", lst->typ)
    };
    /* Wy_None */
    typ = (long) p[1];
    if (typ == Wy_None) {
	typ = itm->typ;
	p[1] = (void *) typ;
    } else if (typ == Wy_Any) {
    } else if (typ != itm->typ) {
	p[1] = (void *) Wy_Any;
    };
    if ((typ < 0) || (typ > Wy_Type_Max)){
	WY_PANIC("Help needed in wycc_set_add for types %d\n", typ)
    };
    /* cnt = (long) p[0]; */
    at = 0;
    chunk = &(p[2]);
    if (((long) p[0]) <1) {
	/* add first member to empty set */
	chunk[0] = (void *) 0;
	chunk[1] = (void *) itm;
	itm->cnt++;
	p[0] = (void *) 1;
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
	    p[0]++;
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
    p[0]++;
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
 * given a set and an item, remove the item from the set (decrement ref cnt)
 */
void wycc_set_del(wycc_obj* lst, wycc_obj* itm) {
    WY_OBJ_SANE(lst, "wycc_set_del lst");
    WY_OBJ_SANE(itm, "wycc_set_del itm");
    void** p = lst->ptr;
    void** chunk;
    void** new;
    void** chg;
    long at, typ, cnt, deep, idx, cp;
    size_t raw;
    wycc_obj* tst;
    int end;

    if (lst->typ != Wy_Set) {
	WY_PANIC("Help needed in wycc_set_del for type %d\n", lst->typ)
    };
    WY_PANIC("Help! wycc_set_del not yet complete\n")
}

/*
 * given a simple type, setup an empty map object
 * 
 */
wycc_obj* wycc_map_new(int typ) {
    //wycc_obj* ans;
    long tmp;
    void** p;

    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_Map;
    tmp = WYCC_MAP_CHUNK + 3;
    p = (void**) calloc(tmp, sizeof(void *));
    p[2] = (void *) Wy_None;
    p[1] = (void *) typ;
    p[0] = (void *) 0;
    //ans->ptr = (void *) p;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_Map, (void*) p);
}

/*
 * put a value in a map at the given key
 */
static void wycc_map_add_odd(wycc_obj* lst, wycc_obj* key, wycc_obj* itm, int cnt) {
    if (key == NULL) {
	key = wycc_box_int(cnt);
    };
    if (itm == NULL) {
	if (map_add_odd == NULL) {
	    map_add_odd = key;
	    return;
	};
	itm = map_add_odd;
	map_add_odd = NULL;
    }
    if (key == NULL) {
	if (map_add_odd == NULL) {
	    map_add_odd = itm;
	    return;
	};
	key = map_add_odd;
	map_add_odd = NULL;
    }
    return wycc_map_add(lst, key, itm);
}

/*
 * put a value in a map at the given key
 */
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
	WY_PANIC("Help needed in wycc_map_add for type %d\n", lst->typ)
    };
    if (key == NULL) {
	WY_PANIC("Help needed in wycc_map_add for NULL key\n")
    };
    if (itm == NULL) {
	WY_PANIC("Help needed in wycc_map_add for NULL value\n")
    };
    typ = (long) p[1];
    if (typ == Wy_None) {
	typ = key->typ;
	p[1] = (void *) typ;
    } else if (typ == Wy_Any) {
    } else if (typ != key->typ) {
	p[1] = (void *) Wy_Any;
    };
    if ((typ < 0) || (typ > Wy_Type_Max)){
	WY_PANIC("Help needed in wycc_map_add for types %d\n", typ)
    };
    typ = (long) p[2];
    if (typ == Wy_None) {
	typ = itm->typ;
	p[2] = (void *) typ;
    } else if (typ == Wy_Any) {
    } else if (typ != itm->typ) {
	p[2] = (void *) Wy_Any;
    };
    if ((typ < 0) || (typ > Wy_Type_Max)){
	WY_PANIC("Help needed in wycc_map_add for types %d\n", typ)
    };
    at = 0;
    chunk = &(p[3]);
    if (((long) p[0]) <1) {
	/* add first member to empty map */
	chunk[0] = (void *) 0;
	chunk[1] = (void *) itm;
	chunk[2] = (void *) key;
	itm->cnt++;
	key->cnt++;
	p[0] = (void *) 1;
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
	    itm->cnt++;
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
	    p[0]++;
	    wycc_chunk_rebal(0, p, chunk, at, deep);
	    return;
	}
	// end = compar(key, tst);
	end = wycc_comp_gen(key, tst);
	if (end == 0) {
	    /* key match == done ; swap the value stored */
	    tst = (wycc_obj *) chunk[at -1];
	    itm->cnt++;
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
    p[0]++;
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
	    for (; cp < (WYCC_MAP_CHUNK - 2); cp++) {
		chunk[cp++] = (void *) NULL;
	    };
	    new[WYCC_MAP_CHUNK-1] = chunk;
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
    for (idx= (WYCC_MAP_CHUNK - 2); idx > (at-1) ; idx--) {
	chunk[idx] = chunk[idx - 2];
    }
    chunk[at--] = key;
    chunk[at]   = itm;
    /**** rebalance tree */
    return;
}

/*
 * given a map and a key return the val if map contains key;
 * else return NULL.
 */
wycc_obj* wycc_index_of_map(wycc_obj* map, wycc_obj* key){
    WY_OBJ_SANE(map, "wycc_index_of_map map");
    WY_OBJ_SANE(key, "wycc_index_of_map key");
    void** p = map->ptr;
    void** chunk;
    wycc_obj* ans;
    wycc_obj* tst;
    long at, typ, cnt;
    // int (*compar)(wycc_obj* lhs, wycc_obj* rhs);
    int end;

    typ = (long) p[1];
    if (typ == Wy_None) {
	fprintf(stderr, "ERROR: IndexOf for empty map \n");
	exit(-4);
    };
    typ = (long) p[2];
    if (typ == Wy_None) {
	fprintf(stderr, "ERROR: IndexOf for empty map.\n");
	exit(-4);
    };
    at = 0;
    chunk = &(p[3]);
    if (((long) p[0]) <1) {
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
	if (end > 0) {
	    continue;
	};
	/* the item in question goes before here; ergo down a chunk */
	chunk = chunk[(3*at) - 2];
	at = 0;
	//deep++;

    };
    at *= 3;		/* 3 ::= sizeof branch value key triplet */
    while (at < (WYCC_MAP_CHUNK -4)) {
	at += 2;
	tst = (wycc_obj *) chunk[at];
	if (tst == (wycc_obj *) NULL) {
	    //fprintf(stderr, "ERROR: key not found IndexOf map \n");
	    //exit(-4);
	    return (wycc_obj *) NULL;
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
    return NULL;
}

/*
 * given a map and an item, remove the item from the map (decrement ref cnt)
 */
void wycc_map_del(wycc_obj* lst, wycc_obj* itm) {
    WY_OBJ_SANE(lst, "wycc_map_del lst");
    WY_OBJ_SANE(itm, "wycc_map_del itm");
    void** p = lst->ptr;
    void** chunk;
    void** new;
    void** chg;
    long at, typ, cnt, deep, idx, cp;
    size_t raw;
    wycc_obj* tst;
    int end;

    if (lst->typ != Wy_Map) {
	WY_PANIC("Help needed in wycc_map_del for type %d\n", lst->typ)
    };
    WY_PANIC("Help! wycc_map_del not yet complete\n")
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
	WY_PANIC("FAILURE: ref count went negative (%d:%d@%p)\n"
		, itm->cnt, itm->typ, (void *) itm)
    }
    ptr = itm->ptr;
    typ = itm->typ;
    if (wycc_debug_flag) {
	fprintf(stderr, "note: deallocing box for typ %d\n", typ);
    };
    //    free(itm);
    wycc_obj_free(itm);
    if (typ == Wy_None) {
	fprintf(stderr, "note: deallocing box for typ %d\n", typ);
	return (wycc_obj *) NULL;
    };
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
    if (typ == Wy_Null) {
	return (wycc_obj *) NULL;
    };
    if (typ == Wy_Token) {
	return (wycc_obj *) NULL;
    };
    if (typ == Wy_Addr) {
	return (wycc_obj *) NULL;
    };
    if (typ == Wy_Byte) {
	return (wycc_obj *) NULL;
    };
    if (typ == Wy_Ref1) {
	wycc_deref_box((wycc_obj *) ptr);
	return (wycc_obj *) NULL;
    };
    if (typ == Wy_Ref2) {
	wycc_deref_box((wycc_obj *) ptr);
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
    if (typ == Wy_Float) {
        free(ptr);
	return;
    };
    if ((typ == Wy_List) || (typ == Wy_Tuple)) {
	siz = (long) p[0];
	for (idx= 0; idx < siz; idx++) {
	    itm = (wycc_obj*) p[3+ idx]; 
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
	void** chunk = &(p[3]);
	wycc_dealloc_map_chunk(chunk);
	free(ptr);
	return;
    }
    if (typ == Wy_Record) {
	itm = (wycc_obj *) p[1];
	if (itm->cnt > 1) {
	    itm->cnt--;		/* never attempt to reclaim the meta record. */
	};
	siz = (long) p[0];
	for (idx= 0; idx < siz; idx++) {
	    itm = (wycc_obj*) p[3+ idx]; 
	    wycc_deref_box(itm);
	}
        free(ptr);
	return;
    }
    if (typ == Wy_WInt) {
	wycc_wint_free(ptr);
	return;
    }
    WY_PANIC("ERROR: unrecognized type (%d) in dealloc\n", typ)
}

/*
 * given a wycc box type (Wy_*),
 * if the type is an alias for String or Int return Wy_String or Wy_Int
 * else return arg unchanged
 */
static int wycc_type_dealias(int typ) {
    
    if (typ == Wy_CString) {
	return Wy_String;
    };
    if (typ == Wy_WString) {
	return Wy_String;
    };
    if (typ == Wy_Char) {
	return Wy_Int;
    };
    if (typ == Wy_Bool) {
	return Wy_Int;
    };
    if (typ == Wy_Byte) {
	return Wy_Int;
    };
    if (typ == Wy_WInt) {
	return Wy_Int;
    };
    return typ;
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

    lt = wycc_type_dealias(lt);
    rt = wycc_type_dealias(rt);
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
    if (lt == Wy_Set) {
	return wycc_comp_set(lhs, rhs);
    };
    if (lt == Wy_Record) {
	return wycc_comp_record(lhs, rhs);
    };
    if (lt == Wy_List) {
	return wycc_comp_list(lhs, rhs);
    };
    if (lt == Wy_Tuple) {
	return wycc_comp_list(lhs, rhs);
    };
    if (lt == Wy_Float) {
	return wycc_comp_float(lhs, rhs);
    };
    if (lt == Wy_Null) {
	return 0;
    };
    WY_PANIC("Help needed in wycc_comp_gen for type %d\n", lt)
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
	WY_PANIC("Help needed in wycc_comp_str for type %d\n", lhs->typ)
    };
    if ((rhs->typ != Wy_String) && (rhs->typ != Wy_CString)) {
	WY_PANIC("Help needed in wycc_comp_str for type %d\n", rhs->typ)
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

static int wycc_comp_array(wycc_obj** lhs, wycc_obj** rhs, int cnt){
    wycc_obj *litm;
    wycc_obj *ritm;
    int idx;
    int end;

    for (idx = 0; idx < cnt ; idx ++) {
	litm = lhs[idx];
	ritm = rhs[idx];
	end = wycc_comp_gen(litm, ritm);
	if (end != 0) {
	    return end;
	};
    }
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

    idx = (int) lblk[0];
    cnt = (int) rblk[0];
    if (idx < cnt) {
	return -1;
    };
    if (cnt < idx) {
	return 1;
    };
    return wycc_comp_array((wycc_obj**)&(lblk[3]), (wycc_obj**)&(rblk[3]), cnt);
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
    int lcnt = (int) lblk[0];
    int rcnt = (int) rblk[0];
    int end;
    int cnt;

    if (lcnt < rcnt) {
	return -1;
    };
    if (rcnt < lcnt) {
	return 1;
    };
    end = wycc_comp_rmeta((wycc_obj*) lblk[1], (wycc_obj*) rblk[1]);
    if (end != 0) {
	return end;
    };
    cnt = lcnt;
    return wycc_comp_array((wycc_obj**)&(lblk[3]), (wycc_obj**)&(rblk[3]), cnt);
    //return wycc_comp_array(&(lblk[3]), &(rblk[3]), lcnt);
}

/*
 * a not quite simple comparison of two records
 * a record with fewer fields is always smaller
 * a 
 */
static int wycc_comp_rmeta(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_record lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_record rhs");
    int ltok = (int) lhs->ptr;
    int rtok = (int) rhs->ptr;
    int end;

    if (lhs->typ != Wy_Rcd_Rcd) {
	WY_PANIC("FAIL wycc_comp_meta called for type %d\n", lhs->typ)
    }
    if (rhs->typ != Wy_Rcd_Rcd) {
	WY_PANIC("FAIL wycc_comp_meta called for type %d\n", rhs->typ)
    }
    if (ltok == rtok) {
	return 0;
    };
    if (ltok < rtok) {
	return -1;
    }
    return 1;
}

#if 0
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
#endif

int wycc_length_of_list(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_length_of_list");
    void** p = itm->ptr;

    if ((itm->typ != Wy_List) && (itm->typ != Wy_Tuple)) {
	WY_PANIC("Help length_of_list called for type %d\n", itm->typ)
    }
    return (int) p[0];
}

int wycc_length_of_set(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_length_of_set");
    void** p = itm->ptr;
    int rslt;

    if (itm->typ != Wy_Set) {
	WY_PANIC("Help length_of_set called for type %d\n", itm->typ)
    };
    if (((long) p[1]) == Wy_None) {
	return 0;
    };
    return (int) p[0];
}

int wycc_length_of_map(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_length_of_map");
    void** p = itm->ptr;
    int rslt;

    if (itm->typ != Wy_Map) {
	WY_PANIC("Help length_of_map called for type %d\n", itm->typ)
    };
    if (((long) p[1]) == Wy_None) {
	return 0;
    };
    if (((long) p[2]) == Wy_None) {
	return 0;
    };
    return (int) p[0];
}

int wycc_length_of_string(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_length_of_string");
    char* p = (char *) itm->ptr;
    int rslt;

    if ((itm->typ != Wy_String) && (itm->typ != Wy_CString)) {
	WY_PANIC("Help length_of_string called for type %d\n", itm->typ)
    };
    rslt = strlen(p);
    return rslt;
}

/*
 * Given an object that needs to updated, return an "unshared" version
 */
wycc_obj* wycc_cow_obj(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_cow_gen");

    if (itm->typ == Wy_CString) {
	/* Constant strings must always be cow'ed */
	return wycc_cow_string(itm);
    };
    if (itm->cnt == 1) {
	return itm;
    };
    if (itm->typ == Wy_String) {
	return wycc_cow_string(itm);
    };
    if (itm->typ == Wy_List) {
	return wycc_cow_list(itm);
    };
    if (itm->typ == Wy_Tuple) {
	return wycc_cow_list(itm);
    };
    if (itm->typ == Wy_Record) {
	return wycc_cow_record(itm);
    };
    if (itm->typ == Wy_Map) {
	return wycc_cow_map(itm);
    };
    if (itm->typ == Wy_Ref1) {
	itm->cnt++;
	return itm;		/* we really do not clone refs. */
    };
    WY_PANIC("Fail: wycc_cow_obj not yet supports type(%d).\n", itm->typ)
}

/*
 * Clone a map
 * **** needs to be written.
 */
wycc_obj* wycc_cow_map(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_cow_map");
    WY_PANIC("Fail: wycc_cow_map not written yet.\n")
}

/*
 * Clone a record
 */
wycc_obj* wycc_cow_record(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc_cow_record");
    //wycc_obj* ans;
    wycc_obj* rcdrcd;
    wycc_obj* nxt;
    void** p = itm->ptr;
    long at, tmp;
    void** new;

    if (itm->typ != Wy_Record) {
	WY_PANIC("Help needed in wycc_cow_record for type %d\n", itm->typ)
    };
    rcdrcd = (wycc_obj*) p[1];
    nxt = wycc_type_record_names((int) rcdrcd->ptr);
    tmp = 3 + wycc_length_of_list(nxt);
    new = (void**) calloc(tmp, sizeof(void *));
    rcdrcd->cnt++;

    new[1] = (void *) rcdrcd;
    for (at= 3; at < tmp ; at++) {
	nxt = (wycc_obj*) p[at];
	nxt->cnt++;
	new[at] = (void *) nxt;
    }
    new[0] = (void *) (tmp-3);
    return wycc_box_new(itm->typ, (void*) new);
}

/*
 * some operation needs to change an object that is currently being shared
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

    if ((lst->typ != Wy_List) && (lst->typ != Wy_Tuple)) {
	WY_PANIC("Help needed in wycc_cow_list for type %d\n", lst->typ)
    };
    tmp = (long) p[2];
    new = (void**) calloc(tmp, sizeof(void *));
    new[1] = p[1];
    new[2] = (void *) tmp;
    tmp = (long) p[0];
    for (at= 0; at < tmp ; at++) {
	nxt = (wycc_obj*) p[3+at];
	nxt->cnt++;
	new[3+at] = (void *) nxt;
    }
    new[0] = (void *) tmp;
    //ans->ptr = (void *) new;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_List, (void*) new);
}

/*
 * sublist is just like a defective cow_list
 */
wycc_obj* wycc_list_slice(wycc_obj* lst, int lo, int hi) {
    WY_OBJ_SANE(lst, "wycc_list_slice");
    //wycc_obj* ans;
    wycc_obj* nxt;
    void** p = lst->ptr;
    long at, tmp;
    void** new;

    if (lst->typ != Wy_List) {
	WY_PANIC("Help needed in wycc_list_slice for type %d\n", lst->typ)
    };
    if (lo > hi) {
	WY_PANIC("ERROR in wycc_list_slice %d>=%d\n", lo, hi)
    };
    at = (long) p[0];
    tmp = (hi - lo);
    if (at < tmp) {
	at -= tmp;
	WY_PANIC("ERROR wycc_list_slice too big by %d\n", at)
    };
    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_List;
    tmp += 3;
    new = (void**) calloc(tmp, sizeof(void *));
    new[1] = p[1];
    new[2] = (void *) tmp;
    //tmp = (long) p[0];
    for (at= 0; lo < hi ; lo++,at++) {
	nxt = (wycc_obj*) p[3+lo];
	nxt->cnt++;
	new[3+at] = (void *) nxt;
    }
    new[0] = (void *) at;
    //ans->ptr = (void *) new;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_List, (void*) new);
}

/*
 * if rhs contains lhs return 1;
 * else return 0
 */
static int wycc_compare_member_of(wycc_obj* itm, wycc_obj* rhs) {
    WY_OBJ_SANE(itm, "wycc_compare_member_of itm");
    WY_OBJ_SANE(rhs, "wycc_compare_member_of rhs");
    void** p = rhs->ptr;
    struct chunk_ptr rhs_chunk_ptr;
    struct chunk_ptr *rptr = & rhs_chunk_ptr;
    wycc_obj* nxt;
    int flg;
    int end;

    flg = -1;
    if (rhs->typ == Wy_List) {
	flg = 2;
    };
    if (rhs->typ == Wy_Map) {
	flg = 1;
    };
    if (rhs->typ == Wy_Set) {
	flg = 0;
    };
    if (flg == -1) {
	WY_PANIC("Help needed in wycc_compare_member_of for type %d\n"
		, rhs->typ)
    };
    wycc_chunk_ptr_fill(rptr, rhs, flg);
    
    wycc_chunk_ptr_inc(rptr);
    while (1) {
	if (flg == 2) {
	    nxt = rptr->val;
	} else {
	    nxt = rptr->key;
	};
	if (nxt == NULL) {
	    return 0;
	};
	end = wycc_comp_gen(nxt, itm);
	if (end == 0) {
	    return 1;
	};
	wycc_chunk_ptr_inc(rptr);
    };
}

/*
 * if lhs is a subset of rhs, return 1;
 * if lhs == rhs and flg, return 1;
 * else return 0
 */
static int wycc_compare_subset(wycc_obj* lhs, wycc_obj* rhs, int flg) {
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
	WY_PANIC("Help needed in wycc_compare_subset for type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wycc_compare_subset for type %d\n", rhs->typ)
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
    WY_PANIC("Failure: wycc_compare_subset\n")
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
	WY_PANIC("Failure: wycc_compare with relation %d\n", rel)
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

/*
 * given a list, produce a set.
 */
static wycc_obj *wycc_set_from_list(wycc_obj *lst) {
    WY_OBJ_SANE(lst, "wycc_set_from_list");
    void** p = lst->ptr;
    wycc_obj *ans;
    wycc_obj *itm;
    long idx;

    ans = wycc_set_new(-1);
    for (idx= 0; idx < (long) p[0] ; idx++) {
	itm = (wycc_obj *) p[3 + idx];
	//itm->cnt++;
	wycc_set_add(ans, itm);
    }
    return ans;
}

/*
 * given a ref1 update the underlying object.
 * **** needs to be written
 */
void wycc_update_ref(wycc_obj *ref, wycc_obj *itm) {
    WY_OBJ_SANE(ref, "wycc_update_list ref");
    WY_OBJ_SANE(itm, "wycc_update_list itm");
    wycc_obj* nxt;

    if (ref->typ != Wy_Ref1) {
	WY_PANIC("ERROR: ref in wycc_update_ref is type %d\n", ref->typ)
    };
    nxt = (wycc_obj *) ref->ptr;
    if (nxt->typ != Wy_Ref2) {
	WY_PANIC("ERROR: nxt in wycc_update_ref is type %d\n", nxt->typ)
    };
    nxt->ptr = (void *) itm;
    return;
	WY_PANIC("Help: wycc_update_ref unimplemented\n")

}

/*
 * given a list, an item, and an offset, update the slot of the list at
 * that offset using that item.
 */
wycc_obj* wycc_update_list(wycc_obj* lst, wycc_obj* rhs, long idx){
    WY_OBJ_SANE(lst, "wycc_update_list lst");
    WY_OBJ_SANE(rhs, "wycc_update_list rhs");
    void** p = lst->ptr;
    wycc_obj* itm;
    long lsiz, typ;

    if ((lst->typ != Wy_List) && (lst->typ != Wy_Tuple)) {
	WY_PANIC("ERROR: list in wycc_update_list is type %d\n", lst->typ)
    };
    lsiz = wycc_length_of_list(lst);
    if ((idx < 0) || (idx >= lsiz)) {
	WY_PANIC(
		"ERROR: out of bounds offset value in wycc_update_list (%d,%d)\n"
		, idx, lsiz)
    };
    //if (lst->cnt > 1) {
    //lst = wycc_cow_list(lst);
    //p = lst->ptr;
    //};
    typ = (long) p[1];
    if (typ == Wy_None) {
	typ = rhs->typ;
	p[1] = (void *) typ;
    } else if (typ == Wy_Any) {
    } else if (typ != rhs->typ) {
	p[1] = (void *) Wy_Any;
    };
    itm = p[3+idx];
    p[3+idx] = rhs;
    rhs->cnt++;
    wycc_deref_box(itm);
    return lst;
}

/*
 * given a type number,
 */
static int wycc_type_down(int id) {
    struct type_desc *desc;

    if (id >= type_count) {
	WY_PANIC("Help needed in wycc_type_down w/ %d\n", id)
    };
    desc = &((struct type_desc *)type_parsings)[id];
    if (desc->flgs & Type_Leaf) {
	WY_PANIC("Help needed in wycc_type_down w/ %d\n", id)
    };
    if (desc->flgs & Type_Odd) {
	WY_PANIC("Help needed in wycc_type_down w/ %d\n", id)
    };
    return desc->down;
}

/*
 * given a type number,
 */
static int wycc_type_next(int id) {
    struct type_desc *desc;

    if (id >= type_count) {
	WY_PANIC("Help needed in wycc_type_next w/ %d\n", id)
    };
    desc = &((struct type_desc *)type_parsings)[id];
    if (desc->flgs & Type_Leaf) {
	WY_PANIC("Help needed in wycc_type_next w/ %d\n", id)
    };
    if (desc->flgs & Type_Odd) {
	WY_PANIC("Help needed in wycc_type_next w/ %d\n", id)
    };
    return desc->next;
}

/*
 * given a type number,
 */
static int wycc_type_is_leaf(int id) {
    struct type_desc *desc;

    if (id >= type_count) {
	WY_PANIC("Help needed in wycc_type_is_leaf w/ %d\n", id)
    };
    desc = &((struct type_desc *)type_parsings)[id];
    if (desc->flgs & Type_Leaf) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_odd(int id) {
    struct type_desc *desc;

    if (id >= type_count) {
	WY_PANIC("Help needed in wycc_type_is_odd w/ %d\n", id)
    };
    desc = &((struct type_desc *)type_parsings)[id];
    if (desc->flgs & Type_Odd) {
	return 1;
    };
    return 0;
}

/*
 * given a type token, return a count of the children types.
 */
int wycc_type_child_count(int tok) {
    struct type_desc *desc;

    if (tok == 0) {
	return 0;
    };
    desc = &((struct type_desc *)type_parsings)[tok];
    return 1 + wycc_type_child_count(desc->next);

}
static int wycc_type_flags(int id) {
    struct type_desc *desc;

    if (id >= type_count) {
	WY_PANIC("Help needed: bad id in wycc_type_flags (%d)\n", id)
    };
    desc = &((struct type_desc *)type_parsings)[id];
    return desc->flgs;
}

/*
 * given a type number,
 */
static int wycc_type_is_list(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_List) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_ref(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Reference) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_map(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Map) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_record(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Record) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_chain(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Chain) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_frac(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Frac) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_recurs(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Recurs) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_union(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Union) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_negate(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Negate) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_tuple(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_Tuple) {
	return 1;
    };
    return 0;
}

/*
 * given a type number,
 */
static int wycc_type_is_iter(int id) {
    int flgs = wycc_type_flags(id);

    if (flgs & Type_List) {
	return 1;
    };
    if (flgs & Type_Set) {
	return 1;
    };
    if (flgs & Type_Map) {
	return 1;
    };
    if (flgs & Type_Tuple) {
	return 1;
    };
    /*
     * String is actually iterable,  *****
     */
    return 0;
}

/*
 * give a type string being parsed, return idex of the first non-letter
 * uses hardcoded ascii interpretations; could use ctype.h & isalpha()
 */
static int wycc_type_word(const char *nam, int lo, int hi){
    int at = lo;
    char chr;

    while (at < hi) {
	chr = nam[at];
	if ((chr <'A') || (chr > 'z')) {
	    return at;
	};
	if ((chr >'Z') && (chr < 'a')) {
	    return at;
	};
	at++;
    }
    return at;
}

static int wycc_type_parse_leaf(const char *nam, int *lo, int hi){
    int ans;
    struct type_desc *desc;
    char chr, chr2;
    int brk;

    brk = *lo;
    chr = nam[brk++];
    if ((*lo == 0) && (brk >= hi)) {
	/*
	 * the full type text is one word, and it must an addition
	 */
	//ans = type_count++;
	ans = wycc_type_tok_alloc();
	desc = &((struct type_desc *)type_parsings)[ans];
	/*
	 * CHEAT:  We know that the first three to get here
	 * are the only types that match this description.
	 */
#if Save_Name
	desc->nam = nam;
#endif
	desc->down = 0;
	desc->next = 0;
	if (ans < 3) {
	    desc->flgs = Type_Odd;
	    return ans;
	};
	/*
	 * lessor cheat: any later arrivals that are one word are leaves.
	 */
	if (wycc_debug_flag) {
	    fprintf(stderr, "... wycc_type_parse_leaf %s\n", nam);
	};
	desc->flgs = Type_Leaf;
	return ans;
    }
    if ((brk < hi) && (nam[brk] != ',')) {
	chr2 = nam[brk];
	WY_PANIC("Help needed in wycc_type_parse_leaf w/ %d:%d %c %c\n"
		, *lo, hi, chr, chr2)
    };
    *lo = brk;
    if (chr == 'i') {
	return my_type_int;
    };
    if (chr == 'c') {
	return my_type_char;
    };
    if (chr == 'b') {
	return my_type_bool;
    };
    if (chr == 'd') {
	return my_type_byte;
    };
    if (chr == 'r') {
	return my_type_real;
    };
    if (chr == 's') {
	return my_type_string;
    };
    if (chr == 'v') {
	return my_type_void;
    };
    if (chr == 'n') {
	return my_type_null;
    };
    /* help! need to do a lookup on chr*/
    WY_PANIC("Help needed in wycc_type_parse lookup '%c'\n", chr)
}

static int wycc_type_tok_alloc() {
    size_t siz;

    if (type_count >= type_alloc) {
	type_alloc += Type_Parsings_Alloc;
	siz = type_alloc * sizeof(struct type_desc);
	type_parsings = realloc(type_parsings, siz);
	if (type_parsings == NULL) {
	    fprintf(stderr, "realloc failed in wycc_type_tok_alloc\n");
	    exit(-2);
	};
    };
    return type_count++;
}

/*
 * count characters to the next closure (
 */
static size_t wycc_type_parse_la(const char *nam, int brk, int hi){
    size_t ans = 0;
    int lvl = 0;
    char chr;

    for ( ; brk < hi ; brk++, ans++) {
	chr = nam[brk];
	if (chr == '[') {
	    lvl++;
	} else if ((chr == ',') && (lvl == 0)) {
	    return ans;
	} else if (chr == ']') {
	    lvl--;
	    if (lvl <= 0) {
		return ans + 1;
	    };
	}
    }
    return ans;
}

/*
 * look at the name of the recursive type, and produce an index
 */
static int wycc_parse_recurser_idx(const char *nam, int beg, int siz){
    int ans;
    char chr = nam[beg];
    int at;
    size_t sizt;
    int cnt = strlen(Recursive_Type_Letters) -1;

    if (siz <= 1) {
	ans = 0;
    } else {
	ans = strtol(&(nam[beg]), NULL, 10);
	ans *= cnt;
    };
    if (Recursive_Type_Token_Max < (ans+cnt)) {
	Recursive_Type_Token_Max = (ans+cnt);
	sizt = Recursive_Type_Token_Max * sizeof(int);
	Recursive_Type_Tokens = (int *) realloc(Recursive_Type_Tokens, sizt);
	for (at= 0; at < cnt ; at++) {
	    Recursive_Type_Tokens[ans + at] = -1;
	};
    };
    for (at= 0; at < cnt ; at++) {
	if (chr == Recursive_Type_Letters[at]) {
	    return ans + at;
	};
    };
    return -1;
    WY_PANIC("Help needed in wycc_parse_recurser w/ %c\n", chr)
}

/*
 *
 */
static int wycc_type_parse_fx(const char *nam, int at, size_t siz) {
    int tmp;
    char *buf;

    tmp = wycc_parse_recurser_idx(nam, at, siz);
    if (tmp != -1) {
	return Recursive_Type_Tokens[tmp];
    };
    buf = (char *) malloc(siz+2);
    strncpy(buf, &(nam[at]), siz);
    buf[siz] = '\0';
    if (wycc_debug_flag) {
	fprintf(stderr, "FX new = '%s'\n", buf);
    };
    return wycc_type_internal(buf);
}

/*
 * given a type nam, find the right number for it.
 */
static int wycc_type_parse(const char *nam, int *lo, int hi){
    int ans;
    int brk;
    int tmp;
    int sav;		/* the end of the current compound */
    int sav2;		/* the sep (,) after the first subtype */
    int sav3;		/* the start of the seq. of subtypes */
    int nxt;
    int nxt2;
    int flg;
    int cnt;
    int cnt2;
    int strt, prev, link;
    struct type_desc *desc;
    struct type_desc *back;
    char chr, chr2;
    char *buf;
    wycc_obj *itm;
    wycc_obj *lst;
    size_t siz;
    int mt;

    if (*lo > hi) {
	WY_PANIC("Help needed in wycc_type_parse w/ %d:%d\n", *lo, hi)
    }
    brk = *lo;
    chr = nam[brk++];
    if (wycc_debug_flag) {
	fprintf(stderr, ".. in wycc_type_parse with %s\n", nam);
    }
    if (chr != '[') {
	return wycc_type_parse_leaf(nam, lo, hi);
    };
    chr = nam[1+ *lo];
    tmp = 0;
    sav = -1;
    sav2 = -1;
    /* a name list follows only for records, fracs, and recursers */
    if (chr == '{') {
	sav3 = -1;
    } else if (chr == '%') {
	sav3 = -1;
    } else if (chr == '<') {
	sav3 = -1;
    } else {
	sav3 = 1 + *lo;
    }
    cnt = 0;
    cnt2 = 0;
    for (brk= *lo; brk < hi ;  brk++) {
	chr = nam[brk];
	if (chr == '[') {
	    tmp ++;
	} else if (chr == ']') {
	    tmp--;
	} else if (chr == '}') {
	    if (sav3 < 0) {
		sav3 = brk;
		sav2 = -1;
	    };
	} else if (chr == ',') {
	    if (tmp == 1) {
		cnt2++;
		if (sav2 < 0) {
		    sav2 = brk;
		};
	    };
	};
	if (tmp == 0) {
	    cnt++;
	    if (sav == -1) {
		sav = brk;
	    };
	};
    }
    if (tmp != 0) {
	WY_PANIC("Help: wycc_type_parse type backets unmatch (%d)\n", tmp)
    };
    if (sav != (hi -1)) {
	WY_PANIC("Help: wycc_type_parse type ungrouped (%d, %d)\n", sav , hi)
    };
    brk = 1 + *lo ;
    chr = nam[brk++];

    if (wycc_debug_flag) {
	fprintf(stderr, "Help needed in wycc_type_parse foo %s\n", nam);
	fprintf(stderr, "%d %d ; %d %d %d ; %d %d\n"
		,*lo, hi, sav, sav2, sav3, cnt, cnt2);
    };
    if (chr == '<') {
	if (sav3 < 0) {
	    WY_PANIC("Help: wycc_type_parse recurser name unclosed.\n")
	};
	//siz = sav3 - (2 + *lo);
	siz = sav3 - (brk);
	if (siz != 1) {
	    WY_PANIC("Help: wycc_type_parse long recurser name.\n")
	};
	tmp = wycc_parse_recurser_idx(nam, brk, siz);
	if (tmp == -1) {
	    WY_PANIC("Help: wycc_type_parse bad recursive name.\n")
	};

	if (wycc_debug_flag) {
	    // fprintf(stderr, "recurser name = '%c'\n", nam[2+*lo]);
	    fprintf(stderr, "recurser name = '%c', %d\n", nam[brk], tmp);
	};
	ans = wycc_type_tok_alloc();
	Recursive_Type_Tokens[tmp] = ans;
	sav3++;
	if (sav2 < 0) {
	    siz = sav - sav3;
	} else {
	    siz = sav2 - sav3;
	};
	buf = (char *) malloc(siz+2);
	strncpy(buf, &(nam[sav3]), siz);
	buf[siz] = '\0';
	if (wycc_debug_flag) {
	    fprintf(stderr, "recur = '%s'\n", buf);
	};
	nxt = wycc_type_internal(buf);

	desc = &((struct type_desc *)type_parsings)[ans];
#if Save_Name
	desc->nam = nam;
#endif
	desc->flgs = Type_Recurs;
	desc->down = nxt;
	desc->next = 0;
	return ans;
	
    };
    if ((chr == '{') || (chr == '%')) {
	if (sav3 < 0) {
	    WY_PANIC("Help: wycc_type_parse record names unclosed.\n")
	};
	siz = sav3 - (2 + *lo);
	buf = (char *) malloc(siz+2);
	strncpy(buf, &(nam[2+*lo]), siz);
	buf[siz] = '\0';
	if (wycc_debug_flag) {
	    fprintf(stderr, "names = '%s'\n", buf);
	};
	lst = wycc_record_list_names(buf);
    };
    if (sav3 < 0) {
	sav3 = 1 + *lo;
    };
    sav3++;
    if (wycc_debug_flag) {
	fprintf(stderr, "peek = '%c'\n", nam[sav3]);
    };
    if (nam[sav3] == '+') {
	mt = 1;
	sav3++;
    } else {
	mt = 0;
    }
    if (sav2 < 0) {
	siz = sav - sav3;
    } else {
	siz = sav2 - sav3;
    };
    nxt = wycc_type_parse_fx(nam, sav3, siz);

    flg = -1;
    if (chr == '*') {
	/* set */
	flg = Type_Set;
    } else if (chr == '#') {
	/* list */
	flg = Type_List;
    } else if (chr == '.') {
	/* reference */
	flg = Type_Reference;
    } else if (chr == '!') {
	/* negation */
	flg = Type_Negate;
    };
    if (flg != -1) {
	if ((sav + 1) < hi) {
	    WY_PANIC("Help: wycc_type_parse type extra child (%d, %d)\n"
		    , sav, hi)
	};
	ans = wycc_type_tok_alloc();
	desc = &((struct type_desc *)type_parsings)[ans];
#if Save_Name
	desc->nam = nam;
#endif
	desc->flgs = flg;
	desc->down = nxt;
	desc->next = 0;
	return ans;
    }
    if ((sav2 < 0) && (chr != '{') && (chr != '%')) {
	WY_PANIC("Help needed in wycc_type_parse short children %s\n", nam)
    } else if (sav2 < 0) {
	siz = sav3 - 3;
	if (wycc_debug_flag) {
	    fprintf(stderr, "Help needed in wycc_type_parse names %d %d\n"
		    , siz, nxt);
	};
	buf = (char *) malloc(siz+2);
	strncpy(buf, &(nam[2]), siz);
	buf[siz] = '\0';
	if (wycc_debug_flag) {
	    fprintf(stderr, "name = '%s'\n", buf);
	};
	lst = wycc_list_new(1);
	itm = wycc_box_str(buf);
	wycc_list_add(lst, itm);
	
	strt = wycc_type_tok_alloc();
	desc = &((struct type_desc *)type_parsings)[strt];
#if Save_Name
	desc->nam = buf;
#endif
	desc->flgs = Type_Tuple;
	desc->down = nxt;
	desc->next = 0;
	ans = wycc_type_tok_alloc();
	desc = &((struct type_desc *)type_parsings)[ans];
#if Save_Name
	desc->nam = buf;
#endif
	if (chr == '{') {
	    desc->flgs = Type_Record;
	} else {
	    desc->flgs = Type_Frac;
	};
	desc->down = (int) lst;
	desc->next = strt;
	return ans;
    };
    sav3 += 1 + siz;
    siz = wycc_type_parse_la(nam, sav3, sav);
    //siz = sav - sav3;
    nxt2 = wycc_type_parse_fx(nam, sav3, siz);

    if (chr == '@') {
	/* map */
	//WY_PANIC("Help needed in wycc_type_parse w/ map %s\n", nam);
	//WY_PANIC("%d %d %d %d %s\n",*lo, hi, sav, nxt, buf);
	ans = wycc_type_tok_alloc();
	desc = &((struct type_desc *)type_parsings)[ans];
#if Save_Name
	desc->nam = nam;
#endif
	desc->flgs = Type_Map;
	desc->down = nxt;
	desc->next = nxt2;
	return ans;

    };
    if (chr == '|') {
	flg = Type_Union;
    } else if (chr == '=') {
	flg = Type_Tuple;
    } else if (chr == '{') {
	flg = Type_Record;
    } else if (chr == '%') {
	/* frac == an open record is a fraction of a record */
	flg = Type_Frac;
    } else if (chr == ':') {
	flg = Type_Method;
    } else if (chr == '^') {
	flg = Type_Funct;
    } else {
	WY_PANIC("Help needed in wycc_type_parse w/ %s\n", nam)
    }
    /*
     * need to step thru the list of children and chain them together.
     * %%
     */
    strt = wycc_type_tok_alloc();
    desc = &((struct type_desc *)type_parsings)[strt];
#if Save_Name
    desc->nam = buf;
#endif
    desc->flgs = Type_Chain;
    desc->down = nxt2;
    desc->next = 0;
    prev = strt;

    for (brk = sav3+siz+1 ; brk < hi-1; brk+=siz){
	if (nam[brk] == ',') {
	    siz = 1;
	    continue;
	};
	siz = wycc_type_parse_la(nam, brk, sav);
	if (siz <= 0) {
	    siz = 1;
	    continue;
	}
	nxt2 = wycc_type_parse_fx(nam, brk, siz);
	link = wycc_type_tok_alloc();
	/* We must recompute desc because type_parsings may change */
	desc = &((struct type_desc *)type_parsings)[prev];
	desc->next = link;
	desc = &((struct type_desc *)type_parsings)[link];
#if Save_Name
	desc->nam = buf;
#endif
	desc->flgs = Type_Chain;
	desc->down = nxt2;
	desc->next = 0;
	prev = link;

    };
    ans = wycc_type_tok_alloc();
    desc = &((struct type_desc *)type_parsings)[ans];
#if Save_Name
    desc->nam = buf;
#endif
    desc->flgs = flg;
    desc->down = nxt;
    desc->next = strt;
    if ((flg != Type_Record)  && (flg != Type_Frac)) {
	return ans;
    };
    /*
     * clean up for this special case
     */
    desc->flgs = Type_Tuple;
    nxt = ans;
    ans = wycc_type_tok_alloc();
    desc = &((struct type_desc *)type_parsings)[ans];
#if Save_Name
    desc->nam = buf;
#endif
    desc->flgs = flg;
    desc->down = (int) lst;
    desc->next = nxt;
    return ans;
}

/*
 * given a character string of dense type description, internalise it
 * and return an integer token
 */
int wycc_type_internal(const char *nam){
    wycc_obj *key;
    wycc_obj *val;
    int ans;
    int len;
    int at;

    /*
     * nam may be in code or heap, but is never to be freed
     */
    key = wycc_box_cstr(nam);
    if (wycc_compare_member_of(key, type_dict)) {
	val = wyil_index_of_map(type_dict, key);
	ans = (int) val->ptr;
	val->cnt--;
    } else {
	//ans = type_count++;
	len = strlen(nam);
	at = 0;
	ans = wycc_type_parse(nam, &at, len);
	val = wycc_box_int(ans);
	wycc_map_add(type_dict, key, val);
	val->cnt--;
    }
    wycc_deref_box(key);
    return ans;
}

/*
 * ******************************
 * wyil opcode implementations
 * ******************************
 */

/*
 * given an object, return an object of type list (Wy_List)
 * **** needs rework to handle impedence matching between lists and maps.
 */
static wycc_obj* wyil_convert_iter(wycc_obj* col, int tok, int force){
    struct chunk_ptr col_chunk_ptr;
    struct chunk_ptr *cptr = &col_chunk_ptr;
    funct_fill *filler;
    wycc_obj* ans;
    wycc_obj* key;
    wycc_obj* val;
    int alt;
    int alta = -1;
    int tmp;
    int cnt;
    int flgs = wycc_type_flags(tok);

    wycc_chunk_ptr_fill_as(cptr, col);

    /* **** may need key<->val swap **** */
    
    //if (col->typ == Wy_Set) {
    if (flgs & Type_Set) {
	ans = wycc_set_new(-1);
	filler = wycc_set_add_odd;
    } else if (flgs & Type_List) {
	//tmp = wyil_length_of(col);
	//ans = wycc_list_new(tmp);
	ans = wycc_list_new(cptr->cnt);
	filler = wycc_list_add_odd;
    } else if (flgs & Type_Map) {
	ans = wycc_map_new(cptr->cnt);
	alta = wycc_type_next(tok);
	filler = wycc_map_add_odd;
    } else if (flgs & Type_Tuple) {
	ans = wycc_tuple_new(cptr->cnt);
	filler = wycc_tuple_add_odd;
    } else {
	WY_PANIC("Help needed in wyil_convert_iter for type %d\n", flgs)
    };
    alt = wycc_type_down(tok);
    wycc_chunk_ptr_inc(cptr);
    for (cnt= 0; 1 ; cnt++) {
	key = cptr->key;
	val = cptr->val;
	if ((key == NULL) && (val == NULL)) {
	    return ans;
	};
	if (key != NULL) {
	    key = wyil_convert_tok(key, alt, force);
	};
	if (val != NULL) {
	    if (alta == -1) {
		val = wyil_convert_tok(val, alt, force);
	    } else {
		val = wyil_convert_tok(val, alta, force);
	    };
	};
	filler(ans, key, val, cnt);
	wycc_chunk_ptr_inc(cptr);
    }
}

/*
 * given an object, return an object of type list (Wy_List)
 */
static wycc_obj* wyil_convert_list_str(wycc_obj* lst, int tok, int force){
    char* p = (char *) lst->ptr;
    //wycc_obj* ans;
    wycc_obj* nxt;
    long max;
    int alt;
    long at, tmp;
    void** new;
    int typ;
    int val;

    alt = wycc_type_down(tok);
    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_List;
    tmp = strlen(p);
    if (alt == my_type_any) {
	typ = my_type_char;
    } else if (alt == my_type_char) {
	typ = Wy_Char;
    } else if (alt == my_type_int) {
	typ = Wy_Int;
    } else if (alt == my_type_bool) {
	typ = Wy_Bool;
    } else if (alt == my_type_byte) {
	typ = Wy_Byte;
    } else {
	WY_PANIC("Help needed in wyil_convert_list_str w/ %d\n", alt)
    }
    new = (void**) calloc(tmp+3, sizeof(void *));
    //    new[1] = p[1];
    new[1] = (void *) typ;
    new[2] = (void *) tmp;
    for (at= 0; at < tmp ; at++) {
	val =  p[at];
	if (typ == Wy_Char) {
	    nxt = wycc_box_char(val);
	} else if (typ == Wy_Int) {
	    nxt = wycc_box_int(val);
	} else if (typ == Wy_Bool) {
	    nxt = wycc_box_bool(val);
	} else if (typ == Wy_Byte) {
	    nxt = wycc_box_byte(val);
	} else {
	    nxt = NULL;
	}
	//new[3+at] = (void *) wyil_convert_tok(nxt, alt);
	new[3+at] = nxt;
    }
    new[0] = (void *) tmp;
    //ans->ptr = (void *) new;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_List, (void*) new);
}

/*
 * given an object, return an object of type list (Wy_List)
 */
static wycc_obj* wyil_convert_list(wycc_obj* lst, int tok, int force){
    void** p = lst->ptr;
    //wycc_obj* ans;
    wycc_obj* nxt;
    long max;
    int alt;
    long at, tmp;
    void** new;

    if ((lst->typ == Wy_String) || (lst->typ == Wy_CString)) {
	return wyil_convert_list_str(lst, tok, force);
    }
    if (lst->typ != Wy_List) {
	WY_PANIC("Help needed in wyil_convert_list w/ %d\n", lst->typ)
    };
    alt = wycc_type_down(tok);
    //ans = (wycc_obj*) calloc(1, sizeof(wycc_obj));
    //ans->typ = Wy_List;
    tmp = (long) p[2];
    new = (void**) calloc(tmp, sizeof(void *));
    //    new[1] = p[1];
    new[2] = (void *) tmp;
    tmp = (long) p[0];
    for (at= 0; at < tmp ; at++) {
	nxt = (wycc_obj*) p[3+at];
	//nxt->cnt++;
	//new[3+at] = (void *) nxt;
	new[3+at] = (void *) wyil_convert_tok(nxt, alt, force);
    }
    new[0] = (void *) tmp;
    if (tmp >0) {
	nxt = (wycc_obj *)new[3];
	//tmp    = ((wycc_obj *)new[3])->typ;
	new[1] = (void *) (nxt->typ);
    } else {
	new[1] = (void *) Wy_None;
    }
    //ans->ptr = (void *) new;
    //ans->cnt = 1;
    //return ans;
    return wycc_box_new(Wy_List, (void*) new);
}

/*
 * given an object, return an object that satisfies a union type.
 * this means iterating thru the union to select the best target.
 */
static wycc_obj* wyil_convert_negate(wycc_obj* lst, int tok, int force){

    if (wycc_type_check_tok(lst, tok, force)) {
	lst->cnt++;
	return lst;
    }
    fprintf(stderr, "Design failure: wyil_convert_negate \n");
    exit(-5);
}

/*
 * given an object, return an object that satisfies a union type.
 * this means iterating thru the union to select the best target.
 */
static wycc_obj* wyil_convert_union(wycc_obj* lst, int tok, int force){
    int nxt = tok;
    int alt;
    int cnt = 0;
    int leaf;
    int homo;
    wycc_obj *ans;

    for (nxt= tok; nxt > 0; nxt= wycc_type_next(nxt)) {
	alt = wycc_type_down(nxt);
	if (wycc_type_check_tok(lst, alt, force)) {
	    lst->cnt++;
	    return lst;
	}
	cnt++;
    }
    if (wycc_type_isleaf(lst)) {
	for (nxt= tok; nxt > 0; nxt= wycc_type_next(nxt)) {
	    alt = wycc_type_down(nxt);
	    if (wycc_type_is_leaf(alt)) {
		return wyil_convert_tok(lst, alt, force);
	    };
	}
    };
    for (nxt= tok; nxt > 0; nxt= wycc_type_next(nxt)) {
	alt = wycc_type_down(nxt);
	if (wycc_type_check_tok(lst, alt, force+1)) {
	    return wyil_convert_tok(lst, alt, force+1);
	}
    }

    WY_PANIC("Help needed: wyil_convert_union incomplete (1 of %d)\n", cnt)

}

/*
 * given an object, return an object of type record (Wy_Record)
 */
static wycc_obj* wyil_convert_tuple(wycc_obj* lst, int tok, int force){
    void** p = lst->ptr;
    void** pn;
    wycc_obj* ans;
    wycc_obj* nxt;
    wycc_obj* prt;
    wycc_obj* nnam;
    long max;
    int alt;
    long at, tmp;
    void** new;
    char * vu;

    if (lst->typ != Wy_Tuple) {
	WY_PANIC("Help needed in wyil_convert_tuple w/ %d\n", lst->typ)
    };
    max = (int) p[0];
    ans = wycc_tuple_new(max);
    pn = (void **) ans->ptr;

    for (at= 0; at < max ; at++) {
	if (tok <= 0) {
	    break;
	};
	nxt = (wycc_obj *) p[3+at];
	alt = wycc_type_down(tok);
	if (wycc_debug_flag) {
	    fprintf(stderr, "\tneed to fill in %d", at);
	    fprintf(stderr, "\tconverted to %d\n", alt);
	};
	if (nxt != NULL) {
	    prt = wyil_convert_tok(nxt, alt, force);
	} else {
	    prt = nxt;
	};
	//nxt = (wycc_obj *) 
	pn[3+at] = (void *) prt;
	tok = wycc_type_next(tok);
    }
    return ans;
}

/*
 * given an object, return an object of some recursive type.
 * this conversion is bogus, and will crash if not already trivially true.
 */
static wycc_obj* wyil_convert_recurs(wycc_obj* itm, int tok, int force){

    //if (wycc_type_check_tok(itm, tok)) {
	itm->cnt++;
	return itm;
	//};
    WY_PANIC("Help needed in wyil_convert_recurs\n")

}

/*
 * given an object, return an object of type record (Wy_Record)
 */
static wycc_obj* wyil_convert_frac(wycc_obj* lst, int tok, int force){
    void** p = lst->ptr;
    void** pn;
    wycc_obj* ans;
    wycc_obj* nxt;
    wycc_obj* prt;
    wycc_obj* nnam;
    long max;
    int alt;
    long at, tmp;
    void** new;
    char * vu;

    if (lst->typ != Wy_Record) {
	WY_PANIC("Help needed in wyil_convert_frac w/ %d\n", lst->typ)
    };
    nxt = (wycc_obj *) p[1];
    //alt = (int) nxt->ptr;
    //if (alt == tok) {
    //ans = lst;
    //ans->cnt++;
    //return ans;
    //}
    /* need to check the names; done in the loop below. */
    //nxt = wycc_box_meta(tok);
    //    ans = wycc_record_new(nxt);
    if (lst->cnt > 1) {
	ans = wycc_cow_record(lst);
    } else {
	ans = lst;
	lst->cnt++;
    }
    nnam = (wycc_obj *) wycc_type_down(tok);
    pn = (void **) nnam->ptr;
    max = (long) pn[0];

    alt = wycc_type_next(tok);
    for (at= 0; at < max ; at++) {
	if (alt <= 0) {
	    break;
	};
	nxt = (wycc_obj *) pn[3+at];
	vu = (char *) nxt->ptr;
	tmp = wycc_type_down(alt);
	if (wycc_debug_flag) {
	    fprintf(stderr, "\tneed to adjust %d (%s)", at, vu);
	    fprintf(stderr, "\tconverted to %d\n", tmp);
	};
	nxt = wycc_record_get_nam(lst, vu);
	if (nxt != NULL) {
	    prt = wyil_convert_tok(nxt, tmp, force);
	    wycc_deref_box(nxt);
	} else {
	    prt = nxt;
	};
	if (prt != nxt) {
	    wycc_record_put_nam(ans, vu, prt); 
	}
	alt = wycc_type_next(alt);
    }
    return ans;
}

/*
 * given an object, return an object of type record (Wy_Record)
 */
static wycc_obj* wyil_convert_record(wycc_obj* lst, int tok, int force){
    void** p = lst->ptr;
    void** pn;
    wycc_obj* ans;
    wycc_obj* nxt;
    wycc_obj* prt;
    wycc_obj* nnam;
    long max;
    int alt;
    long at, tmp;
    void** new;
    char * vu;

    if (lst->typ != Wy_Record) {
	WY_PANIC("Help needed in wyil_convert_record w/ %d\n", lst->typ)
    };
    nxt = (wycc_obj *) p[1];
    alt = (int) nxt->ptr;
    if (alt == tok) {
	ans = lst;
	ans->cnt++;
	return ans;
    }
    /* need to check the names; done in the loop below. */
    nxt = wycc_box_meta(tok);
    ans = wycc_record_new(nxt);
    nnam = (wycc_obj *) wycc_type_down(tok);
    pn = (void **) nnam->ptr;
    max = (long) pn[0];

    alt = wycc_type_next(tok);
    for (at= 0; at < max ; at++) {
	if (alt <= 0) {
	    break;
	};
	nxt = (wycc_obj *) pn[3+at];
	vu = (char *) nxt->ptr;
	tmp = wycc_type_down(alt);
	if (wycc_debug_flag) {
	    fprintf(stderr, "\tneed to fill in %d (%s)", at, vu);
	    fprintf(stderr, "\tconverted to %d\n", tmp);
	};
	nxt = wycc_record_get_nam(lst, vu);
	if (nxt != NULL) {
	    prt = wyil_convert_tok(nxt, tmp, force);
	    wycc_deref_box(nxt);
	} else {
	    prt = nxt;
	};
	wycc_record_fill(ans, at, prt); 
	alt = wycc_type_next(alt);
    }
    return ans;
}

/*
 * given an object, return an object of type reference (Wy_Ref or Wy_Addr)
 */
static wycc_obj* wyil_convert_ref(wycc_obj* itm, int tok, int force){
    int alt;

    if ((itm->typ == Wy_Ref1) || (itm->typ == Wy_Addr)) {
	alt = wycc_type_down(tok);
	if (alt == my_type_any) {
	    itm->cnt++;
	    return itm;
	};
    };
    WY_PANIC("Help needed in wyil_convert_ref w/ %d\n", itm->typ)
}

/*
 * given an object, return an object of type char (Wy_Char)
 */
static wycc_obj* wyil_convert_char(wycc_obj* itm){
    long val;
    long double flt;

    if (itm->typ == Wy_Char) {
	itm->cnt++;
	return itm;
    };
    if ((itm->typ == Wy_Byte) || (itm->typ == Wy_Int) 
	|| (itm->typ == Wy_Bool)) {
	val = (long) itm->ptr;
	return wycc_box_char(val);
    } ;
    WY_PANIC("Help needed in wyil_convert_char w/ %d\n", itm->typ)
}

/*
 * given an object, return an object of type char (Wy_Char)
 */
static wycc_obj* wyil_convert_bool(wycc_obj* itm){
    long val;
    long double flt;

    if (itm == NULL) {
	return wycc_box_bool(0);
    };
    if (itm->typ == Wy_Bool) {
	itm->cnt++;
	return itm;
    };
    if ((itm->typ == Wy_Byte) || (itm->typ == Wy_Int) 
	|| (itm->typ == Wy_Bool)) {
	val = (long) itm->ptr;
	return wycc_box_bool((val != 0));
    } ;
    if (itm->typ == Wy_Float) {
	flt = *((long double*)itm->ptr);
	return wycc_box_bool((flt != 0.0));
    };
    WY_PANIC("Help needed in wyil_convert_bool w/ %d\n", itm->typ)
}

/*
 * given an object, return an object of type string (Wy_String)
 */
static wycc_obj* wyil_convert_string(wycc_obj* itm){

    if ((itm->typ == Wy_String) || (itm->typ == Wy_CString)) {
	itm->cnt++;
	return itm;
    };
    WY_PANIC("Help needed in wyil_convert_string w/ %d\n", itm->typ)
}

/*
 * convert an item (wycc_obj) to the type indicated by the token
 */
static wycc_obj* wyil_convert_tok(wycc_obj* itm, int tok, int force){
    if (tok == my_type_any) {
	itm->cnt++;
	return itm;
    };
    if (wycc_debug_flag) {
	fprintf(stderr, "wyil_convert_tok %d => %d", itm->typ, tok);
	fprintf(stderr, "   %d\n", wycc_type_flags(tok));
    }
    if (wycc_type_is_leaf(tok)) {
	if (tok == my_type_int) {
	    return wyil_convert_int(itm);
	};
	if (tok == my_type_real) {
	    return wyil_convert_real(itm);
	};
	if (tok == my_type_char) {
	    return wyil_convert_char(itm);
	};
	if (tok == my_type_bool) {
	    return wyil_convert_bool(itm);
	};
	if (tok == my_type_string) {
	    return wyil_convert_string(itm);
	};
	fprintf(stderr, "Help needed in wyil_convert_tok w/ leaf %d\n", tok);
#if Save_Name
    struct type_desc *desc;
    desc = &((struct type_desc *)type_parsings)[tok];
	    fprintf(stderr, "\t %s\n", desc->nam);
#endif
	exit(-3);
    } else if (wycc_type_is_list(tok)){
	return wyil_convert_list(itm, tok, force);
    } else if (wycc_type_is_tuple(tok)){
	return wyil_convert_tuple(itm, tok, force);
    } else if (wycc_type_is_iter(tok)){
	return wyil_convert_iter(itm, tok, force);
    } else if (wycc_type_is_record(tok)){
	return wyil_convert_record(itm, tok, force);
    } else if (wycc_type_is_union(tok)){
	return wyil_convert_union(itm, tok, force);
    } else if (wycc_type_is_negate(tok)){
	return wyil_convert_negate(itm, tok, force);
    } else if (wycc_type_is_frac(tok)){
	return wyil_convert_frac(itm, tok, force);
    } else if (wycc_type_is_ref(tok)){
	return wyil_convert_ref(itm, tok, force);
    } else if (wycc_type_is_recurs(tok)){
	//return wyil_convert_recurs(itm, tok);
	tok = wycc_type_down(tok);
	return wyil_convert_tok(itm, tok, force);
    };
    WY_PANIC("Help needed in wyil_convert_tok w/ %d : %d\n"
	     , tok, wycc_type_flags(tok))
}

wycc_obj* wyil_convert(wycc_obj* itm, char *typ){
    WY_OBJ_SANE(itm, "wyil_convert");
    wycc_obj* ans = itm;
    int tok;

    if (wycc_debug_flag) {
	fprintf(stderr, "wyil_convert %d => %s\n", itm->typ, typ);
    }
    tok = wycc_type_internal(typ);
    if (wycc_debug_flag) {
	fprintf(stderr, "wyil_convert %d\n", tok);
    }
    return wyil_convert_tok(itm, tok, 0);
}

/*
 *
 */
void wyil_throw(wycc_obj* itm){
    WY_OBJ_SANE(itm, "wyil_throw");

    if (exception_monitor == NULL) {
	fprintf(stderr, "Exception thrown with no catch in place\n");
	exit(-4);

    };
    exception_thrown = itm;
    itm->cnt++;
}

/*
 *
 */
char *wyil_catch(char *nam){
    char *ans;

    ans = exception_monitor;
    exception_monitor = nam;
    return ans;
}


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
 * given a byte, return a byte with every bit inverted.
 */
wycc_obj* wyil_invert(wycc_obj* itm){
    WY_OBJ_SANE(itm, "wyil_invert");
    long rslt;

    if (itm->typ != Wy_Byte) {
	WY_PANIC("Help needed in wyil_invert for type %d\n", itm->typ)
    };
    rslt = (long) itm->ptr;
    rslt ^= 255;
    return wycc_box_byte((char) rslt);
}

/*
 * return a string extracted from the given string 
 */
wycc_obj* wyil_substring(wycc_obj* str, wycc_obj* loo, wycc_obj* hio){
    WY_OBJ_SANE(str, "wyil_substrin str");
    WY_OBJ_SANE(loo, "wyil_substrin loo");
    WY_OBJ_SANE(hio, "wyil_substrin hio");
    wycc_obj* ans;
    size_t siz;
    long lo, hi, max;
    char *buf;
    char *sptr;

    if ((str->typ != Wy_String) && (str->typ != Wy_CString)) {
	WY_PANIC("Help needed in wyil_substring for type %d\n", str->typ)
    };
    if (loo->typ != Wy_Int) {
	WY_PANIC("Help needed in wyil_substring for type %d\n", loo->typ)
    };
    if (hio->typ != Wy_Int) {
	WY_PANIC("Help needed in wyil_substring for type %d\n", hio->typ)
    };
    lo = (long) loo->ptr;
    hi = (long) hio->ptr;
    if (lo > hi) {
	WY_PANIC("Help needed in wyil_substring for limits %d:%d\n", lo, hi)
    };
    siz = hi - lo;
    sptr = (char *) str->ptr;
    max = strlen(sptr);
    if (hi > max) {
	WY_PANIC("Help needed in wyil_substring for max %d:%d\n", hi, max)
    };
    if (lo < 0) {
	WY_PANIC("Help needed in wyil_substring for min %d\n", lo)
    };
    buf = (char *) malloc(siz + 3);
    if (siz > 0) {
	strncpy(buf, (sptr+lo), siz);
    }
    buf[siz] = 0;
    return wycc_box_str(buf);
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
	WY_PANIC("Help needed in wyil_set_diff for type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_diff for type %d\n", rhs->typ)
    };
    ans = wycc_set_new(-1);
    /*
     * short circuit things a bit by checking the type of the members
     */
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
    WY_PANIC("Failure: wyil_set_diff\n")
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
	WY_PANIC("Help needed in wyil_set_insect for type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_insect for type %d\n", rhs->typ)
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

    for (at= 0; at < (long) p[0]; at++) {
	itm = (wycc_obj *) p[3+at];
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
    /* **** Tuple? */
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


    /* **** Tuple? */
    if ((lhs->typ == Wy_List) || (rhs->typ == Wy_List)) {
	return wyil_set_union_list(lhs, rhs);
    }
    if (lhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_union for type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_union for type %d\n", rhs->typ)
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
 * return a set that is the union of lhs set and rhs member
 */
wycc_obj* wyil_set_union_odd(wycc_obj* lhs, wycc_obj* itm){
    WY_OBJ_SANE(lhs, "wyil_set_union_odd lhs");
    WY_OBJ_SANE(itm, "wyil_set_union_odd itm");
    wycc_obj *was = lhs;

    if (lhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_union_odd for type %d\n", lhs->typ)
    };
    //if (rhs->typ != Wy_Set) {
    //WY_PANIC("Help needed in wyil_set_union_left for type %d\n"
    //	, rhs->typ);
    //exit(-3);
    //};
    /* **** should put a type check on rhs and the set */
    if (wycc_compare(itm, lhs, Wyil_Relation_Mo)) {
	lhs->cnt++;
	return lhs;
    };
    lhs = wycc_cow_obj(lhs);
    wycc_set_add(lhs, itm);
    if (lhs == was) {
	lhs->cnt++;
    };
    return lhs;
}

/*
 * return a set that is the intersection between lhs and rhs
 */
wycc_obj* wyil_set_insect_odd(wycc_obj* lhs, wycc_obj* itm){
    WY_OBJ_SANE(lhs, "wyil_set_insect_odd lhs");
    WY_OBJ_SANE(itm, "wyil_set_insect_odd itm");
    wycc_obj* ans;

    if (lhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_insect_odd for type %d\n", lhs->typ)
    };
    /* **** should put a type check on rhs and the set */
    ans = wycc_set_new(-1);
    if (wycc_compare(itm, lhs, Wyil_Relation_Mo)) {
	wycc_set_add(ans, itm);
    };
    return ans;
}

/*
 * return a set that is the difference between the lhs and rhs
 */
wycc_obj* wyil_set_diff_odd(wycc_obj* lhs, wycc_obj* itm){
    WY_OBJ_SANE(lhs, "wyil_set_diff_odd lhs");
    WY_OBJ_SANE(itm, "wyil_set_diff_odd itm");
    wycc_obj *was = lhs;

    if (lhs->typ != Wy_Set) {
	WY_PANIC("Help needed in wyil_set_diff_odd for type %d\n", lhs->typ)
    };
    if (wycc_compare(itm, lhs, Wyil_Relation_Mo)) {
	lhs = wycc_cow_obj(lhs);
	wycc_set_del(lhs, itm);
    };
    if (lhs == was) {
	lhs->cnt++;
    };
    return lhs;
    //WY_PANIC("Failure: wyil_set_diff_odd\n")
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
    wycc_obj *was = str;

    if ((str->typ != Wy_String) && (str->typ != Wy_CString)) {
	WY_PANIC("ERROR: string in wyil_update_string is type %d\n", str->typ)
    };
    if (osv->typ != Wy_Int) {
	WY_PANIC("ERROR: offset  value in wyil_update_string is type %d\n"
		, osv->typ)
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
	//} else if (str->cnt > 1) {
	//swp = str;
	//str = wycc_cow_string(swp);
	//wycc_deref_box(swp);
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
    if (str == was) {
	str->cnt++;
    };
    tmp = (int) rhs->ptr;
    txt = str->ptr;
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

    if (osv->typ != Wy_Int) {
	WY_PANIC("ERROR: offset  value in wyil_update_list is type %d\n"
		, osv->typ)
    };
    return wycc_update_list(lst, rhs, (long) osv->ptr);
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
	WY_PANIC("ERROR: lhs in wyil_list_comb is type %d\n", lhs->typ)
    };
    if (rhs->typ != Wy_List) {
	WY_PANIC("ERROR: rhs in wyil_list_comb is type %d\n", rhs->typ)
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
 * given a wycc obj return an object that represents the length
 */
wycc_obj* wyil_length_of(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wyil_length_of");
    long typ = itm->typ;
    int rslt = 0;

    if (typ == Wy_List) {
	rslt = wycc_length_of_list(itm);
    } else if (typ == Wy_Tuple) {
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
	WY_PANIC("Help needed in lengthOf for type %d\n", itm->typ)
    };
    return wycc_box_int(rslt);
}

/*
 * debug using an unboxed string
 */
void wyil_debug_str(char* mesg) {
    ;
    //fprintf(stderr, "%s", mesg);
    fprintf(stdout, "%s", mesg);
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
	//fprintf(stderr, "%s", ptr1->ptr);
	fprintf(stdout, "%s", ptr1->ptr);
    } else if (ptr1->typ == Wy_CString) {
	//fprintf(stderr, "%s", ptr1->ptr);
	fprintf(stdout, "%s", ptr1->ptr);
    } else {
	//fprintf(stderr, "Help needed in Debug for type %d\n", ptr1->typ);
	fprintf(stdout, "Help needed in Debug for type %d\n", ptr1->typ);
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

static wycc_obj* wyil_index_of_list(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_index_of_list lhs");
    WY_OBJ_SANE(rhs, "wyil_index_of_list rhs");
    void** p = lhs->ptr;
    wycc_obj* ans;
    long idx;

    if (rhs->typ != Wy_Int) {
	WY_PANIC("Help needed in wyil_index_of_list for type %d\n", rhs->typ)
    };
    idx = (long) rhs->ptr;
    if (idx < 0) {
	fprintf(stderr, "ERROR: IndexOf under range for list (%d)\n", idx);
	exit(-4);
    };
    if (idx >= (long) p[0]) {
	fprintf(stderr, "ERROR: IndexOf over range for list (%d)\n", idx);
	exit(-4);
    };
    ans = (wycc_obj*) p[3+idx];
    ans->cnt++;
    return ans;
}

static wycc_obj* wyil_index_of_map(wycc_obj* map, wycc_obj* key){
    WY_OBJ_SANE(map, "wyil_index_of_map map");
    WY_OBJ_SANE(key, "wyil_index_of_map key");
    wycc_obj* ans;

    ans = wycc_index_of_map(map, key);
    if (ans != NULL) {
	return ans;
    }
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
	WY_PANIC("Help needed in wyil_index_of_string for type %d\n"
		 , index->typ)
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
	WY_PANIC("HELP needed in wyil_range for start of type %d\n"
		, lhs->typ)
    };
    if (rhs->typ != Wy_Int) {
	WY_PANIC("HELP needed in wyil_range for end of type %d\n"
		, rhs->typ)
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
    if (lhs->typ == Wy_Tuple) {
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
    WY_PANIC("Help needed in wyil_index_of for type %d\n", lhs->typ)
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
void wycc__print(wycc_obj* sys, wycc_obj* itm) {
    /* WY_OBJ_SANE(sys); */
    WY_OBJ_SANE(itm, "wycc__print itm");
    wycc_obj* alt;
    int tmp;
    FILE *fil;

    //if (sys->typ != Wy_Token) {
    if (sys->typ != Wy_Addr) {
	fprintf(stderr, "Help needed in wycc__print for type %d\n", sys->typ);
    };
    fil = (FILE *) sys->ptr;
    if (itm->typ == Wy_String) {
	//printf("%s", (char *) itm->ptr);
	fprintf(fil, "%s", (char *) itm->ptr);
	return;
    };
    if (itm->typ == Wy_CString) {
	//printf("%s", (char *) itm->ptr);
	fprintf(fil, "%s", (char *) itm->ptr);
	return;
    };
    if (itm->typ == Wy_Char) {
	tmp = (int) itm->ptr;
	//printf("'%c'", (char) tmp);
	fprintf(fil, "'%c'", (char) tmp);
	return;
    };
    if (itm->typ == Wy_Int) {
	//printf("%-.1d", (long) itm->ptr);
	fprintf(fil, "%-.1d", (long) itm->ptr);
	return;
    };
    if (itm->typ == Wy_Bool) {
	if (itm->ptr == NULL) {
	    //printf("false");
	    fprintf(fil, "false");
	} else {
	    //printf("true");
	    fprintf(fil, "true");
	}
	return;
    };
    alt = wycc__toString(itm);
    //printf("%s", alt->ptr);
    fprintf(fil, "%s", alt->ptr);
    wycc_deref_box(alt);
}

static wycc_obj* wycc_inst_nl = NULL;
static wycc_obj* wycc_get_nl(){
    wycc_obj* ans = wycc_inst_nl;

    if (ans != NULL) {
	return ans;
    };
    ans = wycc_box_cstr("\n");
    wycc_inst_nl = ans;
    return ans;
}

/*
 * given a System object, write a line to the file referred to by out
 */
void wycc__println(wycc_obj* sys, wycc_obj* itm) {
    /* WY_OBJ_SANE(sys); */
    WY_OBJ_SANE(itm, "wycc__println itm");
    wycc_obj* nl;

    wycc__print(sys, itm);
    //printf("\n");
    nl = wycc_get_nl();
    wycc__print(sys, nl);
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

    if (((int)p[1]) == -1) {
	return wycc_box_cstr("Unknown");
    };
    meta = (wycc_obj *) p[1];
    cnt = (int) p[0];
    if (meta->typ != Wy_Rcd_Rcd) {
	WY_PANIC("Help needed in wycc__toString_record, bad meta %d\n"
		, meta->typ)
    };
    nams = wycc_type_record_names((int) meta->ptr);
    if (nams->typ != Wy_List) {
	WY_PANIC("Help needed in wycc__toString_record, bad names %d\n"
		, nams->typ)
    };
    siz = 1 + (4 * cnt);
    buf = (char *) malloc(siz);
    buf[0] = '\0';
    strncat(buf, "{", siz);
    at = 1;
    pa = (void **) nams->ptr;
    for (idx= 0; idx < cnt; idx++) {
	nxt = (wycc_obj *) pa[3+idx];
	if ((nxt->typ != Wy_String) && (nxt->typ != Wy_CString)) {
	    WY_PANIC("Help needed in wycc__toString_record for name %d\n"
		    , nxt->typ)
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
	nxt = (wycc_obj *) p[3+idx];
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
    chptr->chk = &(p[3]);
    chptr->at = 0;
    chptr->flg = 1;	/* this is a map */
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

/*
 * given a record, step thru every slot pairing with its meta data
 */
static wycc_obj *wycc__toString_array(wycc_obj *itm){
    WY_OBJ_SANE(itm, "wycc__toString_array");
    size_t siz;
    long cnt, idx, at;
    long tmp, tmpa, tmpb;
    char *buf;
    char *ptr;
    wycc_obj* nxt;

    cnt = wycc_length_of_list(itm);
    siz = 3 + (cnt * 4);	/* minimalist approx. */
    buf = (char *) malloc(siz);
    buf[0] = '\0';
    if (itm->typ == Wy_List) {
	strncat(buf, "[", siz);
    } else {
	strncat(buf, "(", siz);
    };
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
	    if (itm->typ == Wy_List) {
		strcpy((buf+at), ", ");
		at += 2;
	    } else {
		strcpy((buf+at), ",");
		at += 1;
	    };
	};
	strcpy((buf+at), nxt->ptr);
	at += tmp;
	wycc_deref_box(nxt);
    }
    if (itm->typ == Wy_List) {
	strcpy((buf+at), "]");
    } else {
	strcpy((buf+at), ")");
    };
    return wycc_box_str(buf);
}

/*
 * given any type of object, produce a string object that represents its value
 */
wycc_obj* wycc__toString(wycc_obj* itm) {
    WY_OBJ_SANE(itm, "wycc__toString");
    size_t siz;
    long tmp, tmpa, at;
    char *buf;
    char *part;

    if (itm == (wycc_obj *)NULL) {
	return wycc_box_cstr("null");
    }
    //if (itm->typ == Wy_String) {
    if ((itm->typ == Wy_String) || (itm->typ == Wy_CString)) {
	part = (char *) itm->ptr;
	siz = strlen(part);
	siz += 4;
	buf = (char *) malloc(siz);
	sprintf(buf, "\"%s\"", part);
	return wycc_box_str(buf);
	itm->cnt++;
	return itm;
    };
    //if (itm->typ == Wy_CString) {
    //itm->cnt++;
    //	return itm;
    //};
    if (itm->typ == Wy_Int) {
	tmp = (int) itm->ptr;
	buf = (char *) malloc(16);
	snprintf(buf, 16, "%-.1d", tmp);
	return wycc_box_str(buf);
    };
    if (itm->typ == Wy_Float) {
	long double *fltp = (long double *) itm->ptr;
	buf = (char *) malloc(16);
	snprintf(buf, 16, "%#Lg", *fltp);
	siz = strlen(buf) - 1;
	while ((siz > 1) && (buf[siz] == '0') && (buf[siz-1] != '.')) {
	    buf[siz--] = '\0';
	};
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
    if (itm->typ == Wy_Byte) {
	tmp = (int) itm->ptr;
	buf = (char *) malloc(9 + 3);	/* excess pad */
	for (tmpa= 128, at= 0; tmpa >0; at++, tmpa>>=1) {
	    if (tmpa & tmp) {
		buf[at] = '1';
	    } else {
		buf[at] = '0';
	    };
	}
	buf[at++] = 'b';
	buf[at++] = '\0';
	return wycc_box_str(buf);
    };
    if (itm->typ == Wy_Null) {
	return wycc_box_cstr("null");
    };
    if ((itm->typ == Wy_List) || (itm->typ == Wy_Tuple)) {
	return wycc__toString_array(itm);
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
    if (itm->typ == Wy_WInt) {
	return wycc__toString_wint(itm);
    };
    return wycc_box_cstr("Unknown");
}

/*
 * stdlib/whiley/lang
 * from Byte  
 */
// = wycc__toString	(byte)	(char)	(int)	(any)
// wycc__toUnsignedInt	(byte)	([byte])
// wycc__toInt		(byte)
// wycc__toChar		(byte)	([byte])
// .stack
// wycc__top		([int])
// wycc__push		([int], int)
// wycc__pop		([int])
// .list
// wycc__enlarge	([int], int, int)
// wycc__create		(int, int)	(int, bool)
// wycc__reverse	([bool])	([byte])	([int])
// .errors
// wycc__Error		(string)
// wycc__SyntaxError
// .string
// wycc__indexOf	(string, char)	(string, char, int)
// wycc__lastIndexOf	(string, char)
// wycc__replace	(string, char, char)
// wycc__fromASCII	([byte])
// wycc__toUTF8		(string)
// .real
// wycc__toString	(real)
// wycc__parse		(string) ****
// wycc__toDecimal	(real)	(real, int)
// .time
// wycc__current	()
// .file
// wycc__Reader		(string)
// wycc__close		(ref)
// wycc__read		(ref)	(ref, int)
// wycc__Writer		(string)
// wycc__write		(ref, [byte])
// .char
// wycc__isUpperCase	(char)
// wycc__isLowerCase	(char)
// = wycc__isLetter	(char)
// wycc__isDigit	(char)
// wycc__isWhiteSpace	(char)
// .int
// wycc__toHexString	(int)
// wycc__toUnsignedByte	(int)
// wycc__toUnsignedBytes	(int)
// wycc__toSignedByte	(int)
// .system
// = wycc__print		(ref, string)
// = wycc__println	(ref, string)
// .math
// = wycc__abs		(int)	(real)
// = wycc__max		(int, int)	(real, real)
// = wycc__min		(int, int)	(real, real)
// wycc__pow		(int, int)	(real, real)
// wycc__floor		(real)
// wycc__ceil		(real)
// wycc__round		(real)
// wycc__isqrt		(int)
// wycc__sqrt 		(int, real)	(real, real)

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
