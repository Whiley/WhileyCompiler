define state as {int x, int y, SystemOutWriter out}
define pState as process state

void pState::send(int z):
    out<->println(str(this->x))
    out<->println(str(this->y))
    out<->println(str(z))

void System::main([string] args):
    ps = spawn {x:1,y:2,out:out}
    ps->send(1)
