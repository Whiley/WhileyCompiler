import * from whiley.lang.*

define state as {int x, int y}
define pState as process state

int pState::send2(int x, System sys):
    sys.out.println(toString(x))
    return -1

void ::main(System sys,[string] args):
    x = (spawn {x:1,y:2}).send2(1,sys)
    sys.out.println(toString(x))
