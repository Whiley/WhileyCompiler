import whiley.lang.*

public function meth([byte] bytes) -> [int]:
    [int] data = []
    for i in 0 .. |bytes|:
        data = data ++ [Byte.toUnsignedInt(bytes[i])]
    return data

public method main(System.Console sys) -> void:
    [byte] bytes = [00000000b, 00000001b, 00000011b, 00000111b]
    [int] ints = meth(bytes)
    sys.out.println(ints)
