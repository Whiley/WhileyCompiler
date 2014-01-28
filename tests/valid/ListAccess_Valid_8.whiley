import whiley.lang.System

public function meth([byte] bytes) => [int]:
    pos = 0
    data = []
    for i in 0 .. 100:
        pos = pos + 1
        data = data ++ [Byte.toUnsignedInt(bytes[pos])]
    return data

public method main(System.Console sys) => void:
    bytes = [00000000b, 00000001b, 00000011b, 00000111b]
    ints = meth(bytes)
    sys.out.println(ints)
