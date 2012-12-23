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

// =================================================================
// Byte buffer
// =================================================================

// A buffer provides an in memory store of data items which can be used 
// to construct a Reader, Writer or File.

define State as {
    [byte] data,
    int pos
} where pos >= 0 && pos <= |data|

public define Buffer as ref State

// public Buffer ::Buffer():
//     return new { pos: 0, data: [] }

// public Buffer ::Buffer([byte] data):
//     return new { pos: 0, data: data }

public Buffer ::Buffer([byte] data, int pos):
    return new { pos: pos, data: data }

public [byte] ::read(Buffer this, int amount):
    start = this->pos
    // first, calculate how much can be read
    end = start + Math.min(amount,|this->data| - start)
    // second, update bytes pointer
    this->pos = end
    // third, return bytes read
    return this->data[start .. end]

public int ::write(Buffer this, [byte] bytes):
    // FIXME: handle position correctly?
    this->data = this->data + bytes
    return |bytes|

public public bool ::hasMore(Buffer this):
    return this->pos < |this->data|

public int ::available(Buffer this):
    return |this->data| - this->pos

public void ::close(Buffer this):
    this->pos = |this->data|

public void ::flush(Buffer this):
    skip

// =================================================================
// Buffer Reader
// =================================================================

// Create an Reader from a list of bytes.
public Reader ::asReader(Buffer this):
    return {
        read: &(int x -> read(this,x)),
        hasMore: &hasMore(this),
        close: &close(this),
        available: &available(this)
    }

// =================================================================
// Buffer Writer
// =================================================================

public Writer ::asWriter(Buffer this):
    return {
        write: &([byte] x -> write(this,x)),
        flush: &flush(this),
        close: &close(this)
    }

// =================================================================
// As File
// =================================================================
