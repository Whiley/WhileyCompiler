import whiley.lang.System

constant ADD is 1

constant SUB is 2

constant MUL is 3

constant DIV is 4

type binop is {int op, expr left, expr right} where op in {ADD, SUB, MUL, DIV}

type asbinop is {int op, expr left, expr right} where op in {ADD, SUB}

type expr is int | binop

method main(System.Console sys) => void:
    bop1 = {op: ADD, left: 1, right: 2}
    bop2 = bop1
    e1 = bop1
    e2 = {op: SUB, left: bop1, right: 2}
    sys.out.println(Any.toString(e1))
    sys.out.println(Any.toString(e2))
