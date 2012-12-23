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
// Generic Reader
// =================================================================

// A generic reader represents an input stream of items (e.g. bytes or 
// characters), such as from a file, network socket, or a memory buffer.  

public define Reader as {

    // Reads at most a given number of bytes from the stream.  This
    // operation may block if the number requested is greater than that
    // available.
    [byte] ::read(int),

    // Check whether the end-of-stream has been reached and, hence,
    // that there are no further bytes which can be read.
    bool ::hasMore(),

    // Closes this input stream thereby releasin any resources
    // associated with it.
    void ::close(),

    // Return the number of bytes which can be safely read without
    // blocking.
    int ::available(),

    // Space for additional operations defined by refinements of
    // Reader
    ...        
}

// =================================================================
// In-Memory Byte Buffer
// =================================================================

define ByteInputBuffer as ref {
    int pos,
    [byte] bytes
}

// Create an Reader from a list of bytes.
public Reader ::fromBytes([byte] bytes):
    this = new { pos: 0, bytes: bytes }
    return {
        read: &(int x -> bb_read(this,x)),
        hasMore: &bb_hasMore(this),
        close: &bb_close(this),
        available: &bb_available(this)
    }

[byte] ::bb_read(ByteInputBuffer this, int amount):
    start = this->pos
    // first, calculate how much can be read
    end = start + Math.min(amount,|this->bytes| - start)
    // second, update bytes pointer
    this->pos = end
    // third, return bytes read
    return this->bytes[start .. end]

bool ::bb_hasMore(ByteInputBuffer this):
    return this->pos < |this->bytes|

void ::bb_close(ByteInputBuffer this):
    this->pos = |this->bytes|

int ::bb_available(ByteInputBuffer this):
    return |this->bytes| - this->pos







