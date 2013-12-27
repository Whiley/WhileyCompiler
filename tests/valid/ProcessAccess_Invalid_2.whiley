import * from whiley.lang.System

define state as {int x, int y, SystemOutWriter out}
define pState as ref state

void pState::send(int z):
    this->out.println(Any.toString(this->x))
    this->out.println(Any.toString(this->y))
    this->out.println(Any.toString(z))

void ::main(System.Console sys):
    ps = new {x:1,y:2,out:sys.out}
    ps.send(1)
