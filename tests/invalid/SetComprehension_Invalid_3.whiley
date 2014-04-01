import * from whiley.lang.*

method main(System.Console sys) => void:
    {int} xs = {1, 2, 3}
    {int} zs = { x | x in zs }
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(zs))
