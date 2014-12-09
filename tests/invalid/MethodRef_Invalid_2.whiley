import * from whiley.lang.*

type MyMeth is method(int) -> int

method read(int x) -> int:
    return x + 123

method test(MyMeth m) -> int:
    return m(1)

method main(System.Console sys) -> void:
    int r = test(&read)
    sys.out.println(Any.toString(r))
