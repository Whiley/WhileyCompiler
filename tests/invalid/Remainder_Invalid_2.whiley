import * from whiley.lang.*

function f(int x, real y) -> int:
    return x % y

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(10, 5.23)))
    sys.out.println(Any.toString(f(10, 4)))
    sys.out.println(Any.toString(f(1, 4)))
    sys.out.println(Any.toString(f(103, 2)))
    sys.out.println(Any.toString(f(-10, 5.23)))
    sys.out.println(Any.toString(f(-10, 4)))
    sys.out.println(Any.toString(f(-1, 4)))
    sys.out.println(Any.toString(f(-103, 2)))
    sys.out.println(Any.toString(f(-10, -5.23)))
    sys.out.println(Any.toString(f(-10, -4)))
    sys.out.println(Any.toString(f(-1, -4)))
    sys.out.println(Any.toString(f(-103, -2)))
    sys.out.println(Any.toString(f(10, -5.23)))
    sys.out.println(Any.toString(f(10, -4)))
    sys.out.println(Any.toString(f(1, -4)))
    sys.out.println(Any.toString(f(103, -2)))
