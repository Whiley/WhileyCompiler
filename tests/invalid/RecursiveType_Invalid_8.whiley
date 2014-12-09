
constant ADD is 1

constant SUB is 2

constant MUL is 3

constant DIV is 4

type binop is {int op, expr left, expr right} where op in {ADD, SUB, MUL, DIV}

type asbinop is {int op, expr left, expr right} where op in {ADD, SUB}

type expr is int | binop

type asexpr is int | asbinop

function f(asexpr e) -> asexpr:
    return e

method main(System.Console sys) -> void:
    e1 = {op: MUL, left: 1, right: 2}
    f(e1)
