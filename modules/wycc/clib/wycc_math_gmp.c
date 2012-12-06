/*
 * wycc_math_gmp.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc), especailly support
 * for the math operations.  These routines rely on gmp (Gnu Muliple precision)
 * when there is an attempt to do unbounded integer (WInt) operations 
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
#include "math.h"
#include <gmp.h>
#include <string.h>

#define GMP_Chunk	64
#define LONG_SIZE	31

static wycc_obj* wycc_float_int(wycc_obj* itm);

static void ** mpz_pool = NULL;
static void ** mpq_pool = NULL;
// int Wycc_Math_Gmp = 1;

static mpz_t *wycc_wint_alloc() {
    void **it = mpz_pool;
    mpz_t *ans;
    int idx;

    if (it == NULL) {
	ans = (mpz_t *) calloc(GMP_Chunk, sizeof(__mpz_struct));
	for (idx= 0; idx < (GMP_Chunk -1) ; idx++) {
	    it = (void **) &(ans[idx]);
	    it = (void *) &(ans[idx+1]);
	}
    }
    mpz_pool = (void *) *(it);
    ans = (mpz_t*) it;
    mpz_init(*ans);
    return ans;
}

void wycc_wint_free(void *it) {
    void ** lst;
    mpz_t *wi;

    wi = (mpz_t *) it;
    mpz_clear(*wi);
    lst = (void **) it;
    *lst = (void *) mpz_pool;
    mpz_pool = lst;
}

static mpq_t *wycc_ratio_alloc() {
    void **it = mpq_pool;
    mpq_t *ans;
    int idx;

    if (it == NULL) {
	ans = (mpq_t *) calloc(GMP_Chunk, sizeof(__mpq_struct));
	for (idx= 0; idx < (GMP_Chunk -1) ; idx++) {
	    it = (void **) &(ans[idx]);
	    it = (void *) &(ans[idx+1]);
	}
    }
    mpq_pool = (void *) *(it);
    ans = (mpq_t*) it;
    mpq_init(*ans);
    return ans;
}

void wycc_ratio_free(void *it) {
    void ** lst;
    mpq_t *rati;

    rati = (mpq_t *) it;
    mpq_clear(*rati);
    lst = (void **) it;
    *lst = (void *) mpq_pool;
    mpq_pool = lst;
}

/*
 * given a text/string representations of a very large integer, box up a gmp
 */
wycc_obj *wycc_box_wint(const char *txt) {
    mpz_t *aiw;
    int sts;

    aiw = wycc_wint_alloc();
    sts = mpz_set_str(*aiw, txt, 10);
    if (sts != 0) {
	WY_PANIC("Help needed in wycc_box_wint failed with '%s'\n", txt)
    }
    return wycc_box_new(Wy_WInt, (void *) aiw);
}
/*
 * given a text/string representations of a ratio, box up a gmp
 */
wycc_obj *wycc_box_ratio(const char *txt) {
    mpq_t *air;
    int sts;
    int at;
    char *ptr;
    size_t cnta, cntb, cntc;
    char *buf;

    air = wycc_ratio_alloc();
    cnta = 0;
    cntb = 0;
    for (sts= 0, at= 0; txt[at] != '\0' ; at++) {
	cnta++;
	cntb += sts;
	if (txt[at] == '.') {
	    sts++;
	};
    }
    if (sts > 1) {
	WY_PANIC("Help needed in wycc_box_ratio failed with '%s'\n", txt)
    };
    if (sts == 1) {
	buf = (char *) malloc(4 + cnta + cntb);
	cntc = cnta - cntb - 1;
	strncpy(buf, txt, cntc);
	strncpy((buf+cntc), (txt+cntc+1), cntb);
	ptr= buf + cnta - 1;
	//ptr = buf + cnta;
	*ptr++ = '/';
	*ptr++ = '1';
	for (cntb; cntb > 0; cntb--) { 
	    *ptr++ = '0';
	}
	*ptr++ = '\0';
	if (wycc_debug_flag) {
	    fprintf(stderr, "wycc_box_ratio(%s) => %s\n", txt, buf);
	}
	sts = mpq_set_str(*air, buf, 10);
    } else {
	sts = mpq_set_str(*air, txt, 10);
    }
    if (sts != 0) {
	WY_PANIC("Help needed in wycc_box_ratio failed with '%s'\n", txt)
    }
    mpq_canonicalize(*air);
    return wycc_box_new(Wy_Ratio, (void *) air);
}

