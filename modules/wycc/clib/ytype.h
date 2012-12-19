/*
 * ytype.h
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

void wycc_type_init();
void wycc_register_lib();
int wycc_type_internal(const char *nam);
int wycc_type_child_count(int tok);

/*
 * These are the defines and declarations needed for representing, parsing,
 * and otherwise handling of whiley data types
 */

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
