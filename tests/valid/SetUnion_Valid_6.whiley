import whiley.lang.System

method main(System.Console sys) => void:
    xs = {1, 2, 3}
    ys = [2, 3, 4]
    zs = xs + ys
    sys.out.println(Any.toString(zs))
