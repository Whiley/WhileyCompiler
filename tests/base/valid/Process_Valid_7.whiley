define MyProc as process { int x }

void MyProc::inc(int i):
    this.x = x + i

void System::main([string] args):
    mproc = spawn { x:1 }
    mproc<->inc(10)
    out<->println(str(*mproc.x))
