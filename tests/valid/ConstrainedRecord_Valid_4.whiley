import whiley.lang.*

type point is {
    int x,
    int y
} where x > 0 && y > 0

method main(System.Console sys) -> void:
    point p = {y: 1, x: 1}
    sys.out.println(p)
