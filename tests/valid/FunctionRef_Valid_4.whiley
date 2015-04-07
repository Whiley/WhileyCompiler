import whiley.lang.*

constant table is [&f1, &f2]

function f1(int x) -> int:
    return x

function f2(int x) -> int:
    return -x

type func is function(int)->int

function g(int d) -> int:
    func y = table[d]
    return y(123)

method main(System.Console sys) -> void:
    sys.out.println(g(0))
    sys.out.println(g(1))
