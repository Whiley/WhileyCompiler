import whiley.lang.*

constant ADD is 1

constant SUB is 2

constant MUL is 3

constant DIV is 4

type binop is {int op, expr left, expr right} where op in {ADD, SUB, MUL, DIV}

type expr is int | binop

method main(System.Console sys) -> void:
    expr e1 = {op: ADD, left: 1, right: 2}
    expr e2 = {op: SUB, left: e1, right: 2}
    expr e3 = {op: SUB, left: {op: MUL, left: 2, right: 2}, right: 2}
    sys.out.println(e1)
    sys.out.println(e2)
    sys.out.println(e3)
