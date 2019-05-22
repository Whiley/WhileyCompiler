int[] bases = [1,2,4,8,16,32,64,128]

public function toUnsignedInt(byte b) -> (int r)
ensures 0 <= r && r <= 255:
    //
    int x = 0
    int base = 1
    int i = 0
    //
    while i <= 7
        where 0 <= i
        where 0 <= x && x < base
        where base == bases[i]:
        if (b & 0b00000001) == 0b0000_0001:
            x = x + base
        // NOTE: following mask needed in leu of unsigned right shift
        // operator.
        b = (b >> 1) & 0b01111111
        base = base * 2
        i = i + 1
    //
    return x