/*
 * give a pointer to a gmp rational and a pointer to an object, set the rational
 */
mpq_t *wycc_math_rat(mpq_t *rati, wycc_obj *itm) {
    long ival;
    long double fval;

    mpq_init(*rati);
    if ((itm->typ == Wy_Int) || (itm->typ == Wy_Byte) \
	|| (itm->typ == Wy_Char) || (itm->typ == Wy_Bool)) {
	ival = (long) itm->ptr;
	mpq_set_si(*rati, ival, 1);
	return rati;
    } else if (itm->typ == Wy_Float) {
	fval = *((long double *) itm->ptr);
	mpq_set_d(*rati, fval);
    };
    WY_PANIC("Help needed in wycc_math_rat w/ %d\n", itm->typ)
}

/*
 * simple comarison of two long double (floats)
 */
int wycc_comp_float(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_float lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_float rhs");
    long double *xp;
    long double *yp;

    if ((lhs->typ == Wy_Float) && (rhs->typ == Wy_Float)) {
	xp = (long double *) lhs->ptr;
	yp = (long double *) rhs->ptr;
	if (*xp < *yp) {
	    return -1;
	} else if (*xp > *yp) {
	    return 1;
	};
	return 0;
    };

}

static int fix_cond(int flg) {
    if (flg > 0) {
	return 1;
    } else if (flg < 0) {
	return -1;
    };
    return 0;
}

/*
 * simple comparison of two integers
 */
int wycc_comp_int(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_int lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_int rhs");
    int lhv, rhv;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    int ans;

    if (lhs->typ == Wy_WInt) {
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    ans = mpz_cmp(*liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    ans = mpz_cmp(*liw, rw);
	    mpz_clear(rw);
	}
	return fix_cond(ans);
    } else if (rhs->typ == Wy_WInt) {
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	ans = mpz_cmp(lw, *riw);
	mpz_clear(lw);
	return fix_cond(ans);
    };
    lhv = (long) lhs->ptr;
    rhv = (long) rhs->ptr;
    if (lhv < rhv) {
	return -1;
    } else if (lhv > rhv) {
	return 1;
    };
    return 0;
}

/*
 * given an object, return an object of type int (Wy_Int)
 */
wycc_obj* wyil_convert_int(wycc_obj* itm){
    long val;
    long double flt;

    if ((itm->typ == Wy_Int) || (itm->typ == Wy_WInt)) {
	itm->cnt++;
	return itm;
    };
    if ((itm->typ == Wy_Char) || (itm->typ == Wy_Byte)
	|| (itm->typ == Wy_Bool)) {
	val = (long) itm->ptr;
	return wycc_box_long(val);
    } ;
    if (itm->typ == Wy_Float) {
	flt = *((long double*)itm->ptr);
	val = (long) flt;
	return wycc_box_long(val);
    };
	WY_PANIC("Help needed in wyil_convert_int w/ %d\n", itm->typ)
}

/*
 * given an object, return an object of type real (Wy_Float)
 */
