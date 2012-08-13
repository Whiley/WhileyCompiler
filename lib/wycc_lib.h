/*
 * wycc_lib.h
 *
 * This is a a header file that describes the
 * library of support routines for programs written in
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

typedef struct Wycc_object {
    int typ;
    int cnt;
    void* ptr;
} wycc_obj;

typedef struct Wycc_Init_Routine_Link {
    void (*function)();
    //wycc_initor* nxt;
    struct Wycc_Init_Routine_Link* nxt;
} wycc_initor;

wycc_initor* wycc_init_chain;
int	wycc_debug_flag;

/*
 * routines used by wycc for structure and bookkeeping
 */
/* void wycc_main(); */
void wycc_main(wycc_obj* sys);
wycc_obj* wycc_deref_box(wycc_obj* itm, int flg);
wycc_obj* wycc_box_str(char* text);
wycc_obj* wycc_box_int(int x);
wycc_obj* wycc_box_long(long x);
wycc_obj* wycc_list_new(long siz);
wycc_obj* wycc_list_get(wycc_obj* lst, long at);
void wycc_list_add(wycc_obj* lst, wycc_obj* itm);
wycc_obj* wycc_set_new(int typ);
void wycc_set_add(wycc_obj* lst, wycc_obj* itm);
wycc_obj* wycc_map_new(int typ);
void wycc_map_add(wycc_obj* lst, wycc_obj* key, wycc_obj* itm);

/*
 * routines to implement wyil operations
 */
void wyil_debug_str(char* mesg);
void wyil_debug_obj(wycc_obj* ptr);
wycc_obj* wyil_strappend(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_add(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_sub(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_bit_and(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_bit_ior(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_bit_xor(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_mul(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_div(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_shift_up(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_shift_down(wycc_obj* lhs, wycc_obj* rhs);
wycc_obj* wyil_length_of(wycc_obj* itm);
wycc_obj* wyil_index_of(wycc_obj* lhs, wycc_obj* rhs);


/*
 * routines to implement whiley standard library
 */
wycc_obj* toString(wycc_obj* itm);
void wycc_println(wycc_obj* sys, wycc_obj* itm);

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
