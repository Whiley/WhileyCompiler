define (int x, int y) as state
define process state as pState

void pState::send(int x):
    this->x = x
    assert this->x == x
    print str(*this)
    print "sent"
    print str(x)

void System::main([string] args):
    pState ps = spawn (x:1,y:2)
    ps->send(1)
