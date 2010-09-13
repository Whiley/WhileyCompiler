define state as {int x, int y}
define pState as process state

void pState::f():
    this = spawn (x:3,y:4) // NOT OK

void System::main([string] args):
    pState p = spawn (x:1,y:2)
    print str(*p)
    p->f()
    print str(*p)