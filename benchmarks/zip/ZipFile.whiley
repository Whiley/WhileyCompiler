define ZIP_LOCAL_HEADER as 0x04034b50

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
    
