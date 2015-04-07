import whiley.lang.*

type Point is {int y, int x} where x != y

function f(int x) -> int:
    return x

function Point(int i, int j) -> Point:
    if f(i) != f(j):
        return {y: j, x: i}
    else:
        return {y: -1, x: 1}

method main(System.Console sys) -> void:
    Point rs = Point(1, 1)
    sys.out.println(rs)
    rs = Point(1, 2)
    sys.out.println(rs)
