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

import * from whiley.lang.Errors

// The purpose of this module is to provide some standard types.
define i8 as int where $ >=-128 && $ <= 127
define i16 as int where $ >=-32768 && $ <= 32768
define i32 as int where $ >=-2147483648 && $ <= 2147483647
define i64 as int where $ >= -9223372036854775808 && $ <= 9223372036854775807

define u8 as int where $ >=0 && $ <= 255
define u16 as int where $ >= 0 && $ <= 65535
define u32 as int where $ >= 0 && $ <= 4294967295
define u64 as int where $ >= 0 && $ <= 18446744073709551615

define nat as int where $ >= 0

define digits as [
    '0','1','2','3','4','5','6','7','8','9',
    'a','b','c','d','e','f','g','h'
]

public string toString(int item):
    return Any.toString(item)

// Convert an integer into a hex string
public string toHexString(int item):    
    r = ""
    while item > 0:
        v = item / 16
        w = item % 16
        r = digits[w] + r
        item = v
    return r

// convert an integer into an unsigned byte
public byte toUnsignedByte(int v) requires 0 <= v && v <= 255:
    mask = 00000001b
    r = 0b
    for i in 0..8:
        if (v % 2) == 1:
            r = r | mask
        v = v / 2
        mask = mask << 1
    return r  

// convert an arbitrary sized unsigned integer into a list of bytes in
// little endian form.
public [byte] toUnsignedBytes(int v) requires v >= 0:
    bytes = []
    // do-while is needed here
    r = 0b
    mask = 00000001b
    for i in 0..8:
        if (v % 2) == 1:
            r = r | mask
        v = v / 2
        mask = mask << 1
    bytes = bytes + [r]
    while v > 0:
        r = 0b
        mask = 00000001b
        for i in 0..8:
            if (v % 2) == 1:
                r = r | mask
            v = v / 2
            mask = mask << 1
        bytes = bytes + [r]
    return bytes

// Convert a signed integer into a single byte
public byte toSignedByte(int v) requires -128 <= v && v <= 127:
    if v < 0:
        v = v + 256
    return Int.toUnsignedByte(v)
    

// parse a string representation of an integer value
public int parse(string input) throws SyntaxError:
    r = 0
    for i in 0..|input|:
        c = input[i]
        r = r * 10
        if !Char.isDigit(c):
            throw SyntaxError("invalid number string",i,i)
        r = r + (c - '0')
    return r
    
