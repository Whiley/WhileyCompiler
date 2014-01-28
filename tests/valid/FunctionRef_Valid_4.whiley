import whiley.lang.System
import whiley.io.*

constant table is [&f1, &f2]

function f1(int x) => int:
    return x

function f2(int x) => int:
    return -x

type func is function(int)=>int

function g(int d) => int:
    func y = table[d]
    return y(123)

method main(System.Console sys) => void:
    sys.out.println(Any.toString(g(0)))
    sys.out.println(Any.toString(g(1)))