wycc_obj* wyil_convert_real(wycc_obj* itm){
    long val;
    //long double flt;
    mpq_t *air;
    mpz_t *aiq;

    if ((itm->typ == Wy_Float) || (itm->typ == Wy_Ratio)) {
	itm->cnt++;
	return itm;
    };
    if ((itm->typ == Wy_Char) || (itm->typ == Wy_Byte)
	|| (itm->typ == Wy_Bool) || (itm->typ == Wy_Int)) {
	val = (long) itm->ptr;
	air = wycc_ratio_alloc();
	mpq_set_si(*air, val, 1);
	return wycc_box_new(Wy_Ratio, (void *) air);
    };
    if (itm->typ == Wy_WInt) {
	aiq = (mpz_t *) itm->ptr;
	air = wycc_ratio_alloc();
	mpq_set_z(*air, *aiq);
    }
    WY_PANIC("Help needed in wyil_convert_real w/ %d\n", itm->typ)
}

/*
 * floating point add operation
 */
static wycc_obj* wyil_add_ld(wycc_obj* lhs, wycc_obj* rhs){
    long double *xp;
    long double *yp;
    long double rslt;

    if (lhs->typ != Wy_Float) {
	lhs = wycc_float_int(lhs);
    };
    if (rhs->typ != Wy_Float) {
	rhs = wycc_float_int(rhs);
    };
    xp = (long double *) lhs->ptr;
    yp = (long double *) rhs->ptr;
    rslt = *xp + *yp;
    return wycc_box_float(rslt);
}

/*
 * ratio add operation
 */
static wycc_obj* wyil_add_qz(wycc_obj* lhs, wycc_obj* rhs){
    mpq_t lq;
    mpq_t rq;
    mpq_t *air;
    mpq_t *lir;
    mpq_t *rir;

    if (lhs->typ == Wy_Ratio) {
	lir = (mpq_t *) lhs->ptr;
    } else {
	lir = wycc_math_rat(&lq, lhs);
    };
    if (rhs->typ == Wy_Ratio) {
	rir = (mpq_t *) rhs->ptr;
    } else {
	rir = wycc_math_rat(&rq, lhs);
    };
    air = wycc_ratio_alloc();
    mpq_add(*air, *lir, *rir);
    return wycc_box_new(Wy_Ratio, (void *) air);
}

/*
 * floating point sub operation
 */
static wycc_obj* wyil_sub_ld(wycc_obj* lhs, wycc_obj* rhs){
    long double *xp;
    long double *yp;
    long double rslt;

    if (lhs->typ != Wy_Float) {
	lhs = wycc_float_int(lhs);
    };
    if (rhs->typ != Wy_Float) {
	rhs = wycc_float_int(rhs);
    };
    xp = (long double *) lhs->ptr;
    yp = (long double *) rhs->ptr;
    rslt = *xp - *yp;
    return wycc_box_float(rslt);
}

/*
 * ratio sub operation
 */
static wycc_obj* wyil_sub_qz(wycc_obj* lhs, wycc_obj* rhs){
    mpq_t lq;
    mpq_t rq;
    mpq_t *air;
    mpq_t *lir;
    mpq_t *rir;

    if (lhs->typ == Wy_Ratio) {
	lir = (mpq_t *) lhs->ptr;
    } else {
	lir = wycc_math_rat(&lq, lhs);
    };
    if (rhs->typ == Wy_Ratio) {
	rir = (mpq_t *) rhs->ptr;
    } else {
	rir = wycc_math_rat(&rq, lhs);
    };
    air = wycc_ratio_alloc();
    mpq_sub(*air, *lir, *rir);
    return wycc_box_new(Wy_Ratio, (void *) air);
}

/*
 * rudimentary sub operation
 */
