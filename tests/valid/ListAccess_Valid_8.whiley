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

public function meth(byte[] bytes) -> int[]:
    int[] data = [0; |bytes|]
    int i = 0
    while i < |bytes|
        where i >= 0
        where |data| == |bytes|:
        data[i] = toUnsignedInt(bytes[i])
        i = i + 1
    return data

public export method test() :
    byte[] bytes = [00000000b, 00000001b, 00000011b, 00000111b]
    assume meth(bytes) == [0,1,3,7]

