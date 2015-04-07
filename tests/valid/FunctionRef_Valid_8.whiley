import whiley.lang.*

function read(int a) -> int:
    return -a

function id(int x) -> int:
    return x

function test(function (int)->int read, int arg) -> int:
    return read(arg)

method main(System.Console sys) -> void:
    int x = test(&id, 1)
    sys.out.println(x)
    x = test(&id, 123)
    sys.out.println(x)
    x = test(&id, 223)
    sys.out.println(x)