wycc_obj* wyil_sub(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_sub lhs");
    WY_OBJ_SANE(rhs, "wyil_sub rhs");
    long rslt;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    mpz_t *aiw;

    if ((lhs->typ == Wy_Ratio) || (rhs->typ == Wy_Ratio)){
	return wyil_sub_qz(lhs, rhs);
    };
    if ((lhs->typ == Wy_Float) || (rhs->typ == Wy_Float)){
	return wyil_sub_ld(lhs, rhs);
    };
    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    mpz_sub(*aiw, *liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    mpz_sub(*aiw, *liw, rw);
	    mpz_clear(rw);
	}
	return wycc_box_new(Wy_WInt, (void *) aiw);
    } else if (rhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	mpz_sub(*aiw, lw, *riw);
	mpz_clear(lw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt = ((long) lhs->ptr) - ((long)rhs->ptr); 
    return wycc_box_type_match(lhs, rhs, rslt);
    //WY_PANIC("Help needed in sub for wide ints (%d)\n", ac)
}

/*
 * rudimentary add operation
 */
wycc_obj* wyil_add(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_add lhs");
    WY_OBJ_SANE(rhs, "wyil_add rhs");
    long rslt;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    mpz_t *aiw;

    if ((lhs->typ == Wy_Ratio) || (rhs->typ == Wy_Ratio)){
	return wyil_add_qz(lhs, rhs);
    };
    if ((lhs->typ == Wy_Float) || (rhs->typ == Wy_Float)){
	return wyil_add_ld(lhs, rhs);
    };
    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    mpz_add(*aiw, *liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    mpz_add(*aiw, *liw, rw);
	    mpz_clear(rw);
	}
	return wycc_box_new(Wy_WInt, (void *) aiw);
    } else if (rhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	mpz_add(*aiw, lw, *riw);
	mpz_clear(lw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };

    rslt = ((long) lhs->ptr) + ((long)rhs->ptr); 
    return wycc_box_type_match(lhs, rhs, rslt);
    //WY_PANIC("Help needed in add for wide ints (%d)\n", ac)
}

/*
 * rudimentary negation operation
 */
wycc_obj* wyil_negate(wycc_obj* itm){
    WY_OBJ_SANE(itm, "wyil_negate");
    long double ld;
    long double *ldp;
    mpz_t *liw;
    mpz_t *aiw;
    mpq_t *lir;
    mpq_t *air;

    if (itm->typ == Wy_Int) {
	return wycc_box_long(-((long) itm->ptr));
    };
    if (itm->typ == Wy_Byte) {
	return wycc_box_byte(-((long) itm->ptr));
    };
    if (itm->typ == Wy_Char) {
	return wycc_box_char(-((long) itm->ptr));
    };
    if (itm->typ == Wy_Float) {
	ldp = (long double *) itm->ptr;
	ld = *ldp * (-1.0);
	return wycc_box_float(ld);
    };
    if (itm->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) itm->ptr;
	mpz_neg(*aiw, *liw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    if (itm->typ == Wy_Ratio) {
	air = wycc_ratio_alloc();
	lir = (mpq_t *) itm->ptr;
	mpq_neg(*air, *lir);
	return wycc_box_new(Wy_Ratio, (void *) air);
    };
    WY_PANIC("Help needed in wyil_negate for type (%d)\n", itm->typ)
}

/*
 * given an int of some ilk, conjure the floating point object.
 */
static wycc_obj* wycc_float_int(wycc_obj* itm){
    WY_OBJ_SANE(itm, "wycc_float_int");
    long rslt;
    char chr;
    long double x;

    if ((itm->typ == Wy_Int) || (itm->typ == Wy_Char)) {
	rslt = (long) itm->ptr;
	x = (long double) rslt;
	return wycc_box_float(x);
    };
    if (itm->typ == Wy_Byte) {
	rslt = (long) itm->ptr;
	rslt &= 255;
	x = (long double) rslt;
	return wycc_box_float(x);
    };
    if (itm->typ == Wy_Bool) {
	rslt = 1;
	if (((long) itm->ptr) == 0) {
	    rslt = 0;
	};
	x = (long double) rslt;
	return wycc_box_float(x);
    };
    WY_PANIC("Help needed in wycc_float_int for type %d\n", itm->typ)
}

/*
 * rudimentary and operation
 */
wycc_obj* wyil_bit_and(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_bit_and lhs");
    WY_OBJ_SANE(rhs, "wyil_bit_and rhs");
    long rslt;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    mpz_t *aiw;
    int lc, rc, ac;
    /* wycc_obj* ans;*/

    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    mpz_and(*aiw, *liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    mpz_and(*aiw, *liw, rw);
	    mpz_clear(rw);
	}
	return wycc_box_new(Wy_WInt, (void *) aiw);
    } else if (rhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	mpz_and(*aiw, lw, *riw);
	mpz_clear(lw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt = ((long) lhs->ptr) & ((long)rhs->ptr);
    return wycc_box_type_match(lhs, rhs, rslt);
    // WY_PANIC("Help needed in bit_and for wide ints (%d)\n", ac)
}

/*
 * rudimentary ior operation
 */
wycc_obj* wyil_bit_ior(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_bit_ior lhs");
    WY_OBJ_SANE(rhs, "wyil_bit_ior rhs");
    long rslt;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    mpz_t *aiw;

    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    mpz_ior(*aiw, *liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    mpz_ior(*aiw, *liw, rw);
	    mpz_clear(rw);
	}
	return wycc_box_new(Wy_WInt, (void *) aiw);
    } else if (rhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	mpz_ior(*aiw, lw, *riw);
	mpz_clear(lw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt = ((long) lhs->ptr) | ((long)rhs->ptr); 
    return wycc_box_type_match(lhs, rhs, rslt);
    //WY_PANIC("Help needed in bit_or for wide ints (%d)\n", ac)
}

/*
 * rudimentary xor operation
 */
wycc_obj* wyil_bit_xor(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_bit_xor lhs");
    WY_OBJ_SANE(rhs, "wyil_bit_xor rhs");
    long rslt;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    mpz_t *aiw;

    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    mpz_xor(*aiw, *liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    mpz_xor(*aiw, *liw, rw);
	    mpz_clear(rw);
	}
	return wycc_box_new(Wy_WInt, (void *) aiw);
    } else if (rhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	mpz_xor(*aiw, lw, *riw);
	mpz_clear(lw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt = ((long) lhs->ptr) ^ ((long)rhs->ptr); 
    return wycc_box_type_match(lhs, rhs, rslt);
    // WY_PANIC("Help needed in bit_xor for wide ints (%d)\n", ac)
}

/*
 * floating point div operation
 */
static wycc_obj* wyil_div_ld(wycc_obj* lhs, wycc_obj* rhs){
    long double *xp;
    long double *yp;
    long double rslt;

    if (lhs->typ != Wy_Float) {
	lhs = wycc_float_int(lhs);
    };
    if (rhs->typ != Wy_Float) {
	rhs = wycc_float_int(rhs);
    };
    xp = (long double *) lhs->ptr;
    yp = (long double *) rhs->ptr;
    rslt = *xp / *yp;
    return wycc_box_float(rslt);
}

/*
 * ratio div operation
 */
static wycc_obj* wyil_div_qz(wycc_obj* lhs, wycc_obj* rhs){
    mpq_t lq;
    mpq_t rq;
    mpq_t *air;
    mpq_t *lir;
    mpq_t *rir;

    if (lhs->typ == Wy_Ratio) {
	lir = (mpq_t *) lhs->ptr;
    } else {
	lir = wycc_math_rat(&lq, lhs);
    };
    if (rhs->typ == Wy_Ratio) {
	rir = (mpq_t *) rhs->ptr;
    } else {
	rir = wycc_math_rat(&rq, lhs);
    };
    air = wycc_ratio_alloc();
    mpq_div(*air, *lir, *rir);
    return wycc_box_new(Wy_Ratio, (void *) air);
}

/*
 * rudimentary divide operation
 */
wycc_obj* wyil_div(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_div lhs");
    WY_OBJ_SANE(rhs, "wyil_div rhs");
    int typ;
    long rslt;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    mpz_t *aiw;

    if ((lhs->typ == Wy_Ratio) || (rhs->typ == Wy_Ratio)){
	return wyil_div_qz(lhs, rhs);
    };
    if ((lhs->typ == Wy_Float) || (rhs->typ == Wy_Float)){
	return wyil_div_ld(lhs, rhs);
    };
    typ = wycc_type_dealias(rhs->typ);
    if (typ != Wy_Int) {
	WY_PANIC("Bad type for rhs in div (%d)\n", rhs->typ)
    };
    typ = wycc_type_dealias(lhs->typ);
    if (typ != Wy_Int) {
	WY_PANIC("Bad type for lhs in div (%d)\n", lhs->typ)
    };
    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    mpz_tdiv_q(*aiw, *liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    mpz_tdiv_q(*aiw, *liw, rw);
	    mpz_clear(rw);
	}
	return wycc_box_new(Wy_WInt, (void *) aiw);
    } else if (rhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	mpz_tdiv_q(*aiw, lw, *riw);
	mpz_clear(lw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };

    rslt = ((long) lhs->ptr) / ((long)rhs->ptr); 
    return wycc_box_type_match(lhs, rhs, rslt);
    //WY_PANIC("Help needed in div for wide ints (%d)\n", ac)
}

/*
 * rudimentary modulo operation
 */
wycc_obj* wyil_mod(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_mod lhs");
    WY_OBJ_SANE(rhs, "wyil_mod rhs");
    long rslt;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    mpz_t *aiw;

    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    mpz_mod(*aiw, *liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    mpz_mod(*aiw, *liw, rw);
	    mpz_clear(rw);
	}
	return wycc_box_new(Wy_WInt, (void *) aiw);
    } else if (rhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	mpz_mod(*aiw, lw, *riw);
	mpz_clear(lw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt = ((long) lhs->ptr) % ((long)rhs->ptr); 
    return wycc_box_type_match(lhs, rhs, rslt);
    // WY_PANIC("Help needed in mod for wide ints (%d)\n", ac)
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

/*
 * floating point multiply operation
 */
static wycc_obj* wyil_mul_ld(wycc_obj* lhs, wycc_obj* rhs){
    long double *xp;
    long double *yp;
    long double rslt;

    if (lhs->typ != Wy_Float) {
	lhs = wycc_float_int(lhs);
    };
    if (rhs->typ != Wy_Float) {
	rhs = wycc_float_int(rhs);
    };
    xp = (long double *) lhs->ptr;
    yp = (long double *) rhs->ptr;
    rslt = *xp * *yp;
    return wycc_box_float(rslt);
}

/*
 * ratio multiply operation
 */
static wycc_obj* wyil_mul_qz(wycc_obj* lhs, wycc_obj* rhs){
    mpq_t lq;
    mpq_t rq;
    mpq_t *air;
    mpq_t *lir;
    mpq_t *rir;

    if (lhs->typ == Wy_Ratio) {
	lir = (mpq_t *) lhs->ptr;
    } else {
	lir = wycc_math_rat(&lq, lhs);
    };
    if (rhs->typ == Wy_Ratio) {
	rir = (mpq_t *) rhs->ptr;
    } else {
	rir = wycc_math_rat(&rq, lhs);
    };
    air = wycc_ratio_alloc();
    mpq_mul(*air, *lir, *rir);
    return wycc_box_new(Wy_Ratio, (void *) air);
}

/*
 * rudimentary multiply operation
 */
wycc_obj* wyil_mul(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_mul lhs");
    WY_OBJ_SANE(rhs, "wyil_mul rhs");
    long rslt;
    mpz_t lw;
    mpz_t rw;
    mpz_t *liw;
    mpz_t *riw;
    mpz_t *aiw;
    int lc, rc;
    int typ;

    if ((lhs->typ == Wy_Ratio) || (rhs->typ == Wy_Ratio)){
	return wyil_mul_qz(lhs, rhs);
    };
    if ((lhs->typ == Wy_Float) || (rhs->typ == Wy_Float)){
	return wyil_mul_ld(lhs, rhs);
    };
    typ = wycc_type_dealias(rhs->typ);
    if (typ != Wy_Int) {
	WY_PANIC("Bad type for rhs in mul (%d)\n", rhs->typ)
    };
    typ = wycc_type_dealias(lhs->typ);
    if (typ != Wy_Int) {
	WY_PANIC("Bad type for lhs in mul (%d)\n", lhs->typ)
    };
    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	if (rhs->typ == Wy_WInt) {
	    riw = (mpz_t *) rhs->ptr;
	    mpz_mul(*aiw, *liw, *riw);
	} else {
	    mpz_init(rw);
	    mpz_set_si(rw, (long)rhs->ptr);
	    mpz_mul(*aiw, *liw, rw);
	    mpz_clear(rw);
	}
	return wycc_box_new(Wy_WInt, (void *) aiw);
    } else if (rhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	riw = (mpz_t *) rhs->ptr;
	mpz_init(lw);
	mpz_set_si(lw, (long)lhs->ptr);
	mpz_mul(*aiw, lw, *riw);
	mpz_clear(lw);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt = (long) lhs->ptr;
    lc = wycc_ilog2(rslt);
    rc = wycc_ilog2((long) rhs->ptr);
    if ((lc + rc) < LONG_SIZE) {
	rslt *= (long)rhs->ptr; 
	return wycc_box_long(rslt);
    };
    aiw = wycc_wint_alloc();
    mpz_inits(lw, rw, 0);
    mpz_set_si(lw, (long)lhs->ptr);
    mpz_set_si(rw, (long)rhs->ptr);
    mpz_mul(*aiw, lw, rw);
    mpz_clears(lw, rw, 0);
    return wycc_box_new(Wy_WInt, (void *) aiw);
    //WY_PANIC("Help needed in mul for wide ints (22)\n")
}

wycc_obj* wyil_shift_up(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_shift_up lhs");
    WY_OBJ_SANE(rhs, "wyil_shift_up rhs");
    long rslt;
    mpz_t lw;
    mpz_t *liw;
    mpz_t *aiw;
    mp_bitcnt_t op2;
    int typ;
    long lc, rc;

    typ = wycc_type_dealias(rhs->typ);
    if (typ != Wy_Int) {
	WY_PANIC("Bad type for rhs in shift_up (%d)\n", rhs->typ)
    };
    typ = wycc_type_dealias(lhs->typ);
    if (typ != Wy_Int) {
	WY_PANIC("Bad type for lhs in shift_up (%d)\n", lhs->typ)
    };
    if (rhs->typ == Wy_WInt) {
	WY_PANIC("Help needed in shift_up for wide int count\n")
    };
    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	op2 = (mp_bitcnt_t) rhs->ptr;
	mpz_mul_2exp(*aiw, *liw, op2);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt = (long) lhs->ptr;
    lc = wycc_ilog2(rslt);
    rc = (long) rhs->ptr;
    if ((lc + rc) > LONG_SIZE) {
	aiw = wycc_wint_alloc();
	mpz_clear(lw);
	mpz_set_si(lw, rslt);
	op2 = (mp_bitcnt_t) rc;
	mpz_mul_2exp(*aiw, *liw, op2);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt <<= rc;
    return wycc_box_type_match(lhs, rhs, rslt);
    //return wycc_box_long(rslt);
}

wycc_obj* wyil_shift_down(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_shift_down lhs");
    WY_OBJ_SANE(rhs, "wyil_shift_down rhs");
    long rslt;
    mpz_t lw;
    mpz_t *liw;
    mpz_t *aiw;
    mp_bitcnt_t op2;
    int typ;
    int lc, rc, ac;
    /* wycc_obj* ans;*/

    typ = wycc_type_dealias(rhs->typ);
    if (typ != Wy_Int) {
	WY_PANIC("Bad type for rhs in shift_down (%d)\n", rhs->typ)
    };
    typ = wycc_type_dealias(lhs->typ);
    if (typ != Wy_Int) {
	WY_PANIC("Bad type for lhs in shift_down (%d)\n", lhs->typ)
    };
    if (rhs->typ == Wy_WInt) {
	WY_PANIC("Help needed in shift_up for wide int count\n")
    };
    if (lhs->typ == Wy_WInt) {
	aiw = wycc_wint_alloc();
	liw = (mpz_t *) lhs->ptr;
	op2 = (mp_bitcnt_t) rhs->ptr;
	mpz_tdiv_q_2exp(*aiw, *liw, op2);
	return wycc_box_new(Wy_WInt, (void *) aiw);
    };
    rslt = (long) lhs->ptr;
    rc = (long) rhs->ptr;
    rslt >>= rc;
    return wycc_box_type_match(lhs, rhs, rslt);
    //return wycc_box_long(rslt);
}

wycc_obj* wyil_numer(wycc_obj* rat) {
    WY_OBJ_SANE(rat, "wyil_numer");
    mpq_t *rati;
    mpz_t *aiw;

    if (rat->typ != Wy_Ratio) {
	WY_PANIC("Bad type in wyil_numer (%d)\n", rat->typ)
    };
    rati = (mpq_t *) rat->ptr;
    aiw = wycc_wint_alloc();
    mpq_get_num(*aiw, *rati);
    return wycc_box_new(Wy_WInt, (void *) aiw);
}

wycc_obj* wyil_denom(wycc_obj* rat) {
    WY_OBJ_SANE(rat, "wyil_denom");
    mpq_t *rati;
    mpz_t *aiw;

    if (rat->typ != Wy_Ratio) {
	WY_PANIC("Bad type in wyil_denom (%d)\n", rat->typ)
    };
    rati = (mpq_t *) rat->ptr;
    aiw = wycc_wint_alloc();
    mpq_get_den(*aiw, *rati);
    return wycc_box_new(Wy_WInt, (void *) aiw);
}

/*
 * given a wide int
 */
wycc_obj *wycc__toString_wint(wycc_obj *itm){
    WY_OBJ_SANE(itm, "wycc__toString_wint");
    mpz_t *it = (mpz_t *) itm->ptr;
    size_t cnt;
    char *buf;

    cnt = mpz_sizeinbase(*it, 10);
    buf = (char *) malloc(4+cnt);
    mpz_get_str(buf, 10, *it);
    //fprintf(stderr, "\tstab '%s'\n", buf);
    //WY_PANIC("Help needed in wycc__toString_wint (%d)\n", cnt)
    return wycc_box_new(Wy_String, buf);
}

/*
 * given a ratio
 */
wycc_obj *wycc__toString_ratio(wycc_obj *itm){
    WY_OBJ_SANE(itm, "wycc__toString_ratio");
    mpq_t *it = (mpq_t *) itm->ptr;
    size_t cnt, idx;
    char *buf;

    cnt  = mpz_sizeinbase(mpq_numref(*it), 10);
    cnt += mpz_sizeinbase(mpq_denref(*it), 10);
    //buf = (char *) malloc(5+cnt);
    //mpq_get_str(buf, 10, *it);
    buf = (char *) malloc(7+cnt);
    mpq_get_str((buf), 10, *it);
    if (index(buf, '/') != NULL) {
	for (idx= 5+cnt; idx > 0; idx--) {
	    buf[idx] = buf[idx-1];
	}
	buf[0] = '(';
	strncat(buf, ")", 1);
    }
    //fprintf(stderr, "\tstab '%s'\n", buf);
    //WY_PANIC("Help needed in wycc__toString_wint (%d)\n", cnt)
    return wycc_box_new(Wy_String, buf);
}

static char *foo__s = NULL;
char *wycc_debug_foo__2s(wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc__toString_wint");
    mpz_t *it = (mpz_t *) itm->ptr;
    size_t cnt;

    if (foo__s != NULL) {
	free(foo__s);
    };
    cnt = mpz_sizeinbase(*it, 10);
    foo__s = (char *) malloc(4+cnt);
    mpz_get_str(foo__s, 10, *it);
    return foo__s;
}


/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
