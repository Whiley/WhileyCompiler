

type bytes is {int b1, int b2}

function f(int a) -> bytes:
    bytes bs = {b1: a, b2: a + 1}
    return bs

public export method test() :
    assume f(1) == {b1: 1, b2: 2}
    assume f(2) == {b1: 2, b2: 3}
    assume f(9) == {b1: 9, b2: 10}
