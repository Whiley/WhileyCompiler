define state as {int x, int y}
define pState as process state

void pState::send(int x):
    this->x = x
    assert this->x == x
    out->println(str(*this))
    out->println("sent")
    out->println(str(x))

void System::main([string] args):
    ps = spawn {x:1,y:2}
    ps->send(1)
