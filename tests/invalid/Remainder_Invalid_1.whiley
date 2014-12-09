import * from whiley.lang.*

function f(real x, int y) -> int:
    return x % y

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(10.23, 5)))
    sys.out.println(Any.toString(f(10.23, 4)))
    sys.out.println(Any.toString(f(1, 4)))
    sys.out.println(Any.toString(f(10.233, 2)))
    sys.out.println(Any.toString(f(-10.23, 5)))
    sys.out.println(Any.toString(f(-10.23, 4)))
    sys.out.println(Any.toString(f(-1, 4)))
    sys.out.println(Any.toString(f(-10.233, 2)))
    sys.out.println(Any.toString(f(-10.23, -5)))
    sys.out.println(Any.toString(f(-10.23, -4)))
    sys.out.println(Any.toString(f(-1, -4)))
    sys.out.println(Any.toString(f(-10.233, -2)))
    sys.out.println(Any.toString(f(10.23, -5)))
    sys.out.println(Any.toString(f(10.23, -4)))
    sys.out.println(Any.toString(f(1, -4)))
    sys.out.println(Any.toString(f(10.233, -2)))
