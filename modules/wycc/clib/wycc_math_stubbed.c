/*
 * wycc_math_stubbed.c
 *
 * This is part of a library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc), especailly support
 * for the math operations.  These routines have been stubbed to panic when
 * there is an attempt to do unbounded integer (WInt) operations 
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

static wycc_obj* wycc_float_int(wycc_obj* itm);

void wycc_wint_free(void *it){
    WY_PANIC("Help needed in wycc_wint_free\n")
}
void wycc_ratio_free(void *it){
    WY_PANIC("Help needed in wycc_ratio_free\n")
}

/*
 * given a text/string representations of a very large integer, box up a gmp
 */
wycc_obj *wycc_box_wint(const char *txt) {
    WY_PANIC("Help needed in wycc_box_wint\n")
}

/*
 * almost simple comparison of two wide integers
 */
int wycc_comp_wint(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_wint lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_wint rhs");

    WY_PANIC("Help needed in wycc_comp_wint\n")
}

/*
 * almost simple comparison of two wide integers
 */
int wycc_comp_ratio(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_ratio lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_ratio rhs");

    WY_PANIC("Help needed in wycc_comp_ratio\n")
}
wycc_obj* wyil_numer(wycc_obj* rat) {
    WY_OBJ_SANE(rat, "wyil_numer");

    WY_PANIC("Help needed in wyil_numer\n")
}
wycc_obj* wyil_denom(wycc_obj* rat) {
    WY_OBJ_SANE(rat, "wyil_denom");

    WY_PANIC("Help needed in wyil_denom\n")
}

wycc_obj *wycc__toString_ratio(wycc_obj *rat) {
    WY_OBJ_SANE(rat, "wycc__toString_ratio");

    WY_PANIC("Help needed in wycc__toString_ratio\n")
}

/*
 * simple comarison of two long double (floats)
 */
int wycc_comp_float(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wycc_comp_float lhs");
    WY_OBJ_SANE(rhs, "wycc_comp_float rhs");
    long double *xp;
    long double *yp;

    xp = (long double *) lhs->ptr;
    yp = (long double *) rhs->ptr;
    if (*xp < *yp) {
	return -1;
    } else if (*xp > *yp) {
	return 1;
    };
    return 0;

}

/*
 * simple comparison of two small integers
 */
int wycc_comp_int(wycc_obj* lhs, wycc_obj* rhs){
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
    long double flt;

    if (itm->typ == Wy_Float) {
	itm->cnt++;
	return itm;
    };
    if ((itm->typ == Wy_Char) || (itm->typ == Wy_Byte)
	|| (itm->typ == Wy_Bool) || (itm->typ == Wy_Int)) {
	val = (long) itm->ptr;
	flt = (long double) val;
	return wycc_box_float(flt);
    } 
    WY_PANIC("Help needed in wyil_convert_real w/ %d\n", itm->typ)
}

/*
 * given a numeric object return the size of the number
 * (how many longs it takes to represent it)
 */
int wycc_wint_size(wycc_obj *itm) {
    WY_OBJ_SANE(itm, "wycc_wint_size");

    if (itm->typ == Wy_Int) {
	return (size_t) 1;
    };
    if (itm->typ == Wy_Byte) {
	return (size_t) 1;
    };
    if (itm->typ == Wy_Char) {
	return (size_t) 1;
    };
    WY_PANIC("Help needed in wint_size for type %d\n", itm->typ)
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
 * rudimentary sub operation
 */
wycc_obj* wyil_sub(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_sub lhs");
    WY_OBJ_SANE(rhs, "wyil_sub rhs");
    long rslt;
    int lc, rc, ac;

    if ((lhs->typ == Wy_Float) || (rhs->typ == Wy_Float)){
	return wyil_sub_ld(lhs, rhs);
    };
    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) - ((long)rhs->ptr); 
	return wycc_box_type_match(lhs, rhs, rslt);
    };
    WY_PANIC("Help needed in sub for wide ints (%d)\n", ac)
}

/*
 * rudimentary add operation
 */
wycc_obj* wyil_add(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_add lhs");
    WY_OBJ_SANE(rhs, "wyil_add rhs");
    long rslt;
    int lc, rc, ac;

    if ((lhs->typ == Wy_Float) || (rhs->typ == Wy_Float)){
	return wyil_add_ld(lhs, rhs);
    };
    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) + ((long)rhs->ptr); 
	return wycc_box_type_match(lhs, rhs, rslt);
    };
    WY_PANIC("Help needed in add for wide ints (%d)\n", ac)
}

/*
 * rudimentary negation operation
 */
