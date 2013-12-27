import println from whiley.lang.System

define MyProc as ref { int x }

void MyProc::inc(int i):
    this->x = this->x + i

void ::main(System.Console sys):
    mproc = new { x:1 }
    mproc.inc(10)
    sys.out.println(Any.toString(*mproc.x))
