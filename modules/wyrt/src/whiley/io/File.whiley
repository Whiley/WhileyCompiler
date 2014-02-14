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

import uint from whiley.lang.Int

// ====================================================
// File Reader
// ====================================================
public type Reader is whiley.io.Reader

public method Reader(string fileName) => Reader:
    NativeFile this = createFileObject(filename)
    return {
        read: &(uint n => read(this,n)),
        hasMore: &( => hasMore(this)),
        close: &( => close(this)),
        available: &( => available(this))
    }

// ====================================================
// File Writer
// ====================================================
type Writer is whiley.io.Reader

public method Writer(string fileName) => Writer:
    NativeFile this = NativeFile(filename)
    return {
        write: &([byte] data => write(this,data)),
        close: &( => close(this)),
        flush: &( => flush(this))
    }

// ====================================================
// Native Implementation
// ====================================================

// Represents an unknown underlying data structure
type NativeFile is &any

private native method NativeFile(String filename) => NativeFile

// flush native file 
private native method flush(NativeFile f)

// close native file
private native method close(NativeFile f)

// determine how many bytes can be read without blocking
private native method available(NativeFile f) => uint

// read entire contents of native file
private native method read(NativeFile f) => [byte]
    
// read at most max bytes from native file
private native method read(NativeFile f, int max) => [byte]

// write entire contents of native file
private native method write(NativeFile f, [byte] data)
