(null|func_t)[] table = [&f1, &f2, null]

type func_t is function(int)->int

function f1(int x) -> int:
    return x

function f2(int x) -> int:
    return -x

function g(int d) -> int
requires d >= 0 && d < 3:
    null|func_t y = table[d]
    return y(123)
