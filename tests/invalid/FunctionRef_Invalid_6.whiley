import * from whiley.lang.*

function read(real a) -> real:
    return -a

function id(int x) -> int:
    return x

function test(function read(int)->int, real arg) -> real:
    return read(arg)

method main(System.Console sys) -> void:
    x = test(&id, 1)
    sys.out.println(Any.toString(x))
    x = test(&id, 123)
    sys.out.println(Any.toString(x))
    x = test(&id, 223)
    sys.out.println(Any.toString(x))
