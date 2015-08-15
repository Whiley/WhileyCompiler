// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package whiley.lang

// Increase up to a given size
public function enlarge(int[] list, int size, int element) -> int[]:
    int[] nlist = [0; size]
    int i = 0
    while i < size where i >= 0:
        nlist[i] = list[i]
        i = i + 1
    return nlist

// find first index in list which matches character.  If no match,
// then return null.
public function indexOf(int[] items, int c) -> int|null:
    int i = 0
    while i < |items|:
        if items[i] == c:
            return i
        i = i + 1
    return null

public function indexOf(int[] items, int c, int start) -> int|null:
    //
    int i = start
    while i < |items|:
        if items[i] == c:
            return i
        i = i + 1
    return null

// find last index in list which matches character.  If no match,
// then return null.
public function lastIndexOf(int[] items, int c) -> int|null:
    //
    int i = |items|
    while i > 0:
        i = i - 1
        if items[i] == c:
            return i
    return null

// replace all occurrences of "old" with "new" in list "items".
public function replace(int[] items, int old, int n) -> int[]:
    //
    int i = 0
    while i < |items|:
        if items[i] == old:
            items[i] = n
        i = i + 1
    return items

public function slice(int[] items, int start, int end) -> int[]:
    int[] r = [0; end-start]
    int i = start
    //
    while i < end:
        r[i-start] = items[i]
        i = i + 1
    //
    return r

public function append(int[] lhs, int[] rhs) -> int[]:
    int[] rs = [0; |lhs| + |rhs|]
    int i = |lhs|
    //
    while i > 0:
        i = i - 1
        rs[i] = lhs[i]
    //
    while i < |rhs|:
        rs[i+|lhs|] = rhs[i]
        i = i + 1
    //
    return rs

public function append(int[] items, int item) -> int[]:
    int[] nitems = [0; |items| + 1]
    int i = 0
    //
    while i < |items|:
        nitems[i] = items[i]
        i = i + 1
    //
    nitems[i] = item    
    //
    return nitems

public function append(int item, int[] items) -> int[]:
    int[] nitems = [0; |items| + 1]
    int i = 0
    //
    while i < |items|:
        nitems[i+1] = items[i]
        i = i + 1
    //
    nitems[0] = item    
    //
    return nitems
