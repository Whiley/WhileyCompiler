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

public type normalised is (real x)
    where 0.0 <= x && x <= 1.0

public function toString(real item) -> string:
    return Any.toString(item)

// Convert a string into an integer
public function parse(string input) -> real|null:
    //
    int r = 0
    int dps = 0
    for i in 0..|input|:
        char c = input[i]
        if c == '.' && dps == 0:
            dps = 1
        else if !ASCII.isDigit(c):
            return null
        else:
            r = r * 10
            r = r + (int) (c - '0')
            dps = dps * 10
    // finally, perform division
    real rr = (real) r
    if dps > 0:
        return rr / (real) dps
    else:
        return rr

// print real number to 10dp
public function toDecimal(real x) -> string:
    return toDecimal(x,10)

// the following is a convenience method.
public function toDecimal(real x, int ndigits) -> string:
    string r
    if x < 0.0:
        r = "-"
        x = -x
    else:
        r = ""
    int n / int d = x
    char digit = (char) n / d
    real rem = x - (real) digit
    r = r ++ [digit] ++ "."
    int i = 1
    while i < ndigits && rem != 0.0:
        rem = rem * 10.0
        n / d = rem
        digit = (char) n / d
        rem = rem - (real) digit
        r = r ++ [digit]
        i = i + 1
    // need to support rounding!
    return r
