import whiley.lang.*:*

define state as {int x, int y}
define pState as process state

void pState::f():
    this = spawn {x:3,y:4} // NOT OK

void System::main([string] args):
    p = spawn {x:1,y:2}
    this.out.println(str()*p)
    p.f()
    this.out.println(str()*p)
