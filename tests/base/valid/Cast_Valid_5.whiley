import whiley.lang.*
import println from whiley.lang.System

char toChar(byte b):
    return (char) Byte.toUnsignedInt(b)

void ::main(System.Console sys):
    for i in 32..127:
        c = toChar(Int.toUnsignedByte(i))
        sys.out.println("CHARACTER: " + c)
