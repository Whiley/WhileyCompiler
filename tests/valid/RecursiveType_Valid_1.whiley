import whiley.lang.System

type expr is int | {int op, expr left, expr right}

method main(System.Console sys) => void:
    e = 1
    sys.out.println(Any.toString(e))
