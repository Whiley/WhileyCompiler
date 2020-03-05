
type i8 is (int x) where (x >= -128) && (x <= 127)

function f(int x) -> i8[]
requires (x == 0) || (x == 256):
    return [(i8) x]

public export method test():
    assume f(256) == [256]