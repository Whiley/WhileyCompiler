import * from whiley.lang.*

define state as {int x, int y, SystemOutWriter out}
define pState as process state

void pState::send(int z):
    this.out.println(toString(this.x))
    this.out.println(toString(this.y))
    this.out.println(toString(z))

void ::main(System sys,[string] args):
    ps = spawn {x:1,y:2,out:sys.out}
    ps.send(1)
