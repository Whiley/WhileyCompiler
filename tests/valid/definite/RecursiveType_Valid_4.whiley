define expr as int | (int op, expr left, expr right)

void System::main([string] args):
    expr e = (op:1,left:1,right:2)
    print str(e)
