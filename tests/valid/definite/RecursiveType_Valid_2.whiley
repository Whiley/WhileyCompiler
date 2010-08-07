define binop as (int op, expr left, expr right)
define expr as int | binop

void System::main([string] args):
    expr e = 123
    print str(e)
