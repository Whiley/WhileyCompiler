import * from whiley.lang.*

method main(System.Console sys) => void:
    xs = {1, 2, 3, 4}
    zs = { x | y in xs }
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(zs))
