import * from whiley.lang.*

define state as {int x, int y} where x < y
define pState as process state

int pState::send2(int x, System.Console sys):
    sys.out.println(Any.toString(x))
    return -1

void ::main(System.Console sys):
    x = (spawn {x:1,y:2}).send2(1,sys)
    sys.out.println(Any.toString(x))
