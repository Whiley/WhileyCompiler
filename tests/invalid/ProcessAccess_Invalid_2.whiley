import * from whiley.lang.*

define state as {int x, int y}
define pState as ref state

void pState::f():
    this = new {x:3,y:4} // NOT OK

void ::main(System.Console sys):
    p = new {x:1,y:2}
    sys.out.println(Any.toString()*p)
    p.f()
    sys.out.println(Any.toString()*p)
