/*
 * wycc_main.c
 *
 * This is part of a library of support routines for programs written in
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
#include "common.h"
#include "FOM.h"

/*
 * just in case we need to refer to them from some routine other than main
 */
static int	orig_argc;
static char**	orig_argv;
static char**	orig_envp;

int	wycc_experiment_flag = 0;
wycc_obj*	exception_thrown = NULL;
char*		exception_monitor = NULL;


/*
 * wycc+wyil needs a unix/c standard starting routine.
 * **** to be changed ****
 * * whiley may have more than one main (with different signatures
 *    and may need to have them registered from each .o file linked in.
 * * these routines and all the other .o files need to agree about
 *    various type information.
 */
int main(int argc, char** argv, char** envp) {
    static wycc_obj *mt_string;
    int idx;
    char* argp;
    wycc_initor* ini;
    wycc_obj* sys;
    wycc_obj* lst;
    wycc_obj* itm;
    void **p;
    void *rtn;

    /*
     * process the command line arguments
     */
    orig_argc = argc;
    orig_argv = argv;
    orig_envp = envp;
    wycc_debug_flag = 0;
    wycc_experiment_flag = 0;
    for (idx=1; idx < argc ; idx++) {
	argp = argv[idx];
	if (strcmp(argp, "-D") == 0) {
	    wycc_debug_flag = 1;
	    continue;
	};
	if (strcmp(argp, "-X") == 0) {
	    wycc_experiment_flag = 1;
	    continue;
	};
    };
    /*
     * Handle all the initalisations.
     */
    if (wycc_debug_flag) {
	if (wycc_init_chain == NULL) {
	    fprintf(stderr, "wycc_init_chain is null\n");
	};
    };
    wycc_type_init();

    /*
     * register the known routines (FOM == function or method)
     * and the record types
     */
    //list_FOM = wycc_list_new(8);
    mt_string = wycc_box_cstr("");
    map_FOM = wycc_map_new(-1);
    wycc_map_add(map_FOM, mt_string, mt_string);
    wycc_register_lib();

    for (ini= wycc_init_chain; ini != NULL; ) {
	ini->functionr();
	ini = (wycc_initor*)ini->nxt;
    };
    /*
     * answer the searches for specific routines
     */
    for (ini= wycc_init_chain; ini != NULL; ) {
	ini->functionq();
	ini = (wycc_initor*)ini->nxt;
    };
    //sys = wycc_box_int(1);	/* **** KLUDGE **** */
    /* **** need to build recrec for system */
    /*
     * call the main routines
     */
    itm = wycc_box_token(1);
    sys = wycc_rrecord_new(2);
    wycc_record_fill(sys, 1, itm);
    wycc_deref_box(itm);
    lst = wycc_list_new(argc-1);
    for (idx=1; idx < argc ; idx++) {
	itm = wycc_box_cstr(argv[idx]);
	wycc_list_add(lst, itm);
    }
    wycc_record_fill(sys, 0, lst);
    //    wycc__main(sys);
    itm = wycc_fom_handle("main", "[:v,v,[{args,out}[#s],[.a]]]");
    if (itm != NULL) {
	p = (void **) itm->ptr;
	rtn = p[3];
	itm = ((FOM_1a) rtn)(sys);
    };
    fflush(stderr);
    fflush(stdout);
    exit(0);
    return 0;
}

/*
;;; Local Variables: ***
;;; c-basic-offset: 4 ***
;;; End: ***
 */
