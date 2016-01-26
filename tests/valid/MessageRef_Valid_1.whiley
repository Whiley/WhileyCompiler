

type MyProc is &{int position}

type MyMeth is method(MyProc, int) -> int

method read(MyProc this, int x) -> int:
    return x + 123

public export method test(MyMeth m, MyProc proc) -> int:
    return m(proc,1)

public export method test() :
    MyProc p = new {position: 0}
    int r = test(&read, p)
    assume r == 124
