import whiley.lang.*

type sr8nat is (int n) where n > 0

type sr8tup is {sr8nat f, int g} where g > f

method main(System.Console sys) -> void:
    [sr8tup] x = [{f: 1, g: 3}, {f: 4, g: 8}]
    x[0].f = 2
    sys.out.println(x)
