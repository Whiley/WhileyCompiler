import * from whiley.lang.*

type MyMeth is int ::(int)

method read(int x) -> int:
    return x + 123

function test(MyMeth m) -> int:
    return m(1)

method main(System.Console sys) -> void:
    r = test(&read)
    sys.out.println(Any.toString(r))
