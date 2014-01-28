import whiley.lang.System

method main(System.Console sys) => void:
    {int} xs = {1, 2, 3, 4}
    {int} ys = xs + {5, 1}
    sys.out.println(Any.toString(xs))
    xs = xs + {6}
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(ys))
