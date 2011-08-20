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

define FileReader as process { string fileName }

// not sure if this makes sense per se
FileReader ::openReader(string fileName):
    extern jvm:
        aload 0
        invokestatic wyjc/runtime/IO.openReader:(Ljava/lang/String;)Lwyjc/runtime/Actor;
        areturn
    // the following line is dead code
    return spawn {fileName: ""}

void FileReader::close():
    extern jvm:
        aload 0
        invokestatic wyjc/runtime/IO.closeFile:(Lwyjc/runtime/Actor;)V;

// read the whole file
[byte] FileReader::read():
    extern jvm:
        aload 0
        invokestatic wyjc/runtime/IO.readFile:(Lwyjc/runtime/Actor;)Lwyjc/runtime/List;
        areturn
    return []
    
// read at most max bytes 
[byte] FileReader::read(int max):
    extern jvm:
        aload 0
        aload 1
        invokestatic wyjc/runtime/IO.readFile:(Lwyjc/runtime/Actor;Ljava/math/BigInteger;)Lwyjc/runtime/List;
        areturn
    return []
