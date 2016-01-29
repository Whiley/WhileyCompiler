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

/**
 * Return absolute value of integer variable.
 */
public function abs(int x) -> (int r)
// if input positive, then result equals input
ensures x >= 0 ==> r == x
// if input negative, then result equals negated input
ensures x < 0 ==> r == -x:
    //
    if x < 0:
        return -x
    else:
        return x

/**
 * Return maximum of two integer variables
 */
public function max(int a, int b) -> (int r)
// Return cannot be smaller than either parameter
ensures r >= a && r >= b
// Return value must equal one parameter
ensures r == a || r == b:
    //
    if a < b:
        return b
    else:
        return a

/**
 * Return minimum of two integer variables
 */
public function min(int a, int b) -> (int r)
// Return cannot be greater than either parameter
ensures r <= a && r <= b
// Return value must equal one parameter
ensures r == a || r == b:
    //
    if a > b:
        return b
    else:
        return a

/**
 * Return integer value raised to a given power.
 */
public function pow(int base, int exponent) -> int
// Exponent cannot be negative
requires exponent > 0:
    //
    int r = 1
    int i = 0
    while i < exponent:
        r = r * base
        i = i + 1
    return r

// Based on an excellent article entitled "Integer Square Roots"
// by Jack W. Crenshaw, published in the eetimes, 1998.
public function isqrt(int x) -> (int r)
requires x >= 0
ensures r >= 0:
    //
    int square = 1
    int delta = 3
    while square <= x:
        square = square + delta
        delta = delta + 2
    return (delta/2) - 1