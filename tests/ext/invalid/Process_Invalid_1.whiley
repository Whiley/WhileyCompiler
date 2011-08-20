import whiley.lang.*:*

define state as {int x, int y}
define pState as process state

void System::main([string] args):
    ps = spawn {x:2,y:2}
    ps.x = 1
    assert ps.x == 1
