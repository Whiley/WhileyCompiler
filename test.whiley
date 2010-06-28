define binop as (int op, expr left, expr right)
define expr as int | binop

void System::main([string] args):
    expr e = 1
    print str(e)
