import println from whiley.lang.System

define state as {int x, int y}
define pState as ref state

void pState::send(int x, System.Console sys):
    this->x = x
    assert *this.x == x
    sys.out.println(Any.toString(*this))
    sys.out.println("sent")
    sys.out.println(Any.toString(x))

void ::main(System.Console sys):
    ps = new {x:1,y:2}
    ps.send(1,sys)
