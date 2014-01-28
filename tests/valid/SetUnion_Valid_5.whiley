import whiley.lang.System

method main(System.Console sys) => void:
    xs = {1, 2, 3, 4}
    ys = {5} + xs
    sys.out.println(Any.toString(xs))
    xs = xs + {6}
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(ys))
