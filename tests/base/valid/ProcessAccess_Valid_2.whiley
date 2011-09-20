import * from whiley.lang.*

define state as {int x, int y, SystemOutWriter out}
define pState as process state

void pState::send(int z):
    this.out.println(str(this.x))
    this.out.println(str(this.y))
    this.out.println(str(z))

void ::main(System sys,[string] args):
    ps = spawn {x:1,y:2,out:sys.out}
    ps.send(1)
