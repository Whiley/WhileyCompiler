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
 * Return absolute value of real variable.
 */
public function abs(real x) -> (real r)
// if input positive, then result equals input
ensures x >= 0.0 ==> r == x
// if input negative, then result equals negated input
ensures x < 0.0 ==> r == -x:
    //
    if x < 0.0:
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
 * Return maximum of two real variables
 */
public function max(real a, real b) -> (real r)
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
 * Return minimum of two real variables
 */
public function min(real a, real b) -> (real r)
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
    for i in 0 .. exponent:
        r = r * base
    return r

/**
 * Return largest integer which is less-than-or-equal to
 * the given value
 */
public function floor(real x) -> (int r)
// Return is greater-than-or-equal to input
ensures ((real) r) <= x
// Input value is between return and return plus one
ensures ((real) r + 1) > x:
    //
    int num
    int den
    num/den = x
    int r = num / den
    if x < 0.0 && den != 1:
        return r - 1
    else:
        return r

/**
 * Return smallest integer which is greater-than-or-equal to
 * the given value
 */
public function ceil(real x) -> (int r)
// Return is greater-than-or-equal to input
ensures x <= ((real) r)
// Input value is between return and return less one
ensures ((real) r - 1) < x:
    //
    int num
    int den
    num/den = x
    int r = num / den
    if x > 0.0 && den != 1:
        return r + 1
    else:
        return r

/**
 * Round an arbitrary number to the nearest integer,
 * following the "round half away from zero" protocol.
 */
public function round(real x) -> (int r)
// Difference between input and return is 0.5 or less
ensures max(x,(real) r) - min(x, (real) r) <= 0.5:
    //
    if x >= 0.0:
        return floor(x + 0.5)
    else:
        return ceil(x - 0.5)

/**
 * The constant PI to 20 decimal places.  Whilst this is clearly an
 * approximation, it should be sufficient for most purposes.
 */
constant PI is 3.14159265358979323846

constant E is 2.718281828459045

constant ERROR is 0.00000000000000000001

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

// The following is a first approximation at this.  It computes the
// square root of a number to within a given error threshold.
public native function sqrt(int x, real error) -> (real r)
    requires x >= 0
    ensures r >= 0.0

public function sqrt(real x, real error) -> (real r)
requires x >= 0.0
ensures r >= 0.0:
    //
    int numerator
    int denominator
    numerator/denominator = x
    return sqrt(numerator,error) / sqrt(denominator,error)
