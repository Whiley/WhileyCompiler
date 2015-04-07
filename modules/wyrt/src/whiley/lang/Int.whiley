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
import char from whiley.lang.ASCII

/**
 * Represents all signed integers representable in 8bits
 * of space in the two's complement representation.
 */
public type i8 is (int x)
    where x >=-128 && x <= 127

/**
 * Represents all signed integers representable in 16bits
 * of space in the two's complement representation.
 */
public type i16 is (int x)
    where x >=-32768 && x <= 32768

/**
 * Represents all signed integers representable in 32bits
 * of space in the two's complement representation.
 */
public type i32 is (int x)
    where x >=-2147483648 && x <= 2147483647

/**
 * Represents all signed integers representable in 64bits
 * of space in the two's complement representation.
 */
public type i64 is (int x)
    where x >= -9223372036854775808 && x <= 9223372036854775807

/**
 * Represents all unsigned integers representable in 8bits
 * of space.
 */
public type u8 is (int x)
    where x >=0 && x <= 255

/**
 * Represents all unsigned integers representable in 16bits
 * of space.
 */
public type u16 is (int x)
    where x >= 0 && x <= 65535

/**
 * Represents all unsigned integers representable in 32bits
 * of space.
 */
public type u32 is (int x)
    where x >= 0 && x <= 4294967295

/**
 * Represents all unsigned integers representable in 64bits
 * of space.
 */
public type u64 is (int x)
    where x >= 0 && x <= 18446744073709551615

/**
 * Represents all possible unsigned integers.
 */
public type uint is (int x) where x >= 0

public type nat is (int x) where x >= 0

constant digits is [
    '0','1','2','3','4','5','6','7','8','9',
    'a','b','c','d','e','f','g','h'
]

public function toString(int item) -> string:
    return Any.toString(item)

// Convert an integer into a hex string
public function toHexString(int item) -> string:
    string r = ""
    while item > 0:
        int v = item / 16
        int w = item % 16
        r = [digits[w]] ++ r
        item = v
    return r

// convert an integer into an unsigned byte
public function toUnsignedByte(u8 v) -> byte:
    //
    byte mask = 00000001b
    byte r = 0b
    for i in 0..8:
        if (v % 2) == 1:
            r = r | mask
        v = v / 2
        mask = mask << 1
    return r

// convert an arbitrary sized unsigned integer into a list of bytes in
// little endian form.
public function toUnsignedBytes(uint v) -> [byte]:
    //
    [byte] bytes = []
    // do-while is needed here
    byte r = 0b
    byte mask = 00000001b
    for i in 0..8:
        if (v % 2) == 1:
            r = r | mask
        v = v / 2
        mask = mask << 1
    bytes = bytes ++ [r]
    while v > 0:
        r = 0b
        mask = 00000001b
        for i in 0..8:
            if (v % 2) == 1:
                r = r | mask
            v = v / 2
            mask = mask << 1
        bytes = bytes ++ [r]
    return bytes

// Convert a signed integer into a single byte
public function toSignedByte(i8 v) -> byte:
    //
    if v < 0:
        v = v + 256
    return Int.toUnsignedByte(v)

// parse a string representation of an integer value
public function parse(string input) -> int|null:
    //
    // first, check for negative number
    int start = 0
    bool negative

    if input[0] == '-':
        negative = true
        start = start + 1
    else:
        negative = false
    // now, parse remaining digits
    int r = 0
    for i in start .. |input|:
        char c = input[i]
        r = r * 10
        if !ASCII.isDigit(c):
            return null
        r = r + ((int) c - '0')
    // done
    if negative:
        return -r
    else:
        return r

