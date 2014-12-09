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

package whiley.io

import whiley.lang.*

// =================================================================
// Byte buffer
// =================================================================

// A buffer provides an in memory store of data items which can be used
// to construct a Reader, Writer or File.

public type State is {
    [byte] data,
    int pos
} where pos >= 0 && pos <= |data|

public type Buffer is &State

public method Buffer() -> Buffer:
    return new { pos: 0, data: [] }

public method Buffer([byte] data) -> Buffer:
    return new { pos: 0, data: data }

public method Buffer([byte] data, int pos) -> Buffer:
    return new { pos: pos, data: data }

public method read(Buffer this, int amount) -> [byte]:
    int start = this->pos
    // first, calculate how much can be read
    int end = start + Math.min(amount,|this->data| - start)
    // second, update bytes pointer
    this->pos = end
    // third, return bytes read
    return this->data[start .. end]

public method write(Buffer this, [byte] bytes) -> int:
    // FIXME: handle position correctly?
    this->data = this->data ++ bytes
    return |bytes|

public method hasMore(Buffer this) -> bool:
    return this->pos < |this->data|

public method available(Buffer this) -> int:
    return |this->data| - this->pos

public method close(Buffer this):
    this->pos = |this->data|

public method flush(Buffer this):
    skip

// =================================================================
// Buffer Reader
// =================================================================

// Create an Reader from a list of bytes.
public method toReader(Buffer this) -> Reader:
    return {
        read: &(int x -> read(this,x)),
        hasMore: &( -> hasMore(this)),
        close: &( -> close(this)),
        available: &( -> available(this))
    }

// =================================================================
// Buffer Writer
// =================================================================

public method toWriter(Buffer this) -> Writer:
    return {
        write: &([byte] x -> write(this,x)),
        flush: &( -> flush(this)),
        close: &( -> close(this))
    }

// =================================================================
// As File
// =================================================================
