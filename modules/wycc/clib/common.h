/*
 * common.h
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

/*
 * these are the definitions and declarations that are needed for most of the
 * routines in the wycc_lib.a to interoperate, but should not be needed
 * (nor even used) directly by either the user's code or the whiley
 * compiler.
 */

#include <stdio.h>
#include <stdlib.h>

#define WY_SEG_FAULT		((wycc_obj *) (3))->cnt++;
//#define WY_PANIC(...) fprintf(stderr,__VA_ARGS__);exit(-3);
#define WY_PANIC(...) fprintf(stderr,__VA_ARGS__);WY_SEG_FAULT;


int	wycc_experiment_flag;
wycc_obj*	exception_thrown;
char*		exception_monitor;

int wycc_comp_gen(wycc_obj* lhs, wycc_obj* rhs);
int wycc_comp_list(wycc_obj* lhs, wycc_obj* rhs);

/*
 * kludges 
 */
void bp();

wycc_obj* wycc_index_of_map(wycc_obj* map, wycc_obj* key);
wycc_obj* wycc_box_addr(void* ptr);
int wycc_type_dealias(int typ);

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
