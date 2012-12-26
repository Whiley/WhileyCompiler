/*
 * chunk.h
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

void wycc_chunk_ptr_fill(struct chunk_ptr *ptr, wycc_obj *itm, int typ);
void wycc_chunk_ptr_inc(struct chunk_ptr *chunk);
void wycc_chunk_ptr_fill_as(struct chunk_ptr *ptr, wycc_obj *itm);

