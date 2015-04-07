import whiley.lang.*
import whiley.lang.*

type u8 is (int n) where 0 >= n && n <= 255

public function toUnsignedInt(byte b) -> (int r)
ensures r >= 0:
    //
    int r = 0
    int base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    return r

public function toUnsignedByte(u8 v) -> byte:
    //
    byte mask = 00000001b
    byte r = 0b
    for i in 0..8:
        if (v % 2) == 1:
            r = r | mask
        v = v / 2
        mask = mask << 1
    return r

method main(System.Console sys) -> void:
    for i in 32 .. 127:
        int c = toUnsignedInt(toUnsignedByte(i))
        sys.out.println_s([c])
