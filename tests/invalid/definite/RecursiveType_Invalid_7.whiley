define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define binop as (int op, expr left, expr right) where op in {ADD,SUB,MUL,DIV}
define asbinop as (int op, expr left, expr right) where op in {ADD,SUB}
define expr as int | binop
define asexpr as int | asbinop

void System::main([string] args):
    expr e1 = (op:MUL,left:1,right:2)
    asexpr e2 = e1
