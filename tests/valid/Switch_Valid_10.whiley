import whiley.lang.System

function f(real x) => int:
    switch x:
        case 1.23:
            return 0
        case 2.01:
            return -1
    return 10

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1.23)))
    sys.out.println(Any.toString(f(2.01)))
    sys.out.println(Any.toString(f(3.0)))
    sys.out.println(Any.toString(f(-1.0)))
