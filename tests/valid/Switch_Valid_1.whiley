import whiley.lang.*

type nat is (int n) where n >= 0

function f(int x) -> nat:
    switch x:
        case 1:
            return x - 1
        case -1:
            return x + 1
    return 1

method main(System.Console sys) -> void:
    sys.out.println(f(2))
    sys.out.println(f(1))
    sys.out.println(f(0))
    sys.out.println(f(-1))
    sys.out.println(f(-2))
