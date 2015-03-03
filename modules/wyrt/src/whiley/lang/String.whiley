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

// Define the notion of an ASCII character and an ASCII string
type ASCII_char is (int x) where 0 <= x && x <= 255
type ASCII_string is [ASCII_char]

public function isUpperCase(ASCII_char c) -> bool:
    return 'A' <= c && c <= 'Z'

public function isLowerCase(ASCII_char c) -> bool:
    return 'a' <= c && c <= 'z'

public function isLetter(ASCII_char c) -> bool:
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')

public function isDigit(ASCII_char c) -> bool:
    return '0' <= c && c <= '9'

public function isWhiteSpace(ASCII_char c) -> bool:
    return c == ' ' || c == '\t' || c == '\n' || c == '\r'

// Convert a byte stream into a string using the standard ASCII
// encoding.
public function fromASCII([byte] data) -> ASCII_string:
    string r = ""
    for b in data:
        r = r ++ [Byte.toInt(b)]
    return r


