// Convert a byte into an unsigned int.  This assumes a little endian
// encoding.
public function toUnsignedInt(byte b) -> int:
    int r = 0
    int base = 1
    while b != 0b0:
        if (b & 0b00000001) == 0b00000001:
            r = r + base
        // NOTE: following mask needed in leu of unsigned right shift
        // operator.
        b = (b >> 1) & 0b01111111                        
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
    byte[] bytes = [0b00000000, 0b00000001, 0b00000011, 0b00000111]
    assume meth(bytes) == [0,1,3,7]

