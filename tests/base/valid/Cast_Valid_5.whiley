import whiley.lang.*
import * from whiley.lang.System

char toChar(byte b):
    return (char) Byte.toUnsignedInt(b)

void ::main(System sys, [string] args):
    c = toChar('H')
    sys.out.println("CHARACTER: " + c)