import * from whiley.lang.*

function f(real x, real y) -> int:
    return x % y

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(10.5343, 5.2354)))
    sys.out.println(Any.toString(f(10.5343, 4.2345)))
    sys.out.println(Any.toString(f(1, 4.2345)))
    sys.out.println(Any.toString(f(10.53433, 2)))
    sys.out.println(Any.toString(f(-10.5343, 5.2354)))
    sys.out.println(Any.toString(f(-10.5343, 4.2345)))
    sys.out.println(Any.toString(f(-1, 4.2345)))
    sys.out.println(Any.toString(f(-10.53433, 2)))
    sys.out.println(Any.toString(f(-10.5343, -5)))
    sys.out.println(Any.toString(f(-10.5343, -4)))
    sys.out.println(Any.toString(f(-1, -4)))
    sys.out.println(Any.toString(f(-10.53433, -2)))
    sys.out.println(Any.toString(f(10.5343, -5)))
    sys.out.println(Any.toString(f(10.5343, -4)))
    sys.out.println(Any.toString(f(1, -4)))
    sys.out.println(Any.toString(f(10.53433, -2)))
