define (int x, int y) as state
define process state as pState

void System::main([string] args):
    pState ps = spawn (x:2,y:2)
    ps->x = 1
    assert ps->x == 1
