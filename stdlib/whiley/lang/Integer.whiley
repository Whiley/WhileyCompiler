package whiley.lang

// Convert a byte array in little endian form into a int
int le2uint([byte] bytes):
    idx = 0
    val = 0
    base = 1
    while idx < |bytes|:
        val = val + (bytes[idx] * base)
        base = base * 256
        idx = idx + 1
    return val

