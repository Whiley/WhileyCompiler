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

/*
 * just in case we need to refer to them from some routine other than main
 */
static int	orig_argc;
static char**	orig_argv;
static char**	orig_envp;

/*
 * storage for the type registry
 */

static char* wy_type_names[] = {
    "string",
    (char *) NULL
};

/*
 * wycc+wyil needs a unix/c standard starting routine.
 * **** to be changed ****
 * * whiley may have more than one main (with different signatures
 *    and may need to have them registered from each .o file linked in.
 * * these routines and all the other .o files need to agree about
 *    various type information.
 */
int main(int argc, char** argv, char** envp) {
    ;
    orig_argc = argc;
    orig_argv = argv;
    orig_envp = envp;
    wycc_main();
}

wycc_obj* wycc_box_str(char* text) {
    ;
    wycc_obj* ans;

    ans = (wycc_obj*) malloc(sizeof(wycc_obj));
    ans->typ = 1;
    ans->cnt = 0;
    ans->ptr = (void*) text;
    return ans;
}

static void wycc_dealloc_typ(void* ptr, int typ);
wycc_obj* wycc_deref_box(wycc_obj* itm, int flg) {
    ;
    void* ptr;
    int typ;

    if (--itm->cnt <= 0) {
        ;
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
    ;
    if (typ == 1) {
        free (ptr);
    } else {
        ;
	fprintf(stderr, "ERROR: unrecognized type (%d) in dealloc\n", typ);
	exit(-3);
    }
}


/*
 * ******************************
 * wyil opcode implementations
 */
void wyil_debug_str(char* mesg) {
    ;
    fprintf(stderr, "%s\n", mesg);
    return;
}

void wyil_debug_obj(wycc_obj* ptr1) {
    char* mesg;
    if (ptr1->typ == 1) {
        ;
	fprintf(stderr, "%s\n", ptr1->ptr);
    } else {
        ;
	fprintf(stderr, "Help needed in Debug for type %d\n", ptr1->typ);
    };
    return;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
