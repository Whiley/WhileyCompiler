import whiley.lang.*:*

define state as {int x, int y, SystemOutWriter out}
define pState as process state

void pState::send(int z):
    sys.out.println(str(this.x))
    sys.out.println(str(this.y))
    sys.out.println(str(z))

void ::main(System sys,[string] args):
    ps = spawn {x:1,y:2,out:out}
    ps.send(1)
