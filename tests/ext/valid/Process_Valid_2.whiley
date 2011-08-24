import whiley.lang.*:*

define state as {int x, int y} where x < y
define pState as process state

int pState::send2(int x, System sys):
    sys.sys.out.println(str(x))
    return -1

void ::main(System sys,[string] args):
    x = (spawn {x:1,y:2}).send2(1,this)
    sys.out.println(str(x))
