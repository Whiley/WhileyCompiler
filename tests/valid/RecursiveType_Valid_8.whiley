import whiley.lang.*

type nat is (int n) where n >= 0

type expr is nat | {int op, expr left, expr right}

method main(System.Console sys) -> void:
    expr e = 14897
    sys.out.println(e)
