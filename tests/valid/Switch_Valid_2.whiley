import whiley.lang.*

function f(int x) -> int:
    int y
    switch x:
        case 1:
            y = -1
        case 2:
            y = -2
        default:
            y = 0
    return y

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
    sys.out.println(f(-1))
