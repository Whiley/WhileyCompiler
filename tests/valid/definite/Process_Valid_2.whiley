define state as {int x, int y} where x < y
define pState as process state

int pState::send(int x):
    out->println(str(x))
    return -1

void System::main([string] args):
    x = (spawn {x:1,y:2})->send(1)
    out->println(str(x))
