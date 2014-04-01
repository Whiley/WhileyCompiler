import whiley.lang.*
import whiley.lang.System

function toChar(byte b) => char:
    return (char) Byte.toUnsignedInt(b)

method main(System.Console sys) => void:
    for i in 32 .. 127:
        char c = toChar(Int.toUnsignedByte(i))
        sys.out.println("CHARACTER: " ++ c)
