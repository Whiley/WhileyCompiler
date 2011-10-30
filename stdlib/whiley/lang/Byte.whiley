package whiley.lang

public define LilEndian as 0
public define BigEndian as 1

public define Endian as {
    LilEndian,
    BigEndian
}

// convert a byte into a string
string toString(byte b):
    r = "b"
    for i in 0..8:
        if (b & 00000001b) == 00000001b:
            r = "1" + r
        else:
            r = "0" + r
        b = b >> 1	
    return r

// Convert a byte into an unsigned int.  This assumes a little endian
// encoding.
int toUnsignedInt(byte b):
    r = 0
    base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    return r    

// Convert a byte into an unsigned int using a given endianness.
int toUnsignedInt(byte b, Endian endian):
    if endian == LilEndian:
        return toUnsignedInt(b)
    else:        
        // big endian
        r = 0
        base = 1
        while b != 0b:
            if (b & 10000000b) == 10000000b:
                r = r + base
            b = b << 1
            base = base * 2
        return r    

// Convert a byte array into an unsigned int assuming a little endian
// form for both individual bytes, and the array as a whole
int toUnsignedInt([byte] bytes):
    val = 0
    base = 1
    for b in bytes:
        v = toUnsignedInt(b) * base
        val = val + v
        base = base * 256
    return val

// Convert a byte into an unsigned int.  This assumes a little endian
// encoding.
int toInt(byte b):
    r = 0
    base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    // finally, add the sign
    if r >= 128:
        return -(256-r)
    else:
        return r    

// Convert a byte into a unicode character.
char toChar(byte b):
    return (char) toUnsignedInt(b)

// Convert a byte array into a signed int assuming a little endian
// form for both individual bytes, and the array as a whole
int toInt([byte] bytes):
    val = 0
    base = 1
    for b in bytes:
        v = toUnsignedInt(b) * base
        val = val + v
        base = base * 256
    // finally, add the sign
    if val >= (base/2):
        return -(base-val)
    else:
        return val
