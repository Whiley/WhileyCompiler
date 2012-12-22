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
// Output Stream
// =================================================================

// An InputStream represents an input stream of bytes, such as from a
// file, network socket, or a memory buffer.
public define OutputStream as {

    // Writes a given list of bytes to the output stream.
    int ::write([byte]),

    // Flush this output stream thereby forcing those items written
    // thus far to the output device.
    void ::flush(),

    // Closes this output stream thereby releasin any resources
    // associated with it.
    void ::close(),

    // Space for additional operations defined by refinements of
    // InputStream
    ...        
}

// =================================================================
// In-Memory Byte Buffer
// =================================================================

// Create an InputStream from a list of bytes.
public OutputStream ::toBytes(ref [byte] this):
    return {
        write: &([byte] x -> bb_write(this,x)),
        flush: &bb_flush(this),
        close: &bb_close(this)
    }

int ::bb_write(ref [byte] this, [byte] bytes):
    *this = *this + bytes
    return |*this|

void ::bb_flush(ref [byte] this):
    skip

void ::bb_close(ref [byte] this):
    skip

// =================================================================
// Printer
// =================================================================

// A printer provides a way of converting strings into bytes for writing to an OutputStream.
public define Printer as {

    // Closes this output stream thereby releasin any resources
    // associated with it.
    void ::close(),

    // Print a string 
    void ::print(string),

    // Print a string with a trailing newline
    void ::println(string)
}






