define expr as int | {int op, expr left, expr right}

expr f(expr e):
    return e

void System::main([string] args):
    e = {op:1,left:"HELLO",right:2}
    print str(f(e))
