type u8 is (int n) where 0 <= n && n <= 255

public function toUnsignedInt(byte b) -> (int r)
ensures 0 <= r && r <= 255:
    //
    int r = 0
    int base = 1
    int i = 1
    // this is not specified correctly
    while i <= 8
        where 1 <= i
        where 0 <= r && r < base
        where 1 <= base && base <= i*2:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
        i = i + 1
    //
    return r

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

public export method test() -> void:
    int i = 32
    while i < 127 where i >= 0:
        int c = toUnsignedInt(toUnsignedByte(i))
        assume c == i
        i = i + 1
    //
