import println from whiley.lang.System

type point is {int y, int x}

method main(System.Console sys) => void:
    p = {y: 1, x: 1}
    sys.out.println(Any.toString(p))
