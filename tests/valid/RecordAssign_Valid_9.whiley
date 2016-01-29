

type i8 is (int x) where x >= 0 && x <= 255
type bytes is {i8 b1, i8 b2}

function f(int a) -> bytes
requires a > 0 && a < 10:
    bytes bs = {b1: a, b2: a + 1}
    return bs

public export method test() :
    assume f(1) == {b1: 1, b2: 2}
    assume f(2) == {b1: 2, b2: 3}
    assume f(9) == {b1: 9, b2: 10}
