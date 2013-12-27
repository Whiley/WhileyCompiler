import println from whiley.lang.System

define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define binop as {int op, expr left, expr right}
define asbinop as {int op, expr left, expr right}
define expr as int | binop

void ::main(System.Console sys):
    bop1 = {op:ADD,left:1,right:2}
    bop2 = bop1
    e1 = bop1
    e2 = {op:SUB,left:bop1,right:2}
    sys.out.println(Any.toString(e1))
    sys.out.println(Any.toString(e2))
