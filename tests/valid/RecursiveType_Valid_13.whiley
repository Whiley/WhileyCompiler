import println from whiley.lang.System

define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define binop as {int op, expr left, expr right}
define expr as int | binop

void ::main(System.Console sys):
    e1 = {op:ADD,left:1,right:2}
    e2 = {op:SUB,left:e1,right:2}
    e3 = {op:SUB,left:{op:MUL,left:2,right:2},right:2}
    sys.out.println(Any.toString(e1))
    sys.out.println(Any.toString(e2))
    sys.out.println(Any.toString(e3))
