define ZIP_HEADER as 0x04034b50
define ZipError as { string msg, int offset }
define ZipEntry as { string file }
define ZipFile as { int version, [ZipEntry] entries }

// Create a ZipFile structure from an array of bytes which represent
// the zip file.
ZipError|ZipFile zipFile([byte] data):
    header = le2uint(data[0..4])
    if header != ZIP_HEADER:
        return { msg: "invalid zip header", offset: 0 }
    ver = le2uint(data[4..6])
    print str(ver)
    return { version: ver, entries: [] }
    
