

constant table is [&f1, &f2]

function f1(int x) -> int:
    return x

function f2(int x) -> int:
    return -x

type func is function(int)->int

function g(int d) -> int:
    func y = table[d]
    return y(123)

public export method test() :
    assume g(0) == 123
    assume g(1) == -123
