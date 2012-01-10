import * from whiley.lang.*

define MyProc as process { int x }

void MyProc::inc(int i):
    this->x = this->x + i

void ::main(System.Console sys):
    mproc = spawn { x:1 }
    mproc.inc(10)
    sys.out.println(Any.toString(*mproc.x))
