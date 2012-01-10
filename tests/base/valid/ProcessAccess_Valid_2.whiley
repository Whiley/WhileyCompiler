

define state as {int x, int y, SystemOutWriter out}
define pState as process state

void pState::send(int z):
    this->out.println(Any.toString(this->x))
    this->out.println(Any.toString(this->y))
    this->out.println(Any.toString(z))

void ::main(System.Console sys):
    ps = spawn {x:1,y:2,out:sys.out}
    ps.send(1)
