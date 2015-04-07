import whiley.lang.*

function f(int x) -> int:
    switch x:
        case 1:
            return 0
        case 2:
            return -1
    return 10

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
    sys.out.println(f(-1))
