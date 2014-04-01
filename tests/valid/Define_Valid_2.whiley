import whiley.lang.System

type dr2point is {real y, real x}

method main(System.Console sys) => void:
    dr2point p = {y: 2.23, x: 1.0}
    sys.out.println(Any.toString(p))
