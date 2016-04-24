

type MyProc is &{int x}

method inc(MyProc _this, int i) :
    _this->x = _this->x + i

public export method test() :
    MyProc mproc = new {x: 1}
    inc(mproc, 10)
    assume mproc->x == 11
