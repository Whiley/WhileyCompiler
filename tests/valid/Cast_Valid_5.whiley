type u8 is (int n) where 0 <= n && n <= 255

constant bases is [1,2,4,8,16,32,64,128]

public function bases() -> (int[] r)
ensures |bases| == |r|
ensures all { i in 0 .. |r| | bases[i] == r[i] }
ensures all { i in 1 .. |r| | 2 * r[i-1] == r[i] }:
    //
    return bases
    
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
        if (b & 00000001b) == 00000001b:
            x = x + base
        b = b >> 1
        base = base * 2
        i = i + 1
    //
    return x

public function toUnsignedByte(u8 v) -> byte:
    //
    byte mask = 00000001b
    byte r = 0b
    int i = 0
    while i < 8:
        if (v % 2) == 1:
            r = r | mask
        v = v / 2
        mask = mask << 1
        i = i + 1
    return r

public export method test() :
    int i = 32
    while i < 127 where i >= 0:
        int c = toUnsignedInt(toUnsignedByte(i))
        assume c == i
        i = i + 1
    //
