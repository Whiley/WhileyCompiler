define expr as int | (int op, expr left, expr right)

void System::main([string] args):
    expr e = (op:1,left:"HELLO",right:2)
    print str(e)
