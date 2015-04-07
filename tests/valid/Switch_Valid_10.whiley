import whiley.lang.*

function f(real x) -> int:
    switch x:
        case 1.23:
            return 0
        case 2.01:
            return -1
    return 10

method main(System.Console sys) -> void:
    sys.out.println(f(1.23))
    sys.out.println(f(2.01))
    sys.out.println(f(3.0))
    sys.out.println(f(-1.0))
