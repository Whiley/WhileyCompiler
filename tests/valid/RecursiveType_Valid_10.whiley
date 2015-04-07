import whiley.lang.*

type expr is int | {int op, expr left, expr right}

method main(System.Console sys) -> void:
    expr e = {op: 1, left: 1, right: 2}
    sys.out.println(e)
