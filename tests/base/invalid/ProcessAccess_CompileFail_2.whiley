

define state as {int x, int y}
define pState as process state

void pState::f():
    this = spawn {x:3,y:4} // NOT OK

void ::main(System.Console sys):
    p = spawn {x:1,y:2}
    sys.out.println(Any.toString()*p)
    p.f()
    sys.out.println(Any.toString()*p)
