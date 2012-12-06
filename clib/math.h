/*
 * math.h
 *
 * This is a a header file that describes the
 * library of support routines for programs written in
 * the Whiley language when translated into C (ala gcc)
 * especially the math routines.
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

void wycc_wint_free(void *it);
void wycc_ratio_free(void *it);
int wycc_comp_wint(wycc_obj* lhs, wycc_obj* rhs);
int wycc_comp_int(wycc_obj* lhs, wycc_obj* rhs);
int wycc_comp_float(wycc_obj* lhs, wycc_obj* rhs);
int wycc_comp_ratio(wycc_obj* lhs, wycc_obj* rhs);
//wycc_obj* wycc_box_ratio(const char *txt);
wycc_obj* wyil_convert_int(wycc_obj* itm);
wycc_obj* wyil_convert_real(wycc_obj* itm);
wycc_obj *wycc__toString_wint(wycc_obj *itm);
wycc_obj *wycc__toString_ratio(wycc_obj *itm);

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