wycc_obj* wyil_negate(wycc_obj* itm){
    WY_OBJ_SANE(itm, "wyil_negate");
    long double ld;
    long double *ldp;

    if (itm->typ == Wy_Int) {
	return wycc_box_long(-((long) itm->ptr));
    }
    if (itm->typ == Wy_Byte) {
	return wycc_box_byte(-((long) itm->ptr));
    }
    if (itm->typ == Wy_Char) {
	return wycc_box_char(-((long) itm->ptr));
    }
    if (itm->typ == Wy_Float) {
	ldp = (long double *) itm->ptr;
	ld = *ldp * (-1.0);
	return wycc_box_float(ld);
    }
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
    int lc, rc, ac;
    /* wycc_obj* ans;*/

    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) & ((long)rhs->ptr);
	return wycc_box_type_match(lhs, rhs, rslt);
    };
    WY_PANIC("Help needed in bit_and for wide ints (%d)\n", ac)
}

/*
 * rudimentary ior operation
 */
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
	return wycc_box_type_match(lhs, rhs, rslt);
    };
    WY_PANIC("Help needed in bit_or for wide ints (%d)\n", ac)
}

/*
 * rudimentary xor operation
 */
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
	return wycc_box_type_match(lhs, rhs, rslt);
    };
    WY_PANIC("Help needed in bit_xor for wide ints (%d)\n", ac)
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
 * rudimentary divide operation
 */
wycc_obj* wyil_div(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_div lhs");
    WY_OBJ_SANE(rhs, "wyil_div rhs");
    long rslt;
    int lc, rc, ac;

    if ((lhs->typ == Wy_Float) || (rhs->typ == Wy_Float)){
	return wyil_div_ld(lhs, rhs);
    };
    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac < 2) {
	rslt = ((long) lhs->ptr) / ((long)rhs->ptr); 
	return wycc_box_type_match(lhs, rhs, rslt);
    };
    WY_PANIC("Help needed in div for wide ints (%d)\n", ac)
}

/*
 * rudimentary modulo operation
 */
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
	return wycc_box_type_match(lhs, rhs, rslt);
    };
    WY_PANIC("Help needed in mod for wide ints (%d)\n", ac)
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
 * rudimentary multiply operation
 */
wycc_obj* wyil_mul(wycc_obj* lhs, wycc_obj* rhs){
    WY_OBJ_SANE(lhs, "wyil_mul lhs");
    WY_OBJ_SANE(rhs, "wyil_mul rhs");
    long rslt;
    int lc, rc, ac;

    if ((lhs->typ == Wy_Float) || (rhs->typ == Wy_Float)){
	return wyil_mul_ld(lhs, rhs);
    };
    lc = wycc_wint_size(lhs);
    rc = wycc_wint_size(rhs);
    ac = (lc > rc) ? lc : rc;
    if (ac > 1) {
	WY_PANIC("Help needed in mul for wide ints (%d)\n", ac)
    };
    rslt = (long) lhs->ptr;
    lc = wycc_ilog2(rslt);
    rc = wycc_ilog2((long) rhs->ptr);
    if ((lc + rc) > 31) {
	WY_PANIC("Help needed in mul for wide ints (2)\n")
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
	WY_PANIC("Help needed in shift_up for wide ints (%d)\n", lc)
    }
    rslt = (long) rhs->ptr;
    if (rslt > 60*8) {
	fprintf(stderr, "ERROR shift to exceed memory\n");
	exit(-4);
    }
    if (rslt > 60) {
	rslt += 63;
	rslt /= 64;
	WY_PANIC("Help needed in shift_up for wide ints (%d)\n", rslt)
    }
    rc = rslt;
    rslt = (long) lhs->ptr;
    rslt <<= rc;
    return wycc_box_type_match(lhs, rhs, rslt);
    //return wycc_box_long(rslt);
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
	WY_PANIC("Help needed in shift_up for wide ints (%d)\n", lc)
    }
    rslt = (long) rhs->ptr;
    if (rslt > 64) {
	rslt = 0;
	return wycc_box_long(rslt);
    }
    rc = rslt;
    rslt = (long) lhs->ptr;
    rslt >>= rc;
    return wycc_box_type_match(lhs, rhs, rslt);
    //return wycc_box_long(rslt);
}

/*
 * given a record, step thru every slot pairing with its meta data
 */
wycc_obj *wycc__toString_wint(wycc_obj *itm){
    WY_OBJ_SANE(itm, "wycc__toString_wint");

    WY_PANIC("Help needed in wycc__toString_wint\n")
}



/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
