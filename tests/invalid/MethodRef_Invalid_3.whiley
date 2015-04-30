type MyMeth is method(int)->int

method read(int x) -> int:
    return x + 123

function test(MyMeth m) -> int:
    return m(1)
