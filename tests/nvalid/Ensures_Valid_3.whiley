import println from whiley.lang.System

function pred({int} xs) => bool
ensures !$ || no { z in xs | z < 0 }:
    zs = { z | z in xs, z < 0 }
    return |zs| == 0

function countOver({int} xs, int y) => int
requires pred(xs):
    tmp = { x | x in xs, x > y }
    return |tmp|

method main(System.Console sys) => void:
    c1 = countOver({1, 2, 3, 4}, 1)
    c2 = countOver({1, 2, 3, 4}, 3)
    sys.out.println(Any.toString(c1))
    sys.out.println(Any.toString(c2))
