define ZipFile as { [ZipEntry] entries }

// Create a ZipFile structure from an array of bytes which represent
// the zip file.
ZipFile createZipFile([byte] data):
    header = int(data[0..4])
    return { entries: [] }
    
