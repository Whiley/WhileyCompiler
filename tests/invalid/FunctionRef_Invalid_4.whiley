constant table is [&f1, &f2, null]

type func_t is function(int)->int

function f1(int x) -> int:
    return x

function f2(int x) -> int:
    return -x

function g(int d) -> int:
    func_t y = table[d]
    return y(123)
