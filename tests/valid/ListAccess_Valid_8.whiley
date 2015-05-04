// Convert a byte into an unsigned int.  This assumes a little endian
// encoding.
public function toUnsignedInt(byte b) -> int:
    int r = 0
    int base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    return r

public function meth([byte] bytes) -> [int]:
    [int] data = []
    for i in 0 .. |bytes|:
        data = data ++ [toUnsignedInt(bytes[i])]
    return data

public export method test() -> void:
    [byte] bytes = [00000000b, 00000001b, 00000011b, 00000111b]
    assume meth(bytes) == [0,1,3,7]

