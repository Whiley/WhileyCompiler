import whiley.lang.System

method main(System.Console sys) => void:
    {int} xs = {1, 2, 3, 4}
    sys.out.println(Any.toString(xs))
