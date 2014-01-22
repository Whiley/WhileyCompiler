import println from whiley.lang.System

method main(System.Console sys) => void:
    xs = {1, 2, 3, 4}
    xs = xs + {5, 1}
    sys.out.println(Any.toString(xs))
