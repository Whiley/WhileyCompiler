import * from whiley.lang.*

method main(System.Console sys) => void:
    xs = {1, 2, 3}
    zs = { x | x in zs }
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(zs))
