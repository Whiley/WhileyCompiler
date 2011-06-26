define state as {int x, int y}
define pState as process state

void pState::send(int x, System sys):
    this.x = x
    assert *this.x == x
    sys.out.println(str(*this))
    sys.out.println("sent")
    sys.out.println(str(x))

void System::main([string] args):
    ps = spawn {x:1,y:2}
    ps.send(1,this)
