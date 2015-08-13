

function invertByte(bool[] bits) -> (bool[] ret)
// Precondition: 8 bits in a byte
requires |bits| == 8
// Postcondition: return a byte as well
ensures |ret| == 8
// Postcondition: every bit must be inverted
ensures all { i in 0 .. 8 | ret[i] == !bits[i] }:
    //
    int i = 0
    bool[] ret = bits
    //
    while i < 8
    // i is non-negative, and size of bits unchanged
    where i >= 0 && |ret| == |bits| && i <= |bits|
    // Every bit upto i is inverted now
    where all { j in 0 .. i | ret[j] == !bits[j] }:
        //
        ret[i] = !bits[i]
        i = i + 1
    //
    return ret

public export method test():
    assume invertByte([true,false,true,false,true,false,true,false]) == [false, true, false, true, false, true, false, true]
    assume invertByte([true,true,true,true,false,false,false,false]) == [false, false, false, false, true, true, true, true]
