define state as {int x, int y} where x < y
define pState as process state

int pState::send(int x):
    debug str(x)
    return -1

void System::main([string] args):
    int x = (spawn {x:2,y:2})->send(1)
    debug str(x)
