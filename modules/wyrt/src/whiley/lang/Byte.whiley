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

import string from whiley.lang.ASCII
import uint from whiley.lang.Int

// convert a byte into a string
public function toString(byte b) -> string:
    string r = [0; 'b']
    int i = 0
    while i < 8:
        if (b & 00000001b) == 00000001b:
            r[7-i] = '1'
        else:
            r[7-i] = '0'
        b = b >> 1
        i = i + 1
    return r

// Convert a byte into an unsigned int.  This assumes a little endian
// encoding.
public function toUnsignedInt(byte b) -> uint:
    int r = 0
    int base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    return r

// Convert a byte array into an unsigned int assuming a little endian
// form for both individual bytes, and the array as a whole
public function toUnsignedInt([byte] bytes) -> uint:
    int val = 0
    int base = 1
    int i = 0
    while i < |bytes|:
        int v = toUnsignedInt(bytes[i]) * base
        val = val + v
        base = base * 256
        i = i + 1
    return val

// Convert a byte into an unsigned int.  This assumes a little endian
// encoding.
public function toInt(byte b) -> int:
    int r = 0
    int base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    // finally, add the sign
    if r >= 128:
        return -(256-r)
    else:
        return r

// Convert a byte array into a signed int assuming a little endian
// form for both individual bytes, and the array as a whole
public function toInt([byte] bytes) -> int:
    int val = 0
    int base = 1
    int i = 0
    while i < |bytes|:
        int v = toUnsignedInt(bytes[i]) * base
        val = val + v
        base = base * 256
        i = i + 1
    // finally, add the sign
    if val >= (base/2):
        return -(base-val)
    else:
        return val
