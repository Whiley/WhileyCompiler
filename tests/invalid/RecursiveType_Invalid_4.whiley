
constant ADD is 1

constant SUB is 2

constant MUL is 3

constant DIV is 4

type binop is {int op, expr left, expr right} where op in {ADD, SUB, MUL, DIV}

type expr is int | binop

function f(expr e) -> expr:
    return e

method main(System.Console sys) -> void:
    e1 = {op: 0, left: {op: MUL, left: 2, right: 2}, right: 2}
    debug Any.toString(f(e1))
