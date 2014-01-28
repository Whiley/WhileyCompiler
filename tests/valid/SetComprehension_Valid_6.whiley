import whiley.lang.System

method main(System.Console sys) => void:
    xs = {1, 2, 3, 4}
    ys = {1, 2}
    zs = { x + y | x in xs, y in ys, x != y }
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(ys))
    sys.out.println(Any.toString(zs))
