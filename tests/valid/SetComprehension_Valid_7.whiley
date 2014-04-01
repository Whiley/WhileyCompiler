import whiley.lang.System

method main(System.Console sys) => void:
    {int} xs = {1, 2, 3, 4}
    {int} ys = {1, 2}
    {int} zs = { x + y | x in xs, y in ys }
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(ys))
    sys.out.println(Any.toString(zs))
