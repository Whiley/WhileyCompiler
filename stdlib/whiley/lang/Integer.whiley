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

// Convert an integer into a hex string
public string hexStr(int item):    
    r = []
    while item > 0:
        v = item / 16
        w = item - (v*16)
        if w <= 9:                
            r = ['0' + w] + r
        else:
            w = w - 10
            r = ['a' + w] + r
        item = v
    //    return r
    return "FIX HEX STR"

// Convert a byte into an unsigned int assuming a little endian
// orientation.
int le2uint(byte b):
    r = 0
    base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    return r    

// Convert a string into an integer
int str2int(string input) throws Error:
    r = 0
    for i in 0..|input|:
        c = input[i]
        r = r * 10
        if !isDigit(c):
            throw { msg: "invalid number string (" + str(input) + ")" }
        r = r + (c - '0')
    return r

/*
// Convert a byte array in little endian form into an unsigned int
int le2uint([byte] bytes):
    idx = 0
    val = 0
    base = 1
    while idx < |bytes|:
        val = val + (bytes[idx] * base)
        base = base * 256
        idx = idx + 1
    return val

// Convert a byte array in big endian form into an unsigned int
int be2uint([byte] bytes):
    idx = |bytes|
    val = 0
    base = 1
    while idx > 0:
        idx = idx - 1
        val = val + (bytes[idx] * base)
        base = base * 256
    return val

// Convert a byte array into an array of bits where, for each byte,
// the least significant bit comes first.
[bool] lsf2bits([byte] bytes):
    bits = []
    for b in bytes:
        bits = bits + lsf2bits(b)
    return bits

// Convert a byte into an array of bits where the least significant
// bit of the byte comes first.  E.g. byte value 1 gives [TFFFFFFF]
[bool] lsf2bits(byte b):
    // this is a tad ugly.
    b8 = b > 128
    if b8:
        b = b - 128
    b7 = b >= 64
    if b7:
        b = b - 64
    b6 = b >= 32
    if b6:
        b = b - 32
    b5 = b >= 16
    if b5:
        b = b - 16
    b4 = b >= 8
    if b4:
        b = b - 8
    b3 = b >= 4
    if b3:
        b = b - 4
    b2 = b >= 2
    if b2:
        b = b - 2
    b1 = b == 1    
    return [b1,b2,b3,b4,b5,b6,b7,b8]

*/
