
type MyMeth is method(int)->(int)

method read(int x) -> int:
    return x + 123

method call(MyMeth m) -> int:
    return m(1)

public export method test() :
    int r = call(&read)
    assume r == 124
