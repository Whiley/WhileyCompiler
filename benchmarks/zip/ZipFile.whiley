// Copyright (c) 2011, David J. Pearce
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
// DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// -----------------------------------------------------------------------------
// 
// Code for decompressing a zip file.  See the following for more
// information on the ZIP file format:
//
// http://www.pkware.com/documents/casestudies/APPNOTE.TXT

define ZIP_LOCAL_HEADER as 0x04034b50

// Compression Methods
define ZIP_COMPRESSION_NONE as 0
define ZIP_COMPRESSION_LZW as 1
define ZIP_COMPRESSION_REDUCED_1 as 2
define ZIP_COMPRESSION_REDUCED_2 as 3
define ZIP_COMPRESSION_REDUCED_3 as 4
define ZIP_COMPRESSION_REDUCED_4 as 5
define ZIP_COMPRESSION_IMPLODED_1 as 6
define ZIP_COMPRESSION_IMPLODED_2 as 7
define ZIP_COMPRESSION_IMPLODED_3 as 8
define ZIP_COMPRESSION_IMPLODED_4 as 9

define ZipError as { string msg, int offset }

define ZipFile as { [ZipEntry] entries }

define ZipEntry as { 
    int version,  // minimum version needed to extract
    int method,   // compression method
    int lastTime, // last modification time
    int lastDate, // last modification date
    int crc,      // 32bit CRC
    int size,     // compressed size
    int rawSize,  // uncompressed size
    string name,  // filename
    [byte] data   // raw data
}

// Create a ZipFile structure from an array of bytes which represent
// the zip file.
ZipError|ZipFile zipFile([byte] data):
    pos = 0
    es = []
    while pos < |data| && isLocalEntry(data,pos):
        r = parseEntry(data,pos)
        if r ~= ZipError:
            return r
        entry,pos = r
        es = es + [entry]   
    return { entries: es }

bool isLocalEntry([byte] data, int start):
    end = start + 4
    return end < |data| && 
        le2uint(data[start..end]) == ZIP_LOCAL_HEADER

ZipError|(ZipEntry,int) parseEntry([byte] data, int offset):
    header = data[offset..(offset+30)]
    namelen = le2uint(header[26..28])
    extralen = le2uint(header[28..30])
    datalen = le2uint(header[18..22])
    // calculate a few helpful offsets
    nameStart = offset+30
    nameEnd = nameStart + namelen
    dataStart = nameEnd + extralen
    dataEnd = dataStart + datalen
    // now create the entry
    return {
        version: le2uint(header[4..6]),
        method: le2uint(header[8..10]),
        lastTime: le2uint(header[10..12]),
        lastDate: le2uint(header[12..14]),
        crc: le2uint(header[14..18]),
        size: datalen,
        rawSize: le2uint(header[22..26]),
        name: data[nameStart..nameEnd],
        data: data[dataStart..dataEnd]
    },offset+30+namelen+extralen+datalen
    
