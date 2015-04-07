import whiley.lang.*

type point is {int y, int x}

method main(System.Console sys) -> void:
    point p = {y: 1, x: 1}
    sys.out.println(p)
