import whiley.lang.*

type MyProc is &{int position}

type MyMeth is method(MyProc, int) -> int

method read(MyProc this, int x) -> int:
    return x + 123

method test(MyMeth m, MyProc proc) -> int:
    return m(proc,1)

method main(System.Console sys) -> void:
    MyProc p = new {position: 0}
    int r = test(&read, p)
    sys.out.println(r)
