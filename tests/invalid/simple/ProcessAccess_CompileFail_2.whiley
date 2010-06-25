define (int x, int y) as state
define process state as pState

void pState::f():
    this = spawn (x:3,y:4) // NOT OK

void System::main([string] args):
    pState p = spawn (x:1,y:2)
    print str(*p)
    p->f()
    print str(*p)