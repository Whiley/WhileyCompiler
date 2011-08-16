define state as {int x, int y}
define pState as process state

void pState::send(int x, System sys):
    this.x = x
    assert *this.x == x
    sys.this.out.println(str(*this))
    sys.this.out.println("sent")
    sys.this.out.println(str(x))

void System::main([string] args):
    ps = spawn {x:1,y:2}
    ps.send(1,this)
