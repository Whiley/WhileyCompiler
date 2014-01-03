import println from whiley.lang.System

function f({int} xs) => {int}
requires no { x in xs | x < 0 }
ensures no { y in $ | y > 0 }:
    return { -x | x in xs }

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f({1, 2, 3, 4})))
