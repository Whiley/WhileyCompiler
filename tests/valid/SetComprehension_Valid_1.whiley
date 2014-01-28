import whiley.lang.System

method main(System.Console sys) => void:
    {int} xs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    {int} ys = { x | x in xs, ((x / 2) * 2) == x }
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(ys))
