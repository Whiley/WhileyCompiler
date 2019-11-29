

type MyProc is &{int position}

type MyMeth is method(MyProc, int) -> (int)

method read(MyProc _this, int x) -> int:
    return x + 123

method call(MyMeth m, MyProc proc) -> int:
    return m(proc,1)

public export method test() :
    MyProc p = new {position: 0}
    int r = call(&read, p)
    assume r == 124
