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

// Resize an array to a given size
public function resize(int[] items, int size, int element) -> (int[] nitems)
// Required size cannot be negative
requires size >= 0
// Returned array is of specified size
ensures |nitems| == size
// If array is enlarged, the all elements up to new size match
ensures all { i in 0 .. |items| | i >= size || nitems[i] == items[i] }
// All new elements match given element
ensures all { i in |items| .. size | nitems[i] == element}:
    //
    int[] nitems = [element; size]
    int i = 0
    while i < size && i < |items|
    where i >= 0 && |nitems| == size
    // All elements up to i match as before
    where all { j in 0..i | nitems[j] == items[j] }
    // All elements about size match element
    where all { j in |items| .. size | nitems[j] == element}:
        //
        nitems[i] = items[i]
        i = i + 1
    //
    return nitems

// find first index in list which matches character.  If no match,
// then return null.
public function indexOf(int[] items, int item) -> (int|null index)
// If int returned, element at this position matches item
ensures index is int ==> items[index] == item
// If int returned, element at this position is first match
ensures index is int ==> no { i in 0 .. index | items[i] == item }
// If null returned, no element in items matches item
ensures index is null ==> no { i in 0 .. |items| | items[i] == item }:
    //
    return indexOf(items,item,0)

// find first index after a given start point in list which matches character.
// If no match, then return null.
public function indexOf(int[] items, int item, int start) -> (int|null index)
// Starting point cannot be negative
requires start >= 0
// If int returned, element at this position matches item
ensures index is int ==> items[index] == item
// If int returned, element at this position is first match
ensures index is int ==> no { i in start .. index | items[i] == item }
// If null returned, no element in items matches item
ensures index is null ==> no { i in start .. |items| | items[i] == item }:
    //
    int i = start
    //
    while i < |items|
    // i is positive
    where i >= 0
    // No element seen so far matches item
    where no { j in start .. i | items[j] == item }:
        //
        if items[i] == item:
            return i
        i = i + 1
    //
    return null

// find last index in list which matches character.  If no match,
// then return null.
public function lastIndexOf(int[] items, int item) -> (int|null index)
// If int returned, element at this position matches item
ensures index is int ==> items[index] == item
// If int returned, element at this position is last match
ensures index is int ==> no { i in (index+1) .. |items| | items[i] == item }
// If null returned, no element in items matches item
ensures index is null ==> no { i in 0 .. |items| | items[i] == item }:
    //
    int i = |items|
    //
    while i > 0
    where i <= |items|
    // No element seen so far matches item
    where no { j in i..|items| | items[j] == item }:
        //
        i = i - 1
        if items[i] == item:
            return i
    //
    return null

// replace all occurrences of "old" with "new" in list "items".
public function replace(int[] items, int old, int n) -> (int[] r)
// Every position in items matching old replaced with n
ensures all { i in 0..|items| | (items[i] == old) ==> r[i] == n }
// Every other position remains the same
ensures all { i in 0..|items| | (items[i] != old) ==> r[i] == items[i] }
// Size of resulting array remains the same
ensures |items| == |r|:
    //
    int i = 0
    int[] oldItems = items // ghost
    //
    while i < |items|
    where i >= 0 && |items| == |oldItems|
    where all { k in 0..i | (oldItems[k] == old) ==> (items[k] == n) }
    where all { k in 0..|items| | (oldItems[k] != old) ==> (items[k] == oldItems[k]) }:
        if oldItems[i] == old:
            items[i] = n
        i = i + 1
    //
    return items

// Extract slice of items array between start and up to (but not including) end.
public function slice(int[] items, int start, int end) -> (int[] r)
// Given region to slice must make sense
requires start >= 0 && start <= end && end <= |items|
// Size of slice determined by difference between start and end
ensures |r| == (end - start)
// Items returned in slice match those in region from start
ensures all { i in 0..|r| | items[i+start] == r[i] }:
    //
    int[] r = [0; end-start]
    int i = 0
    //
    while i < |r|
    where i >= 0 && |r| == (end-start)
    where all { k in 0..i | r[k] == items[k+start] }:
        r[i] = items[i+start]
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

public function append(int[] items, int item) -> (int[] r)
// Every item from original array is retained
ensures all { k in 0..|items| | r[k] == items[k] }
// Last item in result matches item appended
ensures r[|items|] == item
// Size of array is one larger than original
ensures |r| == |items|+1:
    //
    int[] nitems = [0; |items| + 1]
    int i = 0
    //
    while i < |items| 
    where i >= 0 && i <= |items| && |nitems| == |items|+1
    where all { k in 0..i | nitems[k] == items[k] }:
        nitems[i] = items[i]
        i = i + 1
    //
    nitems[i] = item    
    //
    return nitems

public function append(int item, int[] items) -> (int[] r)
// Every item from original array is retained
ensures all { k in 0..|items| | r[k+1] == items[k] }
// First item in result matches item appended
ensures r[0] == item
// Size of array is one larger than original
ensures |r| == |items|+1:
    //
    int[] nitems = [0; |items| + 1]
    int i = 0
    //
    while i < |items| 
    where i >= 0 && i <= |items| && |nitems| == |items|+1
    where all { k in 0..i | nitems[k+1] == items[k] }:
        nitems[i+1] = items[i]
        i = i + 1
    //
    nitems[0] = item    
    //
    return nitems

public function append(bool[] lhs, bool[] rhs) -> bool[]:
    bool[] rs = [false; |lhs| + |rhs|]
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

public function append(bool[] items, bool item) -> bool[]:
    bool[] nitems = [false; |items| + 1]
    int i = 0
    //
    while i < |items|:
        nitems[i] = items[i]
        i = i + 1
    //
    nitems[i] = item    
    //
    return nitems

public function append(bool item, bool[] items) -> bool[]:
    bool[] nitems = [false; |items| + 1]
    int i = 0
    //
    while i < |items|:
        nitems[i+1] = items[i]
        i = i + 1
    //
    nitems[0] = item    
    //
    return nitems

public function copy(int[] src, int srcStart, int[] dest, int destStart, int length) -> (int[] result)
// Source array must contain enough elements to be copied
requires (srcStart + length) <= |src|
// Destination array must have enough space for copied elements
requires (destStart + length) <= |dest|
// Result is same size as dest
ensures |result| == |dest|
// All elements before copied region are same
ensures all { i in 0 .. destStart | dest[i] == result[i] }
// All elements in copied region match src
ensures all { i in 0 .. length | dest[i+destStart] == src[i+srcStart] }
// All elements above copied region are same
ensures all { i in (destStart+length) .. |dest| | dest[i] == result[i] }:
    //
    int i = srcStart
    int j = destStart
    int srcEnd = srcStart + length
    //
    while i < srcEnd:
        dest[j] = src[i]
        i = i + 1
        j = j + 1
    //
    return dest

