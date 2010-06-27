define state as (int x, int y)
define pState as process state

void pState::send(int z):
    print str(this->x)
    print str(this->y)
    print str(z)

void System::main([string] args):
    pState ps = spawn (x:1,y:2)
    ps->send(1)
