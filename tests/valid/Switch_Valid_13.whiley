import whiley.lang.System

function f(int x) => int:
    switch x:
        case 1, 2:
            return -1
        case 3:
            return 1
    return 10

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(3)))
    sys.out.println(Any.toString(f(-1)))
