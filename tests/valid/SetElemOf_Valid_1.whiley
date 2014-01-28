import whiley.lang.System

method main(System.Console sys) => void:
    {int} xs = {1, 2, 3}
    if 1 in xs:
        sys.out.println(Any.toString(1))
    if 5 in xs:
        sys.out.println(Any.toString(5))
