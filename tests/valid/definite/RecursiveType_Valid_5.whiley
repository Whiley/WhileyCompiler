define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define binop as (int op, expr left, expr right) requires op in {ADD,SUB,MUL,DIV}
define expr as int | binop

void System::main([string] args):
    expr e1 = (op:ADD,left:1,right:2)
    expr e2 = (op:SUB,left:e1,right:2)
    expr e3 = (op:SUB,left:(op:MUL,left:2,right:2),right:2)
    print str(e1)
    print str(e2)
    print str(e3)
