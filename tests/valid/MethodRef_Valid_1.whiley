

type MyMeth is method(int) -> int

method read(int x) -> int:
    return x + 123

public export method test(MyMeth m) -> int:
    return m(1)

public export method test() :
    int r = test(&read)
    assume r == 124
