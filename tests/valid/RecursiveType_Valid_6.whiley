import whiley.lang.*

type binop is {int op, expr left, expr right}

type expr is int | binop

method main(System.Console sys) -> void:
    expr e = 123
    sys.out.println(e)
