import * from whiley.lang.*

define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define binop as {int op, expr left, expr right} where op in {ADD,SUB,MUL,DIV}
define expr as int | binop

expr f(expr e):
    return e

void ::main(System.Console sys):
    e1 = {op:0, left:{op:MUL,left:2,right:2}, right:2}
    debug Any.toString(f(e1))
