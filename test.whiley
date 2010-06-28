define binop as (int op, expr left, expr right)
define expr as int | binop

void System::main([string] args):
    expr e = (op:1, left:1, right:2)
    print str(e)
