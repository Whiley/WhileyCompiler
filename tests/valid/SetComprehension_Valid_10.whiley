import whiley.lang.System

function f({int} xs) => ({int} ys)
// no element of input cannot negative
requires no { x in xs | x < 0 }
// all elements of return must be positive
ensures no { y in ys | y > 0 }:
    //
    return { -x | x in xs }

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f({1, 2, 3, 4})))
