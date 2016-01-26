

type MyProc is &{int x}

method inc(MyProc this, int i) :
    this->x = this->x + i

public export method test() :
    MyProc mproc = new {x: 1}
    inc(mproc, 10)
    assume mproc->x == 11
