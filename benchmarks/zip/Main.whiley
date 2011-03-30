import whiley.io.*

void System::main([string] args):
    file = this->openFile(args[0])
    contents = file->read()
    zf = zipFile(contents)
    if zf ~= ZipError:
        out->println("error: " + zf.msg)
    else:        
        // Ok, this is a valid zip file, so print out the information
        // in a format similar to "unzip -v".
        out->println(" Length  Method  Size   CRC-32  Name")
        out->println("-------- ------- ------ -------- ----")
        rawSize = 0
        size = 0
        for e in zf.entries:
            out->println(rightAlign(e.rawSize,8) + " " + methodStr(e.method) + " " +
                rightAlign(e.size,6) + " " + hexStr(e.crc) + " " + e.name)
            rawSize = rawSize + e.rawSize
            size = size + e.size
        out->println("--------         ------ -------- ----") 
        out->println(rightAlign(rawSize,8) + "        " + rightAlign(size,6) + " " + str(|zf.entries|) + " file(s)")

string methodStr(int method):
    if method == ZIP_COMPRESSION_NONE:
        return "  None"
    else if method == ZIP_COMPRESSION_LZW:
        return "   LZW"
    else if method == ZIP_COMPRESSION_REDUCED_1:
        return "Reduced 1"
    else if method == ZIP_COMPRESSION_REDUCED_2:
        return "Reduced 2"
    else if method == ZIP_COMPRESSION_REDUCED_3:
        return "Reduced 3"
    else if method == ZIP_COMPRESSION_REDUCED_4:
        return "Reduced 4"
    else if method == ZIP_COMPRESSION_IMPLODED_1:
        return "Imploded 1"
    else if method == ZIP_COMPRESSION_IMPLODED_2:
        return "Imploded 2"
    else if method == ZIP_COMPRESSION_IMPLODED_3:
        return "Imploded 3"
    else if method == ZIP_COMPRESSION_IMPLODED_4:
        return "Imploded 4"
    else:
        return "Unknown"
        

string rightAlign(int val, int len):
    return rightAlign(str(val),len)

// pad out the given string to ensure it has len characters
string rightAlign(string s, int len):
    r = ""
    i = |s|
    while i < len:
        r = r + " "
        i = i + 1
    r = r + s
    return r
