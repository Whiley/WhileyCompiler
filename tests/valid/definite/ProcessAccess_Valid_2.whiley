define (int x, int y) as state
define process state as pState

void pState::send(int z):
    print str(this->x)
    print str(this->y)
    print str(z)

void System::main([string] args):
    pState ps = spawn (x:1,y:2)
    ps->send(1)
