import println from whiley.lang.System

function read(int a) => int:
    return -a

function id(int x) => int:
    return x

function test(int(int) read, int arg) => int:
    return read(arg)

method main(System.Console sys) => void:
    x = test(&id, 1)
    sys.out.println(Any.toString(x))
    x = test(&id, 123)
    sys.out.println(Any.toString(x))
    x = test(&id, 223)
    sys.out.println(Any.toString(x))
