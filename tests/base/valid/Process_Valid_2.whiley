define state as {int x, int y}
define pState as process state

int pState::send2(int x, System sys):
    sys.this.out.println(str(x))
    return -1

void System::main([string] args):
    x = (spawn {x:1,y:2}).send2(1,this)
    this.out.println(str(x))
