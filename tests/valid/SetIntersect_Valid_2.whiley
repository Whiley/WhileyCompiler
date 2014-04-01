import whiley.lang.System

method main(System.Console sys) => void:
    {int} xs = {1, 2, 3}
    [int] ys = [2, 3, 4]
    {int} zs = xs & ys
    sys.out.println(Any.toString(zs))
