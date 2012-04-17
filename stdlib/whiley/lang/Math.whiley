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

// return absolute value
int abs(int x):
    if x < 0:
        return -x
    else:
        return x

real abs(real x):
    if x < 0:
        return -x
    else:
        return x

// return larger of two values
int max(int a, int b):
    if a < b:
        return b
    else:
        return a

real max(real a, real b):
    if a < b:
        return b
    else:
        return a

// return small of two values
int min(int a, int b):
    if a > b:
        return b
    else:
        return a

real min(real a, real b):
    if a > b:
        return b
    else:
        return a

// not sure what to do with negative exponents
int pow(int base, int exponent) requires exponent > 0:
    r = 1
    for i in 0 .. exponent:
        r = r * base
    return r
    
// round an arbitrary number x to the largest integer
// not greater than x .
int floor(real x):
    num,den = x
    r = num / den  
    if x < 0 && den != 1: 	 
        return r - 1 
    return r 

    
// round an arbitrary number x to the smallest integer
// not smaller than x.
int ceil(real x):
    num,den = x
    r = num / den  
    if x > 0 && den != 1: 	 
        return r + 1 
    return r 

// round an arbitrary number to the nearest integer, following the
// "round half away from zero" protocol.
int round(real x):
    if x >= 0:
        return floor(x+0.5)
    else:
        return ceil(x-0.5)

// The constant PI to 20 decimal places.  Whilst this is clearly an
// approximation, it should be sufficient for most purposes.
define PI as 3.14159265358979323846 

// Based on an excellent article entitled "Integer Square Roots" 
// by Jack W. Crenshaw, published in the eetimes, 1998.
int isqrt(int x) requires x >= 0, ensures $ >= 0:
    square = 1
    delta = 3
    while square <= x:
        square = square + delta
        delta = delta + 2
    return (delta/2) - 1

// The following is a first approximation at this.  It computes the
// square root of a number to within a given error threshold.
public native real sqrt(int x, real error) requires x >= 0, ensures $ >= 0.0:

public real sqrt(real x, real error) requires x >= 0.0, ensures $ >= 0.0:
    numerator,denominator = x
    return sqrt(numerator,error) / sqrt(denominator,error)
